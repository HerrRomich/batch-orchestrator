package io.github.herrromich.batch.internal

import io.github.herrromich.batch.*
import mu.KotlinLogging
import java.util.concurrent.*

internal class SimpleOrchestrator(threadPoolSize: Int, jobs: Set<Job>) : Orchestrator {
    private val logger = KotlinLogging.logger { }

    private val executor: ThreadPoolExecutor by lazy {
        val executor = ThreadPoolExecutor(
            threadPoolSize,
            threadPoolSize,
            0,
            TimeUnit.SECONDS,
            PriorityBlockingQueue(16)
        )
        logger.info { "Thread pool is initialized." }
        logger.debug { "Thread pool is initialized: $executor" }
        return@lazy executor
    }

    override val jobs = jobs.associateBy { it.jobName }
    private val executions = ConcurrentHashMap<Job, JobExecutionInstance>()

    override fun execute(jobName: String): JobExecution {
        val jobCandidate = jobs.getOrElse(jobName) {
            val message = "Job \"$jobName\" is not registered in the batch orchestrator."
            val ex = OrchestratorException(message)
            logger.warn(message, ex)
            throw ex
        }
        val jobExecution = executions.compute(jobCandidate) { job, execution ->
            if (execution != null) {
                val message =
                    "Job \"$jobName\" is running right now! It is ot allowed to have multiple executions of same job."
                val ex = OrchestratorException(message)
                logger.error(message, ex)
                throw ex
            }
            try {
                job.validate()
            } catch (ex: OrchestratorException) {
                logger.warn("Cannot validate job: \"$jobName\"", ex)
                throw ex
            }
            val jobExecution = JobExecutionInstance(job)
            return@compute jobExecution
        }!!
        submit(jobExecution)
        return jobExecution
    }

    private fun submit(jobExecution: JobExecutionInstance) = synchronized(jobExecution) {
        val fulfilledTasks = jobExecution.tasksWithUncompletedConsumables.filterValues { it.isEmpty() }.keys
            .sortedByDescending { it.priority }
        jobExecution.tasksWithUncompletedConsumables -= fulfilledTasks
        if (fulfilledTasks.isEmpty() && jobExecution.submittedTaskExecutions.isEmpty()) {
            finishJob(jobExecution)
        }
        fulfilledTasks
            .forEach {
                val taskExecution = jobExecution.queuedTaskExecutions.remove(it)!!
                jobExecution.submittedTaskExecutions.add(taskExecution)
                jobExecution.fulfilledTaskCount++
                taskExecution.changeState(TaskExecutionState.FULFILLED)
                logStatistics(jobExecution)
                val future = FutureTaskWithPriority(taskExecution)
                taskExecution.future = future
                executor.execute(future)
            }
    }

    private fun finishJob(jobExecution: JobExecutionInstance) {
        jobExecution.queuedTaskExecutions.values
            .forEach{
                it.changeState(TaskExecutionState.SKIPPED)
            }
        if (jobExecution.failedTaskCount != 0) {
            jobExecution.finish(JobExecutionState.ERROR)
            jobExecution.future.completeExceptionally(OrchestratorException("Job execution is failed."))
        } else if (jobExecution.fatalTaskCount != 0) {
            jobExecution.finish(JobExecutionState.FATAL)
            jobExecution.future.completeExceptionally(OrchestratorException("Job execution is fatally failed."))
        } else if (jobExecution.submittedTaskExecutions.isNotEmpty()) {
            jobExecution.finish(JobExecutionState.FATAL)
            jobExecution.future.completeExceptionally(OrchestratorException("Job execution has stuck."))
        } else {
            jobExecution.finish(JobExecutionState.COMPLETED)
            jobExecution.future.complete(null)
        }
    }

    private fun runTask(taskExecution: TaskExecutionInstance) {
        val jobContext = taskExecution.jobContext
        return synchronized(jobContext) {
            jobContext.runningTaskCount++
            jobContext.fulfilledTaskCount--
            taskExecution.changeState(TaskExecutionState.RUNNING)
            logStatistics(jobContext)
        }
    }

    private fun completeTask(taskExecution: TaskExecutionInstance) {
        val jobContext = taskExecution.jobContext
        synchronized(jobContext) {
            jobContext.submittedTaskExecutions.remove(taskExecution)
            jobContext.runningTaskCount--
            jobContext.completedTaskCount++
            taskExecution.changeState(TaskExecutionState.COMPLETED)
            logStatistics(jobContext)
            reorganizeGraph(taskExecution)
        }
        submit(jobContext)
    }

    private fun cancelTask(taskExecution: TaskExecutionInstance) {
        val jobContext = taskExecution.jobContext
        synchronized(jobContext) {
            if (taskExecution.event.state == TaskExecutionState.RUNNING) {
                jobContext.runningTaskCount--
                jobContext.canceledTaskCount++
                taskExecution.changeState(TaskExecutionState.CANCELED)
                logStatistics(jobContext)
                jobContext.submittedTaskExecutions.remove(taskExecution)
            }
        }
        submit(jobContext)
    }

    private fun failTask(taskExecution: TaskExecutionInstance) {
        val jobContext = taskExecution.jobContext
        synchronized(jobContext) {
            jobContext.submittedTaskExecutions.remove(taskExecution)
            jobContext.runningTaskCount--
            when (taskExecution.task.failLevel) {
                FailLevel.WARN -> {
                    jobContext.warnTaskCount++
                    taskExecution.changeState(TaskExecutionState.WARN)
                    logStatistics(jobContext)
                    reorganizeGraph(taskExecution)
                }

                FailLevel.ERROR -> {
                    jobContext.failedTaskCount++
                    taskExecution.changeState(TaskExecutionState.ERROR)
                    logStatistics(jobContext)
                }

                FailLevel.FATAL -> {
                    jobContext.fatalTaskCount++
                    taskExecution.changeState(TaskExecutionState.FATAL)
                    logStatistics(jobContext)
                    jobContext.submittedTaskExecutions.forEach {
                        it.future?.cancel(true)
                        if (it.event.state == TaskExecutionState.FULFILLED) {
                            jobContext.fulfilledTaskCount--
                            jobContext.canceledTaskCount++
                            it.changeState(TaskExecutionState.CANCELED)
                            logStatistics(jobContext)
                        }
                    }
                    jobContext.submittedTaskExecutions.removeIf{it.event.state == TaskExecutionState.CANCELED}
                    jobContext.queuedTaskExecutions.forEach { _, taskExecution ->
                        jobContext.skippedTaskCount++
                        taskExecution.changeState(TaskExecutionState.SKIPPED)
                    }
                    jobContext.queuedTaskExecutions.clear()
                }
            }
        }
        submit(jobContext)
    }

    private fun reorganizeGraph(taskExecution: TaskExecutionInstance) {
        val jobContext = taskExecution.jobContext
        synchronized(jobContext) {
            jobContext.uncompletedProducibles.forEach { _, tasks ->
                tasks.remove(taskExecution)
            }
            val completedProducibles = jobContext.uncompletedProducibles.filterValues(Set<TaskExecution>::isEmpty).keys
            jobContext.uncompletedProducibles -= completedProducibles
            jobContext.tasksWithUncompletedConsumables.forEach { _, consumables ->
                consumables -= completedProducibles
            }
        }
    }

    private fun logStatistics(jobExecution: JobExecutionInstance) {
        logger.info {
            "Job '${jobExecution.jobName}' statistics: " +
                    "fulfilled -> ${jobExecution.fulfilledTaskCount}; " +
                    "running -> ${jobExecution.runningTaskCount}; " +
                    "done -> ${jobExecution.completedTaskCount} / ${jobExecution.job.tasks.count()}"
        }
    }

    inner class FutureTaskWithPriority(private val taskExecution: TaskExecutionInstance) :
        FutureTask<Void>(TaskRunnable(taskExecution), null),
        Comparable<FutureTaskWithPriority> {
        override fun compareTo(other: FutureTaskWithPriority) =
            taskExecution.task.priority - other.taskExecution.task.priority
    }

    inner class TaskRunnable(private val taskExecution: TaskExecutionInstance) : Runnable {
        override fun run() {
            try {
                runTask(taskExecution)
                taskExecution.task.execute(taskExecution)
                completeTask(taskExecution)
            } catch (e: InterruptedException) {
                cancelTask(taskExecution)
            } catch (e: Exception) {
                logger.error(e) { "Task ${taskExecution.qualfiedTaskName} is failed." }
                failTask(taskExecution)
            }
        }
    }
}

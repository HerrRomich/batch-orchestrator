package io.github.herrromich.batch.internal

import io.github.herrromich.batch.*
import io.github.herrromich.batch.spi.ExecutorProvider
import mu.KotlinLogging
import java.util.ServiceLoader
import java.util.concurrent.*

internal class SimpleOrchestrator(threadPoolSize: Int, jobs: Set<Job>) : Orchestrator {
    private val logger = KotlinLogging.logger { }

    private val executor: Executor by lazy {
        val executorProvider = EXECUTOR_SERVICE_LOADER.singleOrNull() ?: ThreadPoolExecutorProvider
        val executor = executorProvider.provide(
            threadPoolSize,
            PriorityBlockingQueue(16)
        )
        logger.info { "Thread pool is initialized." }
        logger.debug { "Thread pool is initialized: $executor" }
        return@lazy executor
    }

    override val jobs = jobs.associateBy { it.jobName }
    private val executions = ConcurrentHashMap<Job, JobExecutionInstance>()

    override fun execute(jobName: String): JobContext {
        val jobCandidate = jobs.getOrElse(jobName) {
            val message = "Job \"$jobName\" is not registered in the batch orchestrator."
            val ex = OrchestratorException(message)
            logger.warn(message, ex)
            throw ex
        }
        val jobExecution = executions.compute(jobCandidate) { job, execution ->
            if (execution != null) {
                val message =
                    "Job \"$jobName\" is running right now! It is ot allowed to have multiple executions of the same job at the same time."
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
        if (fulfilledTasks.isEmpty()) {
            if (jobExecution.submittedTaskExecutions.isEmpty()) {
                finishJob(jobExecution)
            }
            return
        }
        logger.debug { "Following tasks in job \"${jobExecution.job.jobName}\" are fulfilled: ${fulfilledTasks.map { it.taskName }}" }
        fulfilledTasks
            .forEach {
                val taskExecution = jobExecution.queuedTaskExecutions.remove(it)!!
                jobExecution.submittedTaskExecutions.add(taskExecution)
                jobExecution.fulfilledTaskCount++
                taskExecution.changeState(TaskState.FULFILLED)
                logStatistics(jobExecution)
                val future = FutureTaskWithPriority(taskExecution)
                taskExecution.future = future
                executor.execute(future)
            }
    }

    private fun finishJob(jobExecution: JobExecutionInstance) =
        executions.compute(jobExecution.job) { job, execution ->
            if (execution == null) {
                val message =
                    "Fatal error. Unknown job execution $jobExecution."
                val ex = OrchestratorException(message)
                logger.error(message, ex)
                throw ex
            }
            execution.queuedTaskExecutions.values
                .forEach {
                    it.changeState(TaskState.SKIPPED)
                }
            if (execution.failedTaskCount != 0) {
                execution.finish(JobExecutionState.ERROR)
                execution.future.completeExceptionally(OrchestratorException("Job execution is failed."))
            } else if (execution.fatalTaskCount != 0) {
                execution.finish(JobExecutionState.FATAL)
                execution.future.completeExceptionally(OrchestratorException("Job execution is fatally failed."))
            } else if (execution.submittedTaskExecutions.isNotEmpty()) {
                execution.finish(JobExecutionState.FATAL)
                execution.future.completeExceptionally(OrchestratorException("Job execution has stuck."))
            } else {
                execution.finish(JobExecutionState.COMPLETED)
                execution.future.complete(null)
            }
            return@compute null
        }

    private fun runTask(taskExecution: TaskInstance) {
        val jobContext = taskExecution.jobContext
        return synchronized(jobContext) {
            jobContext.runningTaskCount++
            jobContext.fulfilledTaskCount--
            taskExecution.changeState(TaskState.RUNNING)
            logStatistics(jobContext)
        }
    }

    private fun completeTask(taskExecution: TaskInstance) {
        val jobContext = taskExecution.jobContext
        synchronized(jobContext) {
            jobContext.submittedTaskExecutions.remove(taskExecution)
            jobContext.runningTaskCount--
            jobContext.completedTaskCount++
            taskExecution.changeState(TaskState.COMPLETED)
            logStatistics(jobContext)
            reorganizeGraph(taskExecution)
        }
        submit(jobContext)
    }

    private fun cancelTask(taskExecution: TaskInstance) {
        val jobContext = taskExecution.jobContext
        synchronized(jobContext) {
            if (taskExecution.event.state == TaskState.RUNNING) {
                jobContext.runningTaskCount--
                jobContext.canceledTaskCount++
                taskExecution.changeState(TaskState.CANCELED)
                logStatistics(jobContext)
                jobContext.submittedTaskExecutions.remove(taskExecution)
            }
        }
        submit(jobContext)
    }

    private fun failTask(taskExecution: TaskInstance) {
        val jobContext = taskExecution.jobContext
        synchronized(jobContext) {
            jobContext.submittedTaskExecutions.remove(taskExecution)
            jobContext.runningTaskCount--
            when (taskExecution.task.failLevel) {
                FailLevel.WARN -> {
                    jobContext.warnTaskCount++
                    taskExecution.changeState(TaskState.WARN)
                    logStatistics(jobContext)
                    reorganizeGraph(taskExecution)
                }

                FailLevel.ERROR -> {
                    jobContext.failedTaskCount++
                    taskExecution.changeState(TaskState.ERROR)
                    logStatistics(jobContext)
                }

                FailLevel.FATAL -> {
                    jobContext.fatalTaskCount++
                    taskExecution.changeState(TaskState.FATAL)
                    logStatistics(jobContext)
                    jobContext.submittedTaskExecutions.forEach {
                        it.future?.cancel(true)
                        if (it.event.state == TaskState.FULFILLED) {
                            jobContext.fulfilledTaskCount--
                            jobContext.canceledTaskCount++
                            it.changeState(TaskState.CANCELED)
                            logStatistics(jobContext)
                        }
                    }
                    jobContext.submittedTaskExecutions.removeIf { it.event.state == TaskState.CANCELED }
                    jobContext.queuedTaskExecutions.forEach { _, taskExecution ->
                        jobContext.skippedTaskCount++
                        taskExecution.changeState(TaskState.SKIPPED)
                    }
                    jobContext.queuedTaskExecutions.clear()
                }
            }
        }
        submit(jobContext)
    }

    private fun reorganizeGraph(taskExecution: TaskInstance) {
        val jobContext = taskExecution.jobContext
        synchronized(jobContext) {
            jobContext.uncompletedProducibles.forEach { _, tasks ->
                tasks.remove(taskExecution)
            }
            val completedProducibles = jobContext.uncompletedProducibles.filterValues(Set<TaskInstance>::isEmpty).keys
            if (completedProducibles.isNotEmpty()) {
                logger.debug { "Following resources of job ${jobContext.job.jobName} have been fulfilled: $completedProducibles" }
            }
            jobContext.uncompletedProducibles -= completedProducibles
            jobContext.tasksWithUncompletedConsumables.forEach { _, consumables ->
                consumables -= completedProducibles
            }
        }
    }

    private fun logStatistics(jobExecution: JobExecutionInstance) {
        logger.info {
            "Job '${jobExecution.job.jobName}' statistics: " +
                    "fulfilled -> ${jobExecution.fulfilledTaskCount}; " +
                    "running -> ${jobExecution.runningTaskCount}; " +
                    "done -> ${jobExecution.completedTaskCount} / ${jobExecution.job.tasks.count()}"
        }
    }

    inner class FutureTaskWithPriority(private val taskExecution: TaskInstance) :
        FutureTask<Void>(TaskRunnable(taskExecution), null),
        Comparable<FutureTaskWithPriority> {
        override fun compareTo(other: FutureTaskWithPriority) =
            taskExecution.task.priority - other.taskExecution.task.priority
    }

    inner class TaskRunnable(private val taskInstance: TaskInstance) : Runnable {
        override fun run() {
            try {
                runTask(taskInstance)
                taskInstance.task.execute(taskInstance)
                completeTask(taskInstance)
            } catch (e: InterruptedException) {
                cancelTask(taskInstance)
            } catch (e: Exception) {
                logger.error(e) { "Task ${taskInstance.qualfiedTaskName} is failed." }
                failTask(taskInstance)
            }
        }
    }

    companion object {
        val EXECUTOR_SERVICE_LOADER = ServiceLoader.load(ExecutorProvider::class.java).apply {
            reload()
            if (count() > 1) {
                throw OrchestratorException("There are more then one registered ${ExecutorProvider::class.java} classes.")
            }
        }
    }
}

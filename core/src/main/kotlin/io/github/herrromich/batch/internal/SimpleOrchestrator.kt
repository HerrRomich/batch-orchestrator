package io.github.herrromich.batch.internal

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.JobContext
import io.github.herrromich.batch.Orchestrator
import io.github.herrromich.batch.OrchestratorException
import io.github.herrromich.batch.spi.JobExecutionInstanceProvider
import mu.KotlinLogging
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.concurrent.FutureTask

private val logger = KotlinLogging.logger { }

internal class SimpleOrchestrator(private val executor: Executor, jobs: Set<Job>) : Orchestrator {

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
            return@compute JobExecutionInstanceProvider.provide(job)
        }!!
        jobExecution.start()
        submit(jobExecution)
        return jobExecution
    }

    private fun submit(jobExecution: JobExecutionInstance) = synchronized(jobExecution) {
        if (jobExecution.isFinished) {
            finishJob(jobExecution)
            return
        }
        jobExecution.submit { taskExecution ->
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
            execution.finish()
            return@compute null
        }

    private fun runTask(taskExecution: TaskInstance) {
        taskExecution.execute()
        submit(taskExecution.jobContext)
    }

    private fun cancelTask(taskExecution: TaskInstance) {
        taskExecution.cancel()
        submit(taskExecution.jobContext)
    }

    private fun failTask(taskExecution: TaskInstance) {
        taskExecution.fail()
        submit(taskExecution.jobContext)
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
            } catch (e: InterruptedException) {
                cancelTask(taskInstance)
            } catch (e: Exception) {
                logger.error(e) { "Task ${taskInstance.qualifiedTaskName} is failed." }
                failTask(taskInstance)
            }
        }
    }
}

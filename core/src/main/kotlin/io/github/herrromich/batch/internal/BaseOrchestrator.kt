package io.github.herrromich.batch.internal

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.JobContext
import io.github.herrromich.batch.Orchestrator
import io.github.herrromich.batch.OrchestratorException
import mu.KotlinLogging
import java.util.concurrent.ConcurrentHashMap

private val logger = KotlinLogging.logger { }

abstract class BaseOrchestrator(jobs: Set<Job>) : Orchestrator {

    override val jobs = jobs.associateBy { it.name }
    private val executions = ConcurrentHashMap<Job, JobInstance>()

    override fun execute(jobName: String): JobContext {
        val jobCandidate = jobs.getOrElse(jobName) {
            val message = "Job \"$jobName\" is not registered in the batch orchestrator."
            val ex = OrchestratorException(message)
            logger.warn(message, ex)
            throw ex
        }
        val jobInstance = executions.compute(jobCandidate) { job, jobInstance ->
            if (jobInstance != null) {
                val message =
                    "Job \"$jobName\" is running right now! It is not allowed to have multiple executions of the same job at the same time."
                val ex = OrchestratorException(message)
                logger.error(message, ex)
                throw ex
            }
            val executionInstance = JobInstance.instance(job)
            executionInstance.start()
            return@compute executionInstance
        }!!
        submit(jobInstance)
        return jobInstance
    }

    protected fun submit(jobInstance: JobInstance) = synchronized(jobInstance) {
        if (jobInstance.isFinished) {
            finishJob(jobInstance.job)
            return
        }
        jobInstance.queueUp(::executeTask)
    }

    abstract fun executeTask(taskInstance: TaskInstance)

    private fun finishJob(job: Job) =
        executions.compute(job) { _, jobInstance ->
            if (jobInstance == null) {
                val message =
                    "Fatal error. Unknown jobInstance for job \"${job.name}\"."
                val ex = OrchestratorException(message)
                logger.error(message, ex)
                throw ex
            }
            jobInstance.finish()
            return@compute null
        }
}

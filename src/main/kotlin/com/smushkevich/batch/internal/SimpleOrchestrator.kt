package com.smushkevich.batch.internal

import com.smushkevich.batch.Job
import com.smushkevich.batch.Orchestrator
import com.smushkevich.batch.OrchestratorException
import mu.KotlinLogging
import java.util.concurrent.CompletableFuture

internal class SimpleOrchestrator(jobs: Set<Job>) : Orchestrator {
    private val logger = KotlinLogging.logger { }

    override val jobs: Map<String, Job>
    val executions = mutableSetOf<ExecutionInstance>()

    init {
        this.jobs = jobs.map { it.jobName to it }.toMap()
    }

    override fun execute(jobName: String): ExecutionInstance = synchronized(jobs) {
        val job = jobs.getOrElse(jobName) {
            val message = "Job \"$jobName\" is not registered in the batch orchestrator."
            val ex = OrchestratorException(message)
            logger.warn(message, ex)
            throw ex
        }.apply {
            try {
                validate()
            } catch (ex: OrchestratorException) {
                logger.warn("Cannot validate job: \"$jobName\"", ex)
                throw ex
            }
        }
        executions.firstOrNull { it.job == job }?.let {
            val message =
                "Job \"$jobName\" is running right now! It is ot allowed to have multiple executions of same job."
            val ex = OrchestratorException(message)
            logger.error(message, ex)
            throw ex
        }
        return ExecutionInstance(job, CompletableFuture())
    }
}
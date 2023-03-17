package io.github.herrromich.batch

import kotlin.jvm.Throws

/**
 * Batch orchestrator.
 */
interface Orchestrator {

    /**
     * Configured available jobs.
     */
    val jobs: Map<String, Job>

    /**
     * Starts an execution of job.
     *
     * @return an executing job context.
     *
     * @throws OrchestratorException if job is invalid or already executing.
     */
    @Throws( OrchestratorException::class)
    fun execute(jobName: String): JobContext
}
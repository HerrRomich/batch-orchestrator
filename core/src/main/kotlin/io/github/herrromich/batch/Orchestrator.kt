package io.github.herrromich.batch

import io.github.herrromich.batch.internal.SimpleOrchestrator
import java.util.concurrent.Executor

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

    companion object {
        @JvmStatic
        fun instance(executor: Executor, jobs: Set<Job>): Orchestrator = SimpleOrchestrator(executor, jobs)
    }
}
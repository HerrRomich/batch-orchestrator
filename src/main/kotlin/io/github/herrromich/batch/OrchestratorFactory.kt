package io.github.herrromich.batch

import io.github.herrromich.batch.config.OrchestratorJobFactory
import io.github.herrromich.batch.internal.SimpleOrchestratorFactory

/**
 *
 */
interface OrchestratorFactory {
    val threadPoolSize: Int

    /**
     * Jobs, that will be configured in batch orchestrator.
     */
    val jobs: Set<Job>

    /**
     * Sets the size of thread pool.
     */
    fun thredPoolSize(threadPoolSize: Int): OrchestratorFactory

    /**
     * Adds a job to list of configured with specified [jobName].
     *
     * @return A job factory.
     */
    fun job(jobName: String): OrchestratorJobFactory

    /**
     * configures and creates a batch orchestrator
     */
    fun build(): Orchestrator

    companion object {
        /**
         * Instantiates a batch orchestrator factory.
         */
        @JvmStatic
        fun instance(): OrchestratorFactory = SimpleOrchestratorFactory()
    }

}

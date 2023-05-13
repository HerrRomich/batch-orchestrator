package io.github.herrromich.batch

import io.github.herrromich.batch.config.OrchestratorJobFactory
import io.github.herrromich.batch.internal.SimpleOrchestratorFactory
import io.github.herrromich.batch.spi.ExecutorConfiguration

/**
 *
 */
interface OrchestratorFactory {
    val configuration: ExecutorConfiguration

    /**
     * Jobs, that will be configured in batch orchestrator.
     */
    val jobs: Set<Job>

    fun configuration(configuration: ExecutorConfiguration): OrchestratorFactory

    /**
     * Adds a job to list of configured with specified [jobName].
     *
     * @return A job factory.
     */
    fun job(jobName: String): OrchestratorJobFactory

    /**
     * Adds a job to list of configured.
     *
     * @return A job factory.
     */
    fun job(job: Job): OrchestratorFactory

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

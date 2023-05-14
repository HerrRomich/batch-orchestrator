package io.github.herrromich.batch.builder.fluenrt.orchestrator

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.Orchestrator
import io.github.herrromich.batch.OrchestratorConfiguration
import io.github.herrromich.batch.builder.fluenrt.orchestrator.internal.BaseFluentOrchestratorBuilder

/**
 *
 */
interface FluentOrchestratorBuilder {
    /**
     * Configuration for orchestrator
     */
    val configuration: OrchestratorConfiguration

    /**
     * Jobs, that will be configured in batch orchestrator.
     */
    val jobs: Set<Job>

    /**
     * Sets configuration for orchesrator
     */
    fun configuration(configuration: OrchestratorConfiguration): FluentOrchestratorBuilder

    /**
     * Adds a job to list of configured with specified [name].
     *
     * @return A job builder.
     */
    fun job(name: String): FluentOrchestratorJobConfigBuilder

    /**
     * Adds a job to list of configured.
     *
     * @return A job builder.
     */
    fun job(job: Job): FluentOrchestratorJobInterfaceBuilder

    /**
     * configures and creates a batch orchestrator
     */
    fun build(): Orchestrator

    companion object {

        @JvmStatic
        fun instance(): FluentOrchestratorBuilder = object : BaseFluentOrchestratorBuilder() {
            override fun createOrchestrator() = Orchestrator.instance(jobs, configuration)
        }

    }
}

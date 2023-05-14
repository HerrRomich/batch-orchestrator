package io.github.herrromich.batch.builder.nested.orchestrator

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.Orchestrator
import io.github.herrromich.batch.OrchestratorConfiguration
import io.github.herrromich.batch.builder.nested.orchestrator.internal.NestedBaseOrchestratorBuilder

/**
 *
 */
interface NestedOrchestratorBuilder {
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
    fun configuration(configuration: OrchestratorConfiguration): NestedOrchestratorBuilder

    /**
     * Adds a job to list of configured with specified [name].
     *
     * @return A job builder.
     */
    fun job(
        name: String,
        jobProvider: (jobBuilder: NestedOrchestratorJobBuilder) -> NestedOrchestratorTaskInterfaceBuilder = {
            it.none()
        }
    ): NestedOrchestratorJobInterfaceBuilder

    /**
     * Adds a job to list of configured.
     *
     * @return A job builder.
     */
    fun job(job: Job): NestedOrchestratorJobInterfaceBuilder

    /**
     * configures and creates a batch orchestrator
     */
    fun build(): Orchestrator

    companion object {

        @JvmStatic
        fun instance(): NestedOrchestratorBuilder = object : NestedBaseOrchestratorBuilder() {
            override fun createOrchestrator() = Orchestrator.instance(jobs, configuration)
        }

    }
}

package io.github.herrromich.batch.builder.nested.orchestrator

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.Orchestrator

interface NestedOrchestratorJobInterfaceBuilder {
    val taskName: String
    val jobName: String

    /**
     * Jobs, that will be configured in batch orchestrator.
     */
    val jobs: Set<Job>

    fun andJob(
        name: String,
        jobProvider: (jobBuilder: NestedOrchestratorJobBuilder) -> NestedOrchestratorTaskInterfaceBuilder = { it.none() }
    ): NestedOrchestratorJobInterfaceBuilder

    fun andJob(job: Job): NestedOrchestratorJobInterfaceBuilder

    fun and(): NestedOrchestratorBuilder

    fun build(): Orchestrator
}

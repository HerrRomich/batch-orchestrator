package io.github.herrromich.batch.builder.fluenrt.orchestrator

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.Orchestrator

interface FluentOrchestratorJobInterfaceBuilder {
    /**
     * Jobs, that will be configured in batch orchestrator.
     */
    val jobs: Set<Job>

    fun andJob(name: String): FluentOrchestratorJobConfigBuilder
    fun andJob(job: Job): FluentOrchestratorJobInterfaceBuilder
    fun and(): FluentOrchestratorBuilder
    fun build(): Orchestrator
}

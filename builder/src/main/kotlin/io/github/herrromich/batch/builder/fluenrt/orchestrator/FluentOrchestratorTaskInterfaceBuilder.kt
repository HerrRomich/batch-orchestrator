package io.github.herrromich.batch.builder.fluenrt.orchestrator

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.Task

interface FluentOrchestratorTaskInterfaceBuilder {
    /**
     * Jobs, that will be configured in batch orchestrator.
     */
    val jobs: Set<Job>

    fun andTask(name: String): FluentOrchestratorJobAndTaskConfigBuilder
    fun andTask(task: Task): FluentOrchestratorJobAndTaskInterfaceBuilder
    fun and(): FluentOrchestratorJobAndTaskBuilder
}

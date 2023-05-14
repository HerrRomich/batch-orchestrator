package io.github.herrromich.batch.builder.fluenrt.orchestrator

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.Task

interface FluentOrchestratorJobBuilder {
    /**
     * Jobs, that will be configured in batch orchestrator.
     */
    val jobs: Set<Job>

    val jobName: String
    fun task(name: String): FluentOrchestratorJobAndTaskConfigBuilder
    fun task(task: Task): FluentOrchestratorJobAndTaskInterfaceBuilder
}
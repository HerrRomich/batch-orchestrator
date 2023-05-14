package io.github.herrromich.batch.builder.nested.orchestrator

import io.github.herrromich.batch.Task

interface NestedOrchestratorTaskInterfaceBuilder {
    fun andTask(
        name: String,
        taskProvider: (taskBuilder: NestedOrchestratorTaskConfigBuilder) -> NestedOrchestratorTaskConfigBuilder = { it }
    ): NestedOrchestratorTaskInterfaceBuilder

    fun andTask(task: Task): NestedOrchestratorTaskInterfaceBuilder
    fun and(): NestedOrchestratorJobBuilder
}

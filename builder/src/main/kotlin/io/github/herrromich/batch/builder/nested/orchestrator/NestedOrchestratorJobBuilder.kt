package io.github.herrromich.batch.builder.nested.orchestrator

import io.github.herrromich.batch.Task

interface NestedOrchestratorJobBuilder {
    val jobName: String

    fun task(
        name: String,
        taskProvider: (taskBuilder: NestedOrchestratorTaskConfigBuilder) -> NestedOrchestratorTaskConfigBuilder = { it }
    ): NestedOrchestratorTaskInterfaceBuilder

    fun task(task: Task): NestedOrchestratorTaskInterfaceBuilder

    fun none(): NestedOrchestratorTaskInterfaceBuilder
}
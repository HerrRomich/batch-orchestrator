package io.github.herrromich.batch.builder.nested.orchestrator.internal

import io.github.herrromich.batch.Task
import io.github.herrromich.batch.builder.nested.orchestrator.NestedOrchestratorJobBuilder
import io.github.herrromich.batch.builder.nested.orchestrator.NestedOrchestratorTaskConfigBuilder
import io.github.herrromich.batch.builder.nested.orchestrator.NestedOrchestratorTaskInterfaceBuilder

internal class NestedOrchestratorTaskBuilder(private val orchestratorBuilder: NestedBaseOrchestratorBuilder) : NestedOrchestratorTaskInterfaceBuilder {

    override fun andTask(
        name: String,
        taskProvider: (taskBuilder: NestedOrchestratorTaskConfigBuilder) -> NestedOrchestratorTaskConfigBuilder
    ): NestedOrchestratorTaskInterfaceBuilder {
        orchestratorBuilder.andTask(name, taskProvider)
        return this
    }

    override fun andTask(task: Task): NestedOrchestratorTaskInterfaceBuilder {
        orchestratorBuilder.andTask(task)
        return this
    }

    override fun and(): NestedOrchestratorJobBuilder {
        return orchestratorBuilder
    }

}

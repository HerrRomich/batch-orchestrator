package io.github.herrromich.batch.builder.nested.standalone

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.Task

interface NestedStandaloneTaskInterfaceBuilder {
    val taskName: String
    fun andTask(
        name: String,
        taskProvider: (taskBuilder: NestedStandaloneTaskConfigBuilder) -> NestedStandaloneTaskConfigBuilder = { it }
    ): NestedStandaloneTaskInterfaceBuilder

    fun andTask(task: Task): NestedStandaloneTaskInterfaceBuilder
    fun and(): NestedStandaloneJobBuilder
    fun build(): Job
}

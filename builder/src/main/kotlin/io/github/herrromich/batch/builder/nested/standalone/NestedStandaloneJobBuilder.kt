package io.github.herrromich.batch.builder.nested.standalone

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.Task
import io.github.herrromich.batch.builder.nested.standalone.internal.NestedStandaloneJobBuilderImpl

interface NestedStandaloneJobBuilder {
    val jobName: String
    fun task(
        name: String,
        taskProvider: (taskBuilder: NestedStandaloneTaskConfigBuilder) -> NestedStandaloneTaskConfigBuilder = { it }
    ): NestedStandaloneTaskInterfaceBuilder
    fun task(task: Task): NestedStandaloneTaskInterfaceBuilder
    fun build(): Job

    companion object {
        @JvmStatic
        fun instance(jobName: String): NestedStandaloneJobBuilder = NestedStandaloneJobBuilderImpl(jobName)
    }
}

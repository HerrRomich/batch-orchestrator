package io.github.herrromich.batch.builder.fluenrt.standalone

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.Task
import io.github.herrromich.batch.builder.fluenrt.standalone.internal.FluentStandaloneJobBuilderImpl

interface FluentStandaloneJobBuilder {
    val jobName: String
    fun task(name: String): FluentStandaloneTaskConfigBuilder
    fun task(task: Task): FluentStandaloneTaskInterfaceBuilder
    fun build(): Job

    companion object {
        @JvmStatic
        fun instance(jobName: String): FluentStandaloneJobBuilder = FluentStandaloneJobBuilderImpl(jobName)
    }
}
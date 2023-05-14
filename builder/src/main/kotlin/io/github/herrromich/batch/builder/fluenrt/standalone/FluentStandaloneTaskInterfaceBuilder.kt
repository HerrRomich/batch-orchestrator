package io.github.herrromich.batch.builder.fluenrt.standalone

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.Task

interface FluentStandaloneTaskInterfaceBuilder {
    val taskName: String
    fun andTask(name: String): FluentStandaloneTaskConfigBuilder
    fun andTask(task: Task): FluentStandaloneTaskInterfaceBuilder
    fun and(): FluentStandaloneJobBuilder
    fun build(): Job
}

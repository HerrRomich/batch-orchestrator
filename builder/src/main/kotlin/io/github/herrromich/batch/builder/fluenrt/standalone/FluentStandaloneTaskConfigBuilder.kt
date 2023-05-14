package io.github.herrromich.batch.builder.fluenrt.standalone

import io.github.herrromich.batch.FailLevel
import io.github.herrromich.batch.Job
import io.github.herrromich.batch.Task

interface FluentStandaloneTaskConfigBuilder {
    fun priority(priority: Int): FluentStandaloneTaskConfigBuilder
    fun failLevel(failLevel: FailLevel): FluentStandaloneTaskConfigBuilder
    fun consumables(vararg consumables: String): FluentStandaloneTaskConfigBuilder
    fun consumables(consumables: Collection<String>): FluentStandaloneTaskConfigBuilder
    fun producibles(vararg producibles: String): FluentStandaloneTaskConfigBuilder
    fun producibles(producibles: Collection<String>): FluentStandaloneTaskConfigBuilder
    fun andTask(name: String): FluentStandaloneTaskConfigBuilder
    fun andTask(task: Task): FluentStandaloneTaskInterfaceBuilder
    fun and(): FluentStandaloneJobBuilder
    fun build(): Job
}

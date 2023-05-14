package io.github.herrromich.batch.builder.fluenrt.standalone

import io.github.herrromich.batch.FailLevel
import io.github.herrromich.batch.Task
import io.github.herrromich.batch.builder.fluenrt.standalone.internal.FluentStandaloneTaskBuilderImpl

interface FluentStandaloneTaskBuilder {
    val taskName: String
    fun priority(priority: Int): FluentStandaloneTaskBuilder
    fun failLevel(failLevel: FailLevel): FluentStandaloneTaskBuilder
    fun consumables(vararg consumables: String): FluentStandaloneTaskBuilder
    fun consumables(consumables: Collection<String>): FluentStandaloneTaskBuilder
    fun producibles(vararg producibles: String): FluentStandaloneTaskBuilder
    fun producibles(producibles: Collection<String>): FluentStandaloneTaskBuilder
    fun build(): Task

    companion object {
        @JvmStatic
        fun instance(taskName: String): FluentStandaloneTaskBuilder = FluentStandaloneTaskBuilderImpl(taskName)
    }
}

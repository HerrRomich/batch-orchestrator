package io.github.herrromich.batch.fluent.dsl

import io.github.herrromich.batch.FailLevel
import io.github.herrromich.batch.Task
import io.github.herrromich.batch.builder.fluenrt.standalone.FluentStandaloneTaskBuilder

interface DslTaskBuilder {
    fun priority(priority: Int)
    fun failLevel(failLevel: FailLevel)
    fun consumables(vararg consumables: String)
    fun producibles(vararg producibles: String)
    fun build(): Task
}

abstract class BaseDslTaskBuilder() : DslTaskBuilder {
    abstract val builder: FluentStandaloneTaskBuilder

    override fun priority(priority: Int) {
        builder.priority(priority)
    }

    override fun failLevel(failLevel: FailLevel) {
        builder.failLevel(failLevel)
    }

    override fun consumables(vararg consumables: String) {
        builder.consumables(*consumables)
    }

    override fun producibles(vararg producibles: String) {
        builder.producibles(*producibles)
    }

    override fun build() = builder.build()
}

package io.github.herrromich.batch.dsl

import io.github.herrromich.batch.FailLevel
import io.github.herrromich.batch.TaskContext
import io.github.herrromich.batch.config.SimpleStandaloneJobFactory
import io.github.herrromich.batch.config.SimpleStandaloneTaskFactory

interface DslTaskFactory {
    fun priority(priority: Int)
    fun failLevel(failLevel: FailLevel)
    fun consumables(vararg consumables: String)
    fun producibles(vararg producibles: String)
    fun runnable(runnable: (context: TaskContext) -> Unit)
}

internal class SimpleDslTaskFactory(jobFactory: SimpleStandaloneJobFactory, taskName: String) : DslTaskFactory {
    private val factory = SimpleStandaloneTaskFactory(jobFactory, taskName)

    override fun priority(priority: Int) {
        factory.priority(priority)
    }

    override fun failLevel(failLevel: FailLevel) {
        factory.failLevel(failLevel)
    }

    override fun consumables(vararg consumables: String) {
        factory.consumables(*consumables)
    }

    override fun producibles(vararg producibles: String) {
        factory.producibles(*producibles)
    }

    override fun runnable(runnable: (context: TaskContext) -> Unit) {
        factory.runnable(runnable)
    }

    fun build() = factory.buildTask()
}

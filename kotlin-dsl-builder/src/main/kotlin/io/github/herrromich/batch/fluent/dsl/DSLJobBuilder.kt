package io.github.herrromich.batch.fluent.dsl

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.builder.fluenrt.standalone.FluentStandaloneJobBuilder

interface DSLJobBuilder {
    fun task(name: String, init: DslTaskBuilder.() -> Unit = { })
    fun build(): Job
}

abstract class BaseDslJobBuilder(jobName: String) : DSLJobBuilder {
    private val builder: FluentStandaloneJobBuilder by lazy { createJobBuilder(jobName) }

    abstract fun createJobBuilder(jobName: String): FluentStandaloneJobBuilder

    override fun task(name: String, init: DslTaskBuilder.() -> Unit) {
        val taskBuilder = createTaskBuilder(name)
        taskBuilder.init()
        builder.task(taskBuilder.build())
    }

    abstract fun createTaskBuilder(taskName: String): DslTaskBuilder

    override fun build() = builder.build()
}

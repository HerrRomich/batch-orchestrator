package io.github.herrromich.batch.fluent.dsl

import io.github.herrromich.batch.Task
import io.github.herrromich.batch.builder.fluenrt.orchestrator.FluentOrchestratorBuilder

interface DSLOrchestratorBuilder {
    fun job(name: String, init: DSLJobBuilder.() -> Unit = {})
}

abstract class BaseDSLOrchestratorBuilder<T: Task> : DSLOrchestratorBuilder {
    private val builder = FluentOrchestratorBuilder.instance()

    override fun job(name: String, init: DSLJobBuilder.() -> Unit) {
        val jobBuilder = createJobBuilder(name)
        jobBuilder.init()
        builder.job(jobBuilder.build())
    }

    abstract fun createJobBuilder(jobName: String): DSLJobBuilder

    fun build() = builder.build()
}

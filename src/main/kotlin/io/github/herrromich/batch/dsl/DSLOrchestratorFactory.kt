package io.github.herrromich.batch.dsl

import io.github.herrromich.batch.Orchestrator
import io.github.herrromich.batch.internal.SimpleOrchestratorFactory

interface DSLOrchestratorFactory {
    fun job(jobName: String, init: DslJobFactory.() -> Unit = {})
}

internal class SimpleDSLOrchestratorFactory : DSLOrchestratorFactory {
    private val factory = SimpleOrchestratorFactory()

    override fun job(jobName: String, init: DslJobFactory.() -> Unit) {
        val jobFactory = SimpleDslJobFactory(jobName)
        jobFactory.init()
        factory.addJob(jobFactory.build())
    }

    internal fun build() = factory.build()
}

fun orchestrator(init: DSLOrchestratorFactory.() -> Unit): Orchestrator {
    val factory = SimpleDSLOrchestratorFactory()
    factory.init()
    return factory.build()
}

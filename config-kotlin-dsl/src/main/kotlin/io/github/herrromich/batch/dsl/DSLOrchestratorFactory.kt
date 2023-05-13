package io.github.herrromich.batch.dsl

import io.github.herrromich.batch.Orchestrator
import io.github.herrromich.batch.OrchestratorFactory

interface DSLOrchestratorFactory {
    fun job(jobName: String, init: DslJobFactory.() -> Unit = {})
}

internal class SimpleDSLOrchestratorFactory : DSLOrchestratorFactory {
    private val factory: OrchestratorFactory = OrchestratorFactory.instance()

    override fun job(jobName: String, init: DslJobFactory.() -> Unit) {
        val jobFactory = SimpleDslJobFactory(jobName)
        jobFactory.init()
        factory.job(jobFactory.build())
    }

    internal fun build() = factory.build()
}

fun orchestrator(init: DSLOrchestratorFactory.() -> Unit): Orchestrator {
    val factory = SimpleDSLOrchestratorFactory()
    factory.init()
    return factory.build()
}

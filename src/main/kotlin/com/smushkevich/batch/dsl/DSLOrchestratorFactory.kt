package com.smushkevich.batch.dsl

import com.smushkevich.batch.Orchestrator
import com.smushkevich.batch.internal.SimpleOrchestratorFactory

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

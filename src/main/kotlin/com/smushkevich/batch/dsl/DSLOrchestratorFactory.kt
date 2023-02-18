package com.smushkevich.batch.dsl

import com.smushkevich.batch.Orchestrator
import com.smushkevich.batch.internal.SimpleOrchestratorFactory

interface DSLOrchestratorFactory {
    fun job(jobName: String, init: DslJobFactory.() -> Unit = {}): Unit
}

fun orchestrator(init: DSLOrchestratorFactory.() -> Unit): Orchestrator {
    val factory = SimpleOrchestratorFactory()
    factory.init()
    return factory.build()
}

package io.github.herrromich.batch.fluent.dsl

import io.github.herrromich.batch.Orchestrator
import io.github.herrromich.batch.TestTask

class TestDSLOrchestratorBuilder: BaseDSLOrchestratorBuilder<TestTask>() {
    override fun createJobBuilder(jobName: String) =TestJobBuiilder(jobName)
}

fun testOrchestrator(init: DSLOrchestratorBuilder.() -> Unit): Orchestrator {
    val factory = TestDSLOrchestratorBuilder()
    factory.init()
    return factory.build()
}
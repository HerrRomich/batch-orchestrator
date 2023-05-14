package io.github.herrromich.batch.builder.fluenrt.orchestrator

import io.github.herrromich.batch.Consumer
import io.github.herrromich.batch.FailLevel

interface FluentOrchestratorTaskConfigBuilder : FluentOrchestratorJobInterfaceBuilder, FluentOrchestratorTaskInterfaceBuilder {
    val taskName: String
    fun priority(priority: Int): FluentOrchestratorTaskConfigBuilder
    fun failLevel(failLevel: FailLevel): FluentOrchestratorTaskConfigBuilder
    fun consumables(vararg consumables: String): FluentOrchestratorTaskConfigBuilder
    fun consumables(consumables: Collection<String>): FluentOrchestratorTaskConfigBuilder
    fun producibles(vararg producibles: String): FluentOrchestratorTaskConfigBuilder
    fun producibles(producibles: Collection<String>): FluentOrchestratorTaskConfigBuilder
    fun runnable(runnable: Consumer): FluentOrchestratorTaskConfigBuilder
}

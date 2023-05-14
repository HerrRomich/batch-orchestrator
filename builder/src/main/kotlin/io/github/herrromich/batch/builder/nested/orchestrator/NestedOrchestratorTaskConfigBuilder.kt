package io.github.herrromich.batch.builder.nested.orchestrator

import io.github.herrromich.batch.Consumer
import io.github.herrromich.batch.FailLevel

interface NestedOrchestratorTaskConfigBuilder {
    val taskName: String
    fun priority(priority: Int): NestedOrchestratorTaskConfigBuilder
    fun failLevel(failLevel: FailLevel): NestedOrchestratorTaskConfigBuilder
    fun consumables(vararg consumables: String): NestedOrchestratorTaskConfigBuilder
    fun consumables(consumables: Collection<String>): NestedOrchestratorTaskConfigBuilder
    fun producibles(vararg producibles: String): NestedOrchestratorTaskConfigBuilder
    fun producibles(producibles: Collection<String>): NestedOrchestratorTaskConfigBuilder
    fun runnable(runnable: Consumer): NestedOrchestratorTaskConfigBuilder
}

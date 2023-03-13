package io.github.herrromich.batch

import io.github.herrromich.batch.config.OrchestratorJobFactory
import io.github.herrromich.batch.internal.SimpleOrchestratorFactory

interface OrchestratorFactory {
    val threadPoolSize: Int
    val jobs: Set<Job>
    fun thredPoolSize(threadPoolSize: Int): OrchestratorFactory
    fun job(jobName: String): OrchestratorJobFactory
    fun build(): Orchestrator

    companion object {
        @JvmStatic
        fun instance(): OrchestratorFactory = SimpleOrchestratorFactory()
    }

}

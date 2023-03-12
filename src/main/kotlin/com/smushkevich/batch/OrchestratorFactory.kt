package com.smushkevich.batch

import com.smushkevich.batch.config.OrchestratorJobFactory
import com.smushkevich.batch.internal.SimpleOrchestratorFactory

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

package io.github.herrromich.batch.concurrent

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.Orchestrator
import io.github.herrromich.batch.concurrent.internal.ConcurrentOrchestratorImpl
import java.util.concurrent.Executor

object ExecutorOrchestratorProvider {
    @JvmStatic
    fun provideOrchestrator(jobs: Set<Job>, executor: Executor): Orchestrator =
        ConcurrentOrchestratorImpl(jobs, executor)
}
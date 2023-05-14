package io.github.herrromich.batch

import io.github.herrromich.batch.internal.BaseOrchestrator
import io.github.herrromich.batch.internal.TaskInstance

class TestOrchestratorProvider: OrchestratorProvider {
    override fun provideOrchestrator(jobs: Set<Job>, configuration: OrchestratorConfiguration) = object:BaseOrchestrator(jobs) {
        override fun executeTask(taskInstance: TaskInstance) {
            TODO("Not yet implemented")
        }

    }
}
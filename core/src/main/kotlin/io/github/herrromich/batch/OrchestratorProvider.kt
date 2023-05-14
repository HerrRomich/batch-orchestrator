package io.github.herrromich.batch

interface OrchestratorProvider {
    fun provideOrchestrator(jobs: Set<Job>, configuration: OrchestratorConfiguration): Orchestrator
}
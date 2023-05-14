package io.github.herrromich.batch.concurrent.internal

import io.github.herrromich.batch.*
import io.github.herrromich.batch.concurrent.ConcurrentOrchestratorConfiguration

internal class ConcurrentOrchestratorProvider : OrchestratorProvider {
    override fun provideOrchestrator(jobs: Set<Job>, configuration: OrchestratorConfiguration): Orchestrator {
        return (configuration as? ConcurrentOrchestratorConfiguration)?.let { ConcurrentOrchestratorImpl(jobs, it) }
            ?: throw OrchestratorException(
                """Configuration of wrong type:
  expected: ${ConcurrentOrchestratorConfiguration::class.java.name}
     found: ${configuration::class.java.name}
        """.trimIndent()
            )
    }
}
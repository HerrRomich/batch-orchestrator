package io.github.herrromich.batch.config

import io.github.herrromich.batch.Orchestrator
import io.github.herrromich.batch.OrchestratorFactory

interface OrchestratorJobFactory : JobFactory<OrchestratorJobFactory, OrchestratorTaskFactory> {
    fun andJob(jobName: String): OrchestratorJobFactory

    fun and(): OrchestratorFactory

    fun build(): Orchestrator
}
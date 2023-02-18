package com.smushkevich.batch.config

import com.smushkevich.batch.Orchestrator
import com.smushkevich.batch.OrchestratorFactory

interface OrchestratorJobFactory : JobFactory<OrchestratorJobFactory, OrchestratorTaskFactory> {
    fun andJob(jobName: String): OrchestratorJobFactory

    fun and(): OrchestratorFactory

    fun build(): Orchestrator
}
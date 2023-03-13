package io.github.herrromich.batch.config

import io.github.herrromich.batch.Orchestrator

interface OrchestratorTaskFactory : TaskFactory<OrchestratorJobFactory, OrchestratorTaskFactory> {

    fun andJob(jobName:String): OrchestratorJobFactory

    fun build(): Orchestrator

}

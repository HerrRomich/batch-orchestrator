package com.smushkevich.batch.config

import com.smushkevich.batch.Orchestrator

interface OrchestratorTaskFactory : TaskFactory<OrchestratorJobFactory, OrchestratorTaskFactory> {

    fun andJob(jobName:String): OrchestratorJobFactory

    fun build(): Orchestrator

}

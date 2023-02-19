package com.smushkevich.batch.config

import com.smushkevich.batch.Orchestrator

internal class SimpleOrchestratorTaskFactory(
    jobFactory: OrchestratorJobFactory,
    taskName: String
) : SimpleTaskFactory<OrchestratorJobFactory, OrchestratorTaskFactory>(jobFactory, taskName),
    OrchestratorTaskFactory {
    override val self: OrchestratorTaskFactory
        get() = this

    override fun andJob(jobName: String): OrchestratorJobFactory {
        jobFactory.addTask(taskConfig)
        return jobFactory.andJob(jobName)
    }

    override fun build(): Orchestrator {
        jobFactory.addTask(taskConfig)
        return jobFactory.build()
    }
}

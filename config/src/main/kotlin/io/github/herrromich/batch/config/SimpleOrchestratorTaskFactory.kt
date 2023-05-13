package io.github.herrromich.batch.config

import io.github.herrromich.batch.Orchestrator

internal class SimpleOrchestratorTaskFactory(
    jobFactory: OrchestratorJobFactory,
    taskName: String
) : SimpleTaskFactory<OrchestratorJobFactory, OrchestratorTaskFactory>(jobFactory, taskName),
    OrchestratorTaskFactory {
    override val self: OrchestratorTaskFactory
        get() = this

    override fun andJob(jobName: String): OrchestratorJobFactory {
        jobFactory.task(taskConfig)
        return jobFactory.andJob(jobName)
    }

    override fun build(): Orchestrator {
        jobFactory.task(taskConfig)
        return jobFactory.build()
    }
}

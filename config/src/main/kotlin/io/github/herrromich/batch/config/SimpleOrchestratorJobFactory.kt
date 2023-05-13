package io.github.herrromich.batch.config

import io.github.herrromich.batch.Orchestrator
import io.github.herrromich.batch.OrchestratorException
import io.github.herrromich.batch.internal.SimpleOrchestratorFactory

internal class SimpleOrchestratorJobFactory(
    private val orchestratorFactory: SimpleOrchestratorFactory,
    jobName: String
) : SimpleJobFactory<OrchestratorJobFactory, OrchestratorTaskFactory>(jobName), OrchestratorJobFactory {
    override val self: OrchestratorJobFactory
        get() = this

    override fun and(): SimpleOrchestratorFactory {
        orchestratorFactory.job(jobConfig)
        return orchestratorFactory
    }

    override fun task(taskName: String): SimpleOrchestratorTaskFactory {
        jobConfig.tasks.firstOrNull { it.name == taskName }
            ?.let { throw OrchestratorException("Task \"$taskName\" already contains in JobFactory: \"${jobConfig.jobName}\"") }
        return SimpleOrchestratorTaskFactory(this, taskName)
    }

    override fun andJob(jobName: String): OrchestratorJobFactory {
        orchestratorFactory.job(jobConfig)
        return orchestratorFactory.job(jobName)
    }

    override fun build(): Orchestrator {
        orchestratorFactory.job(jobConfig)
        return orchestratorFactory.build()
    }
}

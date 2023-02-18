package com.smushkevich.batch.config

import com.smushkevich.batch.Orchestrator
import com.smushkevich.batch.OrchestratorException
import com.smushkevich.batch.internal.SimpleOrchestratorFactory

internal class SimpleOrchestratorJobFactory(
    private val orchestratorFactory: SimpleOrchestratorFactory,
    jobConfig: JobConfig
) : SimpleJobFactory<OrchestratorJobFactory, OrchestratorTaskFactory>(jobConfig), OrchestratorJobFactory {
    override val self: OrchestratorJobFactory
        get() = this

    override fun and(): SimpleOrchestratorFactory {
        orchestratorFactory.addJob(jobConfig)
        return orchestratorFactory
    }

    override fun task(taskName: String): SimpleOrchestratorTaskFactory {
        jobConfig.tasks.firstOrNull { it.taskName == taskName }
            ?.let { throw OrchestratorException("Task \"$taskName\" already contains in JobFactory: \"${jobConfig.jobName}\"") }
        return SimpleOrchestratorTaskFactory(this, TaskConfig(jobConfig.jobName, taskName))
    }

    override fun andJob(jobName: String): OrchestratorJobFactory {
        orchestratorFactory.addJob(jobConfig)
        return orchestratorFactory.job(jobName)
    }

    override fun build(): Orchestrator {
        orchestratorFactory.addJob(jobConfig)
        return orchestratorFactory.build()
    }
}

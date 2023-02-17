package com.smushkevich.batch.config

import com.smushkevich.batch.Job
import com.smushkevich.batch.Orchestrator
import com.smushkevich.batch.internal.SimpleOrchestratorFactory

internal class SimpleJobFactory(
    private val orchestratorFactory: SimpleOrchestratorFactory,
    private var jobConfig: JobConfig
) : JobFactory, Job by jobConfig {

    override fun and(): SimpleOrchestratorFactory {
        orchestratorFactory.addJob(jobConfig)
        return orchestratorFactory
    }

    override fun jobName(jobName: String): JobFactory {
        jobConfig =
            jobConfig.copy(jobName = jobName, tasks = jobConfig.tasks.map { it.copy(jobName = jobName) }.toSet())
        return this
    }

    override fun task(taskName: String) = SimpleTaskFactory(this, TaskConfig(jobConfig.jobName, taskName))

    fun addTask(taskConfig: TaskConfig) {
        jobConfig = jobConfig.copy(tasks = jobConfig.tasks + taskConfig)
    }

    override fun andJob(jobName: String): JobFactory {
        orchestratorFactory.addJob(jobConfig)
        return orchestratorFactory.job(jobName)
    }

    override fun build(): Orchestrator {
        orchestratorFactory.addJob(jobConfig)
        return orchestratorFactory.build()
    }
}

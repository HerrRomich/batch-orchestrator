package com.smushkevich.batch.config

import com.smushkevich.batch.Job
import com.smushkevich.batch.OrchestratorException

internal class SimpleStandaloneJobFactory(jobName: String) :
    SimpleJobFactory<StandaloneJobFactory, StandaloneTaskFactory>(jobName), StandaloneJobFactory {
    override val self: StandaloneJobFactory
        get() = this

    override fun task(taskName: String): SimpleStandaloneTaskFactory {
        jobConfig.tasks.firstOrNull { it.taskName == taskName }
            ?.let { throw OrchestratorException("Task \"$taskName\" already contains in JobFactory: \"${jobConfig.jobName}\"") }
        return SimpleStandaloneTaskFactory(this, taskName)
    }

    override fun build(): Job {
        return jobConfig.copy()
    }
}

package com.smushkevich.batch.config

import com.smushkevich.batch.Job
import com.smushkevich.batch.Task

internal class SimpleStandaloneTaskFactory(
    jobFactory: SimpleStandaloneJobFactory,
    taskName: String
) : SimpleTaskFactory<StandaloneJobFactory, StandaloneTaskFactory>(jobFactory, taskName),
    StandaloneTaskFactory {
    override val self: StandaloneTaskFactory
        get() = this

    fun buildTask(): Task {
        return this.taskConfig.copy()
    }

    override fun build(): Job {
        jobFactory.task(taskConfig)
        return jobFactory.build()
    }
}

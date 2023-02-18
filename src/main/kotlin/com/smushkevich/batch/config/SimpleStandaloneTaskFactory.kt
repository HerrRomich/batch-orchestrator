package com.smushkevich.batch.config

import com.smushkevich.batch.Job

internal class SimpleStandaloneTaskFactory(
    jobFactory: SimpleStandaloneJobFactory,
    taskConfig: TaskConfig
) : SimpleTaskFactory<StandaloneJobFactory, StandaloneTaskFactory>(jobFactory, taskConfig),
    StandaloneTaskFactory {
    override val self: StandaloneTaskFactory
        get() = this

    override fun build(): Job {
        jobFactory.addTask(taskConfig)
        return jobFactory.build()
    }
}
package io.github.herrromich.batch.config

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.Task

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

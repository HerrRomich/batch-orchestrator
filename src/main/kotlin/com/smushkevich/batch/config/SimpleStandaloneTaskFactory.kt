package com.smushkevich.batch.config

import com.smushkevich.batch.FailLevel
import com.smushkevich.batch.Job
import com.smushkevich.batch.Task
import com.smushkevich.batch.TaskContext
import com.smushkevich.batch.dsl.DslTaskFactory

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
        jobFactory.addTask(taskConfig)
        return jobFactory.build()
    }
}

package com.smushkevich.batch.config

import com.smushkevich.batch.FailLevel
import com.smushkevich.batch.Job
import com.smushkevich.batch.Task
import com.smushkevich.batch.TaskContext
import com.smushkevich.batch.dsl.DslTaskFactory

internal class SimpleStandaloneTaskFactory(
    jobFactory: SimpleStandaloneJobFactory,
    taskConfig: TaskConfig
) : SimpleTaskFactory<StandaloneJobFactory, StandaloneTaskFactory>(jobFactory, taskConfig),
    StandaloneTaskFactory, DslTaskFactory, Task by taskConfig {
    override val self: StandaloneTaskFactory
        get() = this

    override var priority: Int
        get() = super.priority
        set(value) {
            priority(value)
        }

    override var failLevel: FailLevel
        get() = super.failLevel
        set(value) {
            failLevel(value)
        }


    override fun execute(runnable: (context: TaskContext) -> Unit) {
        runnable(runnable)
    }

    fun buildTask(): Task {
        return this.taskConfig.copy()
    }

    override fun build(): Job {
        jobFactory.addTask(taskConfig)
        return jobFactory.build()
    }
}

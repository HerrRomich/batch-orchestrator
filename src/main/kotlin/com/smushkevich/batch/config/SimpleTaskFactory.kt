package com.smushkevich.batch.config

import com.smushkevich.batch.FailLevel
import com.smushkevich.batch.Task
import com.smushkevich.batch.TaskContext

internal abstract class SimpleTaskFactory<T: JobFactory<T, P>, P: TaskFactory<T, P>>(
    protected val jobFactory: T,
    protected var taskConfig: TaskConfig
) : TaskFactory<T, P>,
    Task by taskConfig {

    abstract protected val self: P

    override fun taskName(taskName: String): P {
        taskConfig = taskConfig.copy(taskName = taskName)
        return self
    }

    override fun priority(priority: Int): P {
        taskConfig = taskConfig.copy(priority = priority)
        return self
    }

    override fun failLevel(failLevel: FailLevel): P {
        taskConfig = taskConfig.copy(failLevel = failLevel)
        return self
    }

    override fun consumables(vararg consumables: String): P {
        taskConfig = taskConfig.copy(consumables = taskConfig.consumables + consumables)
        return self
    }

    override fun producibles(vararg producibles: String): P {
        taskConfig = taskConfig.copy(providables = taskConfig.providables + producibles)
        return self
    }

    override fun runnable(runnable: (context: TaskContext) -> Unit): P {
        taskConfig = taskConfig.copy(runnable = runnable)
        return self
    }

    override fun andTask(taskName: String): P {
        jobFactory.addTask(taskConfig)
        return jobFactory.task(taskName)
    }

    override fun and() = jobFactory
}
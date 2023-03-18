package io.github.herrromich.batch.config

import io.github.herrromich.batch.Consumer
import io.github.herrromich.batch.FailLevel
import io.github.herrromich.batch.Task
import io.github.herrromich.batch.TaskContext

internal abstract class SimpleTaskFactory<J : JobFactory<J, T>, T : TaskFactory<J, T>>(
    protected val jobFactory: J,
    taskName: String
) : TaskFactory<J, T> {
    protected var taskConfig = TaskConfig(jobName = jobFactory.name, taskName = taskName)
    protected abstract val self: T

    override fun name(name: String): T {
        taskConfig = taskConfig.copy(taskName = name)
        return self
    }

    override fun priority(priority: Int): T {
        taskConfig = taskConfig.copy(priority = priority)
        return self
    }

    override fun failLevel(failLevel: FailLevel): T {
        taskConfig = taskConfig.copy(failLevel = failLevel)
        return self
    }

    override fun consumables(vararg consumables: String): T {
        taskConfig = taskConfig.copy(consumables = taskConfig.consumables + consumables)
        return self
    }

    override fun consumables(consumables: Collection<String>): T {
        taskConfig = taskConfig.copy(consumables = taskConfig.consumables + consumables)
        return self
    }

    override fun producibles(vararg producibles: String): T {
        taskConfig = taskConfig.copy(producibles = taskConfig.producibles + producibles)
        return self
    }

    override fun producibles(producibles: Collection<String>): T {
        taskConfig = taskConfig.copy(producibles = taskConfig.producibles + producibles)
        return self
    }

    override fun runnable(runnable: Consumer<TaskContext>): T {
        taskConfig = taskConfig.copy(runnable = runnable)
        return self
    }

    override fun andTask(taskName: String): T {
        jobFactory.task(taskConfig)
        return jobFactory.task(taskName)
    }

    override fun andTask(task: Task): J {
        jobFactory.task(taskConfig)
        return jobFactory.task(task)
    }

    override fun and(): J {
        jobFactory.task(taskConfig)
        return jobFactory
    }
}
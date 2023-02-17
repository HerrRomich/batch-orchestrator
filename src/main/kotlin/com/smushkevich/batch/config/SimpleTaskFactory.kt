package com.smushkevich.batch.config

import com.smushkevich.batch.FailLevel
import com.smushkevich.batch.Orchestrator
import com.smushkevich.batch.Task

internal class SimpleTaskFactory(private val jobFactory: SimpleJobFactory, private var taskConfig: TaskConfig) :
    TaskFactory, Task by taskConfig {

    override fun taskName(taskName: String): TaskFactory {
        taskConfig = taskConfig.copy(taskName = taskName)
        return this
    }

    override fun priority(priority: Int): TaskFactory {
        taskConfig = taskConfig.copy(priority = priority)
        return this
    }

    override fun failLevel(failLevel: FailLevel): TaskFactory {
        taskConfig = taskConfig.copy(failLevel = failLevel)
        return this
    }

    override fun consumable(vararg consumable: String): TaskFactory {
        taskConfig = taskConfig.copy(consumables = taskConfig.consumables + consumable)
        return this
    }

    override fun producible(vararg producible: String): TaskFactory {
        taskConfig = taskConfig.copy(providables = taskConfig.providables + producible)
        return this
    }

    override fun runnable(runnable: () -> Unit): TaskFactory {
        taskConfig = taskConfig.copy(runnable = runnable)
        return this
    }

    override fun andTask(taskName: String): TaskFactory {
        jobFactory.addTask(taskConfig)
        return jobFactory.task(taskName)
    }

    override fun andJob(jobName: String): JobFactory {
        jobFactory.addTask(taskConfig)
        return jobFactory.andJob(jobName)
    }

    override fun and(): JobFactory {
        jobFactory.addTask(taskConfig)
        return jobFactory
    }

    override fun build(): Orchestrator {
        jobFactory.addTask(taskConfig)
        return jobFactory.build()
    }
}

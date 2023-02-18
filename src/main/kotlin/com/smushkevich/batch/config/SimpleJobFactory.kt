package com.smushkevich.batch.config

import com.smushkevich.batch.Job
import com.smushkevich.batch.OrchestratorException
import com.smushkevich.batch.Task

internal abstract class SimpleJobFactory<T: JobFactory<T, P>, P: TaskFactory<T, P>>(
    protected var jobConfig: JobConfig
): JobFactory<T, P>, Job by jobConfig {
    protected abstract val self: T

    override fun jobName(jobName: String): T {
        jobConfig =
            jobConfig.copy(jobName = jobName, tasks = jobConfig.tasks.map { it.copy(jobName = jobName) }.toSet())
        return self
    }

    override fun addTask(task: Task) {
        jobConfig.tasks.firstOrNull { it.taskName == task.taskName }
            ?.let { throw OrchestratorException("Task \"${task.taskName}\" already contains in JobFactory: \"${jobConfig.jobName}\"") }
        val taskConfig = (task as? TaskConfig)?.let (TaskConfig::copy ) ?: TaskConfig(task)
        jobConfig = jobConfig.copy(tasks = jobConfig.tasks + taskConfig)
    }
}

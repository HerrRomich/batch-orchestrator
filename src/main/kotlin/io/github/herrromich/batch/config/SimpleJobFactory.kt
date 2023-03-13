package io.github.herrromich.batch.config

import io.github.herrromich.batch.OrchestratorException
import io.github.herrromich.batch.Task

internal abstract class SimpleJobFactory<J: JobFactory<J, T>, T: TaskFactory<J, T>>(
    jobName: String
): JobFactory<J, T> {
    protected var jobConfig = JobConfig(jobName = jobName)
    override val name: String
        get() = jobConfig.jobName
    protected abstract val self: J

    override fun name(name: String): J {
        jobConfig =
            jobConfig.copy(jobName = name, tasks = jobConfig.tasks.map { it.copy(jobName = name) }.toSet())
        return self
    }

    override fun task(task: Task): J {
        jobConfig.tasks.firstOrNull { it.taskName == task.taskName }
            ?.let { throw OrchestratorException("Task \"${task.taskName}\" already contains in JobFactory: \"${jobConfig.jobName}\"") }
        val taskConfig = (task as? TaskConfig)?.let (TaskConfig::copy ) ?: TaskConfig(task)
        jobConfig = jobConfig.copy(tasks = jobConfig.tasks + taskConfig)
        return self
    }
}

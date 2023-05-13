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

    override fun task(task: Task): J {
        jobConfig.tasks.firstOrNull { it.name == task.name }
            ?.let { throw OrchestratorException("Task \"${task.name}\" already contains in JobFactory: \"${jobConfig.jobName}\"") }
        val taskConfig = (task as? TaskConfig)?.let (TaskConfig::copy ) ?: TaskConfig(task)
        jobConfig = jobConfig.copy(tasks = jobConfig.tasks + taskConfig)
        return self
    }
}

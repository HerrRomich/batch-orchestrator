package io.github.herrromich.batch.builder.internal

import io.github.herrromich.batch.*
import io.github.herrromich.batch.config.JobConfig
import io.github.herrromich.batch.config.TaskConfig

abstract class BaseBuilder {
    var configuration = object : OrchestratorConfiguration {}
    var jobs: Set<JobConfig> = emptySet()
    protected lateinit var jobConfig: JobConfig
    val jobName: String
        get() = jobConfig.name
    protected lateinit var taskConfig: TaskConfig
    val taskName: String
        get() = taskConfig.name

    protected fun setJob(name: String) {
        finalizeJob()
        checkJobDuplicates(name)
        jobConfig = JobConfig(name)
    }

    protected fun setJob(job: Job) {
        finalizeJob()
        checkJobDuplicates(job.name)
        jobConfig = JobConfig(job.name, job.tasks.toSet())
    }

    private fun checkJobDuplicates(name: String) {
        jobs.firstOrNull { it.name == name }
            ?.let { throw OrchestratorException("Job \"$name\" is already contained in JobOrchestratorBuilder.") }
    }

    protected fun setTask(name: String) {
        finalizeTask()
        checkTaskDuplicates(name)
        taskConfig = TaskConfig(name)
    }

    protected fun setTask(task: Task) {
        finalizeTask()
        checkTaskDuplicates(task.name)
        taskConfig = TaskConfig(task)
    }

    private fun checkTaskDuplicates(name: String) {
        jobConfig.tasks.firstOrNull { it.name == name }
            ?.let { throw OrchestratorException("Task \"$name\" is already contained in JobBuilder: \"${jobConfig.name}\".") }
    }

    protected fun setPriority(priority: Int) {
        taskConfig = taskConfig.copy(priority = priority)
    }

    protected fun setFailLevel(failLevel: FailLevel) {
        taskConfig = taskConfig.copy(failLevel = failLevel)
    }

    protected fun setConsumables(vararg consumables: String) {
        taskConfig = taskConfig.copy(consumables = taskConfig.consumables + consumables)
    }

    protected fun setConsumables(consumables: Collection<String>) {
        taskConfig = taskConfig.copy(consumables = taskConfig.consumables + consumables)
    }

    protected fun setProducibles(vararg producibles: String) {
        taskConfig = taskConfig.copy(producibles = taskConfig.producibles + producibles)
    }

    protected fun setProducibles(producibles: Collection<String>) {
        taskConfig = taskConfig.copy(producibles = taskConfig.producibles + producibles)
    }

    protected fun setRunnable(runnable: Consumer) {
        taskConfig = taskConfig.copy(runnable = runnable)
    }

    protected fun finalizeJob() {
        if (::jobConfig.isInitialized) {
            finalizeTask()
            jobs += jobConfig
        }
    }

    protected fun finalizeTask() {
        if (::taskConfig.isInitialized) {
            jobConfig = jobConfig.copy(tasks = jobConfig.tasks + taskConfig.copy())
        }
    }
}

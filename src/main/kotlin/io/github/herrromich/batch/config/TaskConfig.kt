package io.github.herrromich.batch.config

import io.github.herrromich.batch.*

internal data class TaskConfig(
    override val jobName: String,
    override val taskName: String,
    override val priority: Int = TaskPriorities.DEFAULT,
    override val failLevel: FailLevel = FailLevel.ERROR,
    override val consumables: Set<String> = emptySet(),
    override val producibles: Set<String> = emptySet(),
    val runnable: Consumer<TaskContext> = Consumer { TODO("Task $jobName.$taskName has no runnable!") },
) : Task {
    constructor(task: Task) : this(
        jobName = task.jobName,
        taskName = task.taskName,
        priority = task.priority,
        failLevel = task.failLevel,
        consumables = task.consumables.toSet(),
        producibles = task.producibles.toSet(),
        runnable = task::execute
    )

    override fun execute(context: TaskContext) {
        runnable.accept(context)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TaskConfig

        if (jobName != other.jobName) return false
        if (taskName != other.taskName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = jobName.hashCode()
        result = 31 * result + taskName.hashCode()
        return result
    }
}


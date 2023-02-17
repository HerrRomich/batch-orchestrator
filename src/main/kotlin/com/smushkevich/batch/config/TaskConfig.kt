package com.smushkevich.batch.config

import com.smushkevich.batch.FailLevel
import com.smushkevich.batch.Task
import com.smushkevich.batch.TaskPriorities

internal data class TaskConfig(
    override val jobName: String,
    override val taskName: String,
    override val priority: Int = TaskPriorities.DEFAULT,
    override val failLevel: FailLevel = FailLevel.ERROR,
    override val consumables: Set<String> = emptySet(),
    override val providables: Set<String> = emptySet(),
    override val runnable: () -> Unit = { TODO("Task $jobName.$taskName has no runnable!") },
) : Task {
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


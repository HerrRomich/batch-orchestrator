package com.smushkevich.batch.config

import com.smushkevich.batch.FailLevel
import com.smushkevich.batch.TaskConfig
import com.smushkevich.batch.TaskPriorities

internal data class SimpleTaskConfig(
    override val jobName: String,
    override val taskName: String,
    override val priority: Int = TaskPriorities.DEFAULT,
    override val failLevel: FailLevel = FailLevel.ERROR,
    override val consumables: Set<Any> = emptySet(),
    override val providables: Set<Any> = emptySet(),
    override val runnable: () -> Unit = { TODO("Task $jobName.$taskName has no runnable!") },
) : TaskConfig {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SimpleTaskConfig

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


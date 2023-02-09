package com.smushkevich.batch.config

import com.smushkevich.batch.JobConfig

internal data class SimpleJobConfig(
    override val jobName: String,
    override var tasks: Set<SimpleTaskConfig> = emptySet()
) : JobConfig {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SimpleJobConfig

        if (jobName != other.jobName) return false

        return true
    }

    override fun hashCode(): Int {
        return jobName.hashCode()
    }
}
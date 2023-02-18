package com.smushkevich.batch.config

import com.smushkevich.batch.Job

internal data class JobConfig(
    override val jobName: String,
    override var tasks: Set<TaskConfig> = emptySet()
) : Job {
    constructor(job: Job) : this(
        jobName = job.jobName,
        tasks = job.tasks.map(::TaskConfig).toSet()
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JobConfig

        if (jobName != other.jobName) return false

        return true
    }

    override fun hashCode(): Int {
        return jobName.hashCode()
    }
}
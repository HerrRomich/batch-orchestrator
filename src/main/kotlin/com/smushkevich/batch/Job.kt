package com.smushkevich.batch

import com.smushkevich.batch.internal.JobValidator

interface Job {
    val jobName: String
    val tasks: Set<Task>

    fun validate(): Unit {
        JobValidator.checkOrphans(this)
        JobValidator.checkCycles(this)
    }

}

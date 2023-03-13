package io.github.herrromich.batch

import io.github.herrromich.batch.internal.JobValidator

interface Job {
    val jobName: String
    val tasks: Set<Task>

    fun validate() {
        JobValidator.checkOrphans(this)
        JobValidator.checkCycles(this)
    }

}

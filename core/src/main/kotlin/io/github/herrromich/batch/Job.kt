package io.github.herrromich.batch

import io.github.herrromich.batch.internal.JobValidator

interface Job {
    val jobName: String
    val tasks: Set<Task>

    fun validate() {
        val duplicates = tasks.groupBy(Task::name).filter { it.value.size > 1 }.toMap()
        if (duplicates.isNotEmpty()) {
            throw OrchestratorException("There ara tasks with duplicated names: $duplicates")
        }
        JobValidator.checkOrphans(this)
        JobValidator.checkCycles(this)
    }

}

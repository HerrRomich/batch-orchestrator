package io.github.herrromich.batch.events

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.JobExecutionState
import java.time.LocalDateTime

data class JobEvent(val job: Job, val state: JobExecutionState) : ExecutionEvent {
    override val timestamp: LocalDateTime = LocalDateTime.now()
}
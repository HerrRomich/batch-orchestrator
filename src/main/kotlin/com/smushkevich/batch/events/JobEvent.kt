package com.smushkevich.batch.events

import com.smushkevich.batch.Job
import com.smushkevich.batch.JobExecutionState
import java.time.LocalDateTime

data class JobEvent(val job: Job, val state: JobExecutionState) : ExecutionEvent {
    override val timestamp: LocalDateTime = LocalDateTime.now()
}
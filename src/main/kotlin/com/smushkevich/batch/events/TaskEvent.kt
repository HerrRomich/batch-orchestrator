package com.smushkevich.batch.events

import com.smushkevich.batch.Task
import com.smushkevich.batch.TaskExecutionState
import java.time.LocalDateTime

data class TaskEvent(
    val task: Task,
    val state: TaskExecutionState
) : ExecutionEvent {
    override val timestamp: LocalDateTime = LocalDateTime.now()
}
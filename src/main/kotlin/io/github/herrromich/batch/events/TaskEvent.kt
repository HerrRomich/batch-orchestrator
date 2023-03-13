package io.github.herrromich.batch.events

import io.github.herrromich.batch.Task
import io.github.herrromich.batch.TaskExecutionState
import java.time.LocalDateTime

data class TaskEvent(
    val task: Task,
    val state: TaskExecutionState
) : ExecutionEvent {
    override val timestamp: LocalDateTime = LocalDateTime.now()
}
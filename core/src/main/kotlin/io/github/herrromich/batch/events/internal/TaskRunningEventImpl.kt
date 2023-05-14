package io.github.herrromich.batch.events.internal

import io.github.herrromich.batch.Task
import io.github.herrromich.batch.TaskState
import io.github.herrromich.batch.events.TaskRunningEvent
import java.time.LocalDateTime

internal data class TaskRunningEventImpl(
    override val task: Task,
    override val progress: Double,
) : TaskRunningEvent {
    override val state: TaskState = TaskState.RUNNING
    override val timestamp: LocalDateTime = LocalDateTime.now()
}
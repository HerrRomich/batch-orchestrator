package io.github.herrromich.batch.events.internal

import io.github.herrromich.batch.Task
import io.github.herrromich.batch.TaskState
import io.github.herrromich.batch.events.TaskEvent
import java.time.LocalDateTime

internal data class TaskEventImpl(
    override val task: Task,
    override val state: TaskState,
) : TaskEvent {
    override val timestamp: LocalDateTime = LocalDateTime.now()
}
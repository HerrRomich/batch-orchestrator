package io.github.herrromich.batch.events

import io.github.herrromich.batch.Task
import io.github.herrromich.batch.TaskState

interface TaskEvent : ExecutionEvent {
    /**
     * A reference to the task
     */
    val task: Task

    /**
     * A state of task to which it comes during this event.
     */
    val state: TaskState
}
package io.github.herrromich.batch

import io.github.herrromich.batch.events.TaskEvent
import io.reactivex.rxjava3.core.Flowable

/**
 * Context of running task.
 */
interface TaskExecution: TaskContext {
    val taskName: String
    val qualfiedTaskName: String
    val state: TaskExecutionState
    val events: Flowable<TaskEvent>
}

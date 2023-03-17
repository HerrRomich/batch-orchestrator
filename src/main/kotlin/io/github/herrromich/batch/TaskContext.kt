package io.github.herrromich.batch

import io.github.herrromich.batch.events.TaskEvent
import io.reactivex.rxjava3.core.Flowable

/**
 * Context of running task.
 */
interface TaskContext {

    /**
     * Reference to the parent job context
     */
    val jobContext: JobContext

    /**
     * Reference to a task definition
     */
    val task: Task

    /**
     * Current task state
     */
    val state: TaskState

    /**
     * Task events with reply.
     */
    val events: Flowable<TaskEvent>

}

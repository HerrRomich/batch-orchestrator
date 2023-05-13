package io.github.herrromich.batch

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
}

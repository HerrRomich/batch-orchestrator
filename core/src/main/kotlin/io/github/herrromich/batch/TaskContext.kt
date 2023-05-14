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
     * Changes progress of executing task
     */
    fun setProgress(progress: Double)
}

package io.github.herrromich.batch

/**
 * Task definition.
 */
interface Task {
    /**
     * Name of containing job.
     */
    val jobName: String

    /**
     * Name of task.
     */
    val taskName: String

    /**
     * Priority.
     * The higher the priority, the earlier the task will be started by executor service.
     *
     */
    val priority: Int

    /**
     * Defines, what happens if the task fails.
     */
    val failLevel: FailLevel

    /**
     * List of mnemonic resources, that should be fulfilled before the task could be executed.
     */
    val consumables: Set<String>

    /**
     * List of mnemonic resources, that will be fulffilled during execution of task.
     */
    val producibles: Set<String>

    /**
     * Method that will be executed during task execution. [context] could deliver the info of task execution.
     *
     */
    fun execute(context: TaskContext)
}

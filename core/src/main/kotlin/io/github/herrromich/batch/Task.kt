package io.github.herrromich.batch

/**
 * Task definition.
 */
interface Task {
    /**
     * Name of task.
     */
    val name: String

    /**
     * Priority.
     * The higher the priority, the earlier the task will be started by executor service.
     *
     */
    val priority: Int
        get() = TaskPriorities.DEFAULT

    /**
     * Defines, what happens if the task fails.
     */
    val failLevel: FailLevel
        get() = FailLevel.DEFAULT

    /**
     * List of mnemonic resources, that should be fulfilled before the task could be executed.
     */
    val consumables: Set<String>
        get() = emptySet()

    /**
     * List of mnemonic resources, that will be fulfilled during execution of task.
     */
    val producibles: Set<String>
        get() = emptySet()

    /**
     * Method that will be executed during task execution. [context] could deliver the info of task execution.
     *
     */
    fun execute(context: TaskContext)
}

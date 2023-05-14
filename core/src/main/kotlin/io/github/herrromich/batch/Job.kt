package io.github.herrromich.batch

/**
 * Job definition.
 * Contains of tasks.
 * Tasks should build a directed acyclic graph,
 * according to its consumable and producible resources.
 */
interface Job {
    /**
     * Name of job.
     * Should be unique in orchestrator.
     */
    val name: String

    /**
     * List of tasks, building a complete job.
     */
    val tasks: Set<Task>
}

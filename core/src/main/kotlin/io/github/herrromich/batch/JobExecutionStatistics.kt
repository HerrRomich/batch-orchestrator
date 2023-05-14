package io.github.herrromich.batch

/**
 * A snpshot of statistics during job execution
 */
interface JobExecutionStatistics {
    /**
     * Set of tasks that stay submitted, not yet planned for execution.
     */
    val submittedTasks: Set<String>

    /**
     * A list of tasks with sublist of uncompleted consumable resources.
     */
    val tasksWithUncompletedConsumables: Map<String, Set<String>>

    /**
     * A list of producible resources that are not completed jet with a sublist of tasks, that should be completed to complete resource
     */
    val uncompletedProducibles: Map<String, Set<String>>

    /**
     *
     */
    val queuedTasks: Set<String>
    val fulfilledTaskCount: Int
    val runningTaskCount: Int
    val completedTaskCount: Int
    val warnTaskCount: Int
    val failedTaskCount: Int
    val fatalTaskCount: Int
    val skippedTaskCount: Int
    val canceledTaskCount: Int
}

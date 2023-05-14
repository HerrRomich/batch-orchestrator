package io.github.herrromich.batch

/**
 * Execution states of task.
 */
enum class TaskState {

    /**
     * Initial State.
     * Task is initially prepared for execution.
     */
    SUBMITTED,

    /**
     * All consumbles (resources) already fulfilled. Task can be started for execution and submitted to an executor service
     */
    FULFILLED,

    /**
     * Executor service started running the task in a dedicated thread.
     */
    RUNNING,

    /**
     * The task is failed with the [FailLevel.WARN].
     *
     * The job won't be interrupted and will be ended with state [JobState.COMPLETED]
     */
    WARN,

    /**
     * The task is failed with the [FailLevel.ERROR].
     *
     * The job won't be interrupted until all fulfilled tasks are done.
     *
     * The job will be ended with state [JobState.ERROR]
     */
    ERROR,

    /**
     * The task is failed with the [FailLevel.FATAL].
     *
     * The job will be immediately interrupted.
     *
     * All fulfilled tasks will be skipped.
     *
     * All running tasks will be immediately canceled.
     *
     * The job will be ended with state [JobState.ERROR]
     */
    FATAL,

    /**
     * The task is successfully completed.
     */
    COMPLETED,

    /**
     * The task is canceled during execution [RUNNING].
     *
     * The cancellation is forced by failed task with the [FailLevel.FATAL].
     */
    CANCELED,

    /**
     * The fulfilled [FULFILLED] or queued [SUBMITTED] task is skipped.
     *
     * The skip is forced by failed task with the [FailLevel.FATAL].
     */
    SKIPPED
}

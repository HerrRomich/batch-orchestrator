package io.github.herrromich.batch

/**
 * State of job execution
 */
enum class JobState {
    /**
     * A job is executing.
     */
    EXECUTING,

    /**
     * job is failed.
     *
     * One or more tasks are failed at least with fail level [FailLevel.ERROR]
     *
     * No tasks are failed with fail level [FailLevel.FATAL]
     */
    ERROR,

    /**
     * Job is fatally failed
     *
     * One task is failed at least with fail level [FailLevel.FATAL]
     */
    FATAL,

    /**
     * Job is completed without failures
     */
    COMPLETED,

    /**
     * job is completed with failures.
     *
     * One or more tasks are failed at least with fail level [FailLevel.WARN]
     *
     * No tasks are failed with fail level [FailLevel.ERROR] or [FailLevel.FATAL]
     */
    COMPLETED_WITH_WARNINGS,
}
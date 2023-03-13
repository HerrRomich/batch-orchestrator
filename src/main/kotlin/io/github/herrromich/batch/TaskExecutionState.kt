package io.github.herrromich.batch

enum class TaskExecutionState {
    QUEUED,
    FULFILLED,
    RUNNING,
    WARN,
    ERROR,
    FATAL,
    COMPLETED,
    CANCELED,
    SKIPPED
}

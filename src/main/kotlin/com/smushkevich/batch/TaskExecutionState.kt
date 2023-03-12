package com.smushkevich.batch

enum class TaskExecutionState {
    QUEUED,
    FULFILLED,
    RUNNING,
    WARN,
    ERROR,
    FATAL,
    COMPLETED,
    CANCELED,
    ABORTED
}

package com.smushkevich.batch

enum class JobExecutionState {
    EXECUTING,
    ERROR,
    FATAL,
    COMPLETED
}
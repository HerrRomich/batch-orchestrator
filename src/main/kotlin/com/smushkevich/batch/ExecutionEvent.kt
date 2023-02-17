package com.smushkevich.batch

import java.time.LocalDateTime

interface ExecutionEvent {
    val timestamp: LocalDateTime
    val task: Task

}

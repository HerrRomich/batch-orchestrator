package com.smushkevich.batch.events

import java.time.LocalDateTime

interface ExecutionEvent {
    val timestamp: LocalDateTime
}

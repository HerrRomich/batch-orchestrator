package io.github.herrromich.batch.events

import java.time.LocalDateTime

interface ExecutionEvent {
    val timestamp: LocalDateTime
}

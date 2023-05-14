package io.github.herrromich.batch.events

import java.time.LocalDateTime

/**
 * A common execution event.
 */
interface ExecutionEvent {
    /**
     * Timestamp of an event.
     */
    val timestamp: LocalDateTime
}

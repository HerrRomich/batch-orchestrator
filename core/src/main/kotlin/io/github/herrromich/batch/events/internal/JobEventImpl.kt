package io.github.herrromich.batch.events.internal

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.JobState
import io.github.herrromich.batch.events.JobEvent
import java.time.LocalDateTime

/**
 * A job execution event.
 */
data class JobEventImpl(
    /**
     * A reference to a job to which an event bolongs.
     */
    override val job: Job,

    /**
     * A state of job to which it comes by this event.
     */
    override val state: JobState,
) : JobEvent {
    override val timestamp: LocalDateTime = LocalDateTime.now()
}
package io.github.herrromich.batch.events

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.JobState

/**
 * A job execution event.
 */
interface JobEvent
 : ExecutionEvent {
    /**
     * A reference to a job to which an event bolongs.
     */
    val job: Job

    /**
     * A state of job to which it comes by this event.
     */
    val state: JobState
}
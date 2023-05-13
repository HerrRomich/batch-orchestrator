package io.github.herrromich.batch.internal

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.events.ExecutionEvent

internal class SimpleJobExecutionInstance(job: Job) : JobExecutionInstance(job) {
    private val events = mutableListOf<ExecutionEvent>()

    override fun nextEvent(event: ExecutionEvent) {
        events += event;
    }

    override fun lastEvent(event: ExecutionEvent) {
        events += event;
    }
}
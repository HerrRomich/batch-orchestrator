package io.github.herrromich.batch.internal

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.OrchestratorException
import io.github.herrromich.batch.events.ExecutionEvent
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException

class TestJobInstance(override val job: Job) : JobInstance() {
    private val _events = mutableListOf<ExecutionEvent>()
    val events: List<ExecutionEvent>
        get() = synchronized(this) { _events.toList() }
    private val future = CompletableFuture<Unit>()

    override fun nextEvent(event: ExecutionEvent) {
        _events += event
    }

    override fun complete() {
        future.complete(null)
    }

    override fun completeExceptionally(ex: OrchestratorException) {
        future.completeExceptionally(ex)
    }

    override fun join() {
        try {
            future.get()
        } catch (e: ExecutionException) {
            if (e.cause is OrchestratorException) {
                throw e.cause as OrchestratorException
            } else {
                throw e
            }
        }
    }
}
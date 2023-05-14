package io.github.herrromich.batch.internal

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.OrchestratorException
import io.github.herrromich.batch.events.ExecutionEvent
import mu.KotlinLogging

private val logger = KotlinLogging.logger {  }

class TestJobInstance(override val job: Job, private val events: MutableList<ExecutionEvent>) : JobInstance() {
    override fun nextEvent(event: ExecutionEvent) {
        events.add(event)
    }

    override fun complete() {
        logger.info { "Test job is completed successfully." }
    }

    override fun completeExceptionally(ex: OrchestratorException) {
        logger.info { "Test job is completed with exception." }
    }

    override fun join() {
        logger.info { "Test job is joined." }
    }
}
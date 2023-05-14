package io.github.herrromich.batch.reactive

import io.github.herrromich.batch.JobContext
import io.github.herrromich.batch.OrchestratorException
import io.github.herrromich.batch.reactive.internal.ReactiveJobInstance

object ReactiveEvents {
    /**
     *
     */
    @JvmStatic
    fun getEventsObservable(context: JobContext) =
        (context as? ReactiveJobInstance)?.run { events } ?: throw OrchestratorException("Context is not reactive.")
}
package io.github.herrromich.batch.reactive.internal

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.OrchestratorException
import io.github.herrromich.batch.events.ExecutionEvent
import io.github.herrromich.batch.internal.JobInstance
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.ReplaySubject
import java.util.concurrent.CompletableFuture

internal class ReactiveJobInstance(override val job: Job) : JobInstance() {
    private val _events = ReplaySubject.create<ExecutionEvent>()
    val events: Observable<ExecutionEvent>
        get() = _events
    private val future = CompletableFuture<Unit>()

    override fun nextEvent(event: ExecutionEvent) {
        _events.onNext(event)
    }

    override fun complete() {
        _events.onComplete()
        future.complete(null)
    }

    override fun completeExceptionally(ex: OrchestratorException) {
        _events.onComplete()
        future.completeExceptionally(ex)
    }

    override fun join() {
        future.get()
    }
}

package com.smushkevich.batch.internal

import com.smushkevich.batch.ExecutionEvent
import com.smushkevich.batch.Job
import com.smushkevich.batch.JobExecution
import io.reactivex.rxjava3.subjects.ReplaySubject
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

internal class ExecutionInstance(override val job: Job, private val future: CompletableFuture<Unit>) : JobExecution,
    Future<Unit> by future {

    override val id: UUID = UUID.randomUUID()

    override val events = ReplaySubject.create<ExecutionEvent>()

    fun postEvent(event: ExecutionEvent) {
        events.onNext(event)
    }

    fun complete() {
        events
        events.onComplete()
        future.complete(null)
    }
}

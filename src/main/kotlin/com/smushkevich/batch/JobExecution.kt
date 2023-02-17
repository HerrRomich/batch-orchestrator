package com.smushkevich.batch

import io.reactivex.rxjava3.core.Observable
import java.util.*
import java.util.concurrent.Future

interface JobExecution: Future<Unit> {
    val id: UUID
    val job: Job
    val events: Observable<ExecutionEvent>
}

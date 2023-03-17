package io.github.herrromich.batch

import io.github.herrromich.batch.events.ExecutionEvent
import io.reactivex.rxjava3.core.Flowable
import java.util.*
import java.util.concurrent.Future

/**
 * Context of running job.
 */
interface JobContext: Future<Void> {
    /**
     * A unique identifier of job execution.
     */
    val id: UUID

    /**
     * Reference to the job definition.
     */
    val job: Job

    /**
     * An observable to all job events. Each subscriber will get all historical events replayed.
     */
    val events: Flowable<ExecutionEvent>
}

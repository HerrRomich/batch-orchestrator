package com.smushkevich.batch

import com.smushkevich.batch.events.ExecutionEvent
import io.reactivex.rxjava3.core.Flowable
import java.util.concurrent.Future

/**
 * A context of the job execution.
 */
interface JobExecution: JobContext, Future<Void> {
    /**
     * Name of job.
     */
    val jobName: String
    /**
     * An observable to all job events. Each subscriber will get all historical events replayed.
     */
    val events: Flowable<ExecutionEvent>
}

package io.github.herrromich.batch

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
}

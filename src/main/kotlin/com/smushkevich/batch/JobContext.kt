package com.smushkevich.batch

import java.util.*

/**
 * Context of running job.
 */
interface JobContext {
    /**
     * A unique identifier of job execution.
     */
    val id: UUID

    /**
     * Reference to the job definition.
     */
    val job: Job

}

package com.smushkevich.batch.internal

import com.smushkevich.batch.JobConfig
import com.smushkevich.batch.JobExecution
import com.smushkevich.batch.Orchestrator

internal class SimpleOrchestrator(jobs: Set<JobConfig>) : Orchestrator {

    override val jobs: Map<String, JobConfig>

    init {
        this.jobs = jobs.map { it.jobName to it }.toMap()
    }

    override fun execute(jobName: String): JobExecution {
        TODO("Not yet implemented")
    }

}
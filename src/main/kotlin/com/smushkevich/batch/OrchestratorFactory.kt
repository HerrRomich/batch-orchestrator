package com.smushkevich.batch

import com.smushkevich.batch.config.OrchestratorJobFactory
import com.smushkevich.batch.internal.SimpleOrchestratorFactory

interface OrchestratorFactory {
    val jobs: Set<Job>
    fun job(jobName: String): OrchestratorJobFactory
    fun addJob(job: Job)
    fun build(): Orchestrator

    companion object {
        @JvmStatic
        fun instance(): OrchestratorFactory = SimpleOrchestratorFactory()
    }

}

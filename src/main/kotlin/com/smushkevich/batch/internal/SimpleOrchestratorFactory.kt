package com.smushkevich.batch.internal

import com.smushkevich.batch.Orchestrator
import com.smushkevich.batch.OrchestratorFactory
import com.smushkevich.batch.config.JobConfig
import com.smushkevich.batch.config.SimpleOrchestratorJobFactory

internal class SimpleOrchestratorFactory : OrchestratorFactory {
    override var jobs: Set<JobConfig> = emptySet()

    override fun job(jobName: String) = SimpleOrchestratorJobFactory(this, JobConfig(jobName = jobName))

    override fun build(): Orchestrator = SimpleOrchestrator(jobs)

    fun addJob(jobConfig: JobConfig) {
        jobs = jobs +jobConfig
    }

}

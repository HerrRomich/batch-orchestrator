package com.smushkevich.batch.internal

import com.smushkevich.batch.Job
import com.smushkevich.batch.Orchestrator
import com.smushkevich.batch.OrchestratorFactory
import com.smushkevich.batch.config.JobConfig
import com.smushkevich.batch.config.SimpleOrchestratorJobFactory

internal class SimpleOrchestratorFactory : OrchestratorFactory {
    override var threadPoolSize = 10
    override var jobs: Set<JobConfig> = emptySet()

    override fun thredPoolSize(threadPoolSize: Int): OrchestratorFactory {
        this.threadPoolSize = threadPoolSize
        return this
    }

    override fun job(jobName: String) = SimpleOrchestratorJobFactory(this, jobName)

    override fun build(): Orchestrator = SimpleOrchestrator(threadPoolSize, jobs,)

    fun addJob(job: Job) {
        val jobConfig = (job as? JobConfig)?.let (JobConfig::copy) ?: JobConfig(job)
        jobs = jobs +jobConfig
    }

}

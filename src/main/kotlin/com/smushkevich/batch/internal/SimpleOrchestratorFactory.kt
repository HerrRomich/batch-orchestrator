package com.smushkevich.batch.internal

import com.smushkevich.batch.Job
import com.smushkevich.batch.Orchestrator
import com.smushkevich.batch.OrchestratorFactory
import com.smushkevich.batch.config.JobConfig
import com.smushkevich.batch.config.SimpleOrchestratorJobFactory
import com.smushkevich.batch.config.SimpleStandaloneJobFactory
import com.smushkevich.batch.dsl.DSLOrchestratorFactory
import com.smushkevich.batch.dsl.DslJobFactory

internal class SimpleOrchestratorFactory : OrchestratorFactory, DSLOrchestratorFactory {
    override var jobs: Set<JobConfig> = emptySet()

    override fun job(jobName: String) = SimpleOrchestratorJobFactory(this, JobConfig(jobName = jobName))

    override fun job(jobName: String, init: DslJobFactory.() -> Unit) {
        val factory = SimpleStandaloneJobFactory(JobConfig(jobName))
        factory.init()
        addJob(factory.build())
    }

    override fun build(): Orchestrator = SimpleOrchestrator(jobs)

    override fun addJob(job: Job) {
        val jobConfig = (job as? JobConfig)?.let (JobConfig::copy) ?: JobConfig(job)
        jobs = jobs +jobConfig
    }

}

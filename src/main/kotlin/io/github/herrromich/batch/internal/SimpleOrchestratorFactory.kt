package io.github.herrromich.batch.internal

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.Orchestrator
import io.github.herrromich.batch.OrchestratorFactory
import io.github.herrromich.batch.config.JobConfig
import io.github.herrromich.batch.config.SimpleOrchestratorJobFactory

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

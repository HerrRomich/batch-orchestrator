package io.github.herrromich.batch.internal

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.Orchestrator
import io.github.herrromich.batch.OrchestratorFactory
import io.github.herrromich.batch.config.JobConfig
import io.github.herrromich.batch.config.SimpleOrchestratorJobFactory
import io.github.herrromich.batch.spi.ExecutorConfiguration
import io.github.herrromich.batch.spi.ExecutorProvider

internal class SimpleOrchestratorFactory : OrchestratorFactory {
    override var configuration = object : ExecutorConfiguration {}
    override var jobs: Set<JobConfig> = emptySet()

    override fun configuration(configuration: ExecutorConfiguration): OrchestratorFactory {
        this.configuration = configuration
        return this
    }

    override fun job(jobName: String) = SimpleOrchestratorJobFactory(this, jobName)

    override fun build(): Orchestrator = Orchestrator.instance(ExecutorProvider.provide(configuration), jobs)

    override fun job(job: Job): SimpleOrchestratorFactory {
        val jobConfig = (job as? JobConfig)?.let(JobConfig::copy) ?: JobConfig(job)
        jobs = jobs + jobConfig
        return this
    }

}

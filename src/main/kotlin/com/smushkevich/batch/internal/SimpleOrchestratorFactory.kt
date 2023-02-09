package com.smushkevich.batch.internal

import com.smushkevich.batch.Orchestrator
import com.smushkevich.batch.OrchestratorFactory
import com.smushkevich.batch.config.SimpleJobConfig
import com.smushkevich.batch.config.SimpleJobFactory

internal class SimpleOrchestratorFactory : OrchestratorFactory {
    override var jobs: Set<SimpleJobConfig> = emptySet()

    override fun job(jobName: String) = SimpleJobFactory(this, SimpleJobConfig(jobName = jobName))

    override fun build(): Orchestrator = SimpleOrchestrator(jobs)

    fun addJob(jobConfig: SimpleJobConfig) {
        jobs = jobs +jobConfig
    }

}

package com.smushkevich.batch

import com.smushkevich.batch.config.JobFactory
import com.smushkevich.batch.internal.SimpleOrchestratorFactory

interface OrchestratorFactory {

    val jobs: Set<Job>

    fun job(jobName: String): JobFactory

    fun build(): Orchestrator

    companion object {
        @JvmStatic
        fun instancs(): OrchestratorFactory = SimpleOrchestratorFactory()
    }

}
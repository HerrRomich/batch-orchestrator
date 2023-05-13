package io.github.herrromich.batch.spi

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.Orchestrator
import io.github.herrromich.batch.OrchestratorException
import io.github.herrromich.batch.internal.SimpleOrchestrator
import mu.KotlinLogging
import java.util.*
import java.util.concurrent.Executor

private val logger = KotlinLogging.logger { }

interface OrchestratorProvider {

    fun provide(executor: Executor, jobs: Set<Job>): Orchestrator

    companion object {
        private val ORCHESTRATOR_SERVICE_LOADER = ServiceLoader.load(OrchestratorProvider::class.java).apply {
            reload()
            if (count() > 1) {
                throw OrchestratorException("There are more then one registered ${OrchestratorProvider::class.java} classes.")
            }
        }

        fun provide(executor: Executor, jobs: Set<Job>): Orchestrator {
            val orchestratorProvider = ORCHESTRATOR_SERVICE_LOADER.singleOrNull();
            val orchestrator = orchestratorProvider?.provide(executor, jobs) ?: SimpleOrchestrator(executor, jobs)
            logger.info { "Orchestrator is initialized." }
            logger.debug { "Orchestrator is initialized: $executor" }
            return orchestrator
        }
    }
}
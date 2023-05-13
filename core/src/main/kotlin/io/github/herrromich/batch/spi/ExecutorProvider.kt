package io.github.herrromich.batch.spi

import io.github.herrromich.batch.OrchestratorException
import mu.KotlinLogging
import java.util.*
import java.util.concurrent.Executor

private val logger = KotlinLogging.logger { }

interface ExecutorProvider {

    fun provide(configuration: ExecutorConfiguration): Executor

    companion object {
        private val EXECUTOR_SERVICE_LOADER = ServiceLoader.load(ExecutorProvider::class.java).apply {
            reload()
            if (count() > 1) {
                throw OrchestratorException("There are more then one registered ${ExecutorProvider::class.java} classes.")
            }
        }

        fun provide(configuration: ExecutorConfiguration): Executor {
            val executorProvider =
                EXECUTOR_SERVICE_LOADER.singleOrNull() ?: throw OrchestratorException("No executor provider loaded!");
            val executor = executorProvider.provide(configuration)
            logger.info { "Task executor is initialized." }
            logger.debug { "Task executor is initialized: $executor" }
            return executor
        }
    }
}
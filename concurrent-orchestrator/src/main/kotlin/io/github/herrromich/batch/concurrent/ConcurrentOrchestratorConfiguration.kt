package io.github.herrromich.batch.concurrent

import io.github.herrromich.batch.OrchestratorConfiguration

data class ConcurrentOrchestratorConfiguration(val threadPoolSize: Int = getDefaultThreadPoolSize()) :
    OrchestratorConfiguration {
    companion object {
        const val THREAD_POOL_SIZE = "batch.concurrentThreadPoolSize"

        private fun getDefaultThreadPoolSize() = (System.getProperty(THREAD_POOL_SIZE)?.run { toIntOrNull() }
            ?: 10)
    }
}
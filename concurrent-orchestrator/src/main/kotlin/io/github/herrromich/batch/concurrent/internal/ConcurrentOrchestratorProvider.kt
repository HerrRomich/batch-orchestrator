package io.github.herrromich.batch.concurrent.internal

import io.github.herrromich.batch.*
import io.github.herrromich.batch.concurrent.ConcurrentOrchestratorConfiguration
import java.util.concurrent.Executor
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

internal class ConcurrentOrchestratorProvider : OrchestratorProvider {
    override fun provideOrchestrator(jobs: Set<Job>, configuration: OrchestratorConfiguration) =
        (configuration as? ConcurrentOrchestratorConfiguration)?.let {
            val executor: Executor by lazy {
                ThreadPoolExecutor(
                    configuration.threadPoolSize,
                    configuration.threadPoolSize,
                    1,
                    TimeUnit.MINUTES,
                    PriorityBlockingQueue(16)
                )
            }
            ConcurrentOrchestratorImpl(jobs, executor) }
            ?: throw OrchestratorException(
                """Configuration of wrong type:
      expected: ${ConcurrentOrchestratorConfiguration::class.java.name}
         found: ${configuration::class.java.name}
            """.trimIndent()
            )
}
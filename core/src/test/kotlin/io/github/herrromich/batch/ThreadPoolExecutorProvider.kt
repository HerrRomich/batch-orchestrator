package io.github.herrromich.batch

import io.github.herrromich.batch.spi.ExecutorConfiguration
import io.github.herrromich.batch.spi.ExecutorProvider
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

internal object ThreadPoolExecutorProvider : ExecutorProvider {
    override fun provide(configuration: ExecutorConfiguration): ThreadPoolExecutor {
        val threadPoolSize = (configuration as? ThreadPoolExecutorConfiguration)?.threadPoolSize ?: 10;
        return ThreadPoolExecutor(
            threadPoolSize,
            threadPoolSize,
            1,
            TimeUnit.MINUTES,
            PriorityBlockingQueue(16)
        )
    }
}
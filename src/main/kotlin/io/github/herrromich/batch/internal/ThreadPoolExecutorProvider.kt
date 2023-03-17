package io.github.herrromich.batch.internal

import io.github.herrromich.batch.spi.ExecutorProvider
import java.util.concurrent.*

internal object ThreadPoolExecutorProvider : ExecutorProvider {
    override fun provide(threadPoolSize: Int, workQueue: BlockingQueue<Runnable>) =
        ThreadPoolExecutor(
            threadPoolSize,
            threadPoolSize,
            0,
            TimeUnit.SECONDS,
            workQueue
        )
}
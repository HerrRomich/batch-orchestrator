package io.github.herrromich.batch.spi

import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executor

interface ExecutorProvider {

    fun provide(threadPoolSize: Int, workQueue: BlockingQueue<Runnable>): Executor

}
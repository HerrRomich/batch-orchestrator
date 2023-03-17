package io.github.herrromich.batch;

import io.github.herrromich.batch.spi.ExecutorProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

public class TestExecutorProvider implements ExecutorProvider {
    @NotNull
    @Override
    public Executor provide(int threadPoolSize, @NotNull BlockingQueue<Runnable> workQueue) {
        return new ThreadPoolExecutor(
                threadPoolSize,
                threadPoolSize,
                0,
                TimeUnit.SECONDS,
                workQueue
        );
    }
}

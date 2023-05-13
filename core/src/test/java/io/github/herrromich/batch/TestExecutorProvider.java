package io.github.herrromich.batch;

import io.github.herrromich.batch.spi.ExecutorConfiguration;
import io.github.herrromich.batch.spi.ExecutorProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestExecutorProvider implements ExecutorProvider {
    @NotNull
    @Override
    public Executor provide(@NotNull ExecutorConfiguration configuration) {
        var threadPoolSize = 10;
        if (configuration instanceof ThreadPoolExecutorConfiguration) {
            threadPoolSize = ((ThreadPoolExecutorConfiguration) configuration).getThreadPoolSize();
        }
        return new ThreadPoolExecutor(
                threadPoolSize,
                threadPoolSize,
                0,
                TimeUnit.SECONDS,
                new PriorityBlockingQueue(16)
        );
    }
}

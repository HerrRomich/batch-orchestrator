package io.github.herrromich.batch.internal;

import io.github.herrromich.batch.Job;
import io.github.herrromich.batch.Task;
import io.github.herrromich.batch.TestExecutorProvider;
import io.github.herrromich.batch.ThreadPoolExecutorConfiguration;
import io.github.herrromich.batch.job.*;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.ExecutionException;

public class TestJobOrchestrator {

    @Test
    void testJobOrchestrator() throws ExecutionException, InterruptedException {
        final var configuration = new ThreadPoolExecutorConfiguration(10);
        final var executor = new TestExecutorProvider().provide(configuration);
        final Set<Task> tasks = Set.of(new TestTask1(), new TestTask2(), new TestTask3(), new TestTask4(), new TestTask5(), new TestTask6());
        Set<? extends Job> jobs = Set.of(new TestJob(tasks));
        @SuppressWarnings("KotlinInternalInJava") final var orchestrator = new SimpleOrchestrator(executor, jobs);
        orchestrator.execute(TestJob.TEST_JOB).get();
    }
}

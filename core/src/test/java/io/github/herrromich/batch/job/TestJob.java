package io.github.herrromich.batch.job;

import io.github.herrromich.batch.Job;
import io.github.herrromich.batch.Task;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class TestJob implements Job {

    public static final String TEST_JOB = "test-job";
    private final Set<Task> tasks;

    public TestJob(Set<Task> tasks) {
        this.tasks = tasks;
    }

    @NotNull
    @Override
    public String getJobName() {
        return TEST_JOB;
    }

    @NotNull
    @Override
    public Set<Task> getTasks() {
        return tasks;
    }
}

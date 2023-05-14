package io.github.herrromich.batch.job;

import io.github.herrromich.batch.FailLevel;
import io.github.herrromich.batch.TaskPriorities;
import org.jetbrains.annotations.NotNull;

public class TestTask6 extends TestTask {
    @NotNull
    @Override
    public String getName() {
        return "test-task-6";
    }

    @Override
    public int getPriority() {
        return TaskPriorities.HIGHER;
    }

    @NotNull
    @Override
    public FailLevel getFailLevel() {
        return FailLevel.FATAL;
    }
}

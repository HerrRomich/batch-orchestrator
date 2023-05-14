package io.github.herrromich.batch.job;

import io.github.herrromich.batch.TaskPriorities;
import org.jetbrains.annotations.NotNull;

public class TestTask5 extends TestTask {
    @NotNull
    @Override
    public String getName() {
        return "test-task-5";
    }

    @Override
    public int getPriority() {
        return TaskPriorities.HIGHER;
    }
}

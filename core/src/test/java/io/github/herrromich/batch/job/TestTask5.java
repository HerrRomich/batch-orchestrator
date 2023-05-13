package io.github.herrromich.batch.job;

import io.github.herrromich.batch.Task;
import io.github.herrromich.batch.TaskContext;
import io.github.herrromich.batch.TaskPriorities;
import org.jetbrains.annotations.NotNull;

public class TestTask5 implements Task {
    @NotNull
    @Override
    public String getName() {
        return "test-task-5";
    }

    @Override
    public int getPriority() {
        return TaskPriorities.HIGHER;
    }

    @Override
    public void execute(@NotNull TaskContext context) {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

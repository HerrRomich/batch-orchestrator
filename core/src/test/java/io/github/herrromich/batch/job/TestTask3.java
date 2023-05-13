package io.github.herrromich.batch.job;

import io.github.herrromich.batch.Task;
import io.github.herrromich.batch.TaskContext;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class TestTask3 implements Task {
    @NotNull
    @Override
    public String getName() {
        return "test-task-3";
    }

    @NotNull
    @Override
    public Set<String> getConsumables() {
        return Set.of("test-resource-1", "test-resource-2");
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

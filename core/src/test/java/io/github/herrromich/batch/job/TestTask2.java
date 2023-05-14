package io.github.herrromich.batch.job;

import io.github.herrromich.batch.TaskPriorities;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class TestTask2 extends TestTask {
    @NotNull
    @Override
    public String getName() {
        return "test-task-2";
    }

    @Override
    public int getPriority() {
        return TaskPriorities.HIGHER;
    }

    @NotNull
    @Override
    public Set<String> getProducibles() {
        return Set.of("test-resource-1");
    }
}

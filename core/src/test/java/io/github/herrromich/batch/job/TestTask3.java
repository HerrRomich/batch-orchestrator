package io.github.herrromich.batch.job;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class TestTask3 extends TestTask {
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
}

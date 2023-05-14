package io.github.herrromich.batch.job;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class TestTask4 extends TestTask {
    @NotNull
    @Override
    public String getName() {
        return "test-task-4";
    }

    @NotNull
    @Override
    public Set<String> getConsumables() {
        return Set.of("test-resource-1");
    }

    @NotNull
    @Override
    public Set<String> getProducibles() {
        return Set.of("test-resource-2");
    }
}

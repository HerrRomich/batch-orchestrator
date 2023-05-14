package io.github.herrromich.batch.job;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class TestTask1 extends TestTask {
    @NotNull
    @Override
    public String getName() {
        return "test-task-1";
    }

    @NotNull
    @Override
    public Set<String> getProducibles() {
        return Set.of("test-resource-1");
    }
}

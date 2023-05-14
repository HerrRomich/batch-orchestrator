package io.github.herrromich.batch.job;

import io.github.herrromich.batch.FailLevel;
import io.github.herrromich.batch.Task;
import io.github.herrromich.batch.TaskContext;
import io.github.herrromich.batch.TaskPriorities;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public abstract class TestTask implements Task {
    @Override
    public int getPriority() {
        return TaskPriorities.DEFAULT();
    }

    @NotNull
    @Override
    public FailLevel getFailLevel() {
        return FailLevel.DEFAULT();
    }

    @NotNull
    @Override
    public Set<String> getConsumables() {
        return Set.of();
    }

    @NotNull
    @Override
    public Set<String> getProducibles() {
        return Set.of();
    }

    @Override
    public void execute(@NotNull TaskContext context) {
        
    }
}

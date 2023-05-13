package io.github.herrromich.batch;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

public class JobOrchestratorTest {

    @Test
    void test() throws ExecutionException, InterruptedException, OrchestratorException {
        final var orchestrator = OrchestratorFactory.instance()
                .job("test")
                .task("test-task-1")
                .producibles("test-resource-1")
                .runnable(taskExecution -> {
                    Thread.sleep(1000);
                })
                .and()
                .task("test-task-8")
                .priority(TaskPriorities.HIGHER)
                .runnable(taskExecution -> Thread.sleep(200))
                .andTask("test-task-9")
                .priority(TaskPriorities.HIGHER)
                .producibles("test-resource-1")
                .failLevel(FailLevel.ERROR)
                .runnable(taskExecution -> Thread.sleep(100))
                .andTask("test-task-2")
                .producibles("test-resource-1")
                .priority(TaskPriorities.HIGHER)
                .runnable(taskExecution -> Thread.sleep(500))
                .andTask("test-task-3")
                .consumables("test-resource-1")
                .consumables("test-resource-2")
                .runnable(taskExecution -> Thread.sleep(200))
                .andTask("test-task-4")
                .consumables("test-resource-1")
                .producibles("test-resource-2")
                .runnable(taskExecution -> Thread.sleep(200))
                .build();
        JobContext jobContext = orchestrator.execute("test");
        jobContext.get();
    }
}

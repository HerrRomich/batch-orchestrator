package com.smushkevich.batch;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

public class JobOrchestratorTest {

    @Test
    void test() throws ExecutionException, InterruptedException {
        final var orchestrator = OrchestratorFactory.instance()
                .job("test")
                .task("test-task-1")
                .producibles("test-resource-1")
                .runnable(taskExecution -> Thread.sleep(1000))
                .andTask("test-task-8")
                .priority(TaskPriorities.HIGHER)
                .runnable(taskExecution -> Thread.sleep(200))
                .andTask("test-task-9")
                .priority(TaskPriorities.HIGHER)
                .runnable(taskExecution -> Thread.sleep(1200))
                .andTask("test-task-2")
                .producibles("test-resource-1")
                .priority(TaskPriorities.HIGHER)
                .runnable(taskExecution -> Thread.sleep(500))
                .andTask("test-task-3")
                .consumables("test-resource-1", "test-resource-2")
                .runnable(taskExecution -> Thread.sleep(200))
                .andTask("test-task-4")
                .consumables("test-resource-1")
                .producibles("test-resource-2")
                .runnable(taskExecution -> Thread.sleep(200))
                .build();
        JobExecution jobExecution = orchestrator.execute("test");
        jobExecution.getEvents().subscribe(event -> System.out.println(event.getTimestamp()));
        jobExecution.get();
    }
}

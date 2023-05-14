package io.github.herrromich.batch.concurrent.fluent;

import io.github.herrromich.batch.Orchestrator;
import io.github.herrromich.batch.OrchestratorConfiguration;
import io.github.herrromich.batch.Task;
import io.github.herrromich.batch.TaskPriorities;
import io.github.herrromich.batch.builder.fluenrt.orchestrator.FluentOrchestratorBuilder;
import io.github.herrromich.batch.builder.nested.orchestrator.NestedOrchestratorBuilder;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.fail;

public class TestBuilder {
    @Test
    void testFluentOrchestratorBuilder() {
        Orchestrator orchestrator = FluentOrchestratorBuilder.instance()
                .configuration(new OrchestratorConfiguration() {
                })
                .job("test-job")
                .task("test-task1")
                .runnable(context -> {
                })
                .consumables("test-resource1")
                .andTask("test-task2")
                .producibles("test-resource1", "test-resource2")
                .runnable(context -> {
                })
                .andTask("test-task3")
                .producibles("test-resource1")
                .consumables("test-resource2")
                .runnable(context -> {
                })
                .build();
        assertAll(() -> assertThat(orchestrator.getJobs()).hasSize(1),
                () -> {
                    var job = orchestrator.getJobs().computeIfAbsent("test-job", jobName -> fail("Job: \"%1$s\" is not found in orchestrator.".formatted(jobName)));
                    var tasks = job.getTasks().stream().collect(Collectors.toMap(Task::getName, Function.identity()));
                    assertAll(() -> {
                        var task1 = tasks.computeIfAbsent("test-task1", taskName -> fail("Cannot find task: \"%1$s\" is not found".formatted(taskName)));
                        assertAll(() -> assertThat(task1.getConsumables()).isEqualTo(Set.of("test-resource1")),
                                () -> assertThat(task1.getProducibles()).isEqualTo(Set.of()),
                                () -> assertThat(task1.getPriority()).isEqualTo(TaskPriorities.DEFAULT())
                        );
                    });
                });
    }

    @Test
    void testNestedOrchestratorBuilder() {
        Orchestrator orchestrator = NestedOrchestratorBuilder.instance()
                .configuration(new OrchestratorConfiguration() {
                })
                .job("test-job", jobBuilder ->
                        jobBuilder.task("test-task1", taskBuilder -> taskBuilder
                                        .runnable(context -> {
                                        })
                                        .consumables("test-resource1"))
                                .andTask("test-task2", taskBuilder -> taskBuilder
                                        .producibles("test-resource1", "test-resource2")
                                        .runnable(context -> {
                                        }))
                                .andTask("test-task3", taskBuilder -> taskBuilder
                                        .producibles("test-resource1")
                                        .consumables("test-resource2")
                                        .runnable(context -> {
                                        })))
                .build();
        assertAll(() -> assertThat(orchestrator.getJobs()).hasSize(1),
                () -> {
                    var job = orchestrator.getJobs().computeIfAbsent("test-job", jobName -> fail("Job: \"%1$s\" is not found in orchestrator.".formatted(jobName)));
                    var tasks = job.getTasks().stream().collect(Collectors.toMap(Task::getName, Function.identity()));
                    assertAll(() -> {
                        var task1 = tasks.computeIfAbsent("test-task1", taskName -> fail("Cannot find task: \"%1$s\" is not found".formatted(taskName)));
                        assertAll(() -> assertThat(task1.getConsumables()).isEqualTo(Set.of("test-resource1")),
                                () -> assertThat(task1.getProducibles()).isEqualTo(Set.of()),
                                () -> assertThat(task1.getPriority()).isEqualTo(TaskPriorities.DEFAULT())
                        );
                    });
                });
    }
}

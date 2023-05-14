package io.github.herrromich.batch.fluent.dsl

import io.github.herrromich.batch.FailLevel
import io.github.herrromich.batch.TaskPriorities
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DSLOrchestratorBuilderTest {
    @Test
    fun test() {
        val orchestrator = testOrchestrator {
             job("dsl-test-job1") {
                task("dsl-test-task1"){
                    priority(TaskPriorities.HIGHEST)
                    failLevel(FailLevel.WARN)
                    consumables("input-resource1", "input-resource2")
                }
                task("dsl-test-task2"){
                    priority(TaskPriorities.LOWEST)
                    failLevel(FailLevel.FATAL)
                    producibles("input-resource1", "input-resource2")
                }
            }
            job("dsl-test-job2") {
                task("dsl-test-task3") {}
                task("dsl-test-task4") {}
            }
            job("dsl-test-job3")
        }
        val testJob1 = orchestrator.jobs.getValue("dsl-test-job1")
        val testTask1 = testJob1.tasks.first { it.name == "dsl-test-task1" }
        Assertions.assertAll(
            { assertThat(orchestrator.jobs).hasSize(3) },
            { assertThat(testJob1.name).isEqualTo("dsl-test-job1") },
            { assertThat(testJob1.tasks).hasSize(2) },
            { assertThat(testTask1.name).isEqualTo("dsl-test-task1") },
        )
    }

    @Test
    fun testJobBuilder() {
        val job = testJob("test-job") {
            task("test-task1")
            task("test-task2")
        }
        assertThat(job.tasks).hasSize(2)
    }
}

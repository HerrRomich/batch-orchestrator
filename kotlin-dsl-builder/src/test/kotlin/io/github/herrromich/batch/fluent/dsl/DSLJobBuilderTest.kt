package io.github.herrromich.batch.fluent.dsl

import io.github.herrromich.batch.FailLevel
import io.github.herrromich.batch.TaskPriorities
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DSLJobBuilderTest {
    @Test
    fun test() {
        val job = testJob("dsl-test-job1") {
            task("dsl-test-task1") {
                priority(TaskPriorities.HIGHEST)
                failLevel(FailLevel.WARN)
                consumables("input-resource1", "input-resource2")
            }
            task("dsl-test-task2") {
                priority(TaskPriorities.LOWEST)
                failLevel(FailLevel.FATAL)
                producibles("input-resource1", "input-resource2")
            }
        }
        val task = job.tasks.first { it.name == "dsl-test-task2" }
        Assertions.assertAll(
            { assertThat(job.tasks).hasSize(2) },
            { assertThat(task.name).isEqualTo("dsl-test-task2") },
            { assertThat(task.priority).isEqualTo(10) },
            { assertThat(task.failLevel).isEqualTo(FailLevel.FATAL) },
            { assertThat(task.producibles).hasSize(2) },
        )
    }
}
package io.github.herrromich.batch.fluent.standalone

import io.github.herrromich.batch.FailLevel
import io.github.herrromich.batch.TaskPriorities
import io.github.herrromich.batch.builder.fluenrt.standalone.FluentStandaloneTaskBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class FluentStandaloneTaskBuilderTest {

    @Test
    fun build() {
        val taskBuilder = FluentStandaloneTaskBuilder.instance("test-task5")
            .priority(TaskPriorities.LOW)
            .failLevel(FailLevel.WARN)
            .consumables("input-resource1", "input-resource2")
            .consumables(setOf("input-resource3", "input-resource4"))
            .producibles("output-resource1", "output-resource2")
            .producibles(setOf("output-resource3", "output-resource4"))
        val task = taskBuilder.build()
        assertAll(
            { assertThat(taskBuilder.taskName).isEqualTo("test-task5") },
            { assertThat(task.priority).isEqualTo(50) },
            { assertThat(task.failLevel).isEqualTo(FailLevel.WARN) },
            { assertThat(task.consumables).hasSize(4) },
            { assertThat(task.producibles).hasSize(4) }
        )
    }
}
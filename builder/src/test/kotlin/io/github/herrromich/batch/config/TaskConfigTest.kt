package io.github.herrromich.batch.config

import io.github.herrromich.batch.Task
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class TaskConfigTest {
    @Test
    fun `test task configs are equal if names are the same`() {
        val testTaskConfig1 = TaskConfig("test-task1")
        val testTaskConfig2 = TaskConfig("test-task1")
        assertThat(testTaskConfig1).isEqualTo(testTaskConfig2)
    }

    @Test
    fun `test task configs are equal if object is the same`() {
        val testTaskConfig1 = TaskConfig("test-task1")
        assertThat(testTaskConfig1).isEqualTo(testTaskConfig1)
    }

    @Test
    fun `test task configs are not equal to different class`() {
        val testTaskConfig1 = TaskConfig("test-task1")
        assertThat(testTaskConfig1.equals("")).isFalse()
    }

    @Test
    fun `test task configs are not equal to null`() {
        val testTaskConfig1 = TaskConfig("test-task1")
        assertThat(testTaskConfig1.equals(null)).isFalse()
    }

    @Test
    fun `test task configs are not equal if names are not the same`() {
        val testTaskConfig1 = TaskConfig("test-task1")
        val testTaskConfig2 = TaskConfig("test-task2")
        assertThat(testTaskConfig1).isNotEqualTo(testTaskConfig2)
    }

companion object {
    fun assertTaskEqualsDeeply(
        actual: Task,
        testTask1: Task
    ) {
        assertAll(
            { assertThat(actual.name).isEqualTo(testTask1.name) },
            { assertThat(actual.failLevel).isEqualTo(testTask1.failLevel) },
            { assertThat(actual.priority).isEqualTo(testTask1.priority) },
            { assertThat(actual.consumables).isEqualTo(testTask1.consumables) },
            { assertThat(actual.producibles).isEqualTo(testTask1.producibles) },
        )
    }


}
}
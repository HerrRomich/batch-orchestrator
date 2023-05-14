package io.github.herrromich.batch

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TaskTest {

    @Test
    fun `test globaltask fail level should be ERROR`() {
        System.clearProperty("defaultTaskFailLevel")

        val testTask = createTestTask("test-task")

        assertThat(testTask.failLevel).isEqualTo(FailLevel.ERROR)
    }

    @Test
    fun `test default system fail level`() {
        System.setProperty("defaultTaskFailLevel", FailLevel.WARN.toString())

        val testTask = createTestTask("test-task")

        assertThat(testTask.failLevel).isEqualTo(FailLevel.WARN)
    }

    @Test
    fun `test default global task priority should be NORMAL = 100`() {
        System.clearProperty("defaultTaskPriority")

        val testTask = createTestTask("test-task")

        assertThat(testTask.priority).isEqualTo(100)
    }

    @Test
    fun `test default system task priority`() {
        System.setProperty("defaultTaskPriority", 123454321.toString())

        val testTask = createTestTask("test-task")

        assertThat(testTask.priority).isEqualTo(123454321)
    }

    @Test
    fun `test default system task priority uses NORMAL if property not parsable to int`() {
        System.setProperty("defaultTaskPriority", "abcdefg")

        val testTask = createTestTask("test-task")

        assertThat(testTask.priority).isEqualTo(TaskPriorities.NORMAL)
    }

    companion object {
        fun createTestTask(taskName: String) = object : TestTask(taskName) {}
    }
}
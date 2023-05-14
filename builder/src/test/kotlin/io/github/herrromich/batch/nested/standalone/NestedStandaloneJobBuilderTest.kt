package io.github.herrromich.batch.fluent.standalone

import io.github.herrromich.batch.*
import io.github.herrromich.batch.builder.fluenrt.standalone.FluentStandaloneJobBuilder
import io.github.herrromich.batch.builder.nested.standalone.NestedStandaloneJobBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertFailsWith

class NestedStandaloneJobBuilderTest {

    @Test
    fun build() {
        val testTask1 = TestTask1()
        val testTask2 = TestTask2()
        val job = NestedStandaloneJobBuilder.instance("test-job1")
            .task("test-task3")
            .andTask("test-task4") {
                it.priority(TaskPriorities.LOW)
                .failLevel(FailLevel.WARN)
                .consumables("input-resource1", "input-resource2")
                .consumables(setOf("input-resource3", "input-resource4"))
                .producibles("output-resource1", "output-resource2")
                .producibles(setOf("output-resource3", "output-resource4"))
            }
            .andTask(testTask1)
            .andTask("test-task5")
            .and()
            .task(testTask2)
            .build()
        val testTask4 = job.tasks.first { it.name == "test-task4" }
        assertAll(
            { assertThat(job.tasks).hasSize(5) },
            { assertThat(testTask4.name).isEqualTo("test-task4") },
            { assertThat(testTask4.priority).isEqualTo(50) },
            { assertThat(testTask4.failLevel).isEqualTo(FailLevel.WARN) },
            { assertThat(testTask4.consumables).hasSize(4) },
            { assertThat(testTask4.producibles).hasSize(4) }
        )
    }

    @Test
    fun `test builder fails if duplicate task name`() {
        val exception = assertFailsWith<OrchestratorException> {
            val testTask1 = TestTask1()
            FluentStandaloneJobBuilder.instance("test-job1")
                .task("test-task1")
                .andTask(testTask1)
                .build()
        }
        assertThat(exception.message)
            .isEqualTo("Task \"test-task1\" is already contained in JobBuilder: \"test-job1\".")
    }

    @Test
    fun `test builder job name`() {
        val testTask1 = TestTask1()
        val jobBuilder = FluentStandaloneJobBuilder.instance("test-job1")
            .task("test-task3")
            .andTask("test-task4")
            .andTask(testTask1)
            .and()
        assertAll(
            { assertThat(jobBuilder.jobName).isEqualTo("test-job1") }
        )
    }

    @Test
    fun `test builder task name`() {
        val testTask1 = TestTask1()
        val jobBuilder = FluentStandaloneJobBuilder.instance("test-job1")
            .task("test-task3")
            .andTask("test-task4")
            .andTask(testTask1)
        assertAll(
            { assertThat(jobBuilder.taskName).isEqualTo("test-task1") }
        )
    }

    @Test
    fun `test builder task execute should fail if no execution set`() {
        val job = FluentStandaloneJobBuilder.instance("test-job1")
            .task("test-task3")
            .build()

        val task = job.tasks.find { it.name == "test-task3" }
        val mockedJobContext = mock<JobContext> {
            whenever(it.job).thenReturn(job)
        }
        val mockedTaskContext = mock<TaskContext> {
            whenever(it.jobContext).thenReturn(mockedJobContext)
        }
        val exception =
            assertFailsWith<OrchestratorException> { task?.execute(mockedTaskContext) }
        assertThat(exception.message).isEqualTo(
            "No runnable implementation in task: \"test-job1.test-task3\"."
        )
    }
}
package io.github.herrromich.batch.fluent.orchestrator

import io.github.herrromich.batch.*
import io.github.herrromich.batch.builder.fluenrt.orchestrator.FluentOrchestratorBuilder
import mu.KotlinLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.test.assertFailsWith

private val logger = KotlinLogging.logger { }

class FluentdOrchestratorBuilderTest {

    @Test
    fun build() {
        val testJob1 = TestJob1()
        val testJob2 = TestJob2()
        val testTask1 = TestTask1()
        val testTask2 = TestTask2()
        val orchestrator = FluentOrchestratorBuilder.instance()
            .job(testJob1)
            .and()
            .job("test-job3")
            .task(testTask1)
            .andTask("test-task3")
            .runnable { logger.info { "Executing \"test-task3\"" } }
            .andTask(testTask2)
            .and()
            .task("test-task4")
            .priority(TaskPriorities.LOW)
            .failLevel(FailLevel.WARN)
            .consumables("input-resource1", "input-resource2")
            .consumables(setOf("input-resource3", "input-resource4"))
            .producibles("output-resource1", "output-resource2")
            .producibles(setOf("output-resource3", "output-resource4"))
            .runnable { logger.info { "Executing \"test-task4\"" } }
            .andJob("test-job4")
            .andJob(testJob2)
            .build()
        val job = orchestrator.jobs.getValue("test-job3")
        val testTask4 = job.tasks.first { it.name == "test-task4" }
        assertAll(
            { assertThat(orchestrator.jobs).hasSize(4) },
            { assertThat(job.tasks).hasSize(4) },
            { assertThat(testTask4.name).isEqualTo("test-task4") },
            { assertThat(testTask4.priority).isEqualTo(50) },
            { assertThat(testTask4.failLevel).isEqualTo(FailLevel.WARN) },
            { assertThat(testTask4.consumables).hasSize(4) },
            { assertThat(testTask4.producibles).hasSize(4) }
        )
    }

    @Test
    fun `test job name`() {
        val builder = FluentOrchestratorBuilder.instance()
            .job("test-job3")
        assertThat(builder.jobName).isEqualTo("test-job3")
    }

    @Test
    fun `test task name`() {
        val builder = FluentOrchestratorBuilder.instance()
            .job("test-job3")
            .task("test-task4")
        assertThat(builder.taskName).isEqualTo("test-task4")
    }

    @Test
    fun `test builder fails if duplicate job name`() {
        val exception = assertFailsWith<OrchestratorException> {
            val testJob1 = TestJob1()
            FluentOrchestratorBuilder.instance()
                .job(testJob1)
                .andJob("test-job1")
                .build()
        }
        assertThat(exception.message).isEqualTo("Job \"test-job1\" is already contained in JobOrchestratorBuilder.")
    }

    @Test
    fun `test builder fails if duplicate task name`() {
        val exception = assertFailsWith<OrchestratorException> {
            val testJob1 = TestJob1()
            val testTask1 = TestTask1()
            FluentOrchestratorBuilder.instance()
                .job(testJob1)
                .andJob("test-job2")
                .task(testTask1)
                .andTask("test-task1")
                .build()
        }
        assertThat(exception.message).isEqualTo("Task \"test-task1\" is already contained in JobBuilder: \"test-job2\".")
    }

    @Test
    fun `test builder returns configuration if added`() {
        val configuration: OrchestratorConfiguration = object : OrchestratorConfiguration {}
        val builder = FluentOrchestratorBuilder.instance()
            .configuration(configuration)
        assertThat(builder.configuration).isSameAs(configuration)
    }
}
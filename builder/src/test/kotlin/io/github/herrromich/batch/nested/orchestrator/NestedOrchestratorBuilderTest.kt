package io.github.herrromich.batch.nested.orchestrator

import io.github.herrromich.batch.*
import io.github.herrromich.batch.builder.nested.orchestrator.NestedOrchestratorBuilder
import mu.KotlinLogging
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.test.assertFailsWith

private val logger = KotlinLogging.logger { }

class NestedOrchestratorBuilderTest {

    @Test
    fun build() {
        val testJob1 = TestJob1()
        val testJob2 = TestJob2()
        val testTask1 = TestTask1()
        val testTask2 = TestTask2()
        val orchestrator = NestedOrchestratorBuilder.instance()
            .job(testJob1)
            .and()
            .job("test-job3") {
                it.task(testTask1)
                    .andTask("test-task3") {
                        it.runnable { logger.info { "Executing \"test-task3\"" } }
                    }
                    .andTask(testTask2)
                    .and()
                    .task("test-task4") {
                        it.priority(TaskPriorities.LOW)
                            .failLevel(FailLevel.WARN)
                            .consumables("input-resource1", "input-resource2")
                            .consumables(setOf("input-resource3", "input-resource4"))
                            .producibles("output-resource1", "output-resource2")
                            .producibles(setOf("output-resource3", "output-resource4"))
                            .runnable { logger.info { "Executing \"test-task4\"" } }
                    }
                    .andTask("test-task5")
            }
            .andJob("test-job4")
            .andJob(testJob2)
            .build()
        val job = orchestrator.jobs.getValue("test-job3")
        val testTask4 = job.tasks.first { it.name == "test-task4" }
        assertAll(
            { Assertions.assertThat(orchestrator.jobs).hasSize(4) },
            { Assertions.assertThat(job.tasks).hasSize(5) },
            { Assertions.assertThat(testTask4.name).isEqualTo("test-task4") },
            { Assertions.assertThat(testTask4.priority).isEqualTo(50) },
            { Assertions.assertThat(testTask4.failLevel).isEqualTo(FailLevel.WARN) },
            { Assertions.assertThat(testTask4.consumables).hasSize(4) },
            { Assertions.assertThat(testTask4.producibles).hasSize(4) }
        )
    }

    @Test
    fun `test job name`() {
        val builder = NestedOrchestratorBuilder.instance()
            .job("test-job3")
        Assertions.assertThat(builder.jobName).isEqualTo("test-job3")
    }

    @Test
    fun `test task name`() {
        val builder = NestedOrchestratorBuilder.instance()
            .job("test-job3") {
                it.task("test-task4")
            }
        Assertions.assertThat(builder.taskName).isEqualTo("test-task4")
    }

    @Test
    fun `test builder fails if duplicate job name`() {
        val exception = assertFailsWith<OrchestratorException> {
            val testJob1 = TestJob1()
            NestedOrchestratorBuilder.instance()
                .job(testJob1)
                .andJob("test-job1")
                .build()
        }
        Assertions.assertThat(exception.message)
            .isEqualTo("Job \"test-job1\" is already contained in JobOrchestratorBuilder.")
    }

    @Test
    fun `test builder fails if duplicate task name`() {
        val exception = assertFailsWith<OrchestratorException> {
            val testJob1 = TestJob1()
            val testTask1 = TestTask1()
            NestedOrchestratorBuilder.instance()
                .job(testJob1)
                .andJob("test-job2") {
                    it.task(testTask1)
                        .andTask("test-task1")
                }
                .build()
        }
        Assertions.assertThat(exception.message)
            .isEqualTo("Task \"test-task1\" is already contained in JobBuilder: \"test-job2\".")
    }

    @Test
    fun `test builder returns configuration if added`() {
        val configuration: OrchestratorConfiguration = object : OrchestratorConfiguration {}
        val builder = NestedOrchestratorBuilder.instance()
            .configuration(configuration)
        Assertions.assertThat(builder.configuration).isSameAs(configuration)
    }
}
package io.github.herrromich.batch.internal

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.OrchestratorException
import io.github.herrromich.batch.TestTask
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class JobValidatorCheckCyclesTest {

    @Test
    fun `orphan validation should not fail if there are no cycles in resorces`() {
        val job = object : Job {
            override val name = "test-job"
            override val tasks = setOf(
                object : TestTask("test-task1") {
                    override val consumables = setOf("orphan-resource1", "orphan-resource2")
                },
                object : TestTask("test-task2") {
                    override val consumables = setOf("orphan-resource3")
                    override val producibles = setOf("orphan-resource1", "orphan-resource2")
                },
                object : TestTask("test-task3") {
                    override val producibles = setOf("orphan-resource3")
                },
            )

        }
        JobValidator.checkCycles(job)
    }

    @Test
    fun `orphan validation should fail if there are any consumables defined, that arent' produced`() {
        val job = object : Job {
            override val name = "test-job"
            override val tasks = setOf(
                object : TestTask("test-task1") {
                    override val consumables = setOf("orphan-resource1", "orphan-resource2")
                    override val producibles = setOf("orphan-resource3")
                },
                object : TestTask("test-task2") {
                    override val consumables = setOf("orphan-resource3")
                    override val producibles = setOf("orphan-resource1", "orphan-resource2")
                },
            )

        }
        val exception = assertThrows<OrchestratorException> {
            JobValidator.checkCycles(job)
        }
        Assertions.assertThat(exception.message).isEqualTo(
            """There are cycles in graph of job "test-job":
      test-task1
┌───┐ ↓
│   ↓
│  ♦{orphan-resource3}
│   ↓
│  test-task2
│   ↓
│  ♦{orphan-resource1}
│   ↓
│  test-task1
└───┘"""
        )
    }

}
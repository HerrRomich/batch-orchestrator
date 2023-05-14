package io.github.herrromich.batch.internal

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.OrchestratorException
import io.github.herrromich.batch.TestTask
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class JobValidatorCheckOrphansTest {

    @Test
    fun `orphan validation should not fail if there are no orphan consumables`() {
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
        JobValidator.checkOrphans(job)
    }

    @Test
    fun `orphan validation should fail if there are any consumables defined, that arent' produced`() {
        val job = object : Job {
            override val name = "test-job"
            override val tasks = setOf(
                object : TestTask("test-task1") {
                    override val consumables = setOf("orphan-resource1", "orphan-resource2")
                },
                object : TestTask("test-task2") {
                    override val consumables = setOf("orphan-resource3")
                },
            )

        }
        val exception = assertThrows<OrchestratorException> {
            JobValidator.checkOrphans(job)
        }
        Assertions.assertThat(exception.message).isEqualTo(
            """There are tasks in job "test-job", defining consumables, that are not produced by other tasks:
test-task1:
 - orphan-resource1
 - orphan-resource2
test-task2:
 - orphan-resource3""".replace(
                """
""", System.lineSeparator()
            )
        )
    }

}
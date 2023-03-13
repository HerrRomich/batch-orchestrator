package io.github.herrromich.batch.dsl

import io.github.herrromich.batch.OrchestratorException
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class DslJobFactoryTest {
    @Test
    fun `test job DSL factory`() {
        val job = job("integration") {
            task("first-task") {
                priority(10)
                producibles("resource1", "resource2")
            }
            task("second-task")
        }

        SoftAssertions.assertSoftly {
            it.assertThat(job.jobName).isEqualTo("integration")
            it.assertThat(job.tasks.size).isEqualTo(2)
        }
    }

    @Test
    fun `test orchestrator DSL fails`() {
        assertThrows<OrchestratorException> {
            job("integration") {
                task("first-task") {
                    priority(10)
                    producibles("resource1", "resource2")
                    producibles("resource1", "resource2")
                }
                task("first-task")
            }
        }
    }
}
package io.github.herrromich.batch.dsl

import io.github.herrromich.batch.OrchestratorException
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class OrchestratorDSLTest {
    @Test
    fun `test orchestrator DSL`() {
        val orchestrator = orchestrator {
            job("integration") {
                task("first-task") {
                    priority(10)
                    producibles("resource1", "resource2")
                }
                task("second-task")
            }
            job("debug")
        }
        SoftAssertions.assertSoftly {
            it.assertThat(orchestrator.jobs.size).isEqualTo(2)
            it.assertThat(orchestrator.jobs.keys).contains("integration")
            it.assertThat(orchestrator.jobs["integration"]!!.tasks.size).isEqualTo(2)
        }
    }

    @Test
    fun `test orchestrator DSL fails on duplicate task name`() {
        assertThrows<OrchestratorException> {
            orchestrator {
                job("integration") {
                    task("first-task") {
                        priority(10)
                        producibles("resource1", "resource2")
                        producibles("resource1", "resource2")
                    }
                    task("first-task")
                }
                job("debug")
            }
        }
    }
}
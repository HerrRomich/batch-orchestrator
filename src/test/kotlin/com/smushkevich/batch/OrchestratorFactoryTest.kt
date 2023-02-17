package com.smushkevich.batch

import org.assertj.core.api.SoftAssertions
import kotlin.test.Test

class OrchestratorFactoryTest {

    @Test
    fun testOrchestrator() {
        val orchestrator = OrchestratorFactory.instancs()
            .job("integration")
            .task("first-task")
            .producible("resource1", "resource2")
            .andTask("second-task")
            .andJob("debug")
            .build()

        SoftAssertions.assertSoftly {
            it.assertThat(orchestrator.jobs.size).isEqualTo(2)
            it.assertThat(orchestrator.jobs.keys).contains("integration")
            it.assertThat(orchestrator.jobs["integration"]!!.tasks.size).isEqualTo(2)
        }
    }

}
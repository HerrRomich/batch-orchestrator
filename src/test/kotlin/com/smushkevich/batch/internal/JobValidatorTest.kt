package com.smushkevich.batch.internal

import com.smushkevich.batch.OrchestratorException
import com.smushkevich.batch.config.StandaloneJobFactory
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class JobValidatorTest {
    @Test
    fun `check job without orphans should not fail`() {
        val job = StandaloneJobFactory.instance("test-job")
            .task("first-task")
            .producible("test-resource")
            .andTask("second-task")
            .consumable("test-resource")
            .build()
        JobValidator.checkOrphans(job)
    }

    @Test
    fun `check job with orphans should fail`() {
        val job = StandaloneJobFactory.instance("test-job")
            .task("first-task")
            .andTask("second-task")
            .consumable("test-resource")
            .build()
        assertThrows<OrchestratorException> { JobValidator.checkOrphans(job) }
    }

    @Test
    fun `check job without cycles should not fail`() {
        val job = StandaloneJobFactory.instance("test-job")
            .task("first-task")
            .producible("first-resource")
            .andTask("second-task")
            .consumable("first-resource")
            .producible("second-resource")
            .andTask("third-task")
            .consumable("second-resource")
            .producible("third-resource")
            .build()
        JobValidator.checkCycles(job)
    }

    @Test
    fun `check job with cycles should fail`() {
        val job = StandaloneJobFactory.instance("test-job")
            .task("second-task")
            .consumable("first-resource")
            .producible("second-resource")
            .andTask("third-task")
            .consumable("second-resource")
            .producible("first-resource")
            .build()
        assertThrows<OrchestratorException> { JobValidator.checkCycles(job) }
    }
}
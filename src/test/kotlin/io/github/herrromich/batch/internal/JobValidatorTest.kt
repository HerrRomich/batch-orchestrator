package io.github.herrromich.batch.internal

import io.github.herrromich.batch.OrchestratorException
import io.github.herrromich.batch.config.StandaloneJobFactory
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class JobValidatorTest {
    @Test
    fun `check job without orphans should not fail`() {
        val job = StandaloneJobFactory.instance("test-job")
            .task("first-task")
            .producibles("test-resource")
            .andTask("second-task")
            .consumables("test-resource")
            .build()
        JobValidator.checkOrphans(job)
    }

    @Test
    fun `check job with orphans should fail`() {
        val job = StandaloneJobFactory.instance("test-job")
            .task("first-task")
            .andTask("second-task")
            .consumables("test-resource")
            .build()
        assertThrows<OrchestratorException> { JobValidator.checkOrphans(job) }
    }

    @Test
    fun `check job without cycles should not fail`() {
        val job = StandaloneJobFactory.instance("test-job")
            .task("first-task")
            .producibles("first-resource")
            .andTask("second-task")
            .consumables("first-resource")
            .producibles("second-resource")
            .andTask("third-task")
            .consumables("second-resource")
            .producibles("third-resource")
            .build()
        JobValidator.checkCycles(job)
    }

    @Test
    fun `check job with cycles should fail`() {
        val job = StandaloneJobFactory.instance("test-job")
            .task("second-task")
            .consumables("first-resource")
            .producibles("second-resource")
            .andTask("third-task")
            .consumables("second-resource")
            .producibles("first-resource")
            .build()
        assertThrows<OrchestratorException> { JobValidator.checkCycles(job) }
    }
}
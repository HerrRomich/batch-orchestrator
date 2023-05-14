package io.github.herrromich.batch.concurrent.internal

import io.github.herrromich.batch.concurrent.ConcurrentOrchestratorConfiguration
import org.assertj.core.api.Assertions
import kotlin.test.Test

class TestConcurrentOrchestratorConfiguration {

    @Test
    fun `test default configuration`() {
        val configuration = ConcurrentOrchestratorConfiguration()
        Assertions.assertThat(configuration.threadPoolSize).isEqualTo(10)
    }

    @Test
    fun `test default configuration with setting`() {
        System.setProperty(ConcurrentOrchestratorConfiguration.THREAD_POOL_SIZE, "15")
        val configuration = ConcurrentOrchestratorConfiguration()
        Assertions.assertThat(configuration.threadPoolSize).isEqualTo(15)
    }

    @Test
    fun `test default configuration with unparsable setting`() {
        System.setProperty(ConcurrentOrchestratorConfiguration.THREAD_POOL_SIZE, "unparsable")
        val configuration = ConcurrentOrchestratorConfiguration()
        Assertions.assertThat(configuration.threadPoolSize).isEqualTo(10)
    }

    @Test
    fun `test customized configuration`() {
        val configuration = ConcurrentOrchestratorConfiguration(15)
        Assertions.assertThat(configuration.threadPoolSize).isEqualTo(15)
    }

}
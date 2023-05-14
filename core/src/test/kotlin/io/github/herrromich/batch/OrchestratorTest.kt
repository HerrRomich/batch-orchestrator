package io.github.herrromich.batch

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class OrchestratorTest {

    @Test
    fun `test instance should fail if no service registered`() {
        val exception = assertThrows<OrchestratorException> {
            Orchestrator.instance(
                emptySet(),
                object : OrchestratorConfiguration {})
        }
        Assertions.assertThat(exception.message)
            .isEqualTo("There are no registered classes of interface \"io.github.herrromich.batch.OrchestratorProvider\".")
    }
}
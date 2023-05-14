package io.github.herrromich.batch.internal

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.temporal.ChronoUnit

class MiscTest {

    @Test
    fun `test pretty print in null then infinity`() {
        val testDuration: Duration? = null
        val prettyDuration = testDuration.prettyPrint()
        assertThat(prettyDuration).isEqualTo("infinity")
    }

    @Test
    fun `test pretty print hours`() {
        val testDuration = Duration.of(3, ChronoUnit.HOURS)
        val prettyDuration = testDuration.prettyPrint()
        assertThat(prettyDuration).isEqualTo("3h 0m 0.000s")
    }

    @Test
    fun `test pretty print minutes`() {
        val testDuration = Duration.of(3, ChronoUnit.MINUTES)
        val prettyDuration = testDuration.prettyPrint()
        assertThat(prettyDuration).isEqualTo("3m 0.000s")
    }

    @Test
    fun `test pretty print seconds`() {
        val testDuration = Duration.of(3, ChronoUnit.SECONDS)
        val prettyDuration = testDuration.prettyPrint()
        assertThat(prettyDuration).isEqualTo("3.000s")
    }

    @Test
    fun `test pretty print millis`() {
        val testDuration = Duration.of(3, ChronoUnit.MILLIS)
        val prettyDuration = testDuration.prettyPrint()
        assertThat(prettyDuration).isEqualTo(".003s")
    }
}
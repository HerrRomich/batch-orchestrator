package io.github.herrromich.batch.concurrent.internal

import org.assertj.core.api.Assertions
import org.assertj.core.presentation.StandardRepresentation
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.Future

class TestFutureTaskWithPriority {

    @BeforeEach
    fun init() {
        Assertions.useRepresentation(object : StandardRepresentation() {
            override fun toStringOf(future: Future<*>?): String {
                return (future as? FutureTaskWithPriority)?.run { "FutureTaskWithPriority(priority=$priority)" }
                    ?: super.toStringOf(future)
            }
        })
    }

    @AfterEach
    fun finalize() {
        Assertions.useRepresentation(StandardRepresentation())
    }

    @Test
    fun `test should sort by priority`() {
        val feature0 = FutureTaskWithPriority(1) {}
        val feature1 = FutureTaskWithPriority(10) {}
        val feature2 = FutureTaskWithPriority(100) {}
        val feature3 = FutureTaskWithPriority(1000) {}
        val expectedFeature = listOf(feature0, feature1, feature2, feature3)
        val actualFutures = listOf(feature2, feature3, feature1, feature0).sorted()
        Assertions.assertThat(actualFutures).isEqualTo(expectedFeature)
    }

}
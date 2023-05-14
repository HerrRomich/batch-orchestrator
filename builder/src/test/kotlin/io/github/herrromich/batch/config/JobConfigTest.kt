package io.github.herrromich.batch.config

import io.github.herrromich.batch.TestJob1
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class JobConfigTest {

    @Test
    fun `test job config second constructor`() {
        val testJob1 = TestJob1()
        val testJobConfig1 = JobConfig(testJob1.name, testJob1.tasks.toSet())
        assertAll(
            { assertThat(testJobConfig1.name).isEqualTo(testJob1.name) },
            {
                assertAll(*testJobConfig1.tasks.map { jobConfigTask ->
                    val jobTask = testJob1.tasks.find { jobTask -> jobTask.name == jobConfigTask.name }!!
                    return@map { TaskConfigTest.assertTaskEqualsDeeply(jobConfigTask, jobTask) }
                }.toTypedArray())
            },
        )
    }

    @Test
    fun `test job configs are equal if names are same`() {
        val jobConfig1 = JobConfig("test-job1")
        val jobConfig2 = JobConfig("test-job1")
        assertThat(jobConfig1).isEqualTo(jobConfig2)
    }

    @Test
    fun `test job configs are equal if object is same`() {
        val jobConfig1 = JobConfig("test-job1")
        assertThat(jobConfig1).isEqualTo(jobConfig1)
    }

    @Test
    fun `test job configs are not equal if names are not same`() {
        val jobConfig1 = JobConfig("test-job1")
        val jobConfig2 = JobConfig("test-job2")
        assertThat(jobConfig1).isNotEqualTo(jobConfig2)
    }

    @Test
    fun `test job configs are not equal to different class`() {
        val jobConfig1 = JobConfig("test-job1")
        assertThat(jobConfig1.equals("")).isFalse()
    }

    @Test
    fun `test job configs are not equal to null`() {
        val jobConfig1 = JobConfig("test-job1")
        assertThat(jobConfig1.equals(null)).isFalse()
    }
}
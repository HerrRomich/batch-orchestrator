package io.github.herrromich.batch.internal

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.OrchestratorException
import io.github.herrromich.batch.TestTask
import io.github.herrromich.batch.spi.threadLocalJobInstanceFactory
import mu.KotlinLogging
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

private val logger = KotlinLogging.logger { }

class BaseOrchestratorTest {

    private lateinit var baseOrchestratorUnderTest: BaseOrchestrator
    private lateinit var testJob: Job
    private lateinit var mockedJobInstance: JobInstance
    private lateinit var mockedTaskInstance: TaskInstance

    @BeforeEach
    fun init() {
        val testTask1 = object : TestTask("test-task") {}
        testJob = object : Job {
            override val name = "test-job"
            override val tasks = setOf(testTask1)
        }
        val jobs = setOf(testJob)
        mockedJobInstance = mock {
            `when`(it.job).thenReturn(testJob)
        }
        threadLocalJobInstanceFactory.set { mockedJobInstance }
        mockedTaskInstance = mock {
            `when`(it.future).thenCallRealMethod()
            `when`(it.jobContext).thenReturn(mockedJobInstance)
        }
        baseOrchestratorUnderTest = object : BaseOrchestrator(jobs) {
            override fun executeTask(taskInstance: TaskInstance) {
                logger.info { "Running test task instance." }
                submit(taskInstance.jobContext)
            }
        }
    }

    @Test
    fun `test execute should fail if job is unknown`() {
        val exception = assertThrows<OrchestratorException> { baseOrchestratorUnderTest.execute("unknown-job") }
        assertThat(exception.message).isEqualTo("Job \"unknown-job\" is not registered in the batch orchestrator.")
    }

    @Test
    fun `test execute should fail if executed twice the same job`() {
        baseOrchestratorUnderTest.execute("test-job")
        val exception = assertThrows<OrchestratorException> { baseOrchestratorUnderTest.execute("test-job") }
        assertThat(exception.message).isEqualTo("Job \"test-job\" is running right now! It is not allowed to have multiple executions of the same job at the same time.")
    }

    @Test
    fun `test execute and finalize`() {
        `when`(mockedJobInstance.isFinished).thenReturn(false).thenReturn(true)
        `when`(mockedJobInstance.queueUp(any())).then {
            (it.arguments[0] as? ((taskInstance: TaskInstance) -> Unit))?.let { it(mockedTaskInstance) }
                ?: Assertions.fail("Task instance is of wrong type!")
        }
        baseOrchestratorUnderTest.execute("test-job")
        verify(mockedJobInstance).finish()
    }

    @Test
    fun `test execute and finalize fails if job is unknown`() {
        `when`(mockedJobInstance.isFinished).thenReturn(false).thenReturn(true)
        val mockedJob = mock<Job> {
            `when`(it.name).thenReturn("test-job")
        }
        `when`(mockedJobInstance.job).thenReturn(mockedJob)
        `when`(mockedJobInstance.queueUp(any())).then {
            (it.arguments[0] as? ((taskInstance: TaskInstance) -> Unit))?.let { it(mockedTaskInstance) }
                ?: Assertions.fail("Task instance is of wrong type!")
        }
        val exception = assertThrows<OrchestratorException> { baseOrchestratorUnderTest.execute("test-job") }
        assertThat(exception.message).isEqualTo("Fatal error. Unknown jobInstance for job \"test-job\".")
    }
}
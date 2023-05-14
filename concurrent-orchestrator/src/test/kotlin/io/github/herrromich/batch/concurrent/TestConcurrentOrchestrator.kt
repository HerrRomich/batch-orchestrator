package io.github.herrromich.batch.concurrent

import io.github.herrromich.batch.*
import io.github.herrromich.batch.concurrent.internal.ConcurrentOrchestratorProvider
import io.github.herrromich.batch.events.JobEvent
import io.github.herrromich.batch.events.TaskEvent
import io.github.herrromich.batch.internal.TestJobInstance
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class TestConcurrentOrchestrator {

    @Test
    fun `test orchestrator without error`() {
        val tasks = setOf(
            object : TestTask("test-task1") {
                override fun execute(context: TaskContext) {}
            }
        )
        val jobs = setOf(
            object : Job {
                override val name = "test-job"
                override val tasks = tasks
            }
        )
        val orchestrator =
            ConcurrentOrchestratorProvider().provideOrchestrator(jobs, ConcurrentOrchestratorConfiguration())
        val context = orchestrator.execute("test-job")
        context.join()
        val jobContext = context as TestJobInstance
        val lastEvent = jobContext.events.last() as JobEvent
        assertThat(lastEvent.state).isEqualTo(JobState.COMPLETED)
    }

    @Test
    fun `test orchestrator with error`() {
        val tasks = setOf(
            object : TestTask("test-task1") {
                override fun execute(context: TaskContext) {
                    Thread.sleep(100)
                    throw RuntimeException("Just error")
                }
            },
            object : TestTask("test-task2") {
                override fun execute(context: TaskContext) {
                    Thread.sleep(200)
                }
            },
        )
        val jobs = setOf(
            object : Job {
                override val name = "test-job"
                override val tasks = tasks
            }
        )
        val orchestrator =
            ConcurrentOrchestratorProvider().provideOrchestrator(jobs, ConcurrentOrchestratorConfiguration())
        val context = orchestrator.execute("test-job")
        val exception = assertThrows<OrchestratorException> { context.join() }
        val jobContext = context as TestJobInstance
        val lastEvent = jobContext.events.last() as JobEvent
        val lastTask1Event =
            jobContext.events.filterIsInstance<TaskEvent>().filter { it.task.name == "test-task1" }.last()
        val lastTask2Event =
            jobContext.events.filterIsInstance<TaskEvent>().filter { it.task.name == "test-task2" }.last()
        assertAll({
            assertThat(exception.message).isEqualTo("Job execution is failed.")
        },{
            assertThat(lastTask1Event.state).isEqualTo(TaskState.ERROR)
        },{
            assertThat(lastTask2Event.state).isEqualTo(TaskState.COMPLETED)
        }, {
            assertThat(lastEvent.state).isEqualTo(JobState.ERROR)
        })
    }

    @Test
    fun `test orchestrator with warning`() {
        val tasks = setOf(
            object : TestTask("test-task1") {
                override val failLevel = FailLevel.WARN
                override fun execute(context: TaskContext) {
                    Thread.sleep(100)
                    throw RuntimeException("Just error")
                }
            },
            object : TestTask("test-task2") {
                override fun execute(context: TaskContext) {
                    Thread.sleep(200)
                }
            },
        )
        val jobs = setOf(
            object : Job {
                override val name = "test-job"
                override val tasks = tasks
            }
        )
        val orchestrator =
            ConcurrentOrchestratorProvider().provideOrchestrator(jobs, ConcurrentOrchestratorConfiguration())
        val context = orchestrator.execute("test-job")
        context.join()
        val jobContext = context as TestJobInstance
        val lastEvent = jobContext.events.last() as JobEvent
        val lastTask1Event =
            jobContext.events.filterIsInstance<TaskEvent>().filter { it.task.name == "test-task1" }.last()
        val lastTask2Event =
            jobContext.events.filterIsInstance<TaskEvent>().filter { it.task.name == "test-task2" }.last()
        assertAll({
            assertThat(lastTask1Event.state).isEqualTo(TaskState.WARN)
        },{
            assertThat(lastTask2Event.state).isEqualTo(TaskState.COMPLETED)
        }, {
            assertThat(lastEvent.state).isEqualTo(JobState.COMPLETED_WITH_WARNINGS)
        })
    }

    @Test
    fun `test orchestrator with fatal`() {
        val tasks = setOf(
            object : TestTask("test-task1") {
                override val failLevel = FailLevel.FATAL
                override fun execute(context: TaskContext) {
                    Thread.sleep(100)
                    throw RuntimeException("Just error")
                }
            },
            object : TestTask("test-task2") {
                override fun execute(context: TaskContext) {
                    Thread.sleep(200)
                }
            },
        )
        val jobs = setOf(
            object : Job {
                override val name = "test-job"
                override val tasks = tasks
            }
        )
        val orchestrator =
            ConcurrentOrchestratorProvider().provideOrchestrator(jobs, ConcurrentOrchestratorConfiguration())
        val context = orchestrator.execute("test-job")
        val exception = assertThrows<OrchestratorException> { context.join()}
        val jobContext = context as TestJobInstance
        val lastEvent = jobContext.events.last() as JobEvent
        val lastTask1Event =
            jobContext.events.filterIsInstance<TaskEvent>().filter { it.task.name == "test-task1" }.last()
        val lastTask2Event =
            jobContext.events.filterIsInstance<TaskEvent>().filter { it.task.name == "test-task2" }.last()
        assertAll({
            assertThat(exception.message).isEqualTo("Job execution is fatally failed.")
        },{
            assertThat(lastTask1Event.state).isEqualTo(TaskState.FATAL)
        },{
            assertThat(lastTask2Event.state).isEqualTo(TaskState.CANCELED)
        }, {
            assertThat(lastEvent.state).isEqualTo(JobState.FATAL)
        })
    }

    @Test
    fun `test concurrent orchestrator with wrong configuration`() {
        val exeception = assertThrows<OrchestratorException> {
            ConcurrentOrchestratorProvider().provideOrchestrator(
                emptySet(),
                object : OrchestratorConfiguration {})
        }
        assertThat(exeception.message).contains("Configuration of wrong type")
    }

}
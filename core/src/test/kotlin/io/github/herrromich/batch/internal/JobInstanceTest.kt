package io.github.herrromich.batch.internal

import io.github.herrromich.batch.*
import io.github.herrromich.batch.events.ExecutionEvent
import io.github.herrromich.batch.events.JobEvent
import io.github.herrromich.batch.events.TaskEvent
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class JobInstanceTest {

    private lateinit var job: Job
    private lateinit var task1: TestTask
    private var task1FailLevel = FailLevel.ERROR
    private lateinit var task2: TestTask
    private lateinit var task3: TestTask
    private lateinit var unknownTask: TestTask
    private lateinit var jobExecutionUnderTest: JobInstance
    private lateinit var events: MutableList<ExecutionEvent>

    @BeforeEach
    fun initJobInstance() {
        task1 = object : TestTask("test-task1") {
            override val producibles = setOf("test-resource1")
            override val failLevel: FailLevel
                get() = task1FailLevel
        }
        task2 = object : TestTask("test-task2") {
            override val consumables = setOf("test-resource1")
        }
        task3 = object : TestTask("test-task3") {}
        unknownTask = object : TestTask("unknown-task") {}
        job = object : Job {
            override val name = "test-job"
            override val tasks = setOf(task1, task2, task3)
        }
        events = mutableListOf()
        jobExecutionUnderTest = TestJobInstance(job, events)
    }

    @Test
    fun `test job start`() {
        jobExecutionUnderTest.start()
        val statistics = jobExecutionUnderTest.getExecutionStatisticsSnapshot()
        assertAll({ assertThat(statistics.submittedTasks).hasSize(3) },
            { assertThat(statistics.tasksWithUncompletedConsumables).hasSize(1) },
            { assertThat(statistics.fulfilledTaskCount).isEqualTo(0) },
            { assertThat(statistics.runningTaskCount).isEqualTo(0) },
            { assertThat(statistics.completedTaskCount).isEqualTo(0) },
            { assertThat(statistics.warnTaskCount).isEqualTo(0) },
            { assertThat(statistics.failedTaskCount).isEqualTo(0) },
            { assertThat(statistics.fatalTaskCount).isEqualTo(0) },
            { assertThat(statistics.skippedTaskCount).isEqualTo(0) },
            { assertThat(statistics.canceledTaskCount).isEqualTo(0) },
            { assertThat(jobExecutionUnderTest.isFinished).isFalse() })
    }

    @Test
    fun `test job start and queue up`() {
        jobExecutionUnderTest.start()
        jobExecutionUnderTest.queueUp { }
        val statistics = jobExecutionUnderTest.getExecutionStatisticsSnapshot()
        assertAll({ assertThat(statistics.submittedTasks).hasSize(1) },
            { assertThat(statistics.tasksWithUncompletedConsumables).hasSize(1) },
            { assertThat(statistics.fulfilledTaskCount).isEqualTo(2) },
            { assertTaskEventState(events, TaskState.FULFILLED) },
            { assertThat(statistics.runningTaskCount).isEqualTo(0) },
            { assertThat(statistics.completedTaskCount).isEqualTo(0) },
            { assertThat(statistics.warnTaskCount).isEqualTo(0) },
            { assertThat(statistics.failedTaskCount).isEqualTo(0) },
            { assertThat(statistics.fatalTaskCount).isEqualTo(0) },
            { assertThat(statistics.skippedTaskCount).isEqualTo(0) },
            { assertThat(statistics.canceledTaskCount).isEqualTo(0) },
            { assertThat(jobExecutionUnderTest.isFinished).isFalse() })
    }

    private fun assertTaskEventState(
        events: MutableList<ExecutionEvent>,
        expectedState: TaskState
    ) {
        (events.last() as? TaskEvent)?.let { taskEvent ->
            assertThat(taskEvent.state).isEqualTo(expectedState)
        } ?: Assertions.fail("Last event is not of type TaskEvent<TestTask>!")
    }

    @Test
    fun `test job submit should fail if not started`() {
        assertThrows<UninitializedPropertyAccessException> {
            jobExecutionUnderTest.queueUp { }
        }
    }

    @Test
    fun `test job instance doesnt allowed to start if already runs`() {
        jobExecutionUnderTest.start()
        val exception = assertThrows<OrchestratorException> {
            jobExecutionUnderTest.start()
        }
        assertThat(exception.message).isEqualTo("Job instance is already started!")
    }

    @Test
    fun `test job instance set unknown task to started should fail`() {
        jobExecutionUnderTest.start()
        jobExecutionUnderTest.queueUp { }
        val exception = assertThrows<OrchestratorException> { jobExecutionUnderTest.setTaskStarted(unknownTask) }
        assertThat(exception.message).isEqualTo("Fatal error. There is no queued instance of task \"unknown-task\".")
    }

    @Test
    fun `test job instance set submitted task to started should fail`() {
        jobExecutionUnderTest.start()
        jobExecutionUnderTest.queueUp { }
        jobExecutionUnderTest.setTaskStarted(task1)
        val exception = assertThrows<OrchestratorException> { jobExecutionUnderTest.setTaskStarted(task1) }
        assertThat(exception.message).isEqualTo("Fatal error. Task \"test-task1\" cannot be started as it is not fulfilled.")
    }

    @Test
    fun `test job instance set fulfilled task to started`() {
        jobExecutionUnderTest.start()
        jobExecutionUnderTest.queueUp { }
        jobExecutionUnderTest.setTaskStarted(task1)
        val actualStatistics = jobExecutionUnderTest.getExecutionStatisticsSnapshot()
        assertAll({ assertThat(actualStatistics.submittedTasks).hasSize(1) },
            { assertThat(actualStatistics.tasksWithUncompletedConsumables).hasSize(1) },
            { assertThat(actualStatistics.fulfilledTaskCount).isEqualTo(1) },
            { assertTaskEventState(events, TaskState.RUNNING) },
            { assertThat(actualStatistics.runningTaskCount).isEqualTo(1) },
            { assertThat(actualStatistics.completedTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.warnTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.failedTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.fatalTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.skippedTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.canceledTaskCount).isEqualTo(0) },
            { assertThat(jobExecutionUnderTest.isFinished).isFalse() })
    }

    @Test
    fun `test job instance set unknown task to completed should fail`() {
        jobExecutionUnderTest.start()
        jobExecutionUnderTest.queueUp { }
        val exception = assertThrows<OrchestratorException> { jobExecutionUnderTest.setTaskCompleted(unknownTask) }
        assertThat(exception.message).isEqualTo("Fatal error. There is no queued instance of task \"unknown-task\".")
    }

    @Test
    fun `test job instance set fulfilled task to completed should fail`() {
        jobExecutionUnderTest.start()
        jobExecutionUnderTest.queueUp { }
        val exception = assertThrows<OrchestratorException> { jobExecutionUnderTest.setTaskCompleted(task1) }
        assertThat(exception.message).isEqualTo("Fatal error. Task \"test-task1\" cannot be completed as it is not running.")
    }

    @Test
    fun `test job instance set started task to completed`() {
        jobExecutionUnderTest.start()
        jobExecutionUnderTest.queueUp { }
        jobExecutionUnderTest.setTaskStarted(task1)
        jobExecutionUnderTest.setTaskCompleted(task1)
        val actualStatistics = jobExecutionUnderTest.getExecutionStatisticsSnapshot()
        assertAll({ assertThat(actualStatistics.submittedTasks).hasSize(1) },
            { assertThat(actualStatistics.tasksWithUncompletedConsumables).hasSize(0) },
            { assertThat(actualStatistics.fulfilledTaskCount).isEqualTo(1) },
            { assertTaskEventState(events, TaskState.COMPLETED) },
            { assertThat(actualStatistics.runningTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.completedTaskCount).isEqualTo(1) },
            { assertThat(actualStatistics.warnTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.failedTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.fatalTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.skippedTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.canceledTaskCount).isEqualTo(0) },
            { assertThat(jobExecutionUnderTest.isFinished).isFalse() })
    }

    @Test
    fun `test job instance set fulfilled task to cancel should fail`() {
        jobExecutionUnderTest.start()
        jobExecutionUnderTest.queueUp { }
        val exception = assertThrows<OrchestratorException> { jobExecutionUnderTest.setTaskCanceled(task1) }
        assertThat(exception.message).isEqualTo("Fatal error. Task \"test-task1\" cannot be canceled as it is not running.")
    }

    @Test
    fun `test job instance set unknown task to cancel should fail`() {
        jobExecutionUnderTest.start()
        jobExecutionUnderTest.queueUp { }
        jobExecutionUnderTest.setTaskStarted(task1)
        val exception = assertThrows<OrchestratorException> { jobExecutionUnderTest.setTaskCanceled(unknownTask) }
        assertThat(exception.message).isEqualTo("Fatal error. There is no queued instance of task \"unknown-task\".")
    }

    @Test
    fun `test job instance set started task canceled`() {
        jobExecutionUnderTest.start()
        jobExecutionUnderTest.queueUp { }
        jobExecutionUnderTest.setTaskStarted(task1)
        jobExecutionUnderTest.setTaskCanceled(task1)
        val actualStatistics = jobExecutionUnderTest.getExecutionStatisticsSnapshot()
        assertAll({ assertThat(actualStatistics.submittedTasks).hasSize(1) },
            { assertThat(actualStatistics.tasksWithUncompletedConsumables).hasSize(1) },
            { assertThat(actualStatistics.fulfilledTaskCount).isEqualTo(1) },
            { assertTaskEventState(events, TaskState.CANCELED) },
            { assertThat(actualStatistics.runningTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.completedTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.warnTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.failedTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.fatalTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.skippedTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.canceledTaskCount).isEqualTo(1) },
            { assertThat(jobExecutionUnderTest.isFinished).isFalse() })
    }

    @Test
    fun `test job instance set unknown task to failed should fail`() {
        jobExecutionUnderTest.start()
        jobExecutionUnderTest.queueUp { }
        jobExecutionUnderTest.setTaskStarted(task1)
        val exception = assertThrows<OrchestratorException> { jobExecutionUnderTest.setTaskFailed(unknownTask) }
        assertThat(exception.message).isEqualTo("Fatal error. There is no queued instance of task \"unknown-task\".")
    }

    @Test
    fun `test job instance set fulfilled task to fail should fail`() {
        jobExecutionUnderTest.start()
        jobExecutionUnderTest.queueUp { }
        val exception = assertThrows<OrchestratorException> { jobExecutionUnderTest.setTaskFailed(task1) }
        assertThat(exception.message).isEqualTo("Fatal error. Task \"test-task1\" cannot be failed as it is not running.")
    }

    @Test
    fun `test job instance set started task failed if fail level is error`() {
        task1FailLevel = FailLevel.ERROR
        jobExecutionUnderTest.start()
        jobExecutionUnderTest.queueUp { }
        jobExecutionUnderTest.setTaskStarted(task1)
        jobExecutionUnderTest.setTaskFailed(task1)
        val actualStatistics = jobExecutionUnderTest.getExecutionStatisticsSnapshot()
        assertAll({ assertThat(actualStatistics.submittedTasks).hasSize(1) },
            { assertThat(actualStatistics.tasksWithUncompletedConsumables).hasSize(1) },
            { assertThat(actualStatistics.fulfilledTaskCount).isEqualTo(1) },
            { assertTaskEventState(events, TaskState.ERROR) },
            { assertThat(actualStatistics.runningTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.completedTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.warnTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.failedTaskCount).isEqualTo(1) },
            { assertThat(actualStatistics.fatalTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.skippedTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.canceledTaskCount).isEqualTo(0) },
            { assertThat(jobExecutionUnderTest.isFinished).isFalse() })
    }

    @Test
    fun `test job instance set started task failed if fail level is warn`() {
        task1FailLevel = FailLevel.WARN
        jobExecutionUnderTest.start()
        jobExecutionUnderTest.queueUp { }
        jobExecutionUnderTest.setTaskStarted(task1)
        jobExecutionUnderTest.setTaskFailed(task1)
        val actualStatistics = jobExecutionUnderTest.getExecutionStatisticsSnapshot()
        assertAll({ assertThat(actualStatistics.submittedTasks).hasSize(1) },
            { assertThat(actualStatistics.tasksWithUncompletedConsumables).hasSize(0) },
            { assertThat(actualStatistics.fulfilledTaskCount).isEqualTo(1) },
            { assertTaskEventState(events, TaskState.WARN) },
            { assertThat(actualStatistics.runningTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.completedTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.warnTaskCount).isEqualTo(1) },
            { assertThat(actualStatistics.failedTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.fatalTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.skippedTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.canceledTaskCount).isEqualTo(0) },
            { assertThat(jobExecutionUnderTest.isFinished).isFalse() })
    }

    @Test
    fun `test job instance set started task failed if fail level is fatal`() {
        task1FailLevel = FailLevel.FATAL
        jobExecutionUnderTest.start()
        jobExecutionUnderTest.queueUp { }
        jobExecutionUnderTest.setTaskStarted(task1)
        jobExecutionUnderTest.setTaskFailed(task1)
        val actualStatistics = jobExecutionUnderTest.getExecutionStatisticsSnapshot()
        assertAll({ assertThat(actualStatistics.submittedTasks).hasSize(0) },
            { assertThat(actualStatistics.tasksWithUncompletedConsumables).hasSize(1) },
            { assertThat(actualStatistics.fulfilledTaskCount).isEqualTo(0) },
            { assertTaskEventState(events, TaskState.SKIPPED) },
            { assertThat(actualStatistics.runningTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.completedTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.warnTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.failedTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.fatalTaskCount).isEqualTo(1) },
            { assertThat(actualStatistics.skippedTaskCount).isEqualTo(1) },
            { assertThat(actualStatistics.canceledTaskCount).isEqualTo(1) },
            { assertThat(jobExecutionUnderTest.isFinished).isTrue() })
    }

    @Test
    fun `test job finish completed`() {
        jobExecutionUnderTest.start()
        jobExecutionUnderTest.queueUp { }
        jobExecutionUnderTest.setTaskStarted(task1)
        jobExecutionUnderTest.setTaskStarted(task3)
        jobExecutionUnderTest.setTaskCompleted(task1)
        jobExecutionUnderTest.setTaskCompleted(task3)
        jobExecutionUnderTest.queueUp { }
        jobExecutionUnderTest.setTaskStarted(task2)
        jobExecutionUnderTest.setTaskCompleted(task2)
        jobExecutionUnderTest.finish()
        val actualStatistics = jobExecutionUnderTest.getExecutionStatisticsSnapshot()
        assertAll({ assertThat(actualStatistics.submittedTasks).hasSize(0) },
            { assertThat(actualStatistics.tasksWithUncompletedConsumables).hasSize(0) },
            { assertThat(actualStatistics.fulfilledTaskCount).isEqualTo(0) },
            { assertJobEventState(events, JobState.COMPLETED) },
            { assertThat(actualStatistics.runningTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.completedTaskCount).isEqualTo(3) },
            { assertThat(actualStatistics.warnTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.failedTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.fatalTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.skippedTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.canceledTaskCount).isEqualTo(0) },
            { assertThat(jobExecutionUnderTest.isFinished).isTrue() })
    }

    private fun assertJobEventState(
        events: MutableList<ExecutionEvent>,
        expectedState: JobState
    ) {
        (events.last() as? JobEvent)?.let { JobEvent ->
            assertThat(JobEvent.state).isEqualTo(expectedState)
        } ?: Assertions.fail("Last event is not of type JobEvent<TestTask>!")
    }

    @Test
    fun `test job finish error`() {
        task1FailLevel = FailLevel.ERROR
        jobExecutionUnderTest.start()
        jobExecutionUnderTest.queueUp { }
        jobExecutionUnderTest.setTaskStarted(task1)
        jobExecutionUnderTest.setTaskStarted(task3)
        jobExecutionUnderTest.setTaskFailed(task1)
        jobExecutionUnderTest.setTaskCompleted(task3)
        jobExecutionUnderTest.queueUp { }
        jobExecutionUnderTest.finish()
        val actualStatistics = jobExecutionUnderTest.getExecutionStatisticsSnapshot()
        assertAll({ assertThat(actualStatistics.submittedTasks).hasSize(1) },
            { assertThat(actualStatistics.tasksWithUncompletedConsumables).hasSize(1) },
            { assertThat(actualStatistics.fulfilledTaskCount).isEqualTo(0) },
            { assertJobEventState(events, JobState.ERROR) },
            { assertThat(actualStatistics.runningTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.completedTaskCount).isEqualTo(1) },
            { assertThat(actualStatistics.warnTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.failedTaskCount).isEqualTo(1) },
            { assertThat(actualStatistics.fatalTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.skippedTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.canceledTaskCount).isEqualTo(0) },
            { assertThat(jobExecutionUnderTest.isFinished).isTrue() })
    }

    @Test
    fun `test job finish completed with warning`() {
        task1FailLevel = FailLevel.WARN
        jobExecutionUnderTest.start()
        jobExecutionUnderTest.queueUp { }
        jobExecutionUnderTest.setTaskStarted(task1)
        jobExecutionUnderTest.setTaskStarted(task3)
        jobExecutionUnderTest.setTaskFailed(task1)
        jobExecutionUnderTest.setTaskCompleted(task3)
        jobExecutionUnderTest.queueUp { }
        jobExecutionUnderTest.setTaskStarted(task2)
        jobExecutionUnderTest.setTaskCompleted(task2)
        jobExecutionUnderTest.finish()
        val actualStatistics = jobExecutionUnderTest.getExecutionStatisticsSnapshot()
        assertAll({ assertThat(actualStatistics.submittedTasks).hasSize(0) },
            { assertThat(actualStatistics.tasksWithUncompletedConsumables).hasSize(0) },
            { assertThat(actualStatistics.fulfilledTaskCount).isEqualTo(0) },
            { assertJobEventState(events, JobState.COMPLETED_WITH_WARNINGS) },
            { assertThat(actualStatistics.runningTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.completedTaskCount).isEqualTo(2) },
            { assertThat(actualStatistics.warnTaskCount).isEqualTo(1) },
            { assertThat(actualStatistics.failedTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.fatalTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.skippedTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.canceledTaskCount).isEqualTo(0) },
            { assertThat(jobExecutionUnderTest.isFinished).isTrue() })
    }

    @Test
    fun `test job finish fatal`() {
        task1FailLevel = FailLevel.FATAL
        jobExecutionUnderTest.start()
        jobExecutionUnderTest.queueUp { }
        jobExecutionUnderTest.setTaskStarted(task1)
        jobExecutionUnderTest.setTaskStarted(task3)
        jobExecutionUnderTest.setTaskFailed(task1)
        jobExecutionUnderTest.setTaskCanceled(task3)
        jobExecutionUnderTest.queueUp { }
        jobExecutionUnderTest.finish()
        val actualStatistics = jobExecutionUnderTest.getExecutionStatisticsSnapshot()
        assertAll({ assertThat(actualStatistics.submittedTasks).hasSize(0) },
            { assertThat(actualStatistics.tasksWithUncompletedConsumables).hasSize(1) },
            { assertThat(actualStatistics.fulfilledTaskCount).isEqualTo(0) },
            { assertJobEventState(events, JobState.FATAL) },
            { assertThat(actualStatistics.runningTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.completedTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.warnTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.failedTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.fatalTaskCount).isEqualTo(1) },
            { assertThat(actualStatistics.skippedTaskCount).isEqualTo(1) },
            { assertThat(actualStatistics.canceledTaskCount).isEqualTo(1) },
            { assertThat(jobExecutionUnderTest.isFinished).isTrue() })
    }

    @Test
    fun `test job finish fatal if stuck`() {
        jobExecutionUnderTest.start()
        jobExecutionUnderTest.queueUp { }
        jobExecutionUnderTest.setTaskStarted(task1)
        jobExecutionUnderTest.setTaskStarted(task3)
        jobExecutionUnderTest.setTaskCompleted(task1)
        jobExecutionUnderTest.queueUp { }
        jobExecutionUnderTest.finish()
        val actualStatistics = jobExecutionUnderTest.getExecutionStatisticsSnapshot()
        assertAll({ assertThat(actualStatistics.submittedTasks).hasSize(0) },
            { assertThat(actualStatistics.tasksWithUncompletedConsumables).hasSize(0) },
            { assertThat(actualStatistics.fulfilledTaskCount).isEqualTo(1) },
            { assertJobEventState(events, JobState.FATAL) },
            { assertThat(actualStatistics.runningTaskCount).isEqualTo(1) },
            { assertThat(actualStatistics.completedTaskCount).isEqualTo(1) },
            { assertThat(actualStatistics.warnTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.failedTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.fatalTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.skippedTaskCount).isEqualTo(0) },
            { assertThat(actualStatistics.canceledTaskCount).isEqualTo(0) },
            { assertThat(jobExecutionUnderTest.isFinished).isFalse() })
    }
}
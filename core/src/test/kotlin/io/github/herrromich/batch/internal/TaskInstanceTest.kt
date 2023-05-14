package io.github.herrromich.batch.internal

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.TaskState
import io.github.herrromich.batch.TestTask
import io.github.herrromich.batch.events.internal.TaskEventImpl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.util.concurrent.RunnableFuture

class TaskInstanceTest {

    private lateinit var testTask: TestTask
    private lateinit var testJob: Job
    private lateinit var mockedJobInstance: JobInstance
    private lateinit var taskInstanceUnderTest: TaskInstance

    @BeforeEach
    fun initTaskInstance() {
        testTask = object : TestTask("test-task") {}
        testJob = object : Job {
            override val name = "test-job"
            override val tasks = setOf(testTask)
        }
        mockedJobInstance = mock {
            `when`(it.job).doReturn(testJob)
            `when`(it.changeTaskState(any(), any()))
                .doAnswer { invocation ->
                    val task = invocation.arguments[0] as TestTask
                    val taskState = invocation.arguments[1] as TaskState
                    return@doAnswer TaskEventImpl(task, taskState)
                }
        }
        taskInstanceUnderTest = TaskInstance(mockedJobInstance, testTask)
    }

    @Test
    fun `test qualified task name`() {
        val taskQualifiedName = taskInstanceUnderTest.qualifiedTaskName
        assertThat(taskQualifiedName).isEqualTo("test-job.test-task")
    }

    @Test
    fun `test task submit`() {
        taskInstanceUnderTest.submit()
        verify(mockedJobInstance).changeTaskState(testTask, TaskState.SUBMITTED)
    }

    @Test
    fun `test task execute`() {
        taskInstanceUnderTest.execute()
        verify(mockedJobInstance).setTaskStarted(testTask)
        verify(mockedJobInstance).setTaskCompleted(testTask)
    }

    @Test
    fun `test task cancel`() {
        taskInstanceUnderTest.cancel()
        verify(mockedJobInstance).setTaskCanceled(testTask)
    }

    @Test
    fun `test task fail`() {
        taskInstanceUnderTest.fail()
        verify(mockedJobInstance).setTaskFailed(testTask)
    }

    @Test
    fun `test task change state`() {
        taskInstanceUnderTest.submit()
        taskInstanceUnderTest.setState(TaskState.FATAL)
        assertThat(taskInstanceUnderTest.state).isEqualTo(TaskState.FATAL)
        verify(mockedJobInstance).changeTaskState(testTask, TaskState.FATAL)
    }

    @Test
    fun `test task future property`() {
        assertThat(taskInstanceUnderTest.future).isNull()
        val future = mock<RunnableFuture<Void>>{}
        taskInstanceUnderTest.future = future
        assertThat(taskInstanceUnderTest.future).isEqualTo(future)
    }
}
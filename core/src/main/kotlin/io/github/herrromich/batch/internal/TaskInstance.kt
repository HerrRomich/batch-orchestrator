package io.github.herrromich.batch.internal

import io.github.herrromich.batch.Task
import io.github.herrromich.batch.TaskContext
import io.github.herrromich.batch.TaskState
import io.github.herrromich.batch.events.TaskEvent
import mu.KotlinLogging
import prettyPrint
import java.time.Duration
import java.util.concurrent.RunnableFuture

private val logger = KotlinLogging.logger { }

internal class TaskInstance(override val jobContext: JobExecutionInstance, override val task: Task) :
    TaskContext {
    var event: TaskEvent

    init {
        event = jobContext.changeTaskState(
            task = task,
            state = TaskState.SUBMITTED
        )
    }

    val qualifiedTaskName: String
        get() = "${jobContext.job.jobName}.${task.name}"
    override val state: TaskState
        get() = event.state
    var future: RunnableFuture<Void>? = null

    fun execute() {
        jobContext.setTaskStarted()
        task.execute(this)
        jobContext.setTaskCompleted(this)
    }

    fun cancel() {
        jobContext.setTaskCanceled(this)
    }

    fun fail() {
        jobContext.setTaskFailed(this)
    }

    internal fun changeState(state: TaskState): TaskEvent {
        val prevEvent = event
        event = jobContext.changeTaskState(
            task = task,
            state = state
        )
        val duration = Duration.between(prevEvent.timestamp, event.timestamp)
        logger.info {
            "Task $qualifiedTaskName has changed state ${prevEvent.state} -> ${event.state} after ${duration.prettyPrint()}"
        }
        return event
    }
}

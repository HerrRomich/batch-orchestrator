package com.smushkevich.batch.internal

import com.smushkevich.batch.Task
import com.smushkevich.batch.TaskExecution
import com.smushkevich.batch.TaskExecutionState
import com.smushkevich.batch.events.TaskEvent
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.cast
import mu.KotlinLogging
import prettyPrint
import java.time.Duration
import java.util.concurrent.RunnableFuture

private val logger = KotlinLogging.logger { }

internal class TaskExecutionInstance(override val jobContext: JobExecutionInstance, override val task: Task) :
    TaskExecution {
    var event: TaskEvent

    init {
        event = jobContext.changeTaskState(
            task = task,
            state = TaskExecutionState.QUEUED
        )
    }

    override val taskName: String
        get() = task.taskName
    override val qualfiedTaskName: String
        get() = "${jobContext.job.jobName}.$taskName"
    override val state: TaskExecutionState
        get() = event.state
    var future: RunnableFuture<Void>? = null
    override val events: Flowable<TaskEvent> = jobContext.events.filter {
        it is TaskEvent && it.task == task
    }.cast()

    fun changeState(state: TaskExecutionState): TaskEvent {
        val prevEvent = event
        event = jobContext.changeTaskState(
            task = task,
            state = state
        )
        val duration = Duration.between(prevEvent.timestamp, event.timestamp)
        logger.info {
            "Task $qualfiedTaskName has changed state ${prevEvent.state} -> ${event.state} after ${duration.prettyPrint()}"
        }
        return event
    }
}

package io.github.herrromich.batch.internal

import io.github.herrromich.batch.Task
import io.github.herrromich.batch.TaskContext
import io.github.herrromich.batch.TaskState
import io.github.herrromich.batch.events.TaskEvent
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.cast
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

    val qualfiedTaskName: String
        get() = "${jobContext.job.jobName}.${task.taskName}"
    override val state: TaskState
        get() = event.state
    var future: RunnableFuture<Void>? = null
    override val events: Flowable<TaskEvent> = jobContext.events.filter {
        it is TaskEvent && it.task == task
    }.cast()

    fun changeState(state: TaskState): TaskEvent {
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

package io.github.herrromich.batch.internal

import io.github.herrromich.batch.Task
import io.github.herrromich.batch.TaskContext
import io.github.herrromich.batch.TaskState
import io.github.herrromich.batch.events.TaskEvent
import mu.KotlinLogging
import java.time.Duration
import java.util.concurrent.RunnableFuture

private val logger = KotlinLogging.logger { }

class TaskInstance(override val jobContext: JobInstance, override val task: Task) :
    TaskContext {
    private lateinit var event: TaskEvent

    fun submit() {
        event = jobContext.changeTaskState(
            task = task,
            state = TaskState.SUBMITTED
        )
    }

    val qualifiedTaskName: String
        get() = "${jobContext.job.name}.${task.name}"
    val state: TaskState
        get() = event.state
    var future: RunnableFuture<Void>? = null

    fun execute() {
        jobContext.setTaskStarted(task)
        task.execute(this)
        jobContext.setTaskCompleted(task)
    }

    fun cancel() {
        jobContext.setTaskCanceled(task)
    }

    fun fail() {
        jobContext.setTaskFailed(task)
    }

    internal fun setState(state: TaskState) {
        val newEvent = jobContext.changeTaskState(
            task = task,
            state = state
        )
        changeState(newEvent)
    }

    private fun changeState(newEvent: TaskEvent) {
        val prevEvent = event
        event = newEvent
        val duration = Duration.between(prevEvent.timestamp, event.timestamp)
        logger.info {
            "Task $qualifiedTaskName has changed state ${prevEvent.state} -> ${event.state} after ${duration.prettyPrint()}"
        }
    }


    fun start() {
        setProgress(0.0)
    }

    fun complete() {
        setProgress(100.0)
        setState(TaskState.COMPLETED)
    }

    override fun setProgress(progress: Double) {
        val newEvent = jobContext.setTaskProgress(
            task = task,
            progress = progress,
        )
        changeState(newEvent)
    }
}

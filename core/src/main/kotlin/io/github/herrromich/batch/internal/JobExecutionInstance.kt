package io.github.herrromich.batch.internal

import io.github.herrromich.batch.*
import io.github.herrromich.batch.events.ExecutionEvent
import io.github.herrromich.batch.events.JobEvent
import io.github.herrromich.batch.events.TaskEvent
import mu.KotlinLogging
import prettyPrint
import java.time.Duration
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

private val logger = KotlinLogging.logger {  }

abstract class JobExecutionInstance private constructor(
    override val job: Job,
    val future: CompletableFuture<Void>
) : JobContext,
    Future<Void> by future {
    constructor(job: Job) : this(job, CompletableFuture())

    override val id = UUID.randomUUID()!!

    private lateinit var event: JobEvent
    private lateinit var queuedTaskExecutions: MutableMap<Task, TaskInstance>
    private lateinit var tasksWithUncompletedConsumables: MutableMap<Task, MutableSet<String>>
    private lateinit var uncompletedProducibles: MutableMap<String, MutableSet<TaskInstance>>
    private lateinit var submittedTaskExecutions: MutableSet<TaskInstance>
    private var fulfilledTaskCount = 0
    private var runningTaskCount = 0
    private var completedTaskCount = 0
    private var warnTaskCount = 0
    private var failedTaskCount = 0
    private var fatalTaskCount = 0
    private var skippedTaskCount = 0
    private var canceledTaskCount = 0

    internal fun start() {
        if (::event.isInitialized) {
            throw OrchestratorException("Job instance is already started!")
        }
        event = JobEvent(job, JobExecutionState.EXECUTING)
        queuedTaskExecutions = job.tasks
            .associateWithTo(mutableMapOf()) { task -> TaskInstance(this, task) }
        tasksWithUncompletedConsumables = queuedTaskExecutions.map { it.key }
            .associateWithTo(mutableMapOf()) { it.consumables.toMutableSet() }
        uncompletedProducibles = queuedTaskExecutions.asSequence().map { it.value }
            .flatMap { taskExecution -> taskExecution.task.producibles.asSequence().map { it to taskExecution } }
            .groupBy({ it.first }, { it.second })
            .mapValuesTo(mutableMapOf()) { it.value.toMutableSet() }
        submittedTaskExecutions = mutableSetOf<TaskInstance>()
    }

    fun changeTaskState(task: Task, state: TaskState): TaskEvent {
        val event = TaskEvent(task, state)
        nextEvent(event)
        return event
    }

    protected abstract fun nextEvent(event: ExecutionEvent)

    protected abstract fun lastEvent(event: ExecutionEvent)

    internal val isFinished: Boolean
        @Synchronized
        get() = submittedTaskExecutions.isEmpty() && tasksWithUncompletedConsumables.filterValues { it.isEmpty() }
            .isEmpty()

    @Synchronized
    internal fun submit(executeTask: (taskExecution: TaskInstance) -> Unit) {
        val fulfilledTasks = tasksWithUncompletedConsumables.filterValues { it.isEmpty() }.keys
        if (fulfilledTasks.isEmpty()) {
            return
        }
        tasksWithUncompletedConsumables -= fulfilledTasks
        logger.debug { "Following tasks in job \"${job.jobName}\" are fulfilled: ${fulfilledTasks.map { it.name }}" }
        fulfilledTasks.sortedByDescending { it.priority }
            .forEach {
                val taskExecution = queuedTaskExecutions.remove(it)!!
                submittedTaskExecutions.add(taskExecution)
                fulfilledTaskCount++
                taskExecution.changeState(TaskState.FULFILLED)
                logStatistics()
                executeTask(taskExecution)
            }
    }

    @Synchronized
    internal fun setTaskStarted() {
        runningTaskCount++
        fulfilledTaskCount--
        logStatistics()
    }

    @Synchronized
    internal fun setTaskCompleted(taskExecution: TaskInstance) {
        submittedTaskExecutions.remove(taskExecution)
        runningTaskCount--
        completedTaskCount++
        logStatistics()
        reorganizeGraph(taskExecution)
    }

    @Synchronized
    internal fun setTaskCanceled(taskExecution: TaskInstance) {
        if (taskExecution.event.state == TaskState.RUNNING) {
            runningTaskCount--
            canceledTaskCount++
            taskExecution.changeState(TaskState.CANCELED)
            logStatistics()
            submittedTaskExecutions.remove(taskExecution)
        }
    }


    @Synchronized
    internal fun setTaskFailed(taskExecution: TaskInstance) {
        submittedTaskExecutions.remove(taskExecution)
        runningTaskCount--
        when (taskExecution.task.failLevel) {
            FailLevel.WARN -> setTaskFailedWithWarn(taskExecution)
            FailLevel.ERROR -> setTaskFailedWithError(taskExecution)
            FailLevel.FATAL -> setTaskFailedWithFatal(taskExecution)
        }
    }

    private fun setTaskFailedWithWarn(taskExecution: TaskInstance) {
        warnTaskCount++
        taskExecution.changeState(TaskState.WARN)
        logStatistics()
        reorganizeGraph(taskExecution)
    }

    private fun setTaskFailedWithError(taskExecution: TaskInstance) {
        failedTaskCount++
        taskExecution.changeState(TaskState.ERROR)
        logStatistics()
    }

    private fun setTaskFailedWithFatal(taskExecution: TaskInstance) {
        fatalTaskCount++
        taskExecution.changeState(TaskState.FATAL)
        logStatistics()
        submittedTaskExecutions.forEach {
            it.future?.cancel(true)
            if (it.event.state == TaskState.FULFILLED) {
                fulfilledTaskCount--
                canceledTaskCount++
                it.changeState(TaskState.CANCELED)
                logStatistics()
            }
        }
        submittedTaskExecutions.removeIf { it.event.state == TaskState.CANCELED }
        queuedTaskExecutions.forEach { _, taskExecution ->
            skippedTaskCount++
            taskExecution.changeState(TaskState.SKIPPED)
        }
        queuedTaskExecutions.clear()
    }

    internal fun finish() {
        queuedTaskExecutions.values
            .forEach {
                it.changeState(TaskState.SKIPPED)
            }
        if (failedTaskCount != 0) {
            setState(JobExecutionState.ERROR)
            future.completeExceptionally(OrchestratorException("Job execution is failed."))
        } else if (fatalTaskCount != 0) {
            setState(JobExecutionState.FATAL)
            future.completeExceptionally(OrchestratorException("Job execution is fatally failed."))
        } else if (submittedTaskExecutions.isNotEmpty()) {
            setState(JobExecutionState.FATAL)
            future.completeExceptionally(OrchestratorException("Job execution has stuck."))
        } else {
            setState(JobExecutionState.COMPLETED)
            future.complete(null)
        }
    }

    private fun reorganizeGraph(taskExecution: TaskInstance) {
        uncompletedProducibles.forEach { _, tasks ->
            tasks.remove(taskExecution)
        }
        val completedProducibles = uncompletedProducibles.filterValues(Set<TaskInstance>::isEmpty).keys
        if (completedProducibles.isNotEmpty()) {
            logger.debug { "Following resources of job ${job.jobName} have been fulfilled: $completedProducibles" }
        }
        uncompletedProducibles -= completedProducibles
        tasksWithUncompletedConsumables.forEach { _, consumables ->
            consumables -= completedProducibles
        }
    }

    private fun setState(state: JobExecutionState): JobEvent {
        val prevEvent = event
        event = JobEvent(job, state)
        val msg = when (state) {
            JobExecutionState.ERROR -> "is failed"
            JobExecutionState.FATAL -> "is fatally failed"
            JobExecutionState.COMPLETED -> "is completed"
            else -> throw OrchestratorException("Cannot finish job with state $state.")
        }
        lastEvent(event)
        val duration = Duration.between(prevEvent.timestamp, event.timestamp)
        logger.info {
            "Job '${job.jobName} $msg after ${duration.prettyPrint()}"
        }
        return event
    }

    private fun logStatistics() {
        logger.info {
            "Job '${job.jobName}' statistics: " +
                    "fulfilled -> $fulfilledTaskCount; " +
                    "running -> $runningTaskCount; " +
                    "done -> $completedTaskCount / ${job.tasks.count()}"
        }
    }

    data class StateChange(val from: JobEvent?, val to: JobEvent?)
}
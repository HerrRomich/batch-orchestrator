package io.github.herrromich.batch.internal

import io.github.herrromich.batch.*
import io.github.herrromich.batch.events.ExecutionEvent
import io.github.herrromich.batch.events.JobEvent
import io.github.herrromich.batch.events.TaskEvent
import io.github.herrromich.batch.events.internal.JobEventImpl
import io.github.herrromich.batch.events.internal.TaskEventImpl
import io.github.herrromich.batch.events.internal.TaskRunningEventImpl
import mu.KotlinLogging
import java.time.Duration
import java.util.*

private val logger = KotlinLogging.logger { }

abstract class JobInstance : JobContext {

    override val id = UUID.randomUUID()!!

    private lateinit var event: JobEvent
    private lateinit var submittedTaskInstances: MutableMap<Task, TaskInstance>
    private lateinit var tasksWithUncompletedConsumables: MutableMap<TaskInstance, MutableSet<String>>
    private lateinit var uncompletedProducibles: MutableMap<String, MutableSet<TaskInstance>>
    private lateinit var queuedTaskInstances: MutableMap<Task, TaskInstance>
    private var fulfilledTaskCount = 0
    private var runningTaskCount = 0
    private var completedTaskCount = 0
    private var warnTaskCount = 0
    private var failedTaskCount = 0
    private var fatalTaskCount = 0
    private var skippedTaskCount = 0
    private var canceledTaskCount = 0

    @Synchronized
    internal fun start() {
        if (::event.isInitialized) {
            throw OrchestratorException("Job instance is already started!")
        }
        validate()
        setState(JobState.EXECUTING)
        submittedTaskInstances = job.tasks
            .associateWithTo(mutableMapOf()) { task ->
                val taskInstance = createTaskInstance(task)
                taskInstance.submit()
                taskInstance
            }
        tasksWithUncompletedConsumables = submittedTaskInstances.map { it.value }
            .associateWithTo(mutableMapOf()) { it.task.consumables.toMutableSet() }
        uncompletedProducibles = submittedTaskInstances.asSequence().map { it.value }
            .flatMap { taskInstance -> taskInstance.task.producibles.asSequence().map { it to taskInstance } }
            .groupBy({ it.first }, { it.second })
            .mapValuesTo(mutableMapOf()) { it.value.toMutableSet() }
        queuedTaskInstances = mutableMapOf()
    }

    private fun validate() {
        val duplicates = job.tasks.groupBy { it.name }.filter { it.value.size > 1 }.toMap()
        if (duplicates.isNotEmpty()) {
            throw OrchestratorException("There ara tasks with duplicated names: ${duplicates.keys}")
        }
        JobValidator.checkOrphans(job)
        JobValidator.checkCycles(job)
    }


    private fun createTaskInstance(task: Task) = TaskInstance(this, task)

    @Synchronized
    override fun getExecutionStatisticsSnapshot(): JobExecutionStatistics = object : JobExecutionStatistics {
        override val submittedTasks = this@JobInstance.submittedTaskInstances.values.map(TaskInstance::qualifiedTaskName).toSet()
        override val tasksWithUncompletedConsumables =
            this@JobInstance.tasksWithUncompletedConsumables.filter { it.value.isNotEmpty() }
                .mapKeys { it.key.qualifiedTaskName }
                .mapValues { it.value.toSet() }.toMap()
        override val uncompletedProducibles = this@JobInstance.uncompletedProducibles.mapValues { entry ->
            entry.value.map { taskInstance -> taskInstance.qualifiedTaskName }.toSet()
        }.toMap()
        override val queuedTasks = this@JobInstance.queuedTaskInstances.keys.map(Task::name).toSet()
        override val fulfilledTaskCount = this@JobInstance.fulfilledTaskCount
        override val runningTaskCount = this@JobInstance.runningTaskCount
        override val completedTaskCount = this@JobInstance.completedTaskCount
        override val warnTaskCount = this@JobInstance.warnTaskCount
        override val failedTaskCount = this@JobInstance.failedTaskCount
        override val fatalTaskCount = this@JobInstance.fatalTaskCount
        override val skippedTaskCount = this@JobInstance.skippedTaskCount
        override val canceledTaskCount = this@JobInstance.canceledTaskCount
    }

    fun changeTaskState(task: Task, state: TaskState): TaskEvent {
        val event = TaskEventImpl(task, state)
        nextEvent(event)
        return event
    }

    fun setTaskProgress(task: Task, progress: Double): TaskEvent {
        val event = TaskRunningEventImpl(task, progress)
        nextEvent(event)
        return event
    }

    protected abstract fun nextEvent(event: ExecutionEvent)

    internal val isFinished: Boolean
        @Synchronized
        get() = queuedTaskInstances.isEmpty() && tasksWithUncompletedConsumables.filterValues { it.isEmpty() }
            .isEmpty()

    @Synchronized
    internal fun queueUp(executeTask: (taskInstance: TaskInstance) -> Unit) {
        val fulfilledTasks = tasksWithUncompletedConsumables.filterValues { it.isEmpty() }.keys
        if (fulfilledTasks.isEmpty()) {
            return
        }
        tasksWithUncompletedConsumables -= fulfilledTasks
        logger.debug { "Following tasks in job \"${job.name}\" are fulfilled: ${fulfilledTasks.map { it.task.name }}" }
        fulfilledTasks.sortedByDescending { it.task.priority }
            .forEach {
                submittedTaskInstances.compute(it.task) { _, taskInstance ->
                    if (taskInstance == null) throw OrchestratorException("Fatal error. There is no submitted task instance for task ${it.task.name}")
                    if (taskInstance.state != TaskState.SUBMITTED) throw OrchestratorException("Fatal error. Task ${it.task.name} cannot be fulfilled as it is not submitted.")
                    queuedTaskInstances[it.task] = taskInstance
                    fulfilledTaskCount++
                    taskInstance.setState(TaskState.FULFILLED)
                    logStatistics()
                    executeTask(taskInstance)
                    null
                }
            }
    }

    @Synchronized
    internal fun setTaskStarted(task: Task) {
        val taskInstance = queuedTaskInstances[task]
            ?: throw OrchestratorException("Fatal error. There is no queued instance of task \"${task.name}\".")
        if (taskInstance.state != TaskState.FULFILLED) throw OrchestratorException("Fatal error. Task \"${task.name}\" cannot be started as it is not fulfilled.")
        runningTaskCount++
        fulfilledTaskCount--
        taskInstance.start()
        logStatistics()
    }

    @Synchronized
    internal fun setTaskCompleted(task: Task) {
        val taskInstance = queuedTaskInstances.remove(task)
            ?: throw OrchestratorException("Fatal error. There is no queued instance of task \"${task.name}\".")
        if (taskInstance.state != TaskState.RUNNING) throw OrchestratorException("Fatal error. Task \"${task.name}\" cannot be completed as it is not running.")
        runningTaskCount--
        completedTaskCount++
        taskInstance.complete()
        logStatistics()
        reorganizeGraph(taskInstance)
    }


    @Synchronized
    internal fun setTaskCanceled(task: Task) {
        val taskInstance = queuedTaskInstances.remove(task)
            ?: throw OrchestratorException("Fatal error. There is no queued instance of task \"${task.name}\".")
        if (taskInstance.state != TaskState.RUNNING) throw OrchestratorException("Fatal error. Task \"${task.name}\" cannot be canceled as it is not running.")
        runningTaskCount--
        canceledTaskCount++
        taskInstance.setState(TaskState.CANCELED)
        logStatistics()
    }

    @Synchronized
    internal fun setTaskFailed(task: Task) {
        val taskInstance = queuedTaskInstances.remove(task)
            ?: throw OrchestratorException("Fatal error. There is no queued instance of task \"${task.name}\".")
        if (taskInstance.state != TaskState.RUNNING) throw OrchestratorException("Fatal error. Task \"${task.name}\" cannot be failed as it is not running.")
        runningTaskCount--
        when (taskInstance.task.failLevel) {
            FailLevel.WARN -> setTaskFailedWithWarn(taskInstance)
            FailLevel.ERROR -> setTaskFailedWithError(taskInstance)
            FailLevel.FATAL -> setTaskFailedWithFatal(taskInstance)
        }
    }

    private fun setTaskFailedWithWarn(taskInstance: TaskInstance) {
        warnTaskCount++
        taskInstance.setState(TaskState.WARN)
        logStatistics()
        reorganizeGraph(taskInstance)
    }

    private fun setTaskFailedWithError(taskInstance: TaskInstance) {
        failedTaskCount++
        taskInstance.setState(TaskState.ERROR)
        logStatistics()
    }

    private fun setTaskFailedWithFatal(taskInstance: TaskInstance) {
        fatalTaskCount++
        taskInstance.setState(TaskState.FATAL)
        logStatistics()
        queuedTaskInstances.forEach { (_, queuedTaskInstance) ->
            queuedTaskInstance.future?.cancel(true)
            if (queuedTaskInstance.state == TaskState.FULFILLED) {
                fulfilledTaskCount--
                canceledTaskCount++
                queuedTaskInstance.setState(TaskState.CANCELED)
                logStatistics()
            }
        }
        queuedTaskInstances -= queuedTaskInstances.filterValues { it.state == TaskState.CANCELED }.keys
        submittedTaskInstances.forEach { (_, submittedTaskInstance) ->
            skippedTaskCount++
            submittedTaskInstance.setState(TaskState.SKIPPED)
        }
        submittedTaskInstances.clear()
    }

    internal fun finish() {
        submittedTaskInstances.values
            .forEach {
                it.setState(TaskState.SKIPPED)
            }
        if (failedTaskCount != 0) {
            setState(JobState.ERROR)
            completeExceptionally(OrchestratorException("Job execution is failed."))
        } else if (fatalTaskCount != 0) {
            setState(JobState.FATAL)
            completeExceptionally(OrchestratorException("Job execution is fatally failed."))
        } else if (queuedTaskInstances.isNotEmpty()) {
            setState(JobState.FATAL)
            completeExceptionally(OrchestratorException("Job execution has stuck."))
        } else if (warnTaskCount != 0) {
            setState(JobState.COMPLETED_WITH_WARNINGS)
            complete()
        } else {
            setState(JobState.COMPLETED)
            complete()
        }
    }

    abstract fun complete()

    abstract fun completeExceptionally(ex: OrchestratorException)

    private fun reorganizeGraph(taskInstance: TaskInstance) {
        uncompletedProducibles.forEach { (_, tasks) ->
            tasks.remove(taskInstance)
        }
        val completedProducibles = uncompletedProducibles.filterValues(Set<TaskInstance>::isEmpty).keys
        if (completedProducibles.isNotEmpty()) {
            logger.debug { "Following resources of job ${job.name} have been fulfilled: $completedProducibles" }
        }
        uncompletedProducibles -= completedProducibles
        tasksWithUncompletedConsumables.forEach { (_, consumables) ->
            consumables -= completedProducibles
        }
    }

    private fun setState(state: JobState): JobEvent {
        val prevTimestamp = if (::event.isInitialized) event.timestamp else null
        event = JobEventImpl(job, state)
        val msg = when (state) {
            JobState.EXECUTING -> "is executing"
            JobState.ERROR -> "is failed"
            JobState.FATAL -> "is fatally failed"
            JobState.COMPLETED_WITH_WARNINGS -> "is completed with warnings"
            JobState.COMPLETED -> "is completed"
        }
        nextEvent(event)
        val duration = Duration.between(prevTimestamp ?: event.timestamp, event.timestamp)
        logger.info {
            "Job '${job.name} $msg after ${duration.prettyPrint()}"
        }
        return event
    }

    protected open fun logStatistics() {
        logger.info {
            """Job '${job.name}' statistics: 
fulfilled -> $fulfilledTaskCount 
  running -> $runningTaskCount
     done -> $completedTaskCount / ${job.tasks.count()}"""
        }
    }

    companion object {
        private val JOB_INSTANCE_PROVIDER: JobInstanceProvider by lazy {
            ServiceLoader.load(JobInstanceProvider::class.java).run {
                reload()
                try {
                    single()
                } catch (e: NoSuchElementException) {
                    throw OrchestratorException("There are no registered classes of interface \"${JobInstanceProvider::class.java.name}\".")
                } catch (e: IllegalArgumentException) {
                    throw OrchestratorException("There are more then one registered ${JobInstanceProvider::class.java.name} classes.")
                }
            }

        }

        @JvmStatic
        fun instance(job: Job) = JOB_INSTANCE_PROVIDER.provideInstance(job)
    }
}
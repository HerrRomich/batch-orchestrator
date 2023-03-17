package io.github.herrromich.batch.internal

import io.github.herrromich.batch.*
import io.github.herrromich.batch.events.ExecutionEvent
import io.github.herrromich.batch.events.JobEvent
import io.github.herrromich.batch.events.TaskEvent
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.cast
import io.reactivex.rxjava3.processors.ReplayProcessor
import mu.KotlinLogging
import prettyPrint
import java.time.Duration
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

private val logger = KotlinLogging.logger {}

internal class JobExecutionInstance private constructor(
    override val job: Job,
    val future: CompletableFuture<Void>
) : JobContext,
    Future<Void> by future {
    constructor(job: Job) : this(job, CompletableFuture())

    var event: JobEvent = JobEvent(job, JobExecutionState.EXECUTING)
    override val events = ReplayProcessor.create<ExecutionEvent>().apply { onNext(event) }
    override val id = UUID.randomUUID()
    val queuedTaskExecutions = job.tasks
        .associateWithTo(mutableMapOf()) { task -> TaskInstance(this, task) }
    val tasksWithUncompletedConsumables = queuedTaskExecutions.map { it.key }
        .associateWithTo(mutableMapOf()) { it.consumables.toMutableSet() }
    val uncompletedProducibles = queuedTaskExecutions.asSequence().map { it.value }
        .flatMap { taskExecution -> taskExecution.task.producibles.asSequence().map { it to taskExecution } }
        .groupBy({ it.first }, { it.second })
        .mapValuesTo(mutableMapOf()) { it.value.toMutableSet() }
    val submittedTaskExecutions = mutableSetOf<TaskInstance>()
    var fulfilledTaskCount = 0
    var runningTaskCount = 0
    var completedTaskCount = 0
    var warnTaskCount = 0
    var failedTaskCount = 0
    var fatalTaskCount = 0
    var skippedTaskCount = 0
    var canceledTaskCount = 0

    fun changeTaskState(task: Task, state: TaskState): TaskEvent {
        val event = TaskEvent(task, state)
        events.onNext(event)
        return event
    }

    fun finish(state: JobExecutionState): JobEvent {
        val prevEvent = event
        event = JobEvent(job, state)
        val msg = when( state) {
            JobExecutionState.ERROR -> "is failed"
            JobExecutionState.FATAL -> "is fatally failed"
            JobExecutionState.COMPLETED -> "is completed"
            else -> throw OrchestratorException("Cannot finish job with state $state.")
        }
        events.onNext(event)
        events.onComplete()
        val duration = Duration.between(prevEvent.timestamp, event.timestamp)
        logger.info {
            "Job '${job.jobName} $msg after ${duration.prettyPrint()}"
        }
        return event
    }

    fun getLastStateChange() =
        Flowable.just(1)
            .switchMap {
                events
                    .filter { it is JobEvent }
                    .cast<JobEvent>()
                    .scan(StateChange(null, null)) { prev, curr ->
                        StateChange(prev.to, curr)
                    }
            }
            .blockingLast(StateChange(null, null))

    data class StateChange(val from: JobEvent?, val to: JobEvent?)
}

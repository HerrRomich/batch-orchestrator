package io.github.herrromich.batch.concurrent.internal

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.concurrent.ConcurrentOrchestratorConfiguration
import io.github.herrromich.batch.internal.BaseOrchestrator
import io.github.herrromich.batch.internal.TaskInstance
import mu.KotlinLogging
import java.util.concurrent.Executor
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger { }

internal class ConcurrentOrchestratorImpl(jobs: Set<Job>, configuration: ConcurrentOrchestratorConfiguration) :
    BaseOrchestrator(jobs) {

    private val executor: Executor by lazy {
        ThreadPoolExecutor(
            configuration.threadPoolSize,
            configuration.threadPoolSize,
            1,
            TimeUnit.MINUTES,
            PriorityBlockingQueue(16)
        )
    }

    override fun executeTask(taskInstance: TaskInstance) {
        val future = FutureTaskWithPriority(taskInstance.task.priority)
        {
            try {
                taskInstance.execute()
            } catch (e: InterruptedException) {
                taskInstance.cancel()
            } catch (e: Throwable) {
                logger.error(e) { "Task ${taskInstance.qualifiedTaskName} is failed." }
                taskInstance.fail()
            }
            submit(taskInstance.jobContext)
        }
        taskInstance.future = future
        executor.execute(future)
    }

}
package io.github.herrromich.batch.concurrent.internal

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.internal.BaseOrchestrator
import io.github.herrromich.batch.internal.TaskInstance
import mu.KotlinLogging
import java.util.concurrent.Executor

private val logger = KotlinLogging.logger { }

internal class ConcurrentOrchestratorImpl(jobs: Set<Job>, private val executor: Executor) :
    BaseOrchestrator(jobs) {

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
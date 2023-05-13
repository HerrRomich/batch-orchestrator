package io.github.herrromich.batch.spi

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.OrchestratorException
import io.github.herrromich.batch.internal.JobExecutionInstance
import io.github.herrromich.batch.internal.SimpleJobExecutionInstance
import mu.KotlinLogging
import java.util.*

private val logger = KotlinLogging.logger { }

interface JobExecutionInstanceProvider {

    fun provide(job: Job): JobExecutionInstance

    companion object {
        private val JOB_EXECUTION_INSTANCE_SERVICE_LOADER =
            ServiceLoader.load(JobExecutionInstanceProvider::class.java).apply {
                reload()
                if (count() > 1) {
                    throw OrchestratorException("There are more then one registered ${JobExecutionInstanceProvider::class.java} classes.")
                }
            }

        fun provide(job: Job): JobExecutionInstance {
            val jobExecutionInstanceProvider = JOB_EXECUTION_INSTANCE_SERVICE_LOADER.singleOrNull();
            val jobExecutionInstance =
                jobExecutionInstanceProvider?.provide(job) ?: SimpleJobExecutionInstance(job)
            logger.trace { "Orchestrator is initialized: $jobExecutionInstance" }
            return jobExecutionInstance
        }
    }
}
package io.github.herrromich.batch.spring

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.Orchestrator
import io.github.herrromich.batch.concurrent.ConcurrentOrchestratorConfiguration
import io.github.herrromich.batch.concurrent.ExecutorOrchestratorProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
open class JobOrchestratorConfiguration {

    @Bean("batchOrchestratorExecutor")
    open fun batchOrchestratorExecutor(
        @Autowired(required = false)
        config: ConcurrentOrchestratorConfiguration?
    ): TaskExecutor {
        val threadPoolTaskExecutor = ThreadPoolTaskExecutor()
        val threadPoolSize = config?.threadPoolSize ?: 10
        threadPoolTaskExecutor.corePoolSize = threadPoolSize
        threadPoolTaskExecutor.maxPoolSize = threadPoolSize
        return threadPoolTaskExecutor
    }

    @Bean
    open fun orchestrator(
        tasks: Set<Task>,
        @Qualifier("batchOrchestratorExecutor")
        taskExecutor: TaskExecutor
    ): Orchestrator {
        val jobs = tasks.groupBy { it.jobName }.map {
            object : Job {
                override val name = it.key
                override val tasks = tasks.toSet()
            }
        }.toSet()
        return ExecutorOrchestratorProvider.provideOrchestrator(jobs, taskExecutor)
    }

}
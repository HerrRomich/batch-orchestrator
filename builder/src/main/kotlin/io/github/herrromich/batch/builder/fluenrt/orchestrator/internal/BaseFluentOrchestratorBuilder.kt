package io.github.herrromich.batch.builder.fluenrt.orchestrator.internal

import io.github.herrromich.batch.*
import io.github.herrromich.batch.builder.fluenrt.orchestrator.*
import io.github.herrromich.batch.builder.internal.BaseBuilder

abstract class BaseFluentOrchestratorBuilder : BaseBuilder(), FluentOrchestratorBuilder,
    FluentOrchestratorJobInterfaceBuilder, FluentOrchestratorJobConfigBuilder, FluentOrchestratorJobAndTaskBuilder,
    FluentOrchestratorJobAndTaskConfigBuilder, FluentOrchestratorJobAndTaskInterfaceBuilder {
    override fun configuration(configuration: OrchestratorConfiguration): FluentOrchestratorBuilder {
        this.configuration = configuration
        return this
    }

    override fun job(name: String): FluentOrchestratorJobConfigBuilder {
        setJob(name)
        return this
    }

    override fun job(job: Job): FluentOrchestratorJobInterfaceBuilder {
        setJob(job)
        return this
    }

    override fun task(name: String): FluentOrchestratorJobAndTaskConfigBuilder {
        setTask(name)
        return this
    }

    override fun task(task: Task): FluentOrchestratorJobAndTaskInterfaceBuilder {
        setTask(task)
        return this
    }

    override fun andJob(name: String): FluentOrchestratorJobConfigBuilder {
        setJob(name)
        return this
    }

    override fun andJob(job: Job): FluentOrchestratorJobInterfaceBuilder {
        setJob(job)
        return this
    }

    override fun and(): FluentOrchestratorJobAndTaskBuilder {
        return this
    }

    override fun andTask(name: String): FluentOrchestratorJobAndTaskConfigBuilder {
        setTask(name)
        return this
    }

    override fun andTask(task: Task): FluentOrchestratorJobAndTaskInterfaceBuilder {
        setTask(task)
        return this
    }

    override fun priority(priority: Int): FluentOrchestratorTaskConfigBuilder {
        setPriority(priority)
        return this
    }

    override fun failLevel(failLevel: FailLevel): FluentOrchestratorTaskConfigBuilder {
        setFailLevel(failLevel)
        return this
    }

    override fun consumables(vararg consumables: String): FluentOrchestratorTaskConfigBuilder {
        setConsumables(*consumables)
        return this
    }

    override fun consumables(consumables: Collection<String>): FluentOrchestratorTaskConfigBuilder {
        setConsumables(consumables)
        return this
    }

    override fun producibles(vararg producibles: String): FluentOrchestratorTaskConfigBuilder {
        setProducibles(*producibles)
        return this
    }

    override fun producibles(producibles: Collection<String>): FluentOrchestratorTaskConfigBuilder {
        setProducibles(producibles)
        return this
    }

    override fun runnable(runnable: Consumer): FluentOrchestratorTaskConfigBuilder {
        setRunnable(runnable)
        return this
    }
    override fun build(): Orchestrator {
        finalizeJob()
        return createOrchestrator()
    }

    abstract fun createOrchestrator(): Orchestrator
}

package io.github.herrromich.batch.builder.nested.orchestrator.internal

import io.github.herrromich.batch.*
import io.github.herrromich.batch.builder.internal.BaseBuilder
import io.github.herrromich.batch.builder.nested.orchestrator.*

internal abstract class NestedBaseOrchestratorBuilder : BaseBuilder(), NestedOrchestratorBuilder,
    NestedOrchestratorJobBuilder, NestedOrchestratorJobInterfaceBuilder, NestedOrchestratorTaskConfigBuilder {

    private val taskBuilder = NestedOrchestratorTaskBuilder(this)
    override fun configuration(configuration: OrchestratorConfiguration): NestedOrchestratorBuilder {
        this.configuration = configuration
        return this
    }

    override fun job(
        name: String,
        jobProvider: (jobBuilder: NestedOrchestratorJobBuilder) -> NestedOrchestratorTaskInterfaceBuilder
    ): NestedOrchestratorJobInterfaceBuilder {
        setJob(name)
        if (jobProvider(this) != taskBuilder) throw OrchestratorException("Wrong result. Should chain calls to job builder")
        return this
    }

    override fun job(job: Job): NestedOrchestratorJobInterfaceBuilder {
        setJob(job)
        return this
    }

    override fun task(
        name: String,
        taskProvider: (taskBuilder: NestedOrchestratorTaskConfigBuilder) -> NestedOrchestratorTaskConfigBuilder
    ): NestedOrchestratorTaskInterfaceBuilder {
        setTask(name)
        if (taskProvider(this) != this) throw OrchestratorException("Wrong result. Should chain calls to task builder")
        return taskBuilder
    }

    override fun task(task: Task): NestedOrchestratorTaskInterfaceBuilder {
        setTask(task)
        return taskBuilder
    }

    override fun andJob(
        name: String,
        jobProvider: (jobBuilder: NestedOrchestratorJobBuilder) -> NestedOrchestratorTaskInterfaceBuilder
    ): NestedOrchestratorJobInterfaceBuilder {
        setJob(name)
        if (jobProvider(this) != taskBuilder) throw OrchestratorException("Wrong result. Should chain calls to job builder")
        return this
    }

    override fun andJob(job: Job): NestedOrchestratorJobInterfaceBuilder {
        setJob(job)
        return this
    }

    fun andTask(
        name: String,
        taskProvider: (taskBuilder: NestedOrchestratorTaskConfigBuilder) -> NestedOrchestratorTaskConfigBuilder
    ): NestedOrchestratorTaskInterfaceBuilder {
        setTask(name)
        if (taskProvider(this) != this) throw OrchestratorException("Wrong result. Should chain calls to task builder")
        return taskBuilder
    }

    fun andTask(task: Task): NestedOrchestratorTaskInterfaceBuilder {
        setTask(task)
        return taskBuilder
    }

    override fun and(): NestedOrchestratorBuilder {
        return this
    }

    override fun none(): NestedOrchestratorTaskInterfaceBuilder {
        return taskBuilder
    }

    override fun priority(priority: Int): NestedOrchestratorTaskConfigBuilder {
        setPriority(priority)
        return this
    }

    override fun failLevel(failLevel: FailLevel): NestedOrchestratorTaskConfigBuilder {
        setFailLevel(failLevel)
        return this
    }

    override fun consumables(vararg consumables: String): NestedOrchestratorTaskConfigBuilder {
        setConsumables(*consumables)
        return this
    }

    override fun consumables(consumables: Collection<String>): NestedOrchestratorTaskConfigBuilder {
        setConsumables(consumables)
        return this
    }

    override fun producibles(vararg producibles: String): NestedOrchestratorTaskConfigBuilder {
        setProducibles(*producibles)
        return this
    }

    override fun producibles(producibles: Collection<String>): NestedOrchestratorTaskConfigBuilder {
        setProducibles(producibles)
        return this
    }

    override fun runnable(runnable: Consumer): NestedOrchestratorTaskConfigBuilder {
        setRunnable(runnable)
        return this
    }

    override fun build(): Orchestrator {
        finalizeJob()
        return createOrchestrator()
    }

    abstract fun createOrchestrator(): Orchestrator
}

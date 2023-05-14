package io.github.herrromich.batch.builder.nested.standalone.internal

import io.github.herrromich.batch.FailLevel
import io.github.herrromich.batch.Job
import io.github.herrromich.batch.Task
import io.github.herrromich.batch.builder.internal.BaseBuilder
import io.github.herrromich.batch.builder.nested.standalone.NestedStandaloneJobBuilder
import io.github.herrromich.batch.builder.nested.standalone.NestedStandaloneTaskConfigBuilder
import io.github.herrromich.batch.builder.nested.standalone.NestedStandaloneTaskInterfaceBuilder
import io.github.herrromich.batch.config.JobConfig

internal class NestedStandaloneJobBuilderImpl(jobName: String) :
    BaseBuilder(),
    NestedStandaloneJobBuilder, NestedStandaloneTaskConfigBuilder, NestedStandaloneTaskInterfaceBuilder {
    init {
        jobConfig = JobConfig(jobName)
    }

    override fun task(
        name: String,
        taskProvider: (taskBuilder: NestedStandaloneTaskConfigBuilder) -> NestedStandaloneTaskConfigBuilder
    ): NestedStandaloneTaskInterfaceBuilder {
        setTask(name)
        taskProvider(this)
        return this
    }

    override fun task(task: Task): NestedStandaloneTaskInterfaceBuilder {
        setTask(task)
        return this
    }

    override fun andTask(
        name: String,
        taskProvider: (taskBuilder: NestedStandaloneTaskConfigBuilder) -> NestedStandaloneTaskConfigBuilder
    ): NestedStandaloneTaskInterfaceBuilder {
        setTask(name)
        taskProvider(this)
        return this
    }

    override fun andTask(task: Task): NestedStandaloneTaskInterfaceBuilder {
        setTask(task)
        return this
    }

    override fun and(): NestedStandaloneJobBuilder {
        return this
    }

    override fun build(): Job {
        finalizeJob()
        return jobConfig
    }

    override fun priority(priority: Int): NestedStandaloneTaskConfigBuilder {
        setPriority(priority)
        return this
    }

    override fun failLevel(failLevel: FailLevel): NestedStandaloneTaskConfigBuilder {
        setFailLevel(failLevel)
        return this
    }

    override fun consumables(vararg consumables: String): NestedStandaloneTaskConfigBuilder {
        setConsumables(*consumables)
        return this
    }

    override fun consumables(consumables: Collection<String>): NestedStandaloneTaskConfigBuilder {
        setConsumables(consumables)
        return this
    }

    override fun producibles(vararg producibles: String): NestedStandaloneTaskConfigBuilder {
        setProducibles(*producibles)
        return this
    }

    override fun producibles(producibles: Collection<String>): NestedStandaloneTaskConfigBuilder {
        setProducibles(producibles)
        return this
    }
}

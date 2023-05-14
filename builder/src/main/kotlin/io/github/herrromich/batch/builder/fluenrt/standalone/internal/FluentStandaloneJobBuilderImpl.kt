package io.github.herrromich.batch.builder.fluenrt.standalone.internal

import io.github.herrromich.batch.FailLevel
import io.github.herrromich.batch.Job
import io.github.herrromich.batch.Task
import io.github.herrromich.batch.builder.fluenrt.standalone.FluentStandaloneJobBuilder
import io.github.herrromich.batch.builder.fluenrt.standalone.FluentStandaloneTaskConfigBuilder
import io.github.herrromich.batch.builder.fluenrt.standalone.FluentStandaloneTaskInterfaceBuilder
import io.github.herrromich.batch.builder.internal.BaseBuilder
import io.github.herrromich.batch.config.JobConfig

internal class FluentStandaloneJobBuilderImpl(jobName: String) : BaseBuilder(),
    FluentStandaloneJobBuilder, FluentStandaloneTaskConfigBuilder, FluentStandaloneTaskInterfaceBuilder {
    init {
        jobConfig = JobConfig(jobName)
    }

    override fun task(name: String): FluentStandaloneTaskConfigBuilder {
        setTask(name)
        return this
    }

    override fun task(task: Task): FluentStandaloneTaskInterfaceBuilder {
        setTask(task)
        return this
    }

    override fun priority(priority: Int): FluentStandaloneTaskConfigBuilder {
        setPriority(priority)
        return this
    }

    override fun failLevel(failLevel: FailLevel): FluentStandaloneTaskConfigBuilder {
        setFailLevel(failLevel)
        return this
    }

    override fun consumables(vararg consumables: String): FluentStandaloneTaskConfigBuilder {
        setConsumables(*consumables)
        return this
    }

    override fun consumables(consumables: Collection<String>): FluentStandaloneTaskConfigBuilder {
        setConsumables(consumables)
        return this
    }

    override fun producibles(vararg producibles: String): FluentStandaloneTaskConfigBuilder {
        setProducibles(*producibles)
        return this
    }

    override fun producibles(producibles: Collection<String>): FluentStandaloneTaskConfigBuilder {
        setProducibles(producibles)
        return this
    }

    override fun andTask(name: String): FluentStandaloneTaskConfigBuilder {
        setTask(name)
        return this
    }

    override fun andTask(task: Task): FluentStandaloneTaskInterfaceBuilder {
        setTask(task)
        return this
    }

    override fun and(): FluentStandaloneJobBuilder {
        return this
    }

    override fun build(): Job {
        finalizeJob()
        return jobConfig.copy()
    }
}

package io.github.herrromich.batch.builder.fluenrt.standalone.internal

import io.github.herrromich.batch.FailLevel
import io.github.herrromich.batch.Task
import io.github.herrromich.batch.builder.fluenrt.standalone.FluentStandaloneTaskBuilder
import io.github.herrromich.batch.builder.internal.BaseBuilder
import io.github.herrromich.batch.config.TaskConfig

internal class FluentStandaloneTaskBuilderImpl(taskName: String) : BaseBuilder(), FluentStandaloneTaskBuilder {
    init {
        taskConfig = TaskConfig(taskName)
    }

    override fun priority(priority: Int): FluentStandaloneTaskBuilder {
        setPriority(priority)
        return this
    }

    override fun failLevel(failLevel: FailLevel): FluentStandaloneTaskBuilder {
        setFailLevel(failLevel)
        return this
    }

    override fun consumables(vararg consumables: String): FluentStandaloneTaskBuilder {
        setConsumables(*consumables)
        return this
    }

    override fun consumables(consumables: Collection<String>): FluentStandaloneTaskBuilder {
        setConsumables(consumables)
        return this
    }

    override fun producibles(vararg producibles: String): FluentStandaloneTaskBuilder {
        setProducibles(*producibles)
        return this
    }

    override fun producibles(producibles: Collection<String>): FluentStandaloneTaskBuilder {
        setProducibles(producibles)
        return this
    }

    override fun build(): Task {
        return taskConfig.copy()
    }
}

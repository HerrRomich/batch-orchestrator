package io.github.herrromich.batch.dsl

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.config.StandaloneJobFactory

interface DslJobFactory {
    fun task(taskName: String, init: DslTaskFactory.() -> Unit = { })
}

internal class SimpleDslJobFactory(jobName: String): DslJobFactory {
    private val factory = StandaloneJobFactory.instance(jobName)

    override fun task(taskName: String, init: DslTaskFactory.() -> Unit) {
        val taskFactory = SimpleDslTaskFactory(factory, taskName)
        taskFactory.init()
        factory.task(taskFactory.build())
    }

    fun build() = factory.build()
}

fun job(jobName: String, init: DslJobFactory.() -> Unit): Job {
    val factory = SimpleDslJobFactory(jobName)
    factory.init()
    return factory.build()
}


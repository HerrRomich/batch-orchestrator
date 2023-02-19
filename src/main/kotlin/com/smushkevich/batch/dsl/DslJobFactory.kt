package com.smushkevich.batch.dsl

import com.smushkevich.batch.Job
import com.smushkevich.batch.config.SimpleStandaloneJobFactory

interface DslJobFactory {
    fun task(taskName: String, init: DslTaskFactory.() -> Unit = { })
}

internal class SimpleDslJobFactory(jobName: String): DslJobFactory {
    private val factory = SimpleStandaloneJobFactory(jobName)

    override fun task(taskName: String, init: DslTaskFactory.() -> Unit) {
        val taskFactory = SimpleDslTaskFactory(factory, taskName)
        taskFactory.init()
        factory.addTask(taskFactory.build())
    }

    fun build() = factory.build()
}

fun job(jobName: String, init: DslJobFactory.() -> Unit): Job {
    val factory = SimpleDslJobFactory(jobName)
    factory.init()
    return factory.build()
}


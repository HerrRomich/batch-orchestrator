package io.github.herrromich.batch.fluent.dsl

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.builder.fluenrt.standalone.FluentStandaloneJobBuilder

class TestJobBuiilder(jobName: String) : BaseDslJobBuilder(jobName) {
    override fun createJobBuilder(jobName: String) = FluentStandaloneJobBuilder.instance(jobName)

    override fun createTaskBuilder(taskName: String) = TestDslTaskBuilder(taskName)
}

fun testJob(name: String, init: DSLJobBuilder.() -> Unit): Job {
    val builder = TestJobBuiilder(name)
    builder.init()
    return builder.build()
}


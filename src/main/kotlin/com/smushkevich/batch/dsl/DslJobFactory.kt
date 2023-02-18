package com.smushkevich.batch.dsl

import com.smushkevich.batch.Job
import com.smushkevich.batch.config.JobConfig
import com.smushkevich.batch.config.SimpleStandaloneJobFactory

interface DslJobFactory {
    fun task(taskName: String, init: DslTaskFactory.() -> Unit = { }): Unit
}

fun job(jobName: String, init: DslJobFactory.() -> Unit): Job {
    val factory = SimpleStandaloneJobFactory(JobConfig(jobName))
    factory.init()
    return factory.build()
}


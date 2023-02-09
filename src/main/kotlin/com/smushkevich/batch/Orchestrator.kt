package com.smushkevich.batch

interface Orchestrator {

    val jobs: Map<String, JobConfig>

    fun execute(jobName: String): JobExecution
}
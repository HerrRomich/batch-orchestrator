package com.smushkevich.batch

interface Orchestrator {

    val jobs: Map<String, Job>

    fun execute(jobName: String): JobExecution
}
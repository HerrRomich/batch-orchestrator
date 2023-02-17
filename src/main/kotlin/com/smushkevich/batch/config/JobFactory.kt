package com.smushkevich.batch.config

import com.smushkevich.batch.Orchestrator
import com.smushkevich.batch.OrchestratorFactory
import com.smushkevich.batch.Task

interface JobFactory {
    val jobName: String
    val tasks: Set<Task>

    fun jobName(jobName: String): JobFactory

    fun task(taskName: String): TaskFactory

    fun andJob(jobName: String): JobFactory

    fun and(): OrchestratorFactory

    fun build(): Orchestrator
}
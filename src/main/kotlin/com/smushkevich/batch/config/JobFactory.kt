package com.smushkevich.batch.config

import com.smushkevich.batch.Orchestrator
import com.smushkevich.batch.OrchestratorFactory
import com.smushkevich.batch.TaskConfig

interface JobFactory {
    val jobName: String
    val tasks: Set<TaskConfig>

    fun jobName(jobName: String): JobFactory

    fun task(taskName: String): TaskFactory

    fun andJob(jobName: String): JobFactory

    fun and(): OrchestratorFactory

    fun build(): Orchestrator
}
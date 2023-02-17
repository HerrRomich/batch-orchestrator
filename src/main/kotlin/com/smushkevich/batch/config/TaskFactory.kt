package com.smushkevich.batch.config

import com.smushkevich.batch.FailLevel
import com.smushkevich.batch.Orchestrator

interface TaskFactory {
    fun taskName(taskName: String): TaskFactory

    fun priority(priority: Int): TaskFactory

    fun failLevel(failLevel: FailLevel): TaskFactory

    fun consumable(vararg consumable: String): TaskFactory

    fun producible(vararg producible: String): TaskFactory

    fun runnable(runnable: () -> Unit): TaskFactory

    fun andTask(taskName: String): TaskFactory

    fun andJob(jobName:String): JobFactory

    fun and(): JobFactory

    fun build(): Orchestrator

}

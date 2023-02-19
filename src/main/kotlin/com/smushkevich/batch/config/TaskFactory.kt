package com.smushkevich.batch.config

import com.smushkevich.batch.FailLevel
import com.smushkevich.batch.TaskContext

interface TaskFactory<J: JobFactory<J, T>, T: TaskFactory<J, T>> {
    fun name(name: String): T
    fun priority(priority: Int): T
    fun failLevel(failLevel: FailLevel): T
    fun consumables(vararg consumables: String): T
    fun producibles(vararg producibles: String): T
    fun runnable(runnable: (context: TaskContext) -> Unit): T
    fun andTask(taskName: String): T
    fun and(): J
}
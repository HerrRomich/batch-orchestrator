package com.smushkevich.batch.config

import com.smushkevich.batch.FailLevel
import com.smushkevich.batch.TaskContext

interface TaskFactory<T: JobFactory<T, P>, P: TaskFactory<T, P>> {
    fun taskName(taskName: String): P
    fun priority(priority: Int): P
    fun failLevel(failLevel: FailLevel): P
    fun consumables(vararg consumables: String): P
    fun producibles(vararg producibles: String): P
    fun runnable(runnable: (context: TaskContext) -> Unit): P
    fun andTask(taskName: String): P
    fun and(): T
}
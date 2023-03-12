package com.smushkevich.batch.config

import com.smushkevich.batch.Consumer
import com.smushkevich.batch.FailLevel
import com.smushkevich.batch.Task
import com.smushkevich.batch.TaskContext

interface TaskFactory<J: JobFactory<J, T>, T: TaskFactory<J, T>> {
    fun name(name: String): T
    fun priority(priority: Int): T
    fun failLevel(failLevel: FailLevel): T
    fun consumables(vararg consumables: String): T
    fun producibles(vararg producibles: String): T
    fun runnable(runnable: Consumer<TaskContext>): T
    fun andTask(taskName: String): T
    fun andTask(task: Task): J
    fun and(): J
}
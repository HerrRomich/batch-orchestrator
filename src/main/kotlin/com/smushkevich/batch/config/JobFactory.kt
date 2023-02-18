package com.smushkevich.batch.config

import com.smushkevich.batch.Task

interface JobFactory<T: JobFactory<T, P>, P: TaskFactory<T, P>> {
    val jobName: String
    val tasks: Set<Task>
    fun jobName(jobName: String): T
    fun task(taskName: String): P
    fun addTask(task: Task)
}
package com.smushkevich.batch.config

import com.smushkevich.batch.Task

interface JobFactory<J: JobFactory<J, T>, T: TaskFactory<J, T>> {
    val name: String
    fun name(name: String): J
    fun task(taskName: String): T
    fun addTask(task: Task): J
}
package io.github.herrromich.batch.config

import io.github.herrromich.batch.Task

interface JobFactory<J: JobFactory<J, T>, T: TaskFactory<J, T>> {
    val name: String
    fun name(name: String): J
    fun task(taskName: String): T
    fun task(task: Task): J
}
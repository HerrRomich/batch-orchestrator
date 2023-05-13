package io.github.herrromich.batch.internal

import io.github.herrromich.batch.TaskContext

interface Executor {
    fun execute(taskExecution: TaskContext)
}
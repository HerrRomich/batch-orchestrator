package io.github.herrromich.batch.config

import io.github.herrromich.batch.Consumer
import io.github.herrromich.batch.FailLevel
import io.github.herrromich.batch.Task
import io.github.herrromich.batch.TaskContext

interface TaskFactory<J: JobFactory<J, T>, T: TaskFactory<J, T>> {
    fun name(name: String): T
    fun priority(priority: Int): T
    fun failLevel(failLevel: FailLevel): T
    fun consumables(vararg consumables: String): T
    fun consumables(consumables: Collection<String>): T
    fun producibles(vararg producibles: String): T
    fun producibles(producibles: Collection<String>): T
    fun runnable(runnable: Consumer<TaskContext>): T
    fun andTask(taskName: String): T
    fun andTask(task: Task): J
    fun and(): J
}
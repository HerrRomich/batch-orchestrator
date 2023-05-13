package io.github.herrromich.batch.config

import io.github.herrromich.batch.*

internal data class TaskConfig(
    override val name: String,
    override val priority: Int = TaskPriorities.DEFAULT,
    override val failLevel: FailLevel = FailLevel.DEFAULT,
    override val consumables: Set<String> = emptySet(),
    override val producibles: Set<String> = emptySet(),
    val runnable: Consumer<TaskContext> = Consumer { TODO("Task $name has no runnable!") },
) : Task {
    constructor(task: Task) : this(
        name = task.name,
        priority = task.priority,
        failLevel = task.failLevel,
        consumables = task.consumables.toSet(),
        producibles = task.producibles.toSet(),
        runnable = task::execute
    )

    override fun execute(context: TaskContext) {
        runnable.accept(context)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TaskConfig

        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}


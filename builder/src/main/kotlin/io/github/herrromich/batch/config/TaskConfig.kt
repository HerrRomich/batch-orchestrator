package io.github.herrromich.batch.config

import io.github.herrromich.batch.*

data class TaskConfig(
    override val name: String,
    override val priority: Int = TaskPriorities.DEFAULT,
    override val failLevel: FailLevel = FailLevel.DEFAULT,
    override val consumables: Set<String> = emptySet(),
    override val producibles: Set<String> = emptySet(),
    val runnable: Consumer = object : Consumer {
        override fun accept(taskContext: TaskContext) {
            throw OrchestratorException("No runnable implementation in task: \"${taskContext.jobContext.job.name}.${name}\".")
        }
    }
) : Task {
    constructor(task: Task) : this(
        task.name,
        task.priority,
        task.failLevel,
        task.consumables.toSet(),
        task.producibles.toSet(),
        { task.execute(it) }
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


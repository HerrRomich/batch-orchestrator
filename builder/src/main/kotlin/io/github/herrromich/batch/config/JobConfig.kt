package io.github.herrromich.batch.config

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.Task

/**
 * An instance of
 */
data class JobConfig(
    override val name: String,
    override val tasks: Set<Task> = emptySet()
) : Job {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JobConfig

        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
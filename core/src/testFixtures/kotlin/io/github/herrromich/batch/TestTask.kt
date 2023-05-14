package io.github.herrromich.batch

import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

abstract class TestTask(override val name: String) : Task {
    override val priority: Int
        get() = TaskPriorities.DEFAULT
    override val failLevel: FailLevel
        get() = FailLevel.DEFAULT
    override val consumables: Set<String>
        get() = emptySet()
    override val producibles: Set<String>
        get() = emptySet()
    override fun execute(context: TaskContext) {
        logger.info { "Executiong test task: \"${name}\"" }
    }
}

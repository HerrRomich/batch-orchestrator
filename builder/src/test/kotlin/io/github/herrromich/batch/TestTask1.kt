package io.github.herrromich.batch

open class TestTask1 : TestTask("test-task1") {
    override val priority = TaskPriorities.CRITICAL
    override val failLevel = FailLevel.FATAL
    override val consumables = setOf("test-resource1", "test-resource2")
    override val producibles = setOf("test-resource2")
}
package io.github.herrromich.batch.builder.nested.standalone

import io.github.herrromich.batch.FailLevel

interface NestedStandaloneTaskConfigBuilder {
    fun priority(priority: Int): NestedStandaloneTaskConfigBuilder
    fun failLevel(failLevel: FailLevel): NestedStandaloneTaskConfigBuilder
    fun consumables(vararg consumables: String): NestedStandaloneTaskConfigBuilder
    fun consumables(consumables: Collection<String>): NestedStandaloneTaskConfigBuilder
    fun producibles(vararg producibles: String): NestedStandaloneTaskConfigBuilder
    fun producibles(producibles: Collection<String>): NestedStandaloneTaskConfigBuilder
}

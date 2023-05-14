package io.github.herrromich.batch.fluent.dsl

import io.github.herrromich.batch.builder.fluenrt.standalone.FluentStandaloneTaskBuilder

class TestDslTaskBuilder(taskName: String) : BaseDslTaskBuilder() {
    override val builder = FluentStandaloneTaskBuilder.instance(taskName)
}
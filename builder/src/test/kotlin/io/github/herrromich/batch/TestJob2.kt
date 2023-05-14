package io.github.herrromich.batch

class TestJob2: Job {
    override val name = "test-job2"
    override val tasks = setOf(TestTask1(), TestTask2())
}
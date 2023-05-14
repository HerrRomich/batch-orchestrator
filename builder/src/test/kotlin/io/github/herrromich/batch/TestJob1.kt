package io.github.herrromich.batch

class TestJob1: Job {
    override val name = "test-job1"
    override val tasks = setOf(TestTask1(), TestTask2())
}
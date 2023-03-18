package io.github.herrromich.batch

import io.github.herrromich.batch.dsl.orchestrator
import kotlin.test.Test

class JobDSLFactoryOrchestratorTest {
    @Test
    fun `test orchestrator factory DSL`() {
        val orchestrator = orchestrator {
            job("test") {
                task("test-task-1") {
                    producibles("test-resource-1")
                    runnable { Thread.sleep(1000) }
                }
                task("test-task-8") {
                    priority(TaskPriorities.HIGHER)
                    runnable { Thread.sleep(200) }
                }
                task("test-task-9") {
                    priority(TaskPriorities.HIGHER)
                    producibles("test-resource-1")
                    failLevel(FailLevel.ERROR)
                    runnable { Thread.sleep(100) }
                }
                task("test-task-2") {
                    producibles("test-resource-1")
                    priority(TaskPriorities.HIGHER)
                    runnable { Thread.sleep(500) }
                }
                task("test-task-3") {
                    consumables("test-resource-1")
                    consumables("test-resource-2")
                    runnable { Thread.sleep(200) }
                }
                task("test-task-4") {
                    consumables("test-resource-1")
                    producibles("test-resource-2")
                    runnable { Thread.sleep(200) }
                }
            }
        }
        orchestrator.execute("test").get()
        orchestrator.execute("test").get()
        orchestrator.execute("test").get()
        orchestrator.execute("test").get()
    }
}
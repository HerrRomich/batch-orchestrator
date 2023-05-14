package io.github.herrromich.batch.internal

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.OrchestratorException
import io.github.herrromich.batch.Task

object JobValidator {

    @Throws(OrchestratorException::class)
    fun checkOrphans(job: Job) {
        val orphans = job.tasks.flatMap { it.consumables }.toSet() - job.tasks.flatMap { it.producibles }.toSet()
        val orphanTasks = job.tasks.flatMap { task ->
            task.consumables.filter { orphans.contains(it) }.map { consumable -> task to consumable }
        }.groupBy({ it.first }, { it.second })
        if (orphanTasks.isNotEmpty()) {
            val messageBuilder =
                StringBuilder("There are tasks in job \"${job.name}\", defining consumables, that are not produced by other tasks:")
            orphanTasks.forEach { (task, consumables) ->
                messageBuilder.append(System.lineSeparator())
                messageBuilder.append("${task.name}:")
                consumables.forEach { consumable ->
                    messageBuilder.append(System.lineSeparator())
                    messageBuilder.append(" - $consumable")
                }
            }
            val ex = OrchestratorException(messageBuilder.toString())
            throw ex
        }
    }

    @Throws(OrchestratorException::class)
    fun checkCycles(job: Job) {
        val cycleTests = job.tasks.map(JobValidator::CycleTest)
        cycleTests.forEach { cycleTest ->
            if (!cycleTest.visited) {
                val cycles = getCycles(cycleTests, cycleTest, emptyMap())
                if (cycles != null) {
                    val messageBuilder = StringBuilder("There are cycles in graph of job \"${job.name}\":")
                    messageBuilder.appendLine()
                    messageBuilder.appendLine("      ${cycleTest.task.name}")
                    messageBuilder.appendLine("┌───┐ ↓")
                    messageBuilder.appendLine("│   ↓")
                    cycles.entries.forEachIndexed { ind, (resource, cycleTest) ->
                        if (ind > 0) {
                            messageBuilder.appendLine("│   ↓")
                        }
                        messageBuilder.appendLine("│  ♦{$resource}")
                        messageBuilder.appendLine("│   ↓")
                        messageBuilder.appendLine("│  ${cycleTest.task.name}")
                    }
                    messageBuilder.append("└───┘")
                    val ex = OrchestratorException(messageBuilder.toString())
                    throw ex
                }

            }
        }
    }

    private fun  getCycles(
        cycleTests: List<CycleTest>,
        cycleTest: CycleTest,
        cycle: Map<String, CycleTest>
    ): Map<String, CycleTest>? {
        val neighbors = cycleTest.task.producibles.flatMap { providable ->
            cycleTests.flatMap { cycleTest -> cycleTest.task.consumables.map { consumable -> consumable to cycleTest } }
                .filter { it.first == providable }
        }
        neighbors.forEach { neighbor ->
            if (cycle.containsValue(neighbor.second)) {
                return cycle + neighbor
            } else if (!neighbor.second.visited) {
                val cycles = getCycles(cycleTests, neighbor.second, cycle + neighbor)
                if (cycles != null) {
                    return cycles
                }
            }
        }
        cycleTest.visited = true
        return null
    }

    private data class CycleTest(val task: Task) {
        var visited: Boolean = false
    }
}

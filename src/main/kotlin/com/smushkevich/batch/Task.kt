package com.smushkevich.batch

interface Task {
    val jobName: String
    val taskName: String
    val priority: Int
    val failLevel: FailLevel
    val consumables: Set<String>
    val producibles: Set<String>

    fun execute(context: TaskContext)
}

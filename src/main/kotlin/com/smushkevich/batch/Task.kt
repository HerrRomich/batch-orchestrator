package com.smushkevich.batch

interface Task {
    val jobName: String
    val taskName: String
    val priority: Int
    val failLevel: FailLevel
    val consumables: Set<String>
    val providables: Set<String>
    val runnable: () -> Unit
}

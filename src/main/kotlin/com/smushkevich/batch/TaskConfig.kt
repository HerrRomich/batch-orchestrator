package com.smushkevich.batch

interface TaskConfig {
    val jobName: String
    val taskName: String
    val priority: Int
    val failLevel: FailLevel
    val consumables: Set<Any>
    val providables: Set<Any>
    val runnable: () -> Unit
}

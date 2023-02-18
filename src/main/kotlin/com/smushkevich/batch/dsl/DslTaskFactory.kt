package com.smushkevich.batch.dsl

import com.smushkevich.batch.FailLevel
import com.smushkevich.batch.TaskContext

interface DslTaskFactory {
    var priority: Int
    var failLevel: FailLevel
    fun consumables(vararg consumables: String): Any
    fun producibles(vararg producibles: String): Any
    fun execute(runnable: (context: TaskContext) -> Unit)
}

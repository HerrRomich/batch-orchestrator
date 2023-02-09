package com.smushkevich.batch

interface JobConfig {
    val jobName: String
    val tasks: Set<TaskConfig>
}

package com.smushkevich.batch

import java.util.*
import java.util.concurrent.Future

interface JobExecution: Future<Unit> {
    val id: UUID
    val jobConfig: JobConfig
}

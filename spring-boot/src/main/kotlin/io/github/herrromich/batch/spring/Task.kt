package io.github.herrromich.batch.spring

import io.github.herrromich.batch.Task

interface Task: Task {

    /**
     * Name of parent Job
     */
    val jobName: String

}
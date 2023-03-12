package com.smushkevich.batch

interface TaskContext {

    /**
     * Reference to the parent job context
     */
    val jobContext: JobContext

    /**
     * Reference to a task definition
     */
    val task: Task

}

package com.smushkevich.batch.config

import com.smushkevich.batch.Job

interface StandaloneTaskFactory: TaskFactory<StandaloneJobFactory, StandaloneTaskFactory> {
    fun build(): Job
}
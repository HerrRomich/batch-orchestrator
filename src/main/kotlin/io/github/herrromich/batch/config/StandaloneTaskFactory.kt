package io.github.herrromich.batch.config

import io.github.herrromich.batch.Job

interface StandaloneTaskFactory: TaskFactory<StandaloneJobFactory, StandaloneTaskFactory> {
    fun build(): Job
}
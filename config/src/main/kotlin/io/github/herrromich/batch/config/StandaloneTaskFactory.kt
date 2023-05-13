package io.github.herrromich.batch.config

import io.github.herrromich.batch.Task

interface StandaloneTaskFactory : TaskFactory<StandaloneJobFactory, StandaloneTaskFactory> {
    fun build(): Task

    companion object {
        fun instance(jobFactory: StandaloneJobFactory, taskName: String): StandaloneTaskFactory =
            SimpleStandaloneTaskFactory(jobFactory, taskName)
    }
}
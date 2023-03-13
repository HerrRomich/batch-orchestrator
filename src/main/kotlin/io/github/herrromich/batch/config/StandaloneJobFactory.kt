package io.github.herrromich.batch.config

import io.github.herrromich.batch.Job

interface StandaloneJobFactory : JobFactory<StandaloneJobFactory, StandaloneTaskFactory> {
    fun build(): Job

    companion object {
        @JvmStatic
        fun instance(jobName: String): StandaloneJobFactory = SimpleStandaloneJobFactory(jobName)
    }


}
package com.smushkevich.batch.config

import com.smushkevich.batch.Job

interface StandaloneJobFactory : JobFactory<StandaloneJobFactory, StandaloneTaskFactory> {
    fun build(): Job

    companion object {
        @JvmStatic
        fun instance(jobName: String): StandaloneJobFactory = SimpleStandaloneJobFactory(jobName)
    }


}
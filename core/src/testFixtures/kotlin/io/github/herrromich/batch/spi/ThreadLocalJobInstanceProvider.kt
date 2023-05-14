package io.github.herrromich.batch.spi

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.internal.JobInstance
import io.github.herrromich.batch.internal.JobInstanceProvider
import io.github.herrromich.batch.internal.TestJobInstance

val threadLocalJobInstanceFactory: ThreadLocal<(job: Job) -> JobInstance> =
    ThreadLocal.withInitial { ::TestJobInstance }

class ThreadLocalJobInstanceProvider : JobInstanceProvider {
    override fun provideInstance(job: Job) = threadLocalJobInstanceFactory.get().invoke(job)
}
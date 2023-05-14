package io.github.herrromich.batch.reactive.internal

import io.github.herrromich.batch.Job
import io.github.herrromich.batch.internal.JobInstanceProvider

internal class ReactiveJobInstanceProvider: JobInstanceProvider {
    override fun provideInstance(job: Job) = ReactiveJobInstance(job)
}
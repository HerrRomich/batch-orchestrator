package io.github.herrromich.batch.internal

import io.github.herrromich.batch.Job

interface JobInstanceProvider {

    fun provideInstance(job: Job): JobInstance

}

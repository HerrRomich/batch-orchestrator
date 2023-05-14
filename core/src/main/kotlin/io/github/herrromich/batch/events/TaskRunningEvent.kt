package io.github.herrromich.batch.events

interface TaskRunningEvent: TaskEvent {
    val progress: Double
}
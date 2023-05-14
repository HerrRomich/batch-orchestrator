package io.github.herrromich.batch.concurrent.internal

import java.util.concurrent.FutureTask

internal class FutureTaskWithPriority(
    val priority: Int,
    runnable: Runnable
) :
    FutureTask<Void>(runnable, null),
    Comparable<FutureTaskWithPriority> {
    override fun compareTo(other: FutureTaskWithPriority) =
        priority - other.priority
}
package io.github.herrromich.batch.internal

import java.time.Duration

internal fun Duration?.prettyPrint(): String {
    if (this == null) {
        return "infinity"
    }
    val hh = this.toHours()
        .let { if (it == 0L) null else it }
    val mm = this.toMinutesPart()
        .let { if (hh == null && it == 0) null else it }
    val ss = this.toSecondsPart()
        .let { if (mm == null && it == 0) null else it }
    val ms = this.toMillisPart()
    return "${hh?.let { "${it}h " } ?: ""}${mm?.let { "${it}m " } ?: ""}${ss?.let { "$it" } ?: ""}.${
        ms.toString()
            .padStart(3, '0')
    }s"

}

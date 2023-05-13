package io.github.herrromich.batch

object TaskPriorities {

    const val UNIMPORTANT = 0
    const val LOWEST = 10
    const val LOWER = 20
    const val LOW = 50
    const val NORMAL = 100
    const val HIGH = 500
    const val HIGHER = 1000
    const val HIGHEST = 2000
    const val CRITICAL = 5000

    val DEFAULT: Int by lazy {
        try {
            System.getProperty("defaultTaskPriority")?.toInt()
        } catch (e: Exception) {
            null
        } ?: NORMAL
    }

}
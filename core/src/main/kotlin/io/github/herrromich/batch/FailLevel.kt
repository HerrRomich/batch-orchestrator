package io.github.herrromich.batch

enum class FailLevel {
    WARN,
    ERROR,
    FATAL;

    companion object {
        @JvmStatic
        val DEFAULT: FailLevel by lazy {
            System.getProperty("defaultTaskFailLevel")?.let(FailLevel::valueOf) ?: ERROR
        }
    }
}

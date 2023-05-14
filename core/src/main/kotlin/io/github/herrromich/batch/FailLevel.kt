package io.github.herrromich.batch

/**
 * A level of error handling.
 */
enum class FailLevel {
    /**
     * A failed task will be marked with warning.
     * All producible resources will be set as succeeded.
     * Other tasks won't be influenced.
     */
    WARN,

    /**
     * A failed task will be marked with error.
     * All producible resources will be set as failed.
     * All fulfilled tasks will be executed.
     */
    ERROR,

    /**
     * A failed task be marked as fatal.
     * All producible resources will ber set as failed.
     * All running tasks will be canceled.
     * All other tasks will be skipped.
     */
    FATAL;

    companion object {
        @JvmStatic
        val DEFAULT: FailLevel
            @JvmName("DEFAULT")
            get() = System.getProperty("defaultTaskFailLevel")?.let(FailLevel::valueOf) ?: ERROR
    }
}

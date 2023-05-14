package io.github.herrromich.batch;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Consumer {

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    void accept(@NotNull TaskContext t) throws InterruptedException;

}

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableLongBinaryOperator<E extends Throwable> {
   long applyAsLong(long var1, long var3) throws E;
}

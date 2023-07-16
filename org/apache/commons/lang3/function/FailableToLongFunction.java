package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableToLongFunction<T, E extends Throwable> {
   FailableToLongFunction NOP = (t) -> {
      return 0L;
   };

   static <T, E extends Throwable> FailableToLongFunction<T, E> nop() {
      return NOP;
   }

   long applyAsLong(T var1) throws E;
}

package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableToLongBiFunction<T, U, E extends Throwable> {
   FailableToLongBiFunction NOP = (t, u) -> {
      return 0L;
   };

   static <T, U, E extends Throwable> FailableToLongBiFunction<T, U, E> nop() {
      return NOP;
   }

   long applyAsLong(T var1, U var2) throws E;
}

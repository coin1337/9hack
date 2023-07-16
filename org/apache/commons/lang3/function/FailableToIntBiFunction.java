package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableToIntBiFunction<T, U, E extends Throwable> {
   FailableToIntBiFunction NOP = (t, u) -> {
      return 0;
   };

   static <T, U, E extends Throwable> FailableToIntBiFunction<T, U, E> nop() {
      return NOP;
   }

   int applyAsInt(T var1, U var2) throws E;
}

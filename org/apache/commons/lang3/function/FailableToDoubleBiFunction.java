package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableToDoubleBiFunction<T, U, E extends Throwable> {
   FailableToDoubleBiFunction NOP = (t, u) -> {
      return 0.0D;
   };

   static <T, U, E extends Throwable> FailableToDoubleBiFunction<T, U, E> nop() {
      return NOP;
   }

   double applyAsDouble(T var1, U var2) throws E;
}

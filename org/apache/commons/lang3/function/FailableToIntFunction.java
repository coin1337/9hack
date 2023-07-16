package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableToIntFunction<T, E extends Throwable> {
   FailableToIntFunction NOP = (t) -> {
      return 0;
   };

   static <T, E extends Throwable> FailableToIntFunction<T, E> nop() {
      return NOP;
   }

   int applyAsInt(T var1) throws E;
}

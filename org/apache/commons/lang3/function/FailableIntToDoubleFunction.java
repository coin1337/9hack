package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableIntToDoubleFunction<E extends Throwable> {
   FailableIntToDoubleFunction NOP = (t) -> {
      return 0.0D;
   };

   static <E extends Throwable> FailableIntToDoubleFunction<E> nop() {
      return NOP;
   }

   double applyAsDouble(int var1) throws E;
}

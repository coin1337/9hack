package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableLongToDoubleFunction<E extends Throwable> {
   FailableLongToDoubleFunction NOP = (t) -> {
      return 0.0D;
   };

   static <E extends Throwable> FailableLongToDoubleFunction<E> nop() {
      return NOP;
   }

   double applyAsDouble(long var1) throws E;
}

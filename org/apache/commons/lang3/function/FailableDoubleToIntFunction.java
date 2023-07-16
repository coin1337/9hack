package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableDoubleToIntFunction<E extends Throwable> {
   FailableDoubleToIntFunction NOP = (t) -> {
      return 0;
   };

   static <E extends Throwable> FailableDoubleToIntFunction<E> nop() {
      return NOP;
   }

   int applyAsInt(double var1) throws E;
}

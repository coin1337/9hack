package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableDoubleFunction<R, E extends Throwable> {
   FailableDoubleFunction NOP = (t) -> {
      return null;
   };

   static <R, E extends Throwable> FailableDoubleFunction<R, E> nop() {
      return NOP;
   }

   R apply(double var1) throws E;
}

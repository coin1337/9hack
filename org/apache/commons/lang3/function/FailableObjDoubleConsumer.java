package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableObjDoubleConsumer<T, E extends Throwable> {
   FailableObjDoubleConsumer NOP = (t, u) -> {
   };

   static <T, E extends Throwable> FailableObjDoubleConsumer<T, E> nop() {
      return NOP;
   }

   void accept(T var1, double var2) throws E;
}

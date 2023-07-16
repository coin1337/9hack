package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableObjIntConsumer<T, E extends Throwable> {
   FailableObjIntConsumer NOP = (t, u) -> {
   };

   static <T, E extends Throwable> FailableObjIntConsumer<T, E> nop() {
      return NOP;
   }

   void accept(T var1, int var2) throws E;
}

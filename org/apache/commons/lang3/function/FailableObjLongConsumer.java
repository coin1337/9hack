package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableObjLongConsumer<T, E extends Throwable> {
   FailableObjLongConsumer NOP = (t, u) -> {
   };

   static <T, E extends Throwable> FailableObjLongConsumer<T, E> nop() {
      return NOP;
   }

   void accept(T var1, long var2) throws E;
}

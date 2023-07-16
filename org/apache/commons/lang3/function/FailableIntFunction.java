package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableIntFunction<R, E extends Throwable> {
   FailableIntFunction NOP = (t) -> {
      return null;
   };

   static <R, E extends Throwable> FailableIntFunction<R, E> nop() {
      return NOP;
   }

   R apply(int var1) throws E;
}

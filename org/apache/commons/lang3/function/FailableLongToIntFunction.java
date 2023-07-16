package org.apache.commons.lang3.function;

@FunctionalInterface
public interface FailableLongToIntFunction<E extends Throwable> {
   FailableLongToIntFunction NOP = (t) -> {
      return 0;
   };

   static <E extends Throwable> FailableLongToIntFunction<E> nop() {
      return NOP;
   }

   int applyAsInt(long var1) throws E;
}

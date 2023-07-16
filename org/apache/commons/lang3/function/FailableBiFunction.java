package org.apache.commons.lang3.function;

import java.util.Objects;

@FunctionalInterface
public interface FailableBiFunction<T, U, R, E extends Throwable> {
   FailableBiFunction NOP = (t, u) -> {
      return null;
   };

   static <T, U, R, E extends Throwable> FailableBiFunction<T, U, R, E> nop() {
      return NOP;
   }

   default <V> FailableBiFunction<T, U, V, E> andThen(FailableFunction<? super R, ? extends V, E> after) {
      Objects.requireNonNull(after);
      return (t, u) -> {
         return after.apply(this.apply(t, u));
      };
   }

   R apply(T var1, U var2) throws E;
}

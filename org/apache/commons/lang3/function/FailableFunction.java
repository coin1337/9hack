package org.apache.commons.lang3.function;

import java.util.Objects;

@FunctionalInterface
public interface FailableFunction<T, R, E extends Throwable> {
   FailableFunction NOP = (t) -> {
      return null;
   };

   static <T, E extends Throwable> FailableFunction<T, T, E> identity() {
      return (t) -> {
         return t;
      };
   }

   static <T, R, E extends Throwable> FailableFunction<T, R, E> nop() {
      return NOP;
   }

   default <V> FailableFunction<T, V, E> andThen(FailableFunction<? super R, ? extends V, E> after) {
      Objects.requireNonNull(after);
      return (t) -> {
         return after.apply(this.apply(t));
      };
   }

   R apply(T var1) throws E;

   default <V> FailableFunction<V, R, E> compose(FailableFunction<? super V, ? extends T, E> before) {
      Objects.requireNonNull(before);
      return (v) -> {
         return this.apply(before.apply(v));
      };
   }
}

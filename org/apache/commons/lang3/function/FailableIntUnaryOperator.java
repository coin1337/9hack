package org.apache.commons.lang3.function;

import java.util.Objects;

public interface FailableIntUnaryOperator<E extends Throwable> {
   FailableIntUnaryOperator NOP = (t) -> {
      return 0;
   };

   static <E extends Throwable> FailableIntUnaryOperator<E> identity() {
      return (t) -> {
         return t;
      };
   }

   static <E extends Throwable> FailableIntUnaryOperator<E> nop() {
      return NOP;
   }

   default FailableIntUnaryOperator<E> andThen(FailableIntUnaryOperator<E> after) {
      Objects.requireNonNull(after);
      return (t) -> {
         return after.applyAsInt(this.applyAsInt(t));
      };
   }

   int applyAsInt(int var1) throws E;

   default FailableIntUnaryOperator<E> compose(FailableIntUnaryOperator<E> before) {
      Objects.requireNonNull(before);
      return (v) -> {
         return this.applyAsInt(before.applyAsInt(v));
      };
   }
}

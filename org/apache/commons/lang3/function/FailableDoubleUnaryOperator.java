package org.apache.commons.lang3.function;

import java.util.Objects;

public interface FailableDoubleUnaryOperator<E extends Throwable> {
   FailableDoubleUnaryOperator NOP = (t) -> {
      return 0.0D;
   };

   static <E extends Throwable> FailableDoubleUnaryOperator<E> identity() {
      return (t) -> {
         return t;
      };
   }

   static <E extends Throwable> FailableDoubleUnaryOperator<E> nop() {
      return NOP;
   }

   default FailableDoubleUnaryOperator<E> andThen(FailableDoubleUnaryOperator<E> after) {
      Objects.requireNonNull(after);
      return (t) -> {
         return after.applyAsDouble(this.applyAsDouble(t));
      };
   }

   double applyAsDouble(double var1) throws E;

   default FailableDoubleUnaryOperator<E> compose(FailableDoubleUnaryOperator<E> before) {
      Objects.requireNonNull(before);
      return (v) -> {
         return this.applyAsDouble(before.applyAsDouble(v));
      };
   }
}

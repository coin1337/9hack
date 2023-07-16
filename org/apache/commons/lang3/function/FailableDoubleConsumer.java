package org.apache.commons.lang3.function;

import java.util.Objects;

@FunctionalInterface
public interface FailableDoubleConsumer<E extends Throwable> {
   FailableDoubleConsumer NOP = (t) -> {
   };

   static <E extends Throwable> FailableDoubleConsumer<E> nop() {
      return NOP;
   }

   void accept(double var1) throws E;

   default FailableDoubleConsumer<E> andThen(FailableDoubleConsumer<E> after) {
      Objects.requireNonNull(after);
      return (t) -> {
         this.accept(t);
         after.accept(t);
      };
   }
}

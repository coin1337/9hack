package org.apache.commons.lang3.function;

import java.util.Objects;

@FunctionalInterface
public interface FailableBiConsumer<T, U, E extends Throwable> {
   FailableBiConsumer NOP = (t, u) -> {
   };

   static <T, U, E extends Throwable> FailableBiConsumer<T, U, E> nop() {
      return NOP;
   }

   void accept(T var1, U var2) throws E;

   default FailableBiConsumer<T, U, E> andThen(FailableBiConsumer<? super T, ? super U, E> after) {
      Objects.requireNonNull(after);
      return (t, u) -> {
         this.accept(t, u);
         after.accept(t, u);
      };
   }
}

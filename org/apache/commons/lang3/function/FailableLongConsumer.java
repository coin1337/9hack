package org.apache.commons.lang3.function;

import java.util.Objects;

@FunctionalInterface
public interface FailableLongConsumer<E extends Throwable> {
   FailableLongConsumer NOP = (t) -> {
   };

   static <E extends Throwable> FailableLongConsumer<E> nop() {
      return NOP;
   }

   void accept(long var1) throws E;

   default FailableLongConsumer<E> andThen(FailableLongConsumer<E> after) {
      Objects.requireNonNull(after);
      return (t) -> {
         this.accept(t);
         after.accept(t);
      };
   }
}

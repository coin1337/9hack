package org.apache.commons.lang3.function;

import java.util.Objects;

@FunctionalInterface
public interface FailableIntConsumer<E extends Throwable> {
   FailableIntConsumer NOP = (t) -> {
   };

   static <E extends Throwable> FailableIntConsumer<E> nop() {
      return NOP;
   }

   void accept(int var1) throws E;

   default FailableIntConsumer<E> andThen(FailableIntConsumer<E> after) {
      Objects.requireNonNull(after);
      return (t) -> {
         this.accept(t);
         after.accept(t);
      };
   }
}

package org.apache.commons.lang3.function;

import java.util.Objects;

@FunctionalInterface
public interface FailableLongPredicate<E extends Throwable> {
   FailableLongPredicate FALSE = (t) -> {
      return false;
   };
   FailableLongPredicate TRUE = (t) -> {
      return true;
   };

   static <E extends Throwable> FailableLongPredicate<E> falsePredicate() {
      return FALSE;
   }

   static <E extends Throwable> FailableLongPredicate<E> truePredicate() {
      return TRUE;
   }

   default FailableLongPredicate<E> and(FailableLongPredicate<E> other) {
      Objects.requireNonNull(other);
      return (t) -> {
         return this.test(t) && other.test(t);
      };
   }

   default FailableLongPredicate<E> negate() {
      return (t) -> {
         return !this.test(t);
      };
   }

   default FailableLongPredicate<E> or(FailableLongPredicate<E> other) {
      Objects.requireNonNull(other);
      return (t) -> {
         return this.test(t) || other.test(t);
      };
   }

   boolean test(long var1) throws E;
}

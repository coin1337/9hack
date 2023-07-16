package org.apache.commons.lang3.function;

import java.util.Objects;

@FunctionalInterface
public interface FailableDoublePredicate<E extends Throwable> {
   FailableDoublePredicate FALSE = (t) -> {
      return false;
   };
   FailableDoublePredicate TRUE = (t) -> {
      return true;
   };

   static <E extends Throwable> FailableDoublePredicate<E> falsePredicate() {
      return FALSE;
   }

   static <E extends Throwable> FailableDoublePredicate<E> truePredicate() {
      return TRUE;
   }

   default FailableDoublePredicate<E> and(FailableDoublePredicate<E> other) {
      Objects.requireNonNull(other);
      return (t) -> {
         return this.test(t) && other.test(t);
      };
   }

   default FailableDoublePredicate<E> negate() {
      return (t) -> {
         return !this.test(t);
      };
   }

   default FailableDoublePredicate<E> or(FailableDoublePredicate<E> other) {
      Objects.requireNonNull(other);
      return (t) -> {
         return this.test(t) || other.test(t);
      };
   }

   boolean test(double var1) throws E;
}

package org.apache.commons.lang3.function;

import java.util.Objects;

@FunctionalInterface
public interface FailableIntPredicate<E extends Throwable> {
   FailableIntPredicate FALSE = (t) -> {
      return false;
   };
   FailableIntPredicate TRUE = (t) -> {
      return true;
   };

   static <E extends Throwable> FailableIntPredicate<E> falsePredicate() {
      return FALSE;
   }

   static <E extends Throwable> FailableIntPredicate<E> truePredicate() {
      return TRUE;
   }

   default FailableIntPredicate<E> and(FailableIntPredicate<E> other) {
      Objects.requireNonNull(other);
      return (t) -> {
         return this.test(t) && other.test(t);
      };
   }

   default FailableIntPredicate<E> negate() {
      return (t) -> {
         return !this.test(t);
      };
   }

   default FailableIntPredicate<E> or(FailableIntPredicate<E> other) {
      Objects.requireNonNull(other);
      return (t) -> {
         return this.test(t) || other.test(t);
      };
   }

   boolean test(int var1) throws E;
}

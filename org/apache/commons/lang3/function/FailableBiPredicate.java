package org.apache.commons.lang3.function;

import java.util.Objects;

@FunctionalInterface
public interface FailableBiPredicate<T, U, E extends Throwable> {
   FailableBiPredicate FALSE = (t, u) -> {
      return false;
   };
   FailableBiPredicate TRUE = (t, u) -> {
      return true;
   };

   static <T, U, E extends Throwable> FailableBiPredicate<T, U, E> falsePredicate() {
      return FALSE;
   }

   static <T, U, E extends Throwable> FailableBiPredicate<T, U, E> truePredicate() {
      return TRUE;
   }

   default FailableBiPredicate<T, U, E> and(FailableBiPredicate<? super T, ? super U, E> other) {
      Objects.requireNonNull(other);
      return (t, u) -> {
         return this.test(t, u) && other.test(t, u);
      };
   }

   default FailableBiPredicate<T, U, E> negate() {
      return (t, u) -> {
         return !this.test(t, u);
      };
   }

   default FailableBiPredicate<T, U, E> or(FailableBiPredicate<? super T, ? super U, E> other) {
      Objects.requireNonNull(other);
      return (t, u) -> {
         return this.test(t, u) || other.test(t, u);
      };
   }

   boolean test(T var1, U var2) throws E;
}

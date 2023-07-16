package org.apache.commons.lang3.compare;

import java.util.function.Predicate;

public class ComparableUtils {
   public static <A extends Comparable<A>> Predicate<A> between(A b, A c) {
      return (a) -> {
         return is(a).between(b, c);
      };
   }

   public static <A extends Comparable<A>> Predicate<A> betweenExclusive(A b, A c) {
      return (a) -> {
         return is(a).betweenExclusive(b, c);
      };
   }

   public static <A extends Comparable<A>> Predicate<A> ge(A b) {
      return (a) -> {
         return is(a).greaterThanOrEqualTo(b);
      };
   }

   public static <A extends Comparable<A>> Predicate<A> gt(A b) {
      return (a) -> {
         return is(a).greaterThan(b);
      };
   }

   public static <A extends Comparable<A>> ComparableUtils.ComparableCheckBuilder<A> is(A a) {
      return new ComparableUtils.ComparableCheckBuilder(a);
   }

   public static <A extends Comparable<A>> Predicate<A> le(A b) {
      return (a) -> {
         return is(a).lessThanOrEqualTo(b);
      };
   }

   public static <A extends Comparable<A>> Predicate<A> lt(A b) {
      return (a) -> {
         return is(a).lessThan(b);
      };
   }

   private ComparableUtils() {
   }

   public static class ComparableCheckBuilder<A extends Comparable<A>> {
      private final A a;

      private ComparableCheckBuilder(A a) {
         this.a = a;
      }

      public boolean between(A b, A c) {
         return this.betweenOrdered(b, c) || this.betweenOrdered(c, b);
      }

      public boolean betweenExclusive(A b, A c) {
         return this.betweenOrderedExclusive(b, c) || this.betweenOrderedExclusive(c, b);
      }

      private boolean betweenOrdered(A b, A c) {
         return this.greaterThanOrEqualTo(b) && this.lessThanOrEqualTo(c);
      }

      private boolean betweenOrderedExclusive(A b, A c) {
         return this.greaterThan(b) && this.lessThan(c);
      }

      public boolean equalTo(A b) {
         return this.a.compareTo(b) == 0;
      }

      public boolean greaterThan(A b) {
         return this.a.compareTo(b) > 0;
      }

      public boolean greaterThanOrEqualTo(A b) {
         return this.a.compareTo(b) >= 0;
      }

      public boolean lessThan(A b) {
         return this.a.compareTo(b) < 0;
      }

      public boolean lessThanOrEqualTo(A b) {
         return this.a.compareTo(b) <= 0;
      }

      // $FF: synthetic method
      ComparableCheckBuilder(Comparable x0, Object x1) {
         this(x0);
      }
   }
}

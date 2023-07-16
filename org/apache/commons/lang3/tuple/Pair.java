package org.apache.commons.lang3.tuple;

import java.io.Serializable;
import java.util.Objects;
import java.util.Map.Entry;
import org.apache.commons.lang3.builder.CompareToBuilder;

public abstract class Pair<L, R> implements Entry<L, R>, Comparable<Pair<L, R>>, Serializable {
   private static final long serialVersionUID = 4954918890077093841L;
   public static final Pair<?, ?>[] EMPTY_ARRAY = new Pair.PairAdapter[0];

   public static <L, R> Pair<L, R>[] emptyArray() {
      return (Pair[])EMPTY_ARRAY;
   }

   public static <L, R> Pair<L, R> of(L left, R right) {
      return ImmutablePair.of(left, right);
   }

   public static <L, R> Pair<L, R> of(Entry<L, R> pair) {
      return ImmutablePair.of(pair);
   }

   public int compareTo(Pair<L, R> other) {
      return (new CompareToBuilder()).append(this.getLeft(), other.getLeft()).append(this.getRight(), other.getRight()).toComparison();
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof Entry)) {
         return false;
      } else {
         Entry<?, ?> other = (Entry)obj;
         return Objects.equals(this.getKey(), other.getKey()) && Objects.equals(this.getValue(), other.getValue());
      }
   }

   public final L getKey() {
      return this.getLeft();
   }

   public abstract L getLeft();

   public abstract R getRight();

   public R getValue() {
      return this.getRight();
   }

   public int hashCode() {
      return Objects.hashCode(this.getKey()) ^ Objects.hashCode(this.getValue());
   }

   public String toString() {
      return "(" + this.getLeft() + ',' + this.getRight() + ')';
   }

   public String toString(String format) {
      return String.format(format, this.getLeft(), this.getRight());
   }

   private static final class PairAdapter<L, R> extends Pair<L, R> {
      private static final long serialVersionUID = 1L;

      public L getLeft() {
         return null;
      }

      public R getRight() {
         return null;
      }

      public R setValue(R value) {
         return null;
      }
   }
}

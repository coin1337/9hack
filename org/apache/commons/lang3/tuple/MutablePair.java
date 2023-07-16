package org.apache.commons.lang3.tuple;

import java.util.Map.Entry;

public class MutablePair<L, R> extends Pair<L, R> {
   public static final MutablePair<?, ?>[] EMPTY_ARRAY = new MutablePair[0];
   private static final long serialVersionUID = 4954918890077093841L;
   public L left;
   public R right;

   public static <L, R> MutablePair<L, R>[] emptyArray() {
      return (MutablePair[])EMPTY_ARRAY;
   }

   public static <L, R> MutablePair<L, R> of(L left, R right) {
      return new MutablePair(left, right);
   }

   public static <L, R> MutablePair<L, R> of(Entry<L, R> pair) {
      Object left;
      Object right;
      if (pair != null) {
         left = pair.getKey();
         right = pair.getValue();
      } else {
         left = null;
         right = null;
      }

      return new MutablePair(left, right);
   }

   public MutablePair() {
   }

   public MutablePair(L left, R right) {
      this.left = left;
      this.right = right;
   }

   public L getLeft() {
      return this.left;
   }

   public R getRight() {
      return this.right;
   }

   public void setLeft(L left) {
      this.left = left;
   }

   public void setRight(R right) {
      this.right = right;
   }

   public R setValue(R value) {
      R result = this.getRight();
      this.setRight(value);
      return result;
   }
}

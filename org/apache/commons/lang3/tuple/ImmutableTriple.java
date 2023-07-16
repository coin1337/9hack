package org.apache.commons.lang3.tuple;

public final class ImmutableTriple<L, M, R> extends Triple<L, M, R> {
   public static final ImmutableTriple<?, ?, ?>[] EMPTY_ARRAY = new ImmutableTriple[0];
   private static final ImmutableTriple NULL = of((Object)null, (Object)null, (Object)null);
   private static final long serialVersionUID = 1L;
   public final L left;
   public final M middle;
   public final R right;

   public static <L, M, R> ImmutableTriple<L, M, R>[] emptyArray() {
      return (ImmutableTriple[])EMPTY_ARRAY;
   }

   public static <L, M, R> ImmutableTriple<L, M, R> nullTriple() {
      return NULL;
   }

   public static <L, M, R> ImmutableTriple<L, M, R> of(L left, M middle, R right) {
      return new ImmutableTriple(left, middle, right);
   }

   public ImmutableTriple(L left, M middle, R right) {
      this.left = left;
      this.middle = middle;
      this.right = right;
   }

   public L getLeft() {
      return this.left;
   }

   public M getMiddle() {
      return this.middle;
   }

   public R getRight() {
      return this.right;
   }
}

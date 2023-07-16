package software.bernie.shadowed.fasterxml.jackson.databind.util;

public final class LinkedNode<T> {
   private final T value;
   private LinkedNode<T> next;

   public LinkedNode(T value, LinkedNode<T> next) {
      this.value = value;
      this.next = next;
   }

   public void linkNext(LinkedNode<T> n) {
      if (this.next != null) {
         throw new IllegalStateException();
      } else {
         this.next = n;
      }
   }

   public LinkedNode<T> next() {
      return this.next;
   }

   public T value() {
      return this.value;
   }

   public static <ST> boolean contains(LinkedNode<ST> node, ST value) {
      while(node != null) {
         if (node.value() == value) {
            return true;
         }

         node = node.next();
      }

      return false;
   }
}

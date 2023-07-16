package software.bernie.shadowed.fasterxml.jackson.databind.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayIterator<T> implements Iterator<T>, Iterable<T> {
   private final T[] _a;
   private int _index;

   public ArrayIterator(T[] a) {
      this._a = a;
      this._index = 0;
   }

   public boolean hasNext() {
      return this._index < this._a.length;
   }

   public T next() {
      if (this._index >= this._a.length) {
         throw new NoSuchElementException();
      } else {
         return this._a[this._index++];
      }
   }

   public void remove() {
      throw new UnsupportedOperationException();
   }

   public Iterator<T> iterator() {
      return this;
   }
}

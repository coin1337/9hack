package software.bernie.shadowed.fasterxml.jackson.databind.util;

import java.lang.reflect.Array;
import java.util.List;

public final class ObjectBuffer {
   private static final int SMALL_CHUNK = 16384;
   private static final int MAX_CHUNK = 262144;
   private LinkedNode<Object[]> _head;
   private LinkedNode<Object[]> _tail;
   private int _size;
   private Object[] _freeBuffer;

   public Object[] resetAndStart() {
      this._reset();
      return this._freeBuffer == null ? (this._freeBuffer = new Object[12]) : this._freeBuffer;
   }

   public Object[] resetAndStart(Object[] base, int count) {
      this._reset();
      if (this._freeBuffer == null || this._freeBuffer.length < count) {
         this._freeBuffer = new Object[Math.max(12, count)];
      }

      System.arraycopy(base, 0, this._freeBuffer, 0, count);
      return this._freeBuffer;
   }

   public Object[] appendCompletedChunk(Object[] fullChunk) {
      LinkedNode<Object[]> next = new LinkedNode(fullChunk, (LinkedNode)null);
      if (this._head == null) {
         this._head = this._tail = next;
      } else {
         this._tail.linkNext(next);
         this._tail = next;
      }

      int len = fullChunk.length;
      this._size += len;
      if (len < 16384) {
         len += len;
      } else if (len < 262144) {
         len += len >> 2;
      }

      return new Object[len];
   }

   public Object[] completeAndClearBuffer(Object[] lastChunk, int lastChunkEntries) {
      int totalSize = lastChunkEntries + this._size;
      Object[] result = new Object[totalSize];
      this._copyTo(result, totalSize, lastChunk, lastChunkEntries);
      this._reset();
      return result;
   }

   public <T> T[] completeAndClearBuffer(Object[] lastChunk, int lastChunkEntries, Class<T> componentType) {
      int totalSize = lastChunkEntries + this._size;
      T[] result = (Object[])((Object[])Array.newInstance(componentType, totalSize));
      this._copyTo(result, totalSize, lastChunk, lastChunkEntries);
      this._reset();
      return result;
   }

   public void completeAndClearBuffer(Object[] lastChunk, int lastChunkEntries, List<Object> resultList) {
      for(LinkedNode n = this._head; n != null; n = n.next()) {
         Object[] curr = (Object[])n.value();
         int i = 0;

         for(int len = curr.length; i < len; ++i) {
            resultList.add(curr[i]);
         }
      }

      for(int i = 0; i < lastChunkEntries; ++i) {
         resultList.add(lastChunk[i]);
      }

      this._reset();
   }

   public int initialCapacity() {
      return this._freeBuffer == null ? 0 : this._freeBuffer.length;
   }

   public int bufferedSize() {
      return this._size;
   }

   protected void _reset() {
      if (this._tail != null) {
         this._freeBuffer = (Object[])this._tail.value();
      }

      this._head = this._tail = null;
      this._size = 0;
   }

   protected final void _copyTo(Object resultArray, int totalSize, Object[] lastChunk, int lastChunkEntries) {
      int ptr = 0;

      for(LinkedNode n = this._head; n != null; n = n.next()) {
         Object[] curr = (Object[])n.value();
         int len = curr.length;
         System.arraycopy(curr, 0, resultArray, ptr, len);
         ptr += len;
      }

      System.arraycopy(lastChunk, 0, resultArray, ptr, lastChunkEntries);
      ptr += lastChunkEntries;
      if (ptr != totalSize) {
         throw new IllegalStateException("Should have gotten " + totalSize + " entries, got " + ptr);
      }
   }
}

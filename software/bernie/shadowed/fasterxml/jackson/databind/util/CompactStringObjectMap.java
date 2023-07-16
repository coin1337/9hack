package software.bernie.shadowed.fasterxml.jackson.databind.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class CompactStringObjectMap implements Serializable {
   private static final long serialVersionUID = 1L;
   private static final CompactStringObjectMap EMPTY = new CompactStringObjectMap(1, 0, new Object[4]);
   private final int _hashMask;
   private final int _spillCount;
   private final Object[] _hashArea;

   private CompactStringObjectMap(int hashMask, int spillCount, Object[] hashArea) {
      this._hashMask = hashMask;
      this._spillCount = spillCount;
      this._hashArea = hashArea;
   }

   public static <T> CompactStringObjectMap construct(Map<String, T> all) {
      if (all.isEmpty()) {
         return EMPTY;
      } else {
         int size = findSize(all.size());
         int mask = size - 1;
         int alloc = (size + (size >> 1)) * 2;
         Object[] hashArea = new Object[alloc];
         int spillCount = 0;

         Entry entry;
         int ix;
         for(Iterator i$ = all.entrySet().iterator(); i$.hasNext(); hashArea[ix + 1] = entry.getValue()) {
            entry = (Entry)i$.next();
            String key = (String)entry.getKey();
            int slot = key.hashCode() & mask;
            ix = slot + slot;
            if (hashArea[ix] != null) {
               ix = size + (slot >> 1) << 1;
               if (hashArea[ix] != null) {
                  ix = (size + (size >> 1) << 1) + spillCount;
                  spillCount += 2;
                  if (ix >= hashArea.length) {
                     hashArea = Arrays.copyOf(hashArea, hashArea.length + 4);
                  }
               }
            }

            hashArea[ix] = key;
         }

         return new CompactStringObjectMap(mask, spillCount, hashArea);
      }
   }

   private static final int findSize(int size) {
      if (size <= 5) {
         return 8;
      } else if (size <= 12) {
         return 16;
      } else {
         int needed = size + (size >> 2);

         int result;
         for(result = 32; result < needed; result += result) {
         }

         return result;
      }
   }

   public Object find(String key) {
      int slot = key.hashCode() & this._hashMask;
      int ix = slot << 1;
      Object match = this._hashArea[ix];
      return match != key && !key.equals(match) ? this._find2(key, slot, match) : this._hashArea[ix + 1];
   }

   private final Object _find2(String key, int slot, Object match) {
      if (match == null) {
         return null;
      } else {
         int hashSize = this._hashMask + 1;
         int ix = hashSize + (slot >> 1) << 1;
         match = this._hashArea[ix];
         if (key.equals(match)) {
            return this._hashArea[ix + 1];
         } else {
            if (match != null) {
               int i = hashSize + (hashSize >> 1) << 1;

               for(int end = i + this._spillCount; i < end; i += 2) {
                  match = this._hashArea[i];
                  if (match == key || key.equals(match)) {
                     return this._hashArea[i + 1];
                  }
               }
            }

            return null;
         }
      }
   }

   public Object findCaseInsensitive(String key) {
      int i = 0;

      for(int end = this._hashArea.length; i < end; i += 2) {
         Object k2 = this._hashArea[i];
         if (k2 != null) {
            String s = (String)k2;
            if (s.equalsIgnoreCase(key)) {
               return this._hashArea[i + 1];
            }
         }
      }

      return null;
   }

   public List<String> keys() {
      int end = this._hashArea.length;
      List<String> keys = new ArrayList(end >> 2);

      for(int i = 0; i < end; i += 2) {
         Object key = this._hashArea[i];
         if (key != null) {
            keys.add((String)key);
         }
      }

      return keys;
   }
}

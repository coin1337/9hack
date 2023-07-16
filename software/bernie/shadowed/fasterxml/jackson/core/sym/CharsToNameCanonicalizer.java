package software.bernie.shadowed.fasterxml.jackson.core.sym;

import java.util.Arrays;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicReference;
import software.bernie.shadowed.fasterxml.jackson.core.JsonFactory;
import software.bernie.shadowed.fasterxml.jackson.core.util.InternCache;

public final class CharsToNameCanonicalizer {
   public static final int HASH_MULT = 33;
   private static final int DEFAULT_T_SIZE = 64;
   private static final int MAX_T_SIZE = 65536;
   static final int MAX_ENTRIES_FOR_REUSE = 12000;
   static final int MAX_COLL_CHAIN_LENGTH = 100;
   private final CharsToNameCanonicalizer _parent;
   private final AtomicReference<CharsToNameCanonicalizer.TableInfo> _tableInfo;
   private final int _seed;
   private final int _flags;
   private boolean _canonicalize;
   private String[] _symbols;
   private CharsToNameCanonicalizer.Bucket[] _buckets;
   private int _size;
   private int _sizeThreshold;
   private int _indexMask;
   private int _longestCollisionList;
   private boolean _hashShared;
   private BitSet _overflows;

   private CharsToNameCanonicalizer(int seed) {
      this._parent = null;
      this._seed = seed;
      this._canonicalize = true;
      this._flags = -1;
      this._hashShared = false;
      this._longestCollisionList = 0;
      this._tableInfo = new AtomicReference(CharsToNameCanonicalizer.TableInfo.createInitial(64));
   }

   private CharsToNameCanonicalizer(CharsToNameCanonicalizer parent, int flags, int seed, CharsToNameCanonicalizer.TableInfo parentState) {
      this._parent = parent;
      this._seed = seed;
      this._tableInfo = null;
      this._flags = flags;
      this._canonicalize = JsonFactory.Feature.CANONICALIZE_FIELD_NAMES.enabledIn(flags);
      this._symbols = parentState.symbols;
      this._buckets = parentState.buckets;
      this._size = parentState.size;
      this._longestCollisionList = parentState.longestCollisionList;
      int arrayLen = this._symbols.length;
      this._sizeThreshold = _thresholdSize(arrayLen);
      this._indexMask = arrayLen - 1;
      this._hashShared = true;
   }

   private static int _thresholdSize(int hashAreaSize) {
      return hashAreaSize - (hashAreaSize >> 2);
   }

   public static CharsToNameCanonicalizer createRoot() {
      long now = System.currentTimeMillis();
      int seed = (int)now + (int)(now >>> 32) | 1;
      return createRoot(seed);
   }

   protected static CharsToNameCanonicalizer createRoot(int seed) {
      return new CharsToNameCanonicalizer(seed);
   }

   public CharsToNameCanonicalizer makeChild(int flags) {
      return new CharsToNameCanonicalizer(this, flags, this._seed, (CharsToNameCanonicalizer.TableInfo)this._tableInfo.get());
   }

   public void release() {
      if (this.maybeDirty()) {
         if (this._parent != null && this._canonicalize) {
            this._parent.mergeChild(new CharsToNameCanonicalizer.TableInfo(this));
            this._hashShared = true;
         }

      }
   }

   private void mergeChild(CharsToNameCanonicalizer.TableInfo childState) {
      int childCount = childState.size;
      CharsToNameCanonicalizer.TableInfo currState = (CharsToNameCanonicalizer.TableInfo)this._tableInfo.get();
      if (childCount != currState.size) {
         if (childCount > 12000) {
            childState = CharsToNameCanonicalizer.TableInfo.createInitial(64);
         }

         this._tableInfo.compareAndSet(currState, childState);
      }
   }

   public int size() {
      return this._tableInfo != null ? ((CharsToNameCanonicalizer.TableInfo)this._tableInfo.get()).size : this._size;
   }

   public int bucketCount() {
      return this._symbols.length;
   }

   public boolean maybeDirty() {
      return !this._hashShared;
   }

   public int hashSeed() {
      return this._seed;
   }

   public int collisionCount() {
      int count = 0;
      CharsToNameCanonicalizer.Bucket[] arr$ = this._buckets;
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         CharsToNameCanonicalizer.Bucket bucket = arr$[i$];
         if (bucket != null) {
            count += bucket.length;
         }
      }

      return count;
   }

   public int maxCollisionLength() {
      return this._longestCollisionList;
   }

   public String findSymbol(char[] buffer, int start, int len, int h) {
      if (len < 1) {
         return "";
      } else if (!this._canonicalize) {
         return new String(buffer, start, len);
      } else {
         int index = this._hashToIndex(h);
         String sym = this._symbols[index];
         if (sym != null) {
            if (sym.length() == len) {
               int i = 0;

               while(sym.charAt(i) == buffer[start + i]) {
                  ++i;
                  if (i == len) {
                     return sym;
                  }
               }
            }

            CharsToNameCanonicalizer.Bucket b = this._buckets[index >> 1];
            if (b != null) {
               sym = b.has(buffer, start, len);
               if (sym != null) {
                  return sym;
               }

               sym = this._findSymbol2(buffer, start, len, b.next);
               if (sym != null) {
                  return sym;
               }
            }
         }

         return this._addSymbol(buffer, start, len, h, index);
      }
   }

   private String _findSymbol2(char[] buffer, int start, int len, CharsToNameCanonicalizer.Bucket b) {
      while(b != null) {
         String sym = b.has(buffer, start, len);
         if (sym != null) {
            return sym;
         }

         b = b.next;
      }

      return null;
   }

   private String _addSymbol(char[] buffer, int start, int len, int h, int index) {
      if (this._hashShared) {
         this.copyArrays();
         this._hashShared = false;
      } else if (this._size >= this._sizeThreshold) {
         this.rehash();
         index = this._hashToIndex(this.calcHash(buffer, start, len));
      }

      String newSymbol = new String(buffer, start, len);
      if (JsonFactory.Feature.INTERN_FIELD_NAMES.enabledIn(this._flags)) {
         newSymbol = InternCache.instance.intern(newSymbol);
      }

      ++this._size;
      if (this._symbols[index] == null) {
         this._symbols[index] = newSymbol;
      } else {
         int bix = index >> 1;
         CharsToNameCanonicalizer.Bucket newB = new CharsToNameCanonicalizer.Bucket(newSymbol, this._buckets[bix]);
         int collLen = newB.length;
         if (collLen > 100) {
            this._handleSpillOverflow(bix, newB);
         } else {
            this._buckets[bix] = newB;
            this._longestCollisionList = Math.max(collLen, this._longestCollisionList);
         }
      }

      return newSymbol;
   }

   private void _handleSpillOverflow(int bindex, CharsToNameCanonicalizer.Bucket newBucket) {
      if (this._overflows == null) {
         this._overflows = new BitSet();
         this._overflows.set(bindex);
      } else if (this._overflows.get(bindex)) {
         if (JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW.enabledIn(this._flags)) {
            this.reportTooManyCollisions(100);
         }

         this._canonicalize = false;
      } else {
         this._overflows.set(bindex);
      }

      this._symbols[bindex + bindex] = newBucket.symbol;
      this._buckets[bindex] = null;
      this._size -= newBucket.length;
      this._longestCollisionList = -1;
   }

   public int _hashToIndex(int rawHash) {
      rawHash += rawHash >>> 15;
      rawHash ^= rawHash << 7;
      rawHash += rawHash >>> 3;
      return rawHash & this._indexMask;
   }

   public int calcHash(char[] buffer, int start, int len) {
      int hash = this._seed;
      int i = start;

      for(int end = start + len; i < end; ++i) {
         hash = hash * 33 + buffer[i];
      }

      return hash == 0 ? 1 : hash;
   }

   public int calcHash(String key) {
      int len = key.length();
      int hash = this._seed;

      for(int i = 0; i < len; ++i) {
         hash = hash * 33 + key.charAt(i);
      }

      return hash == 0 ? 1 : hash;
   }

   private void copyArrays() {
      String[] oldSyms = this._symbols;
      this._symbols = (String[])Arrays.copyOf(oldSyms, oldSyms.length);
      CharsToNameCanonicalizer.Bucket[] oldBuckets = this._buckets;
      this._buckets = (CharsToNameCanonicalizer.Bucket[])Arrays.copyOf(oldBuckets, oldBuckets.length);
   }

   private void rehash() {
      int size = this._symbols.length;
      int newSize = size + size;
      if (newSize > 65536) {
         this._size = 0;
         this._canonicalize = false;
         this._symbols = new String[64];
         this._buckets = new CharsToNameCanonicalizer.Bucket[32];
         this._indexMask = 63;
         this._hashShared = false;
      } else {
         String[] oldSyms = this._symbols;
         CharsToNameCanonicalizer.Bucket[] oldBuckets = this._buckets;
         this._symbols = new String[newSize];
         this._buckets = new CharsToNameCanonicalizer.Bucket[newSize >> 1];
         this._indexMask = newSize - 1;
         this._sizeThreshold = _thresholdSize(newSize);
         int count = 0;
         int maxColl = 0;

         int i;
         int index;
         for(i = 0; i < size; ++i) {
            String symbol = oldSyms[i];
            if (symbol != null) {
               ++count;
               int index = this._hashToIndex(this.calcHash(symbol));
               if (this._symbols[index] == null) {
                  this._symbols[index] = symbol;
               } else {
                  index = index >> 1;
                  CharsToNameCanonicalizer.Bucket newB = new CharsToNameCanonicalizer.Bucket(symbol, this._buckets[index]);
                  this._buckets[index] = newB;
                  maxColl = Math.max(maxColl, newB.length);
               }
            }
         }

         size >>= 1;

         for(i = 0; i < size; ++i) {
            for(CharsToNameCanonicalizer.Bucket b = oldBuckets[i]; b != null; b = b.next) {
               ++count;
               String symbol = b.symbol;
               index = this._hashToIndex(this.calcHash(symbol));
               if (this._symbols[index] == null) {
                  this._symbols[index] = symbol;
               } else {
                  int bix = index >> 1;
                  CharsToNameCanonicalizer.Bucket newB = new CharsToNameCanonicalizer.Bucket(symbol, this._buckets[bix]);
                  this._buckets[bix] = newB;
                  maxColl = Math.max(maxColl, newB.length);
               }
            }
         }

         this._longestCollisionList = maxColl;
         this._overflows = null;
         if (count != this._size) {
            throw new IllegalStateException(String.format("Internal error on SymbolTable.rehash(): had %d entries; now have %d", this._size, count));
         }
      }
   }

   protected void reportTooManyCollisions(int maxLen) {
      throw new IllegalStateException("Longest collision chain in symbol table (of size " + this._size + ") now exceeds maximum, " + maxLen + " -- suspect a DoS attack based on hash collisions");
   }

   private static final class TableInfo {
      final int size;
      final int longestCollisionList;
      final String[] symbols;
      final CharsToNameCanonicalizer.Bucket[] buckets;

      public TableInfo(int size, int longestCollisionList, String[] symbols, CharsToNameCanonicalizer.Bucket[] buckets) {
         this.size = size;
         this.longestCollisionList = longestCollisionList;
         this.symbols = symbols;
         this.buckets = buckets;
      }

      public TableInfo(CharsToNameCanonicalizer src) {
         this.size = src._size;
         this.longestCollisionList = src._longestCollisionList;
         this.symbols = src._symbols;
         this.buckets = src._buckets;
      }

      public static CharsToNameCanonicalizer.TableInfo createInitial(int sz) {
         return new CharsToNameCanonicalizer.TableInfo(0, 0, new String[sz], new CharsToNameCanonicalizer.Bucket[sz >> 1]);
      }
   }

   static final class Bucket {
      public final String symbol;
      public final CharsToNameCanonicalizer.Bucket next;
      public final int length;

      public Bucket(String s, CharsToNameCanonicalizer.Bucket n) {
         this.symbol = s;
         this.next = n;
         this.length = n == null ? 1 : n.length + 1;
      }

      public String has(char[] buf, int start, int len) {
         if (this.symbol.length() != len) {
            return null;
         } else {
            int i = 0;

            while(this.symbol.charAt(i) == buf[start + i]) {
               ++i;
               if (i >= len) {
                  return this.symbol;
               }
            }

            return null;
         }
      }
   }
}

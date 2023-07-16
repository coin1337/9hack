package software.bernie.shadowed.fasterxml.jackson.databind.util;

import java.lang.reflect.Array;
import java.util.HashSet;

public final class ArrayBuilders {
   private ArrayBuilders.BooleanBuilder _booleanBuilder = null;
   private ArrayBuilders.ByteBuilder _byteBuilder = null;
   private ArrayBuilders.ShortBuilder _shortBuilder = null;
   private ArrayBuilders.IntBuilder _intBuilder = null;
   private ArrayBuilders.LongBuilder _longBuilder = null;
   private ArrayBuilders.FloatBuilder _floatBuilder = null;
   private ArrayBuilders.DoubleBuilder _doubleBuilder = null;

   public ArrayBuilders.BooleanBuilder getBooleanBuilder() {
      if (this._booleanBuilder == null) {
         this._booleanBuilder = new ArrayBuilders.BooleanBuilder();
      }

      return this._booleanBuilder;
   }

   public ArrayBuilders.ByteBuilder getByteBuilder() {
      if (this._byteBuilder == null) {
         this._byteBuilder = new ArrayBuilders.ByteBuilder();
      }

      return this._byteBuilder;
   }

   public ArrayBuilders.ShortBuilder getShortBuilder() {
      if (this._shortBuilder == null) {
         this._shortBuilder = new ArrayBuilders.ShortBuilder();
      }

      return this._shortBuilder;
   }

   public ArrayBuilders.IntBuilder getIntBuilder() {
      if (this._intBuilder == null) {
         this._intBuilder = new ArrayBuilders.IntBuilder();
      }

      return this._intBuilder;
   }

   public ArrayBuilders.LongBuilder getLongBuilder() {
      if (this._longBuilder == null) {
         this._longBuilder = new ArrayBuilders.LongBuilder();
      }

      return this._longBuilder;
   }

   public ArrayBuilders.FloatBuilder getFloatBuilder() {
      if (this._floatBuilder == null) {
         this._floatBuilder = new ArrayBuilders.FloatBuilder();
      }

      return this._floatBuilder;
   }

   public ArrayBuilders.DoubleBuilder getDoubleBuilder() {
      if (this._doubleBuilder == null) {
         this._doubleBuilder = new ArrayBuilders.DoubleBuilder();
      }

      return this._doubleBuilder;
   }

   public static Object getArrayComparator(final Object defaultValue) {
      final int length = Array.getLength(defaultValue);
      final Class<?> defaultValueType = defaultValue.getClass();
      return new Object() {
         public boolean equals(Object other) {
            if (other == this) {
               return true;
            } else if (!ClassUtil.hasClass(other, defaultValueType)) {
               return false;
            } else if (Array.getLength(other) != length) {
               return false;
            } else {
               for(int i = 0; i < length; ++i) {
                  Object value1 = Array.get(defaultValue, i);
                  Object value2 = Array.get(other, i);
                  if (value1 != value2 && value1 != null && !value1.equals(value2)) {
                     return false;
                  }
               }

               return true;
            }
         }
      };
   }

   public static <T> HashSet<T> arrayToSet(T[] elements) {
      if (elements == null) {
         return new HashSet();
      } else {
         int len = elements.length;
         HashSet<T> result = new HashSet(len);

         for(int i = 0; i < len; ++i) {
            result.add(elements[i]);
         }

         return result;
      }
   }

   public static <T> T[] insertInListNoDup(T[] array, T element) {
      int len = array.length;

      for(int ix = 0; ix < len; ++ix) {
         if (array[ix] == element) {
            if (ix == 0) {
               return array;
            }

            T[] result = (Object[])((Object[])Array.newInstance(array.getClass().getComponentType(), len));
            System.arraycopy(array, 0, result, 1, ix);
            result[0] = element;
            ++ix;
            int left = len - ix;
            if (left > 0) {
               System.arraycopy(array, ix, result, ix, left);
            }

            return result;
         }
      }

      T[] result = (Object[])((Object[])Array.newInstance(array.getClass().getComponentType(), len + 1));
      if (len > 0) {
         System.arraycopy(array, 0, result, 1, len);
      }

      result[0] = element;
      return result;
   }

   public static final class DoubleBuilder extends PrimitiveArrayBuilder<double[]> {
      public final double[] _constructArray(int len) {
         return new double[len];
      }
   }

   public static final class FloatBuilder extends PrimitiveArrayBuilder<float[]> {
      public final float[] _constructArray(int len) {
         return new float[len];
      }
   }

   public static final class LongBuilder extends PrimitiveArrayBuilder<long[]> {
      public final long[] _constructArray(int len) {
         return new long[len];
      }
   }

   public static final class IntBuilder extends PrimitiveArrayBuilder<int[]> {
      public final int[] _constructArray(int len) {
         return new int[len];
      }
   }

   public static final class ShortBuilder extends PrimitiveArrayBuilder<short[]> {
      public final short[] _constructArray(int len) {
         return new short[len];
      }
   }

   public static final class ByteBuilder extends PrimitiveArrayBuilder<byte[]> {
      public final byte[] _constructArray(int len) {
         return new byte[len];
      }
   }

   public static final class BooleanBuilder extends PrimitiveArrayBuilder<boolean[]> {
      public final boolean[] _constructArray(int len) {
         return new boolean[len];
      }
   }
}

package org.apache.commons.lang3;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableInt;

public class ArrayUtils {
   public static final boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[0];
   public static final Boolean[] EMPTY_BOOLEAN_OBJECT_ARRAY = new Boolean[0];
   public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
   public static final Byte[] EMPTY_BYTE_OBJECT_ARRAY = new Byte[0];
   public static final char[] EMPTY_CHAR_ARRAY = new char[0];
   public static final Character[] EMPTY_CHARACTER_OBJECT_ARRAY = new Character[0];
   public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];
   public static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
   public static final Double[] EMPTY_DOUBLE_OBJECT_ARRAY = new Double[0];
   public static final Field[] EMPTY_FIELD_ARRAY = new Field[0];
   public static final float[] EMPTY_FLOAT_ARRAY = new float[0];
   public static final Float[] EMPTY_FLOAT_OBJECT_ARRAY = new Float[0];
   public static final int[] EMPTY_INT_ARRAY = new int[0];
   public static final Integer[] EMPTY_INTEGER_OBJECT_ARRAY = new Integer[0];
   public static final long[] EMPTY_LONG_ARRAY = new long[0];
   public static final Long[] EMPTY_LONG_OBJECT_ARRAY = new Long[0];
   public static final Method[] EMPTY_METHOD_ARRAY = new Method[0];
   public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
   public static final short[] EMPTY_SHORT_ARRAY = new short[0];
   public static final Short[] EMPTY_SHORT_OBJECT_ARRAY = new Short[0];
   public static final String[] EMPTY_STRING_ARRAY = new String[0];
   public static final Throwable[] EMPTY_THROWABLE_ARRAY = new Throwable[0];
   public static final Type[] EMPTY_TYPE_ARRAY = new Type[0];
   public static final int INDEX_NOT_FOUND = -1;

   public static boolean[] add(boolean[] array, boolean element) {
      boolean[] newArray = (boolean[])((boolean[])copyArrayGrow1(array, Boolean.TYPE));
      newArray[newArray.length - 1] = element;
      return newArray;
   }

   /** @deprecated */
   @Deprecated
   public static boolean[] add(boolean[] array, int index, boolean element) {
      return (boolean[])((boolean[])add(array, index, element, Boolean.TYPE));
   }

   public static byte[] add(byte[] array, byte element) {
      byte[] newArray = (byte[])((byte[])copyArrayGrow1(array, Byte.TYPE));
      newArray[newArray.length - 1] = element;
      return newArray;
   }

   /** @deprecated */
   @Deprecated
   public static byte[] add(byte[] array, int index, byte element) {
      return (byte[])((byte[])add(array, index, element, Byte.TYPE));
   }

   public static char[] add(char[] array, char element) {
      char[] newArray = (char[])((char[])copyArrayGrow1(array, Character.TYPE));
      newArray[newArray.length - 1] = element;
      return newArray;
   }

   /** @deprecated */
   @Deprecated
   public static char[] add(char[] array, int index, char element) {
      return (char[])((char[])add(array, index, element, Character.TYPE));
   }

   public static double[] add(double[] array, double element) {
      double[] newArray = (double[])((double[])copyArrayGrow1(array, Double.TYPE));
      newArray[newArray.length - 1] = element;
      return newArray;
   }

   /** @deprecated */
   @Deprecated
   public static double[] add(double[] array, int index, double element) {
      return (double[])((double[])add(array, index, element, Double.TYPE));
   }

   public static float[] add(float[] array, float element) {
      float[] newArray = (float[])((float[])copyArrayGrow1(array, Float.TYPE));
      newArray[newArray.length - 1] = element;
      return newArray;
   }

   /** @deprecated */
   @Deprecated
   public static float[] add(float[] array, int index, float element) {
      return (float[])((float[])add(array, index, element, Float.TYPE));
   }

   public static int[] add(int[] array, int element) {
      int[] newArray = (int[])((int[])copyArrayGrow1(array, Integer.TYPE));
      newArray[newArray.length - 1] = element;
      return newArray;
   }

   /** @deprecated */
   @Deprecated
   public static int[] add(int[] array, int index, int element) {
      return (int[])((int[])add(array, index, element, Integer.TYPE));
   }

   /** @deprecated */
   @Deprecated
   public static long[] add(long[] array, int index, long element) {
      return (long[])((long[])add(array, index, element, Long.TYPE));
   }

   public static long[] add(long[] array, long element) {
      long[] newArray = (long[])((long[])copyArrayGrow1(array, Long.TYPE));
      newArray[newArray.length - 1] = element;
      return newArray;
   }

   private static Object add(Object array, int index, Object element, Class<?> clss) {
      if (array == null) {
         if (index != 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: 0");
         } else {
            Object joinedArray = Array.newInstance(clss, 1);
            Array.set(joinedArray, 0, element);
            return joinedArray;
         }
      } else {
         int length = Array.getLength(array);
         if (index <= length && index >= 0) {
            Object result = Array.newInstance(clss, length + 1);
            System.arraycopy(array, 0, result, 0, index);
            Array.set(result, index, element);
            if (index < length) {
               System.arraycopy(array, index, result, index + 1, length - index);
            }

            return result;
         } else {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
         }
      }
   }

   /** @deprecated */
   @Deprecated
   public static short[] add(short[] array, int index, short element) {
      return (short[])((short[])add(array, index, element, Short.TYPE));
   }

   public static short[] add(short[] array, short element) {
      short[] newArray = (short[])((short[])copyArrayGrow1(array, Short.TYPE));
      newArray[newArray.length - 1] = element;
      return newArray;
   }

   /** @deprecated */
   @Deprecated
   public static <T> T[] add(T[] array, int index, T element) {
      Class<?> clss = null;
      if (array != null) {
         clss = array.getClass().getComponentType();
      } else {
         if (element == null) {
            throw new IllegalArgumentException("Array and element cannot both be null");
         }

         clss = element.getClass();
      }

      T[] newArray = (Object[])((Object[])add(array, index, element, clss));
      return newArray;
   }

   public static <T> T[] add(T[] array, T element) {
      Class type;
      if (array != null) {
         type = array.getClass().getComponentType();
      } else {
         if (element == null) {
            throw new IllegalArgumentException("Arguments cannot both be null");
         }

         type = element.getClass();
      }

      T[] newArray = (Object[])((Object[])copyArrayGrow1(array, type));
      newArray[newArray.length - 1] = element;
      return newArray;
   }

   public static boolean[] addAll(boolean[] array1, boolean... array2) {
      if (array1 == null) {
         return clone(array2);
      } else if (array2 == null) {
         return clone(array1);
      } else {
         boolean[] joinedArray = new boolean[array1.length + array2.length];
         System.arraycopy(array1, 0, joinedArray, 0, array1.length);
         System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
         return joinedArray;
      }
   }

   public static byte[] addAll(byte[] array1, byte... array2) {
      if (array1 == null) {
         return clone(array2);
      } else if (array2 == null) {
         return clone(array1);
      } else {
         byte[] joinedArray = new byte[array1.length + array2.length];
         System.arraycopy(array1, 0, joinedArray, 0, array1.length);
         System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
         return joinedArray;
      }
   }

   public static char[] addAll(char[] array1, char... array2) {
      if (array1 == null) {
         return clone(array2);
      } else if (array2 == null) {
         return clone(array1);
      } else {
         char[] joinedArray = new char[array1.length + array2.length];
         System.arraycopy(array1, 0, joinedArray, 0, array1.length);
         System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
         return joinedArray;
      }
   }

   public static double[] addAll(double[] array1, double... array2) {
      if (array1 == null) {
         return clone(array2);
      } else if (array2 == null) {
         return clone(array1);
      } else {
         double[] joinedArray = new double[array1.length + array2.length];
         System.arraycopy(array1, 0, joinedArray, 0, array1.length);
         System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
         return joinedArray;
      }
   }

   public static float[] addAll(float[] array1, float... array2) {
      if (array1 == null) {
         return clone(array2);
      } else if (array2 == null) {
         return clone(array1);
      } else {
         float[] joinedArray = new float[array1.length + array2.length];
         System.arraycopy(array1, 0, joinedArray, 0, array1.length);
         System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
         return joinedArray;
      }
   }

   public static int[] addAll(int[] array1, int... array2) {
      if (array1 == null) {
         return clone(array2);
      } else if (array2 == null) {
         return clone(array1);
      } else {
         int[] joinedArray = new int[array1.length + array2.length];
         System.arraycopy(array1, 0, joinedArray, 0, array1.length);
         System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
         return joinedArray;
      }
   }

   public static long[] addAll(long[] array1, long... array2) {
      if (array1 == null) {
         return clone(array2);
      } else if (array2 == null) {
         return clone(array1);
      } else {
         long[] joinedArray = new long[array1.length + array2.length];
         System.arraycopy(array1, 0, joinedArray, 0, array1.length);
         System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
         return joinedArray;
      }
   }

   public static short[] addAll(short[] array1, short... array2) {
      if (array1 == null) {
         return clone(array2);
      } else if (array2 == null) {
         return clone(array1);
      } else {
         short[] joinedArray = new short[array1.length + array2.length];
         System.arraycopy(array1, 0, joinedArray, 0, array1.length);
         System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
         return joinedArray;
      }
   }

   public static <T> T[] addAll(T[] array1, T... array2) {
      if (array1 == null) {
         return clone(array2);
      } else if (array2 == null) {
         return clone(array1);
      } else {
         Class<?> type1 = array1.getClass().getComponentType();
         T[] joinedArray = (Object[])((Object[])Array.newInstance(type1, array1.length + array2.length));
         System.arraycopy(array1, 0, joinedArray, 0, array1.length);

         try {
            System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
            return joinedArray;
         } catch (ArrayStoreException var6) {
            Class<?> type2 = array2.getClass().getComponentType();
            if (!type1.isAssignableFrom(type2)) {
               throw new IllegalArgumentException("Cannot store " + type2.getName() + " in an array of " + type1.getName(), var6);
            } else {
               throw var6;
            }
         }
      }
   }

   public static boolean[] addFirst(boolean[] array, boolean element) {
      return array == null ? add(array, element) : insert(0, (boolean[])array, (boolean[])(element));
   }

   public static byte[] addFirst(byte[] array, byte element) {
      return array == null ? add(array, element) : insert(0, (byte[])array, (byte[])(element));
   }

   public static char[] addFirst(char[] array, char element) {
      return array == null ? add(array, element) : insert(0, (char[])array, (char[])(element));
   }

   public static double[] addFirst(double[] array, double element) {
      return array == null ? add(array, element) : insert(0, (double[])array, (double[])(element));
   }

   public static float[] addFirst(float[] array, float element) {
      return array == null ? add(array, element) : insert(0, (float[])array, (float[])(element));
   }

   public static int[] addFirst(int[] array, int element) {
      return array == null ? add(array, element) : insert(0, (int[])array, (int[])(element));
   }

   public static long[] addFirst(long[] array, long element) {
      return array == null ? add(array, element) : insert(0, (long[])array, (long[])(element));
   }

   public static short[] addFirst(short[] array, short element) {
      return array == null ? add(array, element) : insert(0, (short[])array, (short[])(element));
   }

   public static <T> T[] addFirst(T[] array, T element) {
      return array == null ? add(array, element) : insert(0, (Object[])array, (Object[])(element));
   }

   public static boolean[] clone(boolean[] array) {
      return array == null ? null : (boolean[])array.clone();
   }

   public static byte[] clone(byte[] array) {
      return array == null ? null : (byte[])array.clone();
   }

   public static char[] clone(char[] array) {
      return array == null ? null : (char[])array.clone();
   }

   public static double[] clone(double[] array) {
      return array == null ? null : (double[])array.clone();
   }

   public static float[] clone(float[] array) {
      return array == null ? null : (float[])array.clone();
   }

   public static int[] clone(int[] array) {
      return array == null ? null : (int[])array.clone();
   }

   public static long[] clone(long[] array) {
      return array == null ? null : (long[])array.clone();
   }

   public static short[] clone(short[] array) {
      return array == null ? null : (short[])array.clone();
   }

   public static <T> T[] clone(T[] array) {
      return array == null ? null : (Object[])array.clone();
   }

   public static boolean contains(boolean[] array, boolean valueToFind) {
      return indexOf(array, valueToFind) != -1;
   }

   public static boolean contains(byte[] array, byte valueToFind) {
      return indexOf(array, valueToFind) != -1;
   }

   public static boolean contains(char[] array, char valueToFind) {
      return indexOf(array, valueToFind) != -1;
   }

   public static boolean contains(double[] array, double valueToFind) {
      return indexOf(array, valueToFind) != -1;
   }

   public static boolean contains(double[] array, double valueToFind, double tolerance) {
      return indexOf(array, valueToFind, 0, tolerance) != -1;
   }

   public static boolean contains(float[] array, float valueToFind) {
      return indexOf(array, valueToFind) != -1;
   }

   public static boolean contains(int[] array, int valueToFind) {
      return indexOf(array, valueToFind) != -1;
   }

   public static boolean contains(long[] array, long valueToFind) {
      return indexOf(array, valueToFind) != -1;
   }

   public static boolean contains(Object[] array, Object objectToFind) {
      return indexOf(array, objectToFind) != -1;
   }

   public static boolean contains(short[] array, short valueToFind) {
      return indexOf(array, valueToFind) != -1;
   }

   private static Object copyArrayGrow1(Object array, Class<?> newArrayComponentType) {
      if (array != null) {
         int arrayLength = Array.getLength(array);
         Object newArray = Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);
         System.arraycopy(array, 0, newArray, 0, arrayLength);
         return newArray;
      } else {
         return Array.newInstance(newArrayComponentType, 1);
      }
   }

   public static <T> T get(T[] array, int index) {
      return get(array, index, (Object)null);
   }

   public static <T> T get(T[] array, int index, T defaultValue) {
      return isArrayIndexValid(array, index) ? array[index] : defaultValue;
   }

   public static int getLength(Object array) {
      return array == null ? 0 : Array.getLength(array);
   }

   public static int hashCode(Object array) {
      return (new HashCodeBuilder()).append(array).toHashCode();
   }

   public static BitSet indexesOf(boolean[] array, boolean valueToFind) {
      return indexesOf(array, valueToFind, 0);
   }

   public static BitSet indexesOf(boolean[] array, boolean valueToFind, int startIndex) {
      BitSet bitSet = new BitSet();
      if (array == null) {
         return bitSet;
      } else {
         while(startIndex < array.length) {
            startIndex = indexOf(array, valueToFind, startIndex);
            if (startIndex == -1) {
               break;
            }

            bitSet.set(startIndex);
            ++startIndex;
         }

         return bitSet;
      }
   }

   public static BitSet indexesOf(byte[] array, byte valueToFind) {
      return indexesOf((byte[])array, (byte)valueToFind, 0);
   }

   public static BitSet indexesOf(byte[] array, byte valueToFind, int startIndex) {
      BitSet bitSet = new BitSet();
      if (array == null) {
         return bitSet;
      } else {
         while(startIndex < array.length) {
            startIndex = indexOf(array, valueToFind, startIndex);
            if (startIndex == -1) {
               break;
            }

            bitSet.set(startIndex);
            ++startIndex;
         }

         return bitSet;
      }
   }

   public static BitSet indexesOf(char[] array, char valueToFind) {
      return indexesOf((char[])array, (char)valueToFind, 0);
   }

   public static BitSet indexesOf(char[] array, char valueToFind, int startIndex) {
      BitSet bitSet = new BitSet();
      if (array == null) {
         return bitSet;
      } else {
         while(startIndex < array.length) {
            startIndex = indexOf(array, valueToFind, startIndex);
            if (startIndex == -1) {
               break;
            }

            bitSet.set(startIndex);
            ++startIndex;
         }

         return bitSet;
      }
   }

   public static BitSet indexesOf(double[] array, double valueToFind) {
      return indexesOf(array, valueToFind, 0);
   }

   public static BitSet indexesOf(double[] array, double valueToFind, double tolerance) {
      return indexesOf(array, valueToFind, 0, tolerance);
   }

   public static BitSet indexesOf(double[] array, double valueToFind, int startIndex) {
      BitSet bitSet = new BitSet();
      if (array == null) {
         return bitSet;
      } else {
         while(startIndex < array.length) {
            startIndex = indexOf(array, valueToFind, startIndex);
            if (startIndex == -1) {
               break;
            }

            bitSet.set(startIndex);
            ++startIndex;
         }

         return bitSet;
      }
   }

   public static BitSet indexesOf(double[] array, double valueToFind, int startIndex, double tolerance) {
      BitSet bitSet = new BitSet();
      if (array == null) {
         return bitSet;
      } else {
         while(startIndex < array.length) {
            startIndex = indexOf(array, valueToFind, startIndex, tolerance);
            if (startIndex == -1) {
               break;
            }

            bitSet.set(startIndex);
            ++startIndex;
         }

         return bitSet;
      }
   }

   public static BitSet indexesOf(float[] array, float valueToFind) {
      return indexesOf(array, valueToFind, 0);
   }

   public static BitSet indexesOf(float[] array, float valueToFind, int startIndex) {
      BitSet bitSet = new BitSet();
      if (array == null) {
         return bitSet;
      } else {
         while(startIndex < array.length) {
            startIndex = indexOf(array, valueToFind, startIndex);
            if (startIndex == -1) {
               break;
            }

            bitSet.set(startIndex);
            ++startIndex;
         }

         return bitSet;
      }
   }

   public static BitSet indexesOf(int[] array, int valueToFind) {
      return indexesOf((int[])array, (int)valueToFind, 0);
   }

   public static BitSet indexesOf(int[] array, int valueToFind, int startIndex) {
      BitSet bitSet = new BitSet();
      if (array == null) {
         return bitSet;
      } else {
         while(startIndex < array.length) {
            startIndex = indexOf(array, valueToFind, startIndex);
            if (startIndex == -1) {
               break;
            }

            bitSet.set(startIndex);
            ++startIndex;
         }

         return bitSet;
      }
   }

   public static BitSet indexesOf(long[] array, long valueToFind) {
      return indexesOf(array, valueToFind, 0);
   }

   public static BitSet indexesOf(long[] array, long valueToFind, int startIndex) {
      BitSet bitSet = new BitSet();
      if (array == null) {
         return bitSet;
      } else {
         while(startIndex < array.length) {
            startIndex = indexOf(array, valueToFind, startIndex);
            if (startIndex == -1) {
               break;
            }

            bitSet.set(startIndex);
            ++startIndex;
         }

         return bitSet;
      }
   }

   public static BitSet indexesOf(Object[] array, Object objectToFind) {
      return indexesOf(array, objectToFind, 0);
   }

   public static BitSet indexesOf(Object[] array, Object objectToFind, int startIndex) {
      BitSet bitSet = new BitSet();
      if (array == null) {
         return bitSet;
      } else {
         while(startIndex < array.length) {
            startIndex = indexOf(array, objectToFind, startIndex);
            if (startIndex == -1) {
               break;
            }

            bitSet.set(startIndex);
            ++startIndex;
         }

         return bitSet;
      }
   }

   public static BitSet indexesOf(short[] array, short valueToFind) {
      return indexesOf((short[])array, (short)valueToFind, 0);
   }

   public static BitSet indexesOf(short[] array, short valueToFind, int startIndex) {
      BitSet bitSet = new BitSet();
      if (array == null) {
         return bitSet;
      } else {
         while(startIndex < array.length) {
            startIndex = indexOf(array, valueToFind, startIndex);
            if (startIndex == -1) {
               break;
            }

            bitSet.set(startIndex);
            ++startIndex;
         }

         return bitSet;
      }
   }

   public static int indexOf(boolean[] array, boolean valueToFind) {
      return indexOf(array, valueToFind, 0);
   }

   public static int indexOf(boolean[] array, boolean valueToFind, int startIndex) {
      if (isEmpty(array)) {
         return -1;
      } else {
         if (startIndex < 0) {
            startIndex = 0;
         }

         for(int i = startIndex; i < array.length; ++i) {
            if (valueToFind == array[i]) {
               return i;
            }
         }

         return -1;
      }
   }

   public static int indexOf(byte[] array, byte valueToFind) {
      return indexOf((byte[])array, (byte)valueToFind, 0);
   }

   public static int indexOf(byte[] array, byte valueToFind, int startIndex) {
      if (array == null) {
         return -1;
      } else {
         if (startIndex < 0) {
            startIndex = 0;
         }

         for(int i = startIndex; i < array.length; ++i) {
            if (valueToFind == array[i]) {
               return i;
            }
         }

         return -1;
      }
   }

   public static int indexOf(char[] array, char valueToFind) {
      return indexOf((char[])array, (char)valueToFind, 0);
   }

   public static int indexOf(char[] array, char valueToFind, int startIndex) {
      if (array == null) {
         return -1;
      } else {
         if (startIndex < 0) {
            startIndex = 0;
         }

         for(int i = startIndex; i < array.length; ++i) {
            if (valueToFind == array[i]) {
               return i;
            }
         }

         return -1;
      }
   }

   public static int indexOf(double[] array, double valueToFind) {
      return indexOf(array, valueToFind, 0);
   }

   public static int indexOf(double[] array, double valueToFind, double tolerance) {
      return indexOf(array, valueToFind, 0, tolerance);
   }

   public static int indexOf(double[] array, double valueToFind, int startIndex) {
      if (isEmpty(array)) {
         return -1;
      } else {
         if (startIndex < 0) {
            startIndex = 0;
         }

         for(int i = startIndex; i < array.length; ++i) {
            if (valueToFind == array[i]) {
               return i;
            }
         }

         return -1;
      }
   }

   public static int indexOf(double[] array, double valueToFind, int startIndex, double tolerance) {
      if (isEmpty(array)) {
         return -1;
      } else {
         if (startIndex < 0) {
            startIndex = 0;
         }

         double min = valueToFind - tolerance;
         double max = valueToFind + tolerance;

         for(int i = startIndex; i < array.length; ++i) {
            if (array[i] >= min && array[i] <= max) {
               return i;
            }
         }

         return -1;
      }
   }

   public static int indexOf(float[] array, float valueToFind) {
      return indexOf(array, valueToFind, 0);
   }

   public static int indexOf(float[] array, float valueToFind, int startIndex) {
      if (isEmpty(array)) {
         return -1;
      } else {
         if (startIndex < 0) {
            startIndex = 0;
         }

         for(int i = startIndex; i < array.length; ++i) {
            if (valueToFind == array[i]) {
               return i;
            }
         }

         return -1;
      }
   }

   public static int indexOf(int[] array, int valueToFind) {
      return indexOf((int[])array, (int)valueToFind, 0);
   }

   public static int indexOf(int[] array, int valueToFind, int startIndex) {
      if (array == null) {
         return -1;
      } else {
         if (startIndex < 0) {
            startIndex = 0;
         }

         for(int i = startIndex; i < array.length; ++i) {
            if (valueToFind == array[i]) {
               return i;
            }
         }

         return -1;
      }
   }

   public static int indexOf(long[] array, long valueToFind) {
      return indexOf(array, valueToFind, 0);
   }

   public static int indexOf(long[] array, long valueToFind, int startIndex) {
      if (array == null) {
         return -1;
      } else {
         if (startIndex < 0) {
            startIndex = 0;
         }

         for(int i = startIndex; i < array.length; ++i) {
            if (valueToFind == array[i]) {
               return i;
            }
         }

         return -1;
      }
   }

   public static int indexOf(Object[] array, Object objectToFind) {
      return indexOf(array, objectToFind, 0);
   }

   public static int indexOf(Object[] array, Object objectToFind, int startIndex) {
      if (array == null) {
         return -1;
      } else {
         if (startIndex < 0) {
            startIndex = 0;
         }

         int i;
         if (objectToFind == null) {
            for(i = startIndex; i < array.length; ++i) {
               if (array[i] == null) {
                  return i;
               }
            }
         } else {
            for(i = startIndex; i < array.length; ++i) {
               if (objectToFind.equals(array[i])) {
                  return i;
               }
            }
         }

         return -1;
      }
   }

   public static int indexOf(short[] array, short valueToFind) {
      return indexOf((short[])array, (short)valueToFind, 0);
   }

   public static int indexOf(short[] array, short valueToFind, int startIndex) {
      if (array == null) {
         return -1;
      } else {
         if (startIndex < 0) {
            startIndex = 0;
         }

         for(int i = startIndex; i < array.length; ++i) {
            if (valueToFind == array[i]) {
               return i;
            }
         }

         return -1;
      }
   }

   public static boolean[] insert(int index, boolean[] array, boolean... values) {
      if (array == null) {
         return null;
      } else if (isEmpty(values)) {
         return clone(array);
      } else if (index >= 0 && index <= array.length) {
         boolean[] result = new boolean[array.length + values.length];
         System.arraycopy(values, 0, result, index, values.length);
         if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
         }

         if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
         }

         return result;
      } else {
         throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
      }
   }

   public static byte[] insert(int index, byte[] array, byte... values) {
      if (array == null) {
         return null;
      } else if (isEmpty(values)) {
         return clone(array);
      } else if (index >= 0 && index <= array.length) {
         byte[] result = new byte[array.length + values.length];
         System.arraycopy(values, 0, result, index, values.length);
         if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
         }

         if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
         }

         return result;
      } else {
         throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
      }
   }

   public static char[] insert(int index, char[] array, char... values) {
      if (array == null) {
         return null;
      } else if (isEmpty(values)) {
         return clone(array);
      } else if (index >= 0 && index <= array.length) {
         char[] result = new char[array.length + values.length];
         System.arraycopy(values, 0, result, index, values.length);
         if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
         }

         if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
         }

         return result;
      } else {
         throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
      }
   }

   public static double[] insert(int index, double[] array, double... values) {
      if (array == null) {
         return null;
      } else if (isEmpty(values)) {
         return clone(array);
      } else if (index >= 0 && index <= array.length) {
         double[] result = new double[array.length + values.length];
         System.arraycopy(values, 0, result, index, values.length);
         if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
         }

         if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
         }

         return result;
      } else {
         throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
      }
   }

   public static float[] insert(int index, float[] array, float... values) {
      if (array == null) {
         return null;
      } else if (isEmpty(values)) {
         return clone(array);
      } else if (index >= 0 && index <= array.length) {
         float[] result = new float[array.length + values.length];
         System.arraycopy(values, 0, result, index, values.length);
         if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
         }

         if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
         }

         return result;
      } else {
         throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
      }
   }

   public static int[] insert(int index, int[] array, int... values) {
      if (array == null) {
         return null;
      } else if (isEmpty(values)) {
         return clone(array);
      } else if (index >= 0 && index <= array.length) {
         int[] result = new int[array.length + values.length];
         System.arraycopy(values, 0, result, index, values.length);
         if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
         }

         if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
         }

         return result;
      } else {
         throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
      }
   }

   public static long[] insert(int index, long[] array, long... values) {
      if (array == null) {
         return null;
      } else if (isEmpty(values)) {
         return clone(array);
      } else if (index >= 0 && index <= array.length) {
         long[] result = new long[array.length + values.length];
         System.arraycopy(values, 0, result, index, values.length);
         if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
         }

         if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
         }

         return result;
      } else {
         throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
      }
   }

   public static short[] insert(int index, short[] array, short... values) {
      if (array == null) {
         return null;
      } else if (isEmpty(values)) {
         return clone(array);
      } else if (index >= 0 && index <= array.length) {
         short[] result = new short[array.length + values.length];
         System.arraycopy(values, 0, result, index, values.length);
         if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
         }

         if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
         }

         return result;
      } else {
         throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
      }
   }

   @SafeVarargs
   public static <T> T[] insert(int index, T[] array, T... values) {
      if (array == null) {
         return null;
      } else if (isEmpty(values)) {
         return clone(array);
      } else if (index >= 0 && index <= array.length) {
         Class<?> type = array.getClass().getComponentType();
         T[] result = (Object[])((Object[])Array.newInstance(type, array.length + values.length));
         System.arraycopy(values, 0, result, index, values.length);
         if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
         }

         if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
         }

         return result;
      } else {
         throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
      }
   }

   public static <T> boolean isArrayIndexValid(T[] array, int index) {
      return index >= 0 && getLength(array) > index;
   }

   public static boolean isEmpty(boolean[] array) {
      return getLength(array) == 0;
   }

   public static boolean isEmpty(byte[] array) {
      return getLength(array) == 0;
   }

   public static boolean isEmpty(char[] array) {
      return getLength(array) == 0;
   }

   public static boolean isEmpty(double[] array) {
      return getLength(array) == 0;
   }

   public static boolean isEmpty(float[] array) {
      return getLength(array) == 0;
   }

   public static boolean isEmpty(int[] array) {
      return getLength(array) == 0;
   }

   public static boolean isEmpty(long[] array) {
      return getLength(array) == 0;
   }

   public static boolean isEmpty(Object[] array) {
      return getLength(array) == 0;
   }

   public static boolean isEmpty(short[] array) {
      return getLength(array) == 0;
   }

   /** @deprecated */
   @Deprecated
   public static boolean isEquals(Object array1, Object array2) {
      return (new EqualsBuilder()).append(array1, array2).isEquals();
   }

   public static boolean isNotEmpty(boolean[] array) {
      return !isEmpty(array);
   }

   public static boolean isNotEmpty(byte[] array) {
      return !isEmpty(array);
   }

   public static boolean isNotEmpty(char[] array) {
      return !isEmpty(array);
   }

   public static boolean isNotEmpty(double[] array) {
      return !isEmpty(array);
   }

   public static boolean isNotEmpty(float[] array) {
      return !isEmpty(array);
   }

   public static boolean isNotEmpty(int[] array) {
      return !isEmpty(array);
   }

   public static boolean isNotEmpty(long[] array) {
      return !isEmpty(array);
   }

   public static boolean isNotEmpty(short[] array) {
      return !isEmpty(array);
   }

   public static <T> boolean isNotEmpty(T[] array) {
      return !isEmpty(array);
   }

   public static boolean isSameLength(boolean[] array1, boolean[] array2) {
      return getLength(array1) == getLength(array2);
   }

   public static boolean isSameLength(byte[] array1, byte[] array2) {
      return getLength(array1) == getLength(array2);
   }

   public static boolean isSameLength(char[] array1, char[] array2) {
      return getLength(array1) == getLength(array2);
   }

   public static boolean isSameLength(double[] array1, double[] array2) {
      return getLength(array1) == getLength(array2);
   }

   public static boolean isSameLength(float[] array1, float[] array2) {
      return getLength(array1) == getLength(array2);
   }

   public static boolean isSameLength(int[] array1, int[] array2) {
      return getLength(array1) == getLength(array2);
   }

   public static boolean isSameLength(long[] array1, long[] array2) {
      return getLength(array1) == getLength(array2);
   }

   public static boolean isSameLength(Object array1, Object array2) {
      return getLength(array1) == getLength(array2);
   }

   public static boolean isSameLength(Object[] array1, Object[] array2) {
      return getLength(array1) == getLength(array2);
   }

   public static boolean isSameLength(short[] array1, short[] array2) {
      return getLength(array1) == getLength(array2);
   }

   public static boolean isSameType(Object array1, Object array2) {
      if (array1 != null && array2 != null) {
         return array1.getClass().getName().equals(array2.getClass().getName());
      } else {
         throw new IllegalArgumentException("The Array must not be null");
      }
   }

   public static boolean isSorted(boolean[] array) {
      if (array != null && array.length >= 2) {
         boolean previous = array[0];
         int n = array.length;

         for(int i = 1; i < n; ++i) {
            boolean current = array[i];
            if (BooleanUtils.compare(previous, current) > 0) {
               return false;
            }

            previous = current;
         }

         return true;
      } else {
         return true;
      }
   }

   public static boolean isSorted(byte[] array) {
      if (array != null && array.length >= 2) {
         byte previous = array[0];
         int n = array.length;

         for(int i = 1; i < n; ++i) {
            byte current = array[i];
            if (NumberUtils.compare(previous, current) > 0) {
               return false;
            }

            previous = current;
         }

         return true;
      } else {
         return true;
      }
   }

   public static boolean isSorted(char[] array) {
      if (array != null && array.length >= 2) {
         char previous = array[0];
         int n = array.length;

         for(int i = 1; i < n; ++i) {
            char current = array[i];
            if (CharUtils.compare(previous, current) > 0) {
               return false;
            }

            previous = current;
         }

         return true;
      } else {
         return true;
      }
   }

   public static boolean isSorted(double[] array) {
      if (array != null && array.length >= 2) {
         double previous = array[0];
         int n = array.length;

         for(int i = 1; i < n; ++i) {
            double current = array[i];
            if (Double.compare(previous, current) > 0) {
               return false;
            }

            previous = current;
         }

         return true;
      } else {
         return true;
      }
   }

   public static boolean isSorted(float[] array) {
      if (array != null && array.length >= 2) {
         float previous = array[0];
         int n = array.length;

         for(int i = 1; i < n; ++i) {
            float current = array[i];
            if (Float.compare(previous, current) > 0) {
               return false;
            }

            previous = current;
         }

         return true;
      } else {
         return true;
      }
   }

   public static boolean isSorted(int[] array) {
      if (array != null && array.length >= 2) {
         int previous = array[0];
         int n = array.length;

         for(int i = 1; i < n; ++i) {
            int current = array[i];
            if (NumberUtils.compare(previous, current) > 0) {
               return false;
            }

            previous = current;
         }

         return true;
      } else {
         return true;
      }
   }

   public static boolean isSorted(long[] array) {
      if (array != null && array.length >= 2) {
         long previous = array[0];
         int n = array.length;

         for(int i = 1; i < n; ++i) {
            long current = array[i];
            if (NumberUtils.compare(previous, current) > 0) {
               return false;
            }

            previous = current;
         }

         return true;
      } else {
         return true;
      }
   }

   public static boolean isSorted(short[] array) {
      if (array != null && array.length >= 2) {
         short previous = array[0];
         int n = array.length;

         for(int i = 1; i < n; ++i) {
            short current = array[i];
            if (NumberUtils.compare(previous, current) > 0) {
               return false;
            }

            previous = current;
         }

         return true;
      } else {
         return true;
      }
   }

   public static <T extends Comparable<? super T>> boolean isSorted(T[] array) {
      return isSorted(array, Comparable::compareTo);
   }

   public static <T> boolean isSorted(T[] array, Comparator<T> comparator) {
      if (comparator == null) {
         throw new IllegalArgumentException("Comparator should not be null.");
      } else if (array != null && array.length >= 2) {
         T previous = array[0];
         int n = array.length;

         for(int i = 1; i < n; ++i) {
            T current = array[i];
            if (comparator.compare(previous, current) > 0) {
               return false;
            }

            previous = current;
         }

         return true;
      } else {
         return true;
      }
   }

   public static int lastIndexOf(boolean[] array, boolean valueToFind) {
      return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
   }

   public static int lastIndexOf(boolean[] array, boolean valueToFind, int startIndex) {
      if (isEmpty(array)) {
         return -1;
      } else if (startIndex < 0) {
         return -1;
      } else {
         if (startIndex >= array.length) {
            startIndex = array.length - 1;
         }

         for(int i = startIndex; i >= 0; --i) {
            if (valueToFind == array[i]) {
               return i;
            }
         }

         return -1;
      }
   }

   public static int lastIndexOf(byte[] array, byte valueToFind) {
      return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
   }

   public static int lastIndexOf(byte[] array, byte valueToFind, int startIndex) {
      if (array == null) {
         return -1;
      } else if (startIndex < 0) {
         return -1;
      } else {
         if (startIndex >= array.length) {
            startIndex = array.length - 1;
         }

         for(int i = startIndex; i >= 0; --i) {
            if (valueToFind == array[i]) {
               return i;
            }
         }

         return -1;
      }
   }

   public static int lastIndexOf(char[] array, char valueToFind) {
      return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
   }

   public static int lastIndexOf(char[] array, char valueToFind, int startIndex) {
      if (array == null) {
         return -1;
      } else if (startIndex < 0) {
         return -1;
      } else {
         if (startIndex >= array.length) {
            startIndex = array.length - 1;
         }

         for(int i = startIndex; i >= 0; --i) {
            if (valueToFind == array[i]) {
               return i;
            }
         }

         return -1;
      }
   }

   public static int lastIndexOf(double[] array, double valueToFind) {
      return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
   }

   public static int lastIndexOf(double[] array, double valueToFind, double tolerance) {
      return lastIndexOf(array, valueToFind, Integer.MAX_VALUE, tolerance);
   }

   public static int lastIndexOf(double[] array, double valueToFind, int startIndex) {
      if (isEmpty(array)) {
         return -1;
      } else if (startIndex < 0) {
         return -1;
      } else {
         if (startIndex >= array.length) {
            startIndex = array.length - 1;
         }

         for(int i = startIndex; i >= 0; --i) {
            if (valueToFind == array[i]) {
               return i;
            }
         }

         return -1;
      }
   }

   public static int lastIndexOf(double[] array, double valueToFind, int startIndex, double tolerance) {
      if (isEmpty(array)) {
         return -1;
      } else if (startIndex < 0) {
         return -1;
      } else {
         if (startIndex >= array.length) {
            startIndex = array.length - 1;
         }

         double min = valueToFind - tolerance;
         double max = valueToFind + tolerance;

         for(int i = startIndex; i >= 0; --i) {
            if (array[i] >= min && array[i] <= max) {
               return i;
            }
         }

         return -1;
      }
   }

   public static int lastIndexOf(float[] array, float valueToFind) {
      return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
   }

   public static int lastIndexOf(float[] array, float valueToFind, int startIndex) {
      if (isEmpty(array)) {
         return -1;
      } else if (startIndex < 0) {
         return -1;
      } else {
         if (startIndex >= array.length) {
            startIndex = array.length - 1;
         }

         for(int i = startIndex; i >= 0; --i) {
            if (valueToFind == array[i]) {
               return i;
            }
         }

         return -1;
      }
   }

   public static int lastIndexOf(int[] array, int valueToFind) {
      return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
   }

   public static int lastIndexOf(int[] array, int valueToFind, int startIndex) {
      if (array == null) {
         return -1;
      } else if (startIndex < 0) {
         return -1;
      } else {
         if (startIndex >= array.length) {
            startIndex = array.length - 1;
         }

         for(int i = startIndex; i >= 0; --i) {
            if (valueToFind == array[i]) {
               return i;
            }
         }

         return -1;
      }
   }

   public static int lastIndexOf(long[] array, long valueToFind) {
      return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
   }

   public static int lastIndexOf(long[] array, long valueToFind, int startIndex) {
      if (array == null) {
         return -1;
      } else if (startIndex < 0) {
         return -1;
      } else {
         if (startIndex >= array.length) {
            startIndex = array.length - 1;
         }

         for(int i = startIndex; i >= 0; --i) {
            if (valueToFind == array[i]) {
               return i;
            }
         }

         return -1;
      }
   }

   public static int lastIndexOf(Object[] array, Object objectToFind) {
      return lastIndexOf(array, objectToFind, Integer.MAX_VALUE);
   }

   public static int lastIndexOf(Object[] array, Object objectToFind, int startIndex) {
      if (array == null) {
         return -1;
      } else if (startIndex < 0) {
         return -1;
      } else {
         if (startIndex >= array.length) {
            startIndex = array.length - 1;
         }

         int i;
         if (objectToFind == null) {
            for(i = startIndex; i >= 0; --i) {
               if (array[i] == null) {
                  return i;
               }
            }
         } else if (array.getClass().getComponentType().isInstance(objectToFind)) {
            for(i = startIndex; i >= 0; --i) {
               if (objectToFind.equals(array[i])) {
                  return i;
               }
            }
         }

         return -1;
      }
   }

   public static int lastIndexOf(short[] array, short valueToFind) {
      return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
   }

   public static int lastIndexOf(short[] array, short valueToFind, int startIndex) {
      if (array == null) {
         return -1;
      } else if (startIndex < 0) {
         return -1;
      } else {
         if (startIndex >= array.length) {
            startIndex = array.length - 1;
         }

         for(int i = startIndex; i >= 0; --i) {
            if (valueToFind == array[i]) {
               return i;
            }
         }

         return -1;
      }
   }

   public static boolean[] nullToEmpty(boolean[] array) {
      return isEmpty(array) ? EMPTY_BOOLEAN_ARRAY : array;
   }

   public static Boolean[] nullToEmpty(Boolean[] array) {
      return isEmpty((Object[])array) ? EMPTY_BOOLEAN_OBJECT_ARRAY : array;
   }

   public static byte[] nullToEmpty(byte[] array) {
      return isEmpty(array) ? EMPTY_BYTE_ARRAY : array;
   }

   public static Byte[] nullToEmpty(Byte[] array) {
      return isEmpty((Object[])array) ? EMPTY_BYTE_OBJECT_ARRAY : array;
   }

   public static char[] nullToEmpty(char[] array) {
      return isEmpty(array) ? EMPTY_CHAR_ARRAY : array;
   }

   public static Character[] nullToEmpty(Character[] array) {
      return isEmpty((Object[])array) ? EMPTY_CHARACTER_OBJECT_ARRAY : array;
   }

   public static Class<?>[] nullToEmpty(Class<?>[] array) {
      return isEmpty((Object[])array) ? EMPTY_CLASS_ARRAY : array;
   }

   public static double[] nullToEmpty(double[] array) {
      return isEmpty(array) ? EMPTY_DOUBLE_ARRAY : array;
   }

   public static Double[] nullToEmpty(Double[] array) {
      return isEmpty((Object[])array) ? EMPTY_DOUBLE_OBJECT_ARRAY : array;
   }

   public static float[] nullToEmpty(float[] array) {
      return isEmpty(array) ? EMPTY_FLOAT_ARRAY : array;
   }

   public static Float[] nullToEmpty(Float[] array) {
      return isEmpty((Object[])array) ? EMPTY_FLOAT_OBJECT_ARRAY : array;
   }

   public static int[] nullToEmpty(int[] array) {
      return isEmpty(array) ? EMPTY_INT_ARRAY : array;
   }

   public static Integer[] nullToEmpty(Integer[] array) {
      return isEmpty((Object[])array) ? EMPTY_INTEGER_OBJECT_ARRAY : array;
   }

   public static long[] nullToEmpty(long[] array) {
      return isEmpty(array) ? EMPTY_LONG_ARRAY : array;
   }

   public static Long[] nullToEmpty(Long[] array) {
      return isEmpty((Object[])array) ? EMPTY_LONG_OBJECT_ARRAY : array;
   }

   public static Object[] nullToEmpty(Object[] array) {
      return isEmpty(array) ? EMPTY_OBJECT_ARRAY : array;
   }

   public static short[] nullToEmpty(short[] array) {
      return isEmpty(array) ? EMPTY_SHORT_ARRAY : array;
   }

   public static Short[] nullToEmpty(Short[] array) {
      return isEmpty((Object[])array) ? EMPTY_SHORT_OBJECT_ARRAY : array;
   }

   public static String[] nullToEmpty(String[] array) {
      return isEmpty((Object[])array) ? EMPTY_STRING_ARRAY : array;
   }

   public static <T> T[] nullToEmpty(T[] array, Class<T[]> type) {
      if (type == null) {
         throw new IllegalArgumentException("The type must not be null");
      } else {
         return array == null ? (Object[])type.cast(Array.newInstance(type.getComponentType(), 0)) : array;
      }
   }

   public static boolean[] remove(boolean[] array, int index) {
      return (boolean[])((boolean[])remove((Object)array, index));
   }

   public static byte[] remove(byte[] array, int index) {
      return (byte[])((byte[])remove((Object)array, index));
   }

   public static char[] remove(char[] array, int index) {
      return (char[])((char[])remove((Object)array, index));
   }

   public static double[] remove(double[] array, int index) {
      return (double[])((double[])remove((Object)array, index));
   }

   public static float[] remove(float[] array, int index) {
      return (float[])((float[])remove((Object)array, index));
   }

   public static int[] remove(int[] array, int index) {
      return (int[])((int[])remove((Object)array, index));
   }

   public static long[] remove(long[] array, int index) {
      return (long[])((long[])remove((Object)array, index));
   }

   private static Object remove(Object array, int index) {
      int length = getLength(array);
      if (index >= 0 && index < length) {
         Object result = Array.newInstance(array.getClass().getComponentType(), length - 1);
         System.arraycopy(array, 0, result, 0, index);
         if (index < length - 1) {
            System.arraycopy(array, index + 1, result, index, length - index - 1);
         }

         return result;
      } else {
         throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
      }
   }

   public static short[] remove(short[] array, int index) {
      return (short[])((short[])remove((Object)array, index));
   }

   public static <T> T[] remove(T[] array, int index) {
      return (Object[])((Object[])remove((Object)array, index));
   }

   public static boolean[] removeAll(boolean[] array, int... indices) {
      return (boolean[])((boolean[])removeAll((Object)array, (int[])indices));
   }

   public static byte[] removeAll(byte[] array, int... indices) {
      return (byte[])((byte[])removeAll((Object)array, (int[])indices));
   }

   public static char[] removeAll(char[] array, int... indices) {
      return (char[])((char[])removeAll((Object)array, (int[])indices));
   }

   public static double[] removeAll(double[] array, int... indices) {
      return (double[])((double[])removeAll((Object)array, (int[])indices));
   }

   public static float[] removeAll(float[] array, int... indices) {
      return (float[])((float[])removeAll((Object)array, (int[])indices));
   }

   public static int[] removeAll(int[] array, int... indices) {
      return (int[])((int[])removeAll((Object)array, (int[])indices));
   }

   public static long[] removeAll(long[] array, int... indices) {
      return (long[])((long[])removeAll((Object)array, (int[])indices));
   }

   static Object removeAll(Object array, BitSet indices) {
      if (array == null) {
         return null;
      } else {
         int srcLength = getLength(array);
         int removals = indices.cardinality();
         Object result = Array.newInstance(array.getClass().getComponentType(), srcLength - removals);
         int srcIndex = 0;

         int destIndex;
         int count;
         int set;
         for(destIndex = 0; (set = indices.nextSetBit(srcIndex)) != -1; srcIndex = indices.nextClearBit(set)) {
            count = set - srcIndex;
            if (count > 0) {
               System.arraycopy(array, srcIndex, result, destIndex, count);
               destIndex += count;
            }
         }

         count = srcLength - srcIndex;
         if (count > 0) {
            System.arraycopy(array, srcIndex, result, destIndex, count);
         }

         return result;
      }
   }

   static Object removeAll(Object array, int... indices) {
      int length = getLength(array);
      int diff = 0;
      int[] clonedIndices = clone(indices);
      Arrays.sort(clonedIndices);
      int end;
      int dest;
      if (isNotEmpty(clonedIndices)) {
         int i = clonedIndices.length;
         end = length;

         while(true) {
            --i;
            if (i < 0) {
               break;
            }

            dest = clonedIndices[i];
            if (dest < 0 || dest >= length) {
               throw new IndexOutOfBoundsException("Index: " + dest + ", Length: " + length);
            }

            if (dest < end) {
               ++diff;
               end = dest;
            }
         }
      }

      Object result = Array.newInstance(array.getClass().getComponentType(), length - diff);
      if (diff < length) {
         end = length;
         dest = length - diff;

         for(int i = clonedIndices.length - 1; i >= 0; --i) {
            int index = clonedIndices[i];
            if (end - index > 1) {
               int cp = end - index - 1;
               dest -= cp;
               System.arraycopy(array, index + 1, result, dest, cp);
            }

            end = index;
         }

         if (end > 0) {
            System.arraycopy(array, 0, result, 0, end);
         }
      }

      return result;
   }

   public static short[] removeAll(short[] array, int... indices) {
      return (short[])((short[])removeAll((Object)array, (int[])indices));
   }

   public static <T> T[] removeAll(T[] array, int... indices) {
      return (Object[])((Object[])removeAll((Object)array, (int[])indices));
   }

   /** @deprecated */
   @Deprecated
   public static boolean[] removeAllOccurences(boolean[] array, boolean element) {
      return (boolean[])((boolean[])removeAll((Object)array, (BitSet)indexesOf(array, element)));
   }

   /** @deprecated */
   @Deprecated
   public static byte[] removeAllOccurences(byte[] array, byte element) {
      return (byte[])((byte[])removeAll((Object)array, (BitSet)indexesOf(array, element)));
   }

   /** @deprecated */
   @Deprecated
   public static char[] removeAllOccurences(char[] array, char element) {
      return (char[])((char[])removeAll((Object)array, (BitSet)indexesOf(array, element)));
   }

   /** @deprecated */
   @Deprecated
   public static double[] removeAllOccurences(double[] array, double element) {
      return (double[])((double[])removeAll((Object)array, (BitSet)indexesOf(array, element)));
   }

   /** @deprecated */
   @Deprecated
   public static float[] removeAllOccurences(float[] array, float element) {
      return (float[])((float[])removeAll((Object)array, (BitSet)indexesOf(array, element)));
   }

   /** @deprecated */
   @Deprecated
   public static int[] removeAllOccurences(int[] array, int element) {
      return (int[])((int[])removeAll((Object)array, (BitSet)indexesOf(array, element)));
   }

   /** @deprecated */
   @Deprecated
   public static long[] removeAllOccurences(long[] array, long element) {
      return (long[])((long[])removeAll((Object)array, (BitSet)indexesOf(array, element)));
   }

   /** @deprecated */
   @Deprecated
   public static short[] removeAllOccurences(short[] array, short element) {
      return (short[])((short[])removeAll((Object)array, (BitSet)indexesOf(array, element)));
   }

   /** @deprecated */
   @Deprecated
   public static <T> T[] removeAllOccurences(T[] array, T element) {
      return (Object[])((Object[])removeAll((Object)array, (BitSet)indexesOf(array, element)));
   }

   public static boolean[] removeAllOccurrences(boolean[] array, boolean element) {
      return (boolean[])((boolean[])removeAll((Object)array, (BitSet)indexesOf(array, element)));
   }

   public static byte[] removeAllOccurrences(byte[] array, byte element) {
      return (byte[])((byte[])removeAll((Object)array, (BitSet)indexesOf(array, element)));
   }

   public static char[] removeAllOccurrences(char[] array, char element) {
      return (char[])((char[])removeAll((Object)array, (BitSet)indexesOf(array, element)));
   }

   public static double[] removeAllOccurrences(double[] array, double element) {
      return (double[])((double[])removeAll((Object)array, (BitSet)indexesOf(array, element)));
   }

   public static float[] removeAllOccurrences(float[] array, float element) {
      return (float[])((float[])removeAll((Object)array, (BitSet)indexesOf(array, element)));
   }

   public static int[] removeAllOccurrences(int[] array, int element) {
      return (int[])((int[])removeAll((Object)array, (BitSet)indexesOf(array, element)));
   }

   public static long[] removeAllOccurrences(long[] array, long element) {
      return (long[])((long[])removeAll((Object)array, (BitSet)indexesOf(array, element)));
   }

   public static short[] removeAllOccurrences(short[] array, short element) {
      return (short[])((short[])removeAll((Object)array, (BitSet)indexesOf(array, element)));
   }

   public static <T> T[] removeAllOccurrences(T[] array, T element) {
      return (Object[])((Object[])removeAll((Object)array, (BitSet)indexesOf(array, element)));
   }

   public static boolean[] removeElement(boolean[] array, boolean element) {
      int index = indexOf(array, element);
      return index == -1 ? clone(array) : remove(array, index);
   }

   public static byte[] removeElement(byte[] array, byte element) {
      int index = indexOf(array, element);
      return index == -1 ? clone(array) : remove(array, index);
   }

   public static char[] removeElement(char[] array, char element) {
      int index = indexOf(array, element);
      return index == -1 ? clone(array) : remove(array, index);
   }

   public static double[] removeElement(double[] array, double element) {
      int index = indexOf(array, element);
      return index == -1 ? clone(array) : remove(array, index);
   }

   public static float[] removeElement(float[] array, float element) {
      int index = indexOf(array, element);
      return index == -1 ? clone(array) : remove(array, index);
   }

   public static int[] removeElement(int[] array, int element) {
      int index = indexOf(array, element);
      return index == -1 ? clone(array) : remove(array, index);
   }

   public static long[] removeElement(long[] array, long element) {
      int index = indexOf(array, element);
      return index == -1 ? clone(array) : remove(array, index);
   }

   public static short[] removeElement(short[] array, short element) {
      int index = indexOf(array, element);
      return index == -1 ? clone(array) : remove(array, index);
   }

   public static <T> T[] removeElement(T[] array, Object element) {
      int index = indexOf(array, element);
      return index == -1 ? clone(array) : remove(array, index);
   }

   public static boolean[] removeElements(boolean[] array, boolean... values) {
      if (!isEmpty(array) && !isEmpty(values)) {
         HashMap<Boolean, MutableInt> occurrences = new HashMap(2);
         boolean[] var3 = values;
         int i = values.length;

         for(int var5 = 0; var5 < i; ++var5) {
            boolean v = var3[var5];
            Boolean boxed = v;
            MutableInt count = (MutableInt)occurrences.get(boxed);
            if (count == null) {
               occurrences.put(boxed, new MutableInt(1));
            } else {
               count.increment();
            }
         }

         BitSet toRemove = new BitSet();

         for(i = 0; i < array.length; ++i) {
            boolean key = array[i];
            MutableInt count = (MutableInt)occurrences.get(key);
            if (count != null) {
               if (count.decrementAndGet() == 0) {
                  occurrences.remove(key);
               }

               toRemove.set(i);
            }
         }

         return (boolean[])((boolean[])removeAll((Object)array, (BitSet)toRemove));
      } else {
         return clone(array);
      }
   }

   public static byte[] removeElements(byte[] array, byte... values) {
      if (!isEmpty(array) && !isEmpty(values)) {
         Map<Byte, MutableInt> occurrences = new HashMap(values.length);
         byte[] var3 = values;
         int i = values.length;

         for(int var5 = 0; var5 < i; ++var5) {
            byte v = var3[var5];
            Byte boxed = v;
            MutableInt count = (MutableInt)occurrences.get(boxed);
            if (count == null) {
               occurrences.put(boxed, new MutableInt(1));
            } else {
               count.increment();
            }
         }

         BitSet toRemove = new BitSet();

         for(i = 0; i < array.length; ++i) {
            byte key = array[i];
            MutableInt count = (MutableInt)occurrences.get(key);
            if (count != null) {
               if (count.decrementAndGet() == 0) {
                  occurrences.remove(key);
               }

               toRemove.set(i);
            }
         }

         return (byte[])((byte[])removeAll((Object)array, (BitSet)toRemove));
      } else {
         return clone(array);
      }
   }

   public static char[] removeElements(char[] array, char... values) {
      if (!isEmpty(array) && !isEmpty(values)) {
         HashMap<Character, MutableInt> occurrences = new HashMap(values.length);
         char[] var3 = values;
         int i = values.length;

         for(int var5 = 0; var5 < i; ++var5) {
            char v = var3[var5];
            Character boxed = v;
            MutableInt count = (MutableInt)occurrences.get(boxed);
            if (count == null) {
               occurrences.put(boxed, new MutableInt(1));
            } else {
               count.increment();
            }
         }

         BitSet toRemove = new BitSet();

         for(i = 0; i < array.length; ++i) {
            char key = array[i];
            MutableInt count = (MutableInt)occurrences.get(key);
            if (count != null) {
               if (count.decrementAndGet() == 0) {
                  occurrences.remove(key);
               }

               toRemove.set(i);
            }
         }

         return (char[])((char[])removeAll((Object)array, (BitSet)toRemove));
      } else {
         return clone(array);
      }
   }

   public static double[] removeElements(double[] array, double... values) {
      if (!isEmpty(array) && !isEmpty(values)) {
         HashMap<Double, MutableInt> occurrences = new HashMap(values.length);
         double[] var3 = values;
         int i = values.length;

         for(int var5 = 0; var5 < i; ++var5) {
            double v = var3[var5];
            Double boxed = v;
            MutableInt count = (MutableInt)occurrences.get(boxed);
            if (count == null) {
               occurrences.put(boxed, new MutableInt(1));
            } else {
               count.increment();
            }
         }

         BitSet toRemove = new BitSet();

         for(i = 0; i < array.length; ++i) {
            double key = array[i];
            MutableInt count = (MutableInt)occurrences.get(key);
            if (count != null) {
               if (count.decrementAndGet() == 0) {
                  occurrences.remove(key);
               }

               toRemove.set(i);
            }
         }

         return (double[])((double[])removeAll((Object)array, (BitSet)toRemove));
      } else {
         return clone(array);
      }
   }

   public static float[] removeElements(float[] array, float... values) {
      if (!isEmpty(array) && !isEmpty(values)) {
         HashMap<Float, MutableInt> occurrences = new HashMap(values.length);
         float[] var3 = values;
         int i = values.length;

         for(int var5 = 0; var5 < i; ++var5) {
            float v = var3[var5];
            Float boxed = v;
            MutableInt count = (MutableInt)occurrences.get(boxed);
            if (count == null) {
               occurrences.put(boxed, new MutableInt(1));
            } else {
               count.increment();
            }
         }

         BitSet toRemove = new BitSet();

         for(i = 0; i < array.length; ++i) {
            float key = array[i];
            MutableInt count = (MutableInt)occurrences.get(key);
            if (count != null) {
               if (count.decrementAndGet() == 0) {
                  occurrences.remove(key);
               }

               toRemove.set(i);
            }
         }

         return (float[])((float[])removeAll((Object)array, (BitSet)toRemove));
      } else {
         return clone(array);
      }
   }

   public static int[] removeElements(int[] array, int... values) {
      if (!isEmpty(array) && !isEmpty(values)) {
         HashMap<Integer, MutableInt> occurrences = new HashMap(values.length);
         int[] var3 = values;
         int i = values.length;

         int key;
         for(key = 0; key < i; ++key) {
            int v = var3[key];
            Integer boxed = v;
            MutableInt count = (MutableInt)occurrences.get(boxed);
            if (count == null) {
               occurrences.put(boxed, new MutableInt(1));
            } else {
               count.increment();
            }
         }

         BitSet toRemove = new BitSet();

         for(i = 0; i < array.length; ++i) {
            key = array[i];
            MutableInt count = (MutableInt)occurrences.get(key);
            if (count != null) {
               if (count.decrementAndGet() == 0) {
                  occurrences.remove(key);
               }

               toRemove.set(i);
            }
         }

         return (int[])((int[])removeAll((Object)array, (BitSet)toRemove));
      } else {
         return clone(array);
      }
   }

   public static long[] removeElements(long[] array, long... values) {
      if (!isEmpty(array) && !isEmpty(values)) {
         HashMap<Long, MutableInt> occurrences = new HashMap(values.length);
         long[] var3 = values;
         int i = values.length;

         for(int var5 = 0; var5 < i; ++var5) {
            long v = var3[var5];
            Long boxed = v;
            MutableInt count = (MutableInt)occurrences.get(boxed);
            if (count == null) {
               occurrences.put(boxed, new MutableInt(1));
            } else {
               count.increment();
            }
         }

         BitSet toRemove = new BitSet();

         for(i = 0; i < array.length; ++i) {
            long key = array[i];
            MutableInt count = (MutableInt)occurrences.get(key);
            if (count != null) {
               if (count.decrementAndGet() == 0) {
                  occurrences.remove(key);
               }

               toRemove.set(i);
            }
         }

         return (long[])((long[])removeAll((Object)array, (BitSet)toRemove));
      } else {
         return clone(array);
      }
   }

   public static short[] removeElements(short[] array, short... values) {
      if (!isEmpty(array) && !isEmpty(values)) {
         HashMap<Short, MutableInt> occurrences = new HashMap(values.length);
         short[] var3 = values;
         int i = values.length;

         for(int var5 = 0; var5 < i; ++var5) {
            short v = var3[var5];
            Short boxed = v;
            MutableInt count = (MutableInt)occurrences.get(boxed);
            if (count == null) {
               occurrences.put(boxed, new MutableInt(1));
            } else {
               count.increment();
            }
         }

         BitSet toRemove = new BitSet();

         for(i = 0; i < array.length; ++i) {
            short key = array[i];
            MutableInt count = (MutableInt)occurrences.get(key);
            if (count != null) {
               if (count.decrementAndGet() == 0) {
                  occurrences.remove(key);
               }

               toRemove.set(i);
            }
         }

         return (short[])((short[])removeAll((Object)array, (BitSet)toRemove));
      } else {
         return clone(array);
      }
   }

   @SafeVarargs
   public static <T> T[] removeElements(T[] array, T... values) {
      if (!isEmpty(array) && !isEmpty(values)) {
         HashMap<T, MutableInt> occurrences = new HashMap(values.length);
         Object[] var3 = values;
         int i = values.length;

         for(int var5 = 0; var5 < i; ++var5) {
            T v = var3[var5];
            MutableInt count = (MutableInt)occurrences.get(v);
            if (count == null) {
               occurrences.put(v, new MutableInt(1));
            } else {
               count.increment();
            }
         }

         BitSet toRemove = new BitSet();

         for(i = 0; i < array.length; ++i) {
            T key = array[i];
            MutableInt count = (MutableInt)occurrences.get(key);
            if (count != null) {
               if (count.decrementAndGet() == 0) {
                  occurrences.remove(key);
               }

               toRemove.set(i);
            }
         }

         T[] result = (Object[])((Object[])removeAll((Object)array, (BitSet)toRemove));
         return result;
      } else {
         return clone(array);
      }
   }

   public static void reverse(boolean[] array) {
      if (array != null) {
         reverse((boolean[])array, 0, array.length);
      }
   }

   public static void reverse(boolean[] array, int startIndexInclusive, int endIndexExclusive) {
      if (array != null) {
         int i = Math.max(startIndexInclusive, 0);

         for(int j = Math.min(array.length, endIndexExclusive) - 1; j > i; ++i) {
            boolean tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            --j;
         }

      }
   }

   public static void reverse(byte[] array) {
      if (array != null) {
         reverse((byte[])array, 0, array.length);
      }
   }

   public static void reverse(byte[] array, int startIndexInclusive, int endIndexExclusive) {
      if (array != null) {
         int i = Math.max(startIndexInclusive, 0);

         for(int j = Math.min(array.length, endIndexExclusive) - 1; j > i; ++i) {
            byte tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            --j;
         }

      }
   }

   public static void reverse(char[] array) {
      if (array != null) {
         reverse((char[])array, 0, array.length);
      }
   }

   public static void reverse(char[] array, int startIndexInclusive, int endIndexExclusive) {
      if (array != null) {
         int i = Math.max(startIndexInclusive, 0);

         for(int j = Math.min(array.length, endIndexExclusive) - 1; j > i; ++i) {
            char tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            --j;
         }

      }
   }

   public static void reverse(double[] array) {
      if (array != null) {
         reverse((double[])array, 0, array.length);
      }
   }

   public static void reverse(double[] array, int startIndexInclusive, int endIndexExclusive) {
      if (array != null) {
         int i = Math.max(startIndexInclusive, 0);

         for(int j = Math.min(array.length, endIndexExclusive) - 1; j > i; ++i) {
            double tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            --j;
         }

      }
   }

   public static void reverse(float[] array) {
      if (array != null) {
         reverse((float[])array, 0, array.length);
      }
   }

   public static void reverse(float[] array, int startIndexInclusive, int endIndexExclusive) {
      if (array != null) {
         int i = Math.max(startIndexInclusive, 0);

         for(int j = Math.min(array.length, endIndexExclusive) - 1; j > i; ++i) {
            float tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            --j;
         }

      }
   }

   public static void reverse(int[] array) {
      if (array != null) {
         reverse((int[])array, 0, array.length);
      }
   }

   public static void reverse(int[] array, int startIndexInclusive, int endIndexExclusive) {
      if (array != null) {
         int i = Math.max(startIndexInclusive, 0);

         for(int j = Math.min(array.length, endIndexExclusive) - 1; j > i; ++i) {
            int tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            --j;
         }

      }
   }

   public static void reverse(long[] array) {
      if (array != null) {
         reverse((long[])array, 0, array.length);
      }
   }

   public static void reverse(long[] array, int startIndexInclusive, int endIndexExclusive) {
      if (array != null) {
         int i = Math.max(startIndexInclusive, 0);

         for(int j = Math.min(array.length, endIndexExclusive) - 1; j > i; ++i) {
            long tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            --j;
         }

      }
   }

   public static void reverse(Object[] array) {
      if (array != null) {
         reverse((Object[])array, 0, array.length);
      }
   }

   public static void reverse(Object[] array, int startIndexInclusive, int endIndexExclusive) {
      if (array != null) {
         int i = Math.max(startIndexInclusive, 0);

         for(int j = Math.min(array.length, endIndexExclusive) - 1; j > i; ++i) {
            Object tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            --j;
         }

      }
   }

   public static void reverse(short[] array) {
      if (array != null) {
         reverse((short[])array, 0, array.length);
      }
   }

   public static void reverse(short[] array, int startIndexInclusive, int endIndexExclusive) {
      if (array != null) {
         int i = Math.max(startIndexInclusive, 0);

         for(int j = Math.min(array.length, endIndexExclusive) - 1; j > i; ++i) {
            short tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            --j;
         }

      }
   }

   public static void shift(boolean[] array, int offset) {
      if (array != null) {
         shift((boolean[])array, 0, array.length, offset);
      }
   }

   public static void shift(boolean[] array, int startIndexInclusive, int endIndexExclusive, int offset) {
      if (array != null) {
         if (startIndexInclusive < array.length - 1 && endIndexExclusive > 0) {
            if (startIndexInclusive < 0) {
               startIndexInclusive = 0;
            }

            if (endIndexExclusive >= array.length) {
               endIndexExclusive = array.length;
            }

            int n = endIndexExclusive - startIndexInclusive;
            if (n > 1) {
               offset %= n;
               if (offset < 0) {
                  offset += n;
               }

               while(n > 1 && offset > 0) {
                  int n_offset = n - offset;
                  if (offset > n_offset) {
                     swap(array, startIndexInclusive, startIndexInclusive + n - n_offset, n_offset);
                     n = offset;
                     offset -= n_offset;
                  } else {
                     if (offset >= n_offset) {
                        swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                        break;
                     }

                     swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                     startIndexInclusive += offset;
                     n = n_offset;
                  }
               }

            }
         }
      }
   }

   public static void shift(byte[] array, int offset) {
      if (array != null) {
         shift((byte[])array, 0, array.length, offset);
      }
   }

   public static void shift(byte[] array, int startIndexInclusive, int endIndexExclusive, int offset) {
      if (array != null) {
         if (startIndexInclusive < array.length - 1 && endIndexExclusive > 0) {
            if (startIndexInclusive < 0) {
               startIndexInclusive = 0;
            }

            if (endIndexExclusive >= array.length) {
               endIndexExclusive = array.length;
            }

            int n = endIndexExclusive - startIndexInclusive;
            if (n > 1) {
               offset %= n;
               if (offset < 0) {
                  offset += n;
               }

               while(n > 1 && offset > 0) {
                  int n_offset = n - offset;
                  if (offset > n_offset) {
                     swap(array, startIndexInclusive, startIndexInclusive + n - n_offset, n_offset);
                     n = offset;
                     offset -= n_offset;
                  } else {
                     if (offset >= n_offset) {
                        swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                        break;
                     }

                     swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                     startIndexInclusive += offset;
                     n = n_offset;
                  }
               }

            }
         }
      }
   }

   public static void shift(char[] array, int offset) {
      if (array != null) {
         shift((char[])array, 0, array.length, offset);
      }
   }

   public static void shift(char[] array, int startIndexInclusive, int endIndexExclusive, int offset) {
      if (array != null) {
         if (startIndexInclusive < array.length - 1 && endIndexExclusive > 0) {
            if (startIndexInclusive < 0) {
               startIndexInclusive = 0;
            }

            if (endIndexExclusive >= array.length) {
               endIndexExclusive = array.length;
            }

            int n = endIndexExclusive - startIndexInclusive;
            if (n > 1) {
               offset %= n;
               if (offset < 0) {
                  offset += n;
               }

               while(n > 1 && offset > 0) {
                  int n_offset = n - offset;
                  if (offset > n_offset) {
                     swap(array, startIndexInclusive, startIndexInclusive + n - n_offset, n_offset);
                     n = offset;
                     offset -= n_offset;
                  } else {
                     if (offset >= n_offset) {
                        swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                        break;
                     }

                     swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                     startIndexInclusive += offset;
                     n = n_offset;
                  }
               }

            }
         }
      }
   }

   public static void shift(double[] array, int offset) {
      if (array != null) {
         shift((double[])array, 0, array.length, offset);
      }
   }

   public static void shift(double[] array, int startIndexInclusive, int endIndexExclusive, int offset) {
      if (array != null) {
         if (startIndexInclusive < array.length - 1 && endIndexExclusive > 0) {
            if (startIndexInclusive < 0) {
               startIndexInclusive = 0;
            }

            if (endIndexExclusive >= array.length) {
               endIndexExclusive = array.length;
            }

            int n = endIndexExclusive - startIndexInclusive;
            if (n > 1) {
               offset %= n;
               if (offset < 0) {
                  offset += n;
               }

               while(n > 1 && offset > 0) {
                  int n_offset = n - offset;
                  if (offset > n_offset) {
                     swap(array, startIndexInclusive, startIndexInclusive + n - n_offset, n_offset);
                     n = offset;
                     offset -= n_offset;
                  } else {
                     if (offset >= n_offset) {
                        swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                        break;
                     }

                     swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                     startIndexInclusive += offset;
                     n = n_offset;
                  }
               }

            }
         }
      }
   }

   public static void shift(float[] array, int offset) {
      if (array != null) {
         shift((float[])array, 0, array.length, offset);
      }
   }

   public static void shift(float[] array, int startIndexInclusive, int endIndexExclusive, int offset) {
      if (array != null) {
         if (startIndexInclusive < array.length - 1 && endIndexExclusive > 0) {
            if (startIndexInclusive < 0) {
               startIndexInclusive = 0;
            }

            if (endIndexExclusive >= array.length) {
               endIndexExclusive = array.length;
            }

            int n = endIndexExclusive - startIndexInclusive;
            if (n > 1) {
               offset %= n;
               if (offset < 0) {
                  offset += n;
               }

               while(n > 1 && offset > 0) {
                  int n_offset = n - offset;
                  if (offset > n_offset) {
                     swap(array, startIndexInclusive, startIndexInclusive + n - n_offset, n_offset);
                     n = offset;
                     offset -= n_offset;
                  } else {
                     if (offset >= n_offset) {
                        swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                        break;
                     }

                     swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                     startIndexInclusive += offset;
                     n = n_offset;
                  }
               }

            }
         }
      }
   }

   public static void shift(int[] array, int offset) {
      if (array != null) {
         shift((int[])array, 0, array.length, offset);
      }
   }

   public static void shift(int[] array, int startIndexInclusive, int endIndexExclusive, int offset) {
      if (array != null) {
         if (startIndexInclusive < array.length - 1 && endIndexExclusive > 0) {
            if (startIndexInclusive < 0) {
               startIndexInclusive = 0;
            }

            if (endIndexExclusive >= array.length) {
               endIndexExclusive = array.length;
            }

            int n = endIndexExclusive - startIndexInclusive;
            if (n > 1) {
               offset %= n;
               if (offset < 0) {
                  offset += n;
               }

               while(n > 1 && offset > 0) {
                  int n_offset = n - offset;
                  if (offset > n_offset) {
                     swap(array, startIndexInclusive, startIndexInclusive + n - n_offset, n_offset);
                     n = offset;
                     offset -= n_offset;
                  } else {
                     if (offset >= n_offset) {
                        swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                        break;
                     }

                     swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                     startIndexInclusive += offset;
                     n = n_offset;
                  }
               }

            }
         }
      }
   }

   public static void shift(long[] array, int offset) {
      if (array != null) {
         shift((long[])array, 0, array.length, offset);
      }
   }

   public static void shift(long[] array, int startIndexInclusive, int endIndexExclusive, int offset) {
      if (array != null) {
         if (startIndexInclusive < array.length - 1 && endIndexExclusive > 0) {
            if (startIndexInclusive < 0) {
               startIndexInclusive = 0;
            }

            if (endIndexExclusive >= array.length) {
               endIndexExclusive = array.length;
            }

            int n = endIndexExclusive - startIndexInclusive;
            if (n > 1) {
               offset %= n;
               if (offset < 0) {
                  offset += n;
               }

               while(n > 1 && offset > 0) {
                  int n_offset = n - offset;
                  if (offset > n_offset) {
                     swap(array, startIndexInclusive, startIndexInclusive + n - n_offset, n_offset);
                     n = offset;
                     offset -= n_offset;
                  } else {
                     if (offset >= n_offset) {
                        swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                        break;
                     }

                     swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                     startIndexInclusive += offset;
                     n = n_offset;
                  }
               }

            }
         }
      }
   }

   public static void shift(Object[] array, int offset) {
      if (array != null) {
         shift((Object[])array, 0, array.length, offset);
      }
   }

   public static void shift(Object[] array, int startIndexInclusive, int endIndexExclusive, int offset) {
      if (array != null) {
         if (startIndexInclusive < array.length - 1 && endIndexExclusive > 0) {
            if (startIndexInclusive < 0) {
               startIndexInclusive = 0;
            }

            if (endIndexExclusive >= array.length) {
               endIndexExclusive = array.length;
            }

            int n = endIndexExclusive - startIndexInclusive;
            if (n > 1) {
               offset %= n;
               if (offset < 0) {
                  offset += n;
               }

               while(n > 1 && offset > 0) {
                  int n_offset = n - offset;
                  if (offset > n_offset) {
                     swap(array, startIndexInclusive, startIndexInclusive + n - n_offset, n_offset);
                     n = offset;
                     offset -= n_offset;
                  } else {
                     if (offset >= n_offset) {
                        swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                        break;
                     }

                     swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                     startIndexInclusive += offset;
                     n = n_offset;
                  }
               }

            }
         }
      }
   }

   public static void shift(short[] array, int offset) {
      if (array != null) {
         shift((short[])array, 0, array.length, offset);
      }
   }

   public static void shift(short[] array, int startIndexInclusive, int endIndexExclusive, int offset) {
      if (array != null) {
         if (startIndexInclusive < array.length - 1 && endIndexExclusive > 0) {
            if (startIndexInclusive < 0) {
               startIndexInclusive = 0;
            }

            if (endIndexExclusive >= array.length) {
               endIndexExclusive = array.length;
            }

            int n = endIndexExclusive - startIndexInclusive;
            if (n > 1) {
               offset %= n;
               if (offset < 0) {
                  offset += n;
               }

               while(n > 1 && offset > 0) {
                  int n_offset = n - offset;
                  if (offset > n_offset) {
                     swap(array, startIndexInclusive, startIndexInclusive + n - n_offset, n_offset);
                     n = offset;
                     offset -= n_offset;
                  } else {
                     if (offset >= n_offset) {
                        swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                        break;
                     }

                     swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                     startIndexInclusive += offset;
                     n = n_offset;
                  }
               }

            }
         }
      }
   }

   public static void shuffle(boolean[] array) {
      shuffle(array, new Random());
   }

   public static void shuffle(boolean[] array, Random random) {
      for(int i = array.length; i > 1; --i) {
         swap((boolean[])array, i - 1, random.nextInt(i), 1);
      }

   }

   public static void shuffle(byte[] array) {
      shuffle(array, new Random());
   }

   public static void shuffle(byte[] array, Random random) {
      for(int i = array.length; i > 1; --i) {
         swap((byte[])array, i - 1, random.nextInt(i), 1);
      }

   }

   public static void shuffle(char[] array) {
      shuffle(array, new Random());
   }

   public static void shuffle(char[] array, Random random) {
      for(int i = array.length; i > 1; --i) {
         swap((char[])array, i - 1, random.nextInt(i), 1);
      }

   }

   public static void shuffle(double[] array) {
      shuffle(array, new Random());
   }

   public static void shuffle(double[] array, Random random) {
      for(int i = array.length; i > 1; --i) {
         swap((double[])array, i - 1, random.nextInt(i), 1);
      }

   }

   public static void shuffle(float[] array) {
      shuffle(array, new Random());
   }

   public static void shuffle(float[] array, Random random) {
      for(int i = array.length; i > 1; --i) {
         swap((float[])array, i - 1, random.nextInt(i), 1);
      }

   }

   public static void shuffle(int[] array) {
      shuffle(array, new Random());
   }

   public static void shuffle(int[] array, Random random) {
      for(int i = array.length; i > 1; --i) {
         swap((int[])array, i - 1, random.nextInt(i), 1);
      }

   }

   public static void shuffle(long[] array) {
      shuffle(array, new Random());
   }

   public static void shuffle(long[] array, Random random) {
      for(int i = array.length; i > 1; --i) {
         swap((long[])array, i - 1, random.nextInt(i), 1);
      }

   }

   public static void shuffle(Object[] array) {
      shuffle(array, new Random());
   }

   public static void shuffle(Object[] array, Random random) {
      for(int i = array.length; i > 1; --i) {
         swap((Object[])array, i - 1, random.nextInt(i), 1);
      }

   }

   public static void shuffle(short[] array) {
      shuffle(array, new Random());
   }

   public static void shuffle(short[] array, Random random) {
      for(int i = array.length; i > 1; --i) {
         swap((short[])array, i - 1, random.nextInt(i), 1);
      }

   }

   public static boolean[] subarray(boolean[] array, int startIndexInclusive, int endIndexExclusive) {
      if (array == null) {
         return null;
      } else {
         if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
         }

         if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
         }

         int newSize = endIndexExclusive - startIndexInclusive;
         if (newSize <= 0) {
            return EMPTY_BOOLEAN_ARRAY;
         } else {
            boolean[] subarray = new boolean[newSize];
            System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
            return subarray;
         }
      }
   }

   public static byte[] subarray(byte[] array, int startIndexInclusive, int endIndexExclusive) {
      if (array == null) {
         return null;
      } else {
         if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
         }

         if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
         }

         int newSize = endIndexExclusive - startIndexInclusive;
         if (newSize <= 0) {
            return EMPTY_BYTE_ARRAY;
         } else {
            byte[] subarray = new byte[newSize];
            System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
            return subarray;
         }
      }
   }

   public static char[] subarray(char[] array, int startIndexInclusive, int endIndexExclusive) {
      if (array == null) {
         return null;
      } else {
         if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
         }

         if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
         }

         int newSize = endIndexExclusive - startIndexInclusive;
         if (newSize <= 0) {
            return EMPTY_CHAR_ARRAY;
         } else {
            char[] subarray = new char[newSize];
            System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
            return subarray;
         }
      }
   }

   public static double[] subarray(double[] array, int startIndexInclusive, int endIndexExclusive) {
      if (array == null) {
         return null;
      } else {
         if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
         }

         if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
         }

         int newSize = endIndexExclusive - startIndexInclusive;
         if (newSize <= 0) {
            return EMPTY_DOUBLE_ARRAY;
         } else {
            double[] subarray = new double[newSize];
            System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
            return subarray;
         }
      }
   }

   public static float[] subarray(float[] array, int startIndexInclusive, int endIndexExclusive) {
      if (array == null) {
         return null;
      } else {
         if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
         }

         if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
         }

         int newSize = endIndexExclusive - startIndexInclusive;
         if (newSize <= 0) {
            return EMPTY_FLOAT_ARRAY;
         } else {
            float[] subarray = new float[newSize];
            System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
            return subarray;
         }
      }
   }

   public static int[] subarray(int[] array, int startIndexInclusive, int endIndexExclusive) {
      if (array == null) {
         return null;
      } else {
         if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
         }

         if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
         }

         int newSize = endIndexExclusive - startIndexInclusive;
         if (newSize <= 0) {
            return EMPTY_INT_ARRAY;
         } else {
            int[] subarray = new int[newSize];
            System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
            return subarray;
         }
      }
   }

   public static long[] subarray(long[] array, int startIndexInclusive, int endIndexExclusive) {
      if (array == null) {
         return null;
      } else {
         if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
         }

         if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
         }

         int newSize = endIndexExclusive - startIndexInclusive;
         if (newSize <= 0) {
            return EMPTY_LONG_ARRAY;
         } else {
            long[] subarray = new long[newSize];
            System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
            return subarray;
         }
      }
   }

   public static short[] subarray(short[] array, int startIndexInclusive, int endIndexExclusive) {
      if (array == null) {
         return null;
      } else {
         if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
         }

         if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
         }

         int newSize = endIndexExclusive - startIndexInclusive;
         if (newSize <= 0) {
            return EMPTY_SHORT_ARRAY;
         } else {
            short[] subarray = new short[newSize];
            System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
            return subarray;
         }
      }
   }

   public static <T> T[] subarray(T[] array, int startIndexInclusive, int endIndexExclusive) {
      if (array == null) {
         return null;
      } else {
         if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
         }

         if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
         }

         int newSize = endIndexExclusive - startIndexInclusive;
         Class<?> type = array.getClass().getComponentType();
         Object[] subarray;
         if (newSize <= 0) {
            subarray = (Object[])((Object[])Array.newInstance(type, 0));
            return subarray;
         } else {
            subarray = (Object[])((Object[])Array.newInstance(type, newSize));
            System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
            return subarray;
         }
      }
   }

   public static void swap(boolean[] array, int offset1, int offset2) {
      if (!isEmpty(array)) {
         swap((boolean[])array, offset1, offset2, 1);
      }
   }

   public static void swap(boolean[] array, int offset1, int offset2, int len) {
      if (!isEmpty(array) && offset1 < array.length && offset2 < array.length) {
         if (offset1 < 0) {
            offset1 = 0;
         }

         if (offset2 < 0) {
            offset2 = 0;
         }

         len = Math.min(Math.min(len, array.length - offset1), array.length - offset2);

         for(int i = 0; i < len; ++offset2) {
            boolean aux = array[offset1];
            array[offset1] = array[offset2];
            array[offset2] = aux;
            ++i;
            ++offset1;
         }

      }
   }

   public static void swap(byte[] array, int offset1, int offset2) {
      if (!isEmpty(array)) {
         swap((byte[])array, offset1, offset2, 1);
      }
   }

   public static void swap(byte[] array, int offset1, int offset2, int len) {
      if (!isEmpty(array) && offset1 < array.length && offset2 < array.length) {
         if (offset1 < 0) {
            offset1 = 0;
         }

         if (offset2 < 0) {
            offset2 = 0;
         }

         len = Math.min(Math.min(len, array.length - offset1), array.length - offset2);

         for(int i = 0; i < len; ++offset2) {
            byte aux = array[offset1];
            array[offset1] = array[offset2];
            array[offset2] = aux;
            ++i;
            ++offset1;
         }

      }
   }

   public static void swap(char[] array, int offset1, int offset2) {
      if (!isEmpty(array)) {
         swap((char[])array, offset1, offset2, 1);
      }
   }

   public static void swap(char[] array, int offset1, int offset2, int len) {
      if (!isEmpty(array) && offset1 < array.length && offset2 < array.length) {
         if (offset1 < 0) {
            offset1 = 0;
         }

         if (offset2 < 0) {
            offset2 = 0;
         }

         len = Math.min(Math.min(len, array.length - offset1), array.length - offset2);

         for(int i = 0; i < len; ++offset2) {
            char aux = array[offset1];
            array[offset1] = array[offset2];
            array[offset2] = aux;
            ++i;
            ++offset1;
         }

      }
   }

   public static void swap(double[] array, int offset1, int offset2) {
      if (!isEmpty(array)) {
         swap((double[])array, offset1, offset2, 1);
      }
   }

   public static void swap(double[] array, int offset1, int offset2, int len) {
      if (!isEmpty(array) && offset1 < array.length && offset2 < array.length) {
         if (offset1 < 0) {
            offset1 = 0;
         }

         if (offset2 < 0) {
            offset2 = 0;
         }

         len = Math.min(Math.min(len, array.length - offset1), array.length - offset2);

         for(int i = 0; i < len; ++offset2) {
            double aux = array[offset1];
            array[offset1] = array[offset2];
            array[offset2] = aux;
            ++i;
            ++offset1;
         }

      }
   }

   public static void swap(float[] array, int offset1, int offset2) {
      if (!isEmpty(array)) {
         swap((float[])array, offset1, offset2, 1);
      }
   }

   public static void swap(float[] array, int offset1, int offset2, int len) {
      if (!isEmpty(array) && offset1 < array.length && offset2 < array.length) {
         if (offset1 < 0) {
            offset1 = 0;
         }

         if (offset2 < 0) {
            offset2 = 0;
         }

         len = Math.min(Math.min(len, array.length - offset1), array.length - offset2);

         for(int i = 0; i < len; ++offset2) {
            float aux = array[offset1];
            array[offset1] = array[offset2];
            array[offset2] = aux;
            ++i;
            ++offset1;
         }

      }
   }

   public static void swap(int[] array, int offset1, int offset2) {
      if (!isEmpty(array)) {
         swap((int[])array, offset1, offset2, 1);
      }
   }

   public static void swap(int[] array, int offset1, int offset2, int len) {
      if (!isEmpty(array) && offset1 < array.length && offset2 < array.length) {
         if (offset1 < 0) {
            offset1 = 0;
         }

         if (offset2 < 0) {
            offset2 = 0;
         }

         len = Math.min(Math.min(len, array.length - offset1), array.length - offset2);

         for(int i = 0; i < len; ++offset2) {
            int aux = array[offset1];
            array[offset1] = array[offset2];
            array[offset2] = aux;
            ++i;
            ++offset1;
         }

      }
   }

   public static void swap(long[] array, int offset1, int offset2) {
      if (!isEmpty(array)) {
         swap((long[])array, offset1, offset2, 1);
      }
   }

   public static void swap(long[] array, int offset1, int offset2, int len) {
      if (!isEmpty(array) && offset1 < array.length && offset2 < array.length) {
         if (offset1 < 0) {
            offset1 = 0;
         }

         if (offset2 < 0) {
            offset2 = 0;
         }

         len = Math.min(Math.min(len, array.length - offset1), array.length - offset2);

         for(int i = 0; i < len; ++offset2) {
            long aux = array[offset1];
            array[offset1] = array[offset2];
            array[offset2] = aux;
            ++i;
            ++offset1;
         }

      }
   }

   public static void swap(Object[] array, int offset1, int offset2) {
      if (!isEmpty(array)) {
         swap((Object[])array, offset1, offset2, 1);
      }
   }

   public static void swap(Object[] array, int offset1, int offset2, int len) {
      if (!isEmpty(array) && offset1 < array.length && offset2 < array.length) {
         if (offset1 < 0) {
            offset1 = 0;
         }

         if (offset2 < 0) {
            offset2 = 0;
         }

         len = Math.min(Math.min(len, array.length - offset1), array.length - offset2);

         for(int i = 0; i < len; ++offset2) {
            Object aux = array[offset1];
            array[offset1] = array[offset2];
            array[offset2] = aux;
            ++i;
            ++offset1;
         }

      }
   }

   public static void swap(short[] array, int offset1, int offset2) {
      if (!isEmpty(array)) {
         swap((short[])array, offset1, offset2, 1);
      }
   }

   public static void swap(short[] array, int offset1, int offset2, int len) {
      if (!isEmpty(array) && offset1 < array.length && offset2 < array.length) {
         if (offset1 < 0) {
            offset1 = 0;
         }

         if (offset2 < 0) {
            offset2 = 0;
         }

         if (offset1 != offset2) {
            len = Math.min(Math.min(len, array.length - offset1), array.length - offset2);

            for(int i = 0; i < len; ++offset2) {
               short aux = array[offset1];
               array[offset1] = array[offset2];
               array[offset2] = aux;
               ++i;
               ++offset1;
            }

         }
      }
   }

   public static <T> T[] toArray(T... items) {
      return items;
   }

   public static Map<Object, Object> toMap(Object[] array) {
      if (array == null) {
         return null;
      } else {
         Map<Object, Object> map = new HashMap((int)((double)array.length * 1.5D));

         for(int i = 0; i < array.length; ++i) {
            Object object = array[i];
            if (object instanceof Entry) {
               Entry<?, ?> entry = (Entry)object;
               map.put(entry.getKey(), entry.getValue());
            } else {
               if (!(object instanceof Object[])) {
                  throw new IllegalArgumentException("Array element " + i + ", '" + object + "', is neither of type Map.Entry nor an Array");
               }

               Object[] entry = (Object[])((Object[])object);
               if (entry.length < 2) {
                  throw new IllegalArgumentException("Array element " + i + ", '" + object + "', has a length less than 2");
               }

               map.put(entry[0], entry[1]);
            }
         }

         return map;
      }
   }

   public static Boolean[] toObject(boolean[] array) {
      if (array == null) {
         return null;
      } else if (array.length == 0) {
         return EMPTY_BOOLEAN_OBJECT_ARRAY;
      } else {
         Boolean[] result = new Boolean[array.length];

         for(int i = 0; i < array.length; ++i) {
            result[i] = array[i] ? Boolean.TRUE : Boolean.FALSE;
         }

         return result;
      }
   }

   public static Byte[] toObject(byte[] array) {
      if (array == null) {
         return null;
      } else if (array.length == 0) {
         return EMPTY_BYTE_OBJECT_ARRAY;
      } else {
         Byte[] result = new Byte[array.length];

         for(int i = 0; i < array.length; ++i) {
            result[i] = array[i];
         }

         return result;
      }
   }

   public static Character[] toObject(char[] array) {
      if (array == null) {
         return null;
      } else if (array.length == 0) {
         return EMPTY_CHARACTER_OBJECT_ARRAY;
      } else {
         Character[] result = new Character[array.length];

         for(int i = 0; i < array.length; ++i) {
            result[i] = array[i];
         }

         return result;
      }
   }

   public static Double[] toObject(double[] array) {
      if (array == null) {
         return null;
      } else if (array.length == 0) {
         return EMPTY_DOUBLE_OBJECT_ARRAY;
      } else {
         Double[] result = new Double[array.length];

         for(int i = 0; i < array.length; ++i) {
            result[i] = array[i];
         }

         return result;
      }
   }

   public static Float[] toObject(float[] array) {
      if (array == null) {
         return null;
      } else if (array.length == 0) {
         return EMPTY_FLOAT_OBJECT_ARRAY;
      } else {
         Float[] result = new Float[array.length];

         for(int i = 0; i < array.length; ++i) {
            result[i] = array[i];
         }

         return result;
      }
   }

   public static Integer[] toObject(int[] array) {
      if (array == null) {
         return null;
      } else if (array.length == 0) {
         return EMPTY_INTEGER_OBJECT_ARRAY;
      } else {
         Integer[] result = new Integer[array.length];

         for(int i = 0; i < array.length; ++i) {
            result[i] = array[i];
         }

         return result;
      }
   }

   public static Long[] toObject(long[] array) {
      if (array == null) {
         return null;
      } else if (array.length == 0) {
         return EMPTY_LONG_OBJECT_ARRAY;
      } else {
         Long[] result = new Long[array.length];

         for(int i = 0; i < array.length; ++i) {
            result[i] = array[i];
         }

         return result;
      }
   }

   public static Short[] toObject(short[] array) {
      if (array == null) {
         return null;
      } else if (array.length == 0) {
         return EMPTY_SHORT_OBJECT_ARRAY;
      } else {
         Short[] result = new Short[array.length];

         for(int i = 0; i < array.length; ++i) {
            result[i] = array[i];
         }

         return result;
      }
   }

   public static boolean[] toPrimitive(Boolean[] array) {
      if (array == null) {
         return null;
      } else if (array.length == 0) {
         return EMPTY_BOOLEAN_ARRAY;
      } else {
         boolean[] result = new boolean[array.length];

         for(int i = 0; i < array.length; ++i) {
            result[i] = array[i];
         }

         return result;
      }
   }

   public static boolean[] toPrimitive(Boolean[] array, boolean valueForNull) {
      if (array == null) {
         return null;
      } else if (array.length == 0) {
         return EMPTY_BOOLEAN_ARRAY;
      } else {
         boolean[] result = new boolean[array.length];

         for(int i = 0; i < array.length; ++i) {
            Boolean b = array[i];
            result[i] = b == null ? valueForNull : b;
         }

         return result;
      }
   }

   public static byte[] toPrimitive(Byte[] array) {
      if (array == null) {
         return null;
      } else if (array.length == 0) {
         return EMPTY_BYTE_ARRAY;
      } else {
         byte[] result = new byte[array.length];

         for(int i = 0; i < array.length; ++i) {
            result[i] = array[i];
         }

         return result;
      }
   }

   public static byte[] toPrimitive(Byte[] array, byte valueForNull) {
      if (array == null) {
         return null;
      } else if (array.length == 0) {
         return EMPTY_BYTE_ARRAY;
      } else {
         byte[] result = new byte[array.length];

         for(int i = 0; i < array.length; ++i) {
            Byte b = array[i];
            result[i] = b == null ? valueForNull : b;
         }

         return result;
      }
   }

   public static char[] toPrimitive(Character[] array) {
      if (array == null) {
         return null;
      } else if (array.length == 0) {
         return EMPTY_CHAR_ARRAY;
      } else {
         char[] result = new char[array.length];

         for(int i = 0; i < array.length; ++i) {
            result[i] = array[i];
         }

         return result;
      }
   }

   public static char[] toPrimitive(Character[] array, char valueForNull) {
      if (array == null) {
         return null;
      } else if (array.length == 0) {
         return EMPTY_CHAR_ARRAY;
      } else {
         char[] result = new char[array.length];

         for(int i = 0; i < array.length; ++i) {
            Character b = array[i];
            result[i] = b == null ? valueForNull : b;
         }

         return result;
      }
   }

   public static double[] toPrimitive(Double[] array) {
      if (array == null) {
         return null;
      } else if (array.length == 0) {
         return EMPTY_DOUBLE_ARRAY;
      } else {
         double[] result = new double[array.length];

         for(int i = 0; i < array.length; ++i) {
            result[i] = array[i];
         }

         return result;
      }
   }

   public static double[] toPrimitive(Double[] array, double valueForNull) {
      if (array == null) {
         return null;
      } else if (array.length == 0) {
         return EMPTY_DOUBLE_ARRAY;
      } else {
         double[] result = new double[array.length];

         for(int i = 0; i < array.length; ++i) {
            Double b = array[i];
            result[i] = b == null ? valueForNull : b;
         }

         return result;
      }
   }

   public static float[] toPrimitive(Float[] array) {
      if (array == null) {
         return null;
      } else if (array.length == 0) {
         return EMPTY_FLOAT_ARRAY;
      } else {
         float[] result = new float[array.length];

         for(int i = 0; i < array.length; ++i) {
            result[i] = array[i];
         }

         return result;
      }
   }

   public static float[] toPrimitive(Float[] array, float valueForNull) {
      if (array == null) {
         return null;
      } else if (array.length == 0) {
         return EMPTY_FLOAT_ARRAY;
      } else {
         float[] result = new float[array.length];

         for(int i = 0; i < array.length; ++i) {
            Float b = array[i];
            result[i] = b == null ? valueForNull : b;
         }

         return result;
      }
   }

   public static int[] toPrimitive(Integer[] array) {
      if (array == null) {
         return null;
      } else if (array.length == 0) {
         return EMPTY_INT_ARRAY;
      } else {
         int[] result = new int[array.length];

         for(int i = 0; i < array.length; ++i) {
            result[i] = array[i];
         }

         return result;
      }
   }

   public static int[] toPrimitive(Integer[] array, int valueForNull) {
      if (array == null) {
         return null;
      } else if (array.length == 0) {
         return EMPTY_INT_ARRAY;
      } else {
         int[] result = new int[array.length];

         for(int i = 0; i < array.length; ++i) {
            Integer b = array[i];
            result[i] = b == null ? valueForNull : b;
         }

         return result;
      }
   }

   public static long[] toPrimitive(Long[] array) {
      if (array == null) {
         return null;
      } else if (array.length == 0) {
         return EMPTY_LONG_ARRAY;
      } else {
         long[] result = new long[array.length];

         for(int i = 0; i < array.length; ++i) {
            result[i] = array[i];
         }

         return result;
      }
   }

   public static long[] toPrimitive(Long[] array, long valueForNull) {
      if (array == null) {
         return null;
      } else if (array.length == 0) {
         return EMPTY_LONG_ARRAY;
      } else {
         long[] result = new long[array.length];

         for(int i = 0; i < array.length; ++i) {
            Long b = array[i];
            result[i] = b == null ? valueForNull : b;
         }

         return result;
      }
   }

   public static Object toPrimitive(Object array) {
      if (array == null) {
         return null;
      } else {
         Class<?> ct = array.getClass().getComponentType();
         Class<?> pt = ClassUtils.wrapperToPrimitive(ct);
         if (Integer.TYPE.equals(pt)) {
            return toPrimitive((Integer[])((Integer[])array));
         } else if (Long.TYPE.equals(pt)) {
            return toPrimitive((Long[])((Long[])array));
         } else if (Short.TYPE.equals(pt)) {
            return toPrimitive((Short[])((Short[])array));
         } else if (Double.TYPE.equals(pt)) {
            return toPrimitive((Double[])((Double[])array));
         } else {
            return Float.TYPE.equals(pt) ? toPrimitive((Float[])((Float[])array)) : array;
         }
      }
   }

   public static short[] toPrimitive(Short[] array) {
      if (array == null) {
         return null;
      } else if (array.length == 0) {
         return EMPTY_SHORT_ARRAY;
      } else {
         short[] result = new short[array.length];

         for(int i = 0; i < array.length; ++i) {
            result[i] = array[i];
         }

         return result;
      }
   }

   public static short[] toPrimitive(Short[] array, short valueForNull) {
      if (array == null) {
         return null;
      } else if (array.length == 0) {
         return EMPTY_SHORT_ARRAY;
      } else {
         short[] result = new short[array.length];

         for(int i = 0; i < array.length; ++i) {
            Short b = array[i];
            result[i] = b == null ? valueForNull : b;
         }

         return result;
      }
   }

   public static String toString(Object array) {
      return toString(array, "{}");
   }

   public static String toString(Object array, String stringIfNull) {
      return array == null ? stringIfNull : (new ToStringBuilder(array, ToStringStyle.SIMPLE_STYLE)).append(array).toString();
   }

   public static String[] toStringArray(Object[] array) {
      if (array == null) {
         return null;
      } else if (array.length == 0) {
         return EMPTY_STRING_ARRAY;
      } else {
         String[] result = new String[array.length];

         for(int i = 0; i < array.length; ++i) {
            result[i] = array[i].toString();
         }

         return result;
      }
   }

   public static String[] toStringArray(Object[] array, String valueForNullElements) {
      if (null == array) {
         return null;
      } else if (array.length == 0) {
         return EMPTY_STRING_ARRAY;
      } else {
         String[] result = new String[array.length];

         for(int i = 0; i < array.length; ++i) {
            Object object = array[i];
            result[i] = object == null ? valueForNullElements : object.toString();
         }

         return result;
      }
   }
}

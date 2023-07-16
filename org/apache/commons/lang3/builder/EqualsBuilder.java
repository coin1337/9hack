package org.apache.commons.lang3.builder;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.tuple.Pair;

public class EqualsBuilder implements Builder<Boolean> {
   private static final ThreadLocal<Set<Pair<IDKey, IDKey>>> REGISTRY = new ThreadLocal();
   private boolean isEquals = true;
   private boolean testTransients = false;
   private boolean testRecursive = false;
   private List<Class<?>> bypassReflectionClasses = new ArrayList();
   private Class<?> reflectUpToClass = null;
   private String[] excludeFields = null;

   static Set<Pair<IDKey, IDKey>> getRegistry() {
      return (Set)REGISTRY.get();
   }

   static Pair<IDKey, IDKey> getRegisterPair(Object lhs, Object rhs) {
      IDKey left = new IDKey(lhs);
      IDKey right = new IDKey(rhs);
      return Pair.of(left, right);
   }

   static boolean isRegistered(Object lhs, Object rhs) {
      Set<Pair<IDKey, IDKey>> registry = getRegistry();
      Pair<IDKey, IDKey> pair = getRegisterPair(lhs, rhs);
      Pair<IDKey, IDKey> swappedPair = Pair.of(pair.getRight(), pair.getLeft());
      return registry != null && (registry.contains(pair) || registry.contains(swappedPair));
   }

   private static void register(Object lhs, Object rhs) {
      Set<Pair<IDKey, IDKey>> registry = getRegistry();
      if (registry == null) {
         registry = new HashSet();
         REGISTRY.set(registry);
      }

      Pair<IDKey, IDKey> pair = getRegisterPair(lhs, rhs);
      ((Set)registry).add(pair);
   }

   private static void unregister(Object lhs, Object rhs) {
      Set<Pair<IDKey, IDKey>> registry = getRegistry();
      if (registry != null) {
         Pair<IDKey, IDKey> pair = getRegisterPair(lhs, rhs);
         registry.remove(pair);
         if (registry.isEmpty()) {
            REGISTRY.remove();
         }
      }

   }

   public EqualsBuilder() {
      this.bypassReflectionClasses.add(String.class);
   }

   public EqualsBuilder setTestTransients(boolean testTransients) {
      this.testTransients = testTransients;
      return this;
   }

   public EqualsBuilder setTestRecursive(boolean testRecursive) {
      this.testRecursive = testRecursive;
      return this;
   }

   public EqualsBuilder setBypassReflectionClasses(List<Class<?>> bypassReflectionClasses) {
      this.bypassReflectionClasses = bypassReflectionClasses;
      return this;
   }

   public EqualsBuilder setReflectUpToClass(Class<?> reflectUpToClass) {
      this.reflectUpToClass = reflectUpToClass;
      return this;
   }

   public EqualsBuilder setExcludeFields(String... excludeFields) {
      this.excludeFields = excludeFields;
      return this;
   }

   public static boolean reflectionEquals(Object lhs, Object rhs, Collection<String> excludeFields) {
      return reflectionEquals(lhs, rhs, ReflectionToStringBuilder.toNoNullStringArray(excludeFields));
   }

   public static boolean reflectionEquals(Object lhs, Object rhs, String... excludeFields) {
      return reflectionEquals(lhs, rhs, false, (Class)null, excludeFields);
   }

   public static boolean reflectionEquals(Object lhs, Object rhs, boolean testTransients) {
      return reflectionEquals(lhs, rhs, testTransients, (Class)null);
   }

   public static boolean reflectionEquals(Object lhs, Object rhs, boolean testTransients, Class<?> reflectUpToClass, String... excludeFields) {
      return reflectionEquals(lhs, rhs, testTransients, reflectUpToClass, false, excludeFields);
   }

   public static boolean reflectionEquals(Object lhs, Object rhs, boolean testTransients, Class<?> reflectUpToClass, boolean testRecursive, String... excludeFields) {
      if (lhs == rhs) {
         return true;
      } else {
         return lhs != null && rhs != null ? (new EqualsBuilder()).setExcludeFields(excludeFields).setReflectUpToClass(reflectUpToClass).setTestTransients(testTransients).setTestRecursive(testRecursive).reflectionAppend(lhs, rhs).isEquals() : false;
      }
   }

   public EqualsBuilder reflectionAppend(Object lhs, Object rhs) {
      if (!this.isEquals) {
         return this;
      } else if (lhs == rhs) {
         return this;
      } else if (lhs != null && rhs != null) {
         Class<?> lhsClass = lhs.getClass();
         Class<?> rhsClass = rhs.getClass();
         Class testClass;
         if (lhsClass.isInstance(rhs)) {
            testClass = lhsClass;
            if (!rhsClass.isInstance(lhs)) {
               testClass = rhsClass;
            }
         } else {
            if (!rhsClass.isInstance(lhs)) {
               this.isEquals = false;
               return this;
            }

            testClass = rhsClass;
            if (!lhsClass.isInstance(rhs)) {
               testClass = lhsClass;
            }
         }

         try {
            if (testClass.isArray()) {
               this.append(lhs, rhs);
            } else if (this.bypassReflectionClasses == null || !this.bypassReflectionClasses.contains(lhsClass) && !this.bypassReflectionClasses.contains(rhsClass)) {
               this.reflectionAppend(lhs, rhs, testClass);

               while(testClass.getSuperclass() != null && testClass != this.reflectUpToClass) {
                  testClass = testClass.getSuperclass();
                  this.reflectionAppend(lhs, rhs, testClass);
               }
            } else {
               this.isEquals = lhs.equals(rhs);
            }

            return this;
         } catch (IllegalArgumentException var7) {
            this.isEquals = false;
            return this;
         }
      } else {
         this.isEquals = false;
         return this;
      }
   }

   private void reflectionAppend(Object lhs, Object rhs, Class<?> clazz) {
      if (!isRegistered(lhs, rhs)) {
         try {
            register(lhs, rhs);
            Field[] fields = clazz.getDeclaredFields();
            AccessibleObject.setAccessible(fields, true);

            for(int i = 0; i < fields.length && this.isEquals; ++i) {
               Field f = fields[i];
               if (!ArrayUtils.contains(this.excludeFields, f.getName()) && !f.getName().contains("$") && (this.testTransients || !Modifier.isTransient(f.getModifiers())) && !Modifier.isStatic(f.getModifiers()) && !f.isAnnotationPresent(EqualsExclude.class)) {
                  try {
                     this.append(f.get(lhs), f.get(rhs));
                  } catch (IllegalAccessException var11) {
                     throw new InternalError("Unexpected IllegalAccessException");
                  }
               }
            }
         } finally {
            unregister(lhs, rhs);
         }

      }
   }

   public EqualsBuilder appendSuper(boolean superEquals) {
      if (!this.isEquals) {
         return this;
      } else {
         this.isEquals = superEquals;
         return this;
      }
   }

   public EqualsBuilder append(Object lhs, Object rhs) {
      if (!this.isEquals) {
         return this;
      } else if (lhs == rhs) {
         return this;
      } else if (lhs != null && rhs != null) {
         Class<?> lhsClass = lhs.getClass();
         if (lhsClass.isArray()) {
            this.appendArray(lhs, rhs);
         } else if (this.testRecursive && !ClassUtils.isPrimitiveOrWrapper(lhsClass)) {
            this.reflectionAppend(lhs, rhs);
         } else {
            this.isEquals = lhs.equals(rhs);
         }

         return this;
      } else {
         this.setEquals(false);
         return this;
      }
   }

   private void appendArray(Object lhs, Object rhs) {
      if (lhs.getClass() != rhs.getClass()) {
         this.setEquals(false);
      } else if (lhs instanceof long[]) {
         this.append((long[])((long[])lhs), (long[])((long[])rhs));
      } else if (lhs instanceof int[]) {
         this.append((int[])((int[])lhs), (int[])((int[])rhs));
      } else if (lhs instanceof short[]) {
         this.append((short[])((short[])lhs), (short[])((short[])rhs));
      } else if (lhs instanceof char[]) {
         this.append((char[])((char[])lhs), (char[])((char[])rhs));
      } else if (lhs instanceof byte[]) {
         this.append((byte[])((byte[])lhs), (byte[])((byte[])rhs));
      } else if (lhs instanceof double[]) {
         this.append((double[])((double[])lhs), (double[])((double[])rhs));
      } else if (lhs instanceof float[]) {
         this.append((float[])((float[])lhs), (float[])((float[])rhs));
      } else if (lhs instanceof boolean[]) {
         this.append((boolean[])((boolean[])lhs), (boolean[])((boolean[])rhs));
      } else {
         this.append((Object[])((Object[])lhs), (Object[])((Object[])rhs));
      }

   }

   public EqualsBuilder append(long lhs, long rhs) {
      if (!this.isEquals) {
         return this;
      } else {
         this.isEquals = lhs == rhs;
         return this;
      }
   }

   public EqualsBuilder append(int lhs, int rhs) {
      if (!this.isEquals) {
         return this;
      } else {
         this.isEquals = lhs == rhs;
         return this;
      }
   }

   public EqualsBuilder append(short lhs, short rhs) {
      if (!this.isEquals) {
         return this;
      } else {
         this.isEquals = lhs == rhs;
         return this;
      }
   }

   public EqualsBuilder append(char lhs, char rhs) {
      if (!this.isEquals) {
         return this;
      } else {
         this.isEquals = lhs == rhs;
         return this;
      }
   }

   public EqualsBuilder append(byte lhs, byte rhs) {
      if (!this.isEquals) {
         return this;
      } else {
         this.isEquals = lhs == rhs;
         return this;
      }
   }

   public EqualsBuilder append(double lhs, double rhs) {
      return !this.isEquals ? this : this.append(Double.doubleToLongBits(lhs), Double.doubleToLongBits(rhs));
   }

   public EqualsBuilder append(float lhs, float rhs) {
      return !this.isEquals ? this : this.append(Float.floatToIntBits(lhs), Float.floatToIntBits(rhs));
   }

   public EqualsBuilder append(boolean lhs, boolean rhs) {
      if (!this.isEquals) {
         return this;
      } else {
         this.isEquals = lhs == rhs;
         return this;
      }
   }

   public EqualsBuilder append(Object[] lhs, Object[] rhs) {
      if (!this.isEquals) {
         return this;
      } else if (lhs == rhs) {
         return this;
      } else if (lhs != null && rhs != null) {
         if (lhs.length != rhs.length) {
            this.setEquals(false);
            return this;
         } else {
            for(int i = 0; i < lhs.length && this.isEquals; ++i) {
               this.append(lhs[i], rhs[i]);
            }

            return this;
         }
      } else {
         this.setEquals(false);
         return this;
      }
   }

   public EqualsBuilder append(long[] lhs, long[] rhs) {
      if (!this.isEquals) {
         return this;
      } else if (lhs == rhs) {
         return this;
      } else if (lhs != null && rhs != null) {
         if (lhs.length != rhs.length) {
            this.setEquals(false);
            return this;
         } else {
            for(int i = 0; i < lhs.length && this.isEquals; ++i) {
               this.append(lhs[i], rhs[i]);
            }

            return this;
         }
      } else {
         this.setEquals(false);
         return this;
      }
   }

   public EqualsBuilder append(int[] lhs, int[] rhs) {
      if (!this.isEquals) {
         return this;
      } else if (lhs == rhs) {
         return this;
      } else if (lhs != null && rhs != null) {
         if (lhs.length != rhs.length) {
            this.setEquals(false);
            return this;
         } else {
            for(int i = 0; i < lhs.length && this.isEquals; ++i) {
               this.append(lhs[i], rhs[i]);
            }

            return this;
         }
      } else {
         this.setEquals(false);
         return this;
      }
   }

   public EqualsBuilder append(short[] lhs, short[] rhs) {
      if (!this.isEquals) {
         return this;
      } else if (lhs == rhs) {
         return this;
      } else if (lhs != null && rhs != null) {
         if (lhs.length != rhs.length) {
            this.setEquals(false);
            return this;
         } else {
            for(int i = 0; i < lhs.length && this.isEquals; ++i) {
               this.append(lhs[i], rhs[i]);
            }

            return this;
         }
      } else {
         this.setEquals(false);
         return this;
      }
   }

   public EqualsBuilder append(char[] lhs, char[] rhs) {
      if (!this.isEquals) {
         return this;
      } else if (lhs == rhs) {
         return this;
      } else if (lhs != null && rhs != null) {
         if (lhs.length != rhs.length) {
            this.setEquals(false);
            return this;
         } else {
            for(int i = 0; i < lhs.length && this.isEquals; ++i) {
               this.append(lhs[i], rhs[i]);
            }

            return this;
         }
      } else {
         this.setEquals(false);
         return this;
      }
   }

   public EqualsBuilder append(byte[] lhs, byte[] rhs) {
      if (!this.isEquals) {
         return this;
      } else if (lhs == rhs) {
         return this;
      } else if (lhs != null && rhs != null) {
         if (lhs.length != rhs.length) {
            this.setEquals(false);
            return this;
         } else {
            for(int i = 0; i < lhs.length && this.isEquals; ++i) {
               this.append(lhs[i], rhs[i]);
            }

            return this;
         }
      } else {
         this.setEquals(false);
         return this;
      }
   }

   public EqualsBuilder append(double[] lhs, double[] rhs) {
      if (!this.isEquals) {
         return this;
      } else if (lhs == rhs) {
         return this;
      } else if (lhs != null && rhs != null) {
         if (lhs.length != rhs.length) {
            this.setEquals(false);
            return this;
         } else {
            for(int i = 0; i < lhs.length && this.isEquals; ++i) {
               this.append(lhs[i], rhs[i]);
            }

            return this;
         }
      } else {
         this.setEquals(false);
         return this;
      }
   }

   public EqualsBuilder append(float[] lhs, float[] rhs) {
      if (!this.isEquals) {
         return this;
      } else if (lhs == rhs) {
         return this;
      } else if (lhs != null && rhs != null) {
         if (lhs.length != rhs.length) {
            this.setEquals(false);
            return this;
         } else {
            for(int i = 0; i < lhs.length && this.isEquals; ++i) {
               this.append(lhs[i], rhs[i]);
            }

            return this;
         }
      } else {
         this.setEquals(false);
         return this;
      }
   }

   public EqualsBuilder append(boolean[] lhs, boolean[] rhs) {
      if (!this.isEquals) {
         return this;
      } else if (lhs == rhs) {
         return this;
      } else if (lhs != null && rhs != null) {
         if (lhs.length != rhs.length) {
            this.setEquals(false);
            return this;
         } else {
            for(int i = 0; i < lhs.length && this.isEquals; ++i) {
               this.append(lhs[i], rhs[i]);
            }

            return this;
         }
      } else {
         this.setEquals(false);
         return this;
      }
   }

   public boolean isEquals() {
      return this.isEquals;
   }

   public Boolean build() {
      return this.isEquals();
   }

   protected void setEquals(boolean isEquals) {
      this.isEquals = isEquals;
   }

   public void reset() {
      this.isEquals = true;
   }
}

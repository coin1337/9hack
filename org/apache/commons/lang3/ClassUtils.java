package org.apache.commons.lang3;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.commons.lang3.mutable.MutableObject;

public class ClassUtils {
   public static final char PACKAGE_SEPARATOR_CHAR = '.';
   public static final String PACKAGE_SEPARATOR = String.valueOf('.');
   public static final char INNER_CLASS_SEPARATOR_CHAR = '$';
   public static final String INNER_CLASS_SEPARATOR = String.valueOf('$');
   private static final Map<String, Class<?>> namePrimitiveMap = new HashMap();
   private static final Map<Class<?>, Class<?>> primitiveWrapperMap;
   private static final Map<Class<?>, Class<?>> wrapperPrimitiveMap;
   private static final Map<String, String> abbreviationMap;
   private static final Map<String, String> reverseAbbreviationMap;

   public static String getShortClassName(Object object, String valueIfNull) {
      return object == null ? valueIfNull : getShortClassName(object.getClass());
   }

   public static String getShortClassName(Class<?> cls) {
      return cls == null ? "" : getShortClassName(cls.getName());
   }

   public static String getShortClassName(String className) {
      if (StringUtils.isEmpty(className)) {
         return "";
      } else {
         StringBuilder arrayPrefix = new StringBuilder();
         if (className.startsWith("[")) {
            while(className.charAt(0) == '[') {
               className = className.substring(1);
               arrayPrefix.append("[]");
            }

            if (className.charAt(0) == 'L' && className.charAt(className.length() - 1) == ';') {
               className = className.substring(1, className.length() - 1);
            }

            if (reverseAbbreviationMap.containsKey(className)) {
               className = (String)reverseAbbreviationMap.get(className);
            }
         }

         int lastDotIdx = className.lastIndexOf(46);
         int innerIdx = className.indexOf(36, lastDotIdx == -1 ? 0 : lastDotIdx + 1);
         String out = className.substring(lastDotIdx + 1);
         if (innerIdx != -1) {
            out = out.replace('$', '.');
         }

         return out + arrayPrefix;
      }
   }

   public static String getSimpleName(Class<?> cls) {
      return getSimpleName(cls, "");
   }

   public static String getSimpleName(Class<?> cls, String valueIfNull) {
      return cls == null ? valueIfNull : cls.getSimpleName();
   }

   public static String getSimpleName(Object object) {
      return getSimpleName(object, "");
   }

   public static String getSimpleName(Object object, String valueIfNull) {
      return object == null ? valueIfNull : object.getClass().getSimpleName();
   }

   public static String getName(Class<?> cls) {
      return getName(cls, "");
   }

   public static String getName(Class<?> cls, String valueIfNull) {
      return cls == null ? valueIfNull : cls.getName();
   }

   public static String getName(Object object) {
      return getName(object, "");
   }

   public static String getName(Object object, String valueIfNull) {
      return object == null ? valueIfNull : object.getClass().getName();
   }

   public static String getPackageName(Object object, String valueIfNull) {
      return object == null ? valueIfNull : getPackageName(object.getClass());
   }

   public static String getPackageName(Class<?> cls) {
      return cls == null ? "" : getPackageName(cls.getName());
   }

   public static String getPackageName(String className) {
      if (StringUtils.isEmpty(className)) {
         return "";
      } else {
         while(className.charAt(0) == '[') {
            className = className.substring(1);
         }

         if (className.charAt(0) == 'L' && className.charAt(className.length() - 1) == ';') {
            className = className.substring(1);
         }

         int i = className.lastIndexOf(46);
         return i == -1 ? "" : className.substring(0, i);
      }
   }

   public static String getAbbreviatedName(Class<?> cls, int lengthHint) {
      return cls == null ? "" : getAbbreviatedName(cls.getName(), lengthHint);
   }

   public static String getAbbreviatedName(String className, int lengthHint) {
      if (lengthHint <= 0) {
         throw new IllegalArgumentException("len must be > 0");
      } else if (className == null) {
         return "";
      } else if (className.length() <= lengthHint) {
         return className;
      } else {
         char[] abbreviated = className.toCharArray();
         int target = 0;
         int source = 0;

         while(source < abbreviated.length) {
            int runAheadTarget;
            for(runAheadTarget = target; source < abbreviated.length && abbreviated[source] != '.'; abbreviated[runAheadTarget++] = abbreviated[source++]) {
            }

            ++target;
            if (useFull(runAheadTarget, source, abbreviated.length, lengthHint) || target > runAheadTarget) {
               target = runAheadTarget;
            }

            if (source < abbreviated.length) {
               abbreviated[target++] = abbreviated[source++];
            }
         }

         return new String(abbreviated, 0, target);
      }
   }

   private static boolean useFull(int runAheadTarget, int source, int originalLength, int desiredLength) {
      return source >= originalLength || runAheadTarget + originalLength - source <= desiredLength;
   }

   public static List<Class<?>> getAllSuperclasses(Class<?> cls) {
      if (cls == null) {
         return null;
      } else {
         List<Class<?>> classes = new ArrayList();

         for(Class superclass = cls.getSuperclass(); superclass != null; superclass = superclass.getSuperclass()) {
            classes.add(superclass);
         }

         return classes;
      }
   }

   public static List<Class<?>> getAllInterfaces(Class<?> cls) {
      if (cls == null) {
         return null;
      } else {
         LinkedHashSet<Class<?>> interfacesFound = new LinkedHashSet();
         getAllInterfaces(cls, interfacesFound);
         return new ArrayList(interfacesFound);
      }
   }

   private static void getAllInterfaces(Class<?> cls, HashSet<Class<?>> interfacesFound) {
      while(cls != null) {
         Class<?>[] interfaces = cls.getInterfaces();
         Class[] var3 = interfaces;
         int var4 = interfaces.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Class<?> i = var3[var5];
            if (interfacesFound.add(i)) {
               getAllInterfaces(i, interfacesFound);
            }
         }

         cls = cls.getSuperclass();
      }

   }

   public static List<Class<?>> convertClassNamesToClasses(List<String> classNames) {
      if (classNames == null) {
         return null;
      } else {
         List<Class<?>> classes = new ArrayList(classNames.size());
         Iterator var2 = classNames.iterator();

         while(var2.hasNext()) {
            String className = (String)var2.next();

            try {
               classes.add(Class.forName(className));
            } catch (Exception var5) {
               classes.add((Object)null);
            }
         }

         return classes;
      }
   }

   public static List<String> convertClassesToClassNames(List<Class<?>> classes) {
      if (classes == null) {
         return null;
      } else {
         List<String> classNames = new ArrayList(classes.size());
         Iterator var2 = classes.iterator();

         while(var2.hasNext()) {
            Class<?> cls = (Class)var2.next();
            if (cls == null) {
               classNames.add((Object)null);
            } else {
               classNames.add(cls.getName());
            }
         }

         return classNames;
      }
   }

   public static boolean isAssignable(Class<?>[] classArray, Class<?>... toClassArray) {
      return isAssignable(classArray, toClassArray, true);
   }

   public static boolean isAssignable(Class<?>[] classArray, Class<?>[] toClassArray, boolean autoboxing) {
      if (!ArrayUtils.isSameLength((Object[])classArray, (Object[])toClassArray)) {
         return false;
      } else {
         if (classArray == null) {
            classArray = ArrayUtils.EMPTY_CLASS_ARRAY;
         }

         if (toClassArray == null) {
            toClassArray = ArrayUtils.EMPTY_CLASS_ARRAY;
         }

         for(int i = 0; i < classArray.length; ++i) {
            if (!isAssignable(classArray[i], toClassArray[i], autoboxing)) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isPrimitiveOrWrapper(Class<?> type) {
      if (type == null) {
         return false;
      } else {
         return type.isPrimitive() || isPrimitiveWrapper(type);
      }
   }

   public static boolean isPrimitiveWrapper(Class<?> type) {
      return wrapperPrimitiveMap.containsKey(type);
   }

   public static boolean isAssignable(Class<?> cls, Class<?> toClass) {
      return isAssignable(cls, toClass, true);
   }

   public static boolean isAssignable(Class<?> cls, Class<?> toClass, boolean autoboxing) {
      if (toClass == null) {
         return false;
      } else if (cls == null) {
         return !toClass.isPrimitive();
      } else {
         if (autoboxing) {
            if (cls.isPrimitive() && !toClass.isPrimitive()) {
               cls = primitiveToWrapper(cls);
               if (cls == null) {
                  return false;
               }
            }

            if (toClass.isPrimitive() && !cls.isPrimitive()) {
               cls = wrapperToPrimitive(cls);
               if (cls == null) {
                  return false;
               }
            }
         }

         if (cls.equals(toClass)) {
            return true;
         } else if (cls.isPrimitive()) {
            if (!toClass.isPrimitive()) {
               return false;
            } else if (Integer.TYPE.equals(cls)) {
               return Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
            } else if (Long.TYPE.equals(cls)) {
               return Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
            } else if (Boolean.TYPE.equals(cls)) {
               return false;
            } else if (Double.TYPE.equals(cls)) {
               return false;
            } else if (Float.TYPE.equals(cls)) {
               return Double.TYPE.equals(toClass);
            } else if (Character.TYPE.equals(cls)) {
               return Integer.TYPE.equals(toClass) || Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
            } else if (Short.TYPE.equals(cls)) {
               return Integer.TYPE.equals(toClass) || Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
            } else if (!Byte.TYPE.equals(cls)) {
               return false;
            } else {
               return Short.TYPE.equals(toClass) || Integer.TYPE.equals(toClass) || Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
            }
         } else {
            return toClass.isAssignableFrom(cls);
         }
      }
   }

   public static Class<?> primitiveToWrapper(Class<?> cls) {
      Class<?> convertedClass = cls;
      if (cls != null && cls.isPrimitive()) {
         convertedClass = (Class)primitiveWrapperMap.get(cls);
      }

      return convertedClass;
   }

   public static Class<?>[] primitivesToWrappers(Class<?>... classes) {
      if (classes == null) {
         return null;
      } else if (classes.length == 0) {
         return classes;
      } else {
         Class<?>[] convertedClasses = new Class[classes.length];

         for(int i = 0; i < classes.length; ++i) {
            convertedClasses[i] = primitiveToWrapper(classes[i]);
         }

         return convertedClasses;
      }
   }

   public static Class<?> wrapperToPrimitive(Class<?> cls) {
      return (Class)wrapperPrimitiveMap.get(cls);
   }

   public static Class<?>[] wrappersToPrimitives(Class<?>... classes) {
      if (classes == null) {
         return null;
      } else if (classes.length == 0) {
         return classes;
      } else {
         Class<?>[] convertedClasses = new Class[classes.length];

         for(int i = 0; i < classes.length; ++i) {
            convertedClasses[i] = wrapperToPrimitive(classes[i]);
         }

         return convertedClasses;
      }
   }

   public static boolean isInnerClass(Class<?> cls) {
      return cls != null && cls.getEnclosingClass() != null;
   }

   public static Class<?> getClass(ClassLoader classLoader, String className, boolean initialize) throws ClassNotFoundException {
      try {
         Class clazz;
         if (namePrimitiveMap.containsKey(className)) {
            clazz = (Class)namePrimitiveMap.get(className);
         } else {
            clazz = Class.forName(toCanonicalName(className), initialize, classLoader);
         }

         return clazz;
      } catch (ClassNotFoundException var7) {
         int lastDotIndex = className.lastIndexOf(46);
         if (lastDotIndex != -1) {
            try {
               return getClass(classLoader, className.substring(0, lastDotIndex) + '$' + className.substring(lastDotIndex + 1), initialize);
            } catch (ClassNotFoundException var6) {
            }
         }

         throw var7;
      }
   }

   public static Class<?> getClass(ClassLoader classLoader, String className) throws ClassNotFoundException {
      return getClass(classLoader, className, true);
   }

   public static Class<?> getClass(String className) throws ClassNotFoundException {
      return getClass(className, true);
   }

   public static Class<?> getClass(String className, boolean initialize) throws ClassNotFoundException {
      ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
      ClassLoader loader = contextCL == null ? ClassUtils.class.getClassLoader() : contextCL;
      return getClass(loader, className, initialize);
   }

   public static Method getPublicMethod(Class<?> cls, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
      Method declaredMethod = cls.getMethod(methodName, parameterTypes);
      if (Modifier.isPublic(declaredMethod.getDeclaringClass().getModifiers())) {
         return declaredMethod;
      } else {
         List<Class<?>> candidateClasses = new ArrayList();
         candidateClasses.addAll(getAllInterfaces(cls));
         candidateClasses.addAll(getAllSuperclasses(cls));
         Iterator var5 = candidateClasses.iterator();

         while(true) {
            Class candidateClass;
            do {
               if (!var5.hasNext()) {
                  throw new NoSuchMethodException("Can't find a public method for " + methodName + " " + ArrayUtils.toString(parameterTypes));
               }

               candidateClass = (Class)var5.next();
            } while(!Modifier.isPublic(candidateClass.getModifiers()));

            Method candidateMethod;
            try {
               candidateMethod = candidateClass.getMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException var9) {
               continue;
            }

            if (Modifier.isPublic(candidateMethod.getDeclaringClass().getModifiers())) {
               return candidateMethod;
            }
         }
      }
   }

   private static String toCanonicalName(String className) {
      className = StringUtils.deleteWhitespace(className);
      Validate.notNull(className, "className must not be null.");
      if (className.endsWith("[]")) {
         StringBuilder classNameBuffer = new StringBuilder();

         while(className.endsWith("[]")) {
            className = className.substring(0, className.length() - 2);
            classNameBuffer.append("[");
         }

         String abbreviation = (String)abbreviationMap.get(className);
         if (abbreviation != null) {
            classNameBuffer.append(abbreviation);
         } else {
            classNameBuffer.append("L").append(className).append(";");
         }

         className = classNameBuffer.toString();
      }

      return className;
   }

   public static Class<?>[] toClass(Object... array) {
      if (array == null) {
         return null;
      } else if (array.length == 0) {
         return ArrayUtils.EMPTY_CLASS_ARRAY;
      } else {
         Class<?>[] classes = new Class[array.length];

         for(int i = 0; i < array.length; ++i) {
            classes[i] = array[i] == null ? null : array[i].getClass();
         }

         return classes;
      }
   }

   public static String getShortCanonicalName(Object object, String valueIfNull) {
      return object == null ? valueIfNull : getShortCanonicalName(object.getClass().getName());
   }

   public static String getCanonicalName(Class<?> cls) {
      return getCanonicalName(cls, "");
   }

   public static String getCanonicalName(Class<?> cls, String valueIfNull) {
      if (cls == null) {
         return valueIfNull;
      } else {
         String canonicalName = cls.getCanonicalName();
         return canonicalName == null ? valueIfNull : canonicalName;
      }
   }

   public static String getCanonicalName(Object object) {
      return getCanonicalName(object, "");
   }

   public static String getCanonicalName(Object object, String valueIfNull) {
      if (object == null) {
         return valueIfNull;
      } else {
         String canonicalName = object.getClass().getCanonicalName();
         return canonicalName == null ? valueIfNull : canonicalName;
      }
   }

   public static String getShortCanonicalName(Class<?> cls) {
      return cls == null ? "" : getShortCanonicalName(cls.getName());
   }

   public static String getShortCanonicalName(String canonicalName) {
      return getShortClassName(getCanonicalName(canonicalName));
   }

   public static String getPackageCanonicalName(Object object, String valueIfNull) {
      return object == null ? valueIfNull : getPackageCanonicalName(object.getClass().getName());
   }

   public static String getPackageCanonicalName(Class<?> cls) {
      return cls == null ? "" : getPackageCanonicalName(cls.getName());
   }

   public static String getPackageCanonicalName(String name) {
      return getPackageName(getCanonicalName(name));
   }

   private static String getCanonicalName(String className) {
      className = StringUtils.deleteWhitespace(className);
      if (className == null) {
         return null;
      } else {
         int dim;
         for(dim = 0; className.startsWith("["); className = className.substring(1)) {
            ++dim;
         }

         if (dim < 1) {
            return className;
         } else {
            if (className.startsWith("L")) {
               className = className.substring(1, className.endsWith(";") ? className.length() - 1 : className.length());
            } else if (!className.isEmpty()) {
               className = (String)reverseAbbreviationMap.get(className.substring(0, 1));
            }

            StringBuilder canonicalClassNameBuffer = new StringBuilder(className);

            for(int i = 0; i < dim; ++i) {
               canonicalClassNameBuffer.append("[]");
            }

            return canonicalClassNameBuffer.toString();
         }
      }
   }

   public static Iterable<Class<?>> hierarchy(Class<?> type) {
      return hierarchy(type, ClassUtils.Interfaces.EXCLUDE);
   }

   public static Iterable<Class<?>> hierarchy(Class<?> type, ClassUtils.Interfaces interfacesBehavior) {
      Iterable<Class<?>> classes = () -> {
         final MutableObject<Class<?>> next = new MutableObject(type);
         return new Iterator<Class<?>>() {
            public boolean hasNext() {
               return next.getValue() != null;
            }

            public Class<?> next() {
               Class result = (Class)next.getValue();
               next.setValue(result.getSuperclass());
               return result;
            }

            public void remove() {
               throw new UnsupportedOperationException();
            }
         };
      };
      return interfacesBehavior != ClassUtils.Interfaces.INCLUDE ? classes : () -> {
         final Set<Class<?>> seenInterfaces = new HashSet();
         final Iterator<Class<?>> wrapped = classes.iterator();
         return new Iterator<Class<?>>() {
            Iterator interfaces = Collections.emptySet().iterator();

            public boolean hasNext() {
               return this.interfaces.hasNext() || wrapped.hasNext();
            }

            public Class<?> next() {
               Class nextSuperclass;
               if (this.interfaces.hasNext()) {
                  nextSuperclass = (Class)this.interfaces.next();
                  seenInterfaces.add(nextSuperclass);
                  return nextSuperclass;
               } else {
                  nextSuperclass = (Class)wrapped.next();
                  Set currentInterfaces = new LinkedHashSet();
                  this.walkInterfaces(currentInterfaces, nextSuperclass);
                  this.interfaces = currentInterfaces.iterator();
                  return nextSuperclass;
               }
            }

            private void walkInterfaces(Set<Class<?>> addTo, Class<?> c) {
               Class[] var3 = c.getInterfaces();
               int var4 = var3.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  Class iface = var3[var5];
                  if (!seenInterfaces.contains(iface)) {
                     addTo.add(iface);
                  }

                  this.walkInterfaces(addTo, iface);
               }

            }

            public void remove() {
               throw new UnsupportedOperationException();
            }
         };
      };
   }

   static {
      namePrimitiveMap.put("boolean", Boolean.TYPE);
      namePrimitiveMap.put("byte", Byte.TYPE);
      namePrimitiveMap.put("char", Character.TYPE);
      namePrimitiveMap.put("short", Short.TYPE);
      namePrimitiveMap.put("int", Integer.TYPE);
      namePrimitiveMap.put("long", Long.TYPE);
      namePrimitiveMap.put("double", Double.TYPE);
      namePrimitiveMap.put("float", Float.TYPE);
      namePrimitiveMap.put("void", Void.TYPE);
      primitiveWrapperMap = new HashMap();
      primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
      primitiveWrapperMap.put(Byte.TYPE, Byte.class);
      primitiveWrapperMap.put(Character.TYPE, Character.class);
      primitiveWrapperMap.put(Short.TYPE, Short.class);
      primitiveWrapperMap.put(Integer.TYPE, Integer.class);
      primitiveWrapperMap.put(Long.TYPE, Long.class);
      primitiveWrapperMap.put(Double.TYPE, Double.class);
      primitiveWrapperMap.put(Float.TYPE, Float.class);
      primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
      wrapperPrimitiveMap = new HashMap();
      Iterator var0 = primitiveWrapperMap.entrySet().iterator();

      while(var0.hasNext()) {
         Entry<Class<?>, Class<?>> entry = (Entry)var0.next();
         Class<?> primitiveClass = (Class)entry.getKey();
         Class<?> wrapperClass = (Class)entry.getValue();
         if (!primitiveClass.equals(wrapperClass)) {
            wrapperPrimitiveMap.put(wrapperClass, primitiveClass);
         }
      }

      Map<String, String> m = new HashMap();
      m.put("int", "I");
      m.put("boolean", "Z");
      m.put("float", "F");
      m.put("long", "J");
      m.put("short", "S");
      m.put("byte", "B");
      m.put("double", "D");
      m.put("char", "C");
      Map<String, String> r = new HashMap();
      Iterator var6 = m.entrySet().iterator();

      while(var6.hasNext()) {
         Entry<String, String> e = (Entry)var6.next();
         r.put(e.getValue(), e.getKey());
      }

      abbreviationMap = Collections.unmodifiableMap(m);
      reverseAbbreviationMap = Collections.unmodifiableMap(r);
   }

   public static enum Interfaces {
      INCLUDE,
      EXCLUDE;
   }
}

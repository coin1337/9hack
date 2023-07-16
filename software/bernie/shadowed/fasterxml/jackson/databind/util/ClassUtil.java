package software.bernie.shadowed.fasterxml.jackson.databind.util;

import java.io.Closeable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.NoClass;

public final class ClassUtil {
   private static final Class<?> CLS_OBJECT = Object.class;
   private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];
   private static final ClassUtil.Ctor[] NO_CTORS = new ClassUtil.Ctor[0];
   private static final Iterator<?> EMPTY_ITERATOR = Collections.emptyIterator();

   public static <T> Iterator<T> emptyIterator() {
      return EMPTY_ITERATOR;
   }

   public static List<JavaType> findSuperTypes(JavaType type, Class<?> endBefore, boolean addClassItself) {
      if (type != null && !type.hasRawClass(endBefore) && !type.hasRawClass(Object.class)) {
         List<JavaType> result = new ArrayList(8);
         _addSuperTypes(type, endBefore, result, addClassItself);
         return result;
      } else {
         return Collections.emptyList();
      }
   }

   public static List<Class<?>> findRawSuperTypes(Class<?> cls, Class<?> endBefore, boolean addClassItself) {
      if (cls != null && cls != endBefore && cls != Object.class) {
         List<Class<?>> result = new ArrayList(8);
         _addRawSuperTypes(cls, endBefore, result, addClassItself);
         return result;
      } else {
         return Collections.emptyList();
      }
   }

   public static List<Class<?>> findSuperClasses(Class<?> cls, Class<?> endBefore, boolean addClassItself) {
      List<Class<?>> result = new LinkedList();
      if (cls != null && cls != endBefore) {
         if (addClassItself) {
            result.add(cls);
         }

         while((cls = cls.getSuperclass()) != null && cls != endBefore) {
            result.add(cls);
         }
      }

      return result;
   }

   /** @deprecated */
   @Deprecated
   public static List<Class<?>> findSuperTypes(Class<?> cls, Class<?> endBefore) {
      return findSuperTypes(cls, endBefore, new ArrayList(8));
   }

   /** @deprecated */
   @Deprecated
   public static List<Class<?>> findSuperTypes(Class<?> cls, Class<?> endBefore, List<Class<?>> result) {
      _addRawSuperTypes(cls, endBefore, result, false);
      return result;
   }

   private static void _addSuperTypes(JavaType type, Class<?> endBefore, Collection<JavaType> result, boolean addClassItself) {
      if (type != null) {
         Class<?> cls = type.getRawClass();
         if (cls != endBefore && cls != Object.class) {
            if (addClassItself) {
               if (result.contains(type)) {
                  return;
               }

               result.add(type);
            }

            Iterator i$ = type.getInterfaces().iterator();

            while(i$.hasNext()) {
               JavaType intCls = (JavaType)i$.next();
               _addSuperTypes(intCls, endBefore, result, true);
            }

            _addSuperTypes(type.getSuperClass(), endBefore, result, true);
         }
      }
   }

   private static void _addRawSuperTypes(Class<?> cls, Class<?> endBefore, Collection<Class<?>> result, boolean addClassItself) {
      if (cls != endBefore && cls != null && cls != Object.class) {
         if (addClassItself) {
            if (result.contains(cls)) {
               return;
            }

            result.add(cls);
         }

         Class[] arr$ = _interfaces(cls);
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            Class<?> intCls = arr$[i$];
            _addRawSuperTypes(intCls, endBefore, result, true);
         }

         _addRawSuperTypes(cls.getSuperclass(), endBefore, result, true);
      }
   }

   public static String canBeABeanType(Class<?> type) {
      if (type.isAnnotation()) {
         return "annotation";
      } else if (type.isArray()) {
         return "array";
      } else if (type.isEnum()) {
         return "enum";
      } else {
         return type.isPrimitive() ? "primitive" : null;
      }
   }

   public static String isLocalType(Class<?> type, boolean allowNonStatic) {
      try {
         if (hasEnclosingMethod(type)) {
            return "local/anonymous";
         }

         if (!allowNonStatic && !Modifier.isStatic(type.getModifiers()) && getEnclosingClass(type) != null) {
            return "non-static member class";
         }
      } catch (SecurityException var3) {
      } catch (NullPointerException var4) {
      }

      return null;
   }

   public static Class<?> getOuterClass(Class<?> type) {
      try {
         if (hasEnclosingMethod(type)) {
            return null;
         }

         if (!Modifier.isStatic(type.getModifiers())) {
            return getEnclosingClass(type);
         }
      } catch (SecurityException var2) {
      }

      return null;
   }

   public static boolean isProxyType(Class<?> type) {
      String name = type.getName();
      return name.startsWith("net.sf.cglib.proxy.") || name.startsWith("org.hibernate.proxy.");
   }

   public static boolean isConcrete(Class<?> type) {
      int mod = type.getModifiers();
      return (mod & 1536) == 0;
   }

   public static boolean isConcrete(Member member) {
      int mod = member.getModifiers();
      return (mod & 1536) == 0;
   }

   public static boolean isCollectionMapOrArray(Class<?> type) {
      if (type.isArray()) {
         return true;
      } else if (Collection.class.isAssignableFrom(type)) {
         return true;
      } else {
         return Map.class.isAssignableFrom(type);
      }
   }

   public static boolean isBogusClass(Class<?> cls) {
      return cls == Void.class || cls == Void.TYPE || cls == NoClass.class;
   }

   public static boolean isNonStaticInnerClass(Class<?> cls) {
      return !Modifier.isStatic(cls.getModifiers()) && getEnclosingClass(cls) != null;
   }

   public static boolean isObjectOrPrimitive(Class<?> cls) {
      return cls == CLS_OBJECT || cls.isPrimitive();
   }

   public static boolean hasClass(Object inst, Class<?> raw) {
      return inst != null && inst.getClass() == raw;
   }

   public static void verifyMustOverride(Class<?> expType, Object instance, String method) {
      if (instance.getClass() != expType) {
         throw new IllegalStateException(String.format("Sub-class %s (of class %s) must override method '%s'", instance.getClass().getName(), expType.getName(), method));
      }
   }

   /** @deprecated */
   @Deprecated
   public static boolean hasGetterSignature(Method m) {
      if (Modifier.isStatic(m.getModifiers())) {
         return false;
      } else {
         Class<?>[] pts = m.getParameterTypes();
         if (pts != null && pts.length != 0) {
            return false;
         } else {
            return Void.TYPE != m.getReturnType();
         }
      }
   }

   public static Throwable throwIfError(Throwable t) {
      if (t instanceof Error) {
         throw (Error)t;
      } else {
         return t;
      }
   }

   public static Throwable throwIfRTE(Throwable t) {
      if (t instanceof RuntimeException) {
         throw (RuntimeException)t;
      } else {
         return t;
      }
   }

   public static Throwable throwIfIOE(Throwable t) throws IOException {
      if (t instanceof IOException) {
         throw (IOException)t;
      } else {
         return t;
      }
   }

   public static Throwable getRootCause(Throwable t) {
      while(t.getCause() != null) {
         t = t.getCause();
      }

      return t;
   }

   public static Throwable throwRootCauseIfIOE(Throwable t) throws IOException {
      return throwIfIOE(getRootCause(t));
   }

   public static void throwAsIAE(Throwable t) {
      throwAsIAE(t, t.getMessage());
   }

   public static void throwAsIAE(Throwable t, String msg) {
      throwIfRTE(t);
      throwIfError(t);
      throw new IllegalArgumentException(msg, t);
   }

   public static <T> T throwAsMappingException(DeserializationContext ctxt, IOException e0) throws JsonMappingException {
      if (e0 instanceof JsonMappingException) {
         throw (JsonMappingException)e0;
      } else {
         JsonMappingException e = JsonMappingException.from(ctxt, e0.getMessage());
         e.initCause(e0);
         throw e;
      }
   }

   public static void unwrapAndThrowAsIAE(Throwable t) {
      throwAsIAE(getRootCause(t));
   }

   public static void unwrapAndThrowAsIAE(Throwable t, String msg) {
      throwAsIAE(getRootCause(t), msg);
   }

   public static void closeOnFailAndThrowAsIOE(JsonGenerator g, Exception fail) throws IOException {
      g.disable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);

      try {
         g.close();
      } catch (Exception var3) {
         fail.addSuppressed(var3);
      }

      throwIfIOE(fail);
      throwIfRTE(fail);
      throw new RuntimeException(fail);
   }

   public static void closeOnFailAndThrowAsIOE(JsonGenerator g, Closeable toClose, Exception fail) throws IOException {
      if (g != null) {
         g.disable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);

         try {
            g.close();
         } catch (Exception var5) {
            fail.addSuppressed(var5);
         }
      }

      if (toClose != null) {
         try {
            toClose.close();
         } catch (Exception var4) {
            fail.addSuppressed(var4);
         }
      }

      throwIfIOE(fail);
      throwIfRTE(fail);
      throw new RuntimeException(fail);
   }

   public static <T> T createInstance(Class<T> cls, boolean canFixAccess) throws IllegalArgumentException {
      Constructor<T> ctor = findConstructor(cls, canFixAccess);
      if (ctor == null) {
         throw new IllegalArgumentException("Class " + cls.getName() + " has no default (no arg) constructor");
      } else {
         try {
            return ctor.newInstance();
         } catch (Exception var4) {
            unwrapAndThrowAsIAE(var4, "Failed to instantiate class " + cls.getName() + ", problem: " + var4.getMessage());
            return null;
         }
      }
   }

   public static <T> Constructor<T> findConstructor(Class<T> cls, boolean forceAccess) throws IllegalArgumentException {
      try {
         Constructor<T> ctor = cls.getDeclaredConstructor();
         if (forceAccess) {
            checkAndFixAccess(ctor, forceAccess);
         } else if (!Modifier.isPublic(ctor.getModifiers())) {
            throw new IllegalArgumentException("Default constructor for " + cls.getName() + " is not accessible (non-public?): not allowed to try modify access via Reflection: cannot instantiate type");
         }

         return ctor;
      } catch (NoSuchMethodException var3) {
      } catch (Exception var4) {
         unwrapAndThrowAsIAE(var4, "Failed to find default constructor of class " + cls.getName() + ", problem: " + var4.getMessage());
      }

      return null;
   }

   public static Class<?> classOf(Object inst) {
      return inst == null ? null : inst.getClass();
   }

   public static Class<?> rawClass(JavaType t) {
      return t == null ? null : t.getRawClass();
   }

   public static <T> T nonNull(T valueOrNull, T defaultValue) {
      return valueOrNull == null ? defaultValue : valueOrNull;
   }

   public static String nullOrToString(Object value) {
      return value == null ? null : value.toString();
   }

   public static String nonNullString(String str) {
      return str == null ? "" : str;
   }

   public static String quotedOr(Object str, String forNull) {
      return str == null ? forNull : String.format("\"%s\"", str);
   }

   public static String getClassDescription(Object classOrInstance) {
      if (classOrInstance == null) {
         return "unknown";
      } else {
         Class<?> cls = classOrInstance instanceof Class ? (Class)classOrInstance : classOrInstance.getClass();
         return nameOf(cls);
      }
   }

   public static String classNameOf(Object inst) {
      return inst == null ? "[null]" : nameOf(inst.getClass());
   }

   public static String nameOf(Class<?> cls) {
      if (cls == null) {
         return "[null]";
      } else {
         int index;
         for(index = 0; cls.isArray(); cls = cls.getComponentType()) {
            ++index;
         }

         String base = cls.isPrimitive() ? cls.getSimpleName() : cls.getName();
         if (index > 0) {
            StringBuilder sb = new StringBuilder(base);

            do {
               sb.append("[]");
               --index;
            } while(index > 0);

            base = sb.toString();
         }

         return backticked(base);
      }
   }

   public static String nameOf(Named named) {
      return named == null ? "[null]" : backticked(named.getName());
   }

   public static String backticked(String text) {
      return text == null ? "[null]" : (new StringBuilder(text.length() + 2)).append('`').append(text).append('`').toString();
   }

   public static Object defaultValue(Class<?> cls) {
      if (cls == Integer.TYPE) {
         return 0;
      } else if (cls == Long.TYPE) {
         return 0L;
      } else if (cls == Boolean.TYPE) {
         return Boolean.FALSE;
      } else if (cls == Double.TYPE) {
         return 0.0D;
      } else if (cls == Float.TYPE) {
         return 0.0F;
      } else if (cls == Byte.TYPE) {
         return 0;
      } else if (cls == Short.TYPE) {
         return Short.valueOf((short)0);
      } else if (cls == Character.TYPE) {
         return '\u0000';
      } else {
         throw new IllegalArgumentException("Class " + cls.getName() + " is not a primitive type");
      }
   }

   public static Class<?> wrapperType(Class<?> primitiveType) {
      if (primitiveType == Integer.TYPE) {
         return Integer.class;
      } else if (primitiveType == Long.TYPE) {
         return Long.class;
      } else if (primitiveType == Boolean.TYPE) {
         return Boolean.class;
      } else if (primitiveType == Double.TYPE) {
         return Double.class;
      } else if (primitiveType == Float.TYPE) {
         return Float.class;
      } else if (primitiveType == Byte.TYPE) {
         return Byte.class;
      } else if (primitiveType == Short.TYPE) {
         return Short.class;
      } else if (primitiveType == Character.TYPE) {
         return Character.class;
      } else {
         throw new IllegalArgumentException("Class " + primitiveType.getName() + " is not a primitive type");
      }
   }

   public static Class<?> primitiveType(Class<?> type) {
      if (type.isPrimitive()) {
         return type;
      } else if (type == Integer.class) {
         return Integer.TYPE;
      } else if (type == Long.class) {
         return Long.TYPE;
      } else if (type == Boolean.class) {
         return Boolean.TYPE;
      } else if (type == Double.class) {
         return Double.TYPE;
      } else if (type == Float.class) {
         return Float.TYPE;
      } else if (type == Byte.class) {
         return Byte.TYPE;
      } else if (type == Short.class) {
         return Short.TYPE;
      } else {
         return type == Character.class ? Character.TYPE : null;
      }
   }

   /** @deprecated */
   @Deprecated
   public static void checkAndFixAccess(Member member) {
      checkAndFixAccess(member, false);
   }

   public static void checkAndFixAccess(Member member, boolean force) {
      AccessibleObject ao = (AccessibleObject)member;

      try {
         if (force || !Modifier.isPublic(member.getModifiers()) || !Modifier.isPublic(member.getDeclaringClass().getModifiers())) {
            ao.setAccessible(true);
         }
      } catch (SecurityException var5) {
         if (!ao.isAccessible()) {
            Class<?> declClass = member.getDeclaringClass();
            throw new IllegalArgumentException("Cannot access " + member + " (from class " + declClass.getName() + "; failed to set access: " + var5.getMessage());
         }
      }

   }

   public static Class<? extends Enum<?>> findEnumType(EnumSet<?> s) {
      return !s.isEmpty() ? findEnumType((Enum)s.iterator().next()) : ClassUtil.EnumTypeLocator.instance.enumTypeFor(s);
   }

   public static Class<? extends Enum<?>> findEnumType(EnumMap<?, ?> m) {
      return !m.isEmpty() ? findEnumType((Enum)m.keySet().iterator().next()) : ClassUtil.EnumTypeLocator.instance.enumTypeFor(m);
   }

   public static Class<? extends Enum<?>> findEnumType(Enum<?> en) {
      Class<?> ec = en.getClass();
      if (ec.getSuperclass() != Enum.class) {
         ec = ec.getSuperclass();
      }

      return ec;
   }

   public static Class<? extends Enum<?>> findEnumType(Class<?> cls) {
      if (cls.getSuperclass() != Enum.class) {
         cls = cls.getSuperclass();
      }

      return cls;
   }

   public static <T extends Annotation> Enum<?> findFirstAnnotatedEnumValue(Class<Enum<?>> enumClass, Class<T> annotationClass) {
      Field[] fields = getDeclaredFields(enumClass);
      Field[] arr$ = fields;
      int len$ = fields.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Field field = arr$[i$];
         if (field.isEnumConstant()) {
            Annotation defaultValueAnnotation = field.getAnnotation(annotationClass);
            if (defaultValueAnnotation != null) {
               String name = field.getName();
               Enum[] arr$ = (Enum[])enumClass.getEnumConstants();
               int len$ = arr$.length;

               for(int i$ = 0; i$ < len$; ++i$) {
                  Enum<?> enumValue = arr$[i$];
                  if (name.equals(enumValue.name())) {
                     return enumValue;
                  }
               }
            }
         }
      }

      return null;
   }

   public static boolean isJacksonStdImpl(Object impl) {
      return impl == null || isJacksonStdImpl(impl.getClass());
   }

   public static boolean isJacksonStdImpl(Class<?> implClass) {
      return implClass.getAnnotation(JacksonStdImpl.class) != null;
   }

   public static String getPackageName(Class<?> cls) {
      Package pkg = cls.getPackage();
      return pkg == null ? null : pkg.getName();
   }

   public static boolean hasEnclosingMethod(Class<?> cls) {
      return !isObjectOrPrimitive(cls) && cls.getEnclosingMethod() != null;
   }

   public static Field[] getDeclaredFields(Class<?> cls) {
      return cls.getDeclaredFields();
   }

   public static Method[] getDeclaredMethods(Class<?> cls) {
      return cls.getDeclaredMethods();
   }

   public static Annotation[] findClassAnnotations(Class<?> cls) {
      return isObjectOrPrimitive(cls) ? NO_ANNOTATIONS : cls.getDeclaredAnnotations();
   }

   public static Method[] getClassMethods(Class<?> cls) {
      try {
         return getDeclaredMethods(cls);
      } catch (NoClassDefFoundError var6) {
         ClassLoader loader = Thread.currentThread().getContextClassLoader();
         if (loader == null) {
            throw var6;
         } else {
            Class contextClass;
            try {
               contextClass = loader.loadClass(cls.getName());
            } catch (ClassNotFoundException var5) {
               var6.addSuppressed(var5);
               throw var6;
            }

            return contextClass.getDeclaredMethods();
         }
      }
   }

   public static ClassUtil.Ctor[] getConstructors(Class<?> cls) {
      if (!cls.isInterface() && !isObjectOrPrimitive(cls)) {
         Constructor<?>[] rawCtors = cls.getDeclaredConstructors();
         int len = rawCtors.length;
         ClassUtil.Ctor[] result = new ClassUtil.Ctor[len];

         for(int i = 0; i < len; ++i) {
            result[i] = new ClassUtil.Ctor(rawCtors[i]);
         }

         return result;
      } else {
         return NO_CTORS;
      }
   }

   public static Class<?> getDeclaringClass(Class<?> cls) {
      return isObjectOrPrimitive(cls) ? null : cls.getDeclaringClass();
   }

   public static Type getGenericSuperclass(Class<?> cls) {
      return cls.getGenericSuperclass();
   }

   public static Type[] getGenericInterfaces(Class<?> cls) {
      return cls.getGenericInterfaces();
   }

   public static Class<?> getEnclosingClass(Class<?> cls) {
      return isObjectOrPrimitive(cls) ? null : cls.getEnclosingClass();
   }

   private static Class<?>[] _interfaces(Class<?> cls) {
      return cls.getInterfaces();
   }

   public static final class Ctor {
      public final Constructor<?> _ctor;
      private Annotation[] _annotations;
      private Annotation[][] _paramAnnotations;
      private int _paramCount = -1;

      public Ctor(Constructor<?> ctor) {
         this._ctor = ctor;
      }

      public Constructor<?> getConstructor() {
         return this._ctor;
      }

      public int getParamCount() {
         int c = this._paramCount;
         if (c < 0) {
            c = this._ctor.getParameterTypes().length;
            this._paramCount = c;
         }

         return c;
      }

      public Class<?> getDeclaringClass() {
         return this._ctor.getDeclaringClass();
      }

      public Annotation[] getDeclaredAnnotations() {
         Annotation[] result = this._annotations;
         if (result == null) {
            result = this._ctor.getDeclaredAnnotations();
            this._annotations = result;
         }

         return result;
      }

      public Annotation[][] getParameterAnnotations() {
         Annotation[][] result = this._paramAnnotations;
         if (result == null) {
            result = this._ctor.getParameterAnnotations();
            this._paramAnnotations = result;
         }

         return result;
      }
   }

   private static class EnumTypeLocator {
      static final ClassUtil.EnumTypeLocator instance = new ClassUtil.EnumTypeLocator();
      private final Field enumSetTypeField = locateField(EnumSet.class, "elementType", Class.class);
      private final Field enumMapTypeField = locateField(EnumMap.class, "elementType", Class.class);

      public Class<? extends Enum<?>> enumTypeFor(EnumSet<?> set) {
         if (this.enumSetTypeField != null) {
            return (Class)this.get(set, this.enumSetTypeField);
         } else {
            throw new IllegalStateException("Cannot figure out type for EnumSet (odd JDK platform?)");
         }
      }

      public Class<? extends Enum<?>> enumTypeFor(EnumMap<?, ?> set) {
         if (this.enumMapTypeField != null) {
            return (Class)this.get(set, this.enumMapTypeField);
         } else {
            throw new IllegalStateException("Cannot figure out type for EnumMap (odd JDK platform?)");
         }
      }

      private Object get(Object bean, Field field) {
         try {
            return field.get(bean);
         } catch (Exception var4) {
            throw new IllegalArgumentException(var4);
         }
      }

      private static Field locateField(Class<?> fromClass, String expectedName, Class<?> type) {
         Field found = null;
         Field[] fields = ClassUtil.getDeclaredFields(fromClass);
         Field[] arr$ = fields;
         int len$ = fields.length;

         int i$;
         Field f;
         for(i$ = 0; i$ < len$; ++i$) {
            f = arr$[i$];
            if (expectedName.equals(f.getName()) && f.getType() == type) {
               found = f;
               break;
            }
         }

         if (found == null) {
            arr$ = fields;
            len$ = fields.length;

            for(i$ = 0; i$ < len$; ++i$) {
               f = arr$[i$];
               if (f.getType() == type) {
                  if (found != null) {
                     return null;
                  }

                  found = f;
               }
            }
         }

         if (found != null) {
            try {
               found.setAccessible(true);
            } catch (Throwable var9) {
            }
         }

         return found;
      }
   }
}

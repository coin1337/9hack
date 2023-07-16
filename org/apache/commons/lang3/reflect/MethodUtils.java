package org.apache.commons.lang3.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;

public class MethodUtils {
   private static final Comparator<Method> METHOD_BY_SIGNATURE = Comparator.comparing(Method::toString);

   public static Object invokeMethod(Object object, String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      return invokeMethod(object, methodName, ArrayUtils.EMPTY_OBJECT_ARRAY, (Class[])null);
   }

   public static Object invokeMethod(Object object, boolean forceAccess, String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      return invokeMethod(object, forceAccess, methodName, ArrayUtils.EMPTY_OBJECT_ARRAY, (Class[])null);
   }

   public static Object invokeMethod(Object object, String methodName, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      args = ArrayUtils.nullToEmpty(args);
      Class<?>[] parameterTypes = ClassUtils.toClass(args);
      return invokeMethod(object, methodName, args, parameterTypes);
   }

   public static Object invokeMethod(Object object, boolean forceAccess, String methodName, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      args = ArrayUtils.nullToEmpty(args);
      Class<?>[] parameterTypes = ClassUtils.toClass(args);
      return invokeMethod(object, forceAccess, methodName, args, parameterTypes);
   }

   public static Object invokeMethod(Object object, boolean forceAccess, String methodName, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      parameterTypes = ArrayUtils.nullToEmpty(parameterTypes);
      args = ArrayUtils.nullToEmpty(args);
      Method method = null;
      String messagePrefix;
      if (forceAccess) {
         messagePrefix = "No such method: ";
         method = getMatchingMethod(object.getClass(), methodName, parameterTypes);
         if (method != null && !method.isAccessible()) {
            method.setAccessible(true);
         }
      } else {
         messagePrefix = "No such accessible method: ";
         method = getMatchingAccessibleMethod(object.getClass(), methodName, parameterTypes);
      }

      if (method == null) {
         throw new NoSuchMethodException(messagePrefix + methodName + "() on object: " + object.getClass().getName());
      } else {
         args = toVarArgs(method, args);
         return method.invoke(object, args);
      }
   }

   public static Object invokeMethod(Object object, String methodName, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      return invokeMethod(object, false, methodName, args, parameterTypes);
   }

   public static Object invokeExactMethod(Object object, String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      return invokeExactMethod(object, methodName, ArrayUtils.EMPTY_OBJECT_ARRAY, (Class[])null);
   }

   public static Object invokeExactMethod(Object object, String methodName, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      args = ArrayUtils.nullToEmpty(args);
      Class<?>[] parameterTypes = ClassUtils.toClass(args);
      return invokeExactMethod(object, methodName, args, parameterTypes);
   }

   public static Object invokeExactMethod(Object object, String methodName, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      args = ArrayUtils.nullToEmpty(args);
      parameterTypes = ArrayUtils.nullToEmpty(parameterTypes);
      Method method = getAccessibleMethod(object.getClass(), methodName, parameterTypes);
      if (method == null) {
         throw new NoSuchMethodException("No such accessible method: " + methodName + "() on object: " + object.getClass().getName());
      } else {
         return method.invoke(object, args);
      }
   }

   public static Object invokeExactStaticMethod(Class<?> cls, String methodName, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      args = ArrayUtils.nullToEmpty(args);
      parameterTypes = ArrayUtils.nullToEmpty(parameterTypes);
      Method method = getAccessibleMethod(cls, methodName, parameterTypes);
      if (method == null) {
         throw new NoSuchMethodException("No such accessible method: " + methodName + "() on class: " + cls.getName());
      } else {
         return method.invoke((Object)null, args);
      }
   }

   public static Object invokeStaticMethod(Class<?> cls, String methodName, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      args = ArrayUtils.nullToEmpty(args);
      Class<?>[] parameterTypes = ClassUtils.toClass(args);
      return invokeStaticMethod(cls, methodName, args, parameterTypes);
   }

   public static Object invokeStaticMethod(Class<?> cls, String methodName, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      args = ArrayUtils.nullToEmpty(args);
      parameterTypes = ArrayUtils.nullToEmpty(parameterTypes);
      Method method = getMatchingAccessibleMethod(cls, methodName, parameterTypes);
      if (method == null) {
         throw new NoSuchMethodException("No such accessible method: " + methodName + "() on class: " + cls.getName());
      } else {
         args = toVarArgs(method, args);
         return method.invoke((Object)null, args);
      }
   }

   private static Object[] toVarArgs(Method method, Object[] args) {
      if (method.isVarArgs()) {
         Class<?>[] methodParameterTypes = method.getParameterTypes();
         args = getVarArgs(args, methodParameterTypes);
      }

      return args;
   }

   static Object[] getVarArgs(Object[] args, Class<?>[] methodParameterTypes) {
      if (args.length != methodParameterTypes.length || args[args.length - 1] != null && !args[args.length - 1].getClass().equals(methodParameterTypes[methodParameterTypes.length - 1])) {
         Object[] newArgs = new Object[methodParameterTypes.length];
         System.arraycopy(args, 0, newArgs, 0, methodParameterTypes.length - 1);
         Class<?> varArgComponentType = methodParameterTypes[methodParameterTypes.length - 1].getComponentType();
         int varArgLength = args.length - methodParameterTypes.length + 1;
         Object varArgsArray = Array.newInstance(ClassUtils.primitiveToWrapper(varArgComponentType), varArgLength);
         System.arraycopy(args, methodParameterTypes.length - 1, varArgsArray, 0, varArgLength);
         if (varArgComponentType.isPrimitive()) {
            varArgsArray = ArrayUtils.toPrimitive(varArgsArray);
         }

         newArgs[methodParameterTypes.length - 1] = varArgsArray;
         return newArgs;
      } else {
         return args;
      }
   }

   public static Object invokeExactStaticMethod(Class<?> cls, String methodName, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      args = ArrayUtils.nullToEmpty(args);
      Class<?>[] parameterTypes = ClassUtils.toClass(args);
      return invokeExactStaticMethod(cls, methodName, args, parameterTypes);
   }

   public static Method getAccessibleMethod(Class<?> cls, String methodName, Class<?>... parameterTypes) {
      try {
         return getAccessibleMethod(cls.getMethod(methodName, parameterTypes));
      } catch (NoSuchMethodException var4) {
         return null;
      }
   }

   public static Method getAccessibleMethod(Method method) {
      if (!MemberUtils.isAccessible(method)) {
         return null;
      } else {
         Class<?> cls = method.getDeclaringClass();
         if (Modifier.isPublic(cls.getModifiers())) {
            return method;
         } else {
            String methodName = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();
            method = getAccessibleMethodFromInterfaceNest(cls, methodName, parameterTypes);
            if (method == null) {
               method = getAccessibleMethodFromSuperclass(cls, methodName, parameterTypes);
            }

            return method;
         }
      }
   }

   private static Method getAccessibleMethodFromSuperclass(Class<?> cls, String methodName, Class<?>... parameterTypes) {
      for(Class parentClass = cls.getSuperclass(); parentClass != null; parentClass = parentClass.getSuperclass()) {
         if (Modifier.isPublic(parentClass.getModifiers())) {
            try {
               return parentClass.getMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException var5) {
               return null;
            }
         }
      }

      return null;
   }

   private static Method getAccessibleMethodFromInterfaceNest(Class<?> cls, String methodName, Class<?>... parameterTypes) {
      while(cls != null) {
         Class<?>[] interfaces = cls.getInterfaces();
         Class[] var4 = interfaces;
         int var5 = interfaces.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Class<?> anInterface = var4[var6];
            if (Modifier.isPublic(anInterface.getModifiers())) {
               try {
                  return anInterface.getDeclaredMethod(methodName, parameterTypes);
               } catch (NoSuchMethodException var9) {
                  Method method = getAccessibleMethodFromInterfaceNest(anInterface, methodName, parameterTypes);
                  if (method != null) {
                     return method;
                  }
               }
            }
         }

         cls = cls.getSuperclass();
      }

      return null;
   }

   public static Method getMatchingAccessibleMethod(Class<?> cls, String methodName, Class<?>... parameterTypes) {
      try {
         Method method = cls.getMethod(methodName, parameterTypes);
         MemberUtils.setAccessibleWorkaround(method);
         return method;
      } catch (NoSuchMethodException var12) {
         Method[] methods = cls.getMethods();
         List<Method> matchingMethods = new ArrayList();
         Method[] var5 = methods;
         int var6 = methods.length;

         Method accessibleMethod;
         for(int var7 = 0; var7 < var6; ++var7) {
            accessibleMethod = var5[var7];
            if (accessibleMethod.getName().equals(methodName) && MemberUtils.isMatchingMethod(accessibleMethod, parameterTypes)) {
               matchingMethods.add(accessibleMethod);
            }
         }

         matchingMethods.sort(METHOD_BY_SIGNATURE);
         Method bestMatch = null;
         Iterator var15 = matchingMethods.iterator();

         while(true) {
            do {
               do {
                  if (!var15.hasNext()) {
                     if (bestMatch != null) {
                        MemberUtils.setAccessibleWorkaround(bestMatch);
                     }

                     if (bestMatch != null && bestMatch.isVarArgs() && bestMatch.getParameterTypes().length > 0 && parameterTypes.length > 0) {
                        Class<?>[] methodParameterTypes = bestMatch.getParameterTypes();
                        Class<?> methodParameterComponentType = methodParameterTypes[methodParameterTypes.length - 1].getComponentType();
                        String methodParameterComponentTypeName = ClassUtils.primitiveToWrapper(methodParameterComponentType).getName();
                        Class<?> lastParameterType = parameterTypes[parameterTypes.length - 1];
                        String parameterTypeName = lastParameterType == null ? null : lastParameterType.getName();
                        String parameterTypeSuperClassName = lastParameterType == null ? null : lastParameterType.getSuperclass().getName();
                        if (parameterTypeName != null && parameterTypeSuperClassName != null && !methodParameterComponentTypeName.equals(parameterTypeName) && !methodParameterComponentTypeName.equals(parameterTypeSuperClassName)) {
                           return null;
                        }
                     }

                     return bestMatch;
                  }

                  Method method = (Method)var15.next();
                  accessibleMethod = getAccessibleMethod(method);
               } while(accessibleMethod == null);
            } while(bestMatch != null && MemberUtils.compareMethodFit(accessibleMethod, bestMatch, parameterTypes) >= 0);

            bestMatch = accessibleMethod;
         }
      }
   }

   public static Method getMatchingMethod(Class<?> cls, String methodName, Class<?>... parameterTypes) {
      Validate.notNull(cls, "Null class not allowed.");
      Validate.notEmpty((CharSequence)methodName, "Null or blank methodName not allowed.");
      Method[] methodArray = cls.getDeclaredMethods();
      List<Class<?>> superclassList = ClassUtils.getAllSuperclasses(cls);

      Class klass;
      for(Iterator var5 = superclassList.iterator(); var5.hasNext(); methodArray = (Method[])ArrayUtils.addAll((Object[])methodArray, (Object[])klass.getDeclaredMethods())) {
         klass = (Class)var5.next();
      }

      Method inexactMatch = null;
      Method[] var11 = methodArray;
      int var7 = methodArray.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Method method = var11[var8];
         if (methodName.equals(method.getName()) && Objects.deepEquals(parameterTypes, method.getParameterTypes())) {
            return method;
         }

         if (methodName.equals(method.getName()) && ClassUtils.isAssignable(parameterTypes, method.getParameterTypes(), true) && (inexactMatch == null || distance(parameterTypes, method.getParameterTypes()) < distance(parameterTypes, inexactMatch.getParameterTypes()))) {
            inexactMatch = method;
         }
      }

      return inexactMatch;
   }

   private static int distance(Class<?>[] classArray, Class<?>[] toClassArray) {
      int answer = 0;
      if (!ClassUtils.isAssignable(classArray, toClassArray, true)) {
         return -1;
      } else {
         for(int offset = 0; offset < classArray.length; ++offset) {
            if (!classArray[offset].equals(toClassArray[offset])) {
               if (ClassUtils.isAssignable(classArray[offset], toClassArray[offset], true) && !ClassUtils.isAssignable(classArray[offset], toClassArray[offset], false)) {
                  ++answer;
               } else {
                  answer += 2;
               }
            }
         }

         return answer;
      }
   }

   public static Set<Method> getOverrideHierarchy(Method method, ClassUtils.Interfaces interfacesBehavior) {
      Validate.notNull(method);
      Set<Method> result = new LinkedHashSet();
      result.add(method);
      Class<?>[] parameterTypes = method.getParameterTypes();
      Class<?> declaringClass = method.getDeclaringClass();
      Iterator<Class<?>> hierarchy = ClassUtils.hierarchy(declaringClass, interfacesBehavior).iterator();
      hierarchy.next();

      while(true) {
         label32:
         while(true) {
            Method m;
            do {
               if (!hierarchy.hasNext()) {
                  return result;
               }

               Class<?> c = (Class)hierarchy.next();
               m = getMatchingAccessibleMethod(c, method.getName(), parameterTypes);
            } while(m == null);

            if (Arrays.equals(m.getParameterTypes(), parameterTypes)) {
               result.add(m);
            } else {
               Map<TypeVariable<?>, Type> typeArguments = TypeUtils.getTypeArguments(declaringClass, m.getDeclaringClass());

               for(int i = 0; i < parameterTypes.length; ++i) {
                  Type childType = TypeUtils.unrollVariables(typeArguments, method.getGenericParameterTypes()[i]);
                  Type parentType = TypeUtils.unrollVariables(typeArguments, m.getGenericParameterTypes()[i]);
                  if (!TypeUtils.equals(childType, parentType)) {
                     continue label32;
                  }
               }

               result.add(m);
            }
         }
      }
   }

   public static Method[] getMethodsWithAnnotation(Class<?> cls, Class<? extends Annotation> annotationCls) {
      return getMethodsWithAnnotation(cls, annotationCls, false, false);
   }

   public static List<Method> getMethodsListWithAnnotation(Class<?> cls, Class<? extends Annotation> annotationCls) {
      return getMethodsListWithAnnotation(cls, annotationCls, false, false);
   }

   public static Method[] getMethodsWithAnnotation(Class<?> cls, Class<? extends Annotation> annotationCls, boolean searchSupers, boolean ignoreAccess) {
      List<Method> annotatedMethodsList = getMethodsListWithAnnotation(cls, annotationCls, searchSupers, ignoreAccess);
      return (Method[])annotatedMethodsList.toArray(ArrayUtils.EMPTY_METHOD_ARRAY);
   }

   public static List<Method> getMethodsListWithAnnotation(Class<?> cls, Class<? extends Annotation> annotationCls, boolean searchSupers, boolean ignoreAccess) {
      Validate.notNull(cls, "The class must not be null");
      Validate.notNull(annotationCls, "The annotation class must not be null");
      List<Class<?>> classes = searchSupers ? getAllSuperclassesAndInterfaces(cls) : new ArrayList();
      ((List)classes).add(0, cls);
      List<Method> annotatedMethods = new ArrayList();
      Iterator var6 = ((List)classes).iterator();

      while(var6.hasNext()) {
         Class<?> acls = (Class)var6.next();
         Method[] methods = ignoreAccess ? acls.getDeclaredMethods() : acls.getMethods();
         Method[] var9 = methods;
         int var10 = methods.length;

         for(int var11 = 0; var11 < var10; ++var11) {
            Method method = var9[var11];
            if (method.getAnnotation(annotationCls) != null) {
               annotatedMethods.add(method);
            }
         }
      }

      return annotatedMethods;
   }

   public static <A extends Annotation> A getAnnotation(Method method, Class<A> annotationCls, boolean searchSupers, boolean ignoreAccess) {
      Validate.notNull(method, "The method must not be null");
      Validate.notNull(annotationCls, "The annotation class must not be null");
      if (!ignoreAccess && !MemberUtils.isAccessible(method)) {
         return null;
      } else {
         A annotation = method.getAnnotation(annotationCls);
         if (annotation == null && searchSupers) {
            Class<?> mcls = method.getDeclaringClass();
            List<Class<?>> classes = getAllSuperclassesAndInterfaces(mcls);
            Iterator var7 = classes.iterator();

            while(var7.hasNext()) {
               Class<?> acls = (Class)var7.next();
               Method equivalentMethod = ignoreAccess ? getMatchingMethod(acls, method.getName(), method.getParameterTypes()) : getMatchingAccessibleMethod(acls, method.getName(), method.getParameterTypes());
               if (equivalentMethod != null) {
                  annotation = equivalentMethod.getAnnotation(annotationCls);
                  if (annotation != null) {
                     break;
                  }
               }
            }
         }

         return annotation;
      }
   }

   private static List<Class<?>> getAllSuperclassesAndInterfaces(Class<?> cls) {
      if (cls == null) {
         return null;
      } else {
         List<Class<?>> allSuperClassesAndInterfaces = new ArrayList();
         List<Class<?>> allSuperclasses = ClassUtils.getAllSuperclasses(cls);
         int superClassIndex = 0;
         List<Class<?>> allInterfaces = ClassUtils.getAllInterfaces(cls);

         Class acls;
         for(int interfaceIndex = 0; interfaceIndex < allInterfaces.size() || superClassIndex < allSuperclasses.size(); allSuperClassesAndInterfaces.add(acls)) {
            if (interfaceIndex >= allInterfaces.size()) {
               acls = (Class)allSuperclasses.get(superClassIndex++);
            } else if (superClassIndex < allSuperclasses.size() && interfaceIndex >= superClassIndex && superClassIndex < interfaceIndex) {
               acls = (Class)allSuperclasses.get(superClassIndex++);
            } else {
               acls = (Class)allInterfaces.get(interfaceIndex++);
            }
         }

         return allSuperClassesAndInterfaces;
      }
   }
}

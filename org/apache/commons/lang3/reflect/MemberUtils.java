package org.apache.commons.lang3.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.apache.commons.lang3.ClassUtils;

abstract class MemberUtils {
   private static final int ACCESS_TEST = 7;
   private static final Class<?>[] ORDERED_PRIMITIVE_TYPES;

   static boolean setAccessibleWorkaround(AccessibleObject o) {
      if (o != null && !o.isAccessible()) {
         Member m = (Member)o;
         if (!o.isAccessible() && Modifier.isPublic(m.getModifiers()) && isPackageAccess(m.getDeclaringClass().getModifiers())) {
            try {
               o.setAccessible(true);
               return true;
            } catch (SecurityException var3) {
            }
         }

         return false;
      } else {
         return false;
      }
   }

   static boolean isPackageAccess(int modifiers) {
      return (modifiers & 7) == 0;
   }

   static boolean isAccessible(Member m) {
      return m != null && Modifier.isPublic(m.getModifiers()) && !m.isSynthetic();
   }

   static int compareConstructorFit(Constructor<?> left, Constructor<?> right, Class<?>[] actual) {
      return compareParameterTypes(MemberUtils.Executable.of(left), MemberUtils.Executable.of(right), actual);
   }

   static int compareMethodFit(Method left, Method right, Class<?>[] actual) {
      return compareParameterTypes(MemberUtils.Executable.of(left), MemberUtils.Executable.of(right), actual);
   }

   private static int compareParameterTypes(MemberUtils.Executable left, MemberUtils.Executable right, Class<?>[] actual) {
      float leftCost = getTotalTransformationCost(actual, left);
      float rightCost = getTotalTransformationCost(actual, right);
      return Float.compare(leftCost, rightCost);
   }

   private static float getTotalTransformationCost(Class<?>[] srcArgs, MemberUtils.Executable executable) {
      Class<?>[] destArgs = executable.getParameterTypes();
      boolean isVarArgs = executable.isVarArgs();
      float totalCost = 0.0F;
      long normalArgsLen = isVarArgs ? (long)(destArgs.length - 1) : (long)destArgs.length;
      if ((long)srcArgs.length < normalArgsLen) {
         return Float.MAX_VALUE;
      } else {
         for(int i = 0; (long)i < normalArgsLen; ++i) {
            totalCost += getObjectTransformationCost(srcArgs[i], destArgs[i]);
         }

         if (isVarArgs) {
            boolean noVarArgsPassed = srcArgs.length < destArgs.length;
            boolean explicitArrayForVarargs = srcArgs.length == destArgs.length && srcArgs[srcArgs.length - 1] != null && srcArgs[srcArgs.length - 1].isArray();
            float varArgsCost = 0.001F;
            Class<?> destClass = destArgs[destArgs.length - 1].getComponentType();
            if (noVarArgsPassed) {
               totalCost += getObjectTransformationCost(destClass, Object.class) + 0.001F;
            } else if (explicitArrayForVarargs) {
               Class<?> sourceClass = srcArgs[srcArgs.length - 1].getComponentType();
               totalCost += getObjectTransformationCost(sourceClass, destClass) + 0.001F;
            } else {
               for(int i = destArgs.length - 1; i < srcArgs.length; ++i) {
                  Class<?> srcClass = srcArgs[i];
                  totalCost += getObjectTransformationCost(srcClass, destClass) + 0.001F;
               }
            }
         }

         return totalCost;
      }
   }

   private static float getObjectTransformationCost(Class<?> srcClass, Class<?> destClass) {
      if (destClass.isPrimitive()) {
         return getPrimitivePromotionCost(srcClass, destClass);
      } else {
         float cost;
         for(cost = 0.0F; srcClass != null && !destClass.equals(srcClass); srcClass = srcClass.getSuperclass()) {
            if (destClass.isInterface() && ClassUtils.isAssignable(srcClass, destClass)) {
               cost += 0.25F;
               break;
            }

            ++cost;
         }

         if (srcClass == null) {
            ++cost;
         }

         return cost;
      }
   }

   private static float getPrimitivePromotionCost(Class<?> srcClass, Class<?> destClass) {
      if (srcClass == null) {
         return 1.5F;
      } else {
         float cost = 0.0F;
         Class<?> cls = srcClass;
         if (!srcClass.isPrimitive()) {
            cost += 0.1F;
            cls = ClassUtils.wrapperToPrimitive(srcClass);
         }

         for(int i = 0; cls != destClass && i < ORDERED_PRIMITIVE_TYPES.length; ++i) {
            if (cls == ORDERED_PRIMITIVE_TYPES[i]) {
               cost += 0.1F;
               if (i < ORDERED_PRIMITIVE_TYPES.length - 1) {
                  cls = ORDERED_PRIMITIVE_TYPES[i + 1];
               }
            }
         }

         return cost;
      }
   }

   static boolean isMatchingMethod(Method method, Class<?>[] parameterTypes) {
      return isMatchingExecutable(MemberUtils.Executable.of(method), parameterTypes);
   }

   static boolean isMatchingConstructor(Constructor<?> method, Class<?>[] parameterTypes) {
      return isMatchingExecutable(MemberUtils.Executable.of(method), parameterTypes);
   }

   private static boolean isMatchingExecutable(MemberUtils.Executable method, Class<?>[] parameterTypes) {
      Class<?>[] methodParameterTypes = method.getParameterTypes();
      if (ClassUtils.isAssignable(parameterTypes, methodParameterTypes, true)) {
         return true;
      } else if (!method.isVarArgs()) {
         return false;
      } else {
         int i;
         for(i = 0; i < methodParameterTypes.length - 1 && i < parameterTypes.length; ++i) {
            if (!ClassUtils.isAssignable(parameterTypes[i], methodParameterTypes[i], true)) {
               return false;
            }
         }

         for(Class varArgParameterType = methodParameterTypes[methodParameterTypes.length - 1].getComponentType(); i < parameterTypes.length; ++i) {
            if (!ClassUtils.isAssignable(parameterTypes[i], varArgParameterType, true)) {
               return false;
            }
         }

         return true;
      }
   }

   static {
      ORDERED_PRIMITIVE_TYPES = new Class[]{Byte.TYPE, Short.TYPE, Character.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE};
   }

   private static final class Executable {
      private final Class<?>[] parameterTypes;
      private final boolean isVarArgs;

      private static MemberUtils.Executable of(Method method) {
         return new MemberUtils.Executable(method);
      }

      private static MemberUtils.Executable of(Constructor<?> constructor) {
         return new MemberUtils.Executable(constructor);
      }

      private Executable(Method method) {
         this.parameterTypes = method.getParameterTypes();
         this.isVarArgs = method.isVarArgs();
      }

      private Executable(Constructor<?> constructor) {
         this.parameterTypes = constructor.getParameterTypes();
         this.isVarArgs = constructor.isVarArgs();
      }

      public Class<?>[] getParameterTypes() {
         return this.parameterTypes;
      }

      public boolean isVarArgs() {
         return this.isVarArgs;
      }
   }
}

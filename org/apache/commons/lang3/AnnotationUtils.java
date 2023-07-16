package org.apache.commons.lang3;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class AnnotationUtils {
   private static final ToStringStyle TO_STRING_STYLE = new ToStringStyle() {
      private static final long serialVersionUID = 1L;

      {
         this.setDefaultFullDetail(true);
         this.setArrayContentDetail(true);
         this.setUseClassName(true);
         this.setUseShortClassName(true);
         this.setUseIdentityHashCode(false);
         this.setContentStart("(");
         this.setContentEnd(")");
         this.setFieldSeparator(", ");
         this.setArrayStart("[");
         this.setArrayEnd("]");
      }

      protected String getShortClassName(Class<?> cls) {
         Iterator var2 = ClassUtils.getAllInterfaces(cls).iterator();

         Class iface;
         do {
            if (!var2.hasNext()) {
               return "";
            }

            iface = (Class)var2.next();
         } while(!Annotation.class.isAssignableFrom(iface));

         return "@" + iface.getName();
      }

      protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {
         if (value instanceof Annotation) {
            value = AnnotationUtils.toString((Annotation)value);
         }

         super.appendDetail(buffer, fieldName, value);
      }
   };

   public static boolean equals(Annotation a1, Annotation a2) {
      if (a1 == a2) {
         return true;
      } else if (a1 != null && a2 != null) {
         Class<? extends Annotation> type1 = a1.annotationType();
         Class<? extends Annotation> type2 = a2.annotationType();
         Validate.notNull(type1, "Annotation %s with null annotationType()", a1);
         Validate.notNull(type2, "Annotation %s with null annotationType()", a2);
         if (!type1.equals(type2)) {
            return false;
         } else {
            try {
               Method[] var4 = type1.getDeclaredMethods();
               int var5 = var4.length;

               for(int var6 = 0; var6 < var5; ++var6) {
                  Method m = var4[var6];
                  if (m.getParameterTypes().length == 0 && isValidAnnotationMemberType(m.getReturnType())) {
                     Object v1 = m.invoke(a1);
                     Object v2 = m.invoke(a2);
                     if (!memberEquals(m.getReturnType(), v1, v2)) {
                        return false;
                     }
                  }
               }

               return true;
            } catch (InvocationTargetException | IllegalAccessException var10) {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public static int hashCode(Annotation a) {
      int result = 0;
      Class<? extends Annotation> type = a.annotationType();
      Method[] var3 = type.getDeclaredMethods();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Method m = var3[var5];

         try {
            Object value = m.invoke(a);
            if (value == null) {
               throw new IllegalStateException(String.format("Annotation method %s returned null", m));
            }

            result += hashMember(m.getName(), value);
         } catch (RuntimeException var8) {
            throw var8;
         } catch (Exception var9) {
            throw new RuntimeException(var9);
         }
      }

      return result;
   }

   public static String toString(Annotation a) {
      ToStringBuilder builder = new ToStringBuilder(a, TO_STRING_STYLE);
      Method[] var2 = a.annotationType().getDeclaredMethods();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Method m = var2[var4];
         if (m.getParameterTypes().length <= 0) {
            try {
               builder.append(m.getName(), m.invoke(a));
            } catch (RuntimeException var7) {
               throw var7;
            } catch (Exception var8) {
               throw new RuntimeException(var8);
            }
         }
      }

      return builder.build();
   }

   public static boolean isValidAnnotationMemberType(Class<?> type) {
      if (type == null) {
         return false;
      } else {
         if (type.isArray()) {
            type = type.getComponentType();
         }

         return type.isPrimitive() || type.isEnum() || type.isAnnotation() || String.class.equals(type) || Class.class.equals(type);
      }
   }

   private static int hashMember(String name, Object value) {
      int part1 = name.hashCode() * 127;
      if (value.getClass().isArray()) {
         return part1 ^ arrayMemberHash(value.getClass().getComponentType(), value);
      } else {
         return value instanceof Annotation ? part1 ^ hashCode((Annotation)value) : part1 ^ value.hashCode();
      }
   }

   private static boolean memberEquals(Class<?> type, Object o1, Object o2) {
      if (o1 == o2) {
         return true;
      } else if (o1 != null && o2 != null) {
         if (type.isArray()) {
            return arrayMemberEquals(type.getComponentType(), o1, o2);
         } else {
            return type.isAnnotation() ? equals((Annotation)o1, (Annotation)o2) : o1.equals(o2);
         }
      } else {
         return false;
      }
   }

   private static boolean arrayMemberEquals(Class<?> componentType, Object o1, Object o2) {
      if (componentType.isAnnotation()) {
         return annotationArrayMemberEquals((Annotation[])((Annotation[])o1), (Annotation[])((Annotation[])o2));
      } else if (componentType.equals(Byte.TYPE)) {
         return Arrays.equals((byte[])((byte[])o1), (byte[])((byte[])o2));
      } else if (componentType.equals(Short.TYPE)) {
         return Arrays.equals((short[])((short[])o1), (short[])((short[])o2));
      } else if (componentType.equals(Integer.TYPE)) {
         return Arrays.equals((int[])((int[])o1), (int[])((int[])o2));
      } else if (componentType.equals(Character.TYPE)) {
         return Arrays.equals((char[])((char[])o1), (char[])((char[])o2));
      } else if (componentType.equals(Long.TYPE)) {
         return Arrays.equals((long[])((long[])o1), (long[])((long[])o2));
      } else if (componentType.equals(Float.TYPE)) {
         return Arrays.equals((float[])((float[])o1), (float[])((float[])o2));
      } else if (componentType.equals(Double.TYPE)) {
         return Arrays.equals((double[])((double[])o1), (double[])((double[])o2));
      } else {
         return componentType.equals(Boolean.TYPE) ? Arrays.equals((boolean[])((boolean[])o1), (boolean[])((boolean[])o2)) : Arrays.equals((Object[])((Object[])o1), (Object[])((Object[])o2));
      }
   }

   private static boolean annotationArrayMemberEquals(Annotation[] a1, Annotation[] a2) {
      if (a1.length != a2.length) {
         return false;
      } else {
         for(int i = 0; i < a1.length; ++i) {
            if (!equals(a1[i], a2[i])) {
               return false;
            }
         }

         return true;
      }
   }

   private static int arrayMemberHash(Class<?> componentType, Object o) {
      if (componentType.equals(Byte.TYPE)) {
         return Arrays.hashCode((byte[])((byte[])o));
      } else if (componentType.equals(Short.TYPE)) {
         return Arrays.hashCode((short[])((short[])o));
      } else if (componentType.equals(Integer.TYPE)) {
         return Arrays.hashCode((int[])((int[])o));
      } else if (componentType.equals(Character.TYPE)) {
         return Arrays.hashCode((char[])((char[])o));
      } else if (componentType.equals(Long.TYPE)) {
         return Arrays.hashCode((long[])((long[])o));
      } else if (componentType.equals(Float.TYPE)) {
         return Arrays.hashCode((float[])((float[])o));
      } else if (componentType.equals(Double.TYPE)) {
         return Arrays.hashCode((double[])((double[])o));
      } else {
         return componentType.equals(Boolean.TYPE) ? Arrays.hashCode((boolean[])((boolean[])o)) : Arrays.hashCode((Object[])((Object[])o));
      }
   }
}

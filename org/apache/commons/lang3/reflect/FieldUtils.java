package org.apache.commons.lang3.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.Validate;

public class FieldUtils {
   public static Field getField(Class<?> cls, String fieldName) {
      Field field = getField(cls, fieldName, false);
      MemberUtils.setAccessibleWorkaround(field);
      return field;
   }

   public static Field getField(Class<?> cls, String fieldName, boolean forceAccess) {
      Validate.notNull(cls, "The class must not be null");
      Validate.isTrue(StringUtils.isNotBlank(fieldName), "The field name must not be blank/empty");

      for(Class acls = cls; acls != null; acls = acls.getSuperclass()) {
         try {
            Field field = acls.getDeclaredField(fieldName);
            if (!Modifier.isPublic(field.getModifiers())) {
               if (!forceAccess) {
                  continue;
               }

               field.setAccessible(true);
            }

            return field;
         } catch (NoSuchFieldException var8) {
         }
      }

      Field match = null;
      Iterator var10 = ClassUtils.getAllInterfaces(cls).iterator();

      while(var10.hasNext()) {
         Class class1 = (Class)var10.next();

         try {
            Field test = class1.getField(fieldName);
            Validate.isTrue(match == null, "Reference to field %s is ambiguous relative to %s; a matching field exists on two or more implemented interfaces.", fieldName, cls);
            match = test;
         } catch (NoSuchFieldException var7) {
         }
      }

      return match;
   }

   public static Field getDeclaredField(Class<?> cls, String fieldName) {
      return getDeclaredField(cls, fieldName, false);
   }

   public static Field getDeclaredField(Class<?> cls, String fieldName, boolean forceAccess) {
      Validate.notNull(cls, "The class must not be null");
      Validate.isTrue(StringUtils.isNotBlank(fieldName), "The field name must not be blank/empty");

      try {
         Field field = cls.getDeclaredField(fieldName);
         if (!MemberUtils.isAccessible(field)) {
            if (!forceAccess) {
               return null;
            }

            field.setAccessible(true);
         }

         return field;
      } catch (NoSuchFieldException var4) {
         return null;
      }
   }

   public static Field[] getAllFields(Class<?> cls) {
      List<Field> allFieldsList = getAllFieldsList(cls);
      return (Field[])allFieldsList.toArray(ArrayUtils.EMPTY_FIELD_ARRAY);
   }

   public static List<Field> getAllFieldsList(Class<?> cls) {
      Validate.notNull(cls, "The class must not be null");
      List<Field> allFields = new ArrayList();

      for(Class currentClass = cls; currentClass != null; currentClass = currentClass.getSuperclass()) {
         Field[] declaredFields = currentClass.getDeclaredFields();
         Collections.addAll(allFields, declaredFields);
      }

      return allFields;
   }

   public static Field[] getFieldsWithAnnotation(Class<?> cls, Class<? extends Annotation> annotationCls) {
      List<Field> annotatedFieldsList = getFieldsListWithAnnotation(cls, annotationCls);
      return (Field[])annotatedFieldsList.toArray(ArrayUtils.EMPTY_FIELD_ARRAY);
   }

   public static List<Field> getFieldsListWithAnnotation(Class<?> cls, Class<? extends Annotation> annotationCls) {
      Validate.notNull(annotationCls, "The annotation class must not be null");
      List<Field> allFields = getAllFieldsList(cls);
      List<Field> annotatedFields = new ArrayList();
      Iterator var4 = allFields.iterator();

      while(var4.hasNext()) {
         Field field = (Field)var4.next();
         if (field.getAnnotation(annotationCls) != null) {
            annotatedFields.add(field);
         }
      }

      return annotatedFields;
   }

   public static Object readStaticField(Field field) throws IllegalAccessException {
      return readStaticField(field, false);
   }

   public static Object readStaticField(Field field, boolean forceAccess) throws IllegalAccessException {
      Validate.notNull(field, "The field must not be null");
      Validate.isTrue(Modifier.isStatic(field.getModifiers()), "The field '%s' is not static", field.getName());
      return readField((Field)field, (Object)null, forceAccess);
   }

   public static Object readStaticField(Class<?> cls, String fieldName) throws IllegalAccessException {
      return readStaticField(cls, fieldName, false);
   }

   public static Object readStaticField(Class<?> cls, String fieldName, boolean forceAccess) throws IllegalAccessException {
      Field field = getField(cls, fieldName, forceAccess);
      Validate.notNull(field, "Cannot locate field '%s' on %s", fieldName, cls);
      return readStaticField(field, false);
   }

   public static Object readDeclaredStaticField(Class<?> cls, String fieldName) throws IllegalAccessException {
      return readDeclaredStaticField(cls, fieldName, false);
   }

   public static Object readDeclaredStaticField(Class<?> cls, String fieldName, boolean forceAccess) throws IllegalAccessException {
      Field field = getDeclaredField(cls, fieldName, forceAccess);
      Validate.notNull(field, "Cannot locate declared field %s.%s", cls.getName(), fieldName);
      return readStaticField(field, false);
   }

   public static Object readField(Field field, Object target) throws IllegalAccessException {
      return readField(field, target, false);
   }

   public static Object readField(Field field, Object target, boolean forceAccess) throws IllegalAccessException {
      Validate.notNull(field, "The field must not be null");
      if (forceAccess && !field.isAccessible()) {
         field.setAccessible(true);
      } else {
         MemberUtils.setAccessibleWorkaround(field);
      }

      return field.get(target);
   }

   public static Object readField(Object target, String fieldName) throws IllegalAccessException {
      return readField(target, fieldName, false);
   }

   public static Object readField(Object target, String fieldName, boolean forceAccess) throws IllegalAccessException {
      Validate.notNull(target, "target object must not be null");
      Class<?> cls = target.getClass();
      Field field = getField(cls, fieldName, forceAccess);
      Validate.isTrue(field != null, "Cannot locate field %s on %s", fieldName, cls);
      return readField(field, target, false);
   }

   public static Object readDeclaredField(Object target, String fieldName) throws IllegalAccessException {
      return readDeclaredField(target, fieldName, false);
   }

   public static Object readDeclaredField(Object target, String fieldName, boolean forceAccess) throws IllegalAccessException {
      Validate.notNull(target, "target object must not be null");
      Class<?> cls = target.getClass();
      Field field = getDeclaredField(cls, fieldName, forceAccess);
      Validate.isTrue(field != null, "Cannot locate declared field %s.%s", cls, fieldName);
      return readField(field, target, false);
   }

   public static void writeStaticField(Field field, Object value) throws IllegalAccessException {
      writeStaticField(field, value, false);
   }

   public static void writeStaticField(Field field, Object value, boolean forceAccess) throws IllegalAccessException {
      Validate.notNull(field, "The field must not be null");
      Validate.isTrue(Modifier.isStatic(field.getModifiers()), "The field %s.%s is not static", field.getDeclaringClass().getName(), field.getName());
      writeField((Field)field, (Object)null, value, forceAccess);
   }

   public static void writeStaticField(Class<?> cls, String fieldName, Object value) throws IllegalAccessException {
      writeStaticField(cls, fieldName, value, false);
   }

   public static void writeStaticField(Class<?> cls, String fieldName, Object value, boolean forceAccess) throws IllegalAccessException {
      Field field = getField(cls, fieldName, forceAccess);
      Validate.notNull(field, "Cannot locate field %s on %s", fieldName, cls);
      writeStaticField(field, value, false);
   }

   public static void writeDeclaredStaticField(Class<?> cls, String fieldName, Object value) throws IllegalAccessException {
      writeDeclaredStaticField(cls, fieldName, value, false);
   }

   public static void writeDeclaredStaticField(Class<?> cls, String fieldName, Object value, boolean forceAccess) throws IllegalAccessException {
      Field field = getDeclaredField(cls, fieldName, forceAccess);
      Validate.notNull(field, "Cannot locate declared field %s.%s", cls.getName(), fieldName);
      writeField((Field)field, (Object)null, value, false);
   }

   public static void writeField(Field field, Object target, Object value) throws IllegalAccessException {
      writeField(field, target, value, false);
   }

   public static void writeField(Field field, Object target, Object value, boolean forceAccess) throws IllegalAccessException {
      Validate.notNull(field, "The field must not be null");
      if (forceAccess && !field.isAccessible()) {
         field.setAccessible(true);
      } else {
         MemberUtils.setAccessibleWorkaround(field);
      }

      field.set(target, value);
   }

   public static void removeFinalModifier(Field field) {
      removeFinalModifier(field, true);
   }

   /** @deprecated */
   @Deprecated
   public static void removeFinalModifier(Field field, boolean forceAccess) {
      Validate.notNull(field, "The field must not be null");

      try {
         if (Modifier.isFinal(field.getModifiers())) {
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            boolean doForceAccess = forceAccess && !modifiersField.isAccessible();
            if (doForceAccess) {
               modifiersField.setAccessible(true);
            }

            try {
               modifiersField.setInt(field, field.getModifiers() & -17);
            } finally {
               if (doForceAccess) {
                  modifiersField.setAccessible(false);
               }

            }
         }
      } catch (IllegalAccessException | NoSuchFieldException var8) {
         if (SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_12)) {
            throw new UnsupportedOperationException("In java 12+ final cannot be removed.", var8);
         }
      }

   }

   public static void writeField(Object target, String fieldName, Object value) throws IllegalAccessException {
      writeField(target, fieldName, value, false);
   }

   public static void writeField(Object target, String fieldName, Object value, boolean forceAccess) throws IllegalAccessException {
      Validate.notNull(target, "target object must not be null");
      Class<?> cls = target.getClass();
      Field field = getField(cls, fieldName, forceAccess);
      Validate.isTrue(field != null, "Cannot locate declared field %s.%s", cls.getName(), fieldName);
      writeField(field, target, value, false);
   }

   public static void writeDeclaredField(Object target, String fieldName, Object value) throws IllegalAccessException {
      writeDeclaredField(target, fieldName, value, false);
   }

   public static void writeDeclaredField(Object target, String fieldName, Object value, boolean forceAccess) throws IllegalAccessException {
      Validate.notNull(target, "target object must not be null");
      Class<?> cls = target.getClass();
      Field field = getDeclaredField(cls, fieldName, forceAccess);
      Validate.isTrue(field != null, "Cannot locate declared field %s.%s", cls.getName(), fieldName);
      writeField(field, target, value, false);
   }
}

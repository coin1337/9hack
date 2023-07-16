package software.bernie.shadowed.fasterxml.jackson.databind.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonInclude;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMethod;

public class BeanUtil {
   public static String okNameForGetter(AnnotatedMethod am, boolean stdNaming) {
      String name = am.getName();
      String str = okNameForIsGetter(am, name, stdNaming);
      if (str == null) {
         str = okNameForRegularGetter(am, name, stdNaming);
      }

      return str;
   }

   public static String okNameForRegularGetter(AnnotatedMethod am, String name, boolean stdNaming) {
      if (name.startsWith("get")) {
         if ("getCallbacks".equals(name)) {
            if (isCglibGetCallbacks(am)) {
               return null;
            }
         } else if ("getMetaClass".equals(name) && isGroovyMetaClassGetter(am)) {
            return null;
         }

         return stdNaming ? stdManglePropertyName(name, 3) : legacyManglePropertyName(name, 3);
      } else {
         return null;
      }
   }

   public static String okNameForIsGetter(AnnotatedMethod am, String name, boolean stdNaming) {
      if (name.startsWith("is")) {
         Class<?> rt = am.getRawType();
         if (rt == Boolean.class || rt == Boolean.TYPE) {
            return stdNaming ? stdManglePropertyName(name, 2) : legacyManglePropertyName(name, 2);
         }
      }

      return null;
   }

   /** @deprecated */
   @Deprecated
   public static String okNameForSetter(AnnotatedMethod am, boolean stdNaming) {
      String name = okNameForMutator(am, "set", stdNaming);
      return name == null || "metaClass".equals(name) && isGroovyMetaClassSetter(am) ? null : name;
   }

   public static String okNameForMutator(AnnotatedMethod am, String prefix, boolean stdNaming) {
      String name = am.getName();
      if (name.startsWith(prefix)) {
         return stdNaming ? stdManglePropertyName(name, prefix.length()) : legacyManglePropertyName(name, prefix.length());
      } else {
         return null;
      }
   }

   public static Object getDefaultValue(JavaType type) {
      Class<?> cls = type.getRawClass();
      Class<?> prim = ClassUtil.primitiveType(cls);
      if (prim != null) {
         return ClassUtil.defaultValue(prim);
      } else if (!type.isContainerType() && !type.isReferenceType()) {
         if (cls == String.class) {
            return "";
         } else if (type.isTypeOrSubTypeOf(Date.class)) {
            return new Date(0L);
         } else if (type.isTypeOrSubTypeOf(Calendar.class)) {
            Calendar c = new GregorianCalendar();
            c.setTimeInMillis(0L);
            return c;
         } else {
            return null;
         }
      } else {
         return JsonInclude.Include.NON_EMPTY;
      }
   }

   protected static boolean isCglibGetCallbacks(AnnotatedMethod am) {
      Class<?> rt = am.getRawType();
      if (rt.isArray()) {
         Class<?> compType = rt.getComponentType();
         String pkgName = ClassUtil.getPackageName(compType);
         if (pkgName != null && pkgName.contains(".cglib")) {
            return pkgName.startsWith("net.sf.cglib") || pkgName.startsWith("org.hibernate.repackage.cglib") || pkgName.startsWith("org.springframework.cglib");
         }
      }

      return false;
   }

   protected static boolean isGroovyMetaClassSetter(AnnotatedMethod am) {
      Class<?> argType = am.getRawParameterType(0);
      String pkgName = ClassUtil.getPackageName(argType);
      return pkgName != null && pkgName.startsWith("groovy.lang");
   }

   protected static boolean isGroovyMetaClassGetter(AnnotatedMethod am) {
      String pkgName = ClassUtil.getPackageName(am.getRawType());
      return pkgName != null && pkgName.startsWith("groovy.lang");
   }

   protected static String legacyManglePropertyName(String basename, int offset) {
      int end = basename.length();
      if (end == offset) {
         return null;
      } else {
         char c = basename.charAt(offset);
         char d = Character.toLowerCase(c);
         if (c == d) {
            return basename.substring(offset);
         } else {
            StringBuilder sb = new StringBuilder(end - offset);
            sb.append(d);

            for(int i = offset + 1; i < end; ++i) {
               c = basename.charAt(i);
               d = Character.toLowerCase(c);
               if (c == d) {
                  sb.append(basename, i, end);
                  break;
               }

               sb.append(d);
            }

            return sb.toString();
         }
      }
   }

   protected static String stdManglePropertyName(String basename, int offset) {
      int end = basename.length();
      if (end == offset) {
         return null;
      } else {
         char c0 = basename.charAt(offset);
         char c1 = Character.toLowerCase(c0);
         if (c0 == c1) {
            return basename.substring(offset);
         } else if (offset + 1 < end && Character.isUpperCase(basename.charAt(offset + 1))) {
            return basename.substring(offset);
         } else {
            StringBuilder sb = new StringBuilder(end - offset);
            sb.append(c1);
            sb.append(basename, offset + 1, end);
            return sb.toString();
         }
      }
   }
}

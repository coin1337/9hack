package org.apache.commons.lang3;

public class ClassPathUtils {
   public static String toFullyQualifiedName(Class<?> context, String resourceName) {
      Validate.notNull(context, "Parameter '%s' must not be null!", "context");
      Validate.notNull(resourceName, "Parameter '%s' must not be null!", "resourceName");
      return toFullyQualifiedName(context.getPackage(), resourceName);
   }

   public static String toFullyQualifiedName(Package context, String resourceName) {
      Validate.notNull(context, "Parameter '%s' must not be null!", "context");
      Validate.notNull(resourceName, "Parameter '%s' must not be null!", "resourceName");
      return context.getName() + "." + resourceName;
   }

   public static String toFullyQualifiedPath(Class<?> context, String resourceName) {
      Validate.notNull(context, "Parameter '%s' must not be null!", "context");
      Validate.notNull(resourceName, "Parameter '%s' must not be null!", "resourceName");
      return toFullyQualifiedPath(context.getPackage(), resourceName);
   }

   public static String toFullyQualifiedPath(Package context, String resourceName) {
      Validate.notNull(context, "Parameter '%s' must not be null!", "context");
      Validate.notNull(resourceName, "Parameter '%s' must not be null!", "resourceName");
      return context.getName().replace('.', '/') + "/" + resourceName;
   }
}

package org.apache.commons.lang3;

import java.net.URLClassLoader;
import java.util.Arrays;

public class ClassLoaderUtils {
   public static String toString(ClassLoader classLoader) {
      return classLoader instanceof URLClassLoader ? toString((URLClassLoader)classLoader) : classLoader.toString();
   }

   public static String toString(URLClassLoader classLoader) {
      return classLoader + Arrays.toString(classLoader.getURLs());
   }
}

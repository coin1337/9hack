package org.apache.commons.lang3.math;

import org.apache.commons.lang3.Validate;

public class IEEE754rUtils {
   public static double min(double... array) {
      Validate.notNull(array, "The Array must not be null");
      Validate.isTrue(array.length != 0, "Array cannot be empty.");
      double min = array[0];

      for(int i = 1; i < array.length; ++i) {
         min = min(array[i], min);
      }

      return min;
   }

   public static float min(float... array) {
      Validate.notNull(array, "The Array must not be null");
      Validate.isTrue(array.length != 0, "Array cannot be empty.");
      float min = array[0];

      for(int i = 1; i < array.length; ++i) {
         min = min(array[i], min);
      }

      return min;
   }

   public static double min(double a, double b, double c) {
      return min(min(a, b), c);
   }

   public static double min(double a, double b) {
      if (Double.isNaN(a)) {
         return b;
      } else {
         return Double.isNaN(b) ? a : Math.min(a, b);
      }
   }

   public static float min(float a, float b, float c) {
      return min(min(a, b), c);
   }

   public static float min(float a, float b) {
      if (Float.isNaN(a)) {
         return b;
      } else {
         return Float.isNaN(b) ? a : Math.min(a, b);
      }
   }

   public static double max(double... array) {
      Validate.notNull(array, "The Array must not be null");
      Validate.isTrue(array.length != 0, "Array cannot be empty.");
      double max = array[0];

      for(int j = 1; j < array.length; ++j) {
         max = max(array[j], max);
      }

      return max;
   }

   public static float max(float... array) {
      Validate.notNull(array, "The Array must not be null");
      Validate.isTrue(array.length != 0, "Array cannot be empty.");
      float max = array[0];

      for(int j = 1; j < array.length; ++j) {
         max = max(array[j], max);
      }

      return max;
   }

   public static double max(double a, double b, double c) {
      return max(max(a, b), c);
   }

   public static double max(double a, double b) {
      if (Double.isNaN(a)) {
         return b;
      } else {
         return Double.isNaN(b) ? a : Math.max(a, b);
      }
   }

   public static float max(float a, float b, float c) {
      return max(max(a, b), c);
   }

   public static float max(float a, float b) {
      if (Float.isNaN(a)) {
         return b;
      } else {
         return Float.isNaN(b) ? a : Math.max(a, b);
      }
   }
}

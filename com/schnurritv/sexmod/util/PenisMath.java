package com.schnurritv.sexmod.util;

import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class PenisMath {
   public static Vec3d Lerp(Vec3d start, Vec3d end, int step) {
      if (step == 0) {
         return end;
      } else {
         Vec3d distance = end.func_178788_d(start);
         return start.func_72441_c(distance.field_72450_a / (double)step, distance.field_72448_b / (double)step, distance.field_72449_c / (double)step);
      }
   }

   public static double Lerp(double start, double end, double percentage) {
      return start + (end - start) * percentage;
   }

   public static Vec3d Lerp(Vec3d start, Vec3d end, double percentage) {
      Vec3d distance = end.func_178788_d(start);
      return start.func_178787_e(new Vec3d(distance.field_72450_a * percentage, distance.field_72448_b * percentage, distance.field_72449_c * percentage));
   }

   public static Vec2f Lerp(Vec2f start, Vec2f end, float percentage) {
      Vec2f distance = new Vec2f(end.field_189982_i - start.field_189982_i, end.field_189983_j - start.field_189983_j);
      return new Vec2f(distance.field_189982_i * percentage, distance.field_189983_j * percentage);
   }

   public static double cosineInterpolation(double y1, double y2, double percentage) {
      double mu2 = (1.0D - Math.cos(percentage * 3.141592653589793D)) / 2.0D;
      return y1 * (1.0D - mu2) + y2 * mu2;
   }

   public static float clamp(float value, float min, float max) {
      return Math.max(min, Math.min(max, value));
   }

   public static float stabilize(float value, float lastValue, float maxDifference) {
      if (Math.abs(value - lastValue) <= maxDifference) {
         return value;
      } else if (Math.abs(value) < Math.abs(lastValue)) {
         return lastValue > 0.0F ? lastValue - maxDifference : lastValue + maxDifference;
      } else {
         return value > 0.0F ? value - maxDifference : value + maxDifference;
      }
   }

   public static double degreesToGeckoRot(double degree) {
      return 6.283185307179586D / (360.0D / degree);
   }

   public static int roundToInt(double v) {
      return Math.round((float)v);
   }

   public static Vec3d getLocalVector(Vec3d original, float Yrot) {
      return new Vec3d(-Math.sin((double)(Yrot + 90.0F) * 0.017453292519943295D) * original.field_72450_a - Math.sin((double)Yrot * 0.017453292519943295D) * original.field_72449_c, original.field_72448_b, Math.cos((double)(Yrot + 90.0F) * 0.017453292519943295D) * original.field_72449_c + Math.cos((double)Yrot * 0.017453292519943295D) * original.field_72449_c);
   }
}

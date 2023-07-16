package software.bernie.geckolib3.core.easing;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.DoubleStream;
import software.bernie.geckolib3.core.util.Memoizer;

public class EasingManager {
   static Function<Double, Double> quart = poly(4.0D);
   static Function<Double, Double> quint = poly(5.0D);
   static Function<EasingManager.EasingFunctionArgs, Function<Double, Double>> getEasingFunction = Memoizer.memoize(EasingManager::getEasingFuncImpl);

   public static double ease(double number, EasingType easingType, List<Double> easingArgs) {
      Double firstArg = easingArgs != null && easingArgs.size() >= 1 ? (Double)easingArgs.get(0) : null;
      return (Double)((Function)getEasingFunction.apply(new EasingManager.EasingFunctionArgs(easingType, firstArg))).apply(number);
   }

   static Function<Double, Double> getEasingFuncImpl(EasingManager.EasingFunctionArgs args) {
      switch(args.easingType) {
      case Linear:
      default:
         return in(EasingManager::linear);
      case Step:
         return in(step(args.arg0));
      case EaseInSine:
         return in(EasingManager::sin);
      case EaseOutSine:
         return out(EasingManager::sin);
      case EaseInOutSine:
         return inOut(EasingManager::sin);
      case EaseInQuad:
         return in(EasingManager::quad);
      case EaseOutQuad:
         return out(EasingManager::quad);
      case EaseInOutQuad:
         return inOut(EasingManager::quad);
      case EaseInCubic:
         return in(EasingManager::cubic);
      case EaseOutCubic:
         return out(EasingManager::cubic);
      case EaseInOutCubic:
         return inOut(EasingManager::cubic);
      case EaseInExpo:
         return in(EasingManager::exp);
      case EaseOutExpo:
         return out(EasingManager::exp);
      case EaseInOutExpo:
         return inOut(EasingManager::exp);
      case EaseInCirc:
         return in(EasingManager::circle);
      case EaseOutCirc:
         return out(EasingManager::circle);
      case EaseInOutCirc:
         return inOut(EasingManager::circle);
      case EaseInQuart:
         return in(quart);
      case EaseOutQuart:
         return out(quart);
      case EaseInOutQuart:
         return inOut(quart);
      case EaseInQuint:
         return in(quint);
      case EaseOutQuint:
         return out(quint);
      case EaseInOutQuint:
         return inOut(quint);
      case EaseInBack:
         return in(back(args.arg0));
      case EaseOutBack:
         return out(back(args.arg0));
      case EaseInOutBack:
         return inOut(back(args.arg0));
      case EaseInElastic:
         return in(elastic(args.arg0));
      case EaseOutElastic:
         return out(elastic(args.arg0));
      case EaseInOutElastic:
         return inOut(elastic(args.arg0));
      case EaseInBounce:
         return in(bounce(args.arg0));
      case EaseOutBounce:
         return out(bounce(args.arg0));
      case EaseInOutBounce:
         return inOut(bounce(args.arg0));
      }
   }

   static Function<Double, Double> in(Function<Double, Double> easing) {
      return easing;
   }

   static Function<Double, Double> out(Function<Double, Double> easing) {
      return (t) -> {
         return 1.0D - (Double)easing.apply(1.0D - t);
      };
   }

   static Function<Double, Double> inOut(Function<Double, Double> easing) {
      return (t) -> {
         return t < 0.5D ? (Double)easing.apply(t * 2.0D) / 2.0D : 1.0D - (Double)easing.apply((1.0D - t) * 2.0D) / 2.0D;
      };
   }

   static Function<Double, Double> step0() {
      return (n) -> {
         return n > 0.0D ? 1.0D : 0.0D;
      };
   }

   static Function<Double, Double> step1() {
      return (n) -> {
         return n >= 1.0D ? 1.0D : 0.0D;
      };
   }

   static double linear(double t) {
      return t;
   }

   static double quad(double t) {
      return t * t;
   }

   static double cubic(double t) {
      return t * t * t;
   }

   static Function<Double, Double> poly(double n) {
      return (t) -> {
         return Math.pow(t, n);
      };
   }

   static double sin(double t) {
      return 1.0D - Math.cos((double)((float)(t * 3.141592653589793D / 2.0D)));
   }

   static double circle(double t) {
      return 1.0D - Math.sqrt(1.0D - t * t);
   }

   static double exp(double t) {
      return Math.pow(2.0D, 10.0D * (t - 1.0D));
   }

   static Function<Double, Double> elastic(Double bounciness) {
      double p = (bounciness == null ? 1.0D : bounciness) * 3.141592653589793D;
      return (t) -> {
         return 1.0D - Math.pow(Math.cos((double)((float)(t * 3.141592653589793D / 2.0D))), 3.0D) * Math.cos((double)((float)(t * p)));
      };
   }

   static Function<Double, Double> back(Double s) {
      double p = s == null ? 1.70158D : s * 1.70158D;
      return (t) -> {
         return t * t * ((p + 1.0D) * t - p);
      };
   }

   public static Function<Double, Double> bounce(Double s) {
      double k = s == null ? 0.5D : s;
      Function<Double, Double> q = (x) -> {
         return 7.5625D * x * x;
      };
      Function<Double, Double> w = (x) -> {
         return 30.25D * k * Math.pow(x - 0.5454545454545454D, 2.0D) + 1.0D - k;
      };
      Function<Double, Double> r = (x) -> {
         return 121.0D * k * k * Math.pow(x - 0.8181818181818182D, 2.0D) + 1.0D - k * k;
      };
      Function<Double, Double> t = (x) -> {
         return 484.0D * k * k * k * Math.pow(x - 0.9545454545454546D, 2.0D) + 1.0D - k * k * k;
      };
      return (x) -> {
         return min((Double)q.apply(x), (Double)w.apply(x), (Double)r.apply(x), (Double)t.apply(x));
      };
   }

   static Function<Double, Double> step(Double stepArg) {
      int steps = stepArg != null ? stepArg.intValue() : 2;
      double[] intervals = stepRange(steps);
      return (t) -> {
         return intervals[findIntervalBorderIndex(t, intervals, false)];
      };
   }

   static double min(double a, double b, double c, double d) {
      return Math.min(Math.min(a, b), Math.min(c, d));
   }

   static int findIntervalBorderIndex(double point, double[] intervals, boolean useRightBorder) {
      if (point < intervals[0]) {
         return 0;
      } else if (point > intervals[intervals.length - 1]) {
         return intervals.length - 1;
      } else {
         int indexOfNumberToCompare = false;
         int leftBorderIndex = 0;
         int rightBorderIndex = intervals.length - 1;

         while(rightBorderIndex - leftBorderIndex != 1) {
            int indexOfNumberToCompare = leftBorderIndex + (rightBorderIndex - leftBorderIndex) / 2;
            if (point >= intervals[indexOfNumberToCompare]) {
               leftBorderIndex = indexOfNumberToCompare;
            } else {
               rightBorderIndex = indexOfNumberToCompare;
            }
         }

         return useRightBorder ? rightBorderIndex : leftBorderIndex;
      }
   }

   static double[] stepRange(int steps) {
      double stop = 1.0D;
      if (steps < 2) {
         throw new IllegalArgumentException("steps must be > 2, got:" + steps);
      } else {
         double stepLength = 1.0D / (double)steps;
         AtomicInteger i = new AtomicInteger();
         return DoubleStream.generate(() -> {
            return (double)i.getAndIncrement() * stepLength;
         }).limit((long)steps).toArray();
      }
   }

   static class EasingFunctionArgs {
      public final EasingType easingType;
      public final Double arg0;

      public EasingFunctionArgs(EasingType easingType, Double arg0) {
         this.easingType = easingType;
         this.arg0 = arg0;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            EasingManager.EasingFunctionArgs that = (EasingManager.EasingFunctionArgs)o;
            return this.easingType == that.easingType && Objects.equals(this.arg0, that.arg0);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.easingType, this.arg0});
      }
   }
}

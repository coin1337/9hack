package software.bernie.geckolib3.core.util;

import java.util.function.Function;
import software.bernie.geckolib3.core.easing.EasingManager;
import software.bernie.geckolib3.core.easing.EasingType;
import software.bernie.geckolib3.core.keyframe.AnimationPoint;

public class MathUtil {
   public static float lerpValues(AnimationPoint animationPoint, EasingType easingType, Function<Double, Double> customEasingMethod) {
      if (animationPoint.currentTick >= animationPoint.animationEndTick) {
         return animationPoint.animationEndValue.floatValue();
      } else if (animationPoint.currentTick == 0.0D && animationPoint.animationEndTick == 0.0D) {
         return animationPoint.animationEndValue.floatValue();
      } else if (easingType == EasingType.CUSTOM && customEasingMethod != null) {
         return lerpValues((Double)customEasingMethod.apply(animationPoint.currentTick / animationPoint.animationEndTick), animationPoint.animationStartValue, animationPoint.animationEndValue);
      } else {
         if (easingType == EasingType.NONE && animationPoint.keyframe != null) {
            easingType = animationPoint.keyframe.easingType;
         }

         double ease = EasingManager.ease(animationPoint.currentTick / animationPoint.animationEndTick, easingType, animationPoint.keyframe == null ? null : animationPoint.keyframe.easingArgs);
         return lerpValues(ease, animationPoint.animationStartValue, animationPoint.animationEndValue);
      }
   }

   public static float lerpValues(double percentCompleted, double startValue, double endValue) {
      return (float)lerp(percentCompleted, startValue, endValue);
   }

   public static double lerp(double pct, double start, double end) {
      return start + pct * (end - start);
   }
}

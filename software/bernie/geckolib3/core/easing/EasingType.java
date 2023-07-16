package software.bernie.geckolib3.core.easing;

public enum EasingType {
   NONE,
   CUSTOM,
   Linear,
   Step,
   EaseInSine,
   EaseOutSine,
   EaseInOutSine,
   EaseInQuad,
   EaseOutQuad,
   EaseInOutQuad,
   EaseInCubic,
   EaseOutCubic,
   EaseInOutCubic,
   EaseInQuart,
   EaseOutQuart,
   EaseInOutQuart,
   EaseInQuint,
   EaseOutQuint,
   EaseInOutQuint,
   EaseInExpo,
   EaseOutExpo,
   EaseInOutExpo,
   EaseInCirc,
   EaseOutCirc,
   EaseInOutCirc,
   EaseInBack,
   EaseOutBack,
   EaseInOutBack,
   EaseInElastic,
   EaseOutElastic,
   EaseInOutElastic,
   EaseInBounce,
   EaseOutBounce,
   EaseInOutBounce;

   public static EasingType getEasingTypeFromString(String search) {
      EasingType[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         EasingType each = var1[var3];
         if (each.name().compareToIgnoreCase(search) == 0) {
            return each;
         }
      }

      return null;
   }
}

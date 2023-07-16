package software.bernie.geckolib3.core.manager;

import java.util.HashMap;
import software.bernie.geckolib3.core.IAnimatable;

public class AnimationFactory {
   private final IAnimatable animatable;
   private HashMap<Integer, AnimationData> animationDataMap = new HashMap();

   public AnimationFactory(IAnimatable animatable) {
      this.animatable = animatable;
   }

   public AnimationData getOrCreateAnimationData(Integer uniqueID) {
      if (!this.animationDataMap.containsKey(uniqueID)) {
         AnimationData data = new AnimationData();
         this.animatable.registerControllers(data);
         this.animationDataMap.put(uniqueID, data);
      }

      return (AnimationData)this.animationDataMap.get(uniqueID);
   }
}

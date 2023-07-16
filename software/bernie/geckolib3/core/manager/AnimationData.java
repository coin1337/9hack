package software.bernie.geckolib3.core.manager;

import java.util.HashMap;
import org.apache.commons.lang3.tuple.Pair;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.snapshot.BoneSnapshot;

public class AnimationData {
   private HashMap<String, Pair<IBone, BoneSnapshot>> boneSnapshotCollection = new HashMap();
   private HashMap<String, AnimationController> animationControllers = new HashMap();
   public double tick;
   public boolean isFirstTick = true;
   private double resetTickLength = 1.0D;
   public Double startTick;
   public Object ticker;
   public boolean shouldPlayWhilePaused = false;

   public AnimationController addAnimationController(AnimationController value) {
      return (AnimationController)this.animationControllers.put(value.getName(), value);
   }

   public HashMap<String, Pair<IBone, BoneSnapshot>> getBoneSnapshotCollection() {
      return this.boneSnapshotCollection;
   }

   public void setBoneSnapshotCollection(HashMap<String, Pair<IBone, BoneSnapshot>> boneSnapshotCollection) {
      this.boneSnapshotCollection = boneSnapshotCollection;
   }

   public void clearSnapshotCache() {
      this.boneSnapshotCollection = new HashMap();
   }

   public double getResetSpeed() {
      return this.resetTickLength;
   }

   public void setResetSpeedInTicks(double resetTickLength) {
      this.resetTickLength = resetTickLength < 0.0D ? 0.0D : resetTickLength;
   }

   public HashMap<String, AnimationController> getAnimationControllers() {
      return this.animationControllers;
   }
}

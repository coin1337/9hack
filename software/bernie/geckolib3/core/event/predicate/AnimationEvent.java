package software.bernie.geckolib3.core.event.predicate;

import java.util.List;
import java.util.stream.Collectors;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;

public class AnimationEvent<T extends IAnimatable> {
   private final T animatable;
   public double animationTick;
   private final float limbSwing;
   private final float limbSwingAmount;
   private final float partialTick;
   private final boolean isMoving;
   private final List<Object> extraData;
   protected AnimationController controller;

   public AnimationEvent(T animatable, float limbSwing, float limbSwingAmount, float partialTick, boolean isMoving, List<Object> extraData) {
      this.animatable = animatable;
      this.limbSwing = limbSwing;
      this.limbSwingAmount = limbSwingAmount;
      this.partialTick = partialTick;
      this.isMoving = isMoving;
      this.extraData = extraData;
   }

   public double getAnimationTick() {
      return this.animationTick;
   }

   public T getAnimatable() {
      return this.animatable;
   }

   public float getLimbSwing() {
      return this.limbSwing;
   }

   public float getLimbSwingAmount() {
      return this.limbSwingAmount;
   }

   public float getPartialTick() {
      return this.partialTick;
   }

   public boolean isMoving() {
      return this.isMoving;
   }

   public AnimationController getController() {
      return this.controller;
   }

   public void setController(AnimationController controller) {
      this.controller = controller;
   }

   public List<Object> getExtraData() {
      return this.extraData;
   }

   public <T> List<T> getExtraDataOfType(Class<T> type) {
      return (List)this.extraData.stream().filter((x) -> {
         return type.isAssignableFrom(x.getClass());
      }).map((x) -> {
         return type.cast(x);
      }).collect(Collectors.toList());
   }
}

package software.bernie.geckolib3.core.event;

import software.bernie.geckolib3.core.controller.AnimationController;

public class SoundKeyframeEvent<T> extends KeyframeEvent<T> {
   public final String sound;

   public SoundKeyframeEvent(T entity, double animationTick, String sound, AnimationController controller) {
      super(entity, animationTick, controller);
      this.sound = sound;
   }
}

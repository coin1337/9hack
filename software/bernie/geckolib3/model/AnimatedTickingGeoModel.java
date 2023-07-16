package software.bernie.geckolib3.model;

import java.util.Collections;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.resource.GeckoLibCache;

public abstract class AnimatedTickingGeoModel<T extends IAnimatable & IAnimationTickable> extends AnimatedGeoModel<T> {
   public boolean isInitialized() {
      return !this.getAnimationProcessor().getModelRendererList().isEmpty();
   }

   public void setLivingAnimations(T entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
      AnimationData manager = entity.getFactory().getOrCreateAnimationData(uniqueID);
      if (manager.startTick == null) {
         manager.startTick = (double)((float)((IAnimationTickable)entity).tickTimer() + Minecraft.func_71410_x().func_184121_ak());
      }

      if (!Minecraft.func_71410_x().func_147113_T() || manager.shouldPlayWhilePaused) {
         manager.tick = (double)((float)((IAnimationTickable)entity).tickTimer() + Minecraft.func_71410_x().func_184121_ak());
         double gameTick = manager.tick;
         double deltaTicks = gameTick - this.lastGameTickTime;
         this.seekTime += deltaTicks;
         this.lastGameTickTime = gameTick;
      }

      AnimationEvent predicate;
      if (customPredicate == null) {
         predicate = new AnimationEvent(entity, 0.0F, 0.0F, 0.0F, false, Collections.emptyList());
      } else {
         predicate = customPredicate;
      }

      predicate.animationTick = this.seekTime;
      this.getAnimationProcessor().preAnimationSetup(predicate.getAnimatable(), this.seekTime);
      if (!this.getAnimationProcessor().getModelRendererList().isEmpty()) {
         this.getAnimationProcessor().tickAnimation(entity, uniqueID, this.seekTime, predicate, GeckoLibCache.getInstance().parser, this.shouldCrashOnMissing);
      }

      if (!Minecraft.func_71410_x().func_147113_T() || manager.shouldPlayWhilePaused) {
         this.codeAnimations(entity, uniqueID, customPredicate);
      }

   }

   public void codeAnimations(T entity, Integer uniqueID, AnimationEvent<?> customPredicate) {
   }
}

package com.schnurritv.sexmod.companion.fighter;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIWatchClosest2;

public class LookAtNearbyEntity extends EntityAIWatchClosest2 {
   public boolean shouldLook = true;

   public LookAtNearbyEntity(EntityLiving entitylivingIn, Class<? extends Entity> watchTargetClass, float maxDistance, float chanceIn) {
      super(entitylivingIn, watchTargetClass, maxDistance, chanceIn);
   }

   public void func_75246_d() {
      if (this.shouldLook) {
         super.func_75246_d();
      }

   }
}

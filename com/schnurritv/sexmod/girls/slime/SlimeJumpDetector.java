package com.schnurritv.sexmod.girls.slime;

import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SlimeJumpDetector {
   @SubscribeEvent
   public void landing(LivingFallEvent event) {
      if (event.getEntity() instanceof SlimeEntity) {
         event.setDamageMultiplier(0.0F);
      }

   }
}

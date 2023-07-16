package com.schnurritv.sexmod.events;

import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirl;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoDamageForGirlsWhileHavingTheSex {
   @SubscribeEvent
   public void NoDamageForGirlsWhileHavingTheSex(LivingAttackEvent event) {
      if (event.getEntity() instanceof GirlEntity && event.getSource() != DamageSource.field_76380_i) {
         GirlEntity girl = (GirlEntity)event.getEntity();
         event.setCanceled(girl.playerSheHasSexWith() != null);
      }

      if (event.getEntity() instanceof PlayerGirl && event.getSource() != DamageSource.field_76380_i) {
         event.setCanceled(true);
      }

   }
}

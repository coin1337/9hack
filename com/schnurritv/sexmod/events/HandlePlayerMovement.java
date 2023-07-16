package com.schnurritv.sexmod.events;

import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.gui.Sex.SexUI;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MovementInput;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HandlePlayerMovement {
   private static boolean active = true;
   public static boolean isThrusting = false;
   public static boolean isCumming = false;

   @SubscribeEvent
   public void PreventPlayerFromMoving(InputUpdateEvent event) {
      MovementInput movement = event.getMovementInput();
      isThrusting = movement.field_78899_d;
      isCumming = movement.field_78901_c;
      if (!active) {
         if (movement.field_78899_d) {
            GirlEntity.sendThrust(Minecraft.func_71410_x().field_71439_g.getPersistentID());
         }

         if (movement.field_78901_c && SexUI.cumPercentage >= 1.0D) {
            GirlEntity.sendCum(Minecraft.func_71410_x().field_71439_g.getPersistentID());
         }

         movement.field_187256_d = false;
         movement.field_187255_c = false;
         movement.field_187257_e = false;
         movement.field_187258_f = false;
         movement.field_78899_d = false;
         movement.field_78901_c = false;
         movement.field_192832_b = 0.0F;
         movement.field_78902_a = 0.0F;
         Minecraft.func_71410_x().field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
      }
   }

   public static void setActive(boolean active) {
      HandlePlayerMovement.active = active;
   }

   @SubscribeEvent
   public void PreventPlayerFromTakingAction(MouseEvent event) {
      event.setCanceled(!active);
   }
}

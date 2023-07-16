package com.schnurritv.sexmod.girls.bee;

import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirlRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PlayerBeeRenderer extends PlayerGirlRenderer {
   double x = 0.0D;

   public PlayerBeeRenderer(RenderManager renderManager, AnimatedGeoModel model) {
      super(renderManager, model);
   }

   protected void transformItem(boolean offHand, ItemStack stack) {
      GlStateManager.func_179114_b(offHand ? 290.0F : 90.0F, 1.0F, 0.0F, 0.0F);
   }

   protected void transformShield(boolean offHand, boolean active) {
      if (offHand) {
         GlStateManager.func_179114_b(180.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179109_b(0.0F, -0.14F, -0.17F);
         if (active) {
            GlStateManager.func_179114_b(90.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.func_179137_b(0.067D, 0.0D, 0.0D);
         }
      } else if (active) {
         GlStateManager.func_179114_b(-90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179114_b(-90.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.func_179109_b(0.0F, 0.165F, 0.0F);
      }

   }
}

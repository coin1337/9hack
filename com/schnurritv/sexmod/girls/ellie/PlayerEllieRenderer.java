package com.schnurritv.sexmod.girls.ellie;

import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirlRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PlayerEllieRenderer extends PlayerGirlRenderer {
   public PlayerEllieRenderer(RenderManager renderManager, AnimatedGeoModel model) {
      super(renderManager, model);
   }

   protected void transformItem(boolean offHand, ItemStack stack) {
      super.transformItem(offHand, stack);
      switch(stack.func_77973_b().func_77661_b(stack)) {
      default:
         GlStateManager.func_179114_b(offHand ? 90.0F : 180.0F, 1.0F, 0.0F, 0.0F);
         if (offHand) {
            GlStateManager.func_179109_b(0.0F, 0.289F, -0.1F);
         }
      case BLOCK:
      case BOW:
      }
   }

   protected void transformBow(boolean offHand) {
      GlStateManager.func_179114_b(offHand ? 90.0F : 180.0F, 1.0F, 0.0F, 0.0F);
      if (offHand) {
         GlStateManager.func_179137_b(0.2D, -0.2D, 0.0D);
      }

   }

   protected void transformShield(boolean offHand, boolean active) {
      if (offHand) {
         GlStateManager.func_179114_b(180.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
         if (active) {
            GlStateManager.func_179114_b(-90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.func_179114_b(50.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.func_179114_b(-20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.func_179109_b(0.0F, 0.0F, 0.228F);
         }
      } else {
         GlStateManager.func_179109_b(0.0F, 0.282F, 0.141F);
         if (active) {
            GlStateManager.func_179137_b(0.165D, -0.44999998807907104D, 0.0D);
            GlStateManager.func_179114_b(-90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.func_179114_b(-90.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.func_179114_b(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.func_179114_b(-27.0F, 0.0F, 1.0F, 0.0F);
         }
      }

   }
}

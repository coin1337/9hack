package com.schnurritv.sexmod.girls.slime;

import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirlRenderer;
import javax.vecmath.Vector3f;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PlayerSlimeRenderer extends PlayerGirlRenderer {
   Vector3f scaleBody = new Vector3f(0.0F, 0.0F, 0.0F);
   Vector3f posBody = new Vector3f(0.0F, 0.0F, 0.0F);
   Vector3f rotBody = new Vector3f(0.0F, 0.0F, 0.0F);
   Vector3f rotTorso = new Vector3f(0.0F, 0.0F, 0.0F);
   Vector3f rotBoobs = new Vector3f(0.0F, 0.0F, 0.0F);
   Vector3f rotUpperBody = new Vector3f(0.0F, 0.0F, 0.0F);
   Vector3f rotHead = new Vector3f(0.0F, 0.0F, 0.0F);

   public PlayerSlimeRenderer(RenderManager renderManager, AnimatedGeoModel model) {
      super(renderManager, model);
   }

   protected void setUpSpecialBones(String boneName, GeoBone bone) {
      if ("slime".equals(boneName)) {
         this.rotBody = new Vector3f(bone.getRotationX(), bone.getRotationY(), bone.getRotationZ());
         this.scaleBody = new Vector3f(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
         this.posBody = new Vector3f(bone.getPositionX(), bone.getPositionY(), bone.getPositionZ());
      }

      if ("upperBody".equals(boneName)) {
         this.rotUpperBody = new Vector3f(bone.getRotationX(), bone.getRotationY(), bone.getRotationZ());
      }

      if ("torso".equals(boneName)) {
         this.rotTorso = new Vector3f(bone.getRotationX(), bone.getRotationY(), bone.getRotationZ());
      }

      if ("head".equals(boneName)) {
         this.rotHead = new Vector3f(bone.getRotationX(), bone.getRotationY(), bone.getRotationZ());
      }

      if ("boobs".equals(boneName)) {
         this.rotBoobs = new Vector3f(bone.getRotationX(), bone.getRotationY(), bone.getRotationZ());
      }

      if ("figure".equals(boneName)) {
         bone.setRotationX(this.rotBody.x);
         bone.setRotationY(this.rotBody.y);
         bone.setRotationZ(this.rotBody.z);
         bone.setScaleX(this.scaleBody.x);
         bone.setScaleY(this.scaleBody.y);
         bone.setScaleZ(this.scaleBody.z);
         bone.setPositionX(this.posBody.x);
         bone.setPositionY(this.posBody.y);
         bone.setPositionZ(this.posBody.z);
      }

      if ("dress".equals(boneName)) {
         bone.setRotationX(this.rotUpperBody.x);
         bone.setRotationY(this.rotUpperBody.y);
         bone.setRotationZ(this.rotUpperBody.z);
      }

      if ("hat".equals(boneName)) {
         bone.setRotationX(this.rotHead.x);
         bone.setRotationY(this.rotHead.y);
         bone.setRotationZ(this.rotHead.z);
      }

      if ("boobsSlime".equals(boneName)) {
         bone.setRotationX(this.rotBoobs.x);
         bone.setRotationY(this.rotBoobs.y);
         bone.setRotationZ(this.rotBoobs.z);
      }

   }

   protected void transformItem(boolean offHand, ItemStack stack) {
      if (offHand) {
         GlStateManager.func_179114_b(200.0F, 1.0F, 0.0F, 0.0F);
      } else {
         GlStateManager.func_179114_b(180.0F, 1.0F, 0.0F, 0.0F);
      }

   }

   protected void transformShield(boolean offHand, boolean active) {
      if (offHand) {
         GlStateManager.func_179114_b(180.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
         if (active) {
            GlStateManager.func_179114_b(-90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.func_179114_b(35.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.func_179114_b(-20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.func_179109_b(0.0F, 0.0F, 0.228F);
         }
      } else {
         GlStateManager.func_179114_b(30.34F, 1.0F, 0.0F, 0.0F);
         if (active) {
            GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.func_179114_b(-90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.func_179109_b(0.0F, -0.068F, 0.18F);
         }
      }

   }
}

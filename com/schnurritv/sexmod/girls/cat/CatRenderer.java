package com.schnurritv.sexmod.girls.cat;

import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.base.GirlRenderer;
import com.schnurritv.sexmod.util.MatrixHelper;
import com.schnurritv.sexmod.util.PenisMath;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class CatRenderer extends GirlRenderer {
   float headXRot = 0.0F;

   public CatRenderer(RenderManager renderManager, AnimatedGeoModel model, double leashHeightOffset) {
      super(renderManager, model, leashHeightOffset);
   }

   protected ItemStack changeWeaponBeforeRendering(@Nullable ItemStack selectedWeapon) {
      switch(this.girl.currentAction()) {
      case FISHING_IDLE:
      case FISHING_START:
         ItemStack stack = (ItemStack)this.girl.func_184212_Q().func_187225_a(CatEntity.FISHING_ROD);
         this.girl.func_184611_a(EnumHand.MAIN_HAND, stack);
         return stack;
      default:
         return selectedWeapon;
      }
   }

   boolean shouldBeAtTarget() {
      return (Boolean)this.girl.func_184212_Q().func_187225_a(GirlEntity.SHOULD_BE_AT_TARGET);
   }

   protected void setUpSpecialBones(String boneName, GeoBone bone) {
      if (!Minecraft.func_71410_x().func_147113_T()) {
         byte var4 = -1;
         switch(boneName.hashCode()) {
         case -1870254701:
            if (boneName.equals("sideHairL")) {
               var4 = 3;
            }
            break;
         case -1870254695:
            if (boneName.equals("sideHairR")) {
               var4 = 2;
            }
            break;
         case -1548738978:
            if (boneName.equals("offhand")) {
               var4 = 6;
            }
            break;
         case -345841663:
            if (boneName.equals("frontHairL")) {
               var4 = 4;
            }
            break;
         case -345841657:
            if (boneName.equals("frontHairR")) {
               var4 = 5;
            }
            break;
         case 3198432:
            if (boneName.equals("head")) {
               var4 = 0;
            }
            break;
         case 2120576361:
            if (boneName.equals("backHair")) {
               var4 = 1;
            }
         }

         double percentage;
         float distance;
         switch(var4) {
         case 0:
            this.headXRot = bone.getRotationX();
            break;
         case 1:
            if (!this.shouldBeAtTarget()) {
               percentage = (double)this.headXRot / PenisMath.degreesToGeckoRot(45.0D);
               distance = (float)PenisMath.Lerp(0.0D, 0.75D, percentage);
               bone.setPositionZ(bone.getPositionZ() + distance);
               bone.setPositionY(bone.getPositionY() + distance);
               bone.setRotationX(bone.getRotationX() - this.headXRot);
            }
            break;
         case 2:
         case 3:
            if (this.shouldBeAtTarget()) {
               break;
            }

            percentage = (double)this.headXRot / PenisMath.degreesToGeckoRot(45.0D);
            distance = (float)PenisMath.Lerp(0.0D, 1.2999999523162842D, percentage);
            bone.setPositionZ(bone.getPositionZ() - distance);
            bone.setPositionY(bone.getPositionY() + distance);
         case 4:
         case 5:
            if (!this.shouldBeAtTarget()) {
               bone.setRotationX(bone.getRotationX() - this.headXRot);
            }
            break;
         case 6:
            CatEntity cat = (CatEntity)this.girl;
            ItemStack stack = (ItemStack)this.girl.func_184212_Q().func_187225_a(CatEntity.CAUGHT_ITEM);
            if (!stack.equals(ItemStack.field_190927_a) && cat.throwBackPercentage == 1.0F) {
               GlStateManager.func_179094_E();
               Tessellator.func_178181_a().func_78381_a();
               MatrixHelper.multiplyMatrix(IGeoRenderer.MATRIX_STACK, bone);
               GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
               GlStateManager.func_179152_a(cat.fishSizePercentage, cat.fishSizePercentage, cat.fishSizePercentage);
               Minecraft.func_71410_x().func_175597_ag().func_178099_a(this.girl, stack, TransformType.THIRD_PERSON_RIGHT_HAND);
               GirlRenderer.currentBuilder.func_181668_a(7, DefaultVertexFormats.field_181712_l);
               this.func_110776_a((ResourceLocation)Objects.requireNonNull(this.getEntityTexture(this.girl)));
               GlStateManager.func_179121_F();
            }
         }

      }
   }
}

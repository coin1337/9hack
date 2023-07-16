package com.schnurritv.sexmod.girls.base;

import com.daripher.sexmod.client.util.FakeWorld;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.AnimationProcessor;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public abstract class GirlModel<T extends GirlEntity> extends AnimatedGeoModel<T> implements ModelBones {
   public static boolean shouldUseOtherSkins = true;
   protected ResourceLocation[] models = this.getModels();

   protected GirlModel() {
   }

   protected abstract ResourceLocation[] getModels();

   public abstract ResourceLocation getSkin();

   public abstract ResourceLocation getAnimationFile();

   public ResourceLocation getAnimationFileLocation(GirlEntity animatable) {
      return this.getAnimationFile();
   }

   public ResourceLocation getModelLocation(GirlEntity girl) {
      if (girl.field_70170_p instanceof FakeWorld) {
         return this.models[0];
      } else if ((Integer)girl.func_184212_Q().func_187225_a(GirlEntity.CURRENT_MODEL) > this.models.length) {
         System.out.println("Girl doesn't have an outfit Nr." + girl.func_184212_Q().func_187225_a(GirlEntity.CURRENT_MODEL) + " so im just making her nude lol");
         return this.models[0];
      } else {
         return this.models[(Integer)girl.func_184212_Q().func_187225_a(GirlEntity.CURRENT_MODEL)];
      }
   }

   public ResourceLocation getTextureLocation(GirlEntity girl) {
      return this.getSkin();
   }

   public void setMolangQueries(IAnimatable animatable, double currentTick) {
      if (Minecraft.func_71410_x().field_71441_e != null) {
         super.setMolangQueries(animatable, currentTick);
      }

   }

   public void setLivingAnimations(T girl, Integer uniqueID, AnimationEvent customPredicate) {
      try {
         super.setLivingAnimations((IAnimatable)girl, uniqueID, customPredicate);
      } catch (Exception var7) {
      }

      AnimationProcessor processor = this.getAnimationProcessor();

      try {
         this.renderPlayer(girl, processor);
      } catch (IOException var6) {
         var6.printStackTrace();
      }

      if (!(girl.field_70170_p instanceof FakeWorld)) {
         if ((Boolean)girl.func_184212_Q().func_187225_a(GirlEntity.SHOULD_BE_AT_TARGET)) {
            girl.func_180426_a(girl.targetPos().field_72450_a, girl.targetPos().field_72448_b, girl.targetPos().field_72449_c, girl.targetYaw(), 0.0F, 3, true);
         }

         if (girl.actionController != null) {
            girl.actionController.transitionLengthTicks = !(girl.field_70170_p instanceof FakeWorld) && girl.currentAction() != null ? (double)girl.currentAction().transitionTick : 5.0D;
         }

         this.setUpHead(girl, processor, customPredicate);
         if (girl instanceof Fighter) {
            if ((Integer)girl.field_70180_af.func_187225_a(GirlEntity.CURRENT_MODEL) == 0) {
               this.disableArmor(processor);
            } else {
               this.manageArmor(processor, (ItemStack)girl.field_70180_af.func_187225_a(Fighter.HELMET), (ItemStack)girl.field_70180_af.func_187225_a(Fighter.CHEST_PLATE), (ItemStack)girl.field_70180_af.func_187225_a(Fighter.PANTS), (ItemStack)girl.field_70180_af.func_187225_a(Fighter.SHOES));
            }
         }

      }
   }

   void manageArmor(AnimationProcessor<T> processor, ItemStack helmet, ItemStack chest, ItemStack legs, ItemStack feet) {
      this.renderHelmetOrHead(processor, !helmet.func_190926_b());
      this.renderChestPlateOrTorso(processor, !chest.func_190926_b());
      this.renderPantsOrLegs(processor, !legs.func_190926_b());
      this.renderShoesOrFeet(processor, !feet.func_190926_b());
   }

   protected void disableArmor(AnimationProcessor<T> processor) {
      this.renderHelmetOrHead(processor, false);
      this.renderChestPlateOrTorso(processor, false);
      this.renderPantsOrLegs(processor, false);
      this.renderShoesOrFeet(processor, false);
   }

   void renderHelmetOrHead(AnimationProcessor processor, boolean renderHelmet) {
      this.render(this.getHelmetBones(), renderHelmet, processor);
      this.render(this.getHeadBones(), !renderHelmet, processor);
   }

   void renderChestPlateOrTorso(AnimationProcessor<T> processor, boolean renderBreastplate) {
      this.render(this.getChestPlateBones(), renderBreastplate, processor);
      this.render(this.getTorsoBones(), !renderBreastplate, processor);
   }

   void renderPantsOrLegs(AnimationProcessor<T> processor, boolean renderPants) {
      this.render(this.getPantsBones(), renderPants, processor);
      this.render(this.getLegsBones(), !renderPants, processor);
   }

   void renderShoesOrFeet(AnimationProcessor<T> processor, boolean renderShoes) {
      this.render(this.getShoesBones(), renderShoes, processor);
      this.render(this.getFeetBones(), !renderShoes, processor);
   }

   void render(String[] boneNames, boolean render, AnimationProcessor<T> processor) {
      String[] var4 = boneNames;
      int var5 = boneNames.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String boneName = var4[var6];
         this.render(boneName, render, processor);
      }

   }

   void render(String boneName, boolean render, AnimationProcessor<T> processor) {
      if (processor.getBone(boneName) != null) {
         processor.getBone(boneName).setHidden(!render);
      }
   }

   void renderPlayer(T girl, AnimationProcessor<T> processor) throws IOException {
      try {
         processor.getBone("steve").setHidden(!girl.currentAction().hasPlayer);
      } catch (NullPointerException var4) {
      }

   }

   protected void setUpHead(T girl, AnimationProcessor<T> processor, AnimationEvent predicate) {
      if (!(girl.field_70170_p instanceof FakeWorld) && (girl.currentAction() == GirlEntity.Action.NULL || girl.currentAction() == GirlEntity.Action.ATTACK || girl.currentAction() == GirlEntity.Action.BOW)) {
         EntityModelData extraData = (EntityModelData)predicate.getExtraDataOfType(EntityModelData.class).get(0);
         IBone neck = processor.getBone("neck");
         neck.setRotationY(extraData.netHeadYaw * 0.5F * 0.017453292F);
         IBone head = processor.getBone("head");
         head.setRotationY(extraData.netHeadYaw * 0.017453292F);
         head.setRotationX(extraData.headPitch * 0.017453292F);
         IBone body = processor.getBone("body") == null ? processor.getBone("dd") : processor.getBone("body");
         body.setRotationY(0.0F);
      }

   }
}

package com.schnurritv.sexmod.girls.bee;

import com.daripher.sexmod.client.util.FakeWorld;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.base.GirlModel;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.AnimationProcessor;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class BeeModel extends GirlModel {
   protected ResourceLocation[] getModels() {
      return new ResourceLocation[]{new ResourceLocation("sexmod", "geo/bee/bee.geo.json"), new ResourceLocation("sexmod", "geo/bee/armored.geo.json"), new ResourceLocation("sexmod", "geo/bee/bee.geo.json")};
   }

   public ResourceLocation getModelLocation(GirlEntity girl) {
      if (girl.field_70170_p instanceof FakeWorld) {
         return this.models[0];
      } else if ((Integer)girl.func_184212_Q().func_187225_a(GirlEntity.CURRENT_MODEL) > this.models.length) {
         System.out.println("Girl doesn't have an outfit Nr." + girl.func_184212_Q().func_187225_a(GirlEntity.CURRENT_MODEL) + " so im just making her nude lol");
         return this.models[0];
      } else if (girl instanceof PlayerBee) {
         return this.models[(Integer)girl.func_184212_Q().func_187225_a(GirlEntity.CURRENT_MODEL)];
      } else {
         return (Integer)girl.func_184212_Q().func_187225_a(GirlEntity.CURRENT_MODEL) == 1 ? this.models[2] : this.models[0];
      }
   }

   public ResourceLocation getSkin() {
      return new ResourceLocation("sexmod", "textures/entity/bee/bee.png");
   }

   public ResourceLocation getAnimationFile() {
      return new ResourceLocation("sexmod", "animations/bee/bee.animation.json");
   }

   public void setLivingAnimations(GirlEntity girl, Integer uniqueID, AnimationEvent customPredicate) {
      super.setLivingAnimations(girl, uniqueID, customPredicate);
      if (!(girl.field_70170_p instanceof FakeWorld)) {
         AnimationProcessor processor = this.getAnimationProcessor();
         if (girl instanceof BeeEntity) {
            processor.getBone("chest").setHidden(girl.movementController.getCurrentAnimation() == null || !girl.movementController.getCurrentAnimation().animationName.contains("chest"));
         } else {
            IBone chest = processor.getBone("chest");
            if (chest == null) {
               return;
            }

            chest.setHidden(girl.movementController.getCurrentAnimation() == null || !girl.movementController.getCurrentAnimation().animationName.contains("chest"));
         }

      }
   }

   protected void setUpHead(GirlEntity girl, AnimationProcessor processor, AnimationEvent predicate) {
      if (!(girl.field_70170_p instanceof FakeWorld) && (girl.currentAction() == GirlEntity.Action.NULL || girl.currentAction() == GirlEntity.Action.ATTACK || girl.currentAction() == GirlEntity.Action.BOW)) {
         EntityModelData extraData = (EntityModelData)predicate.getExtraDataOfType(EntityModelData.class).get(0);
         IBone neck = processor.getBone("neck");
         neck.setRotationY(extraData.netHeadYaw * 0.5F * 0.017453292F);
         IBone head = processor.getBone("head");
         head.setRotationY(extraData.netHeadYaw * 0.017453292F);
         head.setRotationX(1.0F + extraData.headPitch * 0.017453292F);
         IBone body = processor.getBone("body") == null ? processor.getBone("dd") : processor.getBone("body");
         body.setRotationY(0.0F);
      }

   }

   public String[] getHelmetBones() {
      return new String[]{"armorHelmet"};
   }

   public String[] getHeadBones() {
      return new String[]{"feeler", "feeler2", "brow", "brow2", "brow3", "brow4"};
   }

   public String[] getChestPlateBones() {
      return new String[]{"armorShoulderR", "armorShoulderL", "armorChest", "armorBoobs"};
   }

   public String[] getTorsoBones() {
      return new String[]{"boobsFlesh", "upperBodyL", "upperBodyR"};
   }

   public String[] getPantsBones() {
      return new String[]{"armorBootyR", "armorBootyL", "armorPantsLowL", "armorPantsLowR", "armorPantsLowR", "armorPantsUpR", "armorPantsUpL", "armorHip"};
   }

   public String[] getLegsBones() {
      return new String[]{"fleshL", "fleshR", "vagina", "curvesL", "curvesR", "kneeL", "kneeR"};
   }

   public String[] getShoesBones() {
      return new String[]{"armorShoesL", "armorShoesR"};
   }
}

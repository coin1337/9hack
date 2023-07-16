package com.schnurritv.sexmod.girls.slime;

import com.daripher.sexmod.client.util.FakeWorld;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.base.GirlModel;
import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirl;
import java.util.Arrays;
import javax.vecmath.Vector3f;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.AnimationProcessor;
import software.bernie.geckolib3.core.processor.IBone;

public class SlimeModel extends GirlModel {
   GirlEntity.Action[] actionsWithSlime;

   public SlimeModel() {
      this.actionsWithSlime = new GirlEntity.Action[]{GirlEntity.Action.STARTDOGGY, GirlEntity.Action.DOGGYCUM, GirlEntity.Action.DOGGYSLOW, GirlEntity.Action.DOGGYFAST, GirlEntity.Action.DOGGYCUM, GirlEntity.Action.DOGGYSTART, GirlEntity.Action.WAITDOGGY};
   }

   protected ResourceLocation[] getModels() {
      return new ResourceLocation[]{new ResourceLocation("sexmod", "geo/slime/nude.geo.json"), new ResourceLocation("sexmod", "geo/slime/armored.geo.json"), new ResourceLocation("sexmod", "geo/slime/dressed.geo.json")};
   }

   public ResourceLocation getModelLocation(GirlEntity girl) {
      if (girl.field_70170_p instanceof FakeWorld) {
         return this.models[0];
      } else if ((Integer)girl.func_184212_Q().func_187225_a(GirlEntity.CURRENT_MODEL) > this.models.length) {
         System.out.println("Girl doesn't have an outfit Nr." + girl.func_184212_Q().func_187225_a(GirlEntity.CURRENT_MODEL) + " so im just making her nude lol");
         return this.models[0];
      } else if (girl instanceof PlayerSlime) {
         return this.models[(Integer)girl.func_184212_Q().func_187225_a(GirlEntity.CURRENT_MODEL)];
      } else {
         return (Integer)girl.func_184212_Q().func_187225_a(GirlEntity.CURRENT_MODEL) == 1 ? this.models[2] : this.models[0];
      }
   }

   public ResourceLocation getSkin() {
      return new ResourceLocation("sexmod", "textures/entity/slime/slime.png");
   }

   public ResourceLocation getAnimationFile() {
      return new ResourceLocation("sexmod", "animations/slime/slime.animation.json");
   }

   public void setLivingAnimations(GirlEntity girl, Integer uniqueID, AnimationEvent customPredicate) {
      super.setLivingAnimations(girl, uniqueID, customPredicate);
      AnimationProcessor processor = this.getAnimationProcessor();
      if (!(girl.field_70170_p instanceof FakeWorld) && processor.getBone("bedSlime") != null && processor.getBone("bedSlimeLayer") != null) {
         processor.getBone("bedSlime").setHidden(!Arrays.asList(this.actionsWithSlime).contains(girl.currentAction()));
         processor.getBone("bedSlimeLayer").setHidden(!Arrays.asList(this.actionsWithSlime).contains(girl.currentAction()));
      }

      if (!(girl instanceof PlayerGirl)) {
         this.copyData(new String[]{"head"}, "hat");
         if (!(girl.field_70170_p instanceof FakeWorld)) {
            if (girl instanceof SlimeEntity) {
               if (((SlimeEntity)girl).isPregnant && girl.field_70173_aa % 10 == 0) {
                  GirlEntity.spawnParticleOnGirl(EnumParticleTypes.SPELL_WITCH, girl);
               } else if (((SlimeEntity)girl).getHornyLevel() == 2 && girl.field_70173_aa % 20 == 0) {
                  GirlEntity.spawnParticleOnGirl(EnumParticleTypes.HEART, girl);
               }

            }
         }
      }
   }

   void copyData(String[] froms, String to) {
      AnimationProcessor processor = this.getAnimationProcessor();
      IBone toBone = processor.getBone(to);
      IBone[] fromBones = new IBone[froms.length];

      for(int i = 0; i < fromBones.length; ++i) {
         fromBones[i] = processor.getBone(froms[i]);
      }

      Vector3f totalRot = new Vector3f(0.0F, 0.0F, 0.0F);
      Vector3f totalPos = new Vector3f(0.0F, 0.0F, 0.0F);
      IBone[] var8 = fromBones;
      int var9 = fromBones.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         IBone bone = var8[var10];
         totalRot.add(new Vector3f(bone.getRotationX(), bone.getRotationY(), bone.getRotationZ()));
         totalPos.add(new Vector3f(bone.getPositionX(), bone.getPositionY(), bone.getPositionZ()));
      }

      toBone.setRotationX(totalRot.x);
      toBone.setRotationY(totalRot.y);
      toBone.setRotationZ(totalRot.z);
      toBone.setPositionX(totalPos.x);
      toBone.setPositionY(totalPos.y);
      toBone.setPositionZ(totalPos.z);
      toBone.setPositionZ(totalPos.z);
   }

   public String[] getHelmetBones() {
      return new String[]{"armorHelmet"};
   }

   public String[] getHeadBones() {
      return new String[]{"bigblob"};
   }

   public String[] getChestPlateBones() {
      return new String[]{"armorShoulderR", "armorShoulderL", "armorChest", "armorBoobs"};
   }

   public String[] getTorsoBones() {
      return new String[]{"boobsFlesh", "upperBodyL", "upperBodyR", "cloth"};
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

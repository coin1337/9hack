package com.schnurritv.sexmod.girls.ellie;

import com.daripher.sexmod.client.util.FakeWorld;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.base.GirlModel;
import com.schnurritv.sexmod.util.PenisMath;
import java.util.HashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;

public class EllieModel extends GirlModel {
   HashMap<Integer, float[]> valuesForYaw = new HashMap<Integer, float[]>() {
      {
         this.put(0, new float[]{0.0F, -1.2F, 1.2F});
         this.put(-90, new float[]{2.0F, -71.56F, -68.0F});
         this.put(90, new float[]{-2.0F, 68.0F, 70.5F});
      }
   };

   public EllieModel() {
      this.models = this.getModels();
   }

   protected ResourceLocation[] getModels() {
      return new ResourceLocation[]{new ResourceLocation("sexmod", "geo/ellie/nude.geo.json"), new ResourceLocation("sexmod", "geo/ellie/dressed.geo.json")};
   }

   public ResourceLocation getSkin() {
      return new ResourceLocation("sexmod", "textures/entity/ellie/ellie.png");
   }

   public ResourceLocation getAnimationFile() {
      return new ResourceLocation("sexmod", "animations/ellie/ellie.animation.json");
   }

   public void setLivingAnimations(GirlEntity girl, Integer uniqueID, AnimationEvent customPredicate) {
      super.setLivingAnimations(girl, uniqueID, customPredicate);
      if (!(girl.field_70170_p instanceof FakeWorld) && girl.currentAction() == GirlEntity.Action.SITDOWNIDLE) {
         EntityPlayer player = girl.field_70170_p.func_72890_a(girl, 15.0D);
         if (player != null) {
            IBone head = this.getAnimationProcessor().getBone("head");
            Vec3d distance = girl.func_174791_d().func_178788_d(player.func_174791_d());
            int correspondingYaw = Math.round(girl.targetYaw());
            float yaw;
            float pitch;
            if (correspondingYaw == 180) {
               yaw = (float)Math.atan2(distance.field_72450_a, distance.field_72449_c) * 1.2F;
               if (yaw > 0.0F) {
                  yaw = Math.max(1.5F, Math.min(3.14F, yaw));
               } else {
                  yaw = Math.max(-3.14F, Math.min(-1.5F, yaw));
               }

               if (yaw != 1.5F && yaw != 3.14F && yaw != -3.14F && yaw != -1.5F) {
                  yaw += 3.0F;
               } else {
                  yaw = 0.0F;
               }
            } else {
               pitch = ((float[])this.valuesForYaw.get(correspondingYaw))[1];
               float maxYaw = ((float[])this.valuesForYaw.get(correspondingYaw))[2];
               yaw = ((float)(Math.atan2(distance.field_72450_a, distance.field_72449_c) + (double)((float[])this.valuesForYaw.get(correspondingYaw))[0]) + girl.targetYaw()) * 0.8F;
               yaw = PenisMath.clamp(yaw, pitch, maxYaw);
               if (yaw == pitch || yaw == maxYaw) {
                  yaw = 0.0F;
               }
            }

            pitch = yaw == 0.0F ? 0.0F : PenisMath.clamp((float)((player.field_70163_u - girl.field_70163_u) * 0.5D), -0.75F, 0.75F);
            head.setRotationY(yaw);
            head.setRotationX(pitch);
         }
      }

   }

   public String[] getHelmetBones() {
      return new String[]{"armorHelmet"};
   }

   public String[] getHeadBones() {
      return new String[]{"headband"};
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
      return new String[]{"fleshL", "fleshR", "vagina", "hotpants", "slip", "curvesL", "curvesR", "kneeL", "kneeR"};
   }

   public String[] getShoesBones() {
      return new String[]{"armorShoesL", "armorShoesR"};
   }
}

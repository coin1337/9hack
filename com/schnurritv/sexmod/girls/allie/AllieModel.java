package com.schnurritv.sexmod.girls.allie;

import com.daripher.sexmod.client.util.FakeWorld;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.base.GirlModel;
import net.minecraft.util.ResourceLocation;

public class AllieModel extends GirlModel {
   protected ResourceLocation[] getModels() {
      return new ResourceLocation[]{new ResourceLocation("sexmod", "geo/allie/allie.geo.json"), new ResourceLocation("sexmod", "geo/allie/armored.geo.json"), new ResourceLocation("sexmod", "geo/allie/allie.geo.json")};
   }

   public ResourceLocation getModelLocation(GirlEntity girl) {
      if (girl.field_70170_p instanceof FakeWorld) {
         return this.models[0];
      } else if ((Integer)girl.func_184212_Q().func_187225_a(GirlEntity.CURRENT_MODEL) > this.models.length) {
         System.out.println("Girl doesn't have an outfit Nr." + girl.func_184212_Q().func_187225_a(GirlEntity.CURRENT_MODEL) + " so im just making her nude lol");
         return this.models[0];
      } else if (girl instanceof PlayerAllie) {
         return this.models[(Integer)girl.func_184212_Q().func_187225_a(GirlEntity.CURRENT_MODEL)];
      } else {
         return (Integer)girl.func_184212_Q().func_187225_a(GirlEntity.CURRENT_MODEL) == 1 ? this.models[2] : this.models[0];
      }
   }

   public ResourceLocation getSkin() {
      return new ResourceLocation("sexmod", "textures/entity/allie/allie.png");
   }

   public ResourceLocation getAnimationFile() {
      return new ResourceLocation("sexmod", "animations/allie/allie.animation.json");
   }

   public String[] getHelmetBones() {
      return new String[]{"armorHelmet"};
   }

   public String[] getChestPlateBones() {
      return new String[]{"armorShoulderR", "armorShoulderL", "armorChest", "armorBoobs"};
   }

   public String[] getTorsoBones() {
      return new String[]{"boobsFlesh", "clothes", "clothesR", "clothesL"};
   }
}

package com.schnurritv.sexmod.girls.bia;

import com.schnurritv.sexmod.girls.base.GirlModel;
import net.minecraft.util.ResourceLocation;

public class BiaModel extends GirlModel {
   public BiaModel() {
      this.models = this.getModels();
   }

   protected ResourceLocation[] getModels() {
      return new ResourceLocation[]{new ResourceLocation("sexmod", "geo/bia/bianude.geo.json"), new ResourceLocation("sexmod", "geo/bia/biadressed.geo.json")};
   }

   public ResourceLocation getSkin() {
      return new ResourceLocation("sexmod", "textures/entity/bia/bia.png");
   }

   public ResourceLocation getAnimationFile() {
      return new ResourceLocation("sexmod", "animations/bia/bia.animation.json");
   }

   public String[] getHelmetBones() {
      return new String[]{"armorHelmet"};
   }

   public String[] getHeadBones() {
      return new String[]{"leaf7", "leaf8"};
   }

   public String[] getChestPlateBones() {
      return new String[]{"armorChest", "armorBoobs", "armorShoulderR", "armorShoulderL"};
   }

   public String[] getTorsoBones() {
      return new String[]{"bra", "upperBodyR", "upperBodyL"};
   }

   public String[] getPantsBones() {
      return new String[]{"armorBootyR", "armorBootyL", "armorPantsLowL", "armorPantsLowR", "armorPantsLowR", "armorPantsUpR", "armorPantsUpL", "armorHip"};
   }

   public String[] getLegsBones() {
      return new String[]{"slip", "fleshL", "fleshR", "vagina", "curvesL", "curvesR", "kneeL", "kneeR"};
   }

   public String[] getShoesBones() {
      return new String[]{"armorShoesL", "armorShoesR"};
   }
}

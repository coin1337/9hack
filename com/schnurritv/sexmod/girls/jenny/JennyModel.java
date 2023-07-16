package com.schnurritv.sexmod.girls.jenny;

import com.schnurritv.sexmod.girls.base.GirlModel;
import net.minecraft.util.ResourceLocation;

public class JennyModel extends GirlModel {
   public static JennyModel INSTANCE;

   public JennyModel() {
      INSTANCE = this;
   }

   protected ResourceLocation[] getModels() {
      return new ResourceLocation[]{new ResourceLocation("sexmod", "geo/jenny/jennynude.geo.json"), new ResourceLocation("sexmod", "geo/jenny/jennydressed.geo.json")};
   }

   public ResourceLocation getSkin() {
      return new ResourceLocation("sexmod", "textures/entity/jenny/jenny.png");
   }

   public ResourceLocation getAnimationFile() {
      return new ResourceLocation("sexmod", "animations/jenny/jenny.animation.json");
   }

   public String[] getHelmetBones() {
      return new String[]{"armorHelmet"};
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

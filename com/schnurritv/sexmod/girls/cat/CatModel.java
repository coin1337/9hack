package com.schnurritv.sexmod.girls.cat;

import com.schnurritv.sexmod.girls.base.GirlModel;
import net.minecraft.util.ResourceLocation;

public class CatModel extends GirlModel {
   protected ResourceLocation[] getModels() {
      return new ResourceLocation[]{new ResourceLocation("sexmod", "geo/cat/cat.geo.json"), new ResourceLocation("sexmod", "geo/cat/cat.geo.json")};
   }

   public ResourceLocation getSkin() {
      return new ResourceLocation("sexmod", "textures/entity/cat/cat.png");
   }

   public ResourceLocation getAnimationFile() {
      return new ResourceLocation("sexmod", "animations/cat/cat.animation.json");
   }
}

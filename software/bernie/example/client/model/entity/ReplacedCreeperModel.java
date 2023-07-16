package software.bernie.example.client.model.entity;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ReplacedCreeperModel extends AnimatedGeoModel {
   public ResourceLocation getModelLocation(Object object) {
      return new ResourceLocation("geckolib3", "geo/creeper.geo.json");
   }

   public ResourceLocation getTextureLocation(Object object) {
      return new ResourceLocation("geckolib3", "textures/model/entity/creeper.png");
   }

   public ResourceLocation getAnimationFileLocation(Object animatable) {
      return new ResourceLocation("geckolib3", "animations/creeper.animation.json");
   }
}

package software.bernie.example.client.model.entity;

import net.minecraft.util.ResourceLocation;
import software.bernie.example.entity.GeoExampleEntityLayer;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;

public class LEModel extends AnimatedTickingGeoModel<GeoExampleEntityLayer> {
   public ResourceLocation getModelLocation(GeoExampleEntityLayer object) {
      return new ResourceLocation("geckolib3", "geo/le.geo.json");
   }

   public ResourceLocation getTextureLocation(GeoExampleEntityLayer object) {
      return new ResourceLocation("geckolib3", "textures/model/entity/le.png");
   }

   public ResourceLocation getAnimationFileLocation(GeoExampleEntityLayer animatable) {
      return new ResourceLocation("geckolib3", "animations/le.animations.json");
   }
}

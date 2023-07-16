package software.bernie.example.client.model.tile;

import net.minecraft.util.ResourceLocation;
import software.bernie.example.block.tile.BotariumTileEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BotariumModel extends AnimatedGeoModel<BotariumTileEntity> {
   public ResourceLocation getAnimationFileLocation(BotariumTileEntity entity) {
      return new ResourceLocation("geckolib3", "animations/botarium.animation.json");
   }

   public ResourceLocation getModelLocation(BotariumTileEntity animatable) {
      return new ResourceLocation("geckolib3", "geo/botarium.geo.json");
   }

   public ResourceLocation getTextureLocation(BotariumTileEntity entity) {
      return new ResourceLocation("geckolib3", "textures/block/botarium.png");
   }
}

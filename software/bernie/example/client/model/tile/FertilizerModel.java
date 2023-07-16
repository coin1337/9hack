package software.bernie.example.client.model.tile;

import net.minecraft.util.ResourceLocation;
import software.bernie.example.block.tile.FertilizerTileEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class FertilizerModel extends AnimatedGeoModel<FertilizerTileEntity> {
   public ResourceLocation getAnimationFileLocation(FertilizerTileEntity animatable) {
      return animatable.func_145831_w().func_72896_J() ? new ResourceLocation("geckolib3", "animations/fertilizer.animation.json") : new ResourceLocation("geckolib3", "animations/botarium.animation.json");
   }

   public ResourceLocation getModelLocation(FertilizerTileEntity animatable) {
      return animatable.func_145831_w().func_72896_J() ? new ResourceLocation("geckolib3", "geo/fertilizer.geo.json") : new ResourceLocation("geckolib3", "geo/botarium.geo.json");
   }

   public ResourceLocation getTextureLocation(FertilizerTileEntity entity) {
      return entity.func_145831_w().func_72896_J() ? new ResourceLocation("geckolib3", "textures/block/fertilizer.png") : new ResourceLocation("geckolib3", "textures/block/botarium.png");
   }
}

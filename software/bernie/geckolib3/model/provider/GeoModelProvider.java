package software.bernie.geckolib3.model.provider;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.resource.GeckoLibCache;

public abstract class GeoModelProvider<T> {
   public double seekTime;
   public double lastGameTickTime;
   public boolean shouldCrashOnMissing = false;

   public GeoModel getModel(ResourceLocation location) {
      return (GeoModel)GeckoLibCache.getInstance().getGeoModels().get(location);
   }

   public abstract ResourceLocation getModelLocation(T var1);

   public abstract ResourceLocation getTextureLocation(T var1);
}

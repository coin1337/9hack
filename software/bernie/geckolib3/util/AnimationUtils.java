package software.bernie.geckolib3.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class AnimationUtils {
   public static double convertTicksToSeconds(double ticks) {
      return ticks / 20.0D;
   }

   public static double convertSecondsToTicks(double seconds) {
      return seconds * 20.0D;
   }

   public static <T extends Entity> Render<T> getRenderer(T entity) {
      RenderManager renderManager = Minecraft.func_71410_x().func_175598_ae();
      return renderManager.func_78713_a(entity);
   }

   public static <T extends Entity> GeoModelProvider getGeoModelForEntity(T entity) {
      Render<T> entityRenderer = getRenderer(entity);
      return entityRenderer instanceof IGeoRenderer ? ((IGeoRenderer)entityRenderer).getGeoModelProvider() : null;
   }
}

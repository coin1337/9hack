package software.bernie.example.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderManager;
import software.bernie.example.client.model.entity.LEModel;
import software.bernie.example.client.renderer.entity.layer.GeoExampleLayer;
import software.bernie.example.entity.GeoExampleEntityLayer;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class LERenderer extends GeoEntityRenderer<GeoExampleEntityLayer> {
   public LERenderer(RenderManager renderManager) {
      super(renderManager, new LEModel());
      this.addLayer(new GeoExampleLayer(this));
      this.field_76989_e = 0.2F;
   }
}

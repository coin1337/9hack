package software.bernie.example.client.renderer.entity.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class GeoExampleLayer extends GeoLayerRenderer {
   private static final ResourceLocation LAYER = new ResourceLocation("geckolib3", "textures/model/entity/le_glasses.png");
   private static final ResourceLocation MODEL = new ResourceLocation("geckolib3", "geo/le.geo.json");

   public GeoExampleLayer(IGeoRenderer entityRendererIn) {
      super(entityRendererIn);
   }

   public void render(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, Color renderColor) {
      Minecraft.func_71410_x().func_175598_ae().field_78724_e.func_110577_a(LAYER);
      this.entityRenderer.render(this.entityRenderer.getGeoModelProvider().getModel(MODEL), entitylivingbaseIn, partialTicks, (float)renderColor.getRed() / 255.0F, (float)renderColor.getBlue() / 255.0F, (float)renderColor.getGreen() / 255.0F, (float)renderColor.getAlpha() / 255.0F);
   }

   public boolean func_177142_b() {
      return false;
   }
}

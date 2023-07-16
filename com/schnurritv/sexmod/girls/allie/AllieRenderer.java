package com.schnurritv.sexmod.girls.allie;

import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.base.GirlRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class AllieRenderer extends GirlRenderer {
   public AllieRenderer(RenderManager renderManager, AnimatedGeoModel model, double leashHeightOffset) {
      super(renderManager, model, leashHeightOffset);
   }

   public void render(GeoModel model, GirlEntity animatable, float partialTicks, float red, float green, float blue, float alpha) {
      AllieEntity allie = (AllieEntity)animatable;
      allie.alpha = allie.alpha == 1.0F ? allie.alpha : allie.alpha - 0.01F;
      alpha = allie.alpha;
      GlStateManager.func_179152_a(alpha, alpha, alpha);
      GlStateManager.func_179109_b(0.0F, alpha == 1.0F ? 0.0F : 3.0F - alpha * 3.0F, 0.0F);
      super.render(model, animatable, partialTicks, red, green, blue, alpha);
   }
}

package com.schnurritv.sexmod.girls.slime.friendlySlime;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class FriendlySlimeRenderer extends RenderLiving<FriendlySlimeEntity> {
   private static final ResourceLocation SLIME_TEXTURES = new ResourceLocation("textures/entity/slime/slime.png");

   public FriendlySlimeRenderer(RenderManager p_i47193_1_) {
      super(p_i47193_1_, new FriendlySlimeModel(), 0.25F);
      this.func_177094_a(new FriendlySlimeGelLayer(this));
   }

   public void doRender(FriendlySlimeEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
      this.field_76989_e = 0.25F * (float)entity.getSlimeSize();
      super.func_76986_a(entity, x, y, z, entityYaw, partialTicks);
   }

   protected void preRenderCallback(FriendlySlimeEntity entitylivingbaseIn, float partialTickTime) {
      float f = 0.999F;
      GlStateManager.func_179152_a(0.999F, 0.999F, 0.999F);
      float f1 = (float)entitylivingbaseIn.getSlimeSize();
      float f2 = (entitylivingbaseIn.prevSquishFactor + (entitylivingbaseIn.squishFactor - entitylivingbaseIn.prevSquishFactor) * partialTickTime) / (f1 * 0.5F + 1.0F);
      float f3 = 1.0F / (f2 + 1.0F);
      GlStateManager.func_179152_a(f3 * f1, 1.0F / f3 * f1, f3 * f1);
   }

   protected ResourceLocation getEntityTexture(FriendlySlimeEntity entity) {
      return SLIME_TEXTURES;
   }
}

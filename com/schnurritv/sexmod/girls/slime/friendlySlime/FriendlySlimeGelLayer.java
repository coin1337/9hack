package com.schnurritv.sexmod.girls.slime.friendlySlime;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

public class FriendlySlimeGelLayer implements LayerRenderer<FriendlySlimeEntity> {
   private final FriendlySlimeRenderer slimeRenderer;
   private final ModelBase slimeModel = new ModelSlime(0);

   public FriendlySlimeGelLayer(FriendlySlimeRenderer slimeRendererIn) {
      this.slimeRenderer = slimeRendererIn;
   }

   public void doRenderLayer(FriendlySlimeEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (!entitylivingbaseIn.func_82150_aj()) {
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.func_179108_z();
         GlStateManager.func_179147_l();
         GlStateManager.func_187401_a(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         this.slimeModel.func_178686_a(this.slimeRenderer.func_177087_b());
         this.slimeModel.func_78088_a(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
         GlStateManager.func_179084_k();
         GlStateManager.func_179133_A();
      }

   }

   public boolean func_177142_b() {
      return true;
   }
}

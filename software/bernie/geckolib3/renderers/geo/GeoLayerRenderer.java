package software.bernie.geckolib3.renderers.geo;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.model.provider.GeoModelProvider;

public abstract class GeoLayerRenderer<T extends EntityLivingBase & IAnimatable> implements LayerRenderer<T> {
   protected final IGeoRenderer<T> entityRenderer;

   public GeoLayerRenderer(IGeoRenderer<T> entityRendererIn) {
      this.entityRenderer = entityRendererIn;
   }

   protected static <T extends EntityLivingBase> void renderCopyCutoutModel(ModelBase modelParentIn, ModelBase modelIn, ResourceLocation textureLocationIn, T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float partialTicks, float red, float green, float blue) {
      if (!entityIn.func_82150_aj()) {
         modelParentIn.func_178686_a(modelIn);
         modelIn.func_78086_a(entityIn, limbSwing, limbSwingAmount, partialTicks);
         modelIn.func_78087_a(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, 0.0625F, entityIn);
         renderCutoutModel(modelIn, textureLocationIn, entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, 0.0625F, red, green, blue);
      }

   }

   protected static <T extends EntityLivingBase> void renderCutoutModel(ModelBase modelIn, ResourceLocation textureLocationIn, T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, float red, float green, float blue) {
      GlStateManager.func_179131_c(red, green, blue, 1.0F);
      modelIn.func_78088_a(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
   }

   public void func_177141_a(T entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn) {
   }

   public IGeoRenderer<T> getRenderer() {
      return this.entityRenderer;
   }

   public GeoModelProvider<T> getEntityModel() {
      return this.entityRenderer.getGeoModelProvider();
   }

   protected ResourceLocation getEntityTexture(T entityIn) {
      return this.entityRenderer.getTextureLocation(entityIn);
   }

   public abstract void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7, Color var8);
}

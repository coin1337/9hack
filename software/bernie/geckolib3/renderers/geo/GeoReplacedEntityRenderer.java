package software.bernie.geckolib3.renderers.geo;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import software.bernie.shadowed.eliotlash.mclib.utils.Interpolations;

public abstract class GeoReplacedEntityRenderer<T extends IAnimatable> extends Render<EntityLivingBase> implements IGeoRenderer {
   private final AnimatedGeoModel<T> modelProvider;
   private final T animatable;
   protected final List<GeoLayerRenderer> layerRenderers = Lists.newArrayList();
   private IAnimatable currentAnimatable;
   private static Map<Class<? extends IAnimatable>, GeoReplacedEntityRenderer> renderers = new ConcurrentHashMap();

   public GeoReplacedEntityRenderer(RenderManager renderManager, AnimatedGeoModel<T> modelProvider, T animatable) {
      super(renderManager);
      this.modelProvider = modelProvider;
      this.animatable = animatable;
   }

   public static void registerReplacedEntity(Class<? extends IAnimatable> itemClass, GeoReplacedEntityRenderer renderer) {
      renderers.put(itemClass, renderer);
   }

   public static GeoReplacedEntityRenderer getRenderer(Class<? extends IAnimatable> item) {
      return (GeoReplacedEntityRenderer)renderers.get(item);
   }

   public void doRender(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.func_179094_E();
      GlStateManager.func_179137_b(x, y, z);
      boolean shouldSit = entity.func_184187_bx() != null && entity.func_184187_bx().shouldRiderSit();
      EntityModelData entityModelData = new EntityModelData();
      entityModelData.isSitting = shouldSit;
      entityModelData.isChild = entity.func_70631_g_();
      float f = Interpolations.lerpYaw(entity.field_70760_ar, entity.field_70761_aq, partialTicks);
      float f1 = Interpolations.lerpYaw(entity.field_70758_at, entity.field_70759_as, partialTicks);
      float netHeadYaw = f1 - f;
      float f7;
      if (shouldSit && entity.func_184187_bx() instanceof EntityLivingBase) {
         EntityLivingBase livingentity = (EntityLivingBase)entity.func_184187_bx();
         f = Interpolations.lerpYaw(livingentity.field_70760_ar, livingentity.field_70761_aq, partialTicks);
         netHeadYaw = f1 - f;
         f7 = MathHelper.func_76142_g(netHeadYaw);
         if (f7 < -85.0F) {
            f7 = -85.0F;
         }

         if (f7 >= 85.0F) {
            f7 = 85.0F;
         }

         f = f1 - f7;
         if (f7 * f7 > 2500.0F) {
            f += f7 * 0.2F;
         }

         netHeadYaw = f1 - f;
      }

      float headPitch = Interpolations.lerp(entity.field_70127_C, entity.field_70125_A, partialTicks);
      f7 = this.handleRotationFloat(entity, partialTicks);
      this.applyRotations(entity, f7, f, partialTicks);
      float limbSwingAmount = 0.0F;
      float limbSwing = 0.0F;
      if (!shouldSit && entity.func_70089_S()) {
         limbSwingAmount = Interpolations.lerp(entity.field_184618_aE, entity.field_70721_aZ, partialTicks);
         limbSwing = entity.field_184619_aG - entity.field_70721_aZ * (1.0F - partialTicks);
         if (entity.func_70631_g_()) {
            limbSwing *= 3.0F;
         }

         if (limbSwingAmount > 1.0F) {
            limbSwingAmount = 1.0F;
         }
      }

      entityModelData.headPitch = -headPitch;
      entityModelData.netHeadYaw = -netHeadYaw;
      AnimationEvent predicate = new AnimationEvent(this.animatable, limbSwing, limbSwingAmount, partialTicks, !(limbSwingAmount > -0.15F) || !(limbSwingAmount < 0.15F), Collections.singletonList(entityModelData));
      GeoModel model = this.modelProvider.getModel(this.modelProvider.getModelLocation(this.animatable));
      if (this.modelProvider instanceof IAnimatableModel) {
         this.modelProvider.setLivingAnimations(this.animatable, this.getUniqueID(entity), predicate);
      }

      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b(0.0F, 0.01F, 0.0F);
      Minecraft.func_71410_x().field_71446_o.func_110577_a(this.getEntityTexture(entity));
      Color renderColor = this.getRenderColor(entity, partialTicks);
      if (!entity.func_98034_c(Minecraft.func_71410_x().field_71439_g)) {
         this.render(model, entity, partialTicks, (float)renderColor.getRed() / 255.0F, (float)renderColor.getBlue() / 255.0F, (float)renderColor.getGreen() / 255.0F, (float)renderColor.getAlpha() / 255.0F);
      }

      if (entity instanceof EntityPlayer && !((EntityPlayer)entity).func_175149_v()) {
         Iterator var22 = this.layerRenderers.iterator();

         while(var22.hasNext()) {
            GeoLayerRenderer layerRenderer = (GeoLayerRenderer)var22.next();
            layerRenderer.func_177141_a(entity, limbSwing, limbSwingAmount, partialTicks, f7, netHeadYaw, headPitch, 0.0625F);
         }
      }

      if (entity instanceof EntityLiving) {
         Entity leashHolder = ((EntityLiving)entity).func_110166_bE();
         if (leashHolder != null) {
            this.renderLeash((EntityLiving)entity, x, y, z, entityYaw, partialTicks);
         }
      }

      GlStateManager.func_179121_F();
      GlStateManager.func_179121_F();
   }

   protected void preRenderCallback(EntityLivingBase entitylivingbaseIn, float partialTickTime) {
   }

   @Nullable
   protected ResourceLocation getEntityTexture(EntityLivingBase entity) {
      return this.getTextureLocation(this.currentAnimatable);
   }

   public AnimatedGeoModel getGeoModelProvider() {
      return this.modelProvider;
   }

   protected void applyRotations(EntityLivingBase entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      if (!entityLiving.func_70608_bn()) {
         GlStateManager.func_179114_b(180.0F - rotationYaw, 0.0F, 1.0F, 0.0F);
      }

      if (entityLiving.field_70725_aQ > 0) {
         float f = ((float)entityLiving.field_70725_aQ + partialTicks - 1.0F) / 20.0F * 1.6F;
         f = MathHelper.func_76129_c(f);
         if (f > 1.0F) {
            f = 1.0F;
         }

         GlStateManager.func_179114_b(f * this.getDeathMaxRotation(entityLiving), 0.0F, 0.0F, 1.0F);
      } else if (entityLiving.func_145818_k_() || entityLiving instanceof EntityPlayer) {
         String s = TextFormatting.func_110646_a(entityLiving.func_70005_c_());
         if (("Dinnerbone".equals(s) || "Grumm".equals(s)) && (!(entityLiving instanceof EntityPlayer) || ((EntityPlayer)entityLiving).func_175148_a(EnumPlayerModelParts.CAPE))) {
            GlStateManager.func_179137_b(0.0D, (double)(entityLiving.field_70131_O + 0.1F), 0.0D);
            GlStateManager.func_179114_b(180.0F, 0.0F, 0.0F, 1.0F);
         }
      }

   }

   protected boolean isVisible(EntityLivingBase livingEntityIn) {
      return !livingEntityIn.func_82150_aj();
   }

   private static float getFacingAngle(EnumFacing facingIn) {
      switch(facingIn) {
      case SOUTH:
         return 90.0F;
      case WEST:
         return 0.0F;
      case NORTH:
         return 270.0F;
      case EAST:
         return 180.0F;
      default:
         return 0.0F;
      }
   }

   protected float getDeathMaxRotation(EntityLivingBase entityLivingBaseIn) {
      return 90.0F;
   }

   protected float getSwingProgress(EntityLivingBase livingBase, float partialTickTime) {
      return livingBase.func_70678_g(partialTickTime);
   }

   protected float handleRotationFloat(EntityLivingBase livingBase, float partialTicks) {
      return (float)livingBase.field_70173_aa + partialTicks;
   }

   public final boolean addLayer(GeoLayerRenderer<? extends EntityLivingBase> layer) {
      return this.layerRenderers.add(layer);
   }

   public ResourceLocation getTextureLocation(Object instance) {
      return this.modelProvider.getTextureLocation(this.animatable);
   }

   protected void renderLeash(EntityLiving entityLivingIn, double x, double y, double z, float entityYaw, float partialTicks) {
      Entity entity = entityLivingIn.func_110166_bE();
      if (entity != null) {
         y -= (1.6D - (double)entityLivingIn.field_70131_O) * 0.5D;
         Tessellator tessellator = Tessellator.func_178181_a();
         BufferBuilder bufferbuilder = tessellator.func_178180_c();
         double d0 = this.interpolateValue((double)entity.field_70126_B, (double)entity.field_70177_z, (double)(partialTicks * 0.5F)) * 0.01745329238474369D;
         double d1 = this.interpolateValue((double)entity.field_70127_C, (double)entity.field_70125_A, (double)(partialTicks * 0.5F)) * 0.01745329238474369D;
         double d2 = Math.cos(d0);
         double d3 = Math.sin(d0);
         double d4 = Math.sin(d1);
         if (entity instanceof EntityHanging) {
            d2 = 0.0D;
            d3 = 0.0D;
            d4 = -1.0D;
         }

         double d5 = Math.cos(d1);
         double d6 = this.interpolateValue(entity.field_70169_q, entity.field_70165_t, (double)partialTicks) - d2 * 0.7D - d3 * 0.5D * d5;
         double d7 = this.interpolateValue(entity.field_70167_r + (double)entity.func_70047_e() * 0.7D, entity.field_70163_u + (double)entity.func_70047_e() * 0.7D, (double)partialTicks) - d4 * 0.5D - 0.25D;
         double d8 = this.interpolateValue(entity.field_70166_s, entity.field_70161_v, (double)partialTicks) - d3 * 0.7D + d2 * 0.5D * d5;
         double d9 = this.interpolateValue((double)entityLivingIn.field_70760_ar, (double)entityLivingIn.field_70761_aq, (double)partialTicks) * 0.01745329238474369D + 1.5707963267948966D;
         d2 = Math.cos(d9) * (double)entityLivingIn.field_70130_N * 0.4D;
         d3 = Math.sin(d9) * (double)entityLivingIn.field_70130_N * 0.4D;
         double d10 = this.interpolateValue(entityLivingIn.field_70169_q, entityLivingIn.field_70165_t, (double)partialTicks) + d2;
         double d11 = this.interpolateValue(entityLivingIn.field_70167_r, entityLivingIn.field_70163_u, (double)partialTicks);
         double d12 = this.interpolateValue(entityLivingIn.field_70166_s, entityLivingIn.field_70161_v, (double)partialTicks) + d3;
         x += d2;
         z += d3;
         double d13 = (double)((float)(d6 - d10));
         double d14 = (double)((float)(d7 - d11));
         double d15 = (double)((float)(d8 - d12));
         GlStateManager.func_179090_x();
         GlStateManager.func_179140_f();
         GlStateManager.func_179129_p();
         bufferbuilder.func_181668_a(5, DefaultVertexFormats.field_181706_f);

         int k;
         float f4;
         float f5;
         float f6;
         float f7;
         for(k = 0; k <= 24; ++k) {
            f4 = 0.5F;
            f5 = 0.4F;
            f6 = 0.3F;
            if (k % 2 == 0) {
               f4 *= 0.7F;
               f5 *= 0.7F;
               f6 *= 0.7F;
            }

            f7 = (float)k / 24.0F;
            bufferbuilder.func_181662_b(x + d13 * (double)f7 + 0.0D, y + d14 * (double)(f7 * f7 + f7) * 0.5D + (double)((24.0F - (float)k) / 18.0F + 0.125F), z + d15 * (double)f7).func_181666_a(f4, f5, f6, 1.0F).func_181675_d();
            bufferbuilder.func_181662_b(x + d13 * (double)f7 + 0.025D, y + d14 * (double)(f7 * f7 + f7) * 0.5D + (double)((24.0F - (float)k) / 18.0F + 0.125F) + 0.025D, z + d15 * (double)f7).func_181666_a(f4, f5, f6, 1.0F).func_181675_d();
         }

         tessellator.func_78381_a();
         bufferbuilder.func_181668_a(5, DefaultVertexFormats.field_181706_f);

         for(k = 0; k <= 24; ++k) {
            f4 = 0.5F;
            f5 = 0.4F;
            f6 = 0.3F;
            if (k % 2 == 0) {
               f4 *= 0.7F;
               f5 *= 0.7F;
               f6 *= 0.7F;
            }

            f7 = (float)k / 24.0F;
            bufferbuilder.func_181662_b(x + d13 * (double)f7 + 0.0D, y + d14 * (double)(f7 * f7 + f7) * 0.5D + (double)((24.0F - (float)k) / 18.0F + 0.125F) + 0.025D, z + d15 * (double)f7).func_181666_a(f4, f5, f6, 1.0F).func_181675_d();
            bufferbuilder.func_181662_b(x + d13 * (double)f7 + 0.025D, y + d14 * (double)(f7 * f7 + f7) * 0.5D + (double)((24.0F - (float)k) / 18.0F + 0.125F), z + d15 * (double)f7 + 0.025D).func_181666_a(f4, f5, f6, 1.0F).func_181675_d();
         }

         tessellator.func_78381_a();
         GlStateManager.func_179145_e();
         GlStateManager.func_179098_w();
         GlStateManager.func_179089_o();
      }

   }

   private double interpolateValue(double start, double end, double pct) {
      return start + (end - start) * pct;
   }

   static {
      AnimationController.addModelFetcher((object) -> {
         GeoReplacedEntityRenderer renderer = (GeoReplacedEntityRenderer)renderers.get(object.getClass());
         return renderer == null ? null : renderer.getGeoModelProvider();
      });
   }
}

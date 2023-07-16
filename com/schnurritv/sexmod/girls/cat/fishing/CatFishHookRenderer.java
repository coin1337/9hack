package com.schnurritv.sexmod.girls.cat.fishing;

import com.schnurritv.sexmod.girls.cat.CatEntity;
import com.schnurritv.sexmod.util.PenisMath;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CatFishHookRenderer extends Render<CatFishHook> {
   static final double RIGHT = 0.1896224320030116D;
   static final double UP = -0.5D;
   static final double FORWARD = 0.08742380916962415D;
   private static final ResourceLocation FISH_PARTICLES = new ResourceLocation("textures/particle/particles.png");

   public CatFishHookRenderer(RenderManager renderManagerIn) {
      super(renderManagerIn);
   }

   public void doRender(CatFishHook entity, double x, double y, double z, float entityYaw, float partialTicks) {
      CatEntity cat = entity.getAnglerClientSide();
      if (cat != null && !this.field_188301_f && cat.throwBackPercentage != 1.0F) {
         cat.fishEntity = entity;
         ItemStack caughtItem = (ItemStack)cat.func_184212_Q().func_187225_a(CatEntity.CAUGHT_ITEM);
         if (!caughtItem.func_77973_b().equals(Items.field_190931_a)) {
            cat.throwBackPercentage += 60.0F / (float)Minecraft.func_175610_ah() * 0.01666F * 2.0F;
            cat.throwBackPercentage = Math.min(1.0F, cat.throwBackPercentage);
            EntityPlayer player = Minecraft.func_71410_x().field_71439_g;
            Vec3d playerRenderPos = PenisMath.Lerp(new Vec3d(player.field_70142_S, player.field_70137_T, player.field_70136_U), player.func_174791_d(), (double)partialTicks);
            Vec3d hookPos = new Vec3d(x, y, z);
            Vec3d girlPos = PenisMath.Lerp(new Vec3d(cat.field_70142_S, cat.field_70137_T + 0.875D, cat.field_70136_U), cat.func_174791_d().func_72441_c(0.0D, 0.875D, 0.0D), (double)partialTicks);
            girlPos = girlPos.func_178788_d(playerRenderPos);
            hookPos = PenisMath.Lerp(hookPos, girlPos, (double)cat.throwBackPercentage);
            x = hookPos.field_72450_a;
            y = hookPos.field_72448_b;
            z = hookPos.field_72449_c;
         } else {
            cat.throwBackPercentage = 0.0F;
         }

         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b((float)x, (float)y, (float)z);
         GlStateManager.func_179091_B();
         GlStateManager.func_179152_a(0.5F, 0.5F, 0.5F);
         this.func_180548_c(entity);
         Tessellator tessellator = Tessellator.func_178181_a();
         BufferBuilder bufferbuilder = tessellator.func_178180_c();
         GlStateManager.func_179114_b(180.0F - this.field_76990_c.field_78735_i, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b((float)(this.field_76990_c.field_78733_k.field_74320_O == 2 ? -1 : 1) * -this.field_76990_c.field_78732_j, 1.0F, 0.0F, 0.0F);
         if (this.field_188301_f) {
            GlStateManager.func_179142_g();
            GlStateManager.func_187431_e(this.func_188298_c(entity));
         }

         if (!caughtItem.func_77973_b().equals(Items.field_190931_a)) {
            GlStateManager.func_179152_a(2.0F, 2.0F, 2.0F);
            GlStateManager.func_179109_b(0.0F, -0.2F, 0.0F);
            Minecraft.func_71410_x().func_175597_ag().func_178099_a(cat, caughtItem, TransformType.THIRD_PERSON_RIGHT_HAND);
            GlStateManager.func_179109_b(0.0F, 0.2F, 0.0F);
            GlStateManager.func_179152_a(0.5F, 0.5F, 0.5F);
         }

         this.func_180548_c(entity);
         bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181710_j);
         bufferbuilder.func_181662_b(-0.5D, -0.5D, 0.0D).func_187315_a(0.0625D, 0.1875D).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
         bufferbuilder.func_181662_b(0.5D, -0.5D, 0.0D).func_187315_a(0.125D, 0.1875D).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
         bufferbuilder.func_181662_b(0.5D, 0.5D, 0.0D).func_187315_a(0.125D, 0.125D).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
         bufferbuilder.func_181662_b(-0.5D, 0.5D, 0.0D).func_187315_a(0.0625D, 0.125D).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
         tessellator.func_78381_a();
         if (this.field_188301_f) {
            GlStateManager.func_187417_n();
            GlStateManager.func_179119_h();
         }

         GlStateManager.func_179101_C();
         GlStateManager.func_179121_F();
         int k = cat.func_184591_cq() == EnumHandSide.RIGHT ? 1 : -1;
         ItemStack itemstack = cat.func_184614_ca();
         if (!(itemstack.func_77973_b() instanceof ItemFishingRod)) {
            k = -k;
         }

         cat.field_70177_z = cat.targetYaw();
         cat.field_70761_aq = cat.targetYaw();
         cat.field_70165_t = cat.targetPos().field_72450_a;
         cat.field_70163_u = cat.targetPos().field_72448_b;
         cat.field_70161_v = cat.targetPos().field_72449_c;
         cat.field_70169_q = cat.targetPos().field_72450_a;
         cat.field_70167_r = cat.targetPos().field_72448_b;
         cat.field_70166_s = cat.targetPos().field_72449_c;
         float f9 = (cat.field_70760_ar + (cat.field_70761_aq - cat.field_70760_ar) * partialTicks) * 0.017453292F;
         double d0 = (double)MathHelper.func_76126_a(f9);
         double d1 = (double)MathHelper.func_76134_b(f9);
         double d2 = (double)k * 0.35D;
         double renderXPos = cat.field_70169_q + (cat.field_70165_t - cat.field_70169_q) * (double)partialTicks - d1 * d2 - d0 * 0.8D;
         double d5 = cat.field_70167_r + (double)cat.func_70047_e() + (cat.field_70163_u - cat.field_70167_r) * (double)partialTicks - 0.45D;
         double d6 = cat.field_70166_s + (cat.field_70161_v - cat.field_70166_s) * (double)partialTicks - d0 * d2 + d1 * 0.8D;
         double d7 = cat.func_70093_af() ? -0.1875D : 0.0D;
         double d13 = entity.field_70169_q + (entity.field_70165_t - entity.field_70169_q) * (double)partialTicks - Math.sin((double)(cat.targetYaw() + 90.0F) * 0.017453292519943295D) * 0.1896224320030116D - Math.sin((double)cat.targetYaw() * 0.017453292519943295D) * 0.08742380916962415D;
         double d8 = entity.field_70167_r + (entity.field_70163_u - entity.field_70167_r) * (double)partialTicks + 0.25D + -0.5D;
         double d9 = entity.field_70166_s + (entity.field_70161_v - entity.field_70166_s) * (double)partialTicks + Math.cos((double)(cat.targetYaw() + 90.0F) * 0.017453292519943295D) * 0.1896224320030116D + Math.cos((double)cat.targetYaw() * 0.017453292519943295D) * 0.08742380916962415D;
         double d10 = (double)((float)(renderXPos - d13));
         double d11 = (double)((float)(d5 - d8)) + d7;
         double d12 = (double)((float)(d6 - d9));
         GlStateManager.func_179090_x();
         GlStateManager.func_179140_f();
         if (caughtItem.func_77973_b().equals(Items.field_190931_a)) {
            bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);

            for(int i1 = 0; i1 <= 16; ++i1) {
               float f11 = (float)i1 / 16.0F;
               bufferbuilder.func_181662_b(x + d10 * (double)f11, y + d11 * (double)(f11 * f11 + f11) * 0.5D + 0.25D, z + d12 * (double)f11).func_181669_b(0, 0, 0, 255).func_181675_d();
            }

            tessellator.func_78381_a();
         }

         GlStateManager.func_179145_e();
         GlStateManager.func_179098_w();
         super.func_76986_a(entity, x, y, z, entityYaw, partialTicks);
      }

   }

   @Nullable
   protected ResourceLocation getEntityTexture(CatFishHook entity) {
      return FISH_PARTICLES;
   }
}

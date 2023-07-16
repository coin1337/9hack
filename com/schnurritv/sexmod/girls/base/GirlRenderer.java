package com.schnurritv.sexmod.girls.base;

import com.daripher.sexmod.client.util.FakeWorld;
import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirl;
import com.schnurritv.sexmod.girls.bia.BiaModel;
import com.schnurritv.sexmod.girls.ellie.EllieEntity;
import com.schnurritv.sexmod.girls.ellie.EllieModel;
import com.schnurritv.sexmod.girls.jenny.JennyEntity;
import com.schnurritv.sexmod.girls.jenny.JennyModel;
import com.schnurritv.sexmod.girls.slime.SlimeEntity;
import com.schnurritv.sexmod.girls.slime.SlimeModel;
import com.schnurritv.sexmod.util.MatrixHelper;
import com.schnurritv.sexmod.util.PenisMath;
import com.schnurritv.sexmod.util.SkinHelper;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.geo.render.built.GeoQuad;
import software.bernie.geckolib3.geo.render.built.GeoVertex;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;
import software.bernie.geckolib3.renderers.geo.RenderHurtColor;
import software.bernie.shadowed.eliotlash.mclib.utils.Interpolations;

public class GirlRenderer<T extends GirlEntity & IAnimatable> extends GeoEntityRenderer<T> {
   protected double leashHeightOffset;
   protected T girl;
   Minecraft mc;
   protected static HashMap<UUID, ResourceLocation> playerSkins = new HashMap();
   Color cockColor = new Color(245, 199, 165);
   Color nutColor = new Color(245, 157, 169);
   boolean alreadySaid = false;
   float pull = 0.0F;
   public static BufferBuilder currentBuilder;

   public GirlRenderer(RenderManager renderManager, AnimatedGeoModel<T> model, double leashHeightOffset) {
      super(renderManager, model);
      this.leashHeightOffset = leashHeightOffset;
      this.mc = Minecraft.func_71410_x();
      this.field_76989_e = 0.2F;
   }

   protected ResourceLocation getPlayerSkin(T girl) throws IOException {
      ResourceLocation playerSkin;
      if (!(girl.field_70170_p instanceof FakeWorld) && girl.playerSheHasSexWith() != null) {
         playerSkin = (ResourceLocation)playerSkins.get(girl.playerSheHasSexWith());
         if (playerSkin == null) {
            return this.producePlayerSkin(girl.playerSheHasSexWith(), girl.field_70170_p);
         }
      } else {
         playerSkin = (ResourceLocation)playerSkins.get(this.mc.func_110432_I().func_148256_e().getId());
         if (playerSkin == null) {
            return this.producePlayerSkin(this.mc.func_110432_I().func_148256_e().getId(), girl.field_70170_p);
         }
      }

      return playerSkin;
   }

   protected ResourceLocation producePlayerSkin(UUID player, World world) throws IOException {
      BufferedImage skinImage;
      try {
         skinImage = SkinHelper.getSkinByUUID(player);
         Graphics graphics = skinImage.getGraphics();
         graphics.setColor(this.cockColor);
         graphics.fillRect(0, 0, 4, 3);
         graphics.setColor(this.nutColor);
         graphics.fillRect(4, 0, 3, 3);
      } catch (Exception var5) {
         if (!this.alreadySaid) {
            this.alreadySaid = true;
            System.out.println("couldn't load player skin... offline or cracked? what is it sir?");
         }

         skinImage = ImageIO.read(this.mc.func_110442_L().func_110536_a(new ResourceLocation("sexmod", "textures/player/steve.png")).func_110527_b());
      }

      playerSkins.put(player, this.field_76990_c.field_78724_e.func_110578_a("player" + player, new DynamicTexture(skinImage)));
      return (ResourceLocation)playerSkins.get(player);
   }

   public void render(GeoModel model, T animatable, float partialTicks, float red, float green, float blue, float alpha) {
      GlStateManager.func_179129_p();
      GlStateManager.func_179091_B();
      this.renderEarly(animatable, partialTicks, red, green, blue, alpha);
      this.renderLate(animatable, partialTicks, red, green, blue, alpha);
      BufferBuilder builder = Tessellator.func_178181_a().func_178180_c();
      builder.func_181668_a(7, DefaultVertexFormats.field_181712_l);
      GeoBone steveBone = null;
      this.func_110776_a((ResourceLocation)Objects.requireNonNull(this.getEntityTexture(this.girl)));
      Iterator var10 = model.topLevelBones.iterator();

      while(var10.hasNext()) {
         GeoBone group = (GeoBone)var10.next();
         if (group.getName().equals("steve")) {
            steveBone = group;
         } else {
            this.renderRecursively(builder, group, red, green, blue, alpha);
         }
      }

      Tessellator.func_178181_a().func_78381_a();
      if (steveBone != null) {
         builder.func_181668_a(7, DefaultVertexFormats.field_181712_l);

         try {
            Minecraft.func_71410_x().field_71446_o.func_110577_a(this.getPlayerSkin(this.girl));
         } catch (IOException var12) {
            var12.printStackTrace();
         }

         this.renderRecursively(builder, steveBone, red, green, blue, alpha);
         Tessellator.func_178181_a().func_78381_a();
      }

      this.renderAfter(animatable, partialTicks, red, green, blue, alpha);
      GlStateManager.func_179101_C();
      GlStateManager.func_179089_o();
   }

   public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
      this.girl = entity;
      if (!(this.girl.field_70170_p instanceof FakeWorld)) {
         EntityDataManager dataManager = this.girl.func_184212_Q();
         if (!((String)dataManager.func_187225_a(GirlEntity.MASTER)).equals("")) {
            EntityPlayer master = this.girl.field_70170_p.func_152378_a(UUID.fromString((String)dataManager.func_187225_a(GirlEntity.MASTER)));
            if (master != null && master.func_184218_aH() && master.func_184187_bx() instanceof EntityHorse && ((EntityHorse)master.func_184187_bx()).func_110257_ck()) {
               EntityLiving ridingEntity = (EntityLiving)master.func_184187_bx();
               EntityPlayer player = this.mc.field_71439_g;
               Vec3d lookVec = ridingEntity.func_70040_Z();
               Vec3d masterRenderPos = PenisMath.Lerp(new Vec3d(master.field_70142_S, master.field_70137_T, master.field_70136_U), master.func_174791_d(), (double)partialTicks);
               Vec3d playerRenderPos = PenisMath.Lerp(new Vec3d(player.field_70142_S, player.field_70137_T, player.field_70136_U), player.func_174791_d(), (double)partialTicks);
               playerRenderPos = masterRenderPos.func_178788_d(playerRenderPos);
               x = playerRenderPos.field_72450_a + lookVec.field_72450_a * -0.5D;
               y = playerRenderPos.field_72448_b + 0.15000000596046448D;
               z = playerRenderPos.field_72449_c + lookVec.field_72449_c * -0.5D;
               entity.field_70761_aq = ridingEntity.field_70761_aq;
            }
         } else if ((Boolean)dataManager.func_187225_a(GirlEntity.SHOULD_BE_AT_TARGET)) {
            if (!(entity instanceof PlayerGirl)) {
               Vec3d playerRenderPos = PenisMath.Lerp(new Vec3d(this.mc.field_71439_g.field_70142_S, this.mc.field_71439_g.field_70137_T, this.mc.field_71439_g.field_70136_U), this.mc.field_71439_g.func_174791_d(), (double)partialTicks);
               Vec3d dependantPos = this.girl.targetPos().func_178788_d(playerRenderPos);
               x = dependantPos.field_72450_a;
               y = dependantPos.field_72448_b;
               z = dependantPos.field_72449_c;
            }

            entity.field_70761_aq = (Float)dataManager.func_187225_a(GirlEntity.TARGET_YAW);
            entity.field_70760_ar = (Float)dataManager.func_187225_a(GirlEntity.TARGET_YAW);
         }
      }

      if (entity.func_110167_bD()) {
         this.renderLeash(entity, x, y + this.leashHeightOffset, z, partialTicks);
      }

      GlStateManager.func_179094_E();
      GlStateManager.func_179137_b(x, y, z);
      GL11.glDisable(2896);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 0.5F);
      GlStateManager.func_179108_z();
      GlStateManager.func_179147_l();
      GlStateManager.func_187401_a(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      boolean shouldSit = entity.func_184187_bx() != null && entity.func_184187_bx().shouldRiderSit();
      EntityModelData entityModelData = new EntityModelData();
      entityModelData.isSitting = shouldSit;
      entityModelData.isChild = entity.func_70631_g_();
      float f = Interpolations.lerpYaw(entity.field_70760_ar, entity.field_70761_aq, partialTicks);
      float f1 = Interpolations.lerpYaw(entity.field_70758_at, entity.field_70759_as, partialTicks);
      float netHeadYaw = f1 - f;
      float f3;
      if (shouldSit && entity.func_184187_bx() instanceof EntityLivingBase) {
         EntityLivingBase livingentity = (EntityLivingBase)entity.func_184187_bx();
         f = Interpolations.lerpYaw(livingentity.field_70760_ar, livingentity.field_70761_aq, partialTicks);
         netHeadYaw = f1 - f;
         f3 = MathHelper.func_76142_g(netHeadYaw);
         if (f3 < -85.0F) {
            f3 = -85.0F;
         }

         if (f3 >= 85.0F) {
            f3 = 85.0F;
         }

         f = f1 - f3;
         if (f3 * f3 > 2500.0F) {
            f += f3 * 0.2F;
         }

         netHeadYaw = f1 - f;
      }

      float headPitch = Interpolations.lerp(entity.field_70127_C, entity.field_70125_A, partialTicks);
      f3 = this.handleRotationFloat(entity, partialTicks);
      this.applyRotations(entity, f3, f, partialTicks);
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
      AnimationEvent<T> predicate = new AnimationEvent(entity, limbSwing, limbSwingAmount, partialTicks, !(limbSwingAmount > -0.15F) || !(limbSwingAmount < 0.15F), Collections.singletonList(entityModelData));
      GeoModelProvider modelProvider = super.getGeoModelProvider();
      ResourceLocation location = modelProvider.getModelLocation(entity);
      GeoModel model = modelProvider.getModel(location);
      if (modelProvider instanceof IAnimatableModel) {
         ((IAnimatableModel)modelProvider).setLivingAnimations(entity, entity.func_110124_au().hashCode(), predicate);
      }

      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b(0.0F, 0.01F, 0.0F);
      Minecraft.func_71410_x().field_71446_o.func_110577_a(this.getEntityTexture(entity));
      software.bernie.geckolib3.core.util.Color renderColor = this.getRenderColor(entity, partialTicks);
      boolean flag = this.setDoRenderBrightness(entity, partialTicks);
      this.render(model, entity, partialTicks, (float)renderColor.getRed() / 255.0F, (float)renderColor.getBlue() / 255.0F, (float)renderColor.getGreen() / 255.0F, (float)renderColor.getAlpha() / 255.0F);
      if (flag) {
         RenderHurtColor.unset();
      }

      Iterator var25 = this.layerRenderers.iterator();

      while(var25.hasNext()) {
         GeoLayerRenderer<T> layerRenderer = (GeoLayerRenderer)var25.next();
         layerRenderer.render(entity, limbSwing, limbSwingAmount, partialTicks, limbSwing, netHeadYaw, headPitch, renderColor);
      }

      GL11.glEnable(2896);
      GlStateManager.func_179084_k();
      GlStateManager.func_179133_A();
      GlStateManager.func_179121_F();
      GlStateManager.func_179121_F();
   }

   protected void setUpSpecialBones(String boneName, GeoBone bone) {
   }

   protected void renderLeash(GirlEntity entityLivingIn, double x, double y, double z, float partialTicks) {
      Entity entity = entityLivingIn.func_110166_bE();
      if (entity != null) {
         y -= (1.6D - (double)entityLivingIn.field_70131_O) * 0.5D;
         Tessellator tessellator = Tessellator.func_178181_a();
         BufferBuilder bufferbuilder = tessellator.func_178180_c();
         double d0 = PenisMath.Lerp((double)entity.field_70126_B, (double)entity.field_70177_z, (double)(partialTicks * 0.5F)) * 0.01745329238474369D;
         double d1 = PenisMath.Lerp((double)entity.field_70127_C, (double)entity.field_70125_A, (double)(partialTicks * 0.5F)) * 0.01745329238474369D;
         double d2 = Math.cos(d0);
         double d3 = Math.sin(d0);
         double d4 = Math.sin(d1);
         if (entity instanceof EntityHanging) {
            d2 = 0.0D;
            d3 = 0.0D;
            d4 = -1.0D;
         }

         double d5 = Math.cos(d1);
         double d6 = PenisMath.Lerp(entity.field_70169_q, entity.field_70165_t, (double)partialTicks) - d2 * 0.7D - d3 * 0.5D * d5;
         double d7 = PenisMath.Lerp(entity.field_70167_r + (double)entity.func_70047_e() * 0.7D, entity.field_70163_u + (double)entity.func_70047_e() * 0.7D, (double)partialTicks) - d4 * 0.5D - 0.25D;
         double d8 = PenisMath.Lerp(entity.field_70166_s, entity.field_70161_v, (double)partialTicks) - d3 * 0.7D + d2 * 0.5D * d5;
         double d9 = PenisMath.Lerp((double)entityLivingIn.field_70760_ar, (double)entityLivingIn.field_70761_aq, (double)partialTicks) * 0.01745329238474369D + 1.5707963267948966D;
         d2 = Math.cos(d9) * (double)entityLivingIn.field_70130_N * 0.4D;
         d3 = Math.sin(d9) * (double)entityLivingIn.field_70130_N * 0.4D;
         double d10 = PenisMath.Lerp(entityLivingIn.field_70169_q, entityLivingIn.field_70165_t, (double)partialTicks) + d2;
         double d11 = PenisMath.Lerp(entityLivingIn.field_70167_r, entityLivingIn.field_70163_u, (double)partialTicks);
         double d12 = PenisMath.Lerp(entityLivingIn.field_70166_s, entityLivingIn.field_70161_v, (double)partialTicks) + d3;
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

   public void renderRecursively(BufferBuilder builder, GeoBone bone, float red, float green, float blue, float alpha) {
      if (!(this.girl.field_70170_p instanceof FakeWorld)) {
         String boneName = bone.getName();
         if (boneName.equals("weapon") && this.girl instanceof Fighter) {
            this.renderWeapon(builder, bone);
         }

         if (boneName.equals("itemRenderer") && this.girl.currentAction() == GirlEntity.Action.PAYMENT) {
            this.renderItems(builder, bone);
         }

         currentBuilder = builder;
         this.setUpSpecialBones(boneName, bone);
         MATRIX_STACK.push();
         MATRIX_STACK.translate(bone);
         MATRIX_STACK.moveToPivot(bone);
         MATRIX_STACK.rotate(bone);
         MATRIX_STACK.scale(bone);
         MATRIX_STACK.moveBackFromPivot(bone);
         if (!bone.isHidden) {
            double xOffset = 0.0D;
            if (boneName.startsWith("armor") && (Integer)this.girl.field_70180_af.func_187225_a(GirlEntity.CURRENT_MODEL) != 0) {
               Object model;
               if (this.girl instanceof JennyEntity) {
                  model = new JennyModel();
               } else if (this.girl instanceof EllieEntity) {
                  model = new EllieModel();
               } else if (this.girl instanceof SlimeEntity) {
                  model = new SlimeModel();
               } else {
                  model = new BiaModel();
               }

               ArmorMaterial material;
               try {
                  if (Arrays.asList(((GirlModel)model).getHelmetBones()).contains(boneName)) {
                     material = ((ItemArmor)((ItemArmor)((ItemStack)this.girl.field_70180_af.func_187225_a(Fighter.HELMET)).func_77973_b())).func_82812_d();
                  } else if (Arrays.asList(((GirlModel)model).getChestPlateBones()).contains(boneName)) {
                     material = ((ItemArmor)((ItemArmor)((ItemStack)this.girl.field_70180_af.func_187225_a(Fighter.CHEST_PLATE)).func_77973_b())).func_82812_d();
                  } else if (Arrays.asList(((GirlModel)model).getPantsBones()).contains(boneName)) {
                     material = ((ItemArmor)((ItemArmor)((ItemStack)this.girl.field_70180_af.func_187225_a(Fighter.PANTS)).func_77973_b())).func_82812_d();
                  } else {
                     material = ((ItemArmor)((ItemArmor)((ItemStack)this.girl.field_70180_af.func_187225_a(Fighter.SHOES)).func_77973_b())).func_82812_d();
                  }
               } catch (ClassCastException var15) {
                  System.out.println("couldn't get the armor material");
                  material = ArmorMaterial.GOLD;
               }

               double factor = 0.0D;
               switch(material) {
               case GOLD:
                  factor = 1.0D;
                  break;
               case IRON:
                  factor = 2.0D;
                  break;
               case CHAIN:
                  factor = 3.0D;
                  break;
               case LEATHER:
                  factor = 4.0D;
               }

               xOffset = 72.0D * factor / 4096.0D;
            }

            Iterator var16 = bone.childCubes.iterator();

            while(var16.hasNext()) {
               GeoCube cube = (GeoCube)var16.next();
               MATRIX_STACK.push();
               GlStateManager.func_179094_E();
               this.renderCube(builder, cube, red, green, blue, alpha, xOffset);
               GlStateManager.func_179121_F();
               MATRIX_STACK.pop();
            }

            var16 = bone.childBones.iterator();

            while(var16.hasNext()) {
               GeoBone childBone = (GeoBone)var16.next();
               if (xOffset == 0.0D) {
                  this.renderRecursively(builder, childBone, red, green, blue, alpha);
               } else {
                  this.renderRecursively(builder, childBone, red, green, blue, alpha, xOffset);
               }
            }
         }

         try {
            MATRIX_STACK.pop();
         } catch (IllegalStateException var14) {
         }

      }
   }

   public void renderRecursively(BufferBuilder builder, GeoBone bone, float red, float green, float blue, float alpha, double xOffset) {
      if (!(this.girl.field_70170_p instanceof FakeWorld)) {
         if (bone.getName().equals("weapon")) {
            this.renderWeapon(builder, bone);
         }

         this.setUpSpecialBones(bone.getName(), bone);
         MATRIX_STACK.push();
         MATRIX_STACK.translate(bone);
         MATRIX_STACK.moveToPivot(bone);
         MATRIX_STACK.rotate(bone);
         MATRIX_STACK.scale(bone);
         MATRIX_STACK.moveBackFromPivot(bone);
         if (!bone.isHidden) {
            Iterator var9 = bone.childCubes.iterator();

            while(var9.hasNext()) {
               GeoCube cube = (GeoCube)var9.next();
               MATRIX_STACK.push();
               GlStateManager.func_179094_E();
               this.renderCube(builder, cube, red, green, blue, alpha, xOffset);
               GlStateManager.func_179121_F();
               MATRIX_STACK.pop();
            }

            var9 = bone.childBones.iterator();

            while(var9.hasNext()) {
               GeoBone childBone = (GeoBone)var9.next();
               this.renderRecursively(builder, childBone, red, green, blue, alpha, xOffset);
            }
         }

         MATRIX_STACK.pop();
      }
   }

   public void renderCube(BufferBuilder builder, GeoCube cube, float red, float green, float blue, float alpha, double xOffset) {
      MATRIX_STACK.moveToPivot(cube);
      MATRIX_STACK.rotate(cube);
      MATRIX_STACK.moveBackFromPivot(cube);
      GeoQuad[] var9 = cube.quads;
      int var10 = var9.length;

      for(int var11 = 0; var11 < var10; ++var11) {
         GeoQuad quad = var9[var11];
         if (quad != null) {
            Vector3f normal = new Vector3f((float)quad.normal.func_177958_n(), (float)quad.normal.func_177956_o(), (float)quad.normal.func_177952_p());
            MATRIX_STACK.getNormalMatrix().transform(normal);
            if ((cube.size.y == 0.0F || cube.size.z == 0.0F) && normal.getX() < 0.0F) {
               normal.x *= -1.0F;
            }

            if ((cube.size.x == 0.0F || cube.size.z == 0.0F) && normal.getY() < 0.0F) {
               normal.y *= -1.0F;
            }

            if ((cube.size.x == 0.0F || cube.size.y == 0.0F) && normal.getZ() < 0.0F) {
               normal.z *= -1.0F;
            }

            GeoVertex[] var14 = quad.vertices;
            int var15 = var14.length;

            for(int var16 = 0; var16 < var15; ++var16) {
               GeoVertex vertex = var14[var16];
               Vector4f vector4f = new Vector4f(vertex.position.getX(), vertex.position.getY(), vertex.position.getZ(), 1.0F);
               MATRIX_STACK.getModelMatrix().transform(vector4f);
               builder.func_181662_b((double)vector4f.getX(), (double)vector4f.getY(), (double)vector4f.getZ()).func_187315_a((double)vertex.textureU + xOffset, (double)vertex.textureV).func_181666_a(red, green, blue, alpha).func_181663_c(normal.getX(), normal.getY(), normal.getZ()).func_181675_d();
            }
         }
      }

   }

   void renderItems(BufferBuilder builder, GeoBone bone) {
      ItemStack stack = null;
      ItemRenderer itemRenderer = Minecraft.func_71410_x().func_175597_ag();
      String var5 = (String)this.girl.field_70180_af.func_187225_a(GirlEntity.ANIMATION_FOLLOW_UP);
      byte var6 = -1;
      switch(var5.hashCode()) {
      case -20842805:
         if (var5.equals("blowjob")) {
            var6 = 1;
         }
         break;
      case 64419037:
         if (var5.equals("boobjob")) {
            var6 = 3;
         }
         break;
      case 95761198:
         if (var5.equals("doggy")) {
            var6 = 0;
         }
         break;
      case 109773592:
         if (var5.equals("strip")) {
            var6 = 2;
         }
         break;
      case 2014427283:
         if (var5.equals("touch_boobs")) {
            var6 = 4;
         }
      }

      switch(var6) {
      case 0:
         stack = new ItemStack(Items.field_151045_i, 2);
         break;
      case 1:
         stack = new ItemStack(Items.field_151166_bC, 3);
         break;
      case 2:
         stack = new ItemStack(Items.field_151043_k, 1);
         break;
      case 3:
         stack = new ItemStack(Items.field_151079_bi, 2);
         break;
      case 4:
         stack = new ItemStack(Items.field_151115_aP, 2, 1);
      }

      if (stack != null) {
         for(int i = 0; i < stack.func_190916_E(); ++i) {
            GlStateManager.func_179094_E();
            Tessellator.func_178181_a().func_78381_a();
            MatrixHelper.multiplyMatrix(IGeoRenderer.MATRIX_STACK, bone);
            GL11.glEnable(2896);
            GL11.glRotated((double)bone.getRotationX() + 2.5D, 0.0D, 0.0D, 1.0D);
            GL11.glRotated((double)bone.getRotationY(), 0.0D, 1.0D, 0.0D);
            GL11.glRotated((double)bone.getRotationZ(), 1.0D, 0.0D, 0.0D);
            switch(i) {
            case 1:
               GL11.glRotated(-15.0D, 0.0D, 0.0D, 1.0D);
               GlStateManager.func_179137_b(0.0D, 0.0D, -0.025D);
               break;
            case 2:
               GL11.glRotated(15.0D, 0.0D, 0.0D, 1.0D);
               GlStateManager.func_179137_b(0.0D, 0.0D, 0.025D);
            }

            GlStateManager.func_179152_a(this.girl.itemRendererSize, this.girl.itemRendererSize, this.girl.itemRendererSize);
            itemRenderer.func_178099_a(this.girl, new ItemStack(stack.func_77973_b(), 1), TransformType.THIRD_PERSON_RIGHT_HAND);
            this.func_110776_a((ResourceLocation)Objects.requireNonNull(this.getEntityTexture(this.girl)));
            builder.func_181668_a(7, DefaultVertexFormats.field_181712_l);
            GL11.glDisable(2896);
            GlStateManager.func_179121_F();
         }

      }
   }

   protected ItemStack changeWeaponBeforeRendering(@Nullable ItemStack selectedWeapon) {
      return selectedWeapon;
   }

   void renderWeapon(BufferBuilder builder, GeoBone bone) {
      EntityDataManager dataManager = this.girl.func_184212_Q();
      Fighter girl = (Fighter)this.girl;
      int attackMode = (Integer)dataManager.func_187225_a(Fighter.ATTACK_MODE);
      if (girl.currentAction() != GirlEntity.Action.BOW) {
         this.pull = 0.0F;
      }

      ItemStack boneItem = null;
      if (attackMode == 1) {
         boneItem = (ItemStack)dataManager.func_187225_a(Fighter.WEAPON);
      } else if (attackMode == 2) {
         boneItem = (ItemStack)dataManager.func_187225_a(Fighter.BOW);
      }

      boneItem = this.changeWeaponBeforeRendering(boneItem);
      if (boneItem != null) {
         if (boneItem.func_77973_b().equals(Items.field_151031_f) && girl.currentAction() == GirlEntity.Action.BOW) {
            this.pull += 0.015F;
            girl.setActiveStackUse(Math.round(-this.pull * 20.0F + (float)boneItem.func_77988_m()));
            girl.setActiveItemStack(boneItem);
         }

         GlStateManager.func_179094_E();
         Tessellator.func_178181_a().func_78381_a();
         MatrixHelper.multiplyMatrix(IGeoRenderer.MATRIX_STACK, bone);
         GL11.glEnable(2896);
         if (boneItem.func_77973_b() instanceof ItemBow) {
            GL11.glRotatef((float)girl.holdBowRot, 1.0F, 0.0F, 0.0F);
         } else if (girl.currentAction() == GirlEntity.Action.ATTACK && girl.nextAttack == 0) {
            GlStateManager.func_179137_b(girl.swordOffsetStab.field_72450_a, girl.swordOffsetStab.field_72448_b, girl.swordOffsetStab.field_72449_c);
            GL11.glRotatef((float)girl.stabSwordRot, 1.0F, 0.0F, 0.0F);
         } else {
            GL11.glRotatef((float)girl.slashSwordRot, 1.0F, 0.0F, 0.0F);
         }

         Minecraft.func_71410_x().func_175597_ag().func_178099_a(this.girl, boneItem, TransformType.THIRD_PERSON_RIGHT_HAND);
         this.func_110776_a((ResourceLocation)Objects.requireNonNull(this.getEntityTexture(this.girl)));
         builder.func_181668_a(7, DefaultVertexFormats.field_181712_l);
         GL11.glDisable(2896);
         GlStateManager.func_179121_F();
      }
   }
}

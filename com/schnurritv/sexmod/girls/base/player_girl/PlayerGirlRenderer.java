package com.schnurritv.sexmod.girls.base.player_girl;

import com.schnurritv.sexmod.girls.base.Fighter;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.base.GirlModel;
import com.schnurritv.sexmod.girls.base.GirlRenderer;
import com.schnurritv.sexmod.girls.bia.BiaModel;
import com.schnurritv.sexmod.girls.ellie.EllieEntity;
import com.schnurritv.sexmod.girls.ellie.EllieModel;
import com.schnurritv.sexmod.girls.jenny.JennyEntity;
import com.schnurritv.sexmod.girls.jenny.JennyModel;
import com.schnurritv.sexmod.util.MatrixHelper;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class PlayerGirlRenderer extends GirlRenderer {
   ItemStack mainHandStack;
   ItemStack offHandStack;
   boolean isSneaking;
   boolean isUsingItem;
   protected PlayerGirl entity;
   protected float partialTicks;
   float pull;

   public PlayerGirlRenderer(RenderManager renderManager, AnimatedGeoModel model) {
      super(renderManager, model, 0.0D);
      this.mainHandStack = ItemStack.field_190927_a;
      this.offHandStack = ItemStack.field_190927_a;
      this.isSneaking = false;
      this.isUsingItem = false;
      this.pull = 0.0F;
   }

   public void doRender(GirlEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
      PlayerGirl girl = (PlayerGirl)entity;
      if (girl.getOwner() != null) {
         EntityPlayer player = Minecraft.func_71410_x().field_71439_g.field_70170_p.func_152378_a(girl.getOwner());
         if (player != null) {
            this.mainHandStack = player.func_184614_ca();
            this.offHandStack = player.func_184592_cb();
            this.isUsingItem = girl.isUsingItem;
            this.isSneaking = girl.isSneaking;
            this.entity = (PlayerGirl)entity;
            this.partialTicks = partialTicks;
            girl.updateArmor(player);
            if (!player.getPersistentID().equals(Minecraft.func_71410_x().field_71439_g.getPersistentID())) {
               this.func_147906_a(entity, player.func_70005_c_(), x, y + (double)girl.getNameTagHeight(), z, 300);
            }

            super.doRender(entity, x, y, z, entityYaw, partialTicks);
         }
      }
   }

   protected void setUpSpecialBones(String boneName, GeoBone bone) {
   }

   public void renderRecursively(BufferBuilder builder, GeoBone bone, float red, float green, float blue, float alpha) {
      String boneName = bone.getName();
      if (this.isSneaking) {
         if (boneName.equals("upperBody")) {
            bone.setRotationX(bone.getRotationX() - 0.5F);
         }

         if (boneName.equals("head")) {
            bone.setRotationX(bone.getRotationX() + 0.5F);
         }
      }

      this.setUpSpecialBones(boneName, bone);
      if (this.isUsingItem && (this.mainHandStack.func_77973_b() instanceof ItemBow || this.offHandStack.func_77973_b() instanceof ItemBow)) {
         if (boneName.equals("armR")) {
            bone.setRotationX(bone.getRotationX() - this.girl.field_70125_A / 50.0F);
         }

         if (boneName.equals("armL")) {
            bone.setRotationY(bone.getRotationY() - this.girl.field_70125_A / 50.0F);
         }

         if (this.offHandStack.func_77973_b() instanceof ItemBow) {
            ItemStack temp = this.offHandStack;
            this.offHandStack = this.mainHandStack;
            this.mainHandStack = temp;
         }
      }

      if (this.isUsingItem) {
         if (this.mainHandStack.func_77973_b() instanceof ItemShield) {
            if (boneName.equals("armR")) {
               bone.setRotationZ(0.0F);
               bone.setRotationX(0.5F);
            }
         } else if (this.offHandStack.func_77973_b() instanceof ItemShield && boneName.equals("armL")) {
            bone.setRotationZ(0.0F);
            bone.setRotationX(0.5F);
         }
      }

      if (boneName.equals("weapon") && !this.mainHandStack.func_190926_b()) {
         this.renderItemInHand(builder, bone, false);
      }

      if (boneName.equals("offhand") && !this.offHandStack.func_190926_b()) {
         this.renderItemInHand(builder, bone, true);
      }

      MATRIX_STACK.push();
      MATRIX_STACK.translate(bone);
      MATRIX_STACK.moveToPivot(bone);
      MATRIX_STACK.rotate(bone);
      MATRIX_STACK.scale(bone);
      MATRIX_STACK.moveBackFromPivot(bone);
      if (!bone.isHidden) {
         double xOffset = 0.0D;
         if (boneName.startsWith("armor") && (Integer)this.girl.func_184212_Q().func_187225_a(GirlEntity.CURRENT_MODEL) != 0) {
            Object model;
            if (this.girl instanceof JennyEntity) {
               model = new JennyModel();
            } else if (this.girl instanceof EllieEntity) {
               model = new EllieModel();
            } else {
               model = new BiaModel();
            }

            PlayerGirl playerGirl = (PlayerGirl)this.girl;

            ArmorMaterial material;
            try {
               if (Arrays.asList(((GirlModel)model).getHelmetBones()).contains(boneName)) {
                  material = ((ItemArmor)((ItemArmor)((ItemStack)playerGirl.func_184212_Q().func_187225_a(Fighter.HELMET)).func_77973_b())).func_82812_d();
               } else if (Arrays.asList(((GirlModel)model).getChestPlateBones()).contains(boneName)) {
                  material = ((ItemArmor)((ItemArmor)((ItemStack)playerGirl.func_184212_Q().func_187225_a(Fighter.CHEST_PLATE)).func_77973_b())).func_82812_d();
               } else if (Arrays.asList(((GirlModel)model).getPantsBones()).contains(boneName)) {
                  material = ((ItemArmor)((ItemArmor)((ItemStack)playerGirl.func_184212_Q().func_187225_a(Fighter.PANTS)).func_77973_b())).func_82812_d();
               } else {
                  material = ((ItemArmor)((ItemArmor)((ItemStack)playerGirl.func_184212_Q().func_187225_a(Fighter.SHOES)).func_77973_b())).func_82812_d();
               }
            } catch (ClassCastException var16) {
               System.out.println("couldn't get the armor material");
               material = ArmorMaterial.DIAMOND;
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

         Iterator var18 = bone.childCubes.iterator();

         while(var18.hasNext()) {
            GeoCube cube = (GeoCube)var18.next();
            MATRIX_STACK.push();
            GlStateManager.func_179094_E();
            this.renderCube(builder, cube, red, green, blue, alpha, xOffset);
            GlStateManager.func_179121_F();
            MATRIX_STACK.pop();
         }

         var18 = bone.childBones.iterator();

         while(var18.hasNext()) {
            GeoBone childBone = (GeoBone)var18.next();
            if (xOffset == 0.0D) {
               this.renderRecursively(builder, childBone, red, green, blue, alpha);
            } else {
               this.renderRecursively(builder, childBone, red, green, blue, alpha, xOffset);
            }
         }
      }

      try {
         MATRIX_STACK.pop();
      } catch (IllegalStateException var15) {
      }

   }

   void renderItemInHand(BufferBuilder buffer, GeoBone bone, boolean isOffhand) {
      ItemRenderer itemRenderer = Minecraft.func_71410_x().func_175597_ag();
      GlStateManager.func_179094_E();
      Tessellator.func_178181_a().func_78381_a();
      MatrixHelper.multiplyMatrix(IGeoRenderer.MATRIX_STACK, bone);
      GL11.glEnable(2896);
      GlStateManager.func_179147_l();
      GlStateManager.func_187401_a(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      ItemStack stackUsed = isOffhand ? this.offHandStack : this.mainHandStack;
      switch(stackUsed.func_77973_b().func_77661_b(stackUsed)) {
      case BOW:
         this.transformBow(isOffhand);
         break;
      case BLOCK:
         this.transformShield(isOffhand, this.isUsingItem);
      }

      if (this.isUsingItem && !isOffhand && stackUsed.func_77973_b() instanceof ItemBow) {
         this.pull += 0.015F;
         this.girl.setActiveStackUse(Math.round(-this.pull * 20.0F + (float)stackUsed.func_77988_m()));
         this.girl.setActiveItemStack(stackUsed);
         this.girl.func_184598_c(EnumHand.MAIN_HAND);
         ((PlayerGirl)this.girl).setHandActive();
      } else {
         this.pull = 0.0F;
         this.girl.setActiveStackUse(0);
         this.girl.setActiveItemStack(ItemStack.field_190927_a);
      }

      this.transformItem(isOffhand, stackUsed);
      GlStateManager.func_179152_a(0.75F, 0.75F, 0.75F);
      itemRenderer.func_178099_a(this.girl, stackUsed, TransformType.THIRD_PERSON_RIGHT_HAND);
      buffer.func_181668_a(7, DefaultVertexFormats.field_181712_l);
      this.func_110776_a((ResourceLocation)Objects.requireNonNull(this.getEntityTexture(this.girl)));
      GL11.glDisable(2896);
      GlStateManager.func_179121_F();
      GlStateManager.func_179147_l();
      GlStateManager.func_187401_a(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
   }

   protected void transformItem(boolean offHand, ItemStack stack) {
      GlStateManager.func_179114_b(offHand ? 200.0F : 90.0F, 1.0F, 0.0F, 0.0F);
   }

   protected void transformBow(boolean offHand) {
      GlStateManager.func_179114_b(20.0F, 1.0F, 0.0F, 0.0F);
   }

   protected void transformShield(boolean offHand, boolean active) {
      if (offHand) {
         GlStateManager.func_179114_b(180.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
         if (active) {
            GlStateManager.func_179114_b(-90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.func_179114_b(35.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.func_179114_b(-20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.func_179109_b(0.0F, 0.0F, 0.228F);
         }
      } else if (active) {
         GlStateManager.func_179114_b(-90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179114_b(-90.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.func_179109_b(0.0F, 0.165F, 0.0F);
      }

   }
}

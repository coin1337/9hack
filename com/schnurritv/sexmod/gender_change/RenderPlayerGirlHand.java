package com.schnurritv.sexmod.gender_change;

import com.schnurritv.sexmod.girls.allie.AllieHand;
import com.schnurritv.sexmod.girls.allie.PlayerAllie;
import com.schnurritv.sexmod.girls.base.Hand;
import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirl;
import com.schnurritv.sexmod.girls.bee.BeeHand;
import com.schnurritv.sexmod.girls.bee.PlayerBee;
import com.schnurritv.sexmod.girls.bia.BiaHand;
import com.schnurritv.sexmod.girls.bia.PlayerBia;
import com.schnurritv.sexmod.girls.ellie.EllieHand;
import com.schnurritv.sexmod.girls.ellie.PlayerEllie;
import com.schnurritv.sexmod.girls.jenny.JennyHand;
import com.schnurritv.sexmod.girls.jenny.PlayerJenny;
import com.schnurritv.sexmod.girls.slime.PlayerSlime;
import com.schnurritv.sexmod.girls.slime.SlimeHand;
import java.io.PrintWriter;
import java.io.StringWriter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RenderPlayerGirlHand {
   Minecraft mc;
   float equipmentProcess = 2.0F;
   boolean transitionTicket = false;
   private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
   Hand girlHandModel;
   ResourceLocation girlHandSkin;
   float t = 0.0F;

   @SubscribeEvent
   public void renderHand(RenderSpecificHandEvent event) {
      PlayerGirl.tryPuttingGirlsInTable();
      PlayerGirl girl = (PlayerGirl)PlayerGirl.playerGirlTable.get(Minecraft.func_71410_x().field_71439_g.getPersistentID());
      if (girl != null) {
         if (girl instanceof PlayerBia) {
            this.girlHandModel = new BiaHand();
            this.girlHandSkin = new ResourceLocation("sexmod", "textures/entity/bia/hand.png");
         } else if (girl instanceof PlayerJenny) {
            this.girlHandModel = new JennyHand();
            this.girlHandSkin = new ResourceLocation("sexmod", "textures/entity/jenny/hand.png");
         } else if (girl instanceof PlayerEllie) {
            this.girlHandModel = new EllieHand();
            this.girlHandSkin = new ResourceLocation("sexmod", "textures/entity/ellie/hand.png");
         } else if (girl instanceof PlayerSlime) {
            this.girlHandModel = new SlimeHand();
            this.girlHandSkin = new ResourceLocation("sexmod", "textures/entity/slime/hand.png");
         } else if (girl instanceof PlayerAllie) {
            this.girlHandModel = new AllieHand();
            this.girlHandSkin = new ResourceLocation("sexmod", "textures/entity/allie/hand.png");
         } else if (girl instanceof PlayerBee) {
            this.girlHandModel = new BeeHand();
            this.girlHandSkin = new ResourceLocation("sexmod", "textures/entity/bee/hand.png");
         }

         if (this.girlHandModel != null && this.girlHandSkin != null) {
            this.mc = Minecraft.func_71410_x();
            float prevEquippedProgressMainHand = 0.0F;
            float equippedProgressMainHand = 0.0F;

            try {
               ItemRenderer itemRenderer = this.mc.func_175597_ag();
               if ((Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment")) {
                  prevEquippedProgressMainHand = (Float)ObfuscationReflectionHelper.getPrivateValue(ItemRenderer.class, itemRenderer, "prevEquippedProgressMainHand");
                  equippedProgressMainHand = (Float)ObfuscationReflectionHelper.getPrivateValue(ItemRenderer.class, itemRenderer, "equippedProgressMainHand");
               } else {
                  prevEquippedProgressMainHand = (Float)ObfuscationReflectionHelper.getPrivateValue(ItemRenderer.class, itemRenderer, "field_187470_g");
                  equippedProgressMainHand = (Float)ObfuscationReflectionHelper.getPrivateValue(ItemRenderer.class, itemRenderer, "field_187469_f");
               }

               this.equipmentProcess = 2.0F - (prevEquippedProgressMainHand + (equippedProgressMainHand - prevEquippedProgressMainHand) * event.getPartialTicks());
            } catch (Exception var8) {
               System.out.println("couldnt do the reflection thingy");
               StringWriter errors = new StringWriter();
               var8.printStackTrace(new PrintWriter(errors));
               Minecraft.func_71410_x().field_71439_g.func_71165_d(errors.toString());
            }

            AbstractClientPlayer abstractclientplayer = this.mc.field_71439_g;
            float swingProgress = abstractclientplayer.func_70678_g(event.getPartialTicks());
            ItemStack handStack = this.mc.field_71439_g.func_184614_ca();
            if (event.getHand() == EnumHand.MAIN_HAND) {
               if (!handStack.func_190926_b() && !(handStack.func_77973_b() instanceof ItemMap)) {
                  if (equippedProgressMainHand < prevEquippedProgressMainHand) {
                     if (this.transitionTicket) {
                        event.setCanceled(true);
                        this.renderHands(handStack, event.getPartialTicks(), abstractclientplayer, this.equipmentProcess, swingProgress);
                     }
                  } else {
                     this.transitionTicket = false;
                  }
               } else {
                  event.setCanceled(true);
                  this.renderHands(handStack, event.getPartialTicks(), abstractclientplayer, this.equipmentProcess, swingProgress);
                  this.transitionTicket = true;
               }
            } else if (this.mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemMap) {
               event.setCanceled(true);
               this.renderMapFirstPersonOneHand(EnumHandSide.LEFT, this.equipmentProcess - 1.0F, swingProgress, this.mc.field_71439_g.func_184592_cb());
            }

         } else {
            System.out.println("HAND IS NULL uwu did you forget to assign this girl a hand owo?");
         }
      }
   }

   void renderHands(ItemStack stack, float partialTicks, AbstractClientPlayer player, float equipmentProcess, float swingProcess) {
      if (stack.func_77973_b() instanceof ItemMap) {
         if (player.func_184592_cb().func_190926_b()) {
            this.renderMapFirstPersonBothHands(stack, player, swingProcess, partialTicks);
         } else {
            this.renderMapFirstPersonOneHand(EnumHandSide.RIGHT, equipmentProcess - 1.0F, swingProcess, stack);
         }
      } else {
         this.renderRightHand(swingProcess, partialTicks);
      }

   }

   void renderMapFirstPersonOneHand(EnumHandSide hand, float equipmentProcess, float swingProcess, ItemStack stack) {
      float f = hand == EnumHandSide.RIGHT ? 1.0F : -1.0F;
      GlStateManager.func_179109_b(f * 0.125F, -0.125F, 0.0F);
      if (!this.mc.field_71439_g.func_82150_aj()) {
         GlStateManager.func_179094_E();
         GlStateManager.func_179114_b(f * 10.0F, 0.0F, 0.0F, 1.0F);
         this.transformHand(equipmentProcess, swingProcess, hand);
         GlStateManager.func_179109_b(-0.5F, -1.1F, 0.0F);
         if (hand == EnumHandSide.RIGHT) {
            GlStateManager.func_179109_b(0.48F, 0.15F, 0.0F);
         } else {
            GlStateManager.func_179109_b(0.44F, 1.3F, 1.0F);
         }

         Minecraft.func_71410_x().func_110434_K().func_110577_a(this.girlHandSkin);
         this.girlHandModel.getHandRenderer().func_78785_a(0.175F);
         GlStateManager.func_179121_F();
      }

      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b(f * 0.51F, -0.08F + equipmentProcess * -1.2F, -0.75F);
      float f1 = MathHelper.func_76129_c(swingProcess);
      float f2 = MathHelper.func_76126_a(f1 * 3.1415927F);
      float f3 = -0.5F * f2;
      float f4 = 0.4F * MathHelper.func_76126_a(f1 * 6.2831855F);
      float f5 = -0.3F * MathHelper.func_76126_a(swingProcess * 3.1415927F);
      GlStateManager.func_179109_b(f * f3, f4 - 0.3F * f2, f5);
      GlStateManager.func_179114_b(f2 * -45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179114_b(f * f2 * -30.0F, 0.0F, 1.0F, 0.0F);
      this.renderMapFirstPerson(stack);
      GlStateManager.func_179121_F();
   }

   void renderMapFirstPersonBothHands(ItemStack stack, AbstractClientPlayer player, float swingProcess, float partialTicks) {
      float pitchDrag = player.field_70127_C + (player.field_70125_A - player.field_70127_C) * partialTicks;
      float f = MathHelper.func_76129_c(swingProcess);
      float f1 = -0.2F * MathHelper.func_76126_a(swingProcess * 3.1415927F);
      float f2 = -0.4F * MathHelper.func_76126_a(f * 3.1415927F);
      GlStateManager.func_179109_b(0.0F, -f1 / 2.0F, f2);
      float f3 = this.getMapAngleFromPitch(pitchDrag);
      GlStateManager.func_179109_b(0.0F, 0.04F + (this.equipmentProcess - 1.0F) * -1.2F + f3 * -0.5F, -0.72F);
      GlStateManager.func_179114_b(f3 * -85.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179129_p();
      GlStateManager.func_179094_E();
      GlStateManager.func_179114_b(90.0F, 0.0F, 1.0F, 0.0F);
      this.renderArm(EnumHandSide.RIGHT);
      this.renderArm(EnumHandSide.LEFT);
      GlStateManager.func_179121_F();
      GlStateManager.func_179089_o();
      float f4 = MathHelper.func_76126_a(f * 3.1415927F);
      GlStateManager.func_179114_b(f4 * 20.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179152_a(2.0F, 2.0F, 2.0F);
      this.renderMapFirstPerson(stack);
      GlStateManager.func_179145_e();
   }

   void renderMapFirstPerson(ItemStack stack) {
      GlStateManager.func_179114_b(180.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(180.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.func_179152_a(0.38F, 0.38F, 0.38F);
      GlStateManager.func_179140_f();
      this.mc.func_110434_K().func_110577_a(RES_MAP_BACKGROUND);
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      GlStateManager.func_179109_b(-0.5F, -0.5F, 0.0F);
      GlStateManager.func_179152_a(0.0078125F, 0.0078125F, 0.0078125F);
      bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      bufferbuilder.func_181662_b(-7.0D, 135.0D, 0.0D).func_187315_a(0.0D, 1.0D).func_181675_d();
      bufferbuilder.func_181662_b(135.0D, 135.0D, 0.0D).func_187315_a(1.0D, 1.0D).func_181675_d();
      bufferbuilder.func_181662_b(135.0D, -7.0D, 0.0D).func_187315_a(1.0D, 0.0D).func_181675_d();
      bufferbuilder.func_181662_b(-7.0D, -7.0D, 0.0D).func_187315_a(0.0D, 0.0D).func_181675_d();
      tessellator.func_78381_a();
      MapData mapdata = ((ItemMap)stack.func_77973_b()).func_77873_a(stack, this.mc.field_71441_e);
      if (mapdata != null) {
         this.mc.field_71460_t.func_147701_i().func_148250_a(mapdata, false);
      }

   }

   private void renderArm(EnumHandSide p_187455_1_) {
      GlStateManager.func_179094_E();
      float f = p_187455_1_ == EnumHandSide.RIGHT ? 1.0F : -1.0F;
      GlStateManager.func_179114_b(92.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179114_b(f * -41.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.func_179109_b(f * 0.3F, -1.1F, 0.45F);
      if (p_187455_1_ == EnumHandSide.RIGHT) {
         GlStateManager.func_179109_b(0.63F, 0.36F, 0.0F);
      } else {
         GlStateManager.func_179109_b(1.6F, 0.35F, 0.0F);
      }

      Minecraft.func_71410_x().func_110434_K().func_110577_a(this.girlHandSkin);
      this.girlHandModel.getHandRenderer().func_78785_a(0.175F);
      GlStateManager.func_179121_F();
   }

   private float getMapAngleFromPitch(float pitch) {
      float f = 1.0F - pitch / 45.0F + 0.1F;
      f = MathHelper.func_76131_a(f, 0.0F, 1.0F);
      f = -MathHelper.func_76134_b(f * 3.1415927F) * 0.5F + 0.5F;
      return f;
   }

   void renderRightHand(float swingProgress, float partialTicks) {
      GlStateManager.func_179129_p();
      GlStateManager.func_179094_E();
      this.transformHand(this.equipmentProcess, swingProgress, EnumHandSide.RIGHT);
      Minecraft.func_71410_x().func_110434_K().func_110577_a(this.girlHandSkin);
      this.girlHandModel.getHandRenderer().func_78785_a(0.175F);
      GlStateManager.func_179084_k();
      GlStateManager.func_179089_o();
      GlStateManager.func_179121_F();
   }

   private void transformHand(float equipmentProcess, float swingProcess, EnumHandSide handSide) {
      boolean flag = handSide != EnumHandSide.LEFT;
      float f = flag ? 1.0F : -1.0F;
      float f1 = MathHelper.func_76129_c(swingProcess);
      float f2 = -0.3F * MathHelper.func_76126_a(f1 * 3.1415927F);
      float f3 = 0.4F * MathHelper.func_76126_a(f1 * 6.2831855F);
      float f4 = -0.4F * MathHelper.func_76126_a(swingProcess * 3.1415927F);
      GlStateManager.func_179109_b(f * (f2 + 0.64000005F), f3 + -0.6F + equipmentProcess * -0.6F, f4 + -0.71999997F);
      GlStateManager.func_179114_b(f * 45.0F, 0.0F, 1.0F, 0.0F);
      float f5 = MathHelper.func_76126_a(swingProcess * swingProcess * 3.1415927F);
      float f6 = MathHelper.func_76126_a(f1 * 3.1415927F);
      GlStateManager.func_179114_b(f * f6 * 70.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(f * f5 * -20.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.func_179109_b(f * -1.0F, 3.6F, 3.5F);
      GlStateManager.func_179114_b(f * 120.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.func_179114_b(200.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179114_b(f * -135.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179109_b(f * 5.6F, 0.0F, 0.0F);
      GlStateManager.func_179109_b(0.5F, 1.1F, 0.0F);
   }
}

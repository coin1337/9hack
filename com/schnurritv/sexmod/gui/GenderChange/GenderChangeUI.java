package com.schnurritv.sexmod.gui.GenderChange;

import com.schnurritv.sexmod.Packets.UpdatePlayerModel;
import com.schnurritv.sexmod.girls.allie.AllieEntity;
import com.schnurritv.sexmod.girls.bee.BeeEntity;
import com.schnurritv.sexmod.girls.bia.BiaEntity;
import com.schnurritv.sexmod.girls.ellie.EllieEntity;
import com.schnurritv.sexmod.girls.jenny.JennyEntity;
import com.schnurritv.sexmod.girls.slime.SlimeEntity;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;

public class GenderChangeUI extends GuiScreen {
   EntityLivingBase[] entities;
   int i = 0;
   static float rot = 0.0F;

   public void func_73866_w_() {
      this.entities = new EntityLivingBase[]{new JennyEntity(this.field_146297_k.field_71441_e), new EllieEntity(this.field_146297_k.field_71441_e), new BiaEntity(this.field_146297_k.field_71441_e), new SlimeEntity(this.field_146297_k.field_71441_e), new BeeEntity(this.field_146297_k.field_71441_e), new AllieEntity(this.field_146297_k.field_71441_e), this.field_146297_k.field_71439_g};
   }

   public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
      super.func_73863_a(mouseX, mouseY, partialTicks);
      this.field_146292_n.clear();
      drawEntityOnScreen(this.field_146294_l / 2, this.field_146295_m / 2 + 20, 30, this.entities[this.i]);
      this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 + 30, this.field_146295_m / 2 - 10, 20, 20, ">"));
      this.field_146292_n.add(new GuiButton(2, this.field_146294_l / 2 - 50, this.field_146295_m / 2 - 10, 20, 20, "<"));
      this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 - 30, this.field_146295_m / 2 + 30, 60, 20, "pick"));
   }

   protected void func_146284_a(GuiButton button) throws IOException {
      if (">".equals(button.field_146126_j) && ++this.i >= this.entities.length) {
         this.i = 0;
      }

      if ("<".equals(button.field_146126_j) && --this.i < 0) {
         this.i = this.entities.length - 1;
      }

      if (button.field_146127_k == 0) {
         PacketHandler.INSTANCE.sendToServer(new UpdatePlayerModel(this.i));
         Minecraft.func_71410_x().field_71439_g.func_71053_j();
      }

   }

   public boolean func_73868_f() {
      return false;
   }

   public static void drawEntityOnScreen(int posX, int posY, int scale, EntityLivingBase ent) {
      float f = ent.field_70761_aq;
      float f1 = ent.field_70177_z;
      float f2 = ent.field_70125_A;
      float f3 = ent.field_70758_at;
      float f4 = ent.field_70759_as;
      ent.field_70761_aq = 0.0F;
      ent.field_70177_z = 0.0F;
      ent.field_70125_A = 0.0F;
      ent.field_70758_at = 0.0F;
      ent.field_70759_as = 0.0F;
      rot += 60.0F;
      GlStateManager.func_179142_g();
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b((float)posX, (float)posY, 50.0F);
      GlStateManager.func_179152_a((float)(-scale), (float)scale, (float)scale);
      GlStateManager.func_179114_b(180.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.func_179114_b(135.0F, 0.0F, 1.0F, 0.0F);
      RenderHelper.func_74519_b();
      GlStateManager.func_179114_b(-135.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(rot / (float)Minecraft.func_175610_ah(), 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179109_b(0.0F, 0.0F, 0.0F);
      RenderManager rendermanager = Minecraft.func_71410_x().func_175598_ae();
      rendermanager.func_178631_a(180.0F);
      rendermanager.func_178633_a(false);
      rendermanager.func_188391_a(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.2345679F, false);
      rendermanager.func_178633_a(true);
      GlStateManager.func_179121_F();
      RenderHelper.func_74518_a();
      GlStateManager.func_179101_C();
      GlStateManager.func_179138_g(OpenGlHelper.field_77476_b);
      GlStateManager.func_179090_x();
      GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
      ent.field_70761_aq = f;
      ent.field_70177_z = f1;
      ent.field_70125_A = f2;
      ent.field_70758_at = f3;
      ent.field_70759_as = f4;
   }
}

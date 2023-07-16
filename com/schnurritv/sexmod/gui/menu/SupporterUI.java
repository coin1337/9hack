package com.schnurritv.sexmod.gui.menu;

import com.schnurritv.sexmod.Packets.BeeOpenChest;
import com.schnurritv.sexmod.Packets.ChangeDataParameter;
import com.schnurritv.sexmod.Packets.SendCompanionHome;
import com.schnurritv.sexmod.Packets.SetNewHome;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.base.Supporter;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;

public class SupporterUI extends GuiScreen {
   Supporter girl;
   EntityPlayer player;
   boolean isFollowing;
   static final ResourceLocation ITEMS_BACKGROUND = new ResourceLocation("sexmod", "textures/gui/girlinventory.png");
   double mu = 0.0D;

   public SupporterUI(Supporter girl, EntityPlayer player) {
      this.girl = girl;
      this.player = player;
      this.isFollowing = !"".equals(girl.func_184212_Q().func_187225_a(GirlEntity.MASTER));
   }

   public boolean func_73868_f() {
      return false;
   }

   public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
      super.func_73863_a(mouseX, mouseY, partialTicks);
      this.field_146292_n.clear();
      ScaledResolution resolution = new ScaledResolution(this.field_146297_k);
      int screenWidth = resolution.func_78326_a();
      this.mu = Math.min(1.0D, this.mu + (double)(this.field_146297_k.func_193989_ak() / 5.0F));
      this.field_146292_n.add(new GuiButton(0, screenWidth / 2 - 119 + (int)(100.0D - 100.0D * this.mu), 30, (int)(this.mu * 100.0D), 20, this.isFollowing ? I18n.func_135052_a("action.names.stopfollowme", new Object[0]) : I18n.func_135052_a("action.names.followme", new Object[0])));
      this.field_146292_n.add(new GuiButton(1, screenWidth / 2 + 19, 30, (int)(this.mu * 100.0D), 20, I18n.func_135052_a("action.names.gohome", new Object[0])));
      this.field_146297_k.field_71446_o.func_110577_a(ITEMS_BACKGROUND);
      this.func_73729_b(screenWidth / 2 - 7, 61 - (int)(15.0D - this.mu * 15.0D), 32, 0, 15, 15);
      this.field_146292_n.add(new GuiButton(2, screenWidth / 2 - 10, 59 - (int)(15.0D - this.mu * 15.0D), 20, 20, ""));
      this.func_73729_b(screenWidth / 2 - 20, 20, (Boolean)this.girl.func_184212_Q().func_187225_a(Supporter.HAS_CHEST) ? 0 : 40, 130, 40, 40);
   }

   protected void func_73864_a(int mouseX, int mouseY, int mouseButton) throws IOException {
      ScaledResolution resolution = new ScaledResolution(this.field_146297_k);
      int screenWidth = resolution.func_78326_a();
      if ((Boolean)this.girl.func_184212_Q().func_187225_a(Supporter.HAS_CHEST) && mouseX >= screenWidth / 2 - 20 && mouseX <= screenWidth / 2 + 20 && mouseY >= 20 && mouseY <= 60) {
         PacketHandler.INSTANCE.sendToServer(new BeeOpenChest(this.girl.girlId(), this.player.getPersistentID()));
         this.func_146281_b();
      }

      super.func_73864_a(mouseX, mouseY, mouseButton);
   }

   protected void func_146284_a(GuiButton button) throws IOException {
      super.func_146284_a(button);
      if (button.field_146127_k == 0) {
         if (this.isFollowing) {
            PacketHandler.INSTANCE.sendToServer(new ChangeDataParameter(this.girl.girlId(), "master", ""));
            this.player.func_145747_a(new TextComponentString(I18n.func_135052_a("bee.dialogue.sad", new Object[0])));
         } else {
            PacketHandler.INSTANCE.sendToServer(new ChangeDataParameter(this.girl.girlId(), "master", this.player.getPersistentID().toString()));
            this.player.func_145747_a(new TextComponentString(I18n.func_135052_a("bee.dialogue.exited", new Object[0])));
         }

         this.isFollowing = !this.isFollowing;
         this.player.func_71053_j();
      }

      if (button.field_146127_k == 1) {
         PacketHandler.INSTANCE.sendToServer(new SendCompanionHome(this.girl.girlId()));
         this.player.func_71053_j();
      }

      if (button.field_146127_k == 2) {
         PacketHandler.INSTANCE.sendToServer(new SetNewHome(this.girl.girlId(), new Vec3d(this.girl.field_70165_t, this.girl.field_70163_u, this.girl.field_70161_v)));
         this.player.func_71053_j();
         this.player.func_145747_a(new TextComponentString(I18n.func_135052_a("bee.dialogue.home", new Object[0])));
      }

   }
}

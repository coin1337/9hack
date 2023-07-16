package com.schnurritv.sexmod.gui.menu;

import com.schnurritv.sexmod.Packets.UploadInventoryToServer;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GirlInventoryUI extends GuiContainer {
   static final ResourceLocation ITEMS_BACKGROUND = new ResourceLocation("sexmod", "textures/gui/girlinventory.png");
   UUID containerUUID;
   GirlEntity girl;
   UUID playerUUID;

   public GirlInventoryUI(GirlEntity girl, InventoryPlayer inventoryPlayer, UUID containerUUID) {
      super(new GirlContainer(girl, inventoryPlayer, containerUUID));
      this.containerUUID = containerUUID;
      this.girl = girl;
      this.playerUUID = inventoryPlayer.field_70458_d.getPersistentID();
   }

   public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
      this.func_146276_q_();
      super.func_73863_a(mouseX, mouseY, partialTicks);
      this.func_191948_b(mouseX, mouseY);
   }

   public void func_146281_b() {
      super.func_146281_b();
      GirlContainer rightContainer = null;
      Iterator var2 = GirlContainer.list.iterator();

      while(var2.hasNext()) {
         GirlContainer container = (GirlContainer)var2.next();
         if (container.containerId.equals(this.containerUUID)) {
            ItemStack[] stacks = new ItemStack[42];
            Minecraft.func_71410_x().field_71439_g.field_71071_by.field_70462_a.toArray(stacks);
            stacks[36] = container.func_75139_a(0).func_75211_c();
            stacks[37] = container.func_75139_a(1).func_75211_c();
            stacks[38] = container.func_75139_a(2).func_75211_c();
            stacks[39] = container.func_75139_a(3).func_75211_c();
            stacks[40] = container.func_75139_a(4).func_75211_c();
            stacks[41] = container.func_75139_a(5).func_75211_c();
            PacketHandler.INSTANCE.sendToServer(new UploadInventoryToServer(this.girl.girlId(), this.playerUUID, stacks));
         }
      }

   }

   protected void func_146976_a(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.field_71446_o.func_110577_a(ITEMS_BACKGROUND);
      this.func_73729_b(this.field_146294_l / 2 - 88, this.field_146295_m / 2 - 7 - 24, 33, 16, 176, 114);
   }
}

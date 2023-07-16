package com.schnurritv.sexmod.gui.menu;

import com.schnurritv.sexmod.Packets.RemoveItems;
import com.schnurritv.sexmod.girls.base.Fighter;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.util.PenisMath;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import com.schnurritv.sexmod.util.Handlers.SoundsHandler;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;

public class FighterUI extends GuiScreen {
   GirlEntity girl;
   EntityPlayer player;
   String[] actions;
   @Nullable
   ItemStack[] prices;
   static final ResourceLocation ITEMS_BACKGROUND = new ResourceLocation("sexmod", "textures/gui/girlinventory.png");
   EntityDataManager dataManager;
   boolean hasChosen = false;
   final boolean DRAW_EQUIPMENT;
   float firstTransition = 0.0F;
   float secondTransition = 0.0F;
   String[] companionButtonTexts = new String[]{"action.names.followme", "action.names.stopfollowme", "action.names.gohome", "action.names.setnewhome", "action.names.equipment"};
   int[] extraButtonWidth = new int[]{0, 0, 0, 0, 0};
   int[] textureXOffset = new int[]{64, 80, 47, 32, 96};
   int[] spaces = new int[]{4, 4, 5, 5, 4};
   int[] sizes = new int[]{50, 90, 50, 80, 60};

   public FighterUI(GirlEntity girl, EntityPlayer player, String[] actions, @Nullable ItemStack[] prices, boolean drawEquipment) {
      this.girl = girl;
      this.player = player;
      this.actions = actions;
      this.prices = prices;
      this.DRAW_EQUIPMENT = drawEquipment;
      this.dataManager = girl.func_184212_Q();
   }

   public FighterUI(GirlEntity girl, EntityPlayer player, String[] actions, boolean drawEquipment) {
      this.girl = girl;
      this.player = player;
      this.actions = actions;
      this.DRAW_EQUIPMENT = drawEquipment;
      this.dataManager = girl.func_184212_Q();
   }

   public boolean func_73868_f() {
      return false;
   }

   protected void func_146284_a(GuiButton button) {
      this.hasChosen = true;
      if (this.prices != null && button.field_146127_k >= 5 && !this.player.field_71075_bZ.field_75098_d) {
         Iterator var2 = this.player.field_71071_by.field_70462_a.iterator();

         ItemStack item;
         do {
            if (!var2.hasNext()) {
               this.player.func_145747_a(new TextComponentString("<" + this.girl.func_70005_c_() + "> you cannot afford that..."));
               this.girl.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_SADOH[1]);
               return;
            }

            item = (ItemStack)var2.next();
         } while(!item.func_77973_b().equals(this.prices[button.field_146127_k - 5].func_77973_b()) || item.func_190916_E() < this.prices[button.field_146127_k - 5].func_190916_E() || item.func_77960_j() != this.prices[button.field_146127_k - 5].func_77960_j());

         PacketHandler.INSTANCE.sendToServer(new RemoveItems(this.player.getPersistentID(), this.prices[button.field_146127_k - 5]));
         this.doAction(button);
      } else {
         this.doAction(button);
      }
   }

   void doAction(GuiButton button) {
      String text;
      if (button.field_146127_k < 5) {
         text = this.companionButtonTexts[button.field_146127_k];
      } else {
         text = this.actions[button.field_146127_k - 5];
      }

      this.girl.doAction(text, this.player.getPersistentID());
      Minecraft.func_71410_x().field_71439_g.func_71053_j();
   }

   public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
      super.func_73863_a(mouseX, mouseY, partialTicks);
      this.field_146292_n.clear();
      ScaledResolution resolution = new ScaledResolution(this.field_146297_k);
      int screenWidth = resolution.func_78326_a();
      int screenHeight = resolution.func_78328_b();
      this.firstTransition = Math.min(1.0F, this.firstTransition + this.field_146297_k.func_193989_ak() / 5.0F);
      if (this.firstTransition == 1.0F) {
         this.secondTransition = Math.min(1.0F, this.secondTransition + this.field_146297_k.func_193989_ak() / 5.0F);
      }

      int xText = (int)PenisMath.Lerp(115.0D, 161.0D, (double)this.secondTransition);
      int xItem = (int)PenisMath.Lerp(91.0D, 137.0D, (double)this.secondTransition);
      int x = (int)PenisMath.Lerp(-30.0D, 120.0D, (double)this.firstTransition);
      int y = 70;
      int yText = 52;
      int yItem = 68;

      for(int i = 5; i < this.actions.length + 5; ++i) {
         if (this.secondTransition > 0.0F && this.prices != null && this.prices[i - 5].func_190916_E() != 0) {
            this.field_73735_i = -300.0F;
            this.field_146296_j.field_77023_b = -300.0F;
            this.drawHoveringTextWithZ(Arrays.asList(this.prices[i - 5].func_190916_E() + "x    "), screenWidth - xText, screenHeight - yText, this.field_146289_q);
            this.field_146296_j.func_175042_a(this.prices[i - 5], screenWidth - xItem, screenHeight - yItem);
            this.field_73735_i = 0.0F;
            this.field_146296_j.field_77023_b = 0.0F;
         }

         this.field_146292_n.add(new GuiButton(i, screenWidth - x, screenHeight - y, 100, 20, I18n.func_135052_a(this.actions[i - 5], new Object[0])));
         y += 30;
         yText += 30;
         yItem += 30;
      }

      if (this.DRAW_EQUIPMENT) {
         this.drawEquipment(mouseX, mouseY);
      }

   }

   void drawEquipment(int mouseX, int mouseY) {
      int x = (int)PenisMath.Lerp(-30.0D, 120.0D, (double)this.firstTransition);
      this.field_146296_j.func_175042_a((ItemStack)this.dataManager.func_187225_a(Fighter.WEAPON), x - 105, 68);
      this.field_146296_j.func_175042_a((ItemStack)this.dataManager.func_187225_a(Fighter.BOW), x - 105, 87);
      this.field_146296_j.func_175042_a((ItemStack)this.dataManager.func_187225_a(Fighter.HELMET), x - 105, 109);
      this.field_146296_j.func_175042_a((ItemStack)this.dataManager.func_187225_a(Fighter.CHEST_PLATE), x - 105, 127);
      this.field_146296_j.func_175042_a((ItemStack)this.dataManager.func_187225_a(Fighter.PANTS), x - 105, 146);
      this.field_146296_j.func_175042_a((ItemStack)this.dataManager.func_187225_a(Fighter.SHOES), x - 105, 166);
      if (this.secondTransition != 0.0F) {
         boolean hasMaster = !((String)this.dataManager.func_187225_a(GirlEntity.MASTER)).equals("");
         int buttonX = 35;
         int buttonY = 70;

         for(int i = 0; i < 5; ++i) {
            if (i == 0 && hasMaster) {
               i = 1;
            } else if (i == 1 && !hasMaster) {
               i = 2;
            }

            if (mouseX >= buttonX && mouseX <= buttonX + 23 + this.extraButtonWidth[i] && mouseY >= buttonY && mouseY <= buttonY + 20) {
               this.extraButtonWidth[i] = Math.min(this.sizes[i], this.extraButtonWidth[i] + 7);
            } else {
               this.extraButtonWidth[i] = Math.max(0, this.extraButtonWidth[i] - 7);
            }

            StringBuilder buttonText = new StringBuilder(I18n.func_135052_a(this.companionButtonTexts[i], new Object[0]));

            for(int i2 = 0; i2 < this.spaces[i]; ++i2) {
               buttonText.append(" ");
            }

            this.field_146297_k.field_71446_o.func_110577_a(ITEMS_BACKGROUND);
            this.func_73729_b(this.extraButtonWidth[i] + buttonX - 18 + (int)PenisMath.Lerp(0.0D, 23.0D, (double)this.secondTransition), buttonY + 2, this.textureXOffset[i], 0, 16, 16);
            this.field_146292_n.add(new GuiButton(i, buttonX + 1, buttonY, (int)(PenisMath.Lerp(0.0D, 23.0D, (double)this.secondTransition) + (double)this.extraButtonWidth[i]), 20, this.extraButtonWidth[i] <= 14 ? "" : buttonText.toString()));
            buttonY += 30;
         }

         this.field_146297_k.field_71446_o.func_110577_a(ITEMS_BACKGROUND);
         this.func_73729_b(x - 113, 60, 0, 0, 32, 130);
      }
   }

   void drawHoveringTextWithZ(List<String> textLines, int x, int y, FontRenderer font) {
      GlStateManager.func_179101_C();
      RenderHelper.func_74518_a();
      GlStateManager.func_179140_f();
      int i = 0;
      Iterator var6 = textLines.iterator();

      int k;
      while(var6.hasNext()) {
         String s = (String)var6.next();
         k = this.field_146289_q.func_78256_a(s);
         if (k > i) {
            i = k;
         }
      }

      int l1 = x + 12;
      int i2 = y - 12;
      k = 8;
      if (textLines.size() > 1) {
         k += 2 + (textLines.size() - 1) * 10;
      }

      if (l1 + i > this.field_146294_l) {
         l1 -= 28 + i;
      }

      if (i2 + k + 6 > this.field_146295_m) {
         i2 = this.field_146295_m - k - 6;
      }

      this.func_73733_a(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, -267386864, -267386864);
      this.func_73733_a(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, -267386864, -267386864);
      this.func_73733_a(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, -267386864, -267386864);
      this.func_73733_a(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, -267386864, -267386864);
      this.func_73733_a(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, -267386864, -267386864);
      this.func_73733_a(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, 1347420415, 1344798847);
      this.func_73733_a(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, 1347420415, 1344798847);
      this.func_73733_a(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, 1347420415, 1347420415);
      this.func_73733_a(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, 1344798847, 1344798847);

      for(int k1 = 0; k1 < textLines.size(); ++k1) {
         String s1 = (String)textLines.get(k1);
         this.field_146289_q.func_175063_a(s1, (float)l1, (float)i2, -1);
         if (k1 == 0) {
            i2 += 2;
         }

         i2 += 10;
      }

      GlStateManager.func_179145_e();
      RenderHelper.func_74519_b();
      GlStateManager.func_179091_B();
   }
}

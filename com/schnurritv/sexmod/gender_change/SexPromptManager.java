package com.schnurritv.sexmod.gender_change;

import com.schnurritv.sexmod.Packets.StartStandingSexAnimation;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SexPromptManager {
   public static SexPromptManager INSTANCE;
   private SexPromptManager.SexPrompt activePrompt;

   public void tick() {
      if (INSTANCE.activePrompt != null) {
         if (--INSTANCE.activePrompt.timer <= 0.0F) {
            Minecraft.func_71410_x().field_71439_g.func_145747_a(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.func_135052_a("genderswap.sexpromt.timeout", new Object[0])));
            this.deletePrompt();
         }

      }
   }

   public SexPromptManager.SexPrompt getActivePrompt() {
      return INSTANCE.activePrompt;
   }

   void deletePrompt() {
      INSTANCE.activePrompt = null;
   }

   public void setNewActivePrompt(@Nonnull SexPromptManager.SexPrompt activePrompt) {
      World world = Minecraft.func_71410_x().field_71439_g.field_70170_p;
      EntityPlayer girl = world.func_152378_a(activePrompt.female);
      EntityPlayer male = world.func_152378_a(activePrompt.male);
      if (male != null && girl != null) {
         TextComponentString message1 = new TextComponentString(TextFormatting.LIGHT_PURPLE + (activePrompt.senderIsMale ? male.func_70005_c_() : girl.func_70005_c_()) + " " + TextFormatting.DARK_PURPLE + I18n.func_135052_a("genderswap.sexpromt.playerxaskedfory", new Object[0]) + " " + TextFormatting.LIGHT_PURPLE + I18n.func_135052_a(activePrompt.action, new Object[0]));
         TextComponentString message2 = new TextComponentString(TextFormatting.DARK_PURPLE + I18n.func_135052_a("genderswap.sexpromt.autodeletion", new Object[0]));
         TextComponentString message3 = new TextComponentString(TextFormatting.DARK_PURPLE + "[ " + TextFormatting.LIGHT_PURPLE + I18n.func_135052_a("genderswap.sexpromt.accept", new Object[0]) + TextFormatting.DARK_PURPLE + " | " + TextFormatting.LIGHT_PURPLE + I18n.func_135052_a("genderswap.sexpromt.decline", new Object[0]) + TextFormatting.DARK_PURPLE + " ]");
         girl.func_145747_a(message1);
         girl.func_145747_a(message2);
         girl.func_145747_a(message3);
         this.activePrompt = activePrompt;
      }
   }

   @SubscribeEvent
   public void answer(ClientChatEvent event) {
      if (INSTANCE.getActivePrompt() != null) {
         String msg = event.getMessage().toLowerCase();
         if (msg.equals(I18n.func_135052_a("genderswap.sexpromt.accept", new Object[0]).toLowerCase())) {
            SexPromptManager.SexPrompt prompt = INSTANCE.getActivePrompt();
            this.startSex(prompt.action, prompt.female, prompt.male);
            this.deletePrompt();
            event.setCanceled(true);
         }

         if (msg.equals(I18n.func_135052_a("genderswap.sexpromt.decline", new Object[0]).toLowerCase())) {
            Minecraft.func_71410_x().field_71439_g.func_145747_a(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.func_135052_a("genderswap.sexpromt.declineconformation", new Object[0])));
            this.deletePrompt();
            event.setCanceled(true);
         }

      }
   }

   void startSex(String action, UUID female, UUID male) {
      PacketHandler.INSTANCE.sendToServer(new StartStandingSexAnimation(female, male, action));
      if (INSTANCE.activePrompt.senderIsMale) {
         Minecraft.func_71410_x().field_71474_y.field_74320_O = 1;
      }

   }

   public static class SexPrompt {
      public String action;
      public UUID male;
      public UUID female;
      public float timer;
      boolean senderIsMale;

      public SexPrompt(String action, UUID male, UUID female, boolean senderIsMale) {
         this.action = action;
         this.male = male;
         this.female = female;
         this.timer = 1200.0F;
         this.senderIsMale = senderIsMale;
      }
   }
}

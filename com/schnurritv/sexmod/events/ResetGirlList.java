package com.schnurritv.sexmod.events;

import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirl;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ResetGirlList {
   @SubscribeEvent
   public void resetGirlList(GuiOpenEvent event) {
      if (event.getGui() instanceof GuiMainMenu || event.getGui() instanceof GuiMultiplayer) {
         GirlEntity.girlEntities.clear();
         PlayerGirl.toBePutInTable.clear();
         PlayerGirl.playerGirlTable.clear();
      }

   }
}

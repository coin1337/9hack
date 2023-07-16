package com.schnurritv.sexmod.events;

import com.schnurritv.sexmod.girls.base.GirlEntity;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent.Pre;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DontRenderPlayersThatHaveSex {
   @SubscribeEvent
   public void RenderPlayer(Pre event) {
      Iterator var2 = GirlEntity.girlEntities.iterator();

      GirlEntity girl;
      EntityPlayer player;
      do {
         do {
            do {
               do {
                  if (!var2.hasNext()) {
                     return;
                  }

                  girl = (GirlEntity)var2.next();
               } while(girl.playerSheHasSexWith() == null);
            } while(girl.currentAction() == null);

            player = event.getEntityPlayer();
         } while(!girl.playerSheHasSexWith().equals(player.getPersistentID()) && !girl.playerSheHasSexWith().equals(player.func_110124_au()));
      } while(!girl.currentAction().hasPlayer);

      event.setCanceled(true);
   }

   @SubscribeEvent
   public void RenderHand(RenderHandEvent event) {
      Iterator var2 = GirlEntity.girlEntities.iterator();

      GirlEntity girl;
      EntityPlayerSP player;
      do {
         do {
            do {
               do {
                  if (!var2.hasNext()) {
                     return;
                  }

                  girl = (GirlEntity)var2.next();
               } while(girl.playerSheHasSexWith() == null);
            } while(girl.currentAction() == null);

            player = Minecraft.func_71410_x().field_71439_g;
         } while(!girl.playerSheHasSexWith().equals(player.func_110124_au()) && !girl.playerSheHasSexWith().equals(player.getPersistentID()));
      } while(!girl.currentAction().hasPlayer);

      event.setCanceled(true);
   }
}

package com.schnurritv.sexmod.events;

import com.schnurritv.sexmod.Packets.ResetGirl;
import com.schnurritv.sexmod.Packets.SetPlayerMovement;
import com.schnurritv.sexmod.girls.allie.AllieEntity;
import com.schnurritv.sexmod.girls.allie.lamp.LampItem;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirl;
import com.schnurritv.sexmod.girls.bia.PlayerBia;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class ResetDisconnects {
   static final UUID LAPIS = UUID.fromString("b91e6484-8911-4def-ab04-9fa3452fca5f");

   @SubscribeEvent
   public void resetJoiningPlayer(PlayerLoggedInEvent event) {
      EntityPlayerMP player = event.player.field_70170_p.func_73046_m().func_184103_al().func_177451_a(event.player.getPersistentID());
      player.func_82142_c(false);
      if (!player.field_71075_bZ.field_75098_d && player.field_71075_bZ.field_75100_b) {
         player.field_71075_bZ.field_75100_b = false;
      }

      PacketHandler.INSTANCE.sendTo(new SetPlayerMovement(true), player);
      Iterator var3 = GirlEntity.girlEntities.iterator();

      while(var3.hasNext()) {
         GirlEntity girl = (GirlEntity)var3.next();
         if (girl instanceof AllieEntity) {
            event.player.field_70170_p.func_72900_e(girl);
         }
      }

      var3 = player.field_71071_by.field_70462_a.iterator();

      while(var3.hasNext()) {
         ItemStack stack = (ItemStack)var3.next();
         if (stack.func_77973_b() == LampItem.LAMP_ITEM && stack.func_77942_o()) {
            stack.func_77978_p().func_186854_a("user", UUID.randomUUID());
         }
      }

      PlayerGirl girl = (PlayerGirl)PlayerGirl.playerGirlTable.get(event.player.getPersistentID());
      if (girl != null) {
         girl.setCurrentAction((GirlEntity.Action)null);
         ResetGirl.Handler.resetGirl(girl);
      }

      if (event.player.getPersistentID().equals(LAPIS)) {
         if (girl == null) {
            PlayerGirl girl = new PlayerBia(event.player.field_70170_p, LAPIS);
            girl.func_189654_d(true);
            girl.field_70145_X = true;
            girl.field_70159_w = 0.0D;
            girl.field_70181_x = 0.0D;
            girl.field_70179_y = 0.0D;
            girl.func_70107_b(player.field_70165_t, player.field_70163_u + 69.0D, player.field_70161_v);
            event.player.field_70170_p.func_72838_d(girl);
            girl.onCreation();
         }
      }
   }

   @SubscribeEvent
   public void resetGirls(PlayerLoggedOutEvent event) {
      Iterator var2 = GirlEntity.girlEntities.iterator();

      while(true) {
         while(var2.hasNext()) {
            GirlEntity girl = (GirlEntity)var2.next();
            if (girl instanceof AllieEntity && (girl.playerSheHasSexWith() == null || girl.playerSheHasSexWith().equals(event.player.getPersistentID()))) {
               event.player.field_70170_p.func_72900_e(girl);
            } else if (girl.playerSheHasSexWith() != null) {
               if (girl.playerSheHasSexWith().equals(event.player.getPersistentID()) || girl.playerSheHasSexWith().equals(event.player.func_110124_au())) {
                  ResetGirl.Handler.resetGirl(girl);
                  girl.setCurrentAction((GirlEntity.Action)null);
               }

               if (girl instanceof PlayerGirl && ((PlayerGirl)girl).getOwner().equals(event.player.getPersistentID())) {
                  EntityPlayerMP player = (EntityPlayerMP)event.player.field_70170_p.func_152378_a(girl.playerSheHasSexWith());
                  PacketHandler.INSTANCE.sendTo(new SetPlayerMovement(true), player);
                  ResetGirl.Handler.resetPlayer(player);
                  player.func_82142_c(false);
                  girl.setPlayer((UUID)null);
               }
            }
         }

         return;
      }
   }
}

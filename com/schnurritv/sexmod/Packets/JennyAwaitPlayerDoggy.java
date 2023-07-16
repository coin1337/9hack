package com.schnurritv.sexmod.Packets;

import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.ellie.EllieEntity;
import com.schnurritv.sexmod.girls.jenny.JennyEntity;
import com.schnurritv.sexmod.util.Reference;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class JennyAwaitPlayerDoggy implements IMessage {
   boolean messageValid;
   UUID girlUUID;
   UUID playerUUID;

   public JennyAwaitPlayerDoggy() {
      this.messageValid = false;
   }

   public JennyAwaitPlayerDoggy(UUID girlUUID, UUID playerUUID) {
      this.girlUUID = girlUUID;
      this.playerUUID = playerUUID;
      this.messageValid = true;
   }

   public void fromBytes(ByteBuf buf) {
      this.girlUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
      this.playerUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.girlUUID.toString());
      ByteBufUtils.writeUTF8String(buf, this.playerUUID.toString());
   }

   public static class Handler implements IMessageHandler<JennyAwaitPlayerDoggy, IMessage> {
      public IMessage onMessage(JennyAwaitPlayerDoggy message, MessageContext ctx) {
         if (message.messageValid && ctx.side == Side.SERVER) {
            ArrayList<GirlEntity> girlList = GirlEntity.getGirlsByUUID(message.girlUUID);
            Iterator var4 = girlList.iterator();

            while(true) {
               GirlEntity girl;
               while(true) {
                  if (!var4.hasNext()) {
                     return null;
                  }

                  girl = (GirlEntity)var4.next();
                  PlayerList playerList = Reference.server.func_184103_al();

                  try {
                     playerList.func_177451_a(message.playerUUID).func_70005_c_();
                     break;
                  } catch (NullPointerException var10) {
                     System.out.println("couldn't find player with UUID: " + message.playerUUID);
                     System.out.println("could only find players with thsese UUID's:");
                     Iterator var8 = playerList.func_181057_v().iterator();

                     while(var8.hasNext()) {
                        EntityPlayerMP player = (EntityPlayerMP)var8.next();
                        System.out.println(player.func_70005_c_() + " " + player.func_110124_au());
                     }
                  }
               }

               if (girl instanceof JennyEntity) {
                  ((JennyEntity)girl).awaitPlayer = true;
               } else if (girl instanceof EllieEntity) {
                  ((EllieEntity)girl).awaitPlayer = true;
               }

               girl.setPlayer(message.playerUUID);
            }
         } else {
            System.out.println("received an invalid message @SetPlayerForGirl :(");
            return null;
         }
      }
   }
}

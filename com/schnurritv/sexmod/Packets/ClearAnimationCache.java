package com.schnurritv.sexmod.Packets;

import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirl;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClearAnimationCache implements IMessage {
   boolean messageValid;
   UUID girlUUID;

   public ClearAnimationCache() {
      this.messageValid = false;
   }

   public ClearAnimationCache(UUID girlUUID) {
      this.girlUUID = girlUUID;
      this.messageValid = true;
   }

   public void fromBytes(ByteBuf buf) {
      this.girlUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.girlUUID.toString());
   }

   public static class Handler implements IMessageHandler<ClearAnimationCache, IMessage> {
      public IMessage onMessage(ClearAnimationCache message, MessageContext ctx) {
         if (message.messageValid) {
            ArrayList<GirlEntity> girlList = GirlEntity.getGirlsByUUID(message.girlUUID);
            if (!girlList.isEmpty()) {
               Iterator var4 = girlList.iterator();

               while(var4.hasNext()) {
                  GirlEntity girl = (GirlEntity)var4.next();
                  if (ctx.side.isServer() && !girl.field_70170_p.field_72995_K) {
                     PacketHandler.INSTANCE.sendToAllTracking(new ClearAnimationCache(message.girlUUID), girl);
                     break;
                  }

                  if (ctx.side.isClient() && girl.actionController != null) {
                     girl.actionController.clearAnimationCache();
                  }
               }
            } else {
               PlayerGirl girl = (PlayerGirl)PlayerGirl.playerGirlTable.get(message.girlUUID);
               if (ctx.side.isServer() && !girl.field_70170_p.field_72995_K) {
                  PacketHandler.INSTANCE.sendToAllAround(new ClearAnimationCache(message.girlUUID), new TargetPoint(girl.field_71093_bK, girl.field_70165_t, girl.getRenderPos().field_72448_b, girl.field_70161_v, 15.0D));
               } else if (ctx.side.isClient() && girl.actionController != null) {
                  girl.actionController.clearAnimationCache();
               }
            }
         } else {
            System.out.println("received an invalid message @ClearAnimationCache :(");
         }

         return null;
      }
   }
}

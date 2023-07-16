package com.schnurritv.sexmod.Packets;

import com.schnurritv.sexmod.girls.base.GirlEntity;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class WishDone implements IMessage {
   boolean messageValid;
   UUID girlUUID;

   public WishDone() {
   }

   public WishDone(UUID girlUUID) {
      this.girlUUID = girlUUID;
   }

   public void fromBytes(ByteBuf buf) {
      this.girlUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.girlUUID.toString());
   }

   public static class Handler implements IMessageHandler<WishDone, IMessage> {
      public IMessage onMessage(WishDone message, MessageContext ctx) {
         if (message.messageValid && ctx.side == Side.SERVER) {
            ArrayList<GirlEntity> girlList = GirlEntity.getGirlsByUUID(message.girlUUID);
            Iterator var4 = girlList.iterator();

            while(var4.hasNext()) {
               GirlEntity girl = (GirlEntity)var4.next();
               if (!girl.field_70170_p.field_72995_K) {
                  girl.field_70170_p.func_72900_e(girl);
               }
            }
         } else {
            System.out.println("received an invalid message @UploadInventoryToServer :(");
         }

         return null;
      }
   }
}

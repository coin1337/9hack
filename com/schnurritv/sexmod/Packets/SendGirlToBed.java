package com.schnurritv.sexmod.Packets;

import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.bia.BiaEntity;
import com.schnurritv.sexmod.girls.ellie.EllieEntity;
import com.schnurritv.sexmod.girls.jenny.JennyEntity;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SendGirlToBed implements IMessage {
   boolean messageValid;
   UUID girlUUID;

   public SendGirlToBed() {
      this.messageValid = false;
   }

   public SendGirlToBed(UUID girlUUID) {
      this.girlUUID = girlUUID;
   }

   public void fromBytes(ByteBuf buf) {
      this.girlUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.girlUUID.toString());
   }

   public static class Handler implements IMessageHandler<SendGirlToBed, IMessage> {
      public IMessage onMessage(SendGirlToBed message, MessageContext ctx) {
         if (message.messageValid) {
            ArrayList<GirlEntity> girlList = GirlEntity.getGirlsByUUID(message.girlUUID);
            Iterator var4 = girlList.iterator();

            while(var4.hasNext()) {
               GirlEntity girl = (GirlEntity)var4.next();
               if (!girl.field_70170_p.field_72995_K) {
                  if (girl instanceof JennyEntity) {
                     ((JennyEntity)girl).goForDoggy();
                  } else if (girl instanceof EllieEntity) {
                     ((EllieEntity)girl).goForCowgirl();
                  } else if (girl instanceof BiaEntity) {
                     ((BiaEntity)girl).goForAnal();
                  }
               }
            }
         } else {
            System.out.println("received an invalid message @SendGirlToSex :(");
         }

         return null;
      }
   }
}

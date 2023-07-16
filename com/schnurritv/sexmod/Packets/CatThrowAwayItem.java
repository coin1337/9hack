package com.schnurritv.sexmod.Packets;

import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.cat.CatEntity;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class CatThrowAwayItem implements IMessage {
   boolean messageValid = false;
   UUID cat;

   public CatThrowAwayItem() {
   }

   public CatThrowAwayItem(UUID cat) {
      this.cat = cat;
   }

   public void fromBytes(ByteBuf buf) {
      this.cat = UUID.fromString(ByteBufUtils.readUTF8String(buf));
      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.cat.toString());
   }

   public static class Handler implements IMessageHandler<CatThrowAwayItem, IMessage> {
      public IMessage onMessage(CatThrowAwayItem message, MessageContext ctx) {
         if (message.messageValid && ctx.side.equals(Side.SERVER)) {
            ArrayList<GirlEntity> girls = GirlEntity.getGirlsByUUID(message.cat);
            Iterator var4 = girls.iterator();

            while(var4.hasNext()) {
               GirlEntity girl = (GirlEntity)var4.next();
               if (!girl.field_70170_p.field_72995_K && girl instanceof CatEntity) {
                  CatEntity cat = (CatEntity)girl;
                  cat.throwAwayItem();
               }
            }

            return null;
         } else {
            System.out.println("received an invalid message @CatThrowAwayItem :(");
            return null;
         }
      }
   }
}

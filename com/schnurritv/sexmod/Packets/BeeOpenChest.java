package com.schnurritv.sexmod.Packets;

import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.bee.BeeEntity;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class BeeOpenChest implements IMessage {
   boolean messageValid = false;
   UUID girl;
   UUID player;

   public BeeOpenChest() {
   }

   public BeeOpenChest(UUID girl, UUID player) {
      this.girl = girl;
      this.player = player;
   }

   public void fromBytes(ByteBuf buf) {
      this.girl = UUID.fromString(ByteBufUtils.readUTF8String(buf));
      this.player = UUID.fromString(ByteBufUtils.readUTF8String(buf));
      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.girl.toString());
      ByteBufUtils.writeUTF8String(buf, this.player.toString());
   }

   public static class Handler implements IMessageHandler<BeeOpenChest, IMessage> {
      public IMessage onMessage(BeeOpenChest message, MessageContext ctx) {
         if (message.messageValid) {
            ArrayList<GirlEntity> girls = GirlEntity.getGirlsByUUID(message.girl);
            Iterator var4 = girls.iterator();

            while(var4.hasNext()) {
               GirlEntity girl = (GirlEntity)var4.next();
               if (!girl.field_70170_p.field_72995_K && girl instanceof BeeEntity) {
                  BeeEntity bee = (BeeEntity)girl;
                  if ((Boolean)bee.func_184212_Q().func_187225_a(BeeEntity.IS_TAMED)) {
                     EntityPlayer player = bee.field_70170_p.func_152378_a(message.player);
                     if (player != null) {
                        player.func_71007_a(bee);
                        return null;
                     }
                  }
               }
            }
         } else {
            System.out.println("received an invalid message @BeeOpenChest :(");
         }

         return null;
      }
   }
}

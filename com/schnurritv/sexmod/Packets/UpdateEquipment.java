package com.schnurritv.sexmod.Packets;

import com.schnurritv.sexmod.girls.base.Fighter;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UpdateEquipment implements IMessage {
   boolean messageValid;
   UUID girl;
   NBTTagCompound nbtStuff;

   public UpdateEquipment() {
   }

   public UpdateEquipment(UUID girl, NBTTagCompound nbtStuff) {
      this.girl = girl;
      this.nbtStuff = nbtStuff;
   }

   public void fromBytes(ByteBuf buf) {
      this.girl = UUID.fromString(ByteBufUtils.readUTF8String(buf));
      this.nbtStuff = ByteBufUtils.readTag(buf);
      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.girl.toString());
      ByteBufUtils.writeTag(buf, this.nbtStuff);
   }

   public static class Handler implements IMessageHandler<UpdateEquipment, IMessage> {
      public IMessage onMessage(UpdateEquipment message, MessageContext ctx) {
         if (message.messageValid) {
            ArrayList<GirlEntity> girlList = GirlEntity.getGirlsByUUID(message.girl);
            Iterator var4 = girlList.iterator();

            while(var4.hasNext()) {
               GirlEntity girl = (GirlEntity)var4.next();
               if (girl instanceof Fighter) {
                  ((Fighter)girl).inventory.deserializeNBT(message.nbtStuff);
               }
            }
         } else {
            System.out.println("received an invalid message @UpdateEquipment :(");
         }

         return null;
      }
   }
}

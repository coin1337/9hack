package com.schnurritv.sexmod.Packets;

import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.slime.SlimeEntity;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import io.netty.buffer.ByteBuf;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SetSlimePregnant implements IMessage {
   boolean messageValid;
   UUID girlUUID;
   boolean pregnant;

   public SetSlimePregnant() {
      this.messageValid = false;
   }

   public SetSlimePregnant(UUID girlUUID, boolean pregnant) {
      this.girlUUID = girlUUID;
      this.pregnant = pregnant;
      this.messageValid = true;
   }

   public void fromBytes(ByteBuf buf) {
      this.girlUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
      this.pregnant = buf.readBoolean();
      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.girlUUID.toString());
      buf.writeBoolean(this.pregnant);
   }

   public static class Handler implements IMessageHandler<SetSlimePregnant, IMessage> {
      public IMessage onMessage(SetSlimePregnant message, MessageContext ctx) {
         if (message.messageValid) {
            List<GirlEntity> slimes = GirlEntity.getGirlsByUUID(message.girlUUID);
            Iterator var4;
            GirlEntity slime;
            if (ctx.side.isServer()) {
               PacketHandler.INSTANCE.sendToAllTracking(new SetSlimePregnant(message.girlUUID, message.pregnant), (Entity)slimes.get(0));
               var4 = slimes.iterator();

               while(var4.hasNext()) {
                  slime = (GirlEntity)var4.next();
                  if (slime instanceof SlimeEntity) {
                     ((SlimeEntity)slime).ticksUntilBirth = 300;
                  }
               }
            } else {
               var4 = slimes.iterator();

               while(var4.hasNext()) {
                  slime = (GirlEntity)var4.next();
                  if (slime instanceof SlimeEntity) {
                     ((SlimeEntity)slime).isPregnant = message.pregnant;
                  }
               }
            }
         } else {
            System.out.println("received an invalid message @SetSlimePregnant :(");
         }

         return null;
      }
   }
}

package com.schnurritv.sexmod.Packets;

import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.ellie.EllieEntity;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SendEllieToPlayer implements IMessage {
   boolean messageValid;
   UUID girlUUID;

   public SendEllieToPlayer() {
      this.messageValid = false;
   }

   public SendEllieToPlayer(UUID girlUUID) {
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

   public static class Handler implements IMessageHandler<SendEllieToPlayer, IMessage> {
      public IMessage onMessage(SendEllieToPlayer message, MessageContext ctx) {
         if (message.messageValid) {
            ArrayList<GirlEntity> girlList = GirlEntity.getGirlsByUUID(message.girlUUID);
            Iterator var4 = girlList.iterator();

            while(var4.hasNext()) {
               GirlEntity girl = (GirlEntity)var4.next();
               if (!girl.field_70170_p.field_72995_K && girl instanceof EllieEntity) {
                  EllieEntity ellie = (EllieEntity)girl;
                  EntityPlayer player = girl.func_184102_h().func_184103_al().func_177451_a(ellie.playerSheHasSexWith());
                  ellie.setTargetPos(ellie.getBehindOfPlayer(girl.func_184102_h().func_184103_al().func_177451_a(player.func_110124_au())));
                  ellie.delayedRot = player.field_70177_z;
                  ellie.delayNewRot = true;
                  ellie.func_184212_Q().func_187227_b(GirlEntity.SHOULD_BE_AT_TARGET, true);
               }
            }
         } else {
            System.out.println("received an invalid message @SendEllieToPlayer :(");
         }

         return null;
      }
   }
}

package com.schnurritv.sexmod.Packets;

import com.schnurritv.sexmod.Main;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import io.netty.buffer.ByteBuf;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class OpenGirlInventory implements IMessage {
   boolean messageValid;
   UUID girlID;
   UUID playerID;

   public OpenGirlInventory() {
   }

   public OpenGirlInventory(UUID girlId, UUID playerID) {
      this.girlID = girlId;
      this.playerID = playerID;
      this.messageValid = true;
   }

   public void fromBytes(ByteBuf buf) {
      this.girlID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
      this.playerID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.girlID.toString());
      ByteBufUtils.writeUTF8String(buf, this.playerID.toString());
   }

   public static class Handler implements IMessageHandler<OpenGirlInventory, IMessage> {
      public IMessage onMessage(OpenGirlInventory message, MessageContext ctx) {
         if (message.messageValid && ctx.side == Side.SERVER) {
            Iterator var3 = GirlEntity.girlEntities.iterator();

            while(var3.hasNext()) {
               GirlEntity girl = (GirlEntity)var3.next();
               if (!girl.field_70170_p.field_72995_K && girl.girlId().equals(message.girlID)) {
                  ((EntityPlayerMP)girl.field_70170_p.func_152378_a(message.playerID)).openGui(Main.instance, 0, girl.field_70170_p, girl.func_180425_c().func_177958_n(), girl.func_180425_c().func_177956_o(), girl.func_180425_c().func_177952_p());
               }
            }
         }

         return null;
      }
   }
}

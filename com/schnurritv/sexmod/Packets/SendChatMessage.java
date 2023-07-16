package com.schnurritv.sexmod.Packets;

import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SendChatMessage implements IMessage {
   boolean messageValid;
   String chatMessage;
   int dimension;
   UUID girlUUID;

   public SendChatMessage(String chatMessage, int dimension, UUID girlUUID) {
      this.chatMessage = chatMessage;
      this.dimension = dimension;
      this.girlUUID = girlUUID;
      this.messageValid = true;
   }

   public SendChatMessage() {
      this.messageValid = false;
   }

   public void fromBytes(ByteBuf buf) {
      try {
         int stringLength = buf.readInt();
         byte[] bytes = new byte[stringLength];

         for(int i = 0; i < stringLength; ++i) {
            bytes[i] = buf.readByte();
         }

         this.chatMessage = new String(bytes);
         this.dimension = buf.readInt();
         this.girlUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
         this.messageValid = true;
      } catch (IndexOutOfBoundsException var5) {
         this.messageValid = false;
         System.out.println("couldn't read bytes @SendChatMessage :(");
      }
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.chatMessage.getBytes().length);
      buf.writeBytes(this.chatMessage.getBytes());
      buf.writeInt(this.dimension);
      ByteBufUtils.writeUTF8String(buf, this.girlUUID.toString());
   }

   public static class Handler implements IMessageHandler<SendChatMessage, IMessage> {
      public IMessage onMessage(SendChatMessage message, MessageContext ctx) {
         if (message.messageValid) {
            if (ctx.side.isClient()) {
               Minecraft.func_71410_x().field_71439_g.func_145747_a(new TextComponentString(message.chatMessage));
            } else {
               Vec3d girlPos = ((GirlEntity)GirlEntity.getGirlsByUUID(message.girlUUID).get(0)).prevPos();
               PacketHandler.INSTANCE.sendToAllAround(new SendChatMessage(message.chatMessage, message.dimension, message.girlUUID), new TargetPoint(message.dimension, girlPos.field_72450_a, girlPos.field_72448_b, girlPos.field_72449_c, 40.0D));
            }
         } else {
            System.out.println("recieved an unvalid message @SendChatMessage :(");
         }

         return null;
      }
   }
}

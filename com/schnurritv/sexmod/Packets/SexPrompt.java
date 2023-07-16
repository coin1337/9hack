package com.schnurritv.sexmod.Packets;

import com.schnurritv.sexmod.gender_change.SexPromptManager;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class SexPrompt implements IMessage {
   boolean messageValid = false;
   String action;
   UUID male;
   UUID female;
   boolean senderIsMale;

   public SexPrompt() {
   }

   public SexPrompt(String action, UUID male, UUID female, boolean senderIsMale) {
      this.action = action;
      this.male = male;
      this.female = female;
      this.senderIsMale = senderIsMale;
   }

   public void fromBytes(ByteBuf buf) {
      this.action = ByteBufUtils.readUTF8String(buf);
      this.male = UUID.fromString(ByteBufUtils.readUTF8String(buf));
      this.female = UUID.fromString(ByteBufUtils.readUTF8String(buf));
      this.senderIsMale = buf.readBoolean();
      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.action);
      ByteBufUtils.writeUTF8String(buf, this.male.toString());
      ByteBufUtils.writeUTF8String(buf, this.female.toString());
      buf.writeBoolean(this.senderIsMale);
   }

   public static class Handler implements IMessageHandler<SexPrompt, IMessage> {
      public IMessage onMessage(SexPrompt message, MessageContext ctx) {
         if (!message.messageValid) {
            System.out.println("received an invalid message @SexPrompt :(");
            return null;
         } else {
            if (ctx.side.equals(Side.SERVER)) {
               World world = ctx.getServerHandler().field_147369_b.field_70170_p;
               EntityPlayer female = world.func_152378_a(message.female);
               EntityPlayer male = world.func_152378_a(message.male);
               if (female == null) {
                  System.out.println("Sex prompt invalid -> female player not found");
                  return null;
               }

               if (male == null) {
                  System.out.println("Sex prompt invalid -> male player not found");
                  return null;
               }

               PacketHandler.INSTANCE.sendTo(new SexPrompt(message.action, message.male, message.female, message.senderIsMale), (EntityPlayerMP)((EntityPlayerMP)(message.senderIsMale ? female : male)));
            }

            if (ctx.side.equals(Side.CLIENT)) {
               System.out.println(message.senderIsMale);
               SexPromptManager.INSTANCE.setNewActivePrompt(new SexPromptManager.SexPrompt(message.action, message.male, message.female, message.senderIsMale));
            }

            return null;
         }
      }
   }
}

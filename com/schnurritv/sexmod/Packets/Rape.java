package com.schnurritv.sexmod.Packets;

import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirl;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class Rape implements IMessage {
   boolean messageValid = false;
   UUID male;
   String action;

   public Rape() {
   }

   public Rape(UUID male, String action) {
      this.male = male;
      this.action = action;
   }

   public void fromBytes(ByteBuf buf) {
      this.male = UUID.fromString(ByteBufUtils.readUTF8String(buf));
      this.action = ByteBufUtils.readUTF8String(buf);
      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.male.toString());
      ByteBufUtils.writeUTF8String(buf, this.action);
   }

   public static class Handler implements IMessageHandler<Rape, IMessage> {
      public IMessage onMessage(Rape message, MessageContext ctx) {
         if (message.messageValid && ctx.side.equals(Side.SERVER)) {
            EntityPlayer sender = ctx.getServerHandler().field_147369_b;
            PlayerGirl girl = (PlayerGirl)PlayerGirl.playerGirlTable.get(ctx.getServerHandler().field_147369_b.getPersistentID());
            if (girl == null) {
               sender.func_146105_b(new TextComponentString("uwu i dont kno wat u just did... but it hurts uwu"), true);
               return null;
            } else {
               girl.startStandingSex(message.action, message.male);
               return null;
            }
         } else {
            System.out.println("received an invalid message @Rape :(");
            return null;
         }
      }
   }
}

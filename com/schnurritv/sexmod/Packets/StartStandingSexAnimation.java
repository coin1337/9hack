package com.schnurritv.sexmod.Packets;

import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirl;
import io.netty.buffer.ByteBuf;
import java.util.Iterator;
import java.util.UUID;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class StartStandingSexAnimation implements IMessage {
   boolean messageValid;
   UUID female;
   UUID male;
   String action;

   public StartStandingSexAnimation() {
   }

   public StartStandingSexAnimation(UUID female, UUID male, String action) {
      this.female = female;
      this.male = male;
      this.action = action;
   }

   public void fromBytes(ByteBuf buf) {
      this.female = UUID.fromString(ByteBufUtils.readUTF8String(buf));
      this.male = UUID.fromString(ByteBufUtils.readUTF8String(buf));
      this.action = ByteBufUtils.readUTF8String(buf);
      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.female.toString());
      ByteBufUtils.writeUTF8String(buf, this.male.toString());
      ByteBufUtils.writeUTF8String(buf, this.action);
   }

   public static class Handler implements IMessageHandler<StartStandingSexAnimation, IMessage> {
      public IMessage onMessage(StartStandingSexAnimation message, MessageContext ctx) {
         if (message.messageValid && ctx.side == Side.SERVER) {
            PlayerGirl girl = (PlayerGirl)PlayerGirl.playerGirlTable.get(message.female);
            if (girl == null) {
               System.out.println("girl is null");
               return null;
            }

            Iterator var4 = GirlEntity.girlEntities.iterator();

            while(var4.hasNext()) {
               GirlEntity girlEntity = (GirlEntity)var4.next();
               if (girlEntity instanceof PlayerGirl) {
                  girl = (PlayerGirl)girlEntity;
                  if (!girl.field_70170_p.field_72995_K && girl.getOwner().equals(message.female)) {
                     break;
                  }
               }
            }

            girl.startStandingSex(message.action, message.male);
         } else {
            System.out.println("received an invalid message @StartStandingSexAnimation :(");
         }

         return null;
      }
   }
}

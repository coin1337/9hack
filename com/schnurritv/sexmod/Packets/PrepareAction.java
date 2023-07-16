package com.schnurritv.sexmod.Packets;

import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.bia.BiaEntity;
import com.schnurritv.sexmod.girls.cat.CatEntity;
import com.schnurritv.sexmod.girls.ellie.EllieEntity;
import com.schnurritv.sexmod.girls.jenny.JennyEntity;
import com.schnurritv.sexmod.girls.slime.SlimeEntity;
import io.netty.buffer.ByteBuf;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PrepareAction implements IMessage {
   boolean messageValid;
   UUID girlUUID;
   boolean preparePayment;
   boolean setTargetPos;
   UUID playerUUID = null;

   public PrepareAction() {
      this.messageValid = false;
   }

   public PrepareAction(UUID girlUUID, UUID playerUUID, boolean preparePayment, boolean setTargetPos) {
      this.girlUUID = girlUUID;
      this.preparePayment = preparePayment;
      this.playerUUID = playerUUID;
      this.setTargetPos = setTargetPos;
      this.messageValid = true;
   }

   public void fromBytes(ByteBuf buf) {
      this.girlUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
      this.preparePayment = buf.readBoolean();
      this.setTargetPos = buf.readBoolean();
      String temp = ByteBufUtils.readUTF8String(buf);
      this.playerUUID = temp.equals("null") ? null : UUID.fromString(temp);
      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.girlUUID.toString());
      buf.writeBoolean(this.preparePayment);
      buf.writeBoolean(this.setTargetPos);
      ByteBufUtils.writeUTF8String(buf, this.playerUUID == null ? "null" : this.playerUUID.toString());
   }

   public static class Handler implements IMessageHandler<PrepareAction, IMessage> {
      public static void prepareAction(UUID girlUUID, UUID playerUUID, boolean preparePayment, boolean setTargetPos) {
         List<GirlEntity> girls = GirlEntity.getGirlsByUUID(girlUUID);
         Iterator var5 = girls.iterator();

         while(true) {
            GirlEntity girl;
            do {
               if (!var5.hasNext()) {
                  return;
               }

               girl = (GirlEntity)var5.next();
            } while(girl.field_70170_p.field_72995_K);

            girl.setPlayer(playerUUID);
            if (!(girl instanceof JennyEntity) && !(girl instanceof EllieEntity) && !(girl instanceof CatEntity)) {
               if (girl instanceof SlimeEntity) {
                  SlimeEntity slime = (SlimeEntity)girl;
                  slime.field_70714_bg.func_85156_a(slime.floatTask);
                  slime.field_70714_bg.func_85156_a(slime.hopTask);
               }
            } else {
               girl.field_70714_bg.func_85156_a(girl.aiLookAtPlayer);
               girl.field_70714_bg.func_85156_a(girl.aiWander);
            }

            girl.func_70661_as().func_75499_g();
            girl.field_70159_w = 0.0D;
            girl.field_70181_x = 0.0D;
            girl.field_70179_y = 0.0D;
            if (girl.playerSheHasSexWith() == null) {
               girl.setPlayer(playerUUID);
            }

            if (setTargetPos) {
               girl.setTargetPos(girl.getInFrontOfPlayer());
            }

            girl.TurnPlayerIntoCamera(girl.playerSheHasSexWith());
            if (preparePayment) {
               if (girl instanceof JennyEntity) {
                  ((JennyEntity)girl).isPreparingPayment = true;
               } else if (girl instanceof EllieEntity) {
                  ((EllieEntity)girl).isPreparingPayment = true;
               } else if (girl instanceof BiaEntity) {
                  ((BiaEntity)girl).isPreparingTalk = true;
               } else if (girl instanceof CatEntity) {
                  ((CatEntity)girl).preparePayment();
               }
            }
         }
      }

      public IMessage onMessage(PrepareAction message, MessageContext ctx) {
         if (message.messageValid && ctx.side == Side.SERVER) {
            prepareAction(message.girlUUID, message.playerUUID, message.preparePayment, message.setTargetPos);
         }

         return null;
      }
   }
}

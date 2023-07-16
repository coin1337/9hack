package com.schnurritv.sexmod.Packets;

import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.slime.SlimeEntity;
import io.netty.buffer.ByteBuf;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ChangeDataParameter implements IMessage {
   boolean messageValid;
   UUID girl;
   String parameter;
   String value;

   public ChangeDataParameter() {
      this.messageValid = false;
   }

   public ChangeDataParameter(UUID girl, String parameter, String value) {
      this.girl = girl;
      this.parameter = parameter;
      this.value = value;
      this.messageValid = true;
   }

   public void fromBytes(ByteBuf buf) {
      this.girl = UUID.fromString(ByteBufUtils.readUTF8String(buf));
      this.parameter = ByteBufUtils.readUTF8String(buf);
      this.value = ByteBufUtils.readUTF8String(buf);
      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.girl.toString());
      ByteBufUtils.writeUTF8String(buf, this.parameter);
      ByteBufUtils.writeUTF8String(buf, this.value);
   }

   public static class Handler implements IMessageHandler<ChangeDataParameter, IMessage> {
      public IMessage onMessage(ChangeDataParameter message, MessageContext ctx) {
         if (message.messageValid) {
            Iterator var3 = GirlEntity.getGirlsByUUID(message.girl).iterator();

            while(var3.hasNext()) {
               GirlEntity girl = (GirlEntity)var3.next();
               if (!girl.field_70170_p.field_72995_K && girl.girlId().equals(message.girl)) {
                  String var5 = message.parameter;
                  byte var6 = -1;
                  switch(var5.hashCode()) {
                  case -1206220351:
                     if (var5.equals("playerSheHasSexWith")) {
                        var6 = 3;
                     }
                     break;
                  case -1081267614:
                     if (var5.equals("master")) {
                        var6 = 6;
                     }
                     break;
                  case -815590653:
                     if (var5.equals("targetPos")) {
                        var6 = 5;
                     }
                     break;
                  case -395351248:
                     if (var5.equals("walk speed")) {
                        var6 = 7;
                     }
                     break;
                  case 679533008:
                     if (var5.equals("animationFollowUp")) {
                        var6 = 2;
                     }
                     break;
                  case 1452134704:
                     if (var5.equals("currentModel")) {
                        var6 = 0;
                     }
                     break;
                  case 1712351503:
                     if (var5.equals("currentAction")) {
                        var6 = 1;
                     }
                     break;
                  case 1812907511:
                     if (var5.equals("currentJumpStage")) {
                        var6 = 4;
                     }
                  }

                  switch(var6) {
                  case 0:
                     girl.func_184212_Q().func_187227_b(GirlEntity.CURRENT_MODEL, Integer.valueOf(message.value));
                     break;
                  case 1:
                     girl.setCurrentAction(GirlEntity.Action.valueOf(message.value));
                     break;
                  case 2:
                     girl.func_184212_Q().func_187227_b(GirlEntity.ANIMATION_FOLLOW_UP, message.value);
                     break;
                  case 3:
                     girl.setPlayer(UUID.fromString(message.value));
                     break;
                  case 4:
                     ((SlimeEntity)girl).setCurrentJumpStage(SlimeEntity.JumpStage.valueOf(message.value));
                     break;
                  case 5:
                     String[] pos = message.value.split("f");
                     Vec3d target = new Vec3d(Double.parseDouble(pos[0]), Double.parseDouble(pos[1]), Double.parseDouble(pos[2]));
                     girl.setTargetPos(target);
                     break;
                  case 6:
                     girl.func_184212_Q().func_187227_b(GirlEntity.MASTER, message.value);
                     break;
                  case 7:
                     girl.func_184212_Q().func_187227_b(GirlEntity.WALK_SPEED, message.value);
                  }
               }
            }
         } else {
            System.out.println("received an invalid message @ChangeDataParameter :(");
         }

         return null;
      }
   }
}

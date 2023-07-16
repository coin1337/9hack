package com.schnurritv.sexmod.Packets;

import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.bee.BeeEntity;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SetNewHome implements IMessage {
   boolean messageValid;
   UUID girl;
   Vec3d pos;

   public SetNewHome() {
   }

   public SetNewHome(UUID girl, Vec3d pos) {
      this.girl = girl;
      this.pos = pos;
   }

   public void fromBytes(ByteBuf buf) {
      this.girl = UUID.fromString(ByteBufUtils.readUTF8String(buf));
      this.pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.girl.toString());
      buf.writeDouble(this.pos.field_72450_a);
      buf.writeDouble(this.pos.field_72448_b);
      buf.writeDouble(this.pos.field_72449_c);
   }

   public static class Handler implements IMessageHandler<SetNewHome, IMessage> {
      public IMessage onMessage(SetNewHome message, MessageContext ctx) {
         if (message.messageValid) {
            ArrayList<GirlEntity> girlList = GirlEntity.getGirlsByUUID(message.girl);
            if (!(girlList.get(0) instanceof BeeEntity)) {
               ((GirlEntity)girlList.get(0)).say("Alright! this is my new Home~");
            }

            GirlEntity girl;
            for(Iterator var4 = girlList.iterator(); var4.hasNext(); girl.home = new Vec3d(message.pos.field_72450_a, Math.floor(message.pos.field_72448_b), message.pos.field_72449_c)) {
               girl = (GirlEntity)var4.next();
            }
         } else {
            System.out.println("received an invalid message @SetNewHome :(");
         }

         return null;
      }
   }
}

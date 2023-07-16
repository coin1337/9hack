package com.schnurritv.sexmod.Packets;

import com.schnurritv.sexmod.companion.CompanionPearl;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.util.Reference;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class SendCompanionHome implements IMessage {
   boolean messageValid;
   UUID girl;

   public SendCompanionHome() {
   }

   public SendCompanionHome(UUID girl) {
      this.girl = girl;
   }

   public void fromBytes(ByteBuf buf) {
      this.girl = UUID.fromString(ByteBufUtils.readUTF8String(buf));
      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.girl.toString());
   }

   public static class Handler implements IMessageHandler<SendCompanionHome, IMessage> {
      public static void doIt(UUID girlid) {
         ArrayList<GirlEntity> girlList = GirlEntity.getGirlsByUUID(girlid);
         Iterator var2 = girlList.iterator();

         while(true) {
            while(true) {
               GirlEntity girl;
               do {
                  if (!var2.hasNext()) {
                     return;
                  }

                  girl = (GirlEntity)var2.next();
               } while(girl.field_70170_p.field_72995_K);

               if (girl.currentAction() != GirlEntity.Action.THROW_PEARL) {
                  girl.setCurrentAction(GirlEntity.Action.THROW_PEARL);
                  girl.setTargetYaw((float)Math.atan2(girl.field_70161_v - girl.home.field_72449_c, girl.field_70165_t - girl.home.field_72450_a) * 57.29578F + 90.0F);
                  girl.setTargetPos(girl.func_174791_d());
                  girl.func_184212_Q().func_187227_b(GirlEntity.SHOULD_BE_AT_TARGET, true);
                  girl.pearl = null;
               } else if (girl.pearl == null) {
                  float distance = (float)girl.func_174791_d().func_72438_d(girl.home);
                  girl.pearl = new CompanionPearl(girl.field_70170_p, girl);
                  girl.pearl.func_70186_c(girl.home.field_72450_a - girl.field_70165_t, girl.home.field_72448_b - girl.field_70163_u, girl.home.field_72449_c - girl.field_70161_v, Math.min(4.0F, distance * 0.1F), 0.0F);
                  girl.field_70170_p.func_72838_d(girl.pearl);
               } else {
                  WorldServer server = (WorldServer)girl.field_70170_p;

                  for(int i = 0; i < 32; ++i) {
                     server.func_180505_a(EnumParticleTypes.PORTAL, false, girl.field_70165_t, girl.field_70163_u + Reference.RANDOM.nextDouble() * 2.0D, girl.field_70161_v, 32, 0.2D, 0.2D, 0.2D, Reference.RANDOM.nextGaussian(), new int[0]);
                  }

                  girl.func_70107_b(girl.home.field_72450_a, girl.home.field_72448_b, girl.home.field_72449_c);
                  girl.pearl = null;
                  girl.setCurrentAction(GirlEntity.Action.NULL);
                  girl.func_184212_Q().func_187227_b(GirlEntity.SHOULD_BE_AT_TARGET, false);
                  girl.stopCompanionShip();
               }
            }
         }
      }

      public IMessage onMessage(SendCompanionHome message, MessageContext ctx) {
         if (message.messageValid && ctx.side == Side.SERVER) {
            doIt(message.girl);
         } else {
            System.out.println("received an invalid message @SendCompanionHome :(");
         }

         return null;
      }
   }
}

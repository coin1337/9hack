package com.schnurritv.sexmod.Packets;

import com.schnurritv.sexmod.girls.allie.AllieEntity;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class SummonAllie implements IMessage {
   boolean messageValid = false;

   public void fromBytes(ByteBuf buf) {
      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
   }

   public static class Handler implements IMessageHandler<SummonAllie, IMessage> {
      public IMessage onMessage(SummonAllie message, MessageContext ctx) {
         if (message.messageValid && ctx.side == Side.SERVER) {
            EntityPlayer player = ctx.getServerHandler().field_147369_b;
            Vec3d pos = player.func_174791_d().func_72441_c(-Math.sin((double)player.field_70759_as * 0.017453292519943295D) * 2.0D, 0.0D, Math.cos((double)player.field_70759_as * 0.017453292519943295D) * 2.0D);
            AllieEntity allie = new AllieEntity(player.field_70170_p, player.func_184614_ca());
            allie.setPlayer(player.getPersistentID());
            allie.func_70080_a(pos.field_72450_a, pos.field_72448_b, pos.field_72449_c, player.field_70759_as + 180.0F, player.field_70125_A);
            allie.func_189654_d(true);
            allie.field_70145_X = true;
            player.field_70170_p.func_72838_d(allie);
            BlockPos blockPos = allie.func_180425_c().func_177982_a(0, -1, 0);
            if (allie.field_70170_p.func_180495_p(blockPos).func_177230_c().equals(Blocks.field_150354_m)) {
               allie.setCurrentAction(GirlEntity.Action.SUMMON_SAND);
            } else {
               allie.setCurrentAction(allie.firstWish() ? GirlEntity.Action.SUMMON : GirlEntity.Action.SUMMON_NORMAL);
            }
         } else {
            System.out.println("received an invalid message @SummonAllie :(");
         }

         return null;
      }
   }
}

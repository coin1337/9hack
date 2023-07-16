package com.schnurritv.sexmod.Packets;

import com.schnurritv.sexmod.util.Reference;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MakeRichWish implements IMessage {
   boolean messageValid;
   Vec3d pos;

   public MakeRichWish() {
   }

   public MakeRichWish(Vec3d pos) {
      this.pos = pos;
   }

   public void fromBytes(ByteBuf buf) {
      this.pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      buf.writeDouble(this.pos.field_72450_a);
      buf.writeDouble(this.pos.field_72448_b);
      buf.writeDouble(this.pos.field_72449_c);
   }

   public static class Handler implements IMessageHandler<MakeRichWish, IMessage> {
      public IMessage onMessage(MakeRichWish message, MessageContext ctx) {
         if (message.messageValid && ctx.side == Side.SERVER) {
            World world = ctx.getServerHandler().field_147369_b.field_70170_p;
            EntityItem diamonds = new EntityItem(world, message.pos.field_72450_a, message.pos.field_72448_b, message.pos.field_72449_c, new ItemStack(Items.field_151045_i, Reference.RANDOM.nextInt(2) + 1));
            EntityItem emeralds = new EntityItem(world, message.pos.field_72450_a, message.pos.field_72448_b, message.pos.field_72449_c, new ItemStack(Items.field_151166_bC, Reference.RANDOM.nextInt(2) + 1));
            EntityItem gold = new EntityItem(world, message.pos.field_72450_a, message.pos.field_72448_b, message.pos.field_72449_c, new ItemStack(Items.field_151043_k, Reference.RANDOM.nextInt(2) + 1));
            world.func_72838_d(diamonds);
            world.func_72838_d(emeralds);
            world.func_72838_d(gold);
         } else {
            System.out.println("received an invalid message @MakeRichWish :(");
         }

         return null;
      }
   }
}

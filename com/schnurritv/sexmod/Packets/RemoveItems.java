package com.schnurritv.sexmod.Packets;

import com.schnurritv.sexmod.util.Reference;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class RemoveItems implements IMessage {
   boolean messageValid = false;
   UUID playerId;
   ItemStack stack;

   public RemoveItems() {
   }

   public RemoveItems(UUID playerId, ItemStack stack) {
      this.playerId = playerId;
      this.stack = stack;
   }

   public void fromBytes(ByteBuf buf) {
      this.playerId = UUID.fromString(ByteBufUtils.readUTF8String(buf));
      this.stack = ByteBufUtils.readItemStack(buf);
      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.playerId.toString());
      ByteBufUtils.writeItemStack(buf, this.stack);
   }

   public static class Handler implements IMessageHandler<RemoveItems, IMessage> {
      public IMessage onMessage(RemoveItems message, MessageContext ctx) {
         if (message.messageValid && ctx.side == Side.SERVER) {
            IInventory inventory = Reference.server.func_184103_al().func_177451_a(message.playerId).field_71071_by;

            for(int i = 0; i < inventory.func_70302_i_(); ++i) {
               ItemStack stack = inventory.func_70301_a(i);
               if (stack.func_77973_b().equals(message.stack.func_77973_b())) {
                  stack.func_190918_g(message.stack.func_190916_E());
                  break;
               }
            }
         } else {
            System.out.println("recieved an unvalid message @RemoveItems :(");
         }

         return null;
      }
   }
}

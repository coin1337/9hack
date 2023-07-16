package com.schnurritv.sexmod.Packets;

import com.schnurritv.sexmod.girls.base.Fighter;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class UploadInventoryToServer implements IMessage {
   boolean messageValid = false;
   ItemStack[] stacks = new ItemStack[42];
   UUID girlUUID;
   UUID player;

   public UploadInventoryToServer() {
   }

   public UploadInventoryToServer(UUID girlUUID, UUID player, ItemStack[] stacks) {
      this.girlUUID = girlUUID;
      this.stacks = stacks;
      this.player = player;
   }

   public void fromBytes(ByteBuf buf) {
      this.girlUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
      this.player = UUID.fromString(ByteBufUtils.readUTF8String(buf));

      for(int i = 0; i < 42; ++i) {
         this.stacks[i] = ByteBufUtils.readItemStack(buf);
      }

      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.girlUUID.toString());
      ByteBufUtils.writeUTF8String(buf, this.player.toString());

      for(int i = 0; i < 42; ++i) {
         ByteBufUtils.writeItemStack(buf, this.stacks[i]);
      }

   }

   public static class Handler implements IMessageHandler<UploadInventoryToServer, IMessage> {
      public IMessage onMessage(UploadInventoryToServer message, MessageContext ctx) {
         if (message.messageValid && ctx.side == Side.SERVER) {
            ArrayList<GirlEntity> girlList = GirlEntity.getGirlsByUUID(message.girlUUID);
            Iterator var4 = girlList.iterator();

            while(true) {
               GirlEntity girl;
               do {
                  if (!var4.hasNext()) {
                     return null;
                  }

                  girl = (GirlEntity)var4.next();
               } while(girl.field_70170_p.field_72995_K);

               IInventory inventory = girl.field_70170_p.func_152378_a(message.player).field_71071_by;

               for(int i = 0; i < 36; ++i) {
                  inventory.func_70299_a(i, message.stacks[i]);
               }

               Fighter fighter = (Fighter)girl;
               fighter.inventory.setStackInSlot(0, message.stacks[36]);
               fighter.inventory.setStackInSlot(1, message.stacks[37]);
               fighter.inventory.setStackInSlot(2, message.stacks[38]);
               fighter.inventory.setStackInSlot(3, message.stacks[39]);
               fighter.inventory.setStackInSlot(4, message.stacks[40]);
               fighter.inventory.setStackInSlot(5, message.stacks[41]);
            }
         } else {
            System.out.println("received an invalid message @UploadInventoryToServer :(");
            return null;
         }
      }
   }
}

package com.schnurritv.sexmod.Packets;

import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.cat.CatEntity;
import com.schnurritv.sexmod.girls.cat.fishing.CatFishingRod;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class CatActivateFishing implements IMessage {
   boolean messageValid = false;
   UUID girlUUID;

   public CatActivateFishing() {
   }

   public CatActivateFishing(UUID girlUUID) {
      this.girlUUID = girlUUID;
   }

   public void fromBytes(ByteBuf buf) {
      this.girlUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.girlUUID.toString());
   }

   public static class Handler implements IMessageHandler<CatActivateFishing, IMessage> {
      public IMessage onMessage(CatActivateFishing message, MessageContext ctx) {
         if (message.messageValid && ctx.side == Side.SERVER) {
            ArrayList<GirlEntity> girls = GirlEntity.getGirlsByUUID(message.girlUUID);
            Iterator var4 = girls.iterator();

            while(var4.hasNext()) {
               GirlEntity girl = (GirlEntity)var4.next();
               if (!girl.field_70170_p.field_72995_K && girl instanceof CatEntity) {
                  CatEntity cat = (CatEntity)girl;
                  ItemStack stack = (ItemStack)cat.func_184212_Q().func_187225_a(CatEntity.FISHING_ROD);
                  CatFishingRod rod = (CatFishingRod)stack.func_77973_b();
                  rod.onItemRightClick(ctx.getServerHandler().field_147369_b.field_70170_p, cat, EnumHand.MAIN_HAND);
               }
            }
         } else {
            System.out.println("received an invalid message @CatActivateFishing :(");
         }

         return null;
      }
   }
}

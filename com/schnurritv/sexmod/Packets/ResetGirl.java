package com.schnurritv.sexmod.Packets;

import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirl;
import com.schnurritv.sexmod.util.Reference;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class ResetGirl implements IMessage {
   boolean messageValid;
   UUID girlUUID;
   boolean onlyDoPlayerPart;

   public ResetGirl() {
      this.messageValid = false;
   }

   public ResetGirl(UUID girlUUID) {
      this.girlUUID = girlUUID;
      this.onlyDoPlayerPart = false;
      this.messageValid = true;
   }

   public ResetGirl(UUID girlUUID, boolean onlyDoPlayerPart) {
      this.girlUUID = girlUUID;
      this.onlyDoPlayerPart = onlyDoPlayerPart;
      this.messageValid = true;
   }

   public void fromBytes(ByteBuf buf) {
      this.girlUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
      this.onlyDoPlayerPart = buf.readBoolean();
      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.girlUUID.toString());
      buf.writeBoolean(this.onlyDoPlayerPart);
      this.messageValid = true;
   }

   public static class Handler implements IMessageHandler<ResetGirl, IMessage> {
      public static void resetGirl(GirlEntity girl) {
         girl.onReset();
         if (girl instanceof PlayerGirl && girl.field_70170_p.func_152378_a(((PlayerGirl)girl).getOwner()) != null) {
            PacketHandler.INSTANCE.sendTo(new SetPlayerMovement(true), (EntityPlayerMP)FMLCommonHandler.instance().getMinecraftServerInstance().func_71218_a(girl.field_71093_bK).func_152378_a(((PlayerGirl)girl).getOwner()));
            girl.func_184212_Q().func_187227_b(GirlEntity.CURRENT_MODEL, 1);
            EntityPlayer girlPlayer = girl.field_70170_p.func_152378_a(((PlayerGirl)girl).getOwner());
            girlPlayer.field_71075_bZ.field_75100_b = false;
            girlPlayer.func_189654_d(false);
            girlPlayer.field_70145_X = false;
            if (girl.playerSheHasSexWith() != null) {
               EntityPlayer male = girl.field_70170_p.func_152378_a(girl.playerSheHasSexWith());
               if (male != null) {
                  male.field_71075_bZ.field_75100_b = false;
                  male.func_189654_d(false);
                  male.field_70145_X = false;
               }
            }
         }

         girl.func_184212_Q().func_187227_b(GirlEntity.SHOULD_BE_AT_TARGET, false);
         girl.setPlayer((UUID)null);
         girl.playerIsCumming = false;
         girl.playerIsThrusting = false;
         girl.playerCamPos = null;
         girl.func_189654_d(false);
         girl.field_70145_X = false;
         girl.func_70634_a(girl.func_174791_d().field_72450_a, (double)girl.func_180425_c().func_177956_o(), girl.func_174791_d().field_72449_c);
      }

      public static void resetPlayer(EntityPlayerMP player) {
         player.func_70634_a(player.field_70165_t, Math.ceil(player.field_70163_u), player.field_70161_v);
         player.func_82142_c(false);
         player.field_70145_X = false;
         player.func_189654_d(false);
         player.field_71075_bZ.field_75100_b = false;
         PacketHandler.INSTANCE.sendTo(new SetPlayerMovement(true), player);
      }

      public IMessage onMessage(ResetGirl message, MessageContext ctx) {
         if (message.messageValid && ctx.side == Side.SERVER) {
            ArrayList<GirlEntity> girls = GirlEntity.getGirlsByUUID(message.girlUUID);
            Iterator var4 = girls.iterator();

            while(var4.hasNext()) {
               GirlEntity girl = (GirlEntity)var4.next();
               if (!girl.field_70170_p.field_72995_K) {
                  if (girl.playerSheHasSexWith() != null) {
                     resetPlayer(Reference.server.func_184103_al().func_177451_a(girl.playerSheHasSexWith()));
                  }

                  if (!message.onlyDoPlayerPart) {
                     resetGirl(girl);
                  }
               }
            }
         } else {
            System.out.println("recieved an unvalid message @ResetGirl :(");
         }

         return null;
      }
   }
}

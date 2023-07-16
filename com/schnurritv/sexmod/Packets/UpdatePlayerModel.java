package com.schnurritv.sexmod.Packets;

import com.google.common.base.Optional;
import com.schnurritv.sexmod.girls.allie.PlayerAllie;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirl;
import com.schnurritv.sexmod.girls.bee.PlayerBee;
import com.schnurritv.sexmod.girls.bia.PlayerBia;
import com.schnurritv.sexmod.girls.ellie.PlayerEllie;
import com.schnurritv.sexmod.girls.jenny.PlayerJenny;
import com.schnurritv.sexmod.girls.slime.PlayerSlime;
import io.netty.buffer.ByteBuf;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class UpdatePlayerModel implements IMessage {
   boolean messageValid = false;
   int newModel;

   public UpdatePlayerModel() {
   }

   public UpdatePlayerModel(int newModel) {
      this.newModel = newModel;
   }

   public void fromBytes(ByteBuf buf) {
      this.newModel = buf.readInt();
      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.newModel);
   }

   public static class Handler implements IMessageHandler<UpdatePlayerModel, IMessage> {
      public IMessage onMessage(UpdatePlayerModel message, MessageContext ctx) {
         if (message.messageValid && ctx.side == Side.SERVER) {
            EntityPlayer player = ctx.getServerHandler().field_147369_b;
            World world = player.field_70170_p;
            UUID id = ctx.getServerHandler().field_147369_b.getPersistentID();
            PlayerGirl girl = (PlayerGirl)PlayerGirl.playerGirlTable.get(id);
            if (girl != null) {
               Iterator var7 = GirlEntity.girlEntities.iterator();

               while(var7.hasNext()) {
                  GirlEntity girlEntity = (GirlEntity)var7.next();
                  if (!girlEntity.field_70170_p.field_72995_K && girlEntity.girlId().equals(girl.girlId())) {
                     world.func_72900_e(girlEntity);
                  }
               }

               girl.onDeletion();
               PlayerGirl.playerGirlTable.remove(id);
               GirlEntity.girlEntities.remove(girl);
               girl.setOwner(Optional.absent());
            }

            PlayerGirl playerGirl = null;
            switch(message.newModel) {
            case 0:
               playerGirl = new PlayerJenny(world, ctx.getServerHandler().field_147369_b.getPersistentID());
               break;
            case 1:
               playerGirl = new PlayerEllie(world, ctx.getServerHandler().field_147369_b.getPersistentID());
               break;
            case 2:
               playerGirl = new PlayerBia(world, ctx.getServerHandler().field_147369_b.getPersistentID());
               break;
            case 3:
               playerGirl = new PlayerSlime(world, ctx.getServerHandler().field_147369_b.getPersistentID());
               break;
            case 4:
               playerGirl = new PlayerBee(world, ctx.getServerHandler().field_147369_b.getPersistentID());
               break;
            case 5:
               playerGirl = new PlayerAllie(world, ctx.getServerHandler().field_147369_b.getPersistentID());
            }

            if (playerGirl != null) {
               ((PlayerGirl)playerGirl).func_189654_d(true);
               ((PlayerGirl)playerGirl).field_70145_X = true;
               ((PlayerGirl)playerGirl).field_70159_w = 0.0D;
               ((PlayerGirl)playerGirl).field_70181_x = 0.0D;
               ((PlayerGirl)playerGirl).field_70179_y = 0.0D;
               ((PlayerGirl)playerGirl).func_70107_b(player.field_70165_t, player.field_70163_u + 69.0D, player.field_70161_v);
               world.func_72838_d((Entity)playerGirl);
               ((PlayerGirl)playerGirl).onCreation();
            }
         } else {
            System.out.println("received an invalid message @UpdatePlayerModel :(");
         }

         return null;
      }
   }
}

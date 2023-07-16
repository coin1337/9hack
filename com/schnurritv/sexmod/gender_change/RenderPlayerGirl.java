package com.schnurritv.sexmod.gender_change;

import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirl;
import javax.vecmath.Vector2f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraftforge.client.event.RenderPlayerEvent.Pre;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class RenderPlayerGirl {
   public static final float DONT_RENDER_WITH_THIS_PARTIALTICK = 1.2345679F;

   @SubscribeEvent
   public void renderPlayerGirl(Pre event) {
      if (event.getPartialRenderTick() != 1.2345679F) {
         PlayerGirl.tryPuttingGirlsInTable();
         PlayerGirl girl = (PlayerGirl)PlayerGirl.playerGirlTable.get(event.getEntityPlayer().getPersistentID());
         if (girl != null) {
            event.setCanceled(true);
            RenderManager renderManager = Minecraft.func_71410_x().func_175598_ae();
            EntityPlayer player = event.getEntityPlayer();
            girl.field_70177_z = player.field_70177_z;
            girl.field_70758_at = player.field_70758_at;
            girl.field_70759_as = player.field_70759_as;
            girl.field_70127_C = player.field_70127_C;
            girl.field_70125_A = player.field_70125_A;
            girl.field_70126_B = player.field_70126_B;
            girl.field_70169_q = player.field_70169_q;
            girl.field_70167_r = player.field_70167_r;
            girl.field_70166_s = player.field_70166_s;
            girl.field_70761_aq = player.field_70761_aq;
            girl.field_70760_ar = player.field_70760_ar;
            girl.isSneaking = player.func_70093_af();
            girl.isSprinting = player.func_70051_ag();
            girl.isRiding = player.func_184218_aH();
            girl.field_70122_E = player.field_70122_E;
            girl.isUsingItem = player.func_184605_cv() != 0;
            double x = player.field_70142_S - player.field_70165_t;
            double y = player.field_70161_v - player.field_70136_U;
            double r = 0.017453292519943295D * (double)player.field_70177_z;
            girl.movementVector = new Vector2f((float)(x * Math.cos(r) + y * Math.sin(r)), (float)(x * Math.sin(r) + y * Math.cos(r)));
            float yOffset = 0.0F;
            if (!(Boolean)girl.func_184212_Q().func_187225_a(GirlEntity.SHOULD_BE_AT_TARGET)) {
               if (player.field_82175_bq) {
                  girl.setCurrentAction(GirlEntity.Action.ATTACK);
               }

               if ((player.func_184614_ca().func_77973_b() instanceof ItemBow || player.func_184592_cb().func_77973_b() instanceof ItemBow) && girl.isUsingItem) {
                  girl.setCurrentAction(GirlEntity.Action.BOW);
               }

               if (girl.currentAction() == GirlEntity.Action.BOW && !girl.isUsingItem) {
                  girl.setCurrentAction(GirlEntity.Action.NULL);
               }

               if (girl.currentAction() == GirlEntity.Action.ATTACK && !player.field_82175_bq) {
                  girl.setCurrentAction(GirlEntity.Action.NULL);
               }

               if (girl.currentAction() == GirlEntity.Action.BOW) {
                  girl.field_70177_z = girl.field_70759_as;
                  girl.field_70761_aq = girl.field_70759_as;
                  girl.field_70760_ar = girl.field_70758_at;
               }

               if (girl.isRiding) {
                  yOffset = player.func_184187_bx() instanceof EntityBoat ? 0.4F : 0.2F;
               }
            }

            renderManager.func_188391_a(girl, event.getX(), event.getY() + (double)yOffset, event.getZ(), 90.0F, event.getPartialRenderTick(), false);
         }
      }
   }
}

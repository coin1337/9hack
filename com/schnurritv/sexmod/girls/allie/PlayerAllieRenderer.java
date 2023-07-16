package com.schnurritv.sexmod.girls.allie;

import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirlRenderer;
import com.schnurritv.sexmod.util.PenisMath;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PlayerAllieRenderer extends PlayerGirlRenderer {
   static final float TAIL_WOBBLE_INTENSITY = 8.0F;
   static final float MAX_WOBBLE = 1.68F;
   static final float Y_VALUE_MODIFIER = 5.0F;
   static Collection<PlayerAllieRenderer> renders = new ArrayList();
   double posX = 0.0D;
   double posZ = 0.0D;
   double lastTickPosX = 0.0D;
   double lastTickPosZ = 0.0D;
   float xRotOld = 0.0F;
   float zRotOld = 0.0F;
   float xRot;
   float zRot;
   double oldMovement = 0.0D;
   double movement = 0.0D;

   public PlayerAllieRenderer(RenderManager renderManager, AnimatedGeoModel model) {
      super(renderManager, model);
      renders.add(this);
   }

   protected void setUpSpecialBones(String boneName, GeoBone bone) {
      if (!(Boolean)this.entity.func_184212_Q().func_187225_a(GirlEntity.SHOULD_BE_AT_TARGET)) {
         if ("tail".equals(boneName)) {
            this.addDrag(bone, 0.0F, 0.0F, 1.0F);
         }

         if ("body".equals(boneName)) {
            this.ughIHaveNoClueHowToNameThis(bone);
         }

         if (this.entity.currentAction() != GirlEntity.Action.BOW) {
            if ("armL".equals(boneName)) {
               this.addDrag(bone, 0.0F, -0.34906584F, 0.15F);
            }

            if (this.entity.currentAction() != GirlEntity.Action.ATTACK) {
               if ("armR".equals(boneName)) {
                  this.addDrag(bone, 0.0F, 0.34906584F, 0.15F);
               }

            }
         }
      }
   }

   void addDrag(GeoBone bone, float defaultXRot, float defaultZRot, float dragIntensity) {
      double x = this.posX - this.lastTickPosX;
      double z = this.posZ - this.lastTickPosZ;
      double r = 0.017453292519943295D * (double)this.entity.field_70177_z;
      Vec2f movementVector = new Vec2f((float)(x * Math.cos(r) + z * Math.sin(r)), (float)(-x * Math.sin(r) + z * Math.cos(r)));
      this.xRot = movementVector.field_189983_j * -8.0F;
      this.zRot = movementVector.field_189982_i * 8.0F;
      this.xRot = PenisMath.clamp(this.xRot, -1.68F, 1.68F);
      this.zRot = PenisMath.clamp(this.zRot, -1.68F, 1.68F);
      this.xRot = (float)PenisMath.Lerp((double)this.xRotOld, (double)this.xRot, (double)this.partialTicks);
      this.zRot = (float)PenisMath.Lerp((double)this.zRotOld, (double)this.zRot, (double)this.partialTicks);
      bone.setRotationX(defaultXRot + this.xRot * dragIntensity);
      bone.setRotationZ(defaultZRot + this.zRot * dragIntensity);
   }

   void ughIHaveNoClueHowToNameThis(GeoBone bone) {
      double x = this.posX - this.lastTickPosX;
      double z = this.posZ - this.lastTickPosZ;
      this.movement = (Math.abs(x) + Math.abs(z)) * 5.0D;
      this.movement = (double)PenisMath.clamp((float)this.movement, 0.0F, 1.0F);
      bone.setPositionY((float)PenisMath.cosineInterpolation(5.0D, 0.0D, PenisMath.Lerp(this.oldMovement, this.movement, (double)this.partialTicks)));
      if (this.entity instanceof PlayerAllie) {
         ((PlayerAllie)((PlayerAllie)this.entity)).extraNameTagHeight = (float)PenisMath.cosineInterpolation(0.30000001192092896D, 0.0D, PenisMath.Lerp(this.oldMovement, this.movement, (double)this.partialTicks));
      }

   }

   void update() {
      if (this.entity != null) {
         this.xRotOld = this.xRot;
         this.zRotOld = this.zRot;
         this.oldMovement = this.movement;
         if (this.entity.getOwner() != null) {
            EntityPlayer player = this.girl.field_70170_p.func_152378_a(this.entity.getOwner());
            if (player != null) {
               this.lastTickPosX = this.posX;
               this.lastTickPosZ = this.posZ;
               this.posX = player.field_70165_t;
               this.posZ = player.field_70161_v;
            }
         }
      }
   }

   public void doRender(GirlEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   @EventBusSubscriber
   public static class EventHandler {
      @SubscribeEvent
      public void tick(ClientTickEvent event) {
         Iterator var2 = PlayerAllieRenderer.renders.iterator();

         while(var2.hasNext()) {
            PlayerAllieRenderer renderer = (PlayerAllieRenderer)var2.next();
            renderer.update();
         }

      }
   }
}

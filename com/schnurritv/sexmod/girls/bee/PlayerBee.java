package com.schnurritv.sexmod.girls.bee;

import com.schnurritv.sexmod.Packets.ClearAnimationCache;
import com.schnurritv.sexmod.Packets.SendCompanionHome;
import com.schnurritv.sexmod.events.HandlePlayerMovement;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirl;
import com.schnurritv.sexmod.gui.Sex.BlackScreenUI;
import com.schnurritv.sexmod.gui.Sex.SexUI;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import com.schnurritv.sexmod.util.Handlers.SoundsHandler;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public class PlayerBee extends PlayerGirl {
   protected PlayerBee(World worldIn) {
      super(worldIn);
   }

   public PlayerBee(World worldIn, UUID player) {
      super(worldIn, player);
   }

   public void onCreation() {
      this.allowOwnerFlying(true);
   }

   public void onDeletion() {
      this.allowOwnerFlying(false);
   }

   public float getNameTagHeight() {
      return 1.4F;
   }

   public void startStandingSex(String action, UUID male) {
      this.prepareAction(male);
      this.field_70180_af.func_187227_b(CURRENT_MODEL, 0);
      this.setCurrentAction(GirlEntity.Action.CITIZEN_START);
      EntityPlayer player = this.field_70170_p.func_152378_a(male);
      player.func_70634_a(this.field_70165_t, this.getRenderPos().field_72448_b, this.field_70161_v + 0.800000011920929D);
   }

   public boolean openMenu(EntityPlayer player) {
      renderMenu(player, this, new String[]{"action.names.sex"}, false);
      return true;
   }

   protected void thrust() {
      if (this.currentAction() == GirlEntity.Action.CITIZEN_FAST || this.currentAction() == GirlEntity.Action.CITIZEN_SLOW) {
         this.playerIsThrusting = true;
         if (this.currentAction() == GirlEntity.Action.CITIZEN_FAST) {
            PacketHandler.INSTANCE.sendToServer(new ClearAnimationCache(this.girlId()));
         } else {
            this.setCurrentAction(GirlEntity.Action.CITIZEN_FAST);
         }
      }

   }

   protected void cum() {
      if (this.currentAction() == GirlEntity.Action.CITIZEN_FAST || this.currentAction() == GirlEntity.Action.CITIZEN_SLOW) {
         this.playerIsCumming = true;
         this.setCurrentAction(GirlEntity.Action.CITIZEN_CUM);
      }

   }

   protected <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
      String var2 = event.getController().getName();
      byte var3 = -1;
      switch(var2.hashCode()) {
      case -1422950858:
         if (var2.equals("action")) {
            var3 = 1;
         }
         break;
      case -103677777:
         if (var2.equals("movement")) {
            var3 = 0;
         }
      }

      switch(var3) {
      case 0:
         if (this.currentAction() != GirlEntity.Action.NULL) {
            this.createAnimation("animation.bee.null", true, event);
         } else {
            this.createAnimation("animation.bee.idle", true, event);
         }
         break;
      case 1:
         switch(this.currentAction()) {
         case NULL:
            this.createAnimation("animation.bee.null", false, event);
            break;
         case CITIZEN_START:
            this.createAnimation("animation.bee.sex_start", false, event);
            break;
         case CITIZEN_SLOW:
            this.createAnimation("animation.bee.sex_slow", true, event);
            break;
         case CITIZEN_FAST:
            this.createAnimation("animation.bee.sex_fast", true, event);
            break;
         case CITIZEN_CUM:
            this.createAnimation("animation.bee.sex_cum", false, event);
            break;
         case THROW_PEARL:
            this.createAnimation("animation.bee.throw_pearl", true, event);
            break;
         case ATTACK:
            this.createAnimation("animation.bee.attack" + this.nextAttack, false, event);
            break;
         case BOW:
            this.createAnimation("animation.bee.bowcharge", false, event);
            System.out.println("bow");
            break;
         case RIDE:
            this.createAnimation("animation.bee.ride", true, event);
         }
      }

      return PlayState.CONTINUE;
   }

   public void registerControllers(AnimationData data) {
      if (this.actionController == null) {
         this.setUpControllers();
      }

      AnimationController.ISoundListener soundListener = (event) -> {
         String var2 = event.sound;
         byte var3 = -1;
         switch(var2.hashCode()) {
         case -1961942550:
            if (var2.equals("attackDone")) {
               var3 = 1;
            }
            break;
         case -1825955452:
            if (var2.equals("sex_cumDone")) {
               var3 = 11;
            }
            break;
         case -1825715502:
            if (var2.equals("sex_cumMSG1")) {
               var3 = 9;
            }
            break;
         case -1643193842:
            if (var2.equals("sex_fastReady")) {
               var3 = 6;
            }
            break;
         case -676816985:
            if (var2.equals("attackSound")) {
               var3 = 0;
            }
            break;
         case -191960649:
            if (var2.equals("sex_fastDone")) {
               var3 = 7;
            }
            break;
         case -191720699:
            if (var2.equals("sex_fastMSG1")) {
               var3 = 4;
            }
            break;
         case 106540102:
            if (var2.equals("pearl")) {
               var3 = 2;
            }
            break;
         case 307064358:
            if (var2.equals("resetCumPercentage")) {
               var3 = 3;
            }
            break;
         case 561334891:
            if (var2.equals("sex_startDone")) {
               var3 = 8;
            }
            break;
         case 561574841:
            if (var2.equals("sex_startMSG1")) {
               var3 = 5;
            }
            break;
         case 1319834923:
            if (var2.equals("blackscreen")) {
               var3 = 10;
            }
         }

         switch(var3) {
         case 0:
            this.playSoundAroundHer(SoundEvents.field_187727_dV);
            break;
         case 1:
            this.setCurrentAction(GirlEntity.Action.NULL);
            if (++this.nextAttack == 3) {
               this.nextAttack = 0;
            }
            break;
         case 2:
            if (this.isClosestPlayer() && this.currentAction() == GirlEntity.Action.THROW_PEARL) {
               PacketHandler.INSTANCE.sendToServer(new SendCompanionHome(this.girlId()));
            }
            break;
         case 3:
            if (this.belongsToPlayer()) {
               SexUI.resetCumPercentage();
            }
            break;
         case 4:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING));
            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.03999999910593033D);
            }
            break;
         case 5:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING));
            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.019999999552965164D);
            }
            break;
         case 6:
            this.playerIsThrusting = false;
            break;
         case 7:
            this.playerIsThrusting = HandlePlayerMovement.isThrusting;
            if (this.playerIsThrusting) {
               break;
            }
         case 8:
            this.setCurrentAction(GirlEntity.Action.CITIZEN_SLOW);
            if (this.belongsToPlayer()) {
               SexUI.shouldBeRendered = true;
            }
            break;
         case 9:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_CUMINFLATION), 2.0F);
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING));
            break;
         case 10:
            if (this.belongsToPlayer()) {
               BlackScreenUI.activate();
            }
            break;
         case 11:
            if (this.belongsToPlayer()) {
               SexUI.resetCumPercentage();
               this.resetGirl();
            }
         }

      };
      this.actionController.registerSoundListener(soundListener);
      data.addAnimationController(this.actionController);
      data.addAnimationController(this.movementController);
   }
}

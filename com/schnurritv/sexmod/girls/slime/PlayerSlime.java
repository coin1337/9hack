package com.schnurritv.sexmod.girls.slime;

import com.schnurritv.sexmod.Packets.ClearAnimationCache;
import com.schnurritv.sexmod.Packets.JennyAwaitPlayerDoggy;
import com.schnurritv.sexmod.Packets.SetPlayerMovement;
import com.schnurritv.sexmod.Packets.SetSlimePregnant;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirl;
import com.schnurritv.sexmod.gui.Sex.BlackScreenUI;
import com.schnurritv.sexmod.gui.Sex.SexUI;
import com.schnurritv.sexmod.util.Reference;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import com.schnurritv.sexmod.util.Handlers.SoundsHandler;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public class PlayerSlime extends PlayerGirl {
   boolean flySwitch = false;
   int flip = 0;

   protected PlayerSlime(World worldIn) {
      super(worldIn);
   }

   public PlayerSlime(World worldIn, UUID player) {
      super(worldIn, player);
   }

   public float getNameTagHeight() {
      return 1.6F;
   }

   public boolean hasBedAnimation() {
      return false;
   }

   public void startStandingSex(String action, UUID male) {
      if ("action.names.blowjob".equals(action)) {
         this.prepareAction(male);
         this.setCurrentAction(GirlEntity.Action.SUCKBLOWJOB);
      }

   }

   public boolean openMenu(EntityPlayer player) {
      renderMenu(player, this, new String[]{"action.names.blowjob"}, false);
      return true;
   }

   protected void thrust() {
      if (this.currentAction() != GirlEntity.Action.SUCKBLOWJOB && this.currentAction() != GirlEntity.Action.THRUSTBLOWJOB) {
         if (this.currentAction() == GirlEntity.Action.DOGGYSLOW || this.currentAction() == GirlEntity.Action.DOGGYFAST) {
            this.playerIsThrusting = true;
            if (this.currentAction() == GirlEntity.Action.DOGGYFAST) {
               PacketHandler.INSTANCE.sendToServer(new ClearAnimationCache(this.girlId()));
            } else {
               this.setCurrentAction(GirlEntity.Action.DOGGYFAST);
            }
         }
      } else {
         this.playerIsThrusting = true;
         if (this.currentAction() == GirlEntity.Action.THRUSTBLOWJOB) {
            PacketHandler.INSTANCE.sendToServer(new ClearAnimationCache(this.girlId()));
         } else {
            this.setCurrentAction(GirlEntity.Action.THRUSTBLOWJOB);
         }
      }

   }

   protected void cum() {
      if (this.currentAction() != GirlEntity.Action.SUCKBLOWJOB && this.currentAction() != GirlEntity.Action.THRUSTBLOWJOB) {
         if (this.currentAction() == GirlEntity.Action.DOGGYSLOW || this.currentAction() == GirlEntity.Action.DOGGYFAST) {
            this.playerIsCumming = true;
            this.setCurrentAction(GirlEntity.Action.DOGGYCUM);
         }
      } else {
         this.playerIsCumming = true;
         this.setCurrentAction(GirlEntity.Action.CUMBLOWJOB);
      }

   }

   public void func_70619_bc() {
      super.func_70619_bc();
      if (this.currentAction() == GirlEntity.Action.WAITDOGGY) {
         EntityPlayer player = this.getClosestPlayer();
         if (player != null) {
            if (!(player.func_174791_d().func_72438_d(this.getRenderPos()) > 1.0D)) {
               PacketHandler.INSTANCE.sendTo(new SetPlayerMovement(false), (EntityPlayerMP)player);
               this.setPlayer(player.getPersistentID());
               player.field_70177_z = this.targetYaw();
               this.playerYaw = this.targetYaw();
               player.func_70107_b(this.getRenderPos().field_72450_a, this.getRenderPos().field_72448_b, this.getRenderPos().field_72449_c);
               player.func_191958_b(0.0F, 0.0F, 0.0F, 0.0F);
               this.moveCamera(0.0D, 0.0D, 0.4D, 0.0F, 60.0F);
               this.setCurrentAction(GirlEntity.Action.DOGGYSTART);
               player.func_189654_d(true);
               player.field_70145_X = true;
               EntityPlayer girlPlayer = this.field_70170_p.func_152378_a(this.getOwner());
               girlPlayer.func_189654_d(true);
               player.field_70145_X = true;
               player.field_71075_bZ.field_75100_b = true;
               girlPlayer.field_71075_bZ.field_75100_b = true;
            }
         }
      }
   }

   protected <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
      String var2 = event.getController().getName();
      byte var3 = -1;
      switch(var2.hashCode()) {
      case -1422950858:
         if (var2.equals("action")) {
            var3 = 2;
         }
         break;
      case -103677777:
         if (var2.equals("movement")) {
            var3 = 1;
         }
         break;
      case 3128418:
         if (var2.equals("eyes")) {
            var3 = 0;
         }
      }

      switch(var3) {
      case 0:
         if (this.currentAction() != GirlEntity.Action.NULL && this.currentAction().autoBlink) {
            this.createAnimation("animation.slime.fhappy", true, event);
         } else {
            this.createAnimation("animation.slime.null", true, event);
         }
         break;
      case 1:
         if (this.currentAction() != GirlEntity.Action.NULL) {
            this.createAnimation("animation.slime.null", true, event);
         } else if (this.isRiding) {
            this.createAnimation("animation.slime.sit", true, event);
         } else {
            if (this.movementController.getCurrentAnimation() != null && this.movementController.getCurrentAnimation().animationName.contains("fly") && this.field_70122_E) {
               this.flySwitch = !this.flySwitch;
            }

            if (!this.field_70122_E) {
               this.createAnimation("animation.slime.fly" + (this.flySwitch ? "2" : ""), true, event);
            } else if (Math.abs(this.movementVector.x) + Math.abs(this.movementVector.y) > 0.0F) {
               if (this.isSprinting) {
                  this.createAnimation("animation.slime.run", true, event);
               } else if (this.movementVector.y >= -0.1F) {
                  this.createAnimation("animation.slime.walk", true, event);
               } else {
                  this.createAnimation("animation.slime.backwards_walk", true, event);
               }
            } else {
               this.createAnimation("animation.slime.idle", true, event);
            }
         }
         break;
      case 2:
         if (this.currentAction() == GirlEntity.Action.NULL) {
            this.createAnimation("animation.slime.null", true, event);
         } else {
            switch(this.currentAction()) {
            case UNDRESS:
               this.createAnimation("animation.slime.undress", false, event);
               break;
            case DRESS:
               this.createAnimation("animation.slime.dress", false, event);
               break;
            case STRIP:
               this.createAnimation("animation.slime.strip", false, event);
               break;
            case SUCKBLOWJOB:
               this.createAnimation("animation.slime.blowjobsuck", true, event);
               break;
            case THRUSTBLOWJOB:
               this.createAnimation("animation.slime.blowjobthrust", false, event);
               break;
            case CUMBLOWJOB:
               this.createAnimation("animation.slime.blowjobcum", false, event);
               break;
            case STARTDOGGY:
               this.createAnimation("animation.slime.doggygoonbed", false, event);
               break;
            case WAITDOGGY:
               this.createAnimation("animation.slime.doggywait", true, event);
               break;
            case DOGGYSTART:
               this.createAnimation("animation.slime.doggystart", false, event);
               break;
            case DOGGYSLOW:
               this.createAnimation("animation.slime.doggyslow", true, event);
               break;
            case DOGGYFAST:
               this.createAnimation("animation.slime.doggyfast", false, event);
               break;
            case DOGGYCUM:
               this.createAnimation("animation.slime.doggycum", false, event);
               break;
            case ATTACK:
               this.createAnimation("animation.slime.attack" + this.nextAttack, false, event);
               break;
            case BOW:
               this.createAnimation("animation.slime.bowcharge", false, event);
               break;
            case RIDE:
               this.createAnimation("animation.slime.ride", true, event);
               break;
            case SIT:
               this.createAnimation("animation.slime.sit", true, event);
            }
         }
      }

      return PlayState.CONTINUE;
   }

   public void registerControllers(AnimationData data) {
      AnimationController.ISoundListener actionSoundListener = (event) -> {
         String command = event.sound;
         byte var4 = -1;
         switch(command.hashCode()) {
         case -2038339681:
            if (command.equals("doggyslowMSG1")) {
               var4 = 26;
            }
            break;
         case -2038339680:
            if (command.equals("doggyslowMSG2")) {
               var4 = 14;
            }
            break;
         case -1649091690:
            if (command.equals("doggystartDone")) {
               var4 = 25;
            }
            break;
         case -1648851740:
            if (command.equals("doggystartMSG1")) {
               var4 = 20;
            }
            break;
         case -1648851739:
            if (command.equals("doggystartMSG2")) {
               var4 = 21;
            }
            break;
         case -1648851738:
            if (command.equals("doggystartMSG3")) {
               var4 = 22;
            }
            break;
         case -1648851737:
            if (command.equals("doggystartMSG4")) {
               var4 = 23;
            }
            break;
         case -1648851736:
            if (command.equals("doggystartMSG5")) {
               var4 = 24;
            }
            break;
         case -1370194640:
            if (command.equals("bjcBlackScreen")) {
               var4 = 15;
            }
            break;
         case -572151107:
            if (command.equals("doggycumMSG1")) {
               var4 = 29;
            }
            break;
         case -558244113:
            if (command.equals("becomeNude")) {
               var4 = 2;
            }
            break;
         case -291196098:
            if (command.equals("undress")) {
               var4 = 0;
            }
            break;
         case -90697923:
            if (command.equals("bjcDone")) {
               var4 = 16;
            }
            break;
         case -90457973:
            if (command.equals("bjcMSG1")) {
               var4 = 12;
            }
            break;
         case -90457972:
            if (command.equals("bjcMSG2")) {
               var4 = 13;
            }
            break;
         case -85156797:
            if (command.equals("bjiDone")) {
               var4 = 8;
            }
            break;
         case -74998066:
            if (command.equals("bjtDone")) {
               var4 = 9;
            }
            break;
         case -74758116:
            if (command.equals("bjtMSG1")) {
               var4 = 7;
            }
            break;
         case 13829932:
            if (command.equals("doggyfastDone")) {
               var4 = 28;
            }
            break;
         case 14069882:
            if (command.equals("doggyfastMSG1")) {
               var4 = 27;
            }
            break;
         case 95849015:
            if (command.equals("dress")) {
               var4 = 1;
            }
            break;
         case 441346873:
            if (command.equals("doggyfastReady")) {
               var4 = 11;
            }
            break;
         case 1092262223:
            if (command.equals("doggyCumDone")) {
               var4 = 17;
            }
            break;
         case 1662545087:
            if (command.equals("bjiMSG10")) {
               var4 = 4;
            }
            break;
         case 1662545088:
            if (command.equals("bjiMSG11")) {
               var4 = 5;
            }
            break;
         case 1662545089:
            if (command.equals("bjiMSG12")) {
               var4 = 6;
            }
            break;
         case 1982646231:
            if (command.equals("bjtReady")) {
               var4 = 10;
            }
            break;
         case 1988710681:
            if (command.equals("sexUiOn")) {
               var4 = 3;
            }
            break;
         case 2105823406:
            if (command.equals("doggyGoOnBedDone")) {
               var4 = 19;
            }
            break;
         case 2106063356:
            if (command.equals("doggyGoOnBedMSG1")) {
               var4 = 18;
            }
         }

         switch(var4) {
         case 0:
            if (this.isClosestPlayer()) {
               this.field_70180_af.func_187227_b(CURRENT_MODEL, 0);
               this.resetGirl();
            }
            break;
         case 1:
            if (this.isClosestPlayer()) {
               this.field_70180_af.func_187227_b(CURRENT_MODEL, 1);
               this.setCurrentAction((GirlEntity.Action)null);
               this.resetGirl();
            }
            break;
         case 2:
            this.field_70180_af.func_187227_b(CURRENT_MODEL, 0);
            break;
         case 3:
            if (this.belongsToPlayer()) {
               SexUI.shouldBeRendered = true;
            }
            break;
         case 4:
            if (this.belongsToPlayer()) {
               this.moveCamera(-0.4D, -0.8D, -0.2D, 60.0F, -3.0F);
            }
            break;
         case 5:
            this.playerIsThrusting = false;
            this.playSoundAroundHer(SoundEvents.field_187886_fs, 0.5F);
            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.02D);
            }
            break;
         case 6:
            if (Reference.RANDOM.nextInt(5) == 0) {
               this.playSoundAroundHer(SoundEvents.field_187882_fq, 0.5F);
            }

            this.playSoundAroundHer(SoundEvents.field_187886_fs, 0.5F);
            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.02D);
            }
            break;
         case 7:
            this.playSoundAroundHer(SoundEvents.field_187878_fo);
            this.playSoundAroundHer(SoundEvents.field_187874_fm);
            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.04D);
            }
            break;
         case 8:
            this.setCurrentAction(GirlEntity.Action.SUCKBLOWJOB);
            if (this.belongsToPlayer()) {
               SexUI.shouldBeRendered = true;
            }
            break;
         case 9:
            this.setCurrentAction(GirlEntity.Action.SUCKBLOWJOB);
            break;
         case 10:
         case 11:
            this.playerIsThrusting = false;
            break;
         case 12:
            this.playSoundAroundHer(SoundEvents.field_187882_fq);
            break;
         case 13:
            this.playSoundAroundHer(SoundEvents.field_187882_fq);
            if (this.belongsToPlayer()) {
               SexUI.shouldBeRendered = false;
            }
            break;
         case 14:
            this.playSoundAroundHer(SoundEvents.field_187878_fo);
            break;
         case 15:
            if (this.belongsToPlayer()) {
               BlackScreenUI.activate();
            }
            break;
         case 16:
         case 17:
            if (this.belongsToPlayer()) {
               SexUI.resetCumPercentage();
               this.resetGirl();
               PacketHandler.INSTANCE.sendToServer(new SetSlimePregnant(this.girlId(), true));
            }
            break;
         case 18:
            this.playSoundAroundHer(SoundEvents.field_187886_fs);
            this.playerYaw = this.field_70177_z;
            break;
         case 19:
            PacketHandler.INSTANCE.sendToServer(new JennyAwaitPlayerDoggy(this.girlId(), Minecraft.func_71410_x().field_71439_g.getPersistentID()));
            this.setCurrentAction(GirlEntity.Action.WAITDOGGY);
            break;
         case 20:
            this.playSoundAroundHer(SoundsHandler.MISC_TOUCH[0]);
            break;
         case 21:
            this.playSoundAroundHer(SoundsHandler.MISC_TOUCH[1]);
            break;
         case 22:
            this.playSoundAroundHer(SoundEvents.field_187886_fs, 0.25F);
            break;
         case 23:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_SMALLINSERTS), 1.5F);
            if (this.belongsToPlayer()) {
               SexUI.resetCumPercentage();
            }
            break;
         case 24:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING), 0.33F);
            this.playSoundAroundHer(SoundEvents.field_187878_fo);
            break;
         case 25:
            this.setCurrentAction(GirlEntity.Action.DOGGYSLOW);
            if (this.belongsToPlayer()) {
               SexUI.shouldBeRendered = true;
            }
            break;
         case 26:
            this.playerIsThrusting = false;
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING), 0.33F);
            int rand = Reference.RANDOM.nextInt(4);
            if (rand == 0) {
               rand = Reference.RANDOM.nextInt(2);
               if (rand == 0) {
                  this.playSoundAroundHer(SoundEvents.field_187882_fq);
               } else {
                  this.playSoundAroundHer(SoundEvents.field_187886_fs);
               }
            } else {
               this.playSoundAroundHer(SoundEvents.field_187878_fo);
            }

            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.00666D);
            }
            break;
         case 27:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING), 0.75F);
            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.02D);
            }

            ++this.flip;
            if (this.flip % 2 == 0) {
               int random = Reference.RANDOM.nextInt(2);
               if (random == 0) {
                  this.playSoundAroundHer(SoundEvents.field_187882_fq);
               } else {
                  this.playSoundAroundHer(SoundEvents.field_187886_fs);
               }
            } else {
               this.playSoundAroundHer(SoundEvents.field_187878_fo);
            }
            break;
         case 28:
            this.setCurrentAction(GirlEntity.Action.DOGGYSLOW);
            break;
         case 29:
            this.playSoundAroundHer(SoundsHandler.MISC_CUMINFLATION[0], 4.0F);
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING), 2.0F);
            this.playSoundAroundHer(SoundEvents.field_187874_fm);
         }

      };
      this.actionController.registerSoundListener(actionSoundListener);
      data.addAnimationController(this.actionController);
      data.addAnimationController(this.eyesController);
      data.addAnimationController(this.movementController);
   }
}

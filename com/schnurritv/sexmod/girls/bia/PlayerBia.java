package com.schnurritv.sexmod.girls.bia;

import com.schnurritv.sexmod.Packets.SendCompanionHome;
import com.schnurritv.sexmod.Packets.SetPlayerMovement;
import com.schnurritv.sexmod.events.HandlePlayerMovement;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirl;
import com.schnurritv.sexmod.gui.Sex.BlackScreenUI;
import com.schnurritv.sexmod.gui.Sex.SexUI;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import com.schnurritv.sexmod.util.Handlers.SoundsHandler;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public class PlayerBia extends PlayerGirl {
   boolean flySwitch = false;

   public PlayerBia(World worldIn) {
      super(worldIn);
   }

   public PlayerBia(World worldIn, UUID player) {
      super(worldIn, player);
   }

   public float getNameTagHeight() {
      return 1.5F;
   }

   public void startBedAnimation() {
      this.setCurrentAction(GirlEntity.Action.ANAL_PREPARE);
      this.field_70180_af.func_187227_b(CURRENT_MODEL, 0);
      this.playerYaw = (Float)this.field_70180_af.func_187225_a(TARGET_YAW);
   }

   public void startStandingSex(String action, UUID male) {
      if ("action.names.headpat".equals(action)) {
         this.prepareAction(male);
         this.setCurrentAction(GirlEntity.Action.HEAD_PAT);
      }

   }

   public void func_70619_bc() {
      super.func_70619_bc();
      if (this.currentAction() == GirlEntity.Action.ANAL_WAIT) {
         EntityPlayer player = this.getClosestPlayer();
         if (player != null && player.func_70011_f(this.getRenderPos().field_72450_a, this.getRenderPos().field_72448_b, this.getRenderPos().field_72449_c) < 1.0D) {
            if (this.isGirlPlayer(player.getPersistentID())) {
               player.func_145747_a(new TextComponentString(TextFormatting.DARK_PURPLE + "sowy no lesbo action yet uwu"));
               return;
            }

            PacketHandler.INSTANCE.sendTo(new SetPlayerMovement(false), (EntityPlayerMP)player);
            player.func_70080_a(this.field_70165_t, this.getRenderPos().field_72448_b, this.field_70161_v, this.field_70177_z, this.field_70125_A);
            this.setPlayer(player.getPersistentID());
            this.moveCamera(-0.3D, -1.0D, -0.5D, 0.0F, this.field_70125_A);
            this.setCurrentAction(GirlEntity.Action.ANAL_START);
            player.field_71075_bZ.field_75100_b = true;
            this.field_70170_p.func_152378_a(this.getOwner()).field_71075_bZ.field_75100_b = true;
         }
      }

   }

   public boolean openMenu(EntityPlayer player) {
      renderMenu(player, this, new String[]{"action.names.headpat"}, true);
      return true;
   }

   protected void thrust() {
      if (this.currentAction() == GirlEntity.Action.ANAL_FAST || this.currentAction() == GirlEntity.Action.ANAL_SLOW) {
         this.playerIsThrusting = true;
         this.setCurrentAction(GirlEntity.Action.ANAL_FAST);
      }

   }

   protected void cum() {
      if (this.currentAction() == GirlEntity.Action.ANAL_SLOW || this.currentAction() == GirlEntity.Action.ANAL_FAST) {
         this.playerIsCumming = true;
         this.setCurrentAction(GirlEntity.Action.ANAL_CUM);
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
         if (this.currentAction() == GirlEntity.Action.NULL && this.currentAction().autoBlink) {
            this.createAnimation("animation.bia.fhappy", true, event);
         } else {
            this.createAnimation("animation.bia.null", true, event);
         }
         break;
      case 1:
         if (this.currentAction() != GirlEntity.Action.NULL) {
            this.createAnimation("animation.bia.null", true, event);
         } else if (this.isRiding) {
            this.createAnimation("animation.bia.sit", true, event);
         } else {
            if (this.movementController.getCurrentAnimation() != null && this.movementController.getCurrentAnimation().animationName.contains("fly") && this.field_70122_E) {
               this.flySwitch = !this.flySwitch;
            }

            if (!this.field_70122_E) {
               this.createAnimation("animation.bia.fly" + (this.flySwitch ? "2" : ""), true, event);
            } else if (Math.abs(this.movementVector.x) + Math.abs(this.movementVector.y) > 0.0F) {
               if (this.isSprinting) {
                  this.createAnimation("animation.bia.run", true, event);
               } else if (this.movementVector.y >= -0.1F) {
                  this.createAnimation("animation.bia.fastwalk", true, event);
               } else {
                  this.createAnimation("animation.bia.backwards_walk", true, event);
               }
            } else {
               this.createAnimation("animation.bia.idle", true, event);
            }
         }
         break;
      case 2:
         switch(this.currentAction()) {
         case NULL:
            this.createAnimation("animation.bia.null", true, event);
            break;
         case STRIP:
            this.createAnimation("animation.bia.strip", false, event);
            break;
         case ATTACK:
            this.createAnimation("animation.bia.attack" + this.nextAttack, false, event);
            break;
         case BOW:
            this.createAnimation("animation.bia.bowcharge", false, event);
            break;
         case RIDE:
            this.createAnimation("animation.bia.ride", true, event);
            break;
         case SIT:
            this.createAnimation("animation.bia.sit", true, event);
            break;
         case THROW_PEARL:
            this.createAnimation("animation.bia.throwpearl", false, event);
            break;
         case DOWNED:
            this.createAnimation("animation.bia.downed", true, event);
            break;
         case TALK_HORNY:
            this.createAnimation("animation.bia.talk_horny", false, event);
            break;
         case TALK_IDLE:
            this.createAnimation("animation.bia.talk_idle", true, event);
            break;
         case TALK_RESPONSE:
            this.createAnimation("animation.bia.talk_response", true, event);
            break;
         case ANAL_PREPARE:
            this.createAnimation("animation.bia.anal_prepare", false, event);
            break;
         case ANAL_WAIT:
            this.createAnimation("animation.bia.anal_wait", true, event);
            break;
         case ANAL_START:
            this.createAnimation("animation.bia.anal_start", true, event);
            break;
         case ANAL_SLOW:
            this.createAnimation("animation.bia.anal_slow", true, event);
            break;
         case ANAL_FAST:
            this.createAnimation("animation.bia.anal_fast", true, event);
            break;
         case ANAL_CUM:
            this.createAnimation("animation.bia.anal_cum", false, event);
            break;
         case HEAD_PAT:
            this.createAnimation("animation.bia.headpat", false, event);
         }
      }

      return PlayState.CONTINUE;
   }

   @SideOnly(Side.CLIENT)
   public void registerControllers(AnimationData data) {
      if (this.actionController == null) {
         this.setUpControllers();
      }

      AnimationController.ISoundListener actionSoundListener = (event) -> {
         String var2 = event.sound;
         byte var3 = -1;
         switch(var2.hashCode()) {
         case -1961942550:
            if (var2.equals("attackDone")) {
               var3 = 1;
            }
            break;
         case -1823121897:
            if (var2.equals("anal_cumBlackScreen")) {
               var3 = 25;
            }
            break;
         case -794340298:
            if (var2.equals("anal_cumDone")) {
               var3 = 26;
            }
            break;
         case -794100347:
            if (var2.equals("anal_cumMSG2")) {
               var3 = 24;
            }
            break;
         case -712768382:
            if (var2.equals("anal_prepareDone")) {
               var3 = 17;
            }
            break;
         case -712528432:
            if (var2.equals("anal_prepareMSG1")) {
               var3 = 15;
            }
            break;
         case -712528431:
            if (var2.equals("anal_prepareMSG2")) {
               var3 = 16;
            }
            break;
         case -676816985:
            if (var2.equals("attackSound")) {
               var3 = 0;
            }
            break;
         case -558244113:
            if (var2.equals("becomeNude")) {
               var3 = 2;
            }
            break;
         case -328831560:
            if (var2.equals("anal_slowMSG1")) {
               var3 = 20;
            }
            break;
         case -193947491:
            if (var2.equals("anal_startDone")) {
               var3 = 23;
            }
            break;
         case -193707541:
            if (var2.equals("anal_startMSG1")) {
               var3 = 18;
            }
            break;
         case -193707540:
            if (var2.equals("anal_startMSG2")) {
               var3 = 21;
            }
            break;
         case -188461382:
            if (var2.equals("stripDone")) {
               var3 = 3;
            }
            break;
         case -188221432:
            if (var2.equals("stripMSG1")) {
               var3 = 4;
            }
            break;
         case 106540102:
            if (var2.equals("pearl")) {
               var3 = 6;
            }
            break;
         case 769839126:
            if (var2.equals("talk_responseDone")) {
               var3 = 14;
            }
            break;
         case 770079076:
            if (var2.equals("talk_responseMSG1")) {
               var3 = 11;
            }
            break;
         case 770079077:
            if (var2.equals("talk_responseMSG2")) {
               var3 = 12;
            }
            break;
         case 770079078:
            if (var2.equals("talk_responseMSG3")) {
               var3 = 13;
            }
            break;
         case 1723338053:
            if (var2.equals("anal_fastDone")) {
               var3 = 22;
            }
            break;
         case 1723578003:
            if (var2.equals("anal_fastMSG1")) {
               var3 = 19;
            }
            break;
         case 1888031973:
            if (var2.equals("headpatDone")) {
               var3 = 31;
            }
            break;
         case 1888271923:
            if (var2.equals("headpatMSG1")) {
               var3 = 27;
            }
            break;
         case 1888271924:
            if (var2.equals("headpatMSG2")) {
               var3 = 28;
            }
            break;
         case 1888271925:
            if (var2.equals("headpatMSG3")) {
               var3 = 29;
            }
            break;
         case 1888271926:
            if (var2.equals("headpatMSG4")) {
               var3 = 30;
            }
            break;
         case 1902553459:
            if (var2.equals("talk_hornyMSG1")) {
               var3 = 7;
            }
            break;
         case 1902553460:
            if (var2.equals("talk_hornyMSG2")) {
               var3 = 8;
            }
            break;
         case 1902553461:
            if (var2.equals("talk_hornyMSG3")) {
               var3 = 9;
            }
            break;
         case 1902553462:
            if (var2.equals("talk_hornyMSG4")) {
               var3 = 10;
            }
            break;
         case 1988710681:
            if (var2.equals("sexUiOn")) {
               var3 = 5;
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
            this.changeDataParameterFromClient("currentModel", (Integer)this.field_70180_af.func_187225_a(CURRENT_MODEL) == 1 ? "0" : "1");
            break;
         case 3:
            this.resetGirl();
            this.checkFollowUp();
            break;
         case 4:
            this.say("Hihi~");
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_BIA_GIGGLE));
            break;
         case 5:
            if (this.belongsToPlayer()) {
               SexUI.shouldBeRendered = true;
            }
            break;
         case 6:
            PacketHandler.INSTANCE.sendToServer(new SendCompanionHome(this.girlId()));
            break;
         case 7:
            this.sayAround("Heyaaa~");
            this.playSoundAroundHer(SoundsHandler.GIRLS_BIA_HEY[3]);
            break;
         case 8:
            this.sayAround("I am Hornyyyyy~");
            this.playSoundAroundHer(SoundsHandler.GIRLS_BIA_GIGGLE[2]);
            break;
         case 9:
            this.sayAround("So...");
            this.playSoundAroundHer(SoundsHandler.GIRLS_BIA_BREATH[0]);
            break;
         case 10:
            this.sayAround("Are we gonna have some fun nyaa?");
            this.playSoundAroundHer(SoundsHandler.GIRLS_BIA_HUH[0]);
            break;
         case 11:
            this.sayAround("Huh?!...");
            this.playSoundAroundHer(SoundsHandler.GIRLS_BIA_HUH[2]);
            break;
         case 12:
            this.sayAround("I... uhm...");
            this.playSoundAroundHer(SoundsHandler.GIRLS_BIA_BREATH[1]);
            break;
         case 13:
            this.sayAround("yes~");
            this.playSoundAroundHer(SoundsHandler.GIRLS_BIA_GIGGLE[0]);
            break;
         case 14:
            this.resetPlayer();
            if ((Integer)this.field_70180_af.func_187225_a(CURRENT_MODEL) != 0) {
               this.setCurrentAction(GirlEntity.Action.STRIP);
            } else {
               this.checkFollowUp();
            }
            break;
         case 15:
            this.playSoundAroundHer(SoundsHandler.MISC_PLOB[0]);
            break;
         case 16:
            this.playSoundAroundHer(SoundsHandler.MISC_BEDRUSTLE[0]);
            break;
         case 17:
            this.setCurrentAction(GirlEntity.Action.ANAL_WAIT);
            if (this.belongsToPlayer()) {
               SexUI.resetCumPercentage();
            }
            break;
         case 18:
            this.playSoundAroundHer(SoundsHandler.GIRLS_BIA_MMM[3]);
            this.playSoundAroundHer(SoundsHandler.MISC_POUNDING[34]);
            break;
         case 19:
            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.02D);
            }

            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.02D);
            }

            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING), 0.5F);
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_BIA_AHH));
            break;
         case 20:
         case 21:
            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.02D);
            }

            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING), 0.5F);
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_BIA_AHH));
            this.playerIsThrusting = false;
            break;
         case 22:
            this.playerIsThrusting = HandlePlayerMovement.isThrusting;
            if (this.playerIsThrusting || !this.belongsToPlayer()) {
               break;
            }
         case 23:
            this.setCurrentAction(GirlEntity.Action.ANAL_SLOW);
            if (this.belongsToPlayer()) {
               SexUI.shouldBeRendered = true;
            }
            break;
         case 24:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_BIA_AHH));
            break;
         case 25:
            if (this.belongsToPlayer()) {
               BlackScreenUI.activate();
            }
            break;
         case 26:
            if (this.belongsToPlayer()) {
               SexUI.resetCumPercentage();
               this.resetGirl();
            }
            break;
         case 27:
            this.sayAround("Ooh headpats!");
            this.playSoundAroundHer(SoundsHandler.GIRLS_BIA_BREATH[0]);
            break;
         case 28:
            this.sayAround("Hmmm.... :D");
            this.playSoundAroundHer(SoundsHandler.GIRLS_BIA_MMM[0]);
            break;
         case 29:
            this.sayAround("huh...?");
            this.playSoundAroundHer(SoundsHandler.GIRLS_BIA_HUH[0]);
            break;
         case 30:
            this.sayAround("Tanku hehe");
            this.playSoundAroundHer(SoundsHandler.GIRLS_BIA_GIGGLE[1]);
            break;
         case 31:
            this.resetGirl();
         }

      };
      this.actionController.registerSoundListener(actionSoundListener);
      data.addAnimationController(this.movementController);
      data.addAnimationController(this.eyesController);
      data.addAnimationController(this.actionController);
   }
}

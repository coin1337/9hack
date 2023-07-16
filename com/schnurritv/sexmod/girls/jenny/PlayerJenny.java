package com.schnurritv.sexmod.girls.jenny;

import com.schnurritv.sexmod.Packets.ClearAnimationCache;
import com.schnurritv.sexmod.Packets.JennyAwaitPlayerDoggy;
import com.schnurritv.sexmod.Packets.SendCompanionHome;
import com.schnurritv.sexmod.Packets.SetPlayerMovement;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirl;
import com.schnurritv.sexmod.gui.Sex.BlackScreenUI;
import com.schnurritv.sexmod.gui.Sex.SexUI;
import com.schnurritv.sexmod.util.Reference;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import com.schnurritv.sexmod.util.Handlers.SoundsHandler;
import java.util.UUID;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public class PlayerJenny extends PlayerGirl {
   boolean flySwitch = false;
   int flip = 0;
   boolean paizuriCamSide = false;

   protected PlayerJenny(World worldIn) {
      super(worldIn);
   }

   public PlayerJenny(World worldIn, UUID player) {
      super(worldIn, player);
   }

   public float getNameTagHeight() {
      return 1.75F;
   }

   public void startBedAnimation() {
      this.setCurrentAction(GirlEntity.Action.STARTDOGGY);
      this.field_70180_af.func_187227_b(CURRENT_MODEL, 0);
      this.playerYaw = (Float)this.field_70180_af.func_187225_a(TARGET_YAW);
   }

   public void startStandingSex(String action, UUID male) {
      if ("action.names.boobjob".equals(action)) {
         this.prepareAction(male);
         this.field_70180_af.func_187227_b(CURRENT_MODEL, 0);
         this.setCurrentAction(GirlEntity.Action.PAIZURI_START);
      }

      if ("action.names.blowjob".equals(action)) {
         this.prepareAction(male);
         this.setCurrentAction(GirlEntity.Action.STARTBLOWJOB);
      }

   }

   public void func_70619_bc() {
      super.func_70619_bc();
      if (this.currentAction() == GirlEntity.Action.WAITDOGGY) {
         EntityPlayer closestPlayer = this.getClosestPlayer();
         if (closestPlayer != null && closestPlayer.func_70011_f(this.getRenderPos().field_72450_a, this.getRenderPos().field_72448_b, this.getRenderPos().field_72449_c) < 1.0D) {
            if (this.isGirlPlayer(closestPlayer.getPersistentID())) {
               closestPlayer.func_145747_a(new TextComponentString(TextFormatting.DARK_PURPLE + "sowy no lesbo action yet uwu"));
               return;
            }

            this.setPlayer(closestPlayer.getPersistentID());
            closestPlayer.func_70634_a(this.func_174791_d().field_72450_a, this.getRenderPos().field_72448_b, this.func_174791_d().field_72449_c);
            this.TurnPlayerIntoCamera((EntityPlayerMP)closestPlayer, false);
            closestPlayer.func_191958_b(0.0F, 0.0F, 0.0F, 0.0F);
            closestPlayer.field_71075_bZ.field_75100_b = true;
            this.field_70170_p.func_152378_a(this.getOwner()).field_71075_bZ.field_75100_b = true;
            this.moveCamera(0.0D, 0.0D, 0.4D, 0.0F, 60.0F);
            this.playerCamPos = null;
            this.setCurrentAction(GirlEntity.Action.DOGGYSTART);
            PacketHandler.INSTANCE.sendTo(new SetPlayerMovement(false), (EntityPlayerMP)closestPlayer);
         }
      }

   }

   public boolean openMenu(EntityPlayer player) {
      renderMenu(player, this, new String[]{"action.names.blowjob", "action.names.boobjob"}, true);
      return true;
   }

   protected void thrust() {
      if (this.currentAction() == GirlEntity.Action.DOGGYSLOW || this.currentAction() == GirlEntity.Action.DOGGYFAST) {
         this.playerIsThrusting = true;
         if (this.currentAction() == GirlEntity.Action.DOGGYFAST) {
            PacketHandler.INSTANCE.sendToServer(new ClearAnimationCache(this.girlId()));
         } else {
            this.setCurrentAction(GirlEntity.Action.DOGGYFAST);
         }
      }

      if (this.currentAction() == GirlEntity.Action.SUCKBLOWJOB || this.currentAction() == GirlEntity.Action.THRUSTBLOWJOB) {
         this.playerIsThrusting = true;
         if (this.currentAction() == GirlEntity.Action.THRUSTBLOWJOB) {
            PacketHandler.INSTANCE.sendToServer(new ClearAnimationCache(this.girlId()));
         } else {
            this.setCurrentAction(GirlEntity.Action.THRUSTBLOWJOB);
         }
      }

      if (this.currentAction() == GirlEntity.Action.PAIZURI_SLOW || this.currentAction() == GirlEntity.Action.PAIZURI_FAST) {
         this.playerIsThrusting = true;
         if (this.paizuriCamSide) {
            this.paizuriCamSide = false;
            this.moveCamera(0.0D, 0.0D, 0.0D, 0.0F, 70.0F);
         }

         if (this.currentAction() == GirlEntity.Action.PAIZURI_FAST) {
            PacketHandler.INSTANCE.sendToServer(new ClearAnimationCache(this.girlId()));
         } else {
            this.setCurrentAction(GirlEntity.Action.PAIZURI_FAST);
         }
      }

   }

   protected void cum() {
      if (this.currentAction() != GirlEntity.Action.DOGGYSLOW && this.currentAction() != GirlEntity.Action.DOGGYFAST) {
         if (this.currentAction() != GirlEntity.Action.SUCKBLOWJOB && this.currentAction() != GirlEntity.Action.THRUSTBLOWJOB) {
            if (this.currentAction() == GirlEntity.Action.PAIZURI_FAST || this.currentAction() == GirlEntity.Action.PAIZURI_SLOW) {
               this.playerIsCumming = true;
               this.setCurrentAction(GirlEntity.Action.PAIZURI_CUM);
            }
         } else {
            this.playerIsCumming = true;
            this.setCurrentAction(GirlEntity.Action.CUMBLOWJOB);
            this.moveCamera(0.0D, 0.0D, 0.0D, 0.0F, 70.0F);
         }
      } else {
         this.playerIsCumming = true;
         this.setCurrentAction(GirlEntity.Action.DOGGYCUM);
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
            this.createAnimation("animation.jenny.fhappy", true, event);
         } else {
            this.createAnimation("animation.jenny.null", true, event);
         }
         break;
      case 1:
         if (this.currentAction() != GirlEntity.Action.NULL) {
            this.createAnimation("animation.jenny.null", true, event);
         } else if (this.isRiding) {
            this.createAnimation("animation.jenny.sit", true, event);
         } else {
            if (this.movementController.getCurrentAnimation() != null && this.movementController.getCurrentAnimation().animationName.contains("fly") && this.field_70122_E) {
               this.flySwitch = !this.flySwitch;
            }

            if (!this.field_70122_E) {
               this.createAnimation("animation.jenny.fly" + (this.flySwitch ? "2" : ""), true, event);
            } else if (Math.abs(this.movementVector.x) + Math.abs(this.movementVector.y) > 0.0F) {
               if (this.isSprinting) {
                  this.createAnimation("animation.jenny.run", true, event);
               } else if (this.movementVector.y >= -0.1F) {
                  this.createAnimation("animation.jenny.fastwalk", true, event);
               } else {
                  this.createAnimation("animation.jenny.backwards_walk", true, event);
               }
            } else {
               this.createAnimation("animation.jenny.idle", true, event);
            }
         }
         break;
      case 2:
         switch(this.currentAction()) {
         case NULL:
            this.createAnimation("animation.jenny.null", true, event);
            break;
         case STRIP:
            this.createAnimation("animation.jenny.strip", false, event);
            break;
         case PAYMENT:
            this.createAnimation("animation.jenny.payment", false, event);
            break;
         case STARTBLOWJOB:
            this.createAnimation("animation.jenny.blowjobintro", false, event);
            break;
         case SUCKBLOWJOB:
            this.createAnimation("animation.jenny.blowjobsuck", true, event);
            break;
         case THRUSTBLOWJOB:
            this.createAnimation("animation.jenny.blowjobthrust", false, event);
            break;
         case CUMBLOWJOB:
            this.createAnimation("animation.jenny.blowjobcum", false, event);
            break;
         case STARTDOGGY:
            this.createAnimation("animation.jenny.doggygoonbed", false, event);
            break;
         case WAITDOGGY:
            this.createAnimation("animation.jenny.doggywait", true, event);
            break;
         case DOGGYSTART:
            this.createAnimation("animation.jenny.doggystart", false, event);
            break;
         case DOGGYSLOW:
            this.createAnimation("animation.jenny.doggyslow", true, event);
            break;
         case DOGGYFAST:
            this.createAnimation("animation.jenny.doggyfast", false, event);
            break;
         case DOGGYCUM:
            this.createAnimation("animation.jenny.doggycum", false, event);
            break;
         case ATTACK:
            this.createAnimation("animation.jenny.attack" + this.nextAttack, false, event);
            break;
         case BOW:
            this.createAnimation("animation.jenny.bowcharge", false, event);
            break;
         case RIDE:
            this.createAnimation("animation.jenny.ride", true, event);
            break;
         case SIT:
            this.createAnimation("animation.jenny.sit", true, event);
            break;
         case THROW_PEARL:
            this.createAnimation("animation.jenny.throwpearl", false, event);
            break;
         case DOWNED:
            this.createAnimation("animation.jenny.downed", true, event);
            break;
         case PAIZURI_START:
            this.createAnimation("animation.jenny.paizuri_start", false, event);
            break;
         case PAIZURI_SLOW:
            this.createAnimation("animation.jenny.paizuri_slow", true, event);
            break;
         case PAIZURI_FAST:
            this.createAnimation("animation.jenny.paizuri_fast", true, event);
            break;
         case PAIZURI_CUM:
            this.createAnimation("animation.jenny.paizuri_cum", false, event);
         }
      }

      return PlayState.CONTINUE;
   }

   public void registerControllers(AnimationData data) {
      if (this.actionController == null) {
         this.setUpControllers();
      }

      AnimationController.ISoundListener actionSoundListener = (event) -> {
         String var2 = event.sound;
         byte var3 = -1;
         switch(var2.hashCode()) {
         case -2038339681:
            if (var2.equals("doggyslowMSG1")) {
               var3 = 51;
            }
            break;
         case -2038339680:
            if (var2.equals("doggyslowMSG2")) {
               var3 = 52;
            }
            break;
         case -1961942550:
            if (var2.equals("attackDone")) {
               var3 = 1;
            }
            break;
         case -1649091690:
            if (var2.equals("doggystartDone")) {
               var3 = 50;
            }
            break;
         case -1648851740:
            if (var2.equals("doggystartMSG1")) {
               var3 = 45;
            }
            break;
         case -1648851739:
            if (var2.equals("doggystartMSG2")) {
               var3 = 46;
            }
            break;
         case -1648851738:
            if (var2.equals("doggystartMSG3")) {
               var3 = 47;
            }
            break;
         case -1648851737:
            if (var2.equals("doggystartMSG4")) {
               var3 = 48;
            }
            break;
         case -1648851736:
            if (var2.equals("doggystartMSG5")) {
               var3 = 49;
            }
            break;
         case -1540860248:
            if (var2.equals("paymentDone")) {
               var3 = 10;
            }
            break;
         case -1540620298:
            if (var2.equals("paymentMSG1")) {
               var3 = 5;
            }
            break;
         case -1540620297:
            if (var2.equals("paymentMSG2")) {
               var3 = 6;
            }
            break;
         case -1540620296:
            if (var2.equals("paymentMSG3")) {
               var3 = 7;
            }
            break;
         case -1540620295:
            if (var2.equals("paymentMSG4")) {
               var3 = 9;
            }
            break;
         case -1370194640:
            if (var2.equals("bjcBlackScreen")) {
               var3 = 36;
            }
            break;
         case -1149499193:
            if (var2.equals("boobjob_camera")) {
               var3 = 61;
            }
            break;
         case -872965157:
            if (var2.equals("paizuriSlowMSG1")) {
               var3 = 64;
            }
            break;
         case -676816985:
            if (var2.equals("attackSound")) {
               var3 = 0;
            }
            break;
         case -669376408:
            if (var2.equals("paizuri_cumDone")) {
               var3 = 38;
            }
            break;
         case -572151107:
            if (var2.equals("doggycumMSG1")) {
               var3 = 55;
            }
            break;
         case -572151106:
            if (var2.equals("doggycumMSG2")) {
               var3 = 56;
            }
            break;
         case -572151105:
            if (var2.equals("doggycumMSG3")) {
               var3 = 57;
            }
            break;
         case -572151104:
            if (var2.equals("doggycumMSG4")) {
               var3 = 58;
            }
            break;
         case -572151103:
            if (var2.equals("doggycumMSG5")) {
               var3 = 59;
            }
            break;
         case -558244113:
            if (var2.equals("becomeNude")) {
               var3 = 2;
            }
            break;
         case -362733489:
            if (var2.equals("paizuri_startDone")) {
               var3 = 62;
            }
            break;
         case -362282087:
            if (var2.equals("paizuri_startStep")) {
               var3 = 67;
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
         case -90697923:
            if (var2.equals("bjcDone")) {
               var3 = 37;
            }
            break;
         case -90457973:
            if (var2.equals("bjcMSG1")) {
               var3 = 29;
            }
            break;
         case -90457972:
            if (var2.equals("bjcMSG2")) {
               var3 = 30;
            }
            break;
         case -90457971:
            if (var2.equals("bjcMSG3")) {
               var3 = 31;
            }
            break;
         case -90457970:
            if (var2.equals("bjcMSG4")) {
               var3 = 32;
            }
            break;
         case -90457969:
            if (var2.equals("bjcMSG5")) {
               var3 = 33;
            }
            break;
         case -90457968:
            if (var2.equals("bjcMSG6")) {
               var3 = 34;
            }
            break;
         case -90457967:
            if (var2.equals("bjcMSG7")) {
               var3 = 35;
            }
            break;
         case -85156797:
            if (var2.equals("bjiDone")) {
               var3 = 24;
            }
            break;
         case -84916847:
            if (var2.equals("bjiMSG1")) {
               var3 = 11;
            }
            break;
         case -84916846:
            if (var2.equals("bjiMSG2")) {
               var3 = 12;
            }
            break;
         case -84916845:
            if (var2.equals("bjiMSG3")) {
               var3 = 13;
            }
            break;
         case -84916844:
            if (var2.equals("bjiMSG4")) {
               var3 = 14;
            }
            break;
         case -84916843:
            if (var2.equals("bjiMSG5")) {
               var3 = 15;
            }
            break;
         case -84916842:
            if (var2.equals("bjiMSG6")) {
               var3 = 16;
            }
            break;
         case -84916841:
            if (var2.equals("bjiMSG7")) {
               var3 = 17;
            }
            break;
         case -84916840:
            if (var2.equals("bjiMSG8")) {
               var3 = 18;
            }
            break;
         case -84916839:
            if (var2.equals("bjiMSG9")) {
               var3 = 19;
            }
            break;
         case -74998066:
            if (var2.equals("bjtDone")) {
               var3 = 25;
            }
            break;
         case -74758116:
            if (var2.equals("bjtMSG1")) {
               var3 = 23;
            }
            break;
         case 13829932:
            if (var2.equals("doggyfastDone")) {
               var3 = 54;
            }
            break;
         case 14069882:
            if (var2.equals("doggyfastMSG1")) {
               var3 = 53;
            }
            break;
         case 106540102:
            if (var2.equals("pearl")) {
               var3 = 60;
            }
            break;
         case 118020136:
            if (var2.equals("paizuriStartMSG1")) {
               var3 = 65;
            }
            break;
         case 441346873:
            if (var2.equals("doggyfastReady")) {
               var3 = 27;
            }
            break;
         case 738157628:
            if (var2.equals("paizuri_cumStart")) {
               var3 = 68;
            }
            break;
         case 1092262223:
            if (var2.equals("doggyCumDone")) {
               var3 = 39;
            }
            break;
         case 1179444406:
            if (var2.equals("paizuriFastMSG1")) {
               var3 = 63;
            }
            break;
         case 1302251347:
            if (var2.equals("paizuri_fastDone")) {
               var3 = 66;
            }
            break;
         case 1662545087:
            if (var2.equals("bjiMSG10")) {
               var3 = 20;
            }
            break;
         case 1662545088:
            if (var2.equals("bjiMSG11")) {
               var3 = 21;
            }
            break;
         case 1662545089:
            if (var2.equals("bjiMSG12")) {
               var3 = 22;
            }
            break;
         case 1668024441:
            if (var2.equals("paizuriReady")) {
               var3 = 28;
            }
            break;
         case 1982646231:
            if (var2.equals("bjtReady")) {
               var3 = 26;
            }
            break;
         case 1988710681:
            if (var2.equals("sexUiOn")) {
               var3 = 8;
            }
            break;
         case 2105823406:
            if (var2.equals("doggyGoOnBedDone")) {
               var3 = 44;
            }
            break;
         case 2106063356:
            if (var2.equals("doggyGoOnBedMSG1")) {
               var3 = 40;
            }
            break;
         case 2106063357:
            if (var2.equals("doggyGoOnBedMSG2")) {
               var3 = 41;
            }
            break;
         case 2106063358:
            if (var2.equals("doggyGoOnBedMSG3")) {
               var3 = 42;
            }
            break;
         case 2106063359:
            if (var2.equals("doggyGoOnBedMSG4")) {
               var3 = 43;
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
            if (!((String)this.field_70180_af.func_187225_a(ANIMATION_FOLLOW_UP)).equals("boobjob")) {
               this.resetGirl();
            }

            this.checkFollowUp();
            break;
         case 4:
            this.say("Hihi~");
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_JENNY_GIGGLE));
            break;
         case 5:
            this.say("Huh?");
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_HUH[1]);
            break;
         case 6:
            this.playSoundAroundHer(SoundsHandler.MISC_PLOB[0], 0.5F);
            String playerName = "<" + Minecraft.func_71410_x().field_71439_g.func_70005_c_() + "> ";
            String var7 = (String)this.field_70180_af.func_187225_a(ANIMATION_FOLLOW_UP);
            byte var9 = -1;
            switch(var7.hashCode()) {
            case -20842805:
               if (var7.equals("blowjob")) {
                  var9 = 1;
               }
               break;
            case 64419037:
               if (var7.equals("boobjob")) {
                  var9 = 3;
               }
               break;
            case 95761198:
               if (var7.equals("doggy")) {
                  var9 = 2;
               }
               break;
            case 109773592:
               if (var7.equals("strip")) {
                  var9 = 0;
               }
            }

            switch(var9) {
            case 0:
               this.say(playerName + "show Bobs and vegana pls", true);
               return;
            case 1:
               this.say(playerName + "Give me the sucky sucky and these are yours", true);
               return;
            case 2:
               this.say(playerName + "Give me the sex pls :)", true);
               return;
            case 3:
               this.say(playerName + "gib boba OwO", true);
               return;
            default:
               this.say(playerName + "sex pls", true);
               return;
            }
         case 7:
            this.say("Hehe~");
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_JENNY_GIGGLE));
            break;
         case 8:
            if (this.belongsToPlayer()) {
               SexUI.shouldBeRendered = true;
            }
            break;
         case 9:
            this.playSoundAroundHer(SoundsHandler.MISC_PLOB[0], 0.25F);
            break;
         case 10:
            this.checkFollowUp();
            break;
         case 11:
            this.say("What are you...");
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_MMM[8]);
            this.playerYaw = 180.0F;
            if (this.belongsToPlayer()) {
               SexUI.resetCumPercentage();
            }
            break;
         case 12:
            this.say("eh... boys...");
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_LIGHTBREATHING[8]);
            break;
         case 13:
            this.say("OHOhh...!");
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_AFTERSESSIONMOAN[0]);
            break;
         case 14:
            this.playSoundAroundHer(SoundsHandler.MISC_BELLJINGLE[0]);
            break;
         case 15:
            this.say("Was this really necessary?!");
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_HMPH[1], 0.5F);
            if (this.belongsToPlayer()) {
               SexUI.resetCumPercentage();
            }
            break;
         case 16:
            this.say("Oh~");
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_LIGHTBREATHING[8]);
            break;
         case 17:
            this.say("You like it?~");
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_GIGGLE[4]);
            break;
         case 18:
            this.say("<" + Minecraft.func_71410_x().field_71439_g.func_70005_c_() + "> Yee", true);
            this.playSoundAroundHer(SoundsHandler.MISC_PLOB[0], 0.5F);
            break;
         case 19:
            this.say("Hihihi~");
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_GIGGLE[2]);
            break;
         case 20:
            if (this.belongsToPlayer()) {
               this.moveCamera(-0.4D, -0.8D, -0.2D, 60.0F, -3.0F);
            }
            break;
         case 21:
            if (this.belongsToPlayer()) {
               this.playerIsThrusting = false;
            }

            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_JENNY_LIPSOUND));
            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.02D);
            }
            break;
         case 22:
            if (Reference.RANDOM.nextInt(5) == 0) {
               this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_JENNY_BJMOAN));
            }

            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_JENNY_LIPSOUND));
            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.02D);
            }
            break;
         case 23:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_JENNY_MMM));
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_JENNY_LIPSOUND));
            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.04D);
            }
            break;
         case 24:
            this.setCurrentAction(GirlEntity.Action.SUCKBLOWJOB);
            if (this.belongsToPlayer()) {
               SexUI.shouldBeRendered = true;
            }
            break;
         case 25:
            this.setCurrentAction(GirlEntity.Action.SUCKBLOWJOB);
            break;
         case 26:
         case 27:
         case 28:
            if (this.belongsToPlayer()) {
               this.playerIsThrusting = false;
            }
            break;
         case 29:
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_BJMOAN[1]);
            break;
         case 30:
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_BJMOAN[7]);
            if (this.belongsToPlayer()) {
               SexUI.shouldBeRendered = false;
            }
            break;
         case 31:
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_AFTERSESSIONMOAN[1]);
            break;
         case 32:
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_LIGHTBREATHING[0]);
            break;
         case 33:
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_LIGHTBREATHING[1]);
            break;
         case 34:
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_LIGHTBREATHING[2]);
            break;
         case 35:
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_LIGHTBREATHING[3]);
            break;
         case 36:
            if (this.belongsToPlayer()) {
               BlackScreenUI.activate();
            }
            break;
         case 37:
         case 38:
         case 39:
            if (this.belongsToPlayer()) {
               SexUI.resetCumPercentage();
               this.resetGirl();
            }
            break;
         case 40:
            this.playSoundAroundHer(SoundsHandler.MISC_BEDRUSTLE[0]);
            this.playerYaw = this.field_70177_z;
            break;
         case 41:
            this.sayAround("what are you waiting for?~");
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_LIGHTBREATHING[9]);
            break;
         case 42:
            this.sayAround("this ass ain't gonna fuck itself...");
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_GIGGLE[0]);
            break;
         case 43:
            this.playSoundAroundHer(SoundsHandler.MISC_SLAP[0], 0.75F);
            break;
         case 44:
            PacketHandler.INSTANCE.sendToServer(new JennyAwaitPlayerDoggy(this.girlId(), Minecraft.func_71410_x().field_71439_g.getPersistentID()));
            this.setCurrentAction(GirlEntity.Action.WAITDOGGY);
            break;
         case 45:
            this.playSoundAroundHer(SoundsHandler.MISC_TOUCH[0]);
            break;
         case 46:
            this.playSoundAroundHer(SoundsHandler.MISC_TOUCH[1]);
            break;
         case 47:
            this.playSoundAroundHer(SoundsHandler.MISC_BEDRUSTLE[1], 0.5F);
            break;
         case 48:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_SMALLINSERTS));
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_MMM[1]);
            if (this.belongsToPlayer()) {
               SexUI.resetCumPercentage();
            }
            break;
         case 49:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING), 0.33F);
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_JENNY_MOAN));
            break;
         case 50:
            this.setCurrentAction(GirlEntity.Action.DOGGYSLOW);
            if (this.belongsToPlayer()) {
               SexUI.shouldBeRendered = true;
            }
            break;
         case 51:
            if (this.belongsToPlayer()) {
               this.playerIsThrusting = false;
            }

            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING), 0.33F);
            int rand = Reference.RANDOM.nextInt(4);
            if (rand == 0) {
               rand = Reference.RANDOM.nextInt(2);
               if (rand == 0) {
                  this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_JENNY_MMM));
               } else {
                  this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_JENNY_MOAN));
               }
            } else {
               this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_JENNY_HEAVYBREATHING));
            }

            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.00666D);
            }
            break;
         case 52:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_JENNY_LIGHTBREATHING), 0.5F);
            break;
         case 53:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING), 0.75F);
            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.02D);
            }

            ++this.flip;
            if (this.flip % 2 == 0) {
               int random = Reference.RANDOM.nextInt(2);
               if (random == 0) {
                  this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_JENNY_MOAN));
               } else {
                  this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_JENNY_HEAVYBREATHING));
               }
            } else {
               this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_JENNY_AHH));
            }
            break;
         case 54:
            this.setCurrentAction(GirlEntity.Action.DOGGYSLOW);
            break;
         case 55:
            this.playSoundAroundHer(SoundsHandler.MISC_CUMINFLATION[0], 2.0F);
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING), 2.0F);
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_JENNY_MOAN));
            break;
         case 56:
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_HEAVYBREATHING[4]);
            break;
         case 57:
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_HEAVYBREATHING[5]);
            break;
         case 58:
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_HEAVYBREATHING[6]);
            break;
         case 59:
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_HEAVYBREATHING[7]);
            break;
         case 60:
            PacketHandler.INSTANCE.sendToServer(new SendCompanionHome(this.girlId()));
            break;
         case 61:
            if (this.belongsToPlayer() && !this.paizuriCamSide) {
               this.paizuriCamSide = true;
               this.playerYaw = 180.0F;
               this.moveCamera(-0.7D, -0.6D, -0.2D, 60.0F, -3.0F);
            }
            break;
         case 62:
            if (this.belongsToPlayer()) {
               this.setCurrentAction(GirlEntity.Action.PAIZURI_SLOW);
               SexUI.resetCumPercentage();
               SexUI.shouldBeRendered = true;
            }
            break;
         case 63:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING));
            if (this.func_70681_au().nextBoolean()) {
               this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_JENNY_MMM));
            } else {
               this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_JENNY_AHH));
            }

            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.04D);
            }
            break;
         case 64:
         case 65:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING));
            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.02D);
            }
            break;
         case 66:
            this.setCurrentAction(GirlEntity.Action.PAIZURI_SLOW);
            if (this.belongsToPlayer() && !this.paizuriCamSide) {
               this.paizuriCamSide = true;
               this.moveCamera(-0.7D, -0.6D, -0.2D, 60.0F, -3.0F);
            }
            break;
         case 67:
            IBlockState state = this.field_70170_p.func_180495_p(this.func_180425_c().func_177973_b(new Vec3i(0, 1, 0)));
            this.playSoundAroundHer(state.func_177230_c().getSoundType(state, this.field_70170_p, this.func_180425_c(), this).func_185844_d());
            break;
         case 68:
            if (this.belongsToPlayer()) {
               this.playerIsThrusting = false;
               if (!this.paizuriCamSide) {
                  this.moveCamera(-0.7D, -0.6D, -0.2D, 60.0F, -3.0F);
               }
            }
         }

      };
      this.actionController.registerSoundListener(actionSoundListener);
      data.addAnimationController(this.actionController);
      data.addAnimationController(this.movementController);
      data.addAnimationController(this.eyesController);
   }
}

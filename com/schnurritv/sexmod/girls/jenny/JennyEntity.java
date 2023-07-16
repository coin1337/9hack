package com.schnurritv.sexmod.girls.jenny;

import com.daripher.sexmod.client.util.FakeWorld;
import com.schnurritv.sexmod.Packets.ClearAnimationCache;
import com.schnurritv.sexmod.Packets.JennyAwaitPlayerDoggy;
import com.schnurritv.sexmod.Packets.SendCompanionHome;
import com.schnurritv.sexmod.Packets.SendGirlToBed;
import com.schnurritv.sexmod.Packets.SetPlayerMovement;
import com.schnurritv.sexmod.companion.fighter.LookAtNearbyEntity;
import com.schnurritv.sexmod.events.HandlePlayerMovement;
import com.schnurritv.sexmod.gender_change.hornypotion.HornyPotion;
import com.schnurritv.sexmod.girls.base.Fighter;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.gui.Sex.BlackScreenUI;
import com.schnurritv.sexmod.gui.Sex.SexUI;
import com.schnurritv.sexmod.util.PenisMath;
import com.schnurritv.sexmod.util.Reference;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import com.schnurritv.sexmod.util.Handlers.SoundsHandler;
import java.util.UUID;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public class JennyEntity extends Fighter {
   public boolean lookingForBed = false;
   public boolean isPreparingPayment = false;
   public boolean awaitPlayer = false;
   public static final DataParameter<Boolean> isHorny;
   int preparingPaymentTick = 0;
   int bedSearchTick = 0;
   int flip = 0;
   boolean paizuriCamSide = false;

   public JennyEntity(World worldIn) {
      super(worldIn);
      this.func_70105_a(0.49F, 1.95F);
      this.slashSwordRot = 140;
      this.stabSwordRot = 50;
      this.holdBowRot = 140;
      this.swordOffsetStab = new Vec3d(0.0D, -0.029999997854232782D, -0.2D);
   }

   protected String getGirlName() {
      return "Jenny";
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(isHorny, false);
   }

   public float func_70047_e() {
      return 1.64F;
   }

   protected SoundEvent func_184615_bR() {
      return SoundsHandler.random(SoundsHandler.GIRLS_JENNY_SIGH);
   }

   protected SoundEvent func_184601_bQ(DamageSource source) {
      return null;
   }

   public void func_70619_bc() {
      super.func_70619_bc();
      EntityPlayer closestPlayer = this.field_70170_p.func_72890_a(this, 15.0D);
      if (this.awaitPlayer && closestPlayer != null && closestPlayer.func_174791_d().func_72438_d(this.func_174791_d()) < 0.5D) {
         this.awaitPlayer = false;
         this.field_70180_af.func_187227_b(PLAYER_SHE_HAS_SEX_WITH, this.field_70170_p.func_72890_a(this, 15.0D).getPersistentID().toString());
         EntityPlayerMP playerMP = this.func_184102_h().func_184103_al().func_177451_a(this.playerSheHasSexWith());
         this.field_70180_af.func_187227_b(PLAYER_SHE_HAS_SEX_WITH, playerMP.getPersistentID().toString());
         playerMP.func_70634_a(this.func_174791_d().field_72450_a, this.func_174791_d().field_72448_b, this.func_174791_d().field_72449_c);
         this.TurnPlayerIntoCamera(playerMP, false);
         playerMP.func_191958_b(0.0F, 0.0F, 0.0F, 0.0F);
         this.moveCamera(0.0D, 0.0D, 0.4D, 0.0F, 60.0F);
         this.playerCamPos = null;
         this.setCurrentAction(GirlEntity.Action.DOGGYSTART);
         PacketHandler.INSTANCE.sendTo(new SetPlayerMovement(false), playerMP);
      }

      if (this.lookingForBed) {
         if (!(this.func_174791_d().func_72438_d(this.targetPos()) < 0.6D) && this.bedSearchTick <= 200) {
            ++this.bedSearchTick;
            if (this.bedSearchTick == 60 || this.bedSearchTick == 120) {
               this.func_70661_as().func_75499_g();
               this.func_70661_as().func_75492_a(this.targetPos().field_72450_a, this.targetPos().field_72448_b, this.targetPos().field_72449_c, 0.35D);
            }
         } else {
            this.lookingForBed = false;
            this.field_70180_af.func_187227_b(SHOULD_BE_AT_TARGET, true);
            this.bedSearchTick = 0;
            this.field_70145_X = true;
            this.func_189654_d(true);
            this.field_70159_w = 0.0D;
            this.field_70181_x = 0.0D;
            this.field_70179_y = 0.0D;
            this.setCurrentAction(GirlEntity.Action.STARTDOGGY);
         }
      }

      if (this.isPreparingPayment) {
         ++this.preparingPaymentTick;
         if (!this.func_174791_d().equals(TARGET_POS) && this.preparingPaymentTick <= 40) {
            this.field_70177_z = this.targetYaw();

            try {
               TARGET_POS.equals((Object)null);
            } catch (NullPointerException var3) {
               this.setTargetPos(this.getInFrontOfPlayer());
            }

            this.func_189654_d(false);
            Vec3d nextPos = PenisMath.Lerp(this.func_174791_d(), this.targetPos(), 40 - this.preparingPaymentTick);
            this.func_70107_b(nextPos.field_72450_a, nextPos.field_72448_b, nextPos.field_72449_c);
         } else {
            this.isPreparingPayment = false;
            this.preparingPaymentTick = 0;
            this.setTargetYaw(this.field_70170_p.func_73046_m().func_184103_al().func_177451_a(this.playerSheHasSexWith()).field_70177_z + 180.0F);
            this.field_70180_af.func_187227_b(SHOULD_BE_AT_TARGET, true);
            this.func_70661_as().func_75499_g();
            if (!(Boolean)this.field_70180_af.func_187225_a(isHorny)) {
               this.setCurrentAction(GirlEntity.Action.PAYMENT);
            } else {
               this.checkFollowUp();
            }
         }
      }

   }

   public boolean func_184645_a(EntityPlayer player, EnumHand hand) {
      if (super.func_184645_a(player, hand)) {
         return true;
      } else {
         ItemStack itemstack = player.func_184586_b(hand);
         boolean hasNameTag = itemstack.func_77973_b() == Items.field_151057_cb;
         if (hasNameTag) {
            itemstack.func_111282_a(player, this, hand);
            return true;
         } else {
            if (this.field_70170_p.field_72995_K && !this.openMenu(player)) {
               this.sayAround(I18n.func_135052_a("jenny.dialogue.busy", new Object[0]));
            }

            return true;
         }
      }
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (!this.field_70170_p.field_72995_K) {
         this.field_70180_af.func_187227_b(isHorny, this.func_70644_a(HornyPotion.HORNY_EFFECT));
      }

   }

   public boolean openMenu(EntityPlayer player) {
      if (this.playerSheHasSexWith() != null || this.hasMaster() && !((String)this.field_70180_af.func_187225_a(MASTER)).equals(Minecraft.func_71410_x().field_71439_g.getPersistentID().toString())) {
         return false;
      } else {
         String[] actions = new String[]{"action.names.blowjob", "action.names.boobjob", "action.names.doggy", (Integer)this.field_70180_af.func_187225_a(CURRENT_MODEL) == 1 ? "action.names.strip" : "action.names.dressup"};
         renderMenu(player, this, actions, new ItemStack[]{new ItemStack(Items.field_151166_bC, 3), new ItemStack(Items.field_151079_bi, 2), new ItemStack(Items.field_151045_i, 2), (Integer)this.field_70180_af.func_187225_a(CURRENT_MODEL) == 1 ? new ItemStack(Items.field_151043_k, 1) : new ItemStack(Items.field_190931_a, 0)}, true);
         return true;
      }
   }

   public boolean canCloseUiWithoutHavingChosen() {
      return true;
   }

   public void doAction(String animationName, UUID player) {
      super.doAction(animationName, player);
      if ("action.names.blowjob".equals(animationName)) {
         this.changeDataParameterFromClient("animationFollowUp", "blowjob");
         this.prepareAction(true, player);
      } else if ("action.names.boobjob".equals(animationName)) {
         this.changeDataParameterFromClient("animationFollowUp", "boobjob");
         this.prepareAction(true, player);
      } else if ("action.names.doggy".equals(animationName)) {
         this.changeDataParameterFromClient("animationFollowUp", "doggy");
         this.prepareAction(true, player);
      } else if ("action.names.strip".equals(animationName)) {
         this.changeDataParameterFromClient("animationFollowUp", "strip");
         this.prepareAction(true, player);
      } else if ("action.names.dressup".equals(animationName)) {
         this.setCurrentAction(GirlEntity.Action.STRIP);
      }

   }

   protected void prepareAction(boolean shouldPreparePayment, UUID player) {
      super.prepareAction(shouldPreparePayment, true, player);
      HandlePlayerMovement.setActive(false);
   }

   public void goForDoggy() {
      BlockPos temp = this.findBed(this.func_180425_c());
      if (temp == null) {
         this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_HMPH[2]);
         this.sayAround(I18n.func_135052_a("jenny.dialogue.nobedinsight", new Object[0]));
      } else {
         this.field_70714_bg.func_85156_a(this.aiWander);
         this.field_70714_bg.func_85156_a(this.aiLookAtPlayer);
         Vec3d bedPos = new Vec3d((double)temp.func_177958_n(), (double)temp.func_177956_o(), (double)temp.func_177952_p());
         int[] yaws = new int[]{0, 180, -90, 90};
         Vec3d[][] potentialSpaces = new Vec3d[][]{{new Vec3d(0.5D, 0.0D, -0.5D), new Vec3d(0.0D, 0.0D, -1.0D)}, {new Vec3d(0.5D, 0.0D, 1.5D), new Vec3d(0.0D, 0.0D, 1.0D)}, {new Vec3d(-0.5D, 0.0D, 0.5D), new Vec3d(-1.0D, 0.0D, 0.0D)}, {new Vec3d(1.5D, 0.0D, 0.5D), new Vec3d(1.0D, 0.0D, 0.0D)}};
         int whichOne = -1;

         for(int i = 0; i < potentialSpaces.length; ++i) {
            Vec3d searchPos = bedPos.func_178787_e(potentialSpaces[i][1]);
            if (this.field_70170_p.func_180495_p(new BlockPos(searchPos.field_72450_a, searchPos.field_72448_b, searchPos.field_72449_c)).func_177230_c() == Blocks.field_150350_a) {
               if (whichOne == -1) {
                  whichOne = i;
               } else {
                  double oldDistance = this.func_180425_c().func_177954_c(bedPos.func_178787_e(potentialSpaces[whichOne][0]).field_72450_a, bedPos.func_178787_e(potentialSpaces[whichOne][0]).field_72448_b, bedPos.func_178787_e(potentialSpaces[whichOne][0]).field_72449_c);
                  double newDistance = this.func_180425_c().func_177954_c(bedPos.func_178787_e(potentialSpaces[i][0]).field_72450_a, bedPos.func_178787_e(potentialSpaces[i][0]).field_72448_b, bedPos.func_178787_e(potentialSpaces[i][0]).field_72449_c);
                  if (newDistance < oldDistance) {
                     whichOne = i;
                  }
               }
            }
         }

         if (whichOne == -1) {
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_HMPH[2]);
            this.sayAround(I18n.func_135052_a("jenny.dialogue.bedobscured", new Object[0]));
            return;
         }

         Vec3d targetPos = bedPos.func_178787_e(potentialSpaces[whichOne][0]);
         this.setTargetYaw((float)yaws[whichOne]);
         this.setTargetPos(new Vec3d(targetPos.field_72450_a, targetPos.field_72448_b, targetPos.field_72449_c));
         this.playerYaw = this.targetYaw();
         this.func_70661_as().func_75499_g();
         this.func_70661_as().func_75492_a(targetPos.field_72450_a, targetPos.field_72448_b, targetPos.field_72449_c, 0.35D);
         this.lookingForBed = true;
         this.bedSearchTick = 0;
      }

   }

   protected void cum() {
      if (this.currentAction() != GirlEntity.Action.SUCKBLOWJOB && this.currentAction() != GirlEntity.Action.THRUSTBLOWJOB) {
         if (this.currentAction() != GirlEntity.Action.DOGGYSLOW && this.currentAction() != GirlEntity.Action.DOGGYFAST) {
            if (this.currentAction() == GirlEntity.Action.PAIZURI_FAST || this.currentAction() == GirlEntity.Action.PAIZURI_SLOW) {
               this.playerIsCumming = true;
               this.actionController.transitionLengthTicks = 2.0D;
               this.setCurrentAction(GirlEntity.Action.PAIZURI_CUM);
            }
         } else {
            this.playerIsCumming = true;
            this.actionController.transitionLengthTicks = 2.0D;
            this.setCurrentAction(GirlEntity.Action.DOGGYCUM);
         }
      } else {
         this.playerIsCumming = true;
         this.actionController.transitionLengthTicks = 2.0D;
         this.setCurrentAction(GirlEntity.Action.CUMBLOWJOB);
         this.moveCamera(0.0D, 0.0D, 0.0D, 0.0F, 70.0F);
      }

   }

   protected void thrust() {
      if (this.currentAction() != GirlEntity.Action.SUCKBLOWJOB && this.currentAction() != GirlEntity.Action.THRUSTBLOWJOB) {
         if (this.currentAction() != GirlEntity.Action.DOGGYSLOW && this.currentAction() != GirlEntity.Action.DOGGYFAST) {
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
         } else {
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

   public void onReset() {
      this.aiWander = new EntityAIWanderAvoidWater(this, 0.35D);
      this.aiLookAtPlayer = new LookAtNearbyEntity(this, EntityPlayer.class, 3.0F, 1.0F);
      this.field_70714_bg.func_75776_a(5, this.aiLookAtPlayer);
      this.field_70714_bg.func_75776_a(5, this.aiWander);
   }

   protected void checkFollowUp() {
      String var1 = (String)this.field_70180_af.func_187225_a(ANIMATION_FOLLOW_UP);
      byte var2 = -1;
      switch(var1.hashCode()) {
      case -20842805:
         if (var1.equals("blowjob")) {
            var2 = 1;
         }
         break;
      case 64419037:
         if (var1.equals("boobjob")) {
            var2 = 2;
         }
         break;
      case 95761198:
         if (var1.equals("doggy")) {
            var2 = 3;
         }
         break;
      case 109773592:
         if (var1.equals("strip")) {
            var2 = 0;
         }
      }

      switch(var2) {
      case 0:
         this.resetPlayer();
         this.setCurrentAction(GirlEntity.Action.STRIP);
         break;
      case 1:
         this.setCurrentAction(GirlEntity.Action.STARTBLOWJOB);
         break;
      case 2:
         if ((Integer)this.field_70180_af.func_187225_a(GirlEntity.CURRENT_MODEL) != 0) {
            this.setCurrentAction(GirlEntity.Action.STRIP);
            return;
         }

         this.setCurrentAction(GirlEntity.Action.PAIZURI_START);
         break;
      case 3:
         if ((Integer)this.field_70180_af.func_187225_a(GirlEntity.CURRENT_MODEL) != 0) {
            this.setCurrentAction(GirlEntity.Action.STRIP);
            this.resetPlayer();
            return;
         }

         this.resetGirl();
         PacketHandler.INSTANCE.sendToServer(new SendGirlToBed(this.girlId()));
      }

      if (this.field_70170_p.field_72995_K) {
         this.changeDataParameterFromClient("animationFollowUp", "");
      } else {
         this.field_70180_af.func_187227_b(ANIMATION_FOLLOW_UP, "");
      }

   }

   protected <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
      if (this.field_70170_p instanceof FakeWorld) {
         return null;
      } else {
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
            } else if (this.func_184218_aH()) {
               this.createAnimation("animation.jenny.sit", true, event);
            } else if (Math.abs(this.field_70169_q - this.field_70165_t) + Math.abs(this.field_70166_s - this.field_70161_v) > 0.0D) {
               switch(this.getWalkSpeed()) {
               case RUN:
                  this.createAnimation("animation.jenny.run", true, event);
                  break;
               case FAST_WALK:
                  this.createAnimation("animation.jenny.fastwalk", true, event);
                  break;
               case WALK:
                  this.createAnimation("animation.jenny.walk", true, event);
               }

               this.field_70177_z = this.field_70759_as;
            } else {
               this.createAnimation("animation.jenny.idle", true, event);
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
   }

   @SideOnly(Side.CLIENT)
   public void registerControllers(AnimationData data) {
      super.registerControllers(data);
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
            this.say(I18n.func_135052_a("jenny.dialogue.hihi", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_JENNY_GIGGLE));
            break;
         case 5:
            this.say(I18n.func_135052_a("jenny.dialogue.huh", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_HUH[1]);
            break;
         case 6:
            this.playSoundAroundHer(SoundsHandler.MISC_PLOB[0], 0.5F);
            String playerName = "<" + Minecraft.func_71410_x().field_71439_g.func_70005_c_() + "> ";
            String var8 = (String)this.field_70180_af.func_187225_a(ANIMATION_FOLLOW_UP);
            byte var10 = -1;
            switch(var8.hashCode()) {
            case -20842805:
               if (var8.equals("blowjob")) {
                  var10 = 1;
               }
               break;
            case 64419037:
               if (var8.equals("boobjob")) {
                  var10 = 3;
               }
               break;
            case 95761198:
               if (var8.equals("doggy")) {
                  var10 = 2;
               }
               break;
            case 109773592:
               if (var8.equals("strip")) {
                  var10 = 0;
               }
            }

            switch(var10) {
            case 0:
               this.say(playerName + I18n.func_135052_a("jenny.dialogue.showBobsandveganapls", new Object[0]), true);
               return;
            case 1:
               this.say(playerName + I18n.func_135052_a("jenny.dialogue.giveblowjob", new Object[0]), true);
               return;
            case 2:
               this.say(playerName + I18n.func_135052_a("jenny.dialogue.givesex", new Object[0]), true);
               return;
            case 3:
               this.say(playerName + I18n.func_135052_a("jenny.dialogue.givebooba", new Object[0]), true);
               return;
            default:
               this.say(playerName + "sex pls", true);
               return;
            }
         case 7:
            this.say(I18n.func_135052_a("jenny.dialogue.hehe", new Object[0]));
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
            this.say(I18n.func_135052_a("jenny.dialogue.blowjobtext1", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_MMM[8]);
            this.playerYaw = this.field_70177_z + 180.0F;
            if (this.belongsToPlayer()) {
               SexUI.resetCumPercentage();
            }
            break;
         case 12:
            this.say(I18n.func_135052_a("jenny.dialogue.blowjobtext2", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_LIGHTBREATHING[8]);
            break;
         case 13:
            this.say(I18n.func_135052_a("jenny.dialogue.blowjobtext3", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_AFTERSESSIONMOAN[0]);
            break;
         case 14:
            this.playSoundAroundHer(SoundsHandler.MISC_BELLJINGLE[0]);
            break;
         case 15:
            this.say(I18n.func_135052_a("jenny.dialogue.blowjobtext4", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_HMPH[1], 0.5F);
            if (this.belongsToPlayer()) {
               SexUI.resetCumPercentage();
            }
            break;
         case 16:
            this.say(I18n.func_135052_a("jenny.dialogue.blowjobtext5", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_LIGHTBREATHING[8]);
            break;
         case 17:
            this.say(I18n.func_135052_a("jenny.dialogue.blowjobtext6", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_GIGGLE[4]);
            break;
         case 18:
            this.say("<" + Minecraft.func_71410_x().field_71439_g.func_70005_c_() + "> " + I18n.func_135052_a("jenny.dialogue.blowjobtext7", new Object[0]), true);
            this.playSoundAroundHer(SoundsHandler.MISC_PLOB[0], 0.5F);
            break;
         case 19:
            this.say(I18n.func_135052_a("jenny.dialogue.blowjobtext8", new Object[0]));
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
            this.sayAround(I18n.func_135052_a("jenny.dialogue.doggytext1", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.GIRLS_JENNY_LIGHTBREATHING[9]);
            break;
         case 42:
            this.sayAround(I18n.func_135052_a("jenny.dialogue.doggytext2", new Object[0]));
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
            UUID player = Minecraft.func_71410_x().field_71439_g.getPersistentID();
            if (player.equals(this.field_70170_p.func_72890_a(this.getGirl(), 2.0D).getPersistentID())) {
               this.playerYaw = this.field_70170_p.func_152378_a(player).field_70177_z;
               this.setPlayer(player);
               if (!this.paizuriCamSide) {
                  this.paizuriCamSide = true;
                  this.moveCamera(-0.7D, -0.6D, -0.2D, 60.0F, -3.0F);
               }
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
            if (this.belongsToPlayer() && !this.paizuriCamSide) {
               this.moveCamera(-0.7D, -0.6D, -0.2D, 60.0F, -3.0F);
            }
         }

      };
      this.actionController.registerSoundListener(actionSoundListener);
      data.addAnimationController(this.actionController);
   }

   static {
      isHorny = EntityDataManager.func_187226_a(GirlEntity.class, DataSerializers.field_187198_h).func_187156_b().func_187161_a(74);
   }
}

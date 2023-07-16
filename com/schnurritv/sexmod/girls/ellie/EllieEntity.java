package com.schnurritv.sexmod.girls.ellie;

import com.daripher.sexmod.client.util.FakeWorld;
import com.schnurritv.sexmod.Packets.ChangeDataParameter;
import com.schnurritv.sexmod.Packets.ClearAnimationCache;
import com.schnurritv.sexmod.Packets.SendCompanionHome;
import com.schnurritv.sexmod.Packets.SendEllieToPlayer;
import com.schnurritv.sexmod.Packets.SendGirlToBed;
import com.schnurritv.sexmod.Packets.SetPlayerMovement;
import com.schnurritv.sexmod.Packets.TeleportPlayer;
import com.schnurritv.sexmod.companion.fighter.LookAtNearbyEntity;
import com.schnurritv.sexmod.events.HandlePlayerMovement;
import com.schnurritv.sexmod.gender_change.hornypotion.HornyPotion;
import com.schnurritv.sexmod.girls.base.Fighter;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirl;
import com.schnurritv.sexmod.gui.Sex.BlackScreenUI;
import com.schnurritv.sexmod.gui.Sex.SexUI;
import com.schnurritv.sexmod.util.Reference;
import com.schnurritv.sexmod.util.Handlers.LootTableHandler;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import com.schnurritv.sexmod.util.Handlers.SoundsHandler;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public class EllieEntity extends Fighter {
   public boolean delayNewRot = false;
   public float delayedRot = 0.0F;
   public boolean awaitPlayer = false;
   public boolean lookingForBed = false;
   public boolean isPreparingPayment = false;
   public boolean isBusy = false;
   public float hornyLevel = 3200.0F;
   static final float HORNY_SEX_LEVEL = 4800.0F;
   static final float HORNY_RANGE = 10.0F;
   public int delayedRotTick = 0;
   public int bedSearchTick = -1;
   public int sexDelayTick = -1;
   boolean moanBreak = false;

   public EllieEntity(World worldIn) {
      super(worldIn);
      this.func_70105_a(0.49F, 1.95F);
      this.slashSwordRot = -85;
      this.stabSwordRot = -175;
      this.holdBowRot = -85;
      this.swordOffsetStab = new Vec3d(-0.1D, 0.05D, 0.0D);
   }

   protected String getGirlName() {
      return "Ellie";
   }

   protected void func_184651_r() {
      super.func_184651_r();
   }

   public void func_70619_bc() {
      super.func_70619_bc();
      if (this.currentAction() == GirlEntity.Action.NULL) {
         this.field_70145_X = false;
         this.func_189654_d(false);
      }

      if (this.delayNewRot && ++this.delayedRotTick > 1) {
         this.delayedRotTick = 0;
         this.delayNewRot = false;
         this.setTargetYaw(this.delayedRot);
      }

      this.manageApproaching();
      this.manageGoingToBed();
      this.manageSexDelay();
   }

   void manageSexDelay() {
      EntityPlayer closestPlayer = this.field_70170_p.func_72890_a(this, 10.0D);
      if (closestPlayer != null) {
         if (this.sexDelayTick >= 0 && ++this.sexDelayTick == 3) {
            this.sexDelayTick = -1;
            this.field_70180_af.func_187227_b(CURRENT_MODEL, 0);
            this.checkFollowUp();
            this.setPlayer(closestPlayer.getPersistentID());
            closestPlayer.field_71075_bZ.field_75100_b = true;
            closestPlayer.field_70145_X = true;
            closestPlayer.func_191958_b(0.0F, 0.0F, 0.0F, 0.0F);
         }

         if (this.awaitPlayer && closestPlayer.func_174791_d().func_72438_d(this.func_174791_d()) < 1.0D) {
            this.awaitPlayer = false;
            this.sexDelayTick = 0;
         }

      }
   }

   void manageApproaching() {
      EntityPlayer closestPlayer = this.field_70170_p.func_72890_a(this, 10.0D);
      if (!this.isBusy) {
         if (closestPlayer != null) {
            if (closestPlayer.field_70122_E) {
               if (!isHavingSex(closestPlayer)) {
                  if (!PlayerGirl.isPlayerGirl(closestPlayer)) {
                     if (!(++this.hornyLevel < 4800.0F)) {
                        this.isBusy = true;
                        this.approachPlayer(closestPlayer);
                     }
                  }
               }
            }
         }
      }
   }

   void manageGoingToBed() {
      if (this.lookingForBed) {
         ++this.bedSearchTick;
         if (this.func_174791_d().func_72438_d(this.targetPos()) < 1.0D && this.bedSearchTick < 140 || this.bedSearchTick == 140) {
            this.field_70180_af.func_187227_b(SHOULD_BE_AT_TARGET, true);
            this.field_70145_X = true;
            this.func_189654_d(true);
            this.field_70159_w = 0.0D;
            this.field_70181_x = 0.0D;
            this.field_70179_y = 0.0D;
            this.bedSearchTick = 141;
         }

         if (this.bedSearchTick != 5 && this.bedSearchTick != 60 && this.bedSearchTick != 120) {
            if (this.bedSearchTick == 160) {
               this.setCurrentAction(GirlEntity.Action.SITDOWN);
            } else if (this.bedSearchTick == 310) {
               this.awaitPlayer = true;
               this.lookingForBed = false;
               this.bedSearchTick = 0;
            }
         } else {
            this.func_70661_as().func_75499_g();
            this.func_70661_as().func_75492_a(this.targetPos().field_72450_a, this.targetPos().field_72448_b, this.targetPos().field_72449_c, 0.35D);
         }

      }
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      EntityPlayer closestPlayer = this.field_70170_p.func_72890_a(this, 10.0D);
      if (this.field_70170_p.field_72995_K) {
         this.setUpBlackScreen(closestPlayer);
      }

      if (!this.field_70170_p.field_72995_K && this.func_70644_a(HornyPotion.HORNY_EFFECT) && this.hornyLevel < 4800.0F && !this.lookingForBed && !this.awaitPlayer && this.playerSheHasSexWith() == null) {
         this.func_184589_d(HornyPotion.HORNY_EFFECT);
         this.hornyLevel = 6.9420184E7F;
         this.field_70145_X = true;
         this.func_189654_d(true);
         this.field_70181_x = 0.0D;
      }

   }

   @SideOnly(Side.CLIENT)
   void setUpBlackScreen(EntityPlayer closestPlayer) {
      if (this.currentAction() == GirlEntity.Action.SITDOWNIDLE && closestPlayer != null && closestPlayer.func_174791_d().func_72438_d(this.func_174791_d()) < 1.0D && closestPlayer.getPersistentID().equals(Minecraft.func_71410_x().field_71439_g.getPersistentID())) {
         BlackScreenUI.activate();
         HandlePlayerMovement.setActive(false);
      }

   }

   protected void cum() {
      if (this.currentAction() != GirlEntity.Action.COWGIRLFAST && this.currentAction() != GirlEntity.Action.COWGIRLSLOW) {
         if (this.currentAction() == GirlEntity.Action.MISSIONARY_FAST || this.currentAction() == GirlEntity.Action.MISSIONARY_SLOW) {
            this.playerIsCumming = true;
            this.actionController.transitionLengthTicks = 2.0D;
            this.setCurrentAction(GirlEntity.Action.MISSIONARY_CUM);
         }
      } else {
         this.playerIsCumming = true;
         this.actionController.transitionLengthTicks = 2.0D;
         this.setCurrentAction(GirlEntity.Action.COWGIRLCUM);
      }

   }

   protected void thrust() {
      if (this.currentAction() != GirlEntity.Action.COWGIRLSLOW && this.currentAction() != GirlEntity.Action.COWGIRLFAST) {
         if (this.currentAction() == GirlEntity.Action.MISSIONARY_FAST || this.currentAction() == GirlEntity.Action.MISSIONARY_SLOW) {
            this.playerIsThrusting = true;
            if (this.currentAction() == GirlEntity.Action.MISSIONARY_FAST) {
               PacketHandler.INSTANCE.sendToServer(new ClearAnimationCache(this.girlId()));
            } else {
               this.setCurrentAction(GirlEntity.Action.MISSIONARY_FAST);
            }
         }
      } else {
         this.playerIsThrusting = true;
         if (this.currentAction() == GirlEntity.Action.COWGIRLFAST) {
            PacketHandler.INSTANCE.sendToServer(new ClearAnimationCache(this.girlId()));
         } else {
            this.setCurrentAction(GirlEntity.Action.COWGIRLFAST);
         }
      }

   }

   protected SoundEvent func_184601_bQ(DamageSource edamageSourceIn) {
      return null;
   }

   protected SoundEvent func_184615_bR() {
      return null;
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
   }

   protected void prepareAction(EntityPlayerMP player) {
      this.field_70714_bg.func_85156_a(this.aiLookAtPlayer);
      this.field_70714_bg.func_85156_a(this.aiWander);
      this.func_70661_as().func_75499_g();
      this.field_70159_w = 0.0D;
      this.field_70181_x = 0.0D;
      this.field_70179_y = 0.0D;
   }

   public void onReset() {
      this.aiWander = new EntityAIWanderAvoidWater(this, 0.35D);
      this.aiLookAtPlayer = new LookAtNearbyEntity(this, EntityPlayer.class, 3.0F, 1.0F);
      this.field_70714_bg.func_75776_a(5, this.aiLookAtPlayer);
      this.field_70714_bg.func_75776_a(5, this.aiWander);
      this.hornyLevel = 0.0F;
      this.isBusy = false;
      this.delayNewRot = false;
      this.delayedRot = 0.0F;
      this.awaitPlayer = false;
      this.lookingForBed = false;
      this.isPreparingPayment = false;
      this.delayedRotTick = 0;
      this.bedSearchTick = -1;
      this.sexDelayTick = -1;
      this.shouldBeAtTargetYaw = false;
   }

   public boolean openMenu(EntityPlayer player) {
      renderMenu(player, this, new String[]{"action.names.cowgirl", "action.names.missionary"}, true);
      return true;
   }

   public void openCompanionMenu(EntityPlayer player) {
      String[] actions = new String[]{"action.names.strip", "action.names.dressup"};
      renderMenu(player, this, actions, true);
   }

   protected boolean func_184645_a(EntityPlayer player, EnumHand hand) {
      if (super.func_184645_a(player, hand)) {
         return true;
      } else {
         ItemStack itemstack = player.func_184586_b(hand);
         boolean hasNameTag = itemstack.func_77973_b() == Items.field_151057_cb;
         if (hasNameTag) {
            itemstack.func_111282_a(player, this, hand);
            return true;
         } else {
            if (this.field_70170_p.field_72995_K && !this.openCompanionMenuOnClient(player)) {
               this.sayAround(I18n.func_135052_a("ellie.dialogue.busy", new Object[0]));
            }

            return true;
         }
      }
   }

   @SideOnly(Side.CLIENT)
   boolean openCompanionMenuOnClient(EntityPlayer player) {
      if (this.playerSheHasSexWith() != null || this.hasMaster() && !((String)this.field_70180_af.func_187225_a(MASTER)).equals(Minecraft.func_71410_x().field_71439_g.getPersistentID().toString())) {
         return false;
      } else {
         this.openCompanionMenu(player);
         return true;
      }
   }

   public boolean canCloseUiWithoutHavingChosen() {
      return false;
   }

   public void doAction(String animationName, UUID player) {
      super.doAction(animationName, player);
      if ("action.names.missionary".equals(animationName)) {
         this.setPlayer(player);
         this.setCurrentAction(GirlEntity.Action.HUGSELECTED);
         this.changeDataParameterFromClient("animationFollowUp", "Missionary");
      } else if ("action.names.cowgirl".equals(animationName)) {
         this.setPlayer(player);
         this.setCurrentAction(GirlEntity.Action.HUGSELECTED);
         this.changeDataParameterFromClient("animationFollowUp", "cowgirl");
      } else if ("action.names.strip".equals(animationName)) {
         this.setCurrentAction(GirlEntity.Action.STRIP);
         this.resetPlayer();
      } else if ("action.names.dressup".equals(animationName)) {
         this.setCurrentAction(GirlEntity.Action.STRIP);
      }

   }

   public void goForCowgirl() {
      int whichOne = -1;
      int bedsFound = 0;
      Vec3d[][] potentialSpaces = new Vec3d[][]{{new Vec3d(0.5D, 0.0D, -0.18D), new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(0.0D, 0.0D, 1.0D)}, {new Vec3d(0.5D, 0.0D, 1.18D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(0.0D, 0.0D, -1.0D)}, {new Vec3d(-0.18D, 0.0D, 0.5D), new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(1.0D, 0.0D, 0.0D)}, {new Vec3d(1.18D, 0.0D, 0.5D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(-1.0D, 0.0D, 0.0D)}};
      int[] yaws = new int[]{0, 180, -90, 90};

      Vec3d bedPos;
      do {
         BlockPos var10001 = this.func_180425_c();
         ++bedsFound;
         BlockPos temp = this.findBed(var10001, bedsFound);
         if (temp == null) {
            this.say(I18n.func_135052_a("jenny.dialogue.nobedinsight", new Object[0]));
            return;
         }

         bedPos = new Vec3d((double)temp.func_177958_n(), (double)temp.func_177956_o(), (double)temp.func_177952_p());

         for(int i = 0; i < potentialSpaces.length; ++i) {
            Vec3d spaceFront = bedPos.func_178787_e(potentialSpaces[i][1]);
            Block blockFront = this.field_70170_p.func_180495_p(new BlockPos(spaceFront.field_72450_a, spaceFront.field_72448_b, spaceFront.field_72449_c)).func_177230_c();
            Vec3d spaceBack = bedPos.func_178787_e(potentialSpaces[i][2]);
            Block blockBack = this.field_70170_p.func_180495_p(new BlockPos(spaceBack.field_72450_a, spaceBack.field_72448_b, spaceBack.field_72449_c)).func_177230_c();
            if (blockFront == Blocks.field_150350_a && blockBack == Blocks.field_150324_C) {
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
      } while(whichOne == -1);

      this.field_70714_bg.func_85156_a(this.aiWander);
      this.field_70714_bg.func_85156_a(this.aiLookAtPlayer);
      Vec3d targetPos = bedPos.func_178787_e(potentialSpaces[whichOne][0]);
      this.setTargetYaw((float)yaws[whichOne]);
      this.setTargetPos(new Vec3d(targetPos.field_72450_a, targetPos.field_72448_b, targetPos.field_72449_c));
      this.playerYaw = this.targetYaw();
      this.func_70661_as().func_75499_g();
      this.func_70661_as().func_75492_a(targetPos.field_72450_a, targetPos.field_72448_b, targetPos.field_72449_c, 0.35D);
      this.lookingForBed = true;
      this.bedSearchTick = 0;
   }

   boolean shouldCrouch() {
      return this.field_70170_p.func_180495_p(this.func_180425_c().func_177982_a(0, 2, 0)).func_177230_c() != Blocks.field_150350_a;
   }

   void approachPlayer(EntityPlayer player) {
      this.field_70180_af.func_187227_b(PLAYER_SHE_HAS_SEX_WITH, player.getPersistentID().toString());
      this.prepareAction((EntityPlayerMP)player);
      this.shouldBeAtTargetYaw = true;
      Vec3d distance = player.func_174791_d().func_178788_d(this.func_174791_d());
      this.setTargetYaw((float)Math.atan2(distance.field_72449_c, distance.field_72450_a) * 57.29578F + 90.0F);
      PacketHandler.INSTANCE.sendTo(new SetPlayerMovement(false), (EntityPlayerMP)player);
      this.func_189654_d(true);
      this.field_70145_X = true;
      this.field_70159_w = 0.0D;
      this.field_70181_x = 0.0D;
      this.field_70179_y = 0.0D;
      this.setCurrentAction(GirlEntity.Action.DASH);
   }

   protected ResourceLocation func_184647_J() {
      return LootTableHandler.ELLIE;
   }

   public float func_70047_e() {
      return this.shouldCrouch() ? 1.53F : 1.9F;
   }

   protected void checkFollowUp() {
      String var1 = (String)this.field_70180_af.func_187225_a(ANIMATION_FOLLOW_UP);
      byte var2 = -1;
      switch(var1.hashCode()) {
      case 109773592:
         if (var1.equals("strip")) {
            var2 = 0;
         }
         break;
      case 959458983:
         if (var1.equals("cowgirl")) {
            var2 = 2;
         }
         break;
      case 1899551100:
         if (var1.equals("Missionary")) {
            var2 = 1;
         }
      }

      EntityPlayer closestPlayer;
      Vec3d playerPos;
      switch(var2) {
      case 0:
         this.resetPlayer();
         this.setCurrentAction(GirlEntity.Action.STRIP);
         break;
      case 1:
         this.setCurrentAction(GirlEntity.Action.MISSIONARY_START);
         closestPlayer = this.field_70170_p.func_72890_a(this, 10.0D);
         playerPos = this.func_174791_d().func_178786_a(0.0D, 0.1D, 0.0D);
         closestPlayer.func_70080_a(playerPos.field_72450_a, playerPos.field_72448_b, playerPos.field_72449_c, this.targetYaw(), 60.0F);
         closestPlayer.func_70634_a(playerPos.field_72450_a, playerPos.field_72448_b, playerPos.field_72449_c);
         break;
      case 2:
         this.setCurrentAction(GirlEntity.Action.COWGIRLSTART);
         closestPlayer = this.field_70170_p.func_72890_a(this, 10.0D);
         playerPos = this.func_174791_d().func_178787_e(new Vec3d(-Math.sin((double)this.targetYaw() * 0.017453292519943295D) * 1.8D, -0.65D, Math.cos((double)this.targetYaw() * 0.017453292519943295D) * 1.8D));
         closestPlayer.func_70080_a(playerPos.field_72450_a, playerPos.field_72448_b, playerPos.field_72449_c, 180.0F + this.targetYaw(), -30.0F);
         closestPlayer.func_70634_a(playerPos.field_72450_a, playerPos.field_72448_b, playerPos.field_72449_c);
      }

      this.field_70180_af.func_187227_b(ANIMATION_FOLLOW_UP, "");
   }

   public Vec3d getBehindOfPlayer(EntityPlayer player) {
      float playerYaw = player.field_70177_z;
      float distance = 0.5F;
      return player.func_174791_d().func_72441_c(-Math.sin((double)playerYaw * 0.017453292519943295D) * (double)(-distance), 0.0D, Math.cos((double)playerYaw * 0.017453292519943295D) * (double)(-distance));
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
               this.createAnimation("animation.ellie.eyes", true, event);
            } else {
               this.createAnimation("animation.ellie.null", true, event);
            }
            break;
         case 1:
            if (this.currentAction() != GirlEntity.Action.NULL) {
               this.createAnimation("animation.ellie.null", true, event);
            } else {
               double speed = Math.abs(this.field_70169_q - this.field_70165_t) + Math.abs(this.field_70166_s - this.field_70161_v);
               if (speed == 0.0D) {
                  this.createAnimation(this.shouldCrouch() ? "animation.ellie.crouchidle" : "animation.ellie.idle", true, event);
               } else if (this.shouldCrouch()) {
                  this.createAnimation("animation.ellie.crouchwalk", true, event);
               } else {
                  switch(this.getWalkSpeed()) {
                  case RUN:
                     this.createAnimation("animation.ellie.run", true, event);
                     return PlayState.CONTINUE;
                  case FAST_WALK:
                     this.createAnimation("animation.ellie.fastwalk", true, event);
                     return PlayState.CONTINUE;
                  case WALK:
                     this.createAnimation("animation.ellie.walk", true, event);
                  }
               }
            }
            break;
         case 2:
            switch(this.currentAction()) {
            case NULL:
               this.createAnimation("animation.ellie.null", true, event);
               break;
            case STRIP:
               this.createAnimation("animation.ellie.strip", false, event);
               break;
            case DASH:
               this.createAnimation("animation.ellie.dash", false, event);
               break;
            case HUG:
               this.createAnimation("animation.ellie.hug", false, event);
               break;
            case HUGIDLE:
               this.createAnimation("animation.ellie.hugidle", true, event);
               break;
            case HUGSELECTED:
               this.createAnimation("animation.ellie.hugselected", false, event);
               break;
            case SITDOWN:
               this.createAnimation("animation.ellie.sitdown", false, event);
               break;
            case SITDOWNIDLE:
               this.createAnimation("animation.ellie.sitdownidle", true, event);
               break;
            case COWGIRLSTART:
               this.createAnimation("animation.ellie.cowgirlstart", false, event);
               break;
            case COWGIRLSLOW:
               this.createAnimation("animation.ellie.cowgirlslow2", true, event);
               break;
            case COWGIRLFAST:
               this.createAnimation("animation.ellie.cowgirlfast", true, event);
               break;
            case COWGIRLCUM:
               this.createAnimation("animation.ellie.cowgirlcum", true, event);
               break;
            case ATTACK:
               this.createAnimation("animation.ellie.attack" + this.nextAttack, false, event);
               break;
            case BOW:
               this.createAnimation("animation.ellie.bowcharge", false, event);
               break;
            case RIDE:
               this.createAnimation("animation.ellie.ride", true, event);
               break;
            case SIT:
               this.createAnimation("animation.ellie.sit", true, event);
               break;
            case THROW_PEARL:
               this.createAnimation("animation.ellie.throwpearl", false, event);
               break;
            case DOWNED:
               this.createAnimation("animation.ellie.downed", true, event);
               break;
            case MISSIONARY_START:
               this.createAnimation("animation.ellie.missionary_start", false, event);
               break;
            case MISSIONARY_SLOW:
               this.createAnimation("animation.ellie.missionary_slow", true, event);
               break;
            case MISSIONARY_FAST:
               this.createAnimation("animation.ellie.missionary_fast", false, event);
               break;
            case MISSIONARY_CUM:
               this.createAnimation("animation.ellie.missionary_cum", false, event);
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
         case -1961942550:
            if (var2.equals("attackDone")) {
               var3 = 34;
            }
            break;
         case -1919624234:
            if (var2.equals("cowgirlcumDone")) {
               var3 = 32;
            }
            break;
         case -1919384284:
            if (var2.equals("cowgirlcumMSG1")) {
               var3 = 24;
            }
            break;
         case -1919384283:
            if (var2.equals("cowgirlcumMSG2")) {
               var3 = 25;
            }
            break;
         case -1919384282:
            if (var2.equals("cowgirlcumMSG3")) {
               var3 = 26;
            }
            break;
         case -1919384281:
            if (var2.equals("cowgirlcumMSG4")) {
               var3 = 27;
            }
            break;
         case -1919384280:
            if (var2.equals("cowgirlcumMSG5")) {
               var3 = 28;
            }
            break;
         case -1919384279:
            if (var2.equals("cowgirlcumMSG6")) {
               var3 = 30;
            }
            break;
         case -1729717726:
            if (var2.equals("sitdownDone")) {
               var3 = 15;
            }
            break;
         case -1729477776:
            if (var2.equals("sitdownMSG1")) {
               var3 = 14;
            }
            break;
         case -1551050723:
            if (var2.equals("cowgirlStartDone")) {
               var3 = 19;
            }
            break;
         case -1550810774:
            if (var2.equals("cowgirlStartMSG0")) {
               var3 = 16;
            }
            break;
         case -1550810773:
            if (var2.equals("cowgirlStartMSG1")) {
               var3 = 17;
            }
            break;
         case -1550810772:
            if (var2.equals("cowgirlStartMSG2")) {
               var3 = 18;
            }
            break;
         case -1464580128:
            if (var2.equals("cowgirlfastReady")) {
               var3 = 22;
            }
            break;
         case -1062935247:
            if (var2.equals("dashReady")) {
               var3 = 3;
            }
            break;
         case -676816985:
            if (var2.equals("attackSound")) {
               var3 = 33;
            }
            break;
         case -559423612:
            if (var2.equals("missionary_fastReady")) {
               var3 = 21;
            }
            break;
         case -558244113:
            if (var2.equals("becomeNude")) {
               var3 = 0;
            }
            break;
         case -300807046:
            if (var2.equals("missionary_cumDone")) {
               var3 = 31;
            }
            break;
         case -300567096:
            if (var2.equals("missionary_cumMSG1")) {
               var3 = 42;
            }
            break;
         case -300567095:
            if (var2.equals("missionary_cumMSG2")) {
               var3 = 29;
            }
            break;
         case -188461382:
            if (var2.equals("stripDone")) {
               var3 = 1;
            }
            break;
         case -157000319:
            if (var2.equals("missionary_fastDone")) {
               var3 = 39;
            }
            break;
         case -156760369:
            if (var2.equals("missionary_fastMSG1")) {
               var3 = 38;
            }
            break;
         case -91455426:
            if (var2.equals("bedRustle")) {
               var3 = 40;
            }
            break;
         case 106540102:
            if (var2.equals("pearl")) {
               var3 = 35;
            }
            break;
         case 379621455:
            if (var2.equals("cowgirlfastdomMSG1")) {
               var3 = 23;
            }
            break;
         case 1199514355:
            if (var2.equals("cowgirlfastMSG1")) {
               var3 = 20;
            }
            break;
         case 1235071671:
            if (var2.equals("hugselectedDone")) {
               var3 = 13;
            }
            break;
         case 1235311621:
            if (var2.equals("hugselectedMSG1")) {
               var3 = 11;
            }
            break;
         case 1235311622:
            if (var2.equals("hugselectedMSG2")) {
               var3 = 12;
            }
            break;
         case 1257971612:
            if (var2.equals("hugDone")) {
               var3 = 10;
            }
            break;
         case 1258211562:
            if (var2.equals("hugMSG1")) {
               var3 = 5;
            }
            break;
         case 1258211563:
            if (var2.equals("hugMSG2")) {
               var3 = 6;
            }
            break;
         case 1258211564:
            if (var2.equals("hugMSG3")) {
               var3 = 7;
            }
            break;
         case 1258211565:
            if (var2.equals("hugMSG4")) {
               var3 = 8;
            }
            break;
         case 1258211566:
            if (var2.equals("hugMSG5")) {
               var3 = 9;
            }
            break;
         case 1459849139:
            if (var2.equals("bedRustle1")) {
               var3 = 41;
            }
            break;
         case 1534823792:
            if (var2.equals("openSexUi")) {
               var3 = 36;
            }
            break;
         case 1766420020:
            if (var2.equals("dashDone")) {
               var3 = 4;
            }
            break;
         case 1766659970:
            if (var2.equals("dashMSG1")) {
               var3 = 2;
            }
            break;
         case 2085797364:
            if (var2.equals("missionary_slowMSG1")) {
               var3 = 37;
            }
         }

         EntityPlayerSP playerx;
         Vec3d newPos;
         EntityPlayer player;
         switch(var3) {
         case 0:
            this.changeDataParameterFromClient("currentModel", (Integer)this.field_70180_af.func_187225_a(CURRENT_MODEL) == 1 ? "0" : "1");
            break;
         case 1:
            this.setCurrentAction((GirlEntity.Action)null);
            this.resetGirl();
            this.checkFollowUp();
            break;
         case 2:
            player = this.field_70170_p.func_72890_a(this, 15.0D);
            if (player != null) {
               newPos = this.func_174791_d().func_178788_d(player.func_174791_d());
               float chosenDegreesx = (float)Math.atan2(newPos.field_72449_c, newPos.field_72450_a) * 57.29578F;
               this.field_70177_z = chosenDegreesx;
               this.field_70759_as = chosenDegreesx;
               this.field_70761_aq = chosenDegreesx;
            }
            break;
         case 3:
            if (this.isClosestPlayer()) {
               PacketHandler.INSTANCE.sendToServer(new SendEllieToPlayer(this.girlId()));
            }
            break;
         case 4:
            this.setCurrentAction(GirlEntity.Action.HUG);
            player = this.field_70170_p.func_72890_a(this, 15.0D);
            if (player != null) {
               float chosenDegrees = player.field_70177_z;
               this.field_70177_z = chosenDegrees;
               this.field_70759_as = chosenDegrees;
               this.field_70761_aq = chosenDegrees;
            }
            break;
         case 5:
            playerx = Minecraft.func_71410_x().field_71439_g;
            if (playerx.getPersistentID().equals(this.playerSheHasSexWith()) || playerx.func_110124_au().equals(this.playerSheHasSexWith())) {
               PacketHandler.INSTANCE.sendToServer(new TeleportPlayer(playerx.func_110124_au().toString(), playerx.func_174791_d(), playerx.field_70177_z - 80.0F, playerx.field_70125_A));
            }
            break;
         case 6:
            this.say("Hmm...");
            this.playSoundAroundHer(SoundsHandler.GIRLS_ELLIE_HMPH[3], 3.0F);
            break;
         case 7:
            this.say("Hey!");
            this.playSoundAroundHer(SoundsHandler.GIRLS_ELLIE_AHH[2], 3.0F);
            break;
         case 8:
            this.say(I18n.func_135052_a("ellie.dialogue.mommyhorny", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.GIRLS_ELLIE_GIGGLE[0], 3.0F);
            break;
         case 9:
            this.say(I18n.func_135052_a("ellie.dialogue.whattodo", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.GIRLS_ELLIE_HUH[1], 3.0F);
            break;
         case 10:
            playerx = Minecraft.func_71410_x().field_71439_g;
            if (playerx.getPersistentID().equals(this.playerSheHasSexWith())) {
               this.setCurrentAction(GirlEntity.Action.HUGIDLE);
               this.openMenu(playerx);
            }
            break;
         case 11:
            this.say(I18n.func_135052_a("ellie.dialogue.iknow", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.GIRLS_ELLIE_MMM[0], 3.0F);
            break;
         case 12:
            this.say(I18n.func_135052_a("ellie.dialogue.followmedarling", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.GIRLS_ELLIE_GIGGLE[3], 3.0F);
            break;
         case 13:
            if (this.isClosestPlayer()) {
               this.setCurrentAction(GirlEntity.Action.NULL);
               newPos = this.func_174791_d();
               newPos = newPos.func_72441_c(-Math.sin((double)(this.field_70177_z + 90.0F) * 0.017453292519943295D) * -0.7803124785423279D, 0.0D, Math.cos((double)(this.field_70177_z + 90.0F) * 0.017453292519943295D) * -0.7803124785423279D);
               newPos = newPos.func_72441_c(-Math.sin((double)this.field_70177_z * 0.017453292519943295D) * 0.5296875238418579D, 0.0D, Math.cos((double)this.field_70177_z * 0.017453292519943295D) * 0.5296875238418579D);
               String newPosString = newPos.field_72450_a + "f" + newPos.field_72448_b + "f" + newPos.field_72449_c + "f";
               PacketHandler.INSTANCE.sendToServer(new ChangeDataParameter(this.girlId(), "targetPos", newPosString));
               this.resetGirl();
               PacketHandler.INSTANCE.sendToServer(new SendGirlToBed(this.girlId()));
            }
            break;
         case 14:
            this.playSoundAroundHer(SoundsHandler.GIRLS_ELLIE_GIGGLE[3], 3.0F);
            if (this.isClosestPlayer()) {
               this.say(I18n.func_135052_a("ellie.dialogue.cometomommy", new Object[0]));
            }
            break;
         case 15:
            if (this.isClosestPlayer()) {
               this.setCurrentAction(GirlEntity.Action.SITDOWNIDLE);
            }
            break;
         case 16:
            this.playSoundAroundHer(SoundsHandler.GIRLS_ELLIE_GIGGLE[4], 3.0F);
            break;
         case 17:
            if (this.isClosestPlayer()) {
               this.sayAround(I18n.func_135052_a("ellie.dialogue.like", new Object[0]));
               SexUI.resetCumPercentage();
            }
            break;
         case 18:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_ELLIE_AHH), 3.0F);
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING), 0.75F);
            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.02D);
            }
            break;
         case 19:
            if (this.belongsToPlayer()) {
               this.setCurrentAction(GirlEntity.Action.COWGIRLSLOW);
               SexUI.shouldBeRendered = true;
            }
            break;
         case 20:
            if (this.moanBreak) {
               this.moanBreak = false;
            } else {
               this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_ELLIE_AHH), 3.0F);
            }

            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING), 0.75F);
            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.04D);
            }
            break;
         case 21:
            this.playerIsThrusting = false;
            break;
         case 22:
            this.playerIsThrusting = false;
            if (this.belongsToPlayer()) {
               if (!HandlePlayerMovement.isThrusting) {
                  this.setCurrentAction(GirlEntity.Action.COWGIRLSLOW);
               } else if (Reference.RANDOM.nextInt(4) != 1) {
                  this.actionController.clearAnimationCache();
               }
            }
            break;
         case 23:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING), 0.75F);
            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.2D);
            }
            break;
         case 24:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_ELLIE_AHH), 3.0F);
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING), 0.75F);
            break;
         case 25:
            this.playSoundAroundHer(SoundsHandler.GIRLS_ELLIE_MOAN[5], 3.0F);
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING), 0.75F);
            break;
         case 26:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING), 0.75F);
            break;
         case 27:
            if (this.belongsToPlayer()) {
               SexUI.shouldBeRendered = false;
            }
            break;
         case 28:
         case 29:
            this.playSoundAroundHer(SoundsHandler.GIRLS_ELLIE_GIGGLE[4], 3.0F);
            if (this.belongsToPlayer()) {
               this.sayAround(I18n.func_135052_a("ellie.dialogue.goodboy", new Object[0]));
            }
            break;
         case 30:
            if (this.belongsToPlayer()) {
               BlackScreenUI.activate();
            }
            break;
         case 31:
         case 32:
            if (this.belongsToPlayer()) {
               SexUI.resetCumPercentage();
               this.resetGirl();
            }
            break;
         case 33:
            this.playSoundAroundHer(SoundEvents.field_187727_dV);
            break;
         case 34:
            this.setCurrentAction(GirlEntity.Action.NULL);
            if (++this.nextAttack == 3) {
               this.nextAttack = 0;
            }
            break;
         case 35:
            PacketHandler.INSTANCE.sendToServer(new SendCompanionHome(this.girlId()));
            break;
         case 36:
            if (this.isClosestPlayer()) {
               SexUI.shouldBeRendered = true;
            }
            break;
         case 37:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING));
            if (this.func_70681_au().nextBoolean() && this.func_70681_au().nextBoolean()) {
               this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_ELLIE_MOAN), 3.0F);
            } else {
               this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_ELLIE_AHH), 3.0F);
            }

            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.02D);
            }
            break;
         case 38:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING));
            if (!this.func_70681_au().nextBoolean() && !this.func_70681_au().nextBoolean()) {
               this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_ELLIE_AHH), 3.0F);
            } else {
               this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_ELLIE_MOAN), 3.0F);
            }

            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.05D);
            }
            break;
         case 39:
            if (!this.playerIsThrusting) {
               this.setCurrentAction(GirlEntity.Action.MISSIONARY_SLOW);
            } else {
               this.setCurrentAction(GirlEntity.Action.MISSIONARY_FAST);
            }
            break;
         case 40:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING));
            this.playSoundAroundHer(SoundsHandler.MISC_BEDRUSTLE[0]);
            break;
         case 41:
            this.playSoundAroundHer(SoundsHandler.MISC_BEDRUSTLE[1]);
            break;
         case 42:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_ELLIE_AHH), 3.0F);
         }

      };
      this.actionController.registerSoundListener(actionSoundListener);
      data.addAnimationController(this.actionController);
   }
}

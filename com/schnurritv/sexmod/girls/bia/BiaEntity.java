package com.schnurritv.sexmod.girls.bia;

import com.daripher.sexmod.client.util.FakeWorld;
import com.schnurritv.sexmod.Packets.ClearAnimationCache;
import com.schnurritv.sexmod.Packets.SendCompanionHome;
import com.schnurritv.sexmod.Packets.SendGirlToBed;
import com.schnurritv.sexmod.Packets.SetPlayerMovement;
import com.schnurritv.sexmod.companion.fighter.LookAtNearbyEntity;
import com.schnurritv.sexmod.events.HandlePlayerMovement;
import com.schnurritv.sexmod.girls.base.Fighter;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.gui.Sex.BlackScreenUI;
import com.schnurritv.sexmod.gui.Sex.SexUI;
import com.schnurritv.sexmod.util.PenisMath;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import com.schnurritv.sexmod.util.Handlers.SoundsHandler;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
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

public class BiaEntity extends Fighter {
   public boolean isPreparingTalk = false;
   int preparingTalkTick = 0;
   boolean lookingForBed = false;
   int bedSearchTick = 0;

   public BiaEntity(World worldIn) {
      super(worldIn);
      this.func_70105_a(0.49F, 1.65F);
      this.slashSwordRot = 140;
      this.stabSwordRot = 50;
      this.holdBowRot = 140;
      this.swordOffsetStab = new Vec3d(0.0D, -0.029999997854232782D, -0.2D);
   }

   protected String getGirlName() {
      return "Bia";
   }

   public void func_70619_bc() {
      super.func_70619_bc();
      if (this.isPreparingTalk) {
         ++this.preparingTalkTick;
         if (!this.func_174791_d().equals(TARGET_POS) && this.preparingTalkTick <= 40) {
            this.field_70177_z = this.targetYaw();

            try {
               TARGET_POS.equals((Object)null);
            } catch (NullPointerException var2) {
               this.setTargetPos(this.getInFrontOfPlayer());
            }

            this.func_189654_d(false);
            Vec3d nextPos = PenisMath.Lerp(this.func_174791_d(), this.targetPos(), 40 - this.preparingTalkTick);
            this.func_70107_b(nextPos.field_72450_a, nextPos.field_72448_b, nextPos.field_72449_c);
         } else {
            this.isPreparingTalk = false;
            this.preparingTalkTick = 0;
            this.setTargetYaw(this.field_70170_p.func_73046_m().func_184103_al().func_177451_a(this.playerSheHasSexWith()).field_70177_z + 180.0F);
            this.field_70180_af.func_187227_b(SHOULD_BE_AT_TARGET, true);
            this.func_70661_as().func_75499_g();
            this.checkFollowUp();
         }
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
            this.setCurrentAction(GirlEntity.Action.ANAL_PREPARE);
         }
      }

      if (this.currentAction() == GirlEntity.Action.ANAL_WAIT) {
         EntityPlayer player = this.field_70170_p.func_72890_a(this, 15.0D);
         if (player != null && (double)player.func_70032_d(this) < 1.0D) {
            player.field_70145_X = true;
            player.func_189654_d(true);
            PacketHandler.INSTANCE.sendTo(new SetPlayerMovement(false), (EntityPlayerMP)player);
            player.func_70080_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
            this.setPlayer(player.getPersistentID());
            this.moveCamera(-0.3D, -1.0D, -0.5D, 0.0F, this.field_70125_A);
            this.setCurrentAction(GirlEntity.Action.ANAL_START);
         }
      }

   }

   public boolean canCloseUiWithoutHavingChosen() {
      return this.playerSheHasSexWith() == null;
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
               this.sayAround(I18n.func_135052_a("bia.dialogue.busy", new Object[0]));
            }

            return true;
         }
      }
   }

   public boolean openMenu(EntityPlayer player) {
      if (this.playerSheHasSexWith() == null && (!this.hasMaster() || ((String)this.field_70180_af.func_187225_a(MASTER)).equals(Minecraft.func_71410_x().field_71439_g.getPersistentID().toString()))) {
         String[] actions = new String[]{(Integer)this.field_70180_af.func_187225_a(CURRENT_MODEL) == 1 ? "action.names.strip" : "action.names.dressup", "action.names.talk", "action.names.headpat"};
         renderMenu(player, this, actions, true);
         return true;
      } else {
         return false;
      }
   }

   void openSexMenu(EntityPlayer player) {
      renderMenu(player, this, new String[]{"action.names.anal"}, true);
   }

   public void onReset() {
      this.aiWander = new EntityAIWanderAvoidWater(this, 0.35D);
      this.aiLookAtPlayer = new LookAtNearbyEntity(this, EntityPlayer.class, 3.0F, 1.0F);
      this.field_70714_bg.func_75776_a(5, this.aiLookAtPlayer);
      this.field_70714_bg.func_75776_a(5, this.aiWander);
   }

   public void doAction(String animationName, UUID player) {
      super.doAction(animationName, player);
      if ("action.names.talk".equals(animationName)) {
         this.setPlayer(Minecraft.func_71410_x().field_71439_g.getPersistentID());
         this.changeDataParameterFromClient("playerSheHasSexWith", Minecraft.func_71410_x().field_71439_g.getPersistentID().toString());
         this.changeDataParameterFromClient("animationFollowUp", "talkHorny");
         this.prepareAction(player);
      } else if ("action.names.headpat".equals(animationName)) {
         this.setPlayer(Minecraft.func_71410_x().field_71439_g.getPersistentID());
         this.changeDataParameterFromClient("playerSheHasSexWith", Minecraft.func_71410_x().field_71439_g.getPersistentID().toString());
         this.changeDataParameterFromClient("animationFollowUp", "Headpat");
         this.prepareAction(player);
      } else if ("action.names.anal".equals(animationName)) {
         this.changeDataParameterFromClient("animationFollowUp", "anal");
         this.setCurrentAction(GirlEntity.Action.TALK_RESPONSE);
      } else if ("action.names.strip".equals(animationName)) {
         this.setCurrentAction(GirlEntity.Action.STRIP);
      } else if ("action.names.dressup".equals(animationName)) {
         this.setCurrentAction(GirlEntity.Action.STRIP);
      }

   }

   void prepareAction(UUID player) {
      this.prepareAction(true, true, player);
      HandlePlayerMovement.setActive(false);
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
               this.createAnimation("animation.bia.fhappy", true, event);
            } else {
               this.createAnimation("animation.bia.null", true, event);
            }
            break;
         case 1:
            if (this.currentAction() != GirlEntity.Action.NULL) {
               this.createAnimation("animation.bia.null", true, event);
            } else if (this.func_184218_aH()) {
               this.createAnimation("animation.bia.sit", true, event);
            } else if (Math.abs(this.field_70169_q - this.field_70165_t) + Math.abs(this.field_70166_s - this.field_70161_v) > 0.0D) {
               switch(this.getWalkSpeed()) {
               case RUN:
                  this.createAnimation("animation.bia.run", true, event);
                  break;
               case FAST_WALK:
                  this.createAnimation("animation.bia.fastwalk", true, event);
                  break;
               case WALK:
                  this.createAnimation("animation.bia.walk", true, event);
               }

               this.field_70177_z = this.field_70759_as;
            } else {
               this.createAnimation("animation.bia.idle", true, event);
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
               this.createAnimation("animation.bia.anal_wait", false, event);
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
               var3 = 1;
            }
            break;
         case -1823121897:
            if (var2.equals("anal_cumBlackScreen")) {
               var3 = 26;
            }
            break;
         case -794340298:
            if (var2.equals("anal_cumDone")) {
               var3 = 27;
            }
            break;
         case -794100347:
            if (var2.equals("anal_cumMSG2")) {
               var3 = 25;
            }
            break;
         case -712768382:
            if (var2.equals("anal_prepareDone")) {
               var3 = 18;
            }
            break;
         case -712528432:
            if (var2.equals("anal_prepareMSG1")) {
               var3 = 16;
            }
            break;
         case -712528431:
            if (var2.equals("anal_prepareMSG2")) {
               var3 = 17;
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
               var3 = 21;
            }
            break;
         case -193947491:
            if (var2.equals("anal_startDone")) {
               var3 = 24;
            }
            break;
         case -193707541:
            if (var2.equals("anal_startMSG1")) {
               var3 = 19;
            }
            break;
         case -193707540:
            if (var2.equals("anal_startMSG2")) {
               var3 = 22;
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
               var3 = 15;
            }
            break;
         case 770079076:
            if (var2.equals("talk_responseMSG1")) {
               var3 = 12;
            }
            break;
         case 770079077:
            if (var2.equals("talk_responseMSG2")) {
               var3 = 13;
            }
            break;
         case 770079078:
            if (var2.equals("talk_responseMSG3")) {
               var3 = 14;
            }
            break;
         case 1723338053:
            if (var2.equals("anal_fastDone")) {
               var3 = 23;
            }
            break;
         case 1723578003:
            if (var2.equals("anal_fastMSG1")) {
               var3 = 20;
            }
            break;
         case 1888031973:
            if (var2.equals("headpatDone")) {
               var3 = 32;
            }
            break;
         case 1888271923:
            if (var2.equals("headpatMSG1")) {
               var3 = 28;
            }
            break;
         case 1888271924:
            if (var2.equals("headpatMSG2")) {
               var3 = 29;
            }
            break;
         case 1888271925:
            if (var2.equals("headpatMSG3")) {
               var3 = 30;
            }
            break;
         case 1888271926:
            if (var2.equals("headpatMSG4")) {
               var3 = 31;
            }
            break;
         case 1902313509:
            if (var2.equals("talk_hornyDone")) {
               var3 = 11;
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
            this.sayAround(I18n.func_135052_a("bia.dialogue.hihi", new Object[0]));
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
            this.sayAround(I18n.func_135052_a("bia.dialogue.heya", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.GIRLS_BIA_HEY[3]);
            break;
         case 8:
            this.sayAround(I18n.func_135052_a("bia.dialogue.horny", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.GIRLS_BIA_GIGGLE[2]);
            break;
         case 9:
            this.sayAround(I18n.func_135052_a("bia.dialogue.so", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.GIRLS_BIA_BREATH[0]);
            break;
         case 10:
            this.sayAround(I18n.func_135052_a("bia.dialogue.fun", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.GIRLS_BIA_HUH[0]);
            break;
         case 11:
            this.setCurrentAction(GirlEntity.Action.TALK_IDLE);
            if (this.belongsToPlayer()) {
               this.openSexMenu(Minecraft.func_71410_x().field_71439_g);
            }
            break;
         case 12:
            this.sayAround(I18n.func_135052_a("bia.dialogue.huh", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.GIRLS_BIA_HUH[2]);
            break;
         case 13:
            this.sayAround(I18n.func_135052_a("bia.dialogue.iuhm", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.GIRLS_BIA_BREATH[1]);
            break;
         case 14:
            this.sayAround(I18n.func_135052_a("bia.dialogue.yes", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.GIRLS_BIA_GIGGLE[0]);
            break;
         case 15:
            this.resetPlayer();
            if ((Integer)this.field_70180_af.func_187225_a(CURRENT_MODEL) != 0) {
               this.setCurrentAction(GirlEntity.Action.STRIP);
            } else {
               this.checkFollowUp();
            }
            break;
         case 16:
            this.playSoundAroundHer(SoundsHandler.MISC_PLOB[0]);
            break;
         case 17:
            this.playSoundAroundHer(SoundsHandler.MISC_BEDRUSTLE[0]);
            break;
         case 18:
            this.setCurrentAction(GirlEntity.Action.ANAL_WAIT);
            if (this.belongsToPlayer()) {
               SexUI.resetCumPercentage();
            }
            break;
         case 19:
            this.playSoundAroundHer(SoundsHandler.GIRLS_BIA_MMM[3]);
            this.playSoundAroundHer(SoundsHandler.MISC_POUNDING[34]);
            break;
         case 20:
            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.02D);
            }
         case 21:
         case 22:
            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.02D);
            }

            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING), 0.5F);
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_BIA_AHH));
            break;
         case 23:
            this.playerIsThrusting = HandlePlayerMovement.isThrusting;
            if (this.playerIsThrusting) {
               break;
            }
         case 24:
            this.setCurrentAction(GirlEntity.Action.ANAL_SLOW);
            if (this.belongsToPlayer()) {
               SexUI.shouldBeRendered = true;
            }
            break;
         case 25:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_BIA_AHH));
            break;
         case 26:
            if (this.belongsToPlayer()) {
               BlackScreenUI.activate();
            }
            break;
         case 27:
            if (this.belongsToPlayer()) {
               SexUI.resetCumPercentage();
               this.resetGirl();
            }
            break;
         case 28:
            this.sayAround(I18n.func_135052_a("bia.dialogue.headpats", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.GIRLS_BIA_BREATH[0]);
            break;
         case 29:
            this.sayAround(I18n.func_135052_a("bia.dialogue.hmm", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.GIRLS_BIA_MMM[0]);
            break;
         case 30:
            this.sayAround(I18n.func_135052_a("bia.dialogue.huh2", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.GIRLS_BIA_HUH[0]);
            break;
         case 31:
            this.sayAround(I18n.func_135052_a("bia.dialogue.thankyou", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.GIRLS_BIA_GIGGLE[1]);
            break;
         case 32:
            this.resetGirl();
         }

      };
      this.actionController.registerSoundListener(actionSoundListener);
      data.addAnimationController(this.actionController);
   }

   public void goForAnal() {
      BlockPos temp = this.findBed(this.func_180425_c());
      if (temp == null) {
         this.playSoundAroundHer(SoundsHandler.GIRLS_BIA_BREATH[2]);
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
            this.playSoundAroundHer(SoundsHandler.GIRLS_BIA_BREATH[2]);
            this.sayAround(I18n.func_135052_a("jenny.dialogue.bedobscured", new Object[0]));
            return;
         }

         Vec3d targetPos = bedPos.func_178787_e(potentialSpaces[whichOne][0]);
         this.setTargetYaw((float)yaws[whichOne]);
         this.setTargetPos(targetPos);
         this.playerYaw = this.targetYaw();
         this.func_70661_as().func_75499_g();
         this.func_70661_as().func_75492_a(targetPos.field_72450_a, targetPos.field_72448_b, targetPos.field_72449_c, 0.35D);
         this.lookingForBed = true;
         this.bedSearchTick = 0;
      }

   }

   protected void thrust() {
      if (this.currentAction() == GirlEntity.Action.ANAL_FAST || this.currentAction() == GirlEntity.Action.ANAL_SLOW) {
         this.playerIsThrusting = true;
         if (this.currentAction() == GirlEntity.Action.ANAL_FAST) {
            PacketHandler.INSTANCE.sendToServer(new ClearAnimationCache(this.girlId()));
         } else {
            this.setCurrentAction(GirlEntity.Action.ANAL_FAST);
         }
      }

   }

   protected void cum() {
      if (this.currentAction() == GirlEntity.Action.ANAL_SLOW || this.currentAction() == GirlEntity.Action.ANAL_FAST) {
         this.playerIsCumming = true;
         this.setCurrentAction(GirlEntity.Action.ANAL_CUM);
      }

   }

   protected void checkFollowUp() {
      String var1 = (String)this.field_70180_af.func_187225_a(ANIMATION_FOLLOW_UP);
      byte var2 = -1;
      switch(var1.hashCode()) {
      case -1834996061:
         if (var1.equals("Headpat")) {
            var2 = 1;
         }
         break;
      case -1758027254:
         if (var1.equals("talkHorny")) {
            var2 = 0;
         }
         break;
      case 2998552:
         if (var1.equals("anal")) {
            var2 = 2;
         }
      }

      switch(var2) {
      case 0:
         this.setCurrentAction(GirlEntity.Action.TALK_HORNY);
         break;
      case 1:
         this.setCurrentAction(GirlEntity.Action.HEAD_PAT);
         break;
      case 2:
         this.resetGirl();
         PacketHandler.INSTANCE.sendToServer(new SendGirlToBed(this.girlId()));
      }

      if (this.field_70170_p.field_72995_K) {
         this.changeDataParameterFromClient("animationFollowUp", "");
      } else {
         this.field_70180_af.func_187227_b(ANIMATION_FOLLOW_UP, "");
      }

   }
}

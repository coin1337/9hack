package com.schnurritv.sexmod.girls.ellie;

import com.google.common.base.Optional;
import com.schnurritv.sexmod.Packets.ChangeDataParameter;
import com.schnurritv.sexmod.Packets.ClearAnimationCache;
import com.schnurritv.sexmod.Packets.SendCompanionHome;
import com.schnurritv.sexmod.Packets.SendEllieToPlayer;
import com.schnurritv.sexmod.Packets.SendGirlToBed;
import com.schnurritv.sexmod.Packets.SetPlayerMovement;
import com.schnurritv.sexmod.Packets.TeleportPlayer;
import com.schnurritv.sexmod.events.HandlePlayerMovement;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirl;
import com.schnurritv.sexmod.gui.Sex.BlackScreenUI;
import com.schnurritv.sexmod.gui.Sex.SexUI;
import com.schnurritv.sexmod.util.Reference;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import com.schnurritv.sexmod.util.Handlers.SoundsHandler;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
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

public class PlayerEllie extends PlayerGirl {
   boolean flySwitch = false;
   boolean moanBreak = false;

   protected PlayerEllie(World worldIn) {
      super(worldIn);
   }

   public PlayerEllie(World worldIn, UUID player) {
      super(worldIn, player);
   }

   public float getNameTagHeight() {
      return 2.05F;
   }

   public void startBedAnimation() {
      this.setCurrentAction(GirlEntity.Action.SITDOWN);
   }

   public void startStandingSex(String action, UUID male) {
   }

   public void doAction(String actionName, UUID player) {
      if ("action.names.cowgirl".equals(actionName)) {
         this.changeDataParameterFromClient("animationFollowUp", "Cowgirl");
      }

      if ("action.names.missionary".equals(actionName)) {
         this.changeDataParameterFromClient("animationFollowUp", "Missionary");
      }

   }

   public boolean openMenu(EntityPlayer player) {
      renderMenu(player, this, new String[]{"action.names.cowgirl", "action.names.missionary"}, true);
      return true;
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

   protected void cum() {
      if (this.currentAction() != GirlEntity.Action.COWGIRLFAST && this.currentAction() != GirlEntity.Action.COWGIRLSLOW) {
         if (this.currentAction() == GirlEntity.Action.MISSIONARY_FAST || this.currentAction() == GirlEntity.Action.MISSIONARY_SLOW) {
            this.playerIsCumming = true;
            this.setCurrentAction(GirlEntity.Action.MISSIONARY_CUM);
         }
      } else {
         this.playerIsCumming = true;
         this.setCurrentAction(GirlEntity.Action.COWGIRLCUM);
      }

   }

   public void func_70619_bc() {
      super.func_70619_bc();
      if (this.currentAction() == GirlEntity.Action.SITDOWNIDLE) {
         String followUp = (String)this.field_70180_af.func_187225_a(GirlEntity.ANIMATION_FOLLOW_UP);
         if (!"Missionary".equals(followUp) && !"Cowgirl".equals(followUp)) {
            return;
         }

         EntityPlayer closestPlayer = this.getClosestPlayer();
         if (closestPlayer == null || closestPlayer.func_70011_f(this.getRenderPos().field_72450_a, this.getRenderPos().field_72448_b, this.getRenderPos().field_72449_c) > 1.0D) {
            return;
         }

         this.field_70180_af.func_187227_b(GirlEntity.ANIMATION_FOLLOW_UP, "");
         this.field_70180_af.func_187227_b(GirlEntity.CURRENT_MODEL, 0);
         this.setPlayer(closestPlayer.getPersistentID());
         EntityPlayerMP girlPlayer = (EntityPlayerMP)this.field_70170_p.func_152378_a((UUID)((Optional)this.field_70180_af.func_187225_a(OWNER)).get());
         PacketHandler.INSTANCE.sendTo(new SetPlayerMovement(false), (EntityPlayerMP)closestPlayer);
         PacketHandler.INSTANCE.sendTo(new SetPlayerMovement(false), girlPlayer);
         closestPlayer.func_191958_b(0.0F, 0.0F, 0.0F, 0.0F);
         girlPlayer.field_71075_bZ.field_75100_b = true;
         closestPlayer.field_71075_bZ.field_75100_b = true;
         girlPlayer.field_70145_X = true;
         closestPlayer.field_70145_X = true;
         girlPlayer.func_189654_d(true);
         closestPlayer.func_189654_d(true);
         Vec3d playerPos;
         if ("Missionary".equals(followUp)) {
            this.setCurrentAction(GirlEntity.Action.MISSIONARY_START);
            playerPos = this.getRenderPos().func_178786_a(0.0D, 0.1D, 0.0D);
            closestPlayer.func_70080_a(playerPos.field_72450_a, playerPos.field_72448_b, playerPos.field_72449_c, this.targetYaw(), 60.0F);
            closestPlayer.func_70634_a(playerPos.field_72450_a, playerPos.field_72448_b, playerPos.field_72449_c);
         } else {
            this.setCurrentAction(GirlEntity.Action.COWGIRLSTART);
            playerPos = this.getRenderPos().func_178787_e(new Vec3d(-Math.sin((double)this.targetYaw() * 0.017453292519943295D) * 1.8D, -0.65D, Math.cos((double)this.targetYaw() * 0.017453292519943295D) * 1.8D));
            closestPlayer.func_70080_a(playerPos.field_72450_a, playerPos.field_72448_b, playerPos.field_72449_c, 180.0F + this.targetYaw(), -30.0F);
            closestPlayer.func_70634_a(playerPos.field_72450_a, playerPos.field_72448_b, playerPos.field_72449_c);
         }
      }

   }

   boolean shouldCrouch() {
      return this.field_70170_p.func_180495_p(new BlockPos(this.getRenderPos().func_72441_c(0.0D, 2.0D, 0.0D))).func_177230_c() != Blocks.field_150350_a;
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
            this.createAnimation("animation.ellie.eyes", true, event);
         } else {
            this.createAnimation("animation.ellie.null", true, event);
         }
         break;
      case 1:
         if (this.currentAction() != GirlEntity.Action.NULL) {
            this.createAnimation("animation.ellie.null", true, event);
         } else if (this.isRiding) {
            this.createAnimation("animation.ellie.ride", true, event);
         } else {
            if (this.movementController.getCurrentAnimation() != null && this.movementController.getCurrentAnimation().animationName.contains("fly") && this.field_70122_E) {
               this.flySwitch = !this.flySwitch;
            }

            if (!this.field_70122_E) {
               this.createAnimation("animation.ellie.fly" + (this.flySwitch ? "2" : ""), true, event);
            } else if (Math.abs(this.movementVector.x) + Math.abs(this.movementVector.y) > 0.0F) {
               if (this.isSprinting) {
                  this.createAnimation(this.shouldCrouch() ? "animation.ellie.crouchwalk" : "animation.ellie.run", true, event);
               } else if (this.movementVector.y >= -0.1F) {
                  this.createAnimation(this.shouldCrouch() ? "animation.ellie.crouchwalk" : "animation.ellie.fastwalk", true, event);
               } else {
                  this.createAnimation(this.shouldCrouch() ? "animation.ellie.crouchwalk" : "animation.ellie.backwards_walk", true, event);
               }
            } else {
               this.createAnimation(this.shouldCrouch() ? "animation.ellie.crouchidle" : "animation.ellie.idle", true, event);
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
            if (this.isOwner()) {
               this.setCurrentAction(GirlEntity.Action.SITDOWNIDLE);
               this.openMenu(this.field_70170_p.func_152378_a(this.getOwner()));
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
            if (this.belongsToPlayer()) {
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

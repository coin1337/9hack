package com.schnurritv.sexmod.girls.allie;

import com.daripher.sexmod.client.util.FakeWorld;
import com.schnurritv.sexmod.Packets.ClearAnimationCache;
import com.schnurritv.sexmod.Packets.PrepareAction;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirl;
import com.schnurritv.sexmod.gui.Sex.BlackScreenUI;
import com.schnurritv.sexmod.gui.Sex.SexUI;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import com.schnurritv.sexmod.util.Handlers.SoundsHandler;
import java.util.UUID;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public class PlayerAllie extends PlayerGirl {
   public float extraNameTagHeight = 0.0F;
   EntityPlayer prevPlayer = null;

   protected PlayerAllie(World worldIn) {
      super(worldIn);
   }

   public PlayerAllie(World worldIn, UUID player) {
      super(worldIn, player);
   }

   public float getNameTagHeight() {
      return 1.9F + this.extraNameTagHeight;
   }

   public boolean hasBedAnimation() {
      return false;
   }

   public void startStandingSex(String action, UUID male) {
      if ("action.names.deepthroat".equals(action)) {
         this.prepareAction(male);
         this.setCurrentAction(GirlEntity.Action.DEEPTHROAT_START);
      }

   }

   public boolean openMenu(EntityPlayer player) {
      renderMenu(player, this, new String[]{"action.names.deepthroat"}, false);
      return true;
   }

   public void func_70619_bc() {
      super.func_70619_bc();
      if (this.getOwner() != null) {
         EntityPlayer player = this.field_70170_p.func_152378_a(this.getOwner());
         if (player != null && this.prevPlayer == null) {
            this.allowOwnerFlying(true);
            System.out.println("ehhh");
         }

         this.prevPlayer = player;
      }
   }

   public void onCreation() {
      this.allowOwnerFlying(true);
   }

   public void onDeletion() {
      this.allowOwnerFlying(false);
   }

   protected void thrust() {
      if (this.currentAction() == GirlEntity.Action.DEEPTHROAT_FAST || this.currentAction() == GirlEntity.Action.DEEPTHROAT_SLOW) {
         this.playerIsThrusting = true;
         if (this.currentAction() == GirlEntity.Action.DEEPTHROAT_FAST) {
            PacketHandler.INSTANCE.sendToServer(new ClearAnimationCache(this.girlId()));
         } else {
            this.setCurrentAction(GirlEntity.Action.DEEPTHROAT_FAST);
         }
      }

   }

   protected void cum() {
      if (this.currentAction() == GirlEntity.Action.DEEPTHROAT_FAST || this.currentAction() == GirlEntity.Action.DEEPTHROAT_SLOW) {
         this.playerIsCumming = true;
         this.setCurrentAction(GirlEntity.Action.DEEPTHROAT_CUM);
      }

   }

   public void registerControllers(AnimationData data) {
      super.registerControllers(data);
      AnimationController.ISoundListener soundListener = (event) -> {
         String var2 = event.sound;
         byte var3 = -1;
         switch(var2.hashCode()) {
         case -1961942550:
            if (var2.equals("attackDone")) {
               var3 = 1;
            }
            break;
         case -1295947090:
            if (var2.equals("deepthroat_cumDone")) {
               var3 = 11;
            }
            break;
         case -1295707140:
            if (var2.equals("deepthroat_cumMSG1")) {
               var3 = 10;
            }
            break;
         case -1201737451:
            if (var2.equals("deepthroat_startDone")) {
               var3 = 8;
            }
            break;
         case -941570611:
            if (var2.equals("deepthroat_fastDone")) {
               var3 = 7;
            }
            break;
         case -941330661:
            if (var2.equals("deepthroat_fastMSG1")) {
               var3 = 6;
            }
            break;
         case -676816985:
            if (var2.equals("attackSound")) {
               var3 = 0;
            }
            break;
         case 713758766:
            if (var2.equals("deepthroat_normal_prepareMSG1")) {
               var3 = 12;
            }
            break;
         case 1301227072:
            if (var2.equals("deepthroat_slowMSG1")) {
               var3 = 9;
            }
            break;
         case 1319834923:
            if (var2.equals("blackscreen")) {
               var3 = 4;
            }
            break;
         case 1463688954:
            if (var2.equals("deepthroat_prepareDone")) {
               var3 = 5;
            }
            break;
         case 1463928904:
            if (var2.equals("deepthroat_prepareMSG1")) {
               var3 = 2;
            }
            break;
         case 1463928905:
            if (var2.equals("deepthroat_prepareMSG2")) {
               var3 = 3;
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
            this.sayAround(I18n.func_135052_a("allie.dialogue.hihi", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.MISC_PLOB[0]);
            break;
         case 3:
            this.sayAround(I18n.func_135052_a("allie.dialogue.boys", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.MISC_PLOB[0]);
            break;
         case 4:
            if (this.belongsToPlayer()) {
               BlackScreenUI.activate();
            }
            break;
         case 5:
            this.setCurrentAction(GirlEntity.Action.DEEPTHROAT_START);
            if (this.belongsToPlayer()) {
               PacketHandler.INSTANCE.sendToServer(new PrepareAction(this.girlId(), this.playerSheHasSexWith(), false, true));
               this.playerYaw = this.field_70177_z + 180.0F;
               this.moveCamera(0.0D, 0.0D, 1.350000023841858D, 0.0F, 30.0F);
               SexUI.resetCumPercentage();
            }
            break;
         case 6:
            if (this.belongsToPlayer()) {
               this.playerIsThrusting = false;
               SexUI.addCumPercentage(0.03999999910593033D);
            }
            break;
         case 7:
         case 8:
            this.setCurrentAction(GirlEntity.Action.DEEPTHROAT_SLOW);
            break;
         case 9:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING));
            if (this.belongsToPlayer()) {
               SexUI.shouldBeRendered = true;
               SexUI.addCumPercentage(0.019999999552965164D);
            }
            break;
         case 10:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING));
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_CUMINFLATION), 1.5F);
            break;
         case 11:
            if (this.belongsToPlayer()) {
               this.resetGirl();
            }
            break;
         case 12:
            this.sayAround(I18n.func_135052_a("allie.dialogue.alright", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_PLOB));
         }

      };
      this.actionController.registerSoundListener(soundListener);
      data.addAnimationController(this.actionController);
   }

   protected <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
      if (this.field_70170_p instanceof FakeWorld) {
         return PlayState.STOP;
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
            if (this.currentAction() != GirlEntity.Action.NULL || !this.currentAction().autoBlink) {
               this.createAnimation("animation.allie.null", true, event);
            }
            break;
         case 1:
            this.createAnimation("animation.allie.tail", true, event);
            break;
         case 2:
            switch(this.currentAction()) {
            case SUMMON:
               this.createAnimation("animation.allie.summon", false, event);
               break;
            case SUMMON_NORMAL:
               this.createAnimation("animation.allie.summon_normal", false, event);
               break;
            case SUMMON_NORMAL_WAIT:
               this.createAnimation("animation.allie.summon_normal_wait", true, event);
               break;
            case SUMMON_WAIT:
               this.createAnimation("animation.allie.summon_wait", true, event);
               break;
            case DEEPTHROAT_PREPARE:
               this.createAnimation("animation.allie.deepthroat_prepare", false, event);
               break;
            case DEEPTHROAT_NORMAL_PREPARE:
               this.createAnimation("animation.allie.deepthroat_normal_prepare", false, event);
               break;
            case DEEPTHROAT_START:
               this.createAnimation("animation.allie.deepthroat_start", false, event);
               break;
            case DEEPTHROAT_SLOW:
               this.createAnimation("animation.allie.deepthroat_slow", true, event);
               break;
            case DEEPTHROAT_FAST:
               this.createAnimation("animation.allie.deepthroat_fast", true, event);
               break;
            case DEEPTHROAT_CUM:
               this.createAnimation("animation.allie.deepthroat_cum", false, event);
               break;
            case RICH:
               this.createAnimation("animation.allie.rich", false, event);
               break;
            case RICH_NORMAL:
               this.createAnimation("animation.allie.rich_normal", false, event);
               break;
            case SUMMON_SAND:
               this.createAnimation("animation.allie.summon_sand", false, event);
               break;
            case ATTACK:
               this.createAnimation("animation.allie.attack" + this.nextAttack, false, event);
               break;
            case BOW:
               this.createAnimation("animation.allie.bowcharge", false, event);
            }
         }

         return PlayState.CONTINUE;
      }
   }
}

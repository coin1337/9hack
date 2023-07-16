package com.schnurritv.sexmod.girls.allie;

import com.daripher.sexmod.client.util.FakeWorld;
import com.schnurritv.sexmod.Packets.ClearAnimationCache;
import com.schnurritv.sexmod.Packets.MakeRichWish;
import com.schnurritv.sexmod.Packets.PrepareAction;
import com.schnurritv.sexmod.Packets.WishDone;
import com.schnurritv.sexmod.events.HandlePlayerMovement;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.gui.Sex.BlackScreenUI;
import com.schnurritv.sexmod.gui.Sex.SexUI;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import com.schnurritv.sexmod.util.Handlers.SoundsHandler;
import java.util.UUID;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public class AllieEntity extends GirlEntity {
   float alpha = 1.0F;
   public boolean forceOpenUi = false;
   public static final DataParameter<ItemStack> LAMP;

   public AllieEntity(World worldIn) {
      super(worldIn);
   }

   public AllieEntity(World worldIn, ItemStack lamp) {
      super(worldIn);
      this.field_70180_af.func_187227_b(LAMP, lamp);
   }

   protected String getGirlName() {
      return "Allie";
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(LAMP, ItemStack.field_190927_a);
   }

   public boolean firstWish() {
      return ((ItemStack)this.field_70180_af.func_187225_a(LAMP)).func_77978_p().func_74762_e("uses") == 1;
   }

   public boolean canCloseUiWithoutHavingChosen() {
      return true;
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.alpha != 1.0F && this.alpha != -69.0F && this.alpha <= 0.0F) {
         if (this.belongsToPlayer()) {
            PacketHandler.INSTANCE.sendToServer(new WishDone(this.girlId()));
            HandlePlayerMovement.setActive(true);
         }

         this.alpha = -69.0F;
      }

      if (this.forceOpenUi) {
         this.openMenu(this.field_70170_p.func_152378_a(this.playerSheHasSexWith()));
         this.forceOpenUi = false;
      }

   }

   public boolean openMenu(EntityPlayer player) {
      String[] actions = new String[]{"action.names.makemerichallie", "action.names.deepthroat"};
      renderMenu(player, this, actions, false);
      return true;
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
            }
         }

         return PlayState.CONTINUE;
      }
   }

   public void registerControllers(AnimationData data) {
      super.registerControllers(data);
      AnimationController.ISoundListener soundListener = (event) -> {
         String var2 = event.sound;
         byte var3 = -1;
         switch(var2.hashCode()) {
         case -1295947090:
            if (var2.equals("deepthroat_cumDone")) {
               var3 = 17;
            }
            break;
         case -1295707140:
            if (var2.equals("deepthroat_cumMSG1")) {
               var3 = 16;
            }
            break;
         case -1201737451:
            if (var2.equals("deepthroat_startDone")) {
               var3 = 14;
            }
            break;
         case -941570611:
            if (var2.equals("deepthroat_fastDone")) {
               var3 = 13;
            }
            break;
         case -843793805:
            if (var2.equals("rich_MSG1")) {
               var3 = 25;
            }
            break;
         case -177721437:
            if (var2.equals("disappear")) {
               var3 = 26;
            }
            break;
         case 79986691:
            if (var2.equals("summonDone")) {
               var3 = 8;
            }
            break;
         case 80226641:
            if (var2.equals("summonMSG1")) {
               var3 = 0;
            }
            break;
         case 80226642:
            if (var2.equals("summonMSG2")) {
               var3 = 1;
            }
            break;
         case 80226643:
            if (var2.equals("summonMSG3")) {
               var3 = 2;
            }
            break;
         case 80226644:
            if (var2.equals("summonMSG4")) {
               var3 = 3;
            }
            break;
         case 80226645:
            if (var2.equals("summonMSG5")) {
               var3 = 4;
            }
            break;
         case 80226646:
            if (var2.equals("summonMSG6")) {
               var3 = 5;
            }
            break;
         case 80226647:
            if (var2.equals("summonMSG7")) {
               var3 = 6;
            }
            break;
         case 80226648:
            if (var2.equals("summonMSG8")) {
               var3 = 7;
            }
            break;
         case 713758766:
            if (var2.equals("deepthroat_normal_prepareMSG1")) {
               var3 = 24;
            }
            break;
         case 1301227072:
            if (var2.equals("deepthroat_slowMSG1")) {
               var3 = 15;
            }
            break;
         case 1319834923:
            if (var2.equals("blackscreen")) {
               var3 = 11;
            }
            break;
         case 1463688954:
            if (var2.equals("deepthroat_prepareDone")) {
               var3 = 12;
            }
            break;
         case 1463928904:
            if (var2.equals("deepthroat_prepareMSG1")) {
               var3 = 9;
            }
            break;
         case 1463928905:
            if (var2.equals("deepthroat_prepareMSG2")) {
               var3 = 10;
            }
            break;
         case 1865841938:
            if (var2.equals("summon_sandMSG1")) {
               var3 = 27;
            }
            break;
         case 1865841939:
            if (var2.equals("summon_sandMSG2")) {
               var3 = 28;
            }
            break;
         case 1895298855:
            if (var2.equals("summon_normalDone")) {
               var3 = 23;
            }
            break;
         case 1895538805:
            if (var2.equals("summon_normalMSG1")) {
               var3 = 18;
            }
            break;
         case 1895538806:
            if (var2.equals("summon_normalMSG2")) {
               var3 = 19;
            }
            break;
         case 1895538807:
            if (var2.equals("summon_normalMSG3")) {
               var3 = 20;
            }
            break;
         case 1895538808:
            if (var2.equals("summon_normalMSG4")) {
               var3 = 21;
            }
            break;
         case 1895538809:
            if (var2.equals("summon_normalMSG5")) {
               var3 = 22;
            }
         }

         switch(var3) {
         case 0:
            this.sayAround(I18n.func_135052_a("allie.dialogue.summon1", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.MISC_PLOB[0]);
            break;
         case 1:
            this.sayAround(I18n.func_135052_a("allie.dialogue.summon2", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.MISC_PLOB[0]);
            break;
         case 2:
            this.sayAround(I18n.func_135052_a("allie.dialogue.summon3", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.MISC_PLOB[0]);
            break;
         case 3:
            this.sayAround(I18n.func_135052_a("allie.dialogue.summon4", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.MISC_PLOB[0]);
            break;
         case 4:
            this.sayAround(I18n.func_135052_a("allie.dialogue.summon5", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.MISC_PLOB[0]);
            break;
         case 5:
            this.sayAround(I18n.func_135052_a("allie.dialogue.summon6", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.MISC_PLOB[0]);
            break;
         case 6:
            this.sayAround(I18n.func_135052_a("allie.dialogue.summon7", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.MISC_PLOB[0]);
            break;
         case 7:
            this.sayAround(I18n.func_135052_a("allie.dialogue.summon8", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.MISC_PLOB[0]);
            this.openMenu(this.field_70170_p.func_152378_a(this.playerSheHasSexWith()));
            break;
         case 8:
            this.setCurrentAction(GirlEntity.Action.SUMMON_WAIT);
            break;
         case 9:
            this.sayAround(I18n.func_135052_a("allie.dialogue.hihi", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.MISC_PLOB[0]);
            break;
         case 10:
            this.sayAround(I18n.func_135052_a("allie.dialogue.boys", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.MISC_PLOB[0]);
            break;
         case 11:
            if (this.belongsToPlayer()) {
               BlackScreenUI.activate();
            }
            break;
         case 12:
            this.setCurrentAction(GirlEntity.Action.DEEPTHROAT_START);
            if (this.belongsToPlayer()) {
               PacketHandler.INSTANCE.sendToServer(new PrepareAction(this.girlId(), this.playerSheHasSexWith(), false, true));
               this.playerYaw = this.field_70177_z + 180.0F;
               this.moveCamera(0.0D, 0.0D, 1.350000023841858D, 0.0F, 30.0F);
               SexUI.resetCumPercentage();
            }
            break;
         case 13:
            if (this.belongsToPlayer()) {
               this.playerIsThrusting = HandlePlayerMovement.isThrusting;
               SexUI.addCumPercentage(0.03999999910593033D);
            }

            if (this.playerIsThrusting) {
               break;
            }
         case 14:
            this.setCurrentAction(GirlEntity.Action.DEEPTHROAT_SLOW);
            break;
         case 15:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING));
            if (this.belongsToPlayer()) {
               SexUI.shouldBeRendered = true;
               SexUI.addCumPercentage(0.019999999552965164D);
            }
            break;
         case 16:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING));
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_CUMINFLATION), 1.5F);
            break;
         case 17:
            if (this.belongsToPlayer()) {
               this.resetGirl();
               PacketHandler.INSTANCE.sendToServer(new WishDone(this.girlId()));
            }
            break;
         case 18:
            this.sayAround(I18n.func_135052_a("allie.dialogue.sup", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_PLOB));
            break;
         case 19:
            this.sayAround(I18n.func_135052_a("allie.dialogue.youhave", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_PLOB));
            break;
         case 20:
            if (((ItemStack)this.field_70180_af.func_187225_a(LAMP)).func_77978_p().func_74762_e("uses") == 2) {
               this.sayAround(I18n.func_135052_a("allie.dialogue.2wishes", new Object[0]));
            } else {
               this.sayAround(I18n.func_135052_a("allie.dialogue.1wish", new Object[0]));
            }

            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_PLOB));
            break;
         case 21:
            this.sayAround("So...");
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_PLOB));
            break;
         case 22:
            this.sayAround(I18n.func_135052_a("allie.dialogue.tellme", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_PLOB));
            break;
         case 23:
            this.setCurrentAction(GirlEntity.Action.SUMMON_NORMAL_WAIT);
            if (this.belongsToPlayer()) {
               this.openMenu(this.field_70170_p.func_152378_a(this.playerSheHasSexWith()));
            }
            break;
         case 24:
            this.sayAround(I18n.func_135052_a("allie.dialogue.alright", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_PLOB));
            break;
         case 25:
            this.sayAround(I18n.func_135052_a("allie.dialogue.wishgranted", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_PLOB));
            if (this.belongsToPlayer()) {
               PacketHandler.INSTANCE.sendToServer(new MakeRichWish(this.func_174791_d()));
            }
            break;
         case 26:
            this.alpha = 0.99F;
            break;
         case 27:
            this.sayAround(I18n.func_135052_a("allie.dialogue.nooo", new Object[0]));
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_PLOB));
            break;
         case 28:
            if (this.isClosestPlayer()) {
               this.say(I18n.func_135052_a("allie.dialogue.phobia", new Object[0]), true);
            }
         }

      };
      this.actionController.registerSoundListener(soundListener);
      data.addAnimationController(this.actionController);
   }

   public void doAction(String actionName, UUID player) {
      byte var4 = -1;
      switch(actionName.hashCode()) {
      case -1887319439:
         if (actionName.equals("Deepthroat!")) {
            var4 = 1;
         }
         break;
      case 597628943:
         if (actionName.equals("Make me rich!")) {
            var4 = 0;
         }
      }

      switch(var4) {
      case 0:
         if (this.firstWish()) {
            this.setCurrentAction(GirlEntity.Action.RICH);
         } else {
            this.setCurrentAction(GirlEntity.Action.RICH_NORMAL);
         }
         break;
      case 1:
         if (this.firstWish()) {
            this.setCurrentAction(GirlEntity.Action.DEEPTHROAT_PREPARE);
         } else {
            this.setCurrentAction(GirlEntity.Action.DEEPTHROAT_NORMAL_PREPARE);
         }
      }

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

   protected void checkFollowUp() {
   }

   static {
      LAMP = EntityDataManager.func_187226_a(GirlEntity.class, DataSerializers.field_187196_f).func_187156_b().func_187161_a(74);
   }
}

package com.schnurritv.sexmod.girls.cat;

import com.daripher.sexmod.client.util.FakeWorld;
import com.schnurritv.sexmod.Packets.CatActivateFishing;
import com.schnurritv.sexmod.Packets.CatEatingDone;
import com.schnurritv.sexmod.Packets.CatThrowAwayItem;
import com.schnurritv.sexmod.Packets.ClearAnimationCache;
import com.schnurritv.sexmod.Packets.SendCompanionHome;
import com.schnurritv.sexmod.companion.fighter.LookAtNearbyEntity;
import com.schnurritv.sexmod.events.HandlePlayerMovement;
import com.schnurritv.sexmod.girls.base.Fighter;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.cat.fishing.CatFishHook;
import com.schnurritv.sexmod.girls.cat.fishing.CatFishingRod;
import com.schnurritv.sexmod.gui.Sex.BlackScreenUI;
import com.schnurritv.sexmod.gui.Sex.SexUI;
import com.schnurritv.sexmod.gui.menu.FighterUI;
import com.schnurritv.sexmod.util.PenisMath;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import com.schnurritv.sexmod.util.Handlers.SoundsHandler;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public class CatEntity extends Fighter {
   public static final DataParameter<Float> TARGET_DISTANCE;
   public static final DataParameter<ItemStack> FISHING_ROD;
   public static final DataParameter<Boolean> IS_FISHING;
   public static final DataParameter<ItemStack> CAUGHT_ITEM;
   static final float WALK_SPEED = 3.0F;
   static final float HUNGER_START_EATING_TICK = 1200.0F;
   @Nullable
   public CatFishHook fishEntity;
   public float fishSizePercentage = 1.0F;
   public float throwBackPercentage = 0.0F;
   int hunger = 8000;
   public boolean isPreparingTalk = false;
   int preparingTalkTick = 0;
   public BlockPos chosenFishingSpot;
   int chosenFishingSpotDeepness = 0;
   int oldFishingSpotNotAvailableTick = 0;
   boolean isAtFishingSpot;
   long deleteHookAtThisTick = 0L;
   Path lastPath = null;
   int fishEaten = 0;
   boolean moanBoobsSlow = false;
   boolean playIdle2 = false;

   public CatEntity(World worldIn) {
      super(worldIn);
      this.slashSwordRot = 230;
      this.stabSwordRot = 150;
      this.holdBowRot = 320;
      this.swordOffsetStab = new Vec3d(0.0D, -0.05999999718368053D, 0.10000001192092894D);
   }

   protected String getGirlName() {
      return "Luna";
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(TARGET_DISTANCE, 0.0F);
      this.field_70180_af.func_187214_a(FISHING_ROD, new ItemStack(CatFishingRod.CAT_FISHING_ROD));
      this.field_70180_af.func_187214_a(IS_FISHING, false);
      this.field_70180_af.func_187214_a(CAUGHT_ITEM, ItemStack.field_190927_a);
   }

   public boolean canCloseUiWithoutHavingChosen() {
      return false;
   }

   public float func_70047_e() {
      return 1.34F;
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
      String[] actions = new String[]{"action.names.sex", "action.names.touchboobs"};
      ItemStack[] prices = new ItemStack[]{new ItemStack(Items.field_151115_aP, 3, 0), new ItemStack(Items.field_151115_aP, 2, 1)};
      renderMenu(player, this, actions, prices);
      return true;
   }

   @SideOnly(Side.CLIENT)
   protected static void renderMenu(EntityPlayer player, GirlEntity girl, String[] animation, ItemStack[] stacks) {
      Minecraft.func_71410_x().func_147108_a(new FighterUI(girl, player, animation, stacks, true));
   }

   public void catchItem(ItemStack caughtItemStack) {
      this.field_70180_af.func_187227_b(CAUGHT_ITEM, caughtItemStack);
   }

   public void preparePayment() {
      this.isPreparingTalk = true;
   }

   public void func_70619_bc() {
      super.func_70619_bc();
      if (!this.hasMaster()) {
         this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(1.0D);
      } else {
         this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.5D);
      }

      this.manageTargetDistance();
      this.manageFishing();
      this.field_70180_af.func_187227_b(IS_FISHING, this.fishEntity != null && this.field_70180_af.func_187225_a(CAUGHT_ITEM) == ItemStack.field_190927_a);
      if (this.deleteHookAtThisTick == this.field_70170_p.func_82737_E() && this.fishEntity != null) {
         this.field_70170_p.func_72900_e(this.fishEntity);
         this.fishEntity = null;
      }

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

   }

   public void throwAwayItem() {
      EntityItem item = new EntityItem(this.field_70170_p, this.field_70165_t, this.field_70163_u, this.field_70161_v, (ItemStack)this.field_70180_af.func_187225_a(CAUGHT_ITEM));
      Vec3d vel = PenisMath.getLocalVector(new Vec3d(0.0D, 0.20000000298023224D + Math.random() * 0.10000000149011612D, -0.20000000298023224D + Math.random() * -0.10000000149011612D), this.field_70177_z);
      item.field_70159_w = vel.field_72450_a;
      item.field_70181_x = vel.field_72448_b;
      item.field_70179_y = vel.field_72449_c;
      this.field_70170_p.func_72838_d(item);
      this.field_70180_af.func_187227_b(CAUGHT_ITEM, ItemStack.field_190927_a);
   }

   public void doneFishing() {
      this.chosenFishingSpot = null;
      this.chosenFishingSpotDeepness = 0;
      this.oldFishingSpotNotAvailableTick = 0;
      this.isAtFishingSpot = false;
      this.field_70180_af.func_187227_b(SHOULD_BE_AT_TARGET, false);
      this.field_70180_af.func_187227_b(CAUGHT_ITEM, ItemStack.field_190927_a);
      this.func_174810_b(false);
      this.setCurrentAction(GirlEntity.Action.NULL);
      if (this.fishEntity != null) {
         this.field_70170_p.func_72900_e(this.fishEntity);
         this.fishEntity = null;
      }

      if (this.playerSheHasSexWith() == null) {
         this.aiLookAtPlayer = new LookAtNearbyEntity(this, EntityPlayer.class, 3.0F, 1.0F);
         this.field_70714_bg.func_75776_a(5, this.aiLookAtPlayer);
         if (!this.hasMaster()) {
            this.aiWander = new EntityAIWanderAvoidWater(this, 0.35D);
            this.field_70714_bg.func_75776_a(5, this.aiWander);
            System.out.println("done Fishing");
         }
      }
   }

   public void fishEaten() {
      this.doneFishing();
      if (++this.fishEaten >= 3) {
         this.fishEaten = 0;
         this.hunger = 0;
      }

   }

   void manageFishing() {
      if (!this.hasMaster() && this.playerSheHasSexWith() == null) {
         if (!((float)(++this.hunger) < 1200.0F)) {
            this.lookForFishingSpot();
            this.goToFishingSpot();
            if (this.chosenFishingSpot != null && this.lastPath == null && this.func_70661_as().func_75505_d() == null && !this.field_70171_ac && this.field_70122_E) {
               this.func_174810_b(true);
               if (this.aiWander != null) {
                  this.field_70714_bg.func_85156_a(this.aiWander);
                  this.aiWander = null;
               }

               if (this.aiLookAtPlayer != null) {
                  this.field_70714_bg.func_85156_a(this.aiLookAtPlayer);
                  this.aiLookAtPlayer = null;
               }

               if (this.currentAction() == GirlEntity.Action.NULL) {
                  this.setCurrentAction(GirlEntity.Action.FISHING_START);
                  this.setTargetPos(this.func_174791_d());
                  this.field_70180_af.func_187227_b(SHOULD_BE_AT_TARGET, true);
                  this.setTargetYaw((float)Math.atan2(this.field_70161_v - (double)this.chosenFishingSpot.func_177952_p(), this.field_70165_t - (double)this.chosenFishingSpot.func_177958_n()) * 57.29578F + 90.0F);
               }

               if (this.fishEntity != null && this.fishEntity.ticksCatchable == 15) {
                  ((CatFishingRod)((ItemStack)this.field_70180_af.func_187225_a(FISHING_ROD)).func_77973_b()).onItemRightClick(this.field_70170_p, this, EnumHand.MAIN_HAND);
                  this.deleteHookAtThisTick = this.field_70170_p.func_82737_E() + 20L;
                  if (((ItemStack)this.field_70180_af.func_187225_a(CAUGHT_ITEM)).func_77973_b() instanceof ItemFood) {
                     this.setCurrentAction(GirlEntity.Action.FISHING_EAT);
                  } else {
                     this.setCurrentAction(GirlEntity.Action.FISHING_THROW_AWAY);
                  }
               }

            } else {
               this.field_70180_af.func_187227_b(SHOULD_BE_AT_TARGET, false);
               this.lastPath = this.func_70661_as().func_75505_d();
            }
         }
      } else {
         if ((Boolean)this.field_70180_af.func_187225_a(IS_FISHING)) {
            this.doneFishing();
         }

      }
   }

   void goToFishingSpot() {
      if (this.chosenFishingSpot != null) {
         PathNavigate navi = this.func_70661_as();
         navi.func_75492_a((double)this.chosenFishingSpot.func_177958_n(), (double)this.chosenFishingSpot.func_177956_o(), (double)this.chosenFishingSpot.func_177952_p(), 0.3499999940395355D);
         Path path = navi.func_75505_d();
         if (path != null) {
            if (path.func_75874_d() > path.func_75873_e() + 1) {
               PathPoint nextPoint = path.func_75877_a(path.func_75873_e() + 1);
               PathPoint target = path.func_75877_a(path.func_75874_d() - 1);
               BlockPos pos = new BlockPos(nextPoint.field_75839_a, nextPoint.field_75837_b, nextPoint.field_75838_c);
               if (nextPoint.func_75829_a(target) < 3.0F) {
                  navi.func_75499_g();
               }

               if (this.field_70170_p.func_180495_p(pos.func_177982_a(0, 1, 0)).func_177230_c() == Blocks.field_150355_j) {
                  navi.func_75499_g();
               }

               if (this.field_70170_p.func_180495_p(pos).func_177230_c() == Blocks.field_150355_j) {
                  navi.func_75499_g();
               }

               if (this.field_70170_p.func_180495_p(pos.func_177982_a(0, -1, 0)).func_177230_c() == Blocks.field_150355_j) {
                  navi.func_75499_g();
               }
            }

         }
      }
   }

   void lookForFishingSpot() {
      int tries = 0;
      BlockPos selectedWater = null;
      int selectedWaterDeepness = 0;

      while(true) {
         ++tries;
         if (tries >= 50) {
            break;
         }

         BlockPos waterPos = this.findBlock(this.func_180425_c(), tries + 1, Blocks.field_150355_j, 60, 10, new HashSet(Arrays.asList(Biomes.field_76781_i, Biomes.field_76771_b, Biomes.field_150575_M, Biomes.field_76787_r, Biomes.field_150576_N, Biomes.field_76780_h, Biomes.field_150599_m)));
         if (waterPos == null) {
            break;
         }

         while(this.field_70170_p.func_180495_p(waterPos.func_177982_a(0, 1, 0)).func_177230_c() == Blocks.field_150355_j) {
            waterPos = waterPos.func_177982_a(0, 1, 0);
         }

         int deepness = 1;

         for(BlockPos searchPos = waterPos; this.field_70170_p.func_180495_p(searchPos.func_177982_a(0, -1, 0)).func_177230_c() == Blocks.field_150355_j; ++deepness) {
            searchPos = searchPos.func_177982_a(0, -1, 0);
         }

         if (selectedWater == null) {
            selectedWater = waterPos;
            selectedWaterDeepness = deepness;
         } else if (deepness > selectedWaterDeepness) {
            selectedWater = waterPos;
            selectedWaterDeepness = deepness;
            if (deepness >= 6) {
               break;
            }
         }
      }

      if (selectedWater != null) {
         if (this.chosenFishingSpot == null || this.chosenFishingSpotDeepness < selectedWaterDeepness) {
            this.chosenFishingSpot = selectedWater;
            this.chosenFishingSpotDeepness = selectedWaterDeepness;
         }

         if (this.chosenFishingSpot.equals(selectedWater)) {
            this.oldFishingSpotNotAvailableTick = 0;
         } else if (++this.oldFishingSpotNotAvailableTick > 20) {
            this.chosenFishingSpot = selectedWater;
            this.chosenFishingSpotDeepness = selectedWaterDeepness;
         }

      }
   }

   void manageTargetDistance() {
      Path path = this.func_70661_as().func_75505_d();
      if (path != null) {
         PathPoint targetPoint = path.func_75870_c();
         PathPoint currentPoint = new PathPoint(PenisMath.roundToInt(this.field_70165_t), PenisMath.roundToInt(this.field_70163_u), PenisMath.roundToInt(this.field_70161_v));
         if (targetPoint != null) {
            this.field_70180_af.func_187227_b(TARGET_DISTANCE, targetPoint.func_75829_a(currentPoint));
         }
      }
   }

   public void doAction(String actionName, UUID player) {
      super.doAction(actionName, player);
      if ("action.names.touchboobs".equals(actionName)) {
         this.setPlayer(player);
         this.prepareAction(true, true, player);
         this.changeDataParameterFromClient("animationFollowUp", "touch_boobs");
         HandlePlayerMovement.setActive(false);
      }

   }

   protected void thrust() {
      if (this.currentAction() == GirlEntity.Action.TOUCH_BOOBS_FAST || this.currentAction() == GirlEntity.Action.TOUCH_BOOBS_SLOW) {
         this.playerIsThrusting = true;
         if (this.currentAction() == GirlEntity.Action.TOUCH_BOOBS_FAST) {
            PacketHandler.INSTANCE.sendToServer(new ClearAnimationCache(this.girlId()));
         } else {
            this.setCurrentAction(GirlEntity.Action.TOUCH_BOOBS_FAST);
         }
      }

   }

   protected void cum() {
      if (this.currentAction() == GirlEntity.Action.TOUCH_BOOBS_SLOW || this.currentAction() == GirlEntity.Action.TOUCH_BOOBS_FAST) {
         this.playerIsCumming = true;
         this.setCurrentAction(GirlEntity.Action.TOUCH_BOOBS_CUM);
      }

   }

   protected void checkFollowUp() {
      String var1 = (String)this.field_70180_af.func_187225_a(ANIMATION_FOLLOW_UP);
      byte var2 = -1;
      switch(var1.hashCode()) {
      case 2014427283:
         if (var1.equals("touch_boobs")) {
            var2 = 0;
         }
      default:
         switch(var2) {
         case 0:
            if (this.currentAction() != GirlEntity.Action.PAYMENT) {
               this.setCurrentAction(GirlEntity.Action.PAYMENT);
               return;
            } else {
               this.setCurrentAction(GirlEntity.Action.TOUCH_BOOBS_INTRO);
            }
         default:
            if (this.field_70170_p.field_72995_K) {
               this.changeDataParameterFromClient("animationFollowUp", "");
            } else {
               this.field_70180_af.func_187227_b(ANIMATION_FOLLOW_UP, "");
            }

         }
      }
   }

   protected void func_184581_c(DamageSource source) {
      this.playSoundAroundHer(SoundsHandler.GIRLS_LUNA_OUU);
   }

   @Nullable
   protected SoundEvent func_184615_bR() {
      return SoundsHandler.GIRLS_LUNA_OUU[12];
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(1.0D);
   }

   protected float func_175134_bD() {
      return 0.5F;
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
            if (this.currentAction() != GirlEntity.Action.NULL) {
               this.createAnimation("animation.cat.null", true, event);
            } else {
               this.createAnimation("animation.cat.blink", true, event);
            }
            break;
         case 1:
            if (this.currentAction() != GirlEntity.Action.NULL) {
               this.createAnimation("animation.cat.null", true, event);
            } else if (this.func_184218_aH()) {
               this.createAnimation("animation.cat.sit", true, event);
            } else if (Math.abs(this.field_70169_q - this.field_70165_t) + Math.abs(this.field_70166_s - this.field_70161_v) > 0.0D) {
               if (this.field_70122_E && Math.abs(Math.abs(this.field_70167_r) - Math.abs(this.field_70163_u)) < 0.10000000149011612D) {
                  this.createAnimation((Float)this.field_70180_af.func_187225_a(TARGET_DISTANCE) < 3.0F ? "animation.cat.walk" : "animation.cat.run", true, event);
               } else {
                  this.createAnimation("animation.cat.fly", true, event);
               }

               this.field_70177_z = this.field_70759_as;
            } else {
               this.createAnimation("animation.cat.idle" + (this.playIdle2 ? "2" : ""), true, event);
            }
            break;
         case 2:
            switch(this.currentAction()) {
            case NULL:
               this.createAnimation("animation.cat.null", true, event);
               break;
            case ATTACK:
               this.createAnimation("animation.cat.attack" + this.nextAttack, false, event);
               break;
            case RIDE:
            case SIT:
               this.createAnimation("animation.cat.sit", true, event);
               break;
            case BOW:
               this.createAnimation("animation.cat.bowcharge", false, event);
               break;
            case THROW_PEARL:
               this.createAnimation("animation.cat.throwpearl", true, event);
               break;
            case DOWNED:
               this.createAnimation("animation.cat.downed", true, event);
               break;
            case FISHING_START:
               this.createAnimation("animation.cat.start_fishing", false, event);
               break;
            case FISHING_IDLE:
               this.createAnimation("animation.cat.idle_fishing", true, event);
               break;
            case FISHING_EAT:
               this.createAnimation("animation.cat.eat_fishing", false, event);
               break;
            case FISHING_THROW_AWAY:
               this.createAnimation("animation.cat.throw_away", false, event);
               break;
            case PAYMENT:
               this.createAnimation("animation.cat.payment", false, event);
               break;
            case TOUCH_BOOBS_INTRO:
               this.createAnimation("animation.cat.touch_boobs_intro", false, event);
               break;
            case TOUCH_BOOBS_SLOW:
               this.createAnimation("animation.cat.touch_boobs_slow" + (this.moanBoobsSlow ? "1" : ""), true, event);
               break;
            case TOUCH_BOOBS_FAST:
               this.createAnimation("animation.cat.touch_boobs_fast", true, event);
               break;
            case TOUCH_BOOBS_CUM:
               this.createAnimation("animation.cat.touch_boobs_cum", false, event);
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
         case -1961942550:
            if (var2.equals("attackDone")) {
               var3 = 1;
            }
            break;
         case -1540860248:
            if (var2.equals("paymentDone")) {
               var3 = 17;
            }
            break;
         case -1540620298:
            if (var2.equals("paymentMSG1")) {
               var3 = 13;
            }
            break;
         case -1540620297:
            if (var2.equals("paymentMSG2")) {
               var3 = 14;
            }
            break;
         case -1540620296:
            if (var2.equals("paymentMSG3")) {
               var3 = 15;
            }
            break;
         case -1540620295:
            if (var2.equals("paymentMSG4")) {
               var3 = 16;
            }
            break;
         case -1380923296:
            if (var2.equals("breath")) {
               var3 = 18;
            }
            break;
         case -1310305744:
            if (var2.equals("eatPay")) {
               var3 = 8;
            }
            break;
         case -1265725098:
            if (var2.equals("addCumFast")) {
               var3 = 34;
            }
            break;
         case -1265327365:
            if (var2.equals("addCumSlow")) {
               var3 = 33;
            }
            break;
         case -922762033:
            if (var2.equals("start_fishingDone")) {
               var3 = 5;
            }
            break;
         case -676816985:
            if (var2.equals("attackSound")) {
               var3 = 0;
            }
            break;
         case -548534449:
            if (var2.equals("touch_boobs_slowDone")) {
               var3 = 32;
            }
            break;
         case -334109968:
            if (var2.equals("horninya")) {
               var3 = 29;
            }
            break;
         case -274246489:
            if (var2.equals("throw_away")) {
               var3 = 11;
            }
            break;
         case -176763432:
            if (var2.equals("rod_breath")) {
               var3 = 19;
            }
            break;
         case -146438396:
            if (var2.equals("moanOrNya")) {
               var3 = 36;
            }
            break;
         case -108683087:
            if (var2.equals("touch_boobs_cumDone")) {
               var3 = 38;
            }
            break;
         case -108443137:
            if (var2.equals("touch_boobs_cumMSG1")) {
               var3 = 39;
            }
            break;
         case -108443136:
            if (var2.equals("touch_boobs_cumMSG2")) {
               var3 = 40;
            }
            break;
         case -108443135:
            if (var2.equals("touch_boobs_cumMSG3")) {
               var3 = 41;
            }
            break;
         case 100184:
            if (var2.equals("eat")) {
               var3 = 7;
            }
            break;
         case 103675:
            if (var2.equals("huh")) {
               var3 = 22;
            }
            break;
         case 3035601:
            if (var2.equals("burp")) {
               var3 = 9;
            }
            break;
         case 3198650:
            if (var2.equals("hehe")) {
               var3 = 24;
            }
            break;
         case 3206589:
            if (var2.equals("hmph")) {
               var3 = 23;
            }
            break;
         case 3273774:
            if (var2.equals("jump")) {
               var3 = 28;
            }
            break;
         case 3357007:
            if (var2.equals("moan")) {
               var3 = 30;
            }
            break;
         case 54906230:
            if (var2.equals("idleDone")) {
               var3 = 2;
            }
            break;
         case 106540102:
            if (var2.equals("pearl")) {
               var3 = 4;
            }
            break;
         case 110550847:
            if (var2.equals("touch")) {
               var3 = 27;
            }
            break;
         case 298467170:
            if (var2.equals("touch_boobs_introDone")) {
               var3 = 31;
            }
            break;
         case 350188588:
            if (var2.equals("eatingDone")) {
               var3 = 10;
            }
            break;
         case 403702091:
            if (var2.equals("blackScreen")) {
               var3 = 37;
            }
            break;
         case 620933088:
            if (var2.equals("cutenya3")) {
               var3 = 21;
            }
            break;
         case 695019737:
            if (var2.equals("happyOh")) {
               var3 = 20;
            }
            break;
         case 816936963:
            if (var2.equals("touch_boobsMSG1")) {
               var3 = 26;
            }
            break;
         case 968155646:
            if (var2.equals("fastDone")) {
               var3 = 35;
            }
            break;
         case 1193768393:
            if (var2.equals("renderItem")) {
               var3 = 12;
            }
            break;
         case 1672277927:
            if (var2.equals("rod_shoot")) {
               var3 = 6;
            }
            break;
         case 1684190080:
            if (var2.equals("idle2Done")) {
               var3 = 3;
            }
            break;
         case 2094529267:
            if (var2.equals("singing")) {
               var3 = 25;
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
            this.playIdle2 = this.func_70681_au().nextInt(10) == 0;
            break;
         case 3:
            this.playIdle2 = false;
            break;
         case 4:
            PacketHandler.INSTANCE.sendToServer(new SendCompanionHome(this.girlId()));
            break;
         case 5:
            if (this.isClosestPlayer()) {
               this.setCurrentAction(GirlEntity.Action.FISHING_IDLE);
            }
            break;
         case 6:
            if (this.isClosestPlayer()) {
               PacketHandler.INSTANCE.sendToServer(new CatActivateFishing(this.girlId()));
            }
            break;
         case 7:
            this.playSoundAroundHer(SoundEvents.field_187537_bA, 0.5F + 0.5F * (float)this.field_70146_Z.nextInt(2), (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.0F);
            this.fishSizePercentage -= 0.33333334F;
            break;
         case 8:
            this.playSoundAroundHer(SoundEvents.field_187537_bA, 0.5F + 0.5F * (float)this.field_70146_Z.nextInt(2), (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.0F);
            this.itemRendererSize -= 0.33333334F;
            break;
         case 9:
            this.playSoundAroundHer(SoundEvents.field_187739_dZ, 0.5F, this.field_70146_Z.nextFloat() * 0.1F + 0.9F);
            break;
         case 10:
            if (this.isClosestPlayer()) {
               PacketHandler.INSTANCE.sendToServer(new CatEatingDone(this.girlId()));
               this.setCurrentAction(GirlEntity.Action.NULL);
            }

            this.fishSizePercentage = 1.0F;
            this.throwBackPercentage = 0.0F;
            break;
         case 11:
            if (this.isClosestPlayer()) {
               PacketHandler.INSTANCE.sendToServer(new CatThrowAwayItem(this.girlId()));
            }

            this.fishSizePercentage = 1.0F;
            this.throwBackPercentage = 0.0F;
            break;
         case 12:
            this.throwBackPercentage = 1.0F;
            break;
         case 13:
            this.sayAroundAsPlayer(this.playerSheHasSexWith(), "Here, I know u like fish and yea.. these are for you");
            this.playSoundAroundHer(SoundsHandler.MISC_PLOB[0]);
            break;
         case 14:
            this.sayAround("huh~?");
            this.playSoundAroundHer(SoundsHandler.GIRLS_LUNA_HUH);
            break;
         case 15:
            this.sayAround("nyyyaaaa~ :D");
            int[] set = new int[]{1, 7, 10, 11};
            int randomOfSet = set[this.func_70681_au().nextInt(set.length)];
            this.playSoundAroundHer(SoundsHandler.GIRLS_LUNA_CUTENYA[randomOfSet]);
            break;
         case 16:
            this.sayAround("tankuuuu owowowo");
            this.playSoundAroundHer(SoundsHandler.GIRLS_LUNA_OWO);
            break;
         case 17:
            if (this.isClosestPlayer()) {
               this.checkFollowUp();
            }

            this.itemRendererSize = 1.0F;
            break;
         case 18:
         case 19:
            this.playSoundAroundHer(SoundsHandler.GIRLS_LUNA_LIGHTBREATHING);
            break;
         case 20:
            this.playSoundAroundHer(SoundsHandler.GIRLS_LUNA_HAPPYOH);
            break;
         case 21:
            this.playSoundAroundHer(SoundsHandler.GIRLS_LUNA_CUTENYA[3]);
            break;
         case 22:
            this.playSoundAroundHer(SoundsHandler.GIRLS_LUNA_HUH);
            break;
         case 23:
            this.playSoundAroundHer(SoundsHandler.GIRLS_LUNA_HMPH);
            break;
         case 24:
            this.playSoundAroundHer(SoundsHandler.GIRLS_LUNA_GIGGLE);
            break;
         case 25:
            this.playSoundAroundHer(SoundsHandler.GIRLS_LUNA_SINGING);
            break;
         case 26:
            this.sayAround("comon~ touch me hihi~");
            this.playSoundAroundHer(SoundsHandler.GIRLS_LUNA_GIGGLE);
            break;
         case 27:
            this.playSoundAroundHer(SoundsHandler.MISC_TOUCH);
            break;
         case 28:
            this.playSoundAroundHer(SoundsHandler.MISC_JUMP[0], 0.2F);
            break;
         case 29:
            this.playSoundAroundHer(SoundsHandler.GIRLS_LUNA_HORNINYA);
            break;
         case 30:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_LUNA_MOAN));
            break;
         case 31:
            this.setCurrentAction(GirlEntity.Action.TOUCH_BOOBS_SLOW);
            if (this.belongsToPlayer()) {
               SexUI.resetCumPercentage();
               SexUI.shouldBeRendered = true;
               HandlePlayerMovement.setActive(false);
            }
            break;
         case 32:
            if (this.moanBoobsSlow) {
               this.moanBoobsSlow = false;
            } else {
               this.moanBoobsSlow = Math.random() < 0.5D;
            }
            break;
         case 33:
            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.019999999552965164D);
            }
            break;
         case 34:
            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.03999999910593033D);
            }
            break;
         case 35:
            if (this.belongsToPlayer()) {
               this.playerIsThrusting = HandlePlayerMovement.isThrusting;
               if (!this.playerIsThrusting) {
                  this.setCurrentAction(GirlEntity.Action.TOUCH_BOOBS_SLOW);
               }
            }
            break;
         case 36:
            if (Math.random() > 0.5D) {
               this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_LUNA_MOAN));
            } else {
               this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.GIRLS_LUNA_HORNINYA));
            }
            break;
         case 37:
            if (this.belongsToPlayer()) {
               BlackScreenUI.activate();
            }
            break;
         case 38:
            if (this.belongsToPlayer()) {
               SexUI.resetCumPercentage();
               this.resetGirl();
            }
            break;
         case 39:
            this.playSoundAroundHer(SoundsHandler.GIRLS_LUNA_HORNINYA[3]);
            break;
         case 40:
            this.playSoundAroundHer(SoundsHandler.GIRLS_LUNA_HORNINYA[9]);
            break;
         case 41:
            this.playSoundAroundHer(SoundsHandler.GIRLS_LUNA_HORNINYA[1]);
         }

      };
      this.movementController.transitionLengthTicks = 10.0D;
      this.actionController.registerSoundListener(soundListener);
      data.addAnimationController(this.actionController);
      data.addAnimationController(this.movementController);
      data.addAnimationController(this.eyesController);
   }

   static {
      TARGET_DISTANCE = EntityDataManager.func_187226_a(CatEntity.class, DataSerializers.field_187193_c).func_187156_b().func_187161_a(54);
      FISHING_ROD = EntityDataManager.func_187226_a(CatEntity.class, DataSerializers.field_187196_f).func_187156_b().func_187161_a(53);
      IS_FISHING = EntityDataManager.func_187226_a(CatEntity.class, DataSerializers.field_187198_h).func_187156_b().func_187161_a(52);
      CAUGHT_ITEM = EntityDataManager.func_187226_a(CatEntity.class, DataSerializers.field_187196_f).func_187156_b().func_187161_a(51);
   }

   public static class EventHandler {
      @SubscribeEvent
      public void makeCreepersBeAfraid(EntityJoinWorldEvent event) {
         Entity entity = event.getEntity();
         if (entity instanceof EntityCreeper) {
            EntityCreeper creeper = (EntityCreeper)entity;
            creeper.field_70714_bg.func_75776_a(3, new EntityAIAvoidEntity(creeper, CatEntity.class, 6.0F, 1.0D, 1.2D));
         }

      }
   }
}

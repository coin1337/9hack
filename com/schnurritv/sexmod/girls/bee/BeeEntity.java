package com.schnurritv.sexmod.girls.bee;

import com.daripher.sexmod.client.util.FakeWorld;
import com.schnurritv.sexmod.Packets.ClearAnimationCache;
import com.schnurritv.sexmod.Packets.SendCompanionHome;
import com.schnurritv.sexmod.Packets.SetPlayerMovement;
import com.schnurritv.sexmod.companion.fighter.LookAtNearbyEntity;
import com.schnurritv.sexmod.companion.supporter.SupporterCompanion;
import com.schnurritv.sexmod.events.HandlePlayerMovement;
import com.schnurritv.sexmod.gender_change.hornypotion.HornyPotion;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.base.Supporter;
import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirl;
import com.schnurritv.sexmod.gui.Sex.BlackScreenUI;
import com.schnurritv.sexmod.gui.Sex.SexUI;
import com.schnurritv.sexmod.gui.menu.SupporterUI;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import com.schnurritv.sexmod.util.Handlers.SoundsHandler;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWaterFlying;
import net.minecraft.entity.ai.EntityFlyHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public class BeeEntity extends Supporter {
   public float hornyLevel = 3200.0F;
   int particleTicks = 0;
   static final float HORNY_SEX_LEVEL = 4800.0F;
   static final float HORNY_RANGE = 10.0F;
   public static final DataParameter<Boolean> IS_TAMED;

   public BeeEntity(World worldIn) {
      super(worldIn);
      this.field_70765_h = new EntityFlyHelper(this);
      this.func_70105_a(0.3F, 1.5F);
   }

   protected String getGirlName() {
      return "Bee";
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(IS_TAMED, true);
   }

   protected PathNavigate func_175447_b(World worldIn) {
      PathNavigateFlying pathnavigateflying = new PathNavigateFlying(this, worldIn);
      pathnavigateflying.func_192879_a(false);
      pathnavigateflying.func_192877_c(true);
      pathnavigateflying.func_192878_b(true);
      this.field_70699_by = pathnavigateflying;
      return pathnavigateflying;
   }

   protected void func_110147_ax() {
      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111267_a);
      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111266_c);
      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111263_d);
      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_188791_g);
      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_189429_h);
      this.func_110140_aT().func_111150_b(SWIM_SPEED);
      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111265_b).func_111128_a(16.0D);
      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_193334_e);
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(12.0D);
      this.func_110148_a(SharedMonsterAttributes.field_193334_e).func_111128_a(0.4000000059604645D);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.20000000298023224D);
   }

   protected void func_184651_r() {
      this.aiLookAtPlayer = new LookAtNearbyEntity(this, EntityPlayer.class, 3.0F, 1.0F);
      this.field_70714_bg.func_75776_a(0, new SupporterCompanion(this));
      this.field_70714_bg.func_75776_a(1, new EntityAIPanic(this, 1.25D));
      this.field_70714_bg.func_75776_a(1, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(2, this.aiLookAtPlayer);
      this.field_70714_bg.func_75776_a(3, new EntityAIWanderAvoidWaterFlying(this, 1.0D));
   }

   public void func_70619_bc() {
      super.func_70619_bc();
      if (this.func_70644_a(HornyPotion.HORNY_EFFECT) && this.hornyLevel < 4800.0F && this.playerSheHasSexWith() == null) {
         this.func_184589_d(HornyPotion.HORNY_EFFECT);
         this.hornyLevel = 6.9420184E7F;
      }

      if (this.playerSheHasSexWith() == null && (!this.hasMaster() || this.hornyLevel == 6.9420184E7F) && ++this.hornyLevel > 4800.0F) {
         EntityPlayer closestPlayer = this.field_70170_p.func_72890_a(this, 10.0D);
         if (closestPlayer != null) {
            if (closestPlayer.func_70032_d(this) < 1.0F) {
               if (!isHavingSex(closestPlayer) && !PlayerGirl.isPlayerGirl(closestPlayer)) {
                  this.hornyLevel = 0.0F;
                  this.setPlayer(closestPlayer.getPersistentID());
                  this.field_70180_af.func_187227_b(SHOULD_BE_AT_TARGET, true);
                  this.setTargetPos(this.getInFrontOfPlayer());
                  this.setTargetYaw(closestPlayer.field_70177_z - 180.0F);
                  this.field_70699_by.func_75499_g();
                  PacketHandler.INSTANCE.sendTo(new SetPlayerMovement(false), (EntityPlayerMP)closestPlayer);
                  this.setCurrentAction(GirlEntity.Action.CITIZEN_START);
                  Vec3d forward = this.getInFrontOfPlayer(0.2D);
                  closestPlayer.func_70634_a(forward.field_72450_a, forward.field_72448_b, forward.field_72449_c);
               }
            } else {
               this.field_70699_by.func_75499_g();
               this.field_70699_by.func_75497_a(closestPlayer, 1.0D);
            }
         }
      }

      if (this.currentAction().equals(GirlEntity.Action.CITIZEN_CUM)) {
         this.particleTicks = Math.max(1, this.particleTicks);
      }

      if (this.particleTicks != 0) {
         ++this.particleTicks;
         Iterator var3;
         EntityPlayer player;
         if ((Boolean)this.field_70180_af.func_187225_a(IS_TAMED)) {
            if (this.particleTicks < 40) {
               var3 = this.field_70170_p.field_73010_i.iterator();

               while(var3.hasNext()) {
                  player = (EntityPlayer)var3.next();
                  if (player.func_70032_d(this) < 15.0F) {
                     ((EntityPlayerMP)player).field_71135_a.func_147359_a(new SPacketParticles(EnumParticleTypes.HEART, true, (float)this.field_70165_t, (float)this.field_70163_u + 0.3F, (float)this.field_70161_v, 0.2F, 0.3F, 0.2F, 0.25F, 3, new int[0]));
                  }
               }
            } else {
               this.particleTicks = 0;
            }
         } else if (this.particleTicks < 200) {
            var3 = this.field_70170_p.field_73010_i.iterator();

            while(var3.hasNext()) {
               player = (EntityPlayer)var3.next();
               if (player.func_70032_d(this) < 15.0F) {
                  ((EntityPlayerMP)player).field_71135_a.func_147359_a(new SPacketParticles(EnumParticleTypes.SPELL, true, (float)this.field_70165_t, (float)this.field_70163_u + 0.3F, (float)this.field_70161_v, 0.2F, 0.3F, 0.2F, 0.25F, 3, new int[0]));
               }
            }
         } else if (this.particleTicks == 200) {
            this.field_70180_af.func_187227_b(IS_TAMED, this.func_70681_au().nextBoolean());
         } else if (this.particleTicks < 250) {
            var3 = this.field_70170_p.field_73010_i.iterator();

            while(var3.hasNext()) {
               player = (EntityPlayer)var3.next();
               if (player.func_70032_d(this) < 15.0F) {
                  ((EntityPlayerMP)player).field_71135_a.func_147359_a(new SPacketParticles((Boolean)this.field_70180_af.func_187225_a(IS_TAMED) ? EnumParticleTypes.HEART : EnumParticleTypes.VILLAGER_ANGRY, true, (float)this.field_70165_t, (float)this.field_70163_u + 0.3F, (float)this.field_70161_v, 0.2F, 0.3F, 0.2F, 0.25F, 3, new int[0]));
               }
            }
         } else {
            this.particleTicks = 0;
         }

         var3 = this.field_70170_p.field_73010_i.iterator();

         while(var3.hasNext()) {
            player = (EntityPlayer)var3.next();
            if (player.func_70032_d(this) < 15.0F) {
               ((EntityPlayerMP)player).field_71135_a.func_147359_a(new SPacketParticles(EnumParticleTypes.SPELL, true, (float)this.field_70165_t, (float)this.field_70163_u + 0.3F, (float)this.field_70161_v, 0.2F, 0.3F, 0.2F, 0.25F, 10, new int[0]));
            }
         }

      }
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.hornyLevel < 4800.0F && !this.field_70122_E && this.field_70181_x < 0.0D) {
         this.field_70181_x *= 0.4D;
      }

   }

   public void func_180430_e(float distance, float damageMultiplier) {
   }

   protected boolean func_184645_a(EntityPlayer player, EnumHand hand) {
      if ((Boolean)this.field_70180_af.func_187225_a(IS_TAMED) && !(Boolean)this.field_70180_af.func_187225_a(HAS_CHEST) && player.func_184586_b(hand).func_77973_b() == Item.func_150898_a(Blocks.field_150486_ae)) {
         this.field_70180_af.func_187227_b(HAS_CHEST, true);
         player.func_184586_b(hand).func_190918_g(1);
         return super.func_184645_a(player, hand);
      } else {
         if (this.field_70170_p.field_72995_K && (Boolean)this.field_70180_af.func_187225_a(IS_TAMED)) {
            this.openSupporterUI(player);
         }

         return super.func_184645_a(player, hand);
      }
   }

   @SideOnly(Side.CLIENT)
   void openSupporterUI(EntityPlayer player) {
      Minecraft.func_71410_x().func_147108_a(new SupporterUI(this, player));
   }

   public boolean canCloseUiWithoutHavingChosen() {
      return false;
   }

   public boolean openMenu(EntityPlayer player) {
      return false;
   }

   public void doAction(String actionName, UUID player) {
   }

   protected void thrust() {
      if (this.currentAction() == GirlEntity.Action.CITIZEN_FAST || this.currentAction() == GirlEntity.Action.CITIZEN_SLOW) {
         this.playerIsThrusting = true;
         if (this.currentAction() == GirlEntity.Action.CITIZEN_FAST) {
            PacketHandler.INSTANCE.sendToServer(new ClearAnimationCache(this.girlId()));
         } else {
            this.setCurrentAction(GirlEntity.Action.CITIZEN_FAST);
         }
      }

   }

   protected void cum() {
      if (this.currentAction() == GirlEntity.Action.CITIZEN_FAST || this.currentAction() == GirlEntity.Action.CITIZEN_SLOW) {
         this.playerIsCumming = true;
         this.setCurrentAction(GirlEntity.Action.CITIZEN_CUM);
      }

   }

   protected void checkFollowUp() {
   }

   public void func_70014_b(NBTTagCompound compound) {
      super.func_70014_b(compound);
      compound.func_74757_a("isTamed", (Boolean)this.field_70180_af.func_187225_a(IS_TAMED));
      compound.func_74757_a("hasChest", (Boolean)this.field_70180_af.func_187225_a(HAS_CHEST));
      compound.func_74782_a("inventory", this.inventory.serializeNBT());
   }

   public void func_70020_e(NBTTagCompound compound) {
      super.func_70020_e(compound);
      if (compound.func_74764_b("isTamed")) {
         this.field_70180_af.func_187227_b(IS_TAMED, compound.func_74767_n("isTamed"));
      }

      this.field_70180_af.func_187227_b(HAS_CHEST, compound.func_74767_n("hasChest"));
      this.inventory.deserializeNBT(compound.func_74775_l("inventory"));
   }

   @SideOnly(Side.CLIENT)
   protected <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
      if (this.field_70170_p instanceof FakeWorld) {
         return PlayState.STOP;
      } else {
         String var2 = event.getController().getName();
         byte var3 = -1;
         switch(var2.hashCode()) {
         case -1422950858:
            if (var2.equals("action")) {
               var3 = 1;
            }
            break;
         case -103677777:
            if (var2.equals("movement")) {
               var3 = 0;
            }
         }

         switch(var3) {
         case 0:
            if (this.currentAction() != GirlEntity.Action.NULL) {
               this.createAnimation("animation.bee.null", true, event);
            } else {
               this.createAnimation("animation.bee." + ((Boolean)this.field_70180_af.func_187225_a(HAS_CHEST) ? "idle_has_chest" : "idle"), true, event);
            }
            break;
         case 1:
            switch(this.currentAction()) {
            case CITIZEN_START:
               this.createAnimation("animation.bee.sex_start", false, event);
               break;
            case CITIZEN_SLOW:
               this.createAnimation("animation.bee.sex_slow", true, event);
               break;
            case CITIZEN_FAST:
               this.createAnimation("animation.bee.sex_fast", true, event);
               break;
            case CITIZEN_CUM:
               this.createAnimation("animation.bee.sex_cum", false, event);
               break;
            case THROW_PEARL:
               this.createAnimation("animation.bee.throw_pearl", true, event);
            }
         }

         return PlayState.CONTINUE;
      }
   }

   public void registerControllers(AnimationData data) {
      AnimationController.ISoundListener soundListener = (event) -> {
         String var2 = event.sound;
         byte var3 = -1;
         switch(var2.hashCode()) {
         case -1825955452:
            if (var2.equals("sex_cumDone")) {
               var3 = 9;
            }
            break;
         case -1825715502:
            if (var2.equals("sex_cumMSG1")) {
               var3 = 7;
            }
            break;
         case -1643193842:
            if (var2.equals("sex_fastReady")) {
               var3 = 4;
            }
            break;
         case -191960649:
            if (var2.equals("sex_fastDone")) {
               var3 = 5;
            }
            break;
         case -191720699:
            if (var2.equals("sex_fastMSG1")) {
               var3 = 2;
            }
            break;
         case 106540102:
            if (var2.equals("pearl")) {
               var3 = 0;
            }
            break;
         case 307064358:
            if (var2.equals("resetCumPercentage")) {
               var3 = 1;
            }
            break;
         case 561334891:
            if (var2.equals("sex_startDone")) {
               var3 = 6;
            }
            break;
         case 561574841:
            if (var2.equals("sex_startMSG1")) {
               var3 = 3;
            }
            break;
         case 1319834923:
            if (var2.equals("blackscreen")) {
               var3 = 8;
            }
         }

         switch(var3) {
         case 0:
            if (this.isClosestPlayer() && this.currentAction() == GirlEntity.Action.THROW_PEARL) {
               PacketHandler.INSTANCE.sendToServer(new SendCompanionHome(this.girlId()));
            }
            break;
         case 1:
            if (this.belongsToPlayer()) {
               SexUI.resetCumPercentage();
            }
            break;
         case 2:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING));
            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.03999999910593033D);
            }
            break;
         case 3:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING));
            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.019999999552965164D);
            }
            break;
         case 4:
            this.playerIsThrusting = false;
            break;
         case 5:
            this.playerIsThrusting = HandlePlayerMovement.isThrusting;
            if (this.playerIsThrusting) {
               break;
            }
         case 6:
            this.setCurrentAction(GirlEntity.Action.CITIZEN_SLOW);
            if (this.belongsToPlayer()) {
               SexUI.shouldBeRendered = true;
            }
            break;
         case 7:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_CUMINFLATION), 2.0F);
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING));
            break;
         case 8:
            if (this.belongsToPlayer()) {
               BlackScreenUI.activate();
            }
            break;
         case 9:
            if (this.belongsToPlayer()) {
               SexUI.resetCumPercentage();
               this.resetGirl();
            }
         }

      };
      this.actionController.registerSoundListener(soundListener);
      data.addAnimationController(this.actionController);
      data.addAnimationController(this.movementController);
   }

   static {
      IS_TAMED = EntityDataManager.func_187226_a(GirlEntity.class, DataSerializers.field_187198_h).func_187156_b().func_187161_a(76);
   }
}

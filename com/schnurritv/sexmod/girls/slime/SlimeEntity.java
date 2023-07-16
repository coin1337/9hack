package com.schnurritv.sexmod.girls.slime;

import com.daripher.sexmod.client.util.FakeWorld;
import com.schnurritv.sexmod.Packets.ClearAnimationCache;
import com.schnurritv.sexmod.Packets.JennyAwaitPlayerDoggy;
import com.schnurritv.sexmod.Packets.SetPlayerMovement;
import com.schnurritv.sexmod.Packets.SetSlimePregnant;
import com.schnurritv.sexmod.gender_change.hornypotion.HornyPotion;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.slime.friendlySlime.FriendlySlimeEntity;
import com.schnurritv.sexmod.gui.Sex.BlackScreenUI;
import com.schnurritv.sexmod.gui.Sex.SexUI;
import com.schnurritv.sexmod.util.Reference;
import com.schnurritv.sexmod.util.Handlers.LootTableHandler;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import com.schnurritv.sexmod.util.Handlers.SoundsHandler;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public class SlimeEntity extends GirlEntity {
   public double prevRenderPos = -420.0D;
   public int playerChasingCounter = 0;
   public static double hornyLevelIncreasePercentage = 0.2D;
   public boolean awaitPlayer = false;
   public boolean isPregnant = false;
   public int ticksUntilBirth = -1;
   public SlimeEntity.AISlimeFloat floatTask;
   public SlimeEntity.AISlimeHop hopTask;
   public static DataParameter<String> currentJumpStage;
   public static DataParameter<Integer> hornyLevel;
   int ticksUntilJump = -1;
   long antiSoundStackTick = 0L;
   int flip = 0;

   public SlimeEntity.JumpStage currentJumpStage() {
      return SlimeEntity.JumpStage.valueOf((String)this.field_70180_af.func_187225_a(currentJumpStage));
   }

   public void setCurrentJumpStage(SlimeEntity.JumpStage stage) {
      this.field_70180_af.func_187227_b(currentJumpStage, stage.toString());
   }

   public int getHornyLevel() {
      return (Integer)this.field_70180_af.func_187225_a(hornyLevel);
   }

   public void setHornyLevel(int level) {
      this.field_70180_af.func_187227_b(hornyLevel, level);
   }

   public SlimeEntity(World worldIn) {
      super(worldIn);
      this.func_70105_a(0.6F, 1.85F);
      this.field_70765_h = new SlimeEntity.SlimeMoveHelper(this);
   }

   protected String getGirlName() {
      return "Slime";
   }

   public void func_70014_b(NBTTagCompound compound) {
      super.func_70014_b(compound);
      compound.func_74768_a("hornyLevel", (Integer)this.field_70180_af.func_187225_a(hornyLevel));
   }

   public void func_70037_a(NBTTagCompound compound) {
      super.func_70037_a(compound);
      this.field_70180_af.func_187227_b(hornyLevel, compound.func_74762_e("hornyLevel"));
      if ((Integer)this.field_70180_af.func_187225_a(hornyLevel) > 0) {
         this.field_70180_af.func_187227_b(CURRENT_MODEL, 0);
      }

   }

   protected void func_184651_r() {
      Reference.server = this.func_184102_h();
      this.floatTask = new SlimeEntity.AISlimeFloat(this);
      this.hopTask = new SlimeEntity.AISlimeHop(this);
      this.field_70714_bg.func_75776_a(1, this.floatTask);
      this.field_70714_bg.func_75776_a(2, this.hopTask);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      if (!this.field_70170_p.field_72995_K || !(this.field_70170_p instanceof FakeWorld)) {
         this.func_184212_Q().func_187214_a(hornyLevel, 0);
         this.func_184212_Q().func_187214_a(currentJumpStage, SlimeEntity.JumpStage.NONE.toString());
      }

      this.func_189654_d(false);
      this.field_70145_X = false;
   }

   public float func_70047_e() {
      return 1.54F;
   }

   public void doAction(String animationName, UUID player) {
   }

   public boolean openMenu(EntityPlayer player) {
      return false;
   }

   public boolean canCloseUiWithoutHavingChosen() {
      return true;
   }

   protected boolean func_70692_ba() {
      return false;
   }

   public void func_70619_bc() {
      super.func_70619_bc();
      if ((Boolean)this.field_70180_af.func_187225_a(SHOULD_BE_AT_TARGET)) {
         this.func_70034_d(this.targetYaw());
         this.func_70080_a(this.targetPos().field_72450_a, this.targetPos().field_72448_b, this.targetPos().field_72449_c, this.targetYaw(), 0.0F);
         this.func_70101_b(this.targetYaw(), this.field_70125_A);
      }

      EntityPlayer closestPlayer = this.field_70170_p.func_72890_a(this, 5.0D);
      if (this.awaitPlayer && closestPlayer != null && this.func_174791_d().func_72438_d(closestPlayer.func_174791_d()) < 0.75D) {
         this.setCurrentJumpStage(SlimeEntity.JumpStage.NONE);
         this.field_70180_af.func_187227_b(hornyLevel, 0);
         this.awaitPlayer = false;
         EntityPlayerMP playerMP = this.func_184102_h().func_184103_al().func_177451_a(closestPlayer.getPersistentID());
         this.field_70180_af.func_187227_b(PLAYER_SHE_HAS_SEX_WITH, playerMP.getPersistentID().toString());
         playerMP.func_70634_a(this.func_174791_d().field_72450_a, this.func_174791_d().field_72448_b, this.func_174791_d().field_72449_c);
         this.TurnPlayerIntoCamera(playerMP, false);
         playerMP.func_191958_b(0.0F, 0.0F, 0.0F, 0.0F);
         this.func_191958_b(0.0F, 0.0F, 0.0F, 5.0F);
         this.playerCamPos = null;
         this.setTargetPos(this.func_174791_d());
         this.setTargetYaw(this.field_70177_z);
         this.playerYaw = this.targetYaw();
         this.field_70180_af.func_187227_b(SHOULD_BE_AT_TARGET, true);
         this.setCurrentAction(GirlEntity.Action.DOGGYSTART);
         PacketHandler.INSTANCE.sendTo(new SetPlayerMovement(false), playerMP);
         this.moveCamera(0.0D, 0.0D, 0.4D, 0.0F, 60.0F);
      }

      if ((Integer)this.field_70180_af.func_187225_a(hornyLevel) == 2 && closestPlayer != null && this.field_70122_E && this.func_174791_d().func_72438_d(closestPlayer.func_174791_d()) < 0.75D && !isHavingSex(closestPlayer)) {
         this.setCurrentJumpStage(SlimeEntity.JumpStage.NONE);
         this.field_70180_af.func_187227_b(hornyLevel, 0);
         this.field_70180_af.func_187227_b(PLAYER_SHE_HAS_SEX_WITH, closestPlayer.getPersistentID().toString());
         this.TurnPlayerIntoCamera((EntityPlayerMP)closestPlayer, false);
         closestPlayer.func_70634_a(this.func_174791_d().field_72450_a, this.func_174791_d().field_72448_b, this.func_174791_d().field_72449_c);
         this.func_189654_d(true);
         this.field_70145_X = true;
         this.playerCamPos = null;
         this.setTargetPos(this.func_174791_d());
         this.setTargetYaw(this.field_70177_z);
         this.field_70180_af.func_187227_b(SHOULD_BE_AT_TARGET, true);
         this.playerYaw = closestPlayer.field_70177_z;
         this.setCurrentAction(GirlEntity.Action.SUCKBLOWJOB);
         super.prepareAction(false, true, closestPlayer.getPersistentID());
         PacketHandler.INSTANCE.sendTo(new SetPlayerMovement(false), (EntityPlayerMP)closestPlayer);
      }

      --this.ticksUntilJump;
      if (this.ticksUntilJump <= -1) {
         this.ticksUntilJump = -1;
      } else if (this.ticksUntilJump == 0 && !this.isPregnant) {
         Vec3d forward = Vec3d.func_189986_a(this.field_70125_A, this.field_70177_z);
         this.field_70181_x = 0.75D;
         this.field_70159_w = 0.44999998807907104D * forward.field_72450_a;
         this.field_70179_y = 0.44999998807907104D * forward.field_72449_c;
      }

      --this.ticksUntilBirth;
      if (this.ticksUntilBirth <= -1) {
         this.ticksUntilBirth = -1;
      } else if (this.ticksUntilBirth == 0) {
         this.isPregnant = false;
         PacketHandler.INSTANCE.sendToAllAround(new SetSlimePregnant(this.girlId(), false), this.getTargetPoint());
         FriendlySlimeEntity slime = new FriendlySlimeEntity(this.field_70170_p);
         slime.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
         this.field_70170_p.func_72838_d(slime);
         this.playSoundAroundHer(SoundsHandler.MISC_PLOB[0]);
      }

      --this.antiSoundStackTick;
      if (this.func_70644_a(HornyPotion.HORNY_EFFECT)) {
         this.func_184589_d(HornyPotion.HORNY_EFFECT);
         if (this.getHornyLevel() == 0) {
            this.setCurrentAction(GirlEntity.Action.UNDRESS);
         }

         this.setHornyLevel(2);
      }

   }

   public void onReset() {
      this.floatTask = new SlimeEntity.AISlimeFloat(this);
      this.hopTask = new SlimeEntity.AISlimeHop(this);
      this.field_70714_bg.func_75776_a(1, this.floatTask);
      this.field_70714_bg.func_75776_a(2, this.hopTask);
      this.setCurrentAction(GirlEntity.Action.DRESS);
      this.setHornyLevel(0);
      this.func_184212_Q().func_187227_b(GirlEntity.CURRENT_MODEL, 1);
   }

   protected ResourceLocation func_184647_J() {
      return LootTableHandler.SLIME;
   }

   public static double distanceToGround(SlimeEntity slime) {
      if (slime.field_70170_p instanceof FakeWorld) {
         return 0.0D;
      } else {
         float f = 1.0F;
         float f1 = 90.0F;
         float f2 = slime.field_70126_B + (slime.field_70177_z - slime.field_70126_B) * f;
         double d = slime.field_70169_q + (slime.field_70165_t - slime.field_70169_q) * (double)f;
         double d1 = slime.field_70167_r + (slime.field_70163_u - slime.field_70167_r) * (double)f + 1.62D;
         double d2 = slime.field_70166_s + (slime.field_70161_v - slime.field_70166_s) * (double)f;
         Vec3d vec3d = new Vec3d(d, d1, d2);
         float f3 = MathHelper.func_76134_b(-f2 * 0.01745329F - 3.141593F);
         float f4 = MathHelper.func_76126_a(-f2 * 0.01745329F - 3.141593F);
         float f5 = -MathHelper.func_76134_b(-f1 * 0.01745329F);
         float f6 = MathHelper.func_76126_a(-f1 * 0.01745329F);
         float f7 = f4 * f5;
         float f9 = f3 * f5;
         double d3 = 5000.0D;
         Vec3d vec3d1 = vec3d.func_72441_c((double)f7 * d3, (double)f6 * d3, (double)f9 * d3);
         RayTraceResult block = slime.field_70170_p.func_72901_a(vec3d, vec3d1, true);
         return slime.field_70163_u - block.field_72307_f.field_72448_b;
      }
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.field_70170_p.field_72995_K) {
         if (!(this.field_70170_p instanceof FakeWorld)) {
            if (this.currentJumpStage() == SlimeEntity.JumpStage.START && this.field_70163_u - this.prevRenderPos < 0.0D) {
               this.setCurrentJumpStage(SlimeEntity.JumpStage.AIR);
            }

            if (this.currentJumpStage() == SlimeEntity.JumpStage.AIR && distanceToGround(this) < 3.0D) {
               this.setCurrentJumpStage(SlimeEntity.JumpStage.END);
            }
         }

      }
   }

   public void registerControllers(AnimationData data) {
      data.addAnimationController(this.eyesController);
      AnimationController.ISoundListener movementSoundListener = (event) -> {
         String command = event.sound;
         byte var4 = -1;
         switch(command.hashCode()) {
         case -2072814986:
            if (command.equals("jumpStartDone")) {
               var4 = 1;
            }
            break;
         case -2024608958:
            if (command.equals("jumpEndSound")) {
               var4 = 2;
            }
            break;
         case -481399057:
            if (command.equals("jumpEndDone")) {
               var4 = 3;
            }
            break;
         case 674057428:
            if (command.equals("jumpStart")) {
               var4 = 0;
            }
         }

         switch(var4) {
         case 0:
            this.playSoundAroundHer(SoundEvents.field_187882_fq);
            break;
         case 1:
            this.field_70180_af.func_187227_b(currentJumpStage, SlimeEntity.JumpStage.AIR.toString());
            break;
         case 2:
            if (this.antiSoundStackTick < 0L) {
               this.antiSoundStackTick = 20L;
               this.playSoundAroundHer(SoundEvents.field_187886_fs);
            }
            break;
         case 3:
            this.field_70180_af.func_187227_b(currentJumpStage, SlimeEntity.JumpStage.NONE.toString());
            this.changeDataParameterFromClient("currentJumpStage", SlimeEntity.JumpStage.NONE.toString());
         }

      };
      this.movementController.registerSoundListener(movementSoundListener);
      data.addAnimationController(this.movementController);
      AnimationController.ISoundListener actionSoundListener = (event) -> {
         String command = event.sound;
         byte var4 = -1;
         switch(command.hashCode()) {
         case -2038339681:
            if (command.equals("doggyslowMSG1")) {
               var4 = 26;
            }
            break;
         case -2038339680:
            if (command.equals("doggyslowMSG2")) {
               var4 = 14;
            }
            break;
         case -1649091690:
            if (command.equals("doggystartDone")) {
               var4 = 25;
            }
            break;
         case -1648851740:
            if (command.equals("doggystartMSG1")) {
               var4 = 20;
            }
            break;
         case -1648851739:
            if (command.equals("doggystartMSG2")) {
               var4 = 21;
            }
            break;
         case -1648851738:
            if (command.equals("doggystartMSG3")) {
               var4 = 22;
            }
            break;
         case -1648851737:
            if (command.equals("doggystartMSG4")) {
               var4 = 23;
            }
            break;
         case -1648851736:
            if (command.equals("doggystartMSG5")) {
               var4 = 24;
            }
            break;
         case -1370194640:
            if (command.equals("bjcBlackScreen")) {
               var4 = 15;
            }
            break;
         case -572151107:
            if (command.equals("doggycumMSG1")) {
               var4 = 29;
            }
            break;
         case -558244113:
            if (command.equals("becomeNude")) {
               var4 = 2;
            }
            break;
         case -291196098:
            if (command.equals("undress")) {
               var4 = 0;
            }
            break;
         case -90697923:
            if (command.equals("bjcDone")) {
               var4 = 16;
            }
            break;
         case -90457973:
            if (command.equals("bjcMSG1")) {
               var4 = 12;
            }
            break;
         case -90457972:
            if (command.equals("bjcMSG2")) {
               var4 = 13;
            }
            break;
         case -85156797:
            if (command.equals("bjiDone")) {
               var4 = 8;
            }
            break;
         case -74998066:
            if (command.equals("bjtDone")) {
               var4 = 9;
            }
            break;
         case -74758116:
            if (command.equals("bjtMSG1")) {
               var4 = 7;
            }
            break;
         case 13829932:
            if (command.equals("doggyfastDone")) {
               var4 = 28;
            }
            break;
         case 14069882:
            if (command.equals("doggyfastMSG1")) {
               var4 = 27;
            }
            break;
         case 95849015:
            if (command.equals("dress")) {
               var4 = 1;
            }
            break;
         case 441346873:
            if (command.equals("doggyfastReady")) {
               var4 = 11;
            }
            break;
         case 1092262223:
            if (command.equals("doggyCumDone")) {
               var4 = 17;
            }
            break;
         case 1662545087:
            if (command.equals("bjiMSG10")) {
               var4 = 4;
            }
            break;
         case 1662545088:
            if (command.equals("bjiMSG11")) {
               var4 = 5;
            }
            break;
         case 1662545089:
            if (command.equals("bjiMSG12")) {
               var4 = 6;
            }
            break;
         case 1982646231:
            if (command.equals("bjtReady")) {
               var4 = 10;
            }
            break;
         case 1988710681:
            if (command.equals("sexUiOn")) {
               var4 = 3;
            }
            break;
         case 2105823406:
            if (command.equals("doggyGoOnBedDone")) {
               var4 = 19;
            }
            break;
         case 2106063356:
            if (command.equals("doggyGoOnBedMSG1")) {
               var4 = 18;
            }
         }

         switch(var4) {
         case 0:
            if (this.isClosestPlayer()) {
               this.field_70180_af.func_187227_b(CURRENT_MODEL, 0);
               this.resetGirl();
            }
            break;
         case 1:
            if (this.isClosestPlayer()) {
               this.field_70180_af.func_187227_b(CURRENT_MODEL, 1);
               this.setCurrentAction((GirlEntity.Action)null);
               this.resetGirl();
            }
            break;
         case 2:
            this.field_70180_af.func_187227_b(CURRENT_MODEL, 0);
            break;
         case 3:
            if (this.belongsToPlayer()) {
               SexUI.shouldBeRendered = true;
            }
            break;
         case 4:
            if (this.belongsToPlayer()) {
               this.moveCamera(-0.4D, -0.8D, -0.2D, 60.0F, -3.0F);
            }
            break;
         case 5:
            this.playerIsThrusting = false;
            this.playSoundAroundHer(SoundEvents.field_187886_fs, 0.5F);
            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.02D);
            }
            break;
         case 6:
            if (Reference.RANDOM.nextInt(5) == 0) {
               this.playSoundAroundHer(SoundEvents.field_187882_fq, 0.5F);
            }

            this.playSoundAroundHer(SoundEvents.field_187886_fs, 0.5F);
            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.02D);
            }
            break;
         case 7:
            this.playSoundAroundHer(SoundEvents.field_187878_fo);
            this.playSoundAroundHer(SoundEvents.field_187874_fm);
            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.04D);
            }
            break;
         case 8:
            this.setCurrentAction(GirlEntity.Action.SUCKBLOWJOB);
            if (this.belongsToPlayer()) {
               SexUI.shouldBeRendered = true;
            }
            break;
         case 9:
            this.setCurrentAction(GirlEntity.Action.SUCKBLOWJOB);
            break;
         case 10:
         case 11:
            this.playerIsThrusting = false;
            break;
         case 12:
            this.playSoundAroundHer(SoundEvents.field_187882_fq);
            break;
         case 13:
            this.playSoundAroundHer(SoundEvents.field_187882_fq);
            if (this.belongsToPlayer()) {
               SexUI.shouldBeRendered = false;
            }
            break;
         case 14:
            this.playSoundAroundHer(SoundEvents.field_187878_fo);
            break;
         case 15:
            if (this.belongsToPlayer()) {
               BlackScreenUI.activate();
            }
            break;
         case 16:
         case 17:
            if (this.belongsToPlayer()) {
               SexUI.resetCumPercentage();
               this.resetGirl();
               PacketHandler.INSTANCE.sendToServer(new SetSlimePregnant(this.girlId(), true));
            }
            break;
         case 18:
            this.playSoundAroundHer(SoundEvents.field_187886_fs);
            this.playerYaw = this.field_70177_z;
            break;
         case 19:
            PacketHandler.INSTANCE.sendToServer(new JennyAwaitPlayerDoggy(this.girlId(), Minecraft.func_71410_x().field_71439_g.getPersistentID()));
            this.setCurrentAction(GirlEntity.Action.WAITDOGGY);
            break;
         case 20:
            this.playSoundAroundHer(SoundsHandler.MISC_TOUCH[0]);
            break;
         case 21:
            this.playSoundAroundHer(SoundsHandler.MISC_TOUCH[1]);
            break;
         case 22:
            this.playSoundAroundHer(SoundEvents.field_187886_fs, 0.25F);
            break;
         case 23:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_SMALLINSERTS), 1.5F);
            if (this.belongsToPlayer()) {
               SexUI.resetCumPercentage();
            }
            break;
         case 24:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING), 0.33F);
            this.playSoundAroundHer(SoundEvents.field_187878_fo);
            break;
         case 25:
            this.setCurrentAction(GirlEntity.Action.DOGGYSLOW);
            if (this.belongsToPlayer()) {
               SexUI.shouldBeRendered = true;
            }
            break;
         case 26:
            this.playerIsThrusting = false;
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING), 0.33F);
            int rand = Reference.RANDOM.nextInt(4);
            if (rand == 0) {
               rand = Reference.RANDOM.nextInt(2);
               if (rand == 0) {
                  this.playSoundAroundHer(SoundEvents.field_187882_fq);
               } else {
                  this.playSoundAroundHer(SoundEvents.field_187886_fs);
               }
            } else {
               this.playSoundAroundHer(SoundEvents.field_187878_fo);
            }

            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.00666D);
            }
            break;
         case 27:
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING), 0.75F);
            if (this.belongsToPlayer()) {
               SexUI.addCumPercentage(0.02D);
            }

            ++this.flip;
            if (this.flip % 2 == 0) {
               int random = Reference.RANDOM.nextInt(2);
               if (random == 0) {
                  this.playSoundAroundHer(SoundEvents.field_187882_fq);
               } else {
                  this.playSoundAroundHer(SoundEvents.field_187886_fs);
               }
            } else {
               this.playSoundAroundHer(SoundEvents.field_187878_fo);
            }
            break;
         case 28:
            this.setCurrentAction(GirlEntity.Action.DOGGYSLOW);
            break;
         case 29:
            this.playSoundAroundHer(SoundsHandler.MISC_CUMINFLATION[0], 4.0F);
            this.playSoundAroundHer(SoundsHandler.random(SoundsHandler.MISC_POUNDING), 2.0F);
            this.playSoundAroundHer(SoundEvents.field_187874_fm);
         }

      };
      this.actionController.registerSoundListener(actionSoundListener);
      data.addAnimationController(this.actionController);
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
            if (this.currentAction() != GirlEntity.Action.NULL && this.currentAction().autoBlink) {
               this.createAnimation("animation.slime.fhappy", true, event);
            } else {
               this.createAnimation("animation.slime.null", true, event);
            }
            break;
         case 1:
            if (this.currentAction() != GirlEntity.Action.NULL) {
               this.createAnimation("animation.slime.null", true, event);
            } else {
               switch(SlimeEntity.JumpStage.valueOf((String)this.field_70180_af.func_187225_a(currentJumpStage))) {
               case START:
                  this.createAnimation("animation.slime.jumpstart", false, event);
                  return PlayState.CONTINUE;
               case AIR:
                  this.createAnimation("animation.slime.jumpair", true, event);
                  return PlayState.CONTINUE;
               case END:
                  this.createAnimation("animation.slime.jumpend", false, event);
                  return PlayState.CONTINUE;
               case NONE:
                  this.createAnimation("animation.slime.idle", true, event);
               }
            }
            break;
         case 2:
            if (this.currentAction() == GirlEntity.Action.NULL) {
               this.createAnimation("animation.slime.null", true, event);
            } else {
               switch(this.currentAction()) {
               case UNDRESS:
                  this.createAnimation("animation.slime.undress", false, event);
                  break;
               case DRESS:
                  this.createAnimation("animation.slime.dress", false, event);
                  break;
               case STRIP:
                  this.createAnimation("animation.slime.strip", false, event);
                  break;
               case STARTBLOWJOB:
                  this.createAnimation("animation.slime.blowjobintro", false, event);
                  break;
               case SUCKBLOWJOB:
                  this.createAnimation("animation.slime.blowjobsuck", true, event);
                  break;
               case THRUSTBLOWJOB:
                  this.createAnimation("animation.slime.blowjobthrust", false, event);
                  break;
               case CUMBLOWJOB:
                  this.createAnimation("animation.slime.blowjobcum", false, event);
                  break;
               case STARTDOGGY:
                  this.createAnimation("animation.slime.doggygoonbed", false, event);
                  break;
               case WAITDOGGY:
                  this.createAnimation("animation.slime.doggywait", true, event);
                  break;
               case DOGGYSTART:
                  this.createAnimation("animation.slime.doggystart", false, event);
                  break;
               case DOGGYSLOW:
                  this.createAnimation("animation.slime.doggyslow", true, event);
                  break;
               case DOGGYFAST:
                  this.createAnimation("animation.slime.doggyfast", false, event);
                  break;
               case DOGGYCUM:
                  this.createAnimation("animation.slime.doggycum", false, event);
               }
            }
         }

         return PlayState.CONTINUE;
      }
   }

   protected void cum() {
      if (this.currentAction() != GirlEntity.Action.SUCKBLOWJOB && this.currentAction() != GirlEntity.Action.THRUSTBLOWJOB) {
         if (this.currentAction() == GirlEntity.Action.DOGGYSLOW || this.currentAction() == GirlEntity.Action.DOGGYFAST) {
            this.playerIsCumming = true;
            this.actionController.transitionLengthTicks = 2.0D;
            this.setCurrentAction(GirlEntity.Action.DOGGYCUM);
         }
      } else {
         this.playerIsCumming = true;
         this.actionController.transitionLengthTicks = 2.0D;
         this.setCurrentAction(GirlEntity.Action.CUMBLOWJOB);
      }

   }

   protected void thrust() {
      if (this.currentAction() != GirlEntity.Action.SUCKBLOWJOB && this.currentAction() != GirlEntity.Action.THRUSTBLOWJOB) {
         if (this.currentAction() == GirlEntity.Action.DOGGYSLOW || this.currentAction() == GirlEntity.Action.DOGGYFAST) {
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

   protected void checkFollowUp() {
   }

   protected SoundEvent func_184639_G() {
      return null;
   }

   protected SoundEvent func_184601_bQ(DamageSource damageSourceIn) {
      return SoundEvents.field_187880_fp;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_187874_fm;
   }

   static {
      currentJumpStage = EntityDataManager.func_187226_a(SlimeEntity.class, DataSerializers.field_187194_d).func_187156_b().func_187161_a(75);
      hornyLevel = EntityDataManager.func_187226_a(SlimeEntity.class, DataSerializers.field_187192_b).func_187156_b().func_187161_a(74);
   }

   class SlimeMoveHelper extends EntityMoveHelper {
      private float yRot;
      private int jumpDelay;
      private final SlimeEntity slime;

      public SlimeMoveHelper(SlimeEntity slimeIn) {
         super(slimeIn);
         this.slime = slimeIn;
         this.yRot = 180.0F * slimeIn.field_70177_z / 3.1415927F;
      }

      public void setDirection(float p_179920_1_) {
         this.yRot = p_179920_1_;
      }

      public void setSpeed(double speedIn) {
         this.field_75645_e = speedIn;
         this.field_188491_h = net.minecraft.entity.ai.EntityMoveHelper.Action.MOVE_TO;
      }

      public void func_75641_c() {
         this.field_75648_a.field_70177_z = this.func_75639_a(this.field_75648_a.field_70177_z, this.yRot, 90.0F);
         this.field_75648_a.field_70759_as = this.field_75648_a.field_70177_z;
         this.field_75648_a.field_70761_aq = this.field_75648_a.field_70177_z;
         if (this.field_188491_h != net.minecraft.entity.ai.EntityMoveHelper.Action.MOVE_TO) {
            this.field_75648_a.func_191989_p(0.0F);
         } else {
            this.field_188491_h = net.minecraft.entity.ai.EntityMoveHelper.Action.WAIT;
            if (this.field_75648_a.field_70122_E) {
               if (this.jumpDelay-- <= 0) {
                  this.jumpDelay = this.slime.getHornyLevel() == 2 ? 100 : Reference.RANDOM.nextInt(100) + 100;
                  double r = Math.random();
                  if (r < SlimeEntity.hornyLevelIncreasePercentage && !this.slime.isPregnant) {
                     this.slime.setHornyLevel(Math.min(this.slime.getHornyLevel() + 1, 2));
                     if (this.slime.getHornyLevel() == 1) {
                        this.slime.setCurrentAction(GirlEntity.Action.UNDRESS);
                        return;
                     }
                  }

                  float chosenDegrees;
                  if (this.slime.getHornyLevel() != 2) {
                     chosenDegrees = (float)this.slime.func_70681_au().nextInt(360);
                  } else {
                     EntityPlayer playerToChase = this.slime.field_70170_p.func_72890_a(this.slime, 10.0D);
                     if (playerToChase == null) {
                        chosenDegrees = (float)this.slime.func_70681_au().nextInt(360);
                     } else {
                        Vec3d distance = this.slime.func_174791_d().func_178788_d(playerToChase.func_174791_d());
                        chosenDegrees = (float)Math.atan2(distance.field_72449_c, distance.field_72450_a) * 57.29578F + 90.0F;
                     }

                     ++this.slime.playerChasingCounter;
                     if (this.slime.playerChasingCounter > 6) {
                        this.slime.playerChasingCounter = 0;
                        this.slime.setCurrentAction(GirlEntity.Action.STARTDOGGY);
                        this.slime.awaitPlayer = true;
                        this.slime.field_70714_bg.func_85156_a(this.slime.hopTask);
                        this.slime.field_70714_bg.func_85156_a(this.slime.floatTask);
                        return;
                     }
                  }

                  if (!this.slime.isPregnant) {
                     ((SlimeEntity.SlimeMoveHelper)this.slime.func_70605_aq()).setDirection(chosenDegrees);
                     this.slime.field_70180_af.func_187227_b(SlimeEntity.currentJumpStage, SlimeEntity.JumpStage.NONE.toString());
                     this.slime.field_70180_af.func_187227_b(SlimeEntity.currentJumpStage, SlimeEntity.JumpStage.START.toString());
                     this.slime.ticksUntilJump = 15;
                  }
               } else {
                  this.slime.field_70702_br = 0.0F;
                  this.slime.field_191988_bg = 0.0F;
                  this.field_75648_a.func_70659_e(0.0F);
               }
            } else {
               this.field_75648_a.func_70659_e((float)(this.field_75645_e * this.field_75648_a.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111126_e()));
            }
         }

      }
   }

   static class AISlimeHop extends EntityAIBase {
      private final SlimeEntity slime;

      public AISlimeHop(SlimeEntity slimeIn) {
         this.slime = slimeIn;
         this.func_75248_a(5);
      }

      public boolean func_75250_a() {
         return true;
      }

      public void func_75246_d() {
         ((SlimeEntity.SlimeMoveHelper)this.slime.func_70605_aq()).setSpeed(1.0D);
      }
   }

   static class AISlimeFloat extends EntityAIBase {
      private final SlimeEntity slime;

      public AISlimeFloat(SlimeEntity slimeIn) {
         this.slime = slimeIn;
         this.func_75248_a(5);
         ((PathNavigateGround)slimeIn.func_70661_as()).func_179693_d(true);
      }

      public boolean func_75250_a() {
         return this.slime.func_70090_H() || this.slime.func_180799_ab();
      }

      public void func_75246_d() {
         if (this.slime.func_70681_au().nextFloat() < 0.8F) {
            this.slime.func_70683_ar().func_75660_a();
         }

      }
   }

   public static enum JumpStage {
      START,
      AIR,
      END,
      NONE;
   }
}

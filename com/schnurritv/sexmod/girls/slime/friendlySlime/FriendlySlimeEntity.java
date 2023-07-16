package com.schnurritv.sexmod.girls.slime.friendlySlime;

import com.schnurritv.sexmod.girls.slime.SlimeEntity;
import com.schnurritv.sexmod.util.Reference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntityMoveHelper.Action;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class FriendlySlimeEntity extends EntityLiving {
   public static int ticksUntilGrowth = 8400;
   public static List<FriendlySlimeEntity> slimeEntities = new ArrayList();
   private static final DataParameter<Integer> ageInTicks;
   private static final DataParameter<Integer> SLIME_SIZE;
   public float squishAmount;
   public float squishFactor;
   public float prevSquishFactor;
   private boolean wasOnGround;

   public FriendlySlimeEntity(World worldIn) {
      super(worldIn);
      this.field_70765_h = new FriendlySlimeEntity.SlimeMoveHelper(this);
   }

   protected void func_184651_r() {
      this.field_70714_bg.func_75776_a(1, new FriendlySlimeEntity.AISlimeFloat(this));
      this.field_70714_bg.func_75776_a(5, new FriendlySlimeEntity.AISlimeHop(this));
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(SLIME_SIZE, 1);
      this.field_70180_af.func_187214_a(ageInTicks, 0);
   }

   protected boolean func_70692_ba() {
      return false;
   }

   protected void setSlimeSize(int size, boolean resetHealth) {
      this.field_70180_af.func_187227_b(SLIME_SIZE, size);
      this.func_70105_a(0.51000005F * (float)size, 0.51000005F * (float)size);
      this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a((double)(size * size));
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a((double)(0.2F + 0.1F * (float)size));
      if (resetHealth) {
         this.func_70606_j(this.func_110138_aP());
      }

      this.field_70728_aV = size;
   }

   public int getSlimeSize() {
      return (Integer)this.field_70180_af.func_187225_a(SLIME_SIZE);
   }

   public static void registerFixesSlime(DataFixer fixer) {
      EntityLiving.func_189752_a(fixer, FriendlySlimeEntity.class);
   }

   public void func_70014_b(NBTTagCompound compound) {
      super.func_70014_b(compound);
      compound.func_74768_a("Size", this.getSlimeSize() - 1);
      compound.func_74757_a("wasOnGround", this.wasOnGround);
      compound.func_74768_a("ageInTicks", (Integer)this.field_70180_af.func_187225_a(ageInTicks));
   }

   public void func_70037_a(NBTTagCompound compound) {
      super.func_70037_a(compound);
      int i = compound.func_74762_e("Size");
      if (i < 0) {
         i = 0;
      }

      this.setSlimeSize(i + 1, false);
      this.wasOnGround = compound.func_74767_n("wasOnGround");
      this.field_70180_af.func_187227_b(ageInTicks, compound.func_74762_e("ageInTicks"));
   }

   public boolean isSmallSlime() {
      return this.getSlimeSize() <= 1;
   }

   protected EnumParticleTypes getParticleType() {
      return EnumParticleTypes.SLIME;
   }

   public static ArrayList<FriendlySlimeEntity> getSlimeByPos(Vec3d pos) {
      ArrayList<FriendlySlimeEntity> slimes = getByDistance(pos, 0.1D);
      if (slimes.isEmpty()) {
         slimes = getByDistance(pos, 0.5D);
      }

      return slimes;
   }

   private static ArrayList<FriendlySlimeEntity> getByDistance(Vec3d pos, double distance) {
      ArrayList slimes = new ArrayList();

      try {
         Iterator var4 = slimeEntities.iterator();

         while(var4.hasNext()) {
            FriendlySlimeEntity girl = (FriendlySlimeEntity)var4.next();
            if (girl != null) {
               double dist = Math.abs(girl.field_70169_q - pos.field_72450_a) + Math.abs(girl.field_70167_r - pos.field_72448_b) + Math.abs(girl.field_70166_s - pos.field_72449_c);
               if (girl.field_70170_p != null && dist < distance) {
                  slimes.add(girl);
               }
            }
         }
      } catch (Exception var8) {
         System.out.println("couldnt find slimes at distance " + distance);
      }

      return slimes;
   }

   public Vec3d prevPos() {
      return new Vec3d(this.field_70169_q, this.field_70167_r, this.field_70166_s);
   }

   void spawnParticle(EnumParticleTypes particle) {
      double motionX = Reference.RANDOM.nextGaussian() * 0.02D;
      double motionY = Reference.RANDOM.nextGaussian() * 0.02D;
      double motionZ = Reference.RANDOM.nextGaussian() * 0.02D;
      this.field_70170_p.func_175688_a(particle, this.field_70165_t + (double)(Reference.RANDOM.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, this.field_70163_u + 0.15D + (double)(Reference.RANDOM.nextFloat() * this.field_70131_O), this.field_70161_v + (double)(Reference.RANDOM.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, motionX, motionY, motionZ, new int[0]);
   }

   public void func_70071_h_() {
      this.field_70180_af.func_187227_b(ageInTicks, (Integer)this.field_70180_af.func_187225_a(ageInTicks) + 1);
      if (this.field_70170_p.field_72995_K) {
         if ((double)(Integer)this.field_70180_af.func_187225_a(ageInTicks) > (double)ticksUntilGrowth * 0.95D) {
            this.spawnParticle(EnumParticleTypes.CLOUD);
         } else if ((double)(Integer)this.field_70180_af.func_187225_a(ageInTicks) > (double)ticksUntilGrowth * 0.7D && this.field_70173_aa % 10 == 0) {
            this.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY);
         }
      } else if ((Integer)this.field_70180_af.func_187225_a(ageInTicks) > ticksUntilGrowth) {
         SlimeEntity adult = new SlimeEntity(this.field_70170_p);
         adult.func_70080_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
         this.field_70170_p.func_72838_d(adult);
         adult.playSoundAroundHer(SoundEvents.field_187604_bf);
         this.field_70170_p.func_72900_e(this);
      }

      this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5F;
      this.prevSquishFactor = this.squishFactor;
      super.func_70071_h_();
      if (this.field_70122_E && !this.wasOnGround) {
         int i = this.getSlimeSize();
         if (this.spawnCustomParticles()) {
            i = 0;
         }

         for(int j = 0; j < i * 8; ++j) {
            float f = this.field_70146_Z.nextFloat() * 6.2831855F;
            float f1 = this.field_70146_Z.nextFloat() * 0.5F + 0.5F;
            float f2 = MathHelper.func_76126_a(f) * (float)i * 0.5F * f1;
            float f3 = MathHelper.func_76134_b(f) * (float)i * 0.5F * f1;
            World world = this.field_70170_p;
            EnumParticleTypes enumparticletypes = this.getParticleType();
            double d0 = this.field_70165_t + (double)f2;
            double d1 = this.field_70161_v + (double)f3;
            world.func_175688_a(enumparticletypes, d0, this.func_174813_aQ().field_72338_b, d1, 0.0D, 0.0D, 0.0D, new int[0]);
         }

         this.func_184185_a(this.getSquishSound(), this.func_70599_aP(), ((this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.0F) / 0.8F);
         this.squishAmount = -0.5F;
      } else if (!this.field_70122_E && this.wasOnGround) {
         this.squishAmount = 1.0F;
      }

      this.wasOnGround = this.field_70122_E;
      this.alterSquishAmount();
   }

   protected void alterSquishAmount() {
      this.squishAmount *= 0.6F;
   }

   protected int getJumpDelay() {
      return this.field_70146_Z.nextInt(100) + 50;
   }

   protected FriendlySlimeEntity createInstance() {
      return new FriendlySlimeEntity(this.field_70170_p);
   }

   public void func_184206_a(DataParameter<?> key) {
      if (SLIME_SIZE.equals(key)) {
         int i = this.getSlimeSize();
         this.func_70105_a(0.51000005F * (float)i, 0.51000005F * (float)i);
         this.field_70177_z = this.field_70759_as;
         this.field_70761_aq = this.field_70759_as;
         if (this.func_70090_H() && this.field_70146_Z.nextInt(20) == 0) {
            this.func_71061_d_();
         }
      }

      super.func_184206_a(key);
   }

   public void func_70106_y() {
      int i = this.getSlimeSize();
      if (!this.field_70170_p.field_72995_K && i > 1 && this.func_110143_aJ() <= 0.0F) {
         int j = 2 + this.field_70146_Z.nextInt(3);

         for(int k = 0; k < j; ++k) {
            float f = ((float)(k % 2) - 0.5F) * (float)i / 4.0F;
            float f1 = ((float)(k / 2) - 0.5F) * (float)i / 4.0F;
            FriendlySlimeEntity FriendlySlimeEntity = this.createInstance();
            if (this.func_145818_k_()) {
               FriendlySlimeEntity.func_96094_a(this.func_95999_t());
            }

            if (this.func_104002_bU()) {
               FriendlySlimeEntity.func_110163_bv();
            }

            FriendlySlimeEntity.setSlimeSize(i / 2, true);
            FriendlySlimeEntity.func_70012_b(this.field_70165_t + (double)f, this.field_70163_u + 0.5D, this.field_70161_v + (double)f1, this.field_70146_Z.nextFloat() * 360.0F, 0.0F);
            this.field_70170_p.func_72838_d(FriendlySlimeEntity);
         }
      }

      super.func_70106_y();
   }

   public float func_70047_e() {
      return 0.625F * this.field_70131_O;
   }

   protected SoundEvent func_184601_bQ(DamageSource damageSourceIn) {
      return this.isSmallSlime() ? SoundEvents.field_187898_fy : SoundEvents.field_187880_fp;
   }

   protected SoundEvent func_184615_bR() {
      return this.isSmallSlime() ? SoundEvents.field_187896_fx : SoundEvents.field_187874_fm;
   }

   protected SoundEvent getSquishSound() {
      return this.isSmallSlime() ? SoundEvents.field_187900_fz : SoundEvents.field_187886_fs;
   }

   protected Item func_146068_u() {
      return this.getSlimeSize() == 1 ? Items.field_151123_aH : null;
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return this.getSlimeSize() == 1 ? LootTableList.field_186378_ac : LootTableList.field_186419_a;
   }

   protected float func_70599_aP() {
      return 0.4F * (float)this.getSlimeSize();
   }

   public int func_70646_bf() {
      return 0;
   }

   protected boolean makesSoundOnJump() {
      return this.getSlimeSize() > 0;
   }

   protected void func_70664_aZ() {
      this.field_70181_x = 0.41999998688697815D;
      this.field_70160_al = true;
   }

   @Nullable
   public IEntityLivingData func_180482_a(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
      this.setSlimeSize(1, true);
      return super.func_180482_a(difficulty, livingdata);
   }

   protected SoundEvent getJumpSound() {
      return this.isSmallSlime() ? SoundEvents.field_189110_fE : SoundEvents.field_187882_fq;
   }

   protected boolean spawnCustomParticles() {
      return false;
   }

   static {
      ageInTicks = EntityDataManager.func_187226_a(FriendlySlimeEntity.class, DataSerializers.field_187192_b);
      SLIME_SIZE = EntityDataManager.func_187226_a(FriendlySlimeEntity.class, DataSerializers.field_187192_b);
   }

   static class SlimeMoveHelper extends EntityMoveHelper {
      private float yRot;
      private int jumpDelay;
      private final FriendlySlimeEntity slime;
      private boolean isAggressive;

      public SlimeMoveHelper(FriendlySlimeEntity slimeIn) {
         super(slimeIn);
         this.slime = slimeIn;
         this.yRot = 180.0F * slimeIn.field_70177_z / 3.1415927F;
      }

      public void setDirection(float p_179920_1_, boolean p_179920_2_) {
         this.yRot = p_179920_1_;
         this.isAggressive = p_179920_2_;
      }

      public void setSpeed(double speedIn) {
         this.field_75645_e = speedIn;
         this.field_188491_h = Action.MOVE_TO;
      }

      public void func_75641_c() {
         this.field_75648_a.field_70177_z = this.func_75639_a(this.field_75648_a.field_70177_z, this.yRot, 90.0F);
         this.field_75648_a.field_70759_as = this.field_75648_a.field_70177_z;
         this.field_75648_a.field_70761_aq = this.field_75648_a.field_70177_z;
         if (this.field_188491_h != Action.MOVE_TO) {
            this.field_75648_a.func_191989_p(0.0F);
         } else {
            this.field_188491_h = Action.WAIT;
            if (this.field_75648_a.field_70122_E) {
               this.field_75648_a.func_70659_e((float)(this.field_75645_e * this.field_75648_a.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111126_e()));
               if (this.jumpDelay-- <= 0) {
                  this.jumpDelay = this.slime.getJumpDelay();
                  if (this.isAggressive) {
                     this.jumpDelay /= 3;
                  }

                  float chosenDegrees = (float)Reference.RANDOM.nextInt(360);
                  ((FriendlySlimeEntity.SlimeMoveHelper)this.slime.func_70605_aq()).setDirection(chosenDegrees, false);
                  this.slime.func_70683_ar().func_75660_a();
                  if (this.slime.makesSoundOnJump()) {
                     this.slime.func_184185_a(this.slime.getJumpSound(), this.slime.func_70599_aP(), ((this.slime.func_70681_au().nextFloat() - this.slime.func_70681_au().nextFloat()) * 0.2F + 1.0F) * 0.8F);
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
      private final FriendlySlimeEntity slime;

      public AISlimeHop(FriendlySlimeEntity slimeIn) {
         this.slime = slimeIn;
         this.func_75248_a(5);
      }

      public boolean func_75250_a() {
         return true;
      }

      public void func_75246_d() {
         ((FriendlySlimeEntity.SlimeMoveHelper)this.slime.func_70605_aq()).setSpeed(1.0D);
      }
   }

   static class AISlimeFloat extends EntityAIBase {
      private final FriendlySlimeEntity slime;

      public AISlimeFloat(FriendlySlimeEntity slimeIn) {
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

         ((FriendlySlimeEntity.SlimeMoveHelper)this.slime.func_70605_aq()).setSpeed(1.2D);
      }
   }

   static class AISlimeFaceRandom extends EntityAIBase {
      private final FriendlySlimeEntity slime;
      private float chosenDegrees;
      private int nextRandomizeTime;

      public AISlimeFaceRandom(FriendlySlimeEntity slimeIn) {
         this.slime = slimeIn;
         this.func_75248_a(2);
      }

      public boolean func_75250_a() {
         return this.slime.func_70638_az() == null && (this.slime.field_70122_E || this.slime.func_70090_H() || this.slime.func_180799_ab() || this.slime.func_70644_a(MobEffects.field_188424_y));
      }

      public void func_75246_d() {
         if (--this.nextRandomizeTime <= 0) {
            this.nextRandomizeTime = 40 + this.slime.func_70681_au().nextInt(60);
            this.chosenDegrees = (float)this.slime.func_70681_au().nextInt(360);
         }

         ((FriendlySlimeEntity.SlimeMoveHelper)this.slime.func_70605_aq()).setDirection(this.chosenDegrees, false);
      }
   }
}

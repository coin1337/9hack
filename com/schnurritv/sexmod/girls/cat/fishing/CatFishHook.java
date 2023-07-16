package com.schnurritv.sexmod.girls.cat.fishing;

import com.google.common.base.Optional;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.cat.CatEntity;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.LootContext.Builder;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CatFishHook extends Entity {
   private static final DataParameter<Integer> DATA_HOOKED_ENTITY;
   private static final DataParameter<Optional<UUID>> ANGLER_UUID;
   private boolean inGround;
   private int ticksInGround;
   private int ticksInAir;
   public int ticksCatchable;
   private int ticksCaughtDelay;
   private int ticksCatchableDelay;
   private float fishApproachAngle;
   public Entity caughtEntity;
   private CatFishHook.State currentState;
   private int luck;
   private int lureSpeed;
   public static CatEntity nextAngler;

   public CatFishHook(World worldIn, CatEntity fishingPlayer) {
      super(worldIn);
      this.currentState = CatFishHook.State.FLYING;
      this.init(fishingPlayer);
      this.shoot();
   }

   public CatFishHook(World worldIn) {
      super(worldIn);
      this.currentState = CatFishHook.State.FLYING;
   }

   private void init(CatEntity p_190626_1_) {
      this.func_70105_a(0.25F, 0.25F);
      this.field_70158_ak = true;
      p_190626_1_.fishEntity = this;
   }

   protected void func_70088_a() {
      this.func_184212_Q().func_187214_a(DATA_HOOKED_ENTITY, 0);
      this.func_184212_Q().func_187214_a(ANGLER_UUID, Optional.of(nextAngler.girlId()));
   }

   public AxisAlignedBB func_184177_bl() {
      return this.func_174813_aQ().func_186662_g(10.0D);
   }

   CatEntity getAngler() {
      Optional<UUID> angler = (Optional)this.field_70180_af.func_187225_a(ANGLER_UUID);
      if (!angler.isPresent()) {
         return null;
      } else {
         List<GirlEntity> girls = GirlEntity.getGirlsByUUID((UUID)angler.get());
         Iterator var3 = girls.iterator();

         GirlEntity girl;
         do {
            if (!var3.hasNext()) {
               return null;
            }

            girl = (GirlEntity)var3.next();
         } while(girl.field_70170_p.field_72995_K || !(girl instanceof CatEntity));

         return (CatEntity)girl;
      }
   }

   CatEntity getAnglerClientSide() {
      Optional<UUID> angler = (Optional)this.field_70180_af.func_187225_a(ANGLER_UUID);
      if (!angler.isPresent()) {
         return null;
      } else {
         List<GirlEntity> girls = GirlEntity.getGirlsByUUID((UUID)angler.get());
         Iterator var3 = girls.iterator();

         GirlEntity girl;
         do {
            if (!var3.hasNext()) {
               return null;
            }

            girl = (GirlEntity)var3.next();
         } while(!girl.field_70170_p.field_72995_K || !(girl instanceof CatEntity));

         return (CatEntity)girl;
      }
   }

   public void setLureSpeed(int p_191516_1_) {
      this.lureSpeed = p_191516_1_;
   }

   public void setLuck(int p_191517_1_) {
      this.luck = p_191517_1_;
   }

   public void shoot() {
      this.shoot(1);
   }

   public void shoot(int dir) {
      CatEntity angler = this.getAngler();
      if (angler != null) {
         BlockPos spot = angler.chosenFishingSpot;
         float distance = (float)Math.sqrt(angler.func_174791_d().func_186679_c((double)spot.func_177958_n(), (double)spot.func_177956_o(), (double)spot.func_177952_p()));
         float f = -22.5F + 45.0F * (distance / 7.0F);
         float f1 = angler.targetYaw();
         float f2 = MathHelper.func_76134_b(-f1 * 0.017453292F - 3.1415927F);
         float f3 = MathHelper.func_76126_a(-f1 * 0.017453292F - 3.1415927F);
         float f4 = -MathHelper.func_76134_b(-f * 0.017453292F);
         float f5 = MathHelper.func_76126_a(-f * 0.017453292F);
         double d0 = angler.field_70169_q + (angler.field_70165_t - angler.field_70169_q) - (double)f3 * 0.3D;
         double d1 = angler.field_70167_r + (angler.field_70163_u - angler.field_70167_r) + (double)angler.func_70047_e();
         double d2 = angler.field_70166_s + (angler.field_70161_v - angler.field_70166_s) - (double)f2 * 0.3D;
         this.func_70012_b(d0, d1, d2, f1, f);
         this.field_70159_w = (double)dir * (double)(-f3);
         this.field_70181_x = (double)dir * (double)MathHelper.func_76131_a(-(f5 / f4), -5.0F, 5.0F);
         this.field_70179_y = (double)dir * (double)(-f2);
         float f6 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70181_x * this.field_70181_x + this.field_70179_y * this.field_70179_y);
         this.field_70159_w *= 0.6D / (double)f6 + 0.5D + this.field_70146_Z.nextGaussian() * 0.0045D;
         this.field_70181_x *= 0.6D / (double)f6 + 0.5D + this.field_70146_Z.nextGaussian() * 0.0045D;
         this.field_70179_y *= 0.6D / (double)f6 + 0.5D + this.field_70146_Z.nextGaussian() * 0.0045D;
         float f7 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
         this.field_70177_z = (float)(MathHelper.func_181159_b(this.field_70159_w, this.field_70179_y) * 57.29577951308232D);
         this.field_70125_A = (float)(MathHelper.func_181159_b(this.field_70181_x, (double)f7) * 57.29577951308232D);
         this.field_70126_B = this.field_70177_z;
         this.field_70127_C = this.field_70125_A;
      }
   }

   public void func_184206_a(DataParameter<?> key) {
      if (DATA_HOOKED_ENTITY.equals(key)) {
         int i = (Integer)this.func_184212_Q().func_187225_a(DATA_HOOKED_ENTITY);
         this.caughtEntity = i > 0 ? this.field_70170_p.func_73045_a(i - 1) : null;
      }

      super.func_184206_a(key);
   }

   @SideOnly(Side.CLIENT)
   public boolean func_70112_a(double distance) {
      double d0 = 64.0D;
      return distance < 4096.0D;
   }

   @SideOnly(Side.CLIENT)
   public void func_180426_a(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.getAngler() == null) {
         this.func_70106_y();
      } else if (this.field_70170_p.field_72995_K || !this.shouldStopFishing()) {
         if (this.inGround) {
            ++this.ticksInGround;
            if (this.ticksInGround >= 1200) {
               this.func_70106_y();
               return;
            }
         }

         float f = 0.0F;
         BlockPos blockpos = new BlockPos(this);
         IBlockState iblockstate = this.field_70170_p.func_180495_p(blockpos);
         if (iblockstate.func_185904_a() == Material.field_151586_h) {
            f = BlockLiquid.func_190973_f(iblockstate, this.field_70170_p, blockpos);
         }

         double d1;
         if (this.currentState == CatFishHook.State.FLYING) {
            if (this.caughtEntity != null) {
               this.field_70159_w = 0.0D;
               this.field_70181_x = 0.0D;
               this.field_70179_y = 0.0D;
               this.currentState = CatFishHook.State.HOOKED_IN_ENTITY;
               return;
            }

            if (f > 0.0F) {
               this.field_70159_w *= 0.3D;
               this.field_70181_x *= 0.2D;
               this.field_70179_y *= 0.3D;
               this.currentState = CatFishHook.State.BOBBING;
               return;
            }

            if (!this.field_70170_p.field_72995_K) {
               this.checkCollision();
            }

            if (!this.inGround && !this.field_70122_E && !this.field_70123_F) {
               ++this.ticksInAir;
            } else {
               this.ticksInAir = 0;
               this.field_70159_w = 0.0D;
               this.field_70181_x = 0.0D;
               this.field_70179_y = 0.0D;
            }
         } else {
            if (this.currentState == CatFishHook.State.HOOKED_IN_ENTITY) {
               if (this.caughtEntity != null) {
                  if (this.caughtEntity.field_70128_L) {
                     this.caughtEntity = null;
                     this.currentState = CatFishHook.State.FLYING;
                  } else {
                     this.field_70165_t = this.caughtEntity.field_70165_t;
                     d1 = (double)this.caughtEntity.field_70131_O;
                     this.field_70163_u = this.caughtEntity.func_174813_aQ().field_72338_b + d1 * 0.8D;
                     this.field_70161_v = this.caughtEntity.field_70161_v;
                     this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
                  }
               }

               return;
            }

            if (this.currentState == CatFishHook.State.BOBBING) {
               this.field_70159_w *= 0.9D;
               this.field_70179_y *= 0.9D;
               d1 = this.field_70163_u + this.field_70181_x - (double)blockpos.func_177956_o() - (double)f;
               if (Math.abs(d1) < 0.01D) {
                  d1 += Math.signum(d1) * 0.1D;
               }

               this.field_70181_x -= d1 * (double)this.field_70146_Z.nextFloat() * 0.2D;
               if (!this.field_70170_p.field_72995_K && f > 0.0F) {
                  this.catchingFish(blockpos);
               }
            }
         }

         if (iblockstate.func_185904_a() != Material.field_151586_h) {
            this.field_70181_x -= 0.03D;
         }

         this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
         this.updateRotation();
         d1 = 0.92D;
         this.field_70159_w *= 0.92D;
         this.field_70181_x *= 0.92D;
         this.field_70179_y *= 0.92D;
         this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      }

   }

   private boolean shouldStopFishing() {
      return false;
   }

   private void updateRotation() {
      float f = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
      this.field_70177_z = (float)(MathHelper.func_181159_b(this.field_70159_w, this.field_70179_y) * 57.29577951308232D);

      for(this.field_70125_A = (float)(MathHelper.func_181159_b(this.field_70181_x, (double)f) * 57.29577951308232D); this.field_70125_A - this.field_70127_C < -180.0F; this.field_70127_C -= 360.0F) {
      }

      while(this.field_70125_A - this.field_70127_C >= 180.0F) {
         this.field_70127_C += 360.0F;
      }

      while(this.field_70177_z - this.field_70126_B < -180.0F) {
         this.field_70126_B -= 360.0F;
      }

      while(this.field_70177_z - this.field_70126_B >= 180.0F) {
         this.field_70126_B += 360.0F;
      }

      this.field_70125_A = this.field_70127_C + (this.field_70125_A - this.field_70127_C) * 0.2F;
      this.field_70177_z = this.field_70126_B + (this.field_70177_z - this.field_70126_B) * 0.2F;
   }

   private void checkCollision() {
      Vec3d vec3d = new Vec3d(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      Vec3d vec3d1 = new Vec3d(this.field_70165_t + this.field_70159_w, this.field_70163_u + this.field_70181_x, this.field_70161_v + this.field_70179_y);
      RayTraceResult raytraceresult = this.field_70170_p.func_147447_a(vec3d, vec3d1, false, true, false);
      vec3d = new Vec3d(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      vec3d1 = new Vec3d(this.field_70165_t + this.field_70159_w, this.field_70163_u + this.field_70181_x, this.field_70161_v + this.field_70179_y);
      if (raytraceresult != null) {
         vec3d1 = new Vec3d(raytraceresult.field_72307_f.field_72450_a, raytraceresult.field_72307_f.field_72448_b, raytraceresult.field_72307_f.field_72449_c);
      }

      Entity entity = null;
      List<Entity> list = this.field_70170_p.func_72839_b(this, this.func_174813_aQ().func_72321_a(this.field_70159_w, this.field_70181_x, this.field_70179_y).func_186662_g(1.0D));
      double d0 = 0.0D;
      Iterator var8 = list.iterator();

      while(true) {
         Entity entity1;
         double d1;
         do {
            RayTraceResult raytraceresult1;
            do {
               do {
                  do {
                     if (!var8.hasNext()) {
                        if (entity != null) {
                           raytraceresult = new RayTraceResult(entity);
                        }

                        if (raytraceresult != null && raytraceresult.field_72313_a != Type.MISS) {
                           if (raytraceresult.field_72313_a == Type.ENTITY) {
                              this.caughtEntity = raytraceresult.field_72308_g;
                              this.setHookedEntity();
                           } else {
                              this.inGround = true;
                           }
                        }

                        return;
                     }

                     entity1 = (Entity)var8.next();
                  } while(!this.canBeHooked(entity1));
               } while(entity1 == this.getAngler() && this.ticksInAir < 5);

               AxisAlignedBB axisalignedbb = entity1.func_174813_aQ().func_186662_g(0.30000001192092896D);
               raytraceresult1 = axisalignedbb.func_72327_a(vec3d, vec3d1);
            } while(raytraceresult1 == null);

            d1 = vec3d.func_72436_e(raytraceresult1.field_72307_f);
         } while(!(d1 < d0) && d0 != 0.0D);

         entity = entity1;
         d0 = d1;
      }
   }

   private void setHookedEntity() {
      this.func_184212_Q().func_187227_b(DATA_HOOKED_ENTITY, this.caughtEntity.func_145782_y() + 1);
   }

   private void catchingFish(BlockPos p_190621_1_) {
      WorldServer worldserver = (WorldServer)this.field_70170_p;
      int i = 1;
      BlockPos blockpos = p_190621_1_.func_177984_a();
      if (this.field_70146_Z.nextFloat() < 0.25F && this.field_70170_p.func_175727_C(blockpos)) {
         ++i;
      }

      if (this.field_70146_Z.nextFloat() < 0.5F && !this.field_70170_p.func_175678_i(blockpos)) {
         --i;
      }

      if (this.ticksCatchable > 0) {
         --this.ticksCatchable;
         if (this.ticksCatchable <= 0) {
            this.ticksCaughtDelay = 0;
            this.ticksCatchableDelay = 0;
         } else {
            this.field_70181_x -= 0.2D * (double)this.field_70146_Z.nextFloat() * (double)this.field_70146_Z.nextFloat();
         }
      } else {
         float f5;
         float f6;
         float f7;
         double d4;
         double d5;
         double d6;
         IBlockState state;
         if (this.ticksCatchableDelay > 0) {
            this.ticksCatchableDelay -= i;
            if (this.ticksCatchableDelay > 0) {
               this.fishApproachAngle = (float)((double)this.fishApproachAngle + this.field_70146_Z.nextGaussian() * 4.0D);
               f5 = this.fishApproachAngle * 0.017453292F;
               f6 = MathHelper.func_76126_a(f5);
               f7 = MathHelper.func_76134_b(f5);
               d4 = this.field_70165_t + (double)(f6 * (float)this.ticksCatchableDelay * 0.1F);
               d5 = (double)((float)MathHelper.func_76128_c(this.func_174813_aQ().field_72338_b) + 1.0F);
               d6 = this.field_70161_v + (double)(f7 * (float)this.ticksCatchableDelay * 0.1F);
               state = worldserver.func_180495_p(new BlockPos(d4, d5 - 1.0D, d6));
               if (state.func_185904_a() == Material.field_151586_h) {
                  if (this.field_70146_Z.nextFloat() < 0.15F) {
                     worldserver.func_175739_a(EnumParticleTypes.WATER_BUBBLE, d4, d5 - 0.10000000149011612D, d6, 1, (double)f6, 0.1D, (double)f7, 0.0D, new int[0]);
                  }

                  float f3 = f6 * 0.04F;
                  float f4 = f7 * 0.04F;
                  worldserver.func_175739_a(EnumParticleTypes.WATER_WAKE, d4, d5, d6, 0, (double)f4, 0.01D, (double)(-f3), 1.0D, new int[0]);
                  worldserver.func_175739_a(EnumParticleTypes.WATER_WAKE, d4, d5, d6, 0, (double)(-f4), 0.01D, (double)f3, 1.0D, new int[0]);
               }
            } else {
               this.field_70181_x = (double)(-0.4F * MathHelper.func_151240_a(this.field_70146_Z, 0.6F, 1.0F));
               this.func_184185_a(SoundEvents.field_187609_F, 0.25F, 1.0F + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.4F);
               double d3 = this.func_174813_aQ().field_72338_b + 0.5D;
               worldserver.func_175739_a(EnumParticleTypes.WATER_BUBBLE, this.field_70165_t, d3, this.field_70161_v, (int)(1.0F + this.field_70130_N * 20.0F), (double)this.field_70130_N, 0.0D, (double)this.field_70130_N, 0.20000000298023224D, new int[0]);
               worldserver.func_175739_a(EnumParticleTypes.WATER_WAKE, this.field_70165_t, d3, this.field_70161_v, (int)(1.0F + this.field_70130_N * 20.0F), (double)this.field_70130_N, 0.0D, (double)this.field_70130_N, 0.20000000298023224D, new int[0]);
               this.ticksCatchable = MathHelper.func_76136_a(this.field_70146_Z, 20, 40);
            }
         } else if (this.ticksCaughtDelay > 0) {
            this.ticksCaughtDelay -= i;
            f5 = 0.15F;
            if (this.ticksCaughtDelay < 20) {
               f5 = (float)((double)f5 + (double)(20 - this.ticksCaughtDelay) * 0.05D);
            } else if (this.ticksCaughtDelay < 40) {
               f5 = (float)((double)f5 + (double)(40 - this.ticksCaughtDelay) * 0.02D);
            } else if (this.ticksCaughtDelay < 60) {
               f5 = (float)((double)f5 + (double)(60 - this.ticksCaughtDelay) * 0.01D);
            }

            if (this.field_70146_Z.nextFloat() < f5) {
               f6 = MathHelper.func_151240_a(this.field_70146_Z, 0.0F, 360.0F) * 0.017453292F;
               f7 = MathHelper.func_151240_a(this.field_70146_Z, 25.0F, 60.0F);
               d4 = this.field_70165_t + (double)(MathHelper.func_76126_a(f6) * f7 * 0.1F);
               d5 = (double)((float)MathHelper.func_76128_c(this.func_174813_aQ().field_72338_b) + 1.0F);
               d6 = this.field_70161_v + (double)(MathHelper.func_76134_b(f6) * f7 * 0.1F);
               state = worldserver.func_180495_p(new BlockPos((int)d4, (int)d5 - 1, (int)d6));
               if (state.func_185904_a() == Material.field_151586_h) {
                  worldserver.func_175739_a(EnumParticleTypes.WATER_SPLASH, d4, d5, d6, 2 + this.field_70146_Z.nextInt(2), 0.10000000149011612D, 0.0D, 0.10000000149011612D, 0.0D, new int[0]);
               }
            }

            if (this.ticksCaughtDelay <= 0) {
               this.fishApproachAngle = MathHelper.func_151240_a(this.field_70146_Z, 0.0F, 360.0F);
               this.ticksCatchableDelay = MathHelper.func_76136_a(this.field_70146_Z, 20, 80);
            }
         } else {
            this.ticksCaughtDelay = MathHelper.func_76136_a(this.field_70146_Z, 100, 600);
            this.ticksCaughtDelay -= this.lureSpeed * 20 * 5;
         }
      }

   }

   protected boolean canBeHooked(Entity p_189739_1_) {
      return p_189739_1_.func_70067_L() || p_189739_1_ instanceof EntityItem;
   }

   public void func_70014_b(NBTTagCompound compound) {
   }

   public void func_70037_a(NBTTagCompound compound) {
   }

   public int handleHookRetraction() {
      if (!this.field_70170_p.field_72995_K && this.getAngler() != null) {
         int i = 0;
         ItemFishedEvent event = null;
         if (this.caughtEntity != null) {
            this.bringInHookedEntity();
            this.field_70170_p.func_72960_a(this, (byte)31);
            i = this.caughtEntity instanceof EntityItem ? 3 : 5;
         } else if (this.ticksCatchable > 0) {
            Builder lootcontext$builder = new Builder((WorldServer)this.field_70170_p);
            List<ItemStack> result = this.field_70170_p.func_184146_ak().func_186521_a(LootTableList.field_186387_al).func_186462_a(this.field_70146_Z, lootcontext$builder.func_186471_a());
            Iterator var5 = result.iterator();

            while(var5.hasNext()) {
               ItemStack itemstack = (ItemStack)var5.next();
               CatEntity angler = this.getAngler();
               angler.catchItem(itemstack);
            }

            this.ticksCatchable = 9999;
            i = 1;
         }

         if (this.inGround) {
            i = 2;
         }

         return event == null ? i : ((ItemFishedEvent)event).getRodDamage();
      } else {
         return 0;
      }
   }

   protected void bringInHookedEntity() {
      CatEntity angler = this.getAngler();
      if (angler != null) {
         double d0 = angler.field_70165_t - this.field_70165_t;
         double d1 = angler.field_70163_u - this.field_70163_u;
         double d2 = angler.field_70161_v - this.field_70161_v;
         double d3 = 0.1D;
         Entity var10000 = this.caughtEntity;
         var10000.field_70159_w += d0 * 0.1D;
         var10000 = this.caughtEntity;
         var10000.field_70181_x += d1 * 0.1D;
         var10000 = this.caughtEntity;
         var10000.field_70179_y += d2 * 0.1D;
      }

   }

   protected boolean func_70041_e_() {
      return false;
   }

   public void func_70020_e(NBTTagCompound compound) {
   }

   public NBTTagCompound func_189511_e(NBTTagCompound compound) {
      return null;
   }

   static {
      DATA_HOOKED_ENTITY = EntityDataManager.func_187226_a(CatFishHook.class, DataSerializers.field_187192_b);
      ANGLER_UUID = EntityDataManager.func_187226_a(CatFishHook.class, DataSerializers.field_187203_m);
      nextAngler = null;
   }

   static enum State {
      FLYING,
      HOOKED_IN_ENTITY,
      BOBBING;
   }
}

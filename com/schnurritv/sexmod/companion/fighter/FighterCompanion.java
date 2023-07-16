package com.schnurritv.sexmod.companion.fighter;

import com.google.common.collect.Multimap;
import com.schnurritv.sexmod.companion.CompanionBase;
import com.schnurritv.sexmod.girls.base.Fighter;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.util.Reference;
import java.util.Iterator;
import java.util.List;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FighterCompanion extends CompanionBase {
   Fighter girl;
   EntityLivingBase target;
   Entity ridingEntity;
   double lastDistance = 3.4028234663852886E38D;
   Vec3d lastMasterPos;
   int idlePosChangeTick;
   int attackModeCoolDown;
   int attackCoolDown;
   int chargingTicks;
   int shouldntFollowAnymoreTick;

   public FighterCompanion(Fighter girl) {
      super(girl);
      this.lastMasterPos = Vec3d.field_186680_a;
      this.idlePosChangeTick = 0;
      this.attackModeCoolDown = 0;
      this.attackCoolDown = 0;
      this.chargingTicks = 0;
      this.shouldntFollowAnymoreTick = 0;
      this.girl = girl;
   }

   public void func_75246_d() {
      super.func_75246_d();
      this.lastDistance = (double)this.girl.func_70032_d(this.master);
      this.lastMasterPos = this.master.func_174791_d();
      if (this.girl.currentAction() == GirlEntity.Action.BOW) {
         this.girl.setCurrentAction(GirlEntity.Action.NULL);
      }

   }

   boolean shouldAttackTarget(EntityLivingBase target) {
      Vec3d girlPos = this.girl.func_174791_d();
      return this.attackModeCoolDown <= 0 && target != null && target.field_70170_p != null && !this.girl.equals(target) && target.func_70089_S() && girlPos.func_72438_d(this.master.func_174791_d()) < 15.0D && girlPos.func_72438_d(target.func_174791_d()) < 20.0D && !target.equals(this.master);
   }

   protected void evalMode(CompanionBase.Mode mode) {
      double distance;
      Vec3d masterPos;
      switch(mode) {
      case ATTACK:
         this.girl.func_70671_ap().func_75651_a(this.target, 30.0F, 30.0F);
         distance = (double)this.girl.func_70032_d(this.target);
         this.navigator.func_75499_g();
         if (distance < 1.9D && --this.attackCoolDown <= 0) {
            this.attack();
         } else {
            if (this.girl.inventory.getStackInSlot(1).func_77973_b() instanceof ItemBow && this.girl.func_70635_at().func_75522_a(this.target) && ++this.chargingTicks > 0 && distance > 6.0D) {
               this.dataManager.func_187227_b(Fighter.ATTACK_MODE, 2);
               this.girl.setCurrentAction(GirlEntity.Action.BOW);
               if (++this.chargingTicks >= 32) {
                  this.chargingTicks = -20;
                  this.attackTargetWithRangedAttack();
                  this.girl.setCurrentAction(GirlEntity.Action.NULL);
               }

               this.lastDistance = (double)this.girl.func_70032_d(this.master);
               this.lastMasterPos = this.master.func_174791_d();
               return;
            }

            if (distance < 2.0D) {
               this.dataManager.func_187227_b(Fighter.ATTACK_MODE, 1);
               this.navigator.func_75497_a(this.target, 0.5D);
               this.girl.setWalkSpeed(GirlEntity.WalkSpeed.WALK);
            } else {
               this.dataManager.func_187227_b(Fighter.ATTACK_MODE, 1);
               this.navigator.func_75497_a(this.target, 0.7D);
               this.girl.setWalkSpeed(GirlEntity.WalkSpeed.RUN);
            }
         }
         break;
      case FOLLOW:
         this.dataManager.func_187227_b(Fighter.ATTACK_MODE, 0);
         distance = (double)this.girl.func_70032_d(this.master);
         if ((double)this.navigator.func_111269_d() > distance) {
            this.navigator.func_75499_g();
            if (!this.girl.downed) {
               this.navigator.func_75497_a(this.master, 0.5D);
            }
         } else {
            this.tpToPlayer();
         }

         this.idlePosChangeTick = 300;
         this.setMovementSpeed();
         break;
      case IDLE:
         this.dataManager.func_187227_b(Fighter.ATTACK_MODE, 0);
         if (!this.girl.downed) {
            if (++this.idlePosChangeTick > 200 + Reference.RANDOM.nextInt(100)) {
               this.idlePosChangeTick = 0;
               masterPos = this.master.func_174791_d();
               Vec3d idlePos = new Vec3d(masterPos.field_72450_a + 1.0D + (double)(Reference.RANDOM.nextFloat() * 3.0F), masterPos.field_72448_b, masterPos.field_72449_c + 1.0D + (double)(Reference.RANDOM.nextFloat() * 3.0F));
               this.navigator.func_75499_g();
               this.navigator.func_75492_a(idlePos.field_72450_a, idlePos.field_72448_b, idlePos.field_72449_c, 0.5D);
            }

            this.setMovementSpeed();
         } else if (this.girl.func_70032_d(this.master) > 10.0F) {
            this.tpToPlayer();
         }
         break;
      case RIDE:
         if (this.girl.func_184218_aH()) {
            this.girl.setCurrentAction(GirlEntity.Action.SIT);
         } else {
            this.girl.func_189654_d(true);
            this.girl.field_70145_X = true;
            masterPos = this.master.func_174791_d().func_178786_a(this.ridingEntity.func_70040_Z().field_72450_a * 0.5D, 0.0D, this.ridingEntity.func_70040_Z().field_72449_c * 0.5D);
            this.girl.func_70080_a(masterPos.field_72450_a, masterPos.field_72448_b, masterPos.field_72449_c, 0.0F, 0.0F);
            this.girl.field_70159_w = 0.0D;
            this.girl.field_70181_x = 0.0D;
            this.girl.field_70179_y = 0.0D;
            this.girl.setCurrentAction(GirlEntity.Action.RIDE);
         }
         break;
      case DOWNED:
         this.navigator.func_75499_g();
      }

   }

   protected CompanionBase.Mode updateMode() {
      --this.attackModeCoolDown;
      if (!this.girl.downed && this.girl.playerSheHasSexWith() == null) {
         if (this.master.func_184218_aH()) {
            Entity ridingEntity = this.master.func_184187_bx();
            if (this.girl.func_184218_aH() || this.girl.func_184220_m(ridingEntity) || ridingEntity instanceof EntityHorse && ((EntityHorse)ridingEntity).func_110257_ck()) {
               this.ridingEntity = ridingEntity;
               return CompanionBase.Mode.RIDE;
            }
         } else if (!this.master.func_184218_aH() && this.girl.func_184218_aH() || this.currentMode == CompanionBase.Mode.RIDE && !this.master.func_184218_aH()) {
            this.girl.setCurrentAction(GirlEntity.Action.NULL);
            this.girl.func_184210_p();
            this.girl.field_70145_X = false;
            this.girl.func_189654_d(false);
         }

         if (this.shouldAttackTarget(this.target)) {
            return CompanionBase.Mode.ATTACK;
         } else {
            DamageSource source = this.girl.func_189748_bU();
            EntityLivingBase newTarget;
            if (source != null) {
               newTarget = (EntityLivingBase)source.func_76346_g();
               if (this.shouldAttackTarget(newTarget)) {
                  this.target = newTarget;
                  return CompanionBase.Mode.ATTACK;
               }
            }

            newTarget = this.master.func_110144_aD();
            if (this.master.field_70173_aa - this.master.func_142013_aG() < 140 && this.shouldAttackTarget(newTarget)) {
               this.target = newTarget;
               return CompanionBase.Mode.ATTACK;
            } else {
               if (this.currentMode != CompanionBase.Mode.FOLLOW) {
                  source = this.master.func_189748_bU();
                  if (source != null) {
                     newTarget = (EntityLivingBase)source.func_76346_g();
                     if (this.shouldAttackTarget(newTarget)) {
                        this.target = newTarget;
                        return CompanionBase.Mode.ATTACK;
                     }
                  }

                  Vec3d pos = this.girl.func_174791_d();
                  AxisAlignedBB area = new AxisAlignedBB(pos.field_72450_a - 5.0D, pos.field_72448_b - 2.0D, pos.field_72449_c - 5.0D, pos.field_72450_a + 5.0D, pos.field_72448_b + 2.0D, pos.field_72449_c + 5.0D);
                  List<EntityMob> mobs = this.girl.field_70170_p.func_72872_a(EntityMob.class, area);
                  mobs.sort((o1, o2) -> {
                     double o1Distance = (double)o1.func_70032_d(this.girl);
                     double o2Distance = (double)o2.func_70032_d(this.girl);
                     if (o1Distance == o2Distance) {
                        return 0;
                     } else {
                        return o1Distance < o2Distance ? -1 : 1;
                     }
                  });
                  Iterator var6 = mobs.iterator();

                  while(var6.hasNext()) {
                     EntityMob mob = (EntityMob)var6.next();
                     if (this.shouldAttackTarget(mob) && !(mob instanceof EntityCreeper)) {
                        this.target = mob;
                        return CompanionBase.Mode.ATTACK;
                     }
                  }
               }

               float distance = this.girl.func_70032_d(this.master);
               boolean shouldFollow = distance > 5.0F;
               if (!shouldFollow && this.currentMode == CompanionBase.Mode.FOLLOW) {
                  if (++this.shouldntFollowAnymoreTick > 60) {
                     shouldFollow = false;
                     this.shouldntFollowAnymoreTick = 0;
                  } else {
                     shouldFollow = true;
                  }
               }

               if (shouldFollow && this.currentMode == CompanionBase.Mode.ATTACK) {
                  this.attackModeCoolDown = 60;
               }

               return shouldFollow ? CompanionBase.Mode.FOLLOW : CompanionBase.Mode.IDLE;
            }
         }
      } else {
         return CompanionBase.Mode.DOWNED;
      }
   }

   public void attackTargetWithRangedAttack() {
      EntityArrow entityarrow = this.getArrow();
      double d0 = this.target.field_70165_t - this.girl.field_70165_t;
      double d1 = this.target.func_174813_aQ().field_72338_b + (double)(this.target.field_70131_O / 3.0F) - entityarrow.field_70163_u;
      double d2 = this.target.field_70161_v - this.girl.field_70161_v;
      double d3 = (double)MathHelper.func_76133_a(d0 * d0 + d2 * d2);
      entityarrow.func_70186_c(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, 2.0F);
      this.girl.func_184185_a(SoundEvents.field_187866_fi, 1.0F, 1.0F / (this.girl.func_70681_au().nextFloat() * 0.4F + 0.8F));
      this.girl.field_70170_p.func_72838_d(entityarrow);
      entityarrow.func_70239_b(4.5D);
   }

   protected EntityArrow getArrow() {
      EntityArrow arrow = new EntityTippedArrow(this.girl.field_70170_p, this.girl);
      ItemStack bow = this.girl.inventory.getStackInSlot(1);
      double power = (double)EnchantmentHelper.func_77506_a(Enchantments.field_185309_u, bow);
      int punch = EnchantmentHelper.func_77506_a(Enchantments.field_185310_v, bow);
      int fire = EnchantmentHelper.func_77506_a(Enchantments.field_185311_w, bow);
      if (power != 0.0D) {
         arrow.func_70239_b(arrow.func_70242_d() + power * 0.5D + 0.5D);
      }

      if (punch != 0) {
         arrow.func_70240_a(punch);
      }

      if (fire != 0) {
         arrow.func_70015_d(100);
      }

      return arrow;
   }

   void attack() {
      this.girl.setCurrentAction(GirlEntity.Action.ATTACK);
      this.dataManager.func_187227_b(Fighter.ATTACK_MODE, 1);
      ItemStack weapon = this.girl.inventory.getStackInSlot(0);
      Multimap<String, AttributeModifier> modifiers = weapon.func_111283_C(EntityEquipmentSlot.MAINHAND);
      float damage = 0.0F;
      float attackSpeed = 0.0F;

      Iterator var5;
      AttributeModifier modifier;
      for(var5 = modifiers.get(SharedMonsterAttributes.field_111264_e.func_111108_a()).iterator(); var5.hasNext(); damage = (float)modifier.func_111164_d()) {
         modifier = (AttributeModifier)var5.next();
      }

      for(var5 = modifiers.get(SharedMonsterAttributes.field_188790_f.func_111108_a()).iterator(); var5.hasNext(); attackSpeed = (float)modifier.func_111164_d()) {
         modifier = (AttributeModifier)var5.next();
      }

      attackSpeed = Math.max(attackSpeed, 0.5F);
      float extraDamage = EnchantmentHelper.func_152377_a(weapon, this.target.func_70668_bt());
      int knockBack = EnchantmentHelper.func_77506_a(Enchantments.field_180313_o, weapon);
      int fireAspect = EnchantmentHelper.func_77506_a(Enchantments.field_77334_n, weapon);
      int sweeping = EnchantmentHelper.func_77506_a(Enchantments.field_191530_r, weapon);
      this.target.func_70653_a(this.girl, (float)knockBack * 0.5F, (double)MathHelper.func_76126_a(this.girl.field_70177_z * 0.017453292F), (double)(-MathHelper.func_76134_b(this.girl.field_70177_z * 0.017453292F)));
      this.target.func_70015_d(fireAspect * 4);
      if (sweeping != 0) {
         float percentage = 0.5F;
         if (sweeping == 2) {
            percentage = 0.67F;
         } else if (sweeping == 3) {
            percentage = 0.75F;
         }

         Iterator var10 = this.girl.field_70170_p.func_72872_a(EntityLivingBase.class, this.target.func_174813_aQ().func_72314_b(1.0D, 0.25D, 1.0D)).iterator();

         while(var10.hasNext()) {
            EntityLivingBase entitylivingbase = (EntityLivingBase)var10.next();
            if (entitylivingbase != this.girl && entitylivingbase != this.master && entitylivingbase != this.target && !this.girl.func_184191_r(entitylivingbase) && this.girl.func_70068_e(entitylivingbase) < 9.0D) {
               entitylivingbase.func_70653_a(this.girl, 0.4F, (double)MathHelper.func_76126_a(this.girl.field_70177_z * 0.017453292F), (double)(-MathHelper.func_76134_b(this.girl.field_70177_z * 0.017453292F)));
               entitylivingbase.func_70097_a(DamageSource.func_76358_a(this.girl), (damage + extraDamage) * percentage);
            }
         }
      }

      this.target.func_70097_a(DamageSource.func_76358_a(this.girl), damage + extraDamage);
      this.attackCoolDown = Math.round(Math.abs(attackSpeed) / 3.373494F * 20.0F);
   }

   protected double setMovementSpeed() {
      double speed = super.setMovementSpeed();
      if (this.girl.downed) {
         speed = 0.0D;
      }

      this.navigator.func_75489_a(speed);
      this.girl.setWalkSpeed(this.girl.getWalkSpeed());
      return speed;
   }

   public void func_75251_c() {
      super.func_75251_c();
      this.girl.func_184212_Q().func_187227_b(Fighter.ATTACK_MODE, 0);
   }

   @EventBusSubscriber
   public static class EventHandler {
      @SubscribeEvent
      public void downed(LivingHurtEvent event) {
         if (event.getEntityLiving() instanceof Fighter) {
            Fighter girl = (Fighter)event.getEntityLiving();
            if (girl.downed) {
               event.setCanceled(true);
            } else if (girl.func_110143_aJ() - event.getAmount() < 0.0F && !((String)girl.func_184212_Q().func_187225_a(Fighter.MASTER)).equals("")) {
               girl.downed = true;
               girl.setCurrentAction(GirlEntity.Action.DOWNED);
               event.setAmount(girl.func_110143_aJ() - 1.0F);
               girl.func_70661_as().func_75499_g();
            }
         }

      }

      @SubscribeEvent
      public void healDowned(LivingHealEvent event) {
         if (event.getEntityLiving() instanceof Fighter) {
            Fighter girl = (Fighter)event.getEntityLiving();
            if (girl.downed && girl.func_110143_aJ() + event.getAmount() >= girl.func_110138_aP()) {
               girl.downed = false;
               girl.setCurrentAction(GirlEntity.Action.NULL);
            }
         }

      }

      @SubscribeEvent
      public void dropItems(LivingDeathEvent event) {
         if (event.getEntityLiving() instanceof Fighter) {
            Fighter girl = (Fighter)event.getEntityLiving();
            if (girl.field_70170_p.field_72995_K) {
               return;
            }

            for(int i = 0; i < 6; ++i) {
               Item item = girl.inventory.getStackInSlot(i).func_77973_b();
               if (item != Items.field_190931_a) {
                  girl.func_145779_a(item, 1);
               }
            }
         }

      }
   }
}

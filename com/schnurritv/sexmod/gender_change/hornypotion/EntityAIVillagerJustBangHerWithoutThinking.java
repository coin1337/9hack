package com.schnurritv.sexmod.gender_change.hornypotion;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;

public class EntityAIVillagerJustBangHerWithoutThinking extends EntityAIBase {
   private final EntityVillager villager;
   private EntityVillager mate;
   private final World world;
   private int matingTimeout;

   public EntityAIVillagerJustBangHerWithoutThinking(EntityVillager villagerIn) {
      this.villager = villagerIn;
      this.world = villagerIn.field_70170_p;
      this.func_75248_a(3);
   }

   public boolean func_75250_a() {
      if (this.matingTimeout != 0) {
         return false;
      } else {
         Entity entity = this.world.func_72857_a(EntityVillager.class, this.villager.func_174813_aQ().func_72314_b(8.0D, 3.0D, 8.0D), this.villager);
         if (entity == null) {
            return false;
         } else {
            this.mate = (EntityVillager)entity;
            return true;
         }
      }
   }

   public void func_75249_e() {
      this.matingTimeout = 300;
      this.villager.func_70947_e(true);
   }

   public void func_75251_c() {
   }

   public boolean func_75253_b() {
      return true;
   }

   public void func_75246_d() {
      --this.matingTimeout;
      this.villager.func_70671_ap().func_75651_a(this.mate, 10.0F, 30.0F);
      if (this.villager.func_70068_e(this.mate) > 2.25D) {
         this.villager.func_70661_as().func_75497_a(this.mate, 0.25D);
      }

      if (this.matingTimeout <= 0) {
         this.giveBirth();
         this.villager.field_70714_bg.func_85156_a(this);
      }

      if (this.villager.func_70681_au().nextInt(35) == 0) {
         this.world.func_72960_a(this.villager, (byte)12);
      }

   }

   private void giveBirth() {
      EntityAgeable entityvillager = this.villager.func_90011_a(this.mate);
      this.mate.func_70873_a(6000);
      this.villager.func_70873_a(6000);
      this.mate.func_175549_o(false);
      this.villager.func_175549_o(false);
      BabyEntitySpawnEvent event = new BabyEntitySpawnEvent(this.villager, this.mate, entityvillager);
      if (!MinecraftForge.EVENT_BUS.post(event) && event.getChild() != null) {
         EntityAgeable entityvillager = event.getChild();
         entityvillager.func_70873_a(-24000);
         entityvillager.func_70012_b(this.villager.field_70165_t, this.villager.field_70163_u, this.villager.field_70161_v, 0.0F, 0.0F);
         this.world.func_72838_d(entityvillager);
         this.world.func_72960_a(entityvillager, (byte)12);
      }
   }
}

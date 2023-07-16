package com.schnurritv.sexmod.gender_change.hornypotion;

import com.schnurritv.sexmod.gui.GenderChange.GenderChangeUI;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber
public class HornyPotion extends Potion {
   public static final Potion HORNY_EFFECT = new HornyPotion("horny potion", false, 16736968, 0, 0);
   public static final PotionType HORNY_POTION;

   public HornyPotion() {
      super(false, 0);
   }

   public HornyPotion(String name, boolean isBadPotion, int color, int iconIndexX, int iconIndexY) {
      super(isBadPotion, color);
      this.func_76390_b(name);
      this.func_76399_b(iconIndexX, iconIndexY);
      this.setRegistryName(new ResourceLocation("sexmod:" + name));
   }

   public static void registerHornyPotion() {
      ForgeRegistries.POTIONS.register(HORNY_EFFECT);
      ForgeRegistries.POTION_TYPES.register(HORNY_POTION);
      PotionHelper.func_193357_a(PotionTypes.field_185231_c, Item.func_150898_a(Blocks.field_150328_O), HORNY_POTION);
   }

   @SubscribeEvent
   public void removeHornyFromPlayer(PlayerTickEvent event) {
      PotionEffect effect = event.player.func_70660_b(HORNY_EFFECT);
      if (effect != null) {
         if (effect.func_76459_b() == 3500 && event.player.field_70170_p.field_72995_K) {
            if (!event.player.getPersistentID().equals(Minecraft.func_71410_x().field_71439_g.getPersistentID())) {
               return;
            }

            this.openGenderChangeUI();
         }

         if (effect.func_76459_b() < 3450 && !event.player.field_70170_p.field_72995_K) {
            event.player.func_184589_d(HORNY_EFFECT);
         }

      }
   }

   @SideOnly(Side.CLIENT)
   void openGenderChangeUI() {
      Minecraft.func_71410_x().func_147108_a(new GenderChangeUI());
   }

   @SubscribeEvent
   public void makeAnimalsHorny(LivingUpdateEvent event) {
      if (event.getEntity() instanceof EntityVillager) {
         EntityVillager villager = (EntityVillager)event.getEntity();
         if (villager.func_70644_a(HORNY_EFFECT)) {
            villager.field_70714_bg.func_75776_a(2, new EntityAIVillagerJustBangHerWithoutThinking(villager));
            villager.func_184589_d(HORNY_EFFECT);
         }
      }

      if (event.getEntity() instanceof EntityAnimal) {
         EntityAnimal entity = (EntityAnimal)event.getEntity();
         if (entity.func_70644_a(HORNY_EFFECT)) {
            if (entity.func_70874_b() >= 0) {
               entity.func_70873_a(0);
               entity.func_70875_t();
               entity.func_146082_f(entity.field_70170_p.func_72890_a(entity, 30.0D));
            }

            entity.func_184589_d(HORNY_EFFECT);
         }

      }
   }

   static {
      HORNY_POTION = (PotionType)(new PotionType("horny_potion", new PotionEffect[]{new PotionEffect(HORNY_EFFECT, 3600), new PotionEffect(MobEffects.field_76431_k, 200, 1)})).setRegistryName("horny_potion");
   }
}

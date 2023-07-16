package com.schnurritv.sexmod.companion.fighter;

import com.schnurritv.sexmod.girls.base.Fighter;
import com.schnurritv.sexmod.util.Reference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class DamageCalculation {
   public DamageCalculation() {
      DamageCalculation.Armor.addArmor(EntityEquipmentSlot.HEAD, ArmorMaterial.LEATHER, 1, 0);
      DamageCalculation.Armor.addArmor(EntityEquipmentSlot.HEAD, ArmorMaterial.GOLD, 2, 0);
      DamageCalculation.Armor.addArmor(EntityEquipmentSlot.HEAD, ArmorMaterial.CHAIN, 2, 0);
      DamageCalculation.Armor.addArmor(EntityEquipmentSlot.HEAD, ArmorMaterial.IRON, 2, 0);
      DamageCalculation.Armor.addArmor(EntityEquipmentSlot.HEAD, ArmorMaterial.DIAMOND, 3, 3);
      DamageCalculation.Armor.addArmor(EntityEquipmentSlot.CHEST, ArmorMaterial.LEATHER, 3, 0);
      DamageCalculation.Armor.addArmor(EntityEquipmentSlot.CHEST, ArmorMaterial.GOLD, 5, 0);
      DamageCalculation.Armor.addArmor(EntityEquipmentSlot.CHEST, ArmorMaterial.CHAIN, 5, 0);
      DamageCalculation.Armor.addArmor(EntityEquipmentSlot.CHEST, ArmorMaterial.IRON, 6, 0);
      DamageCalculation.Armor.addArmor(EntityEquipmentSlot.CHEST, ArmorMaterial.DIAMOND, 8, 3);
      DamageCalculation.Armor.addArmor(EntityEquipmentSlot.LEGS, ArmorMaterial.LEATHER, 2, 0);
      DamageCalculation.Armor.addArmor(EntityEquipmentSlot.LEGS, ArmorMaterial.GOLD, 3, 0);
      DamageCalculation.Armor.addArmor(EntityEquipmentSlot.LEGS, ArmorMaterial.CHAIN, 4, 0);
      DamageCalculation.Armor.addArmor(EntityEquipmentSlot.LEGS, ArmorMaterial.IRON, 5, 0);
      DamageCalculation.Armor.addArmor(EntityEquipmentSlot.LEGS, ArmorMaterial.DIAMOND, 6, 3);
      DamageCalculation.Armor.addArmor(EntityEquipmentSlot.FEET, ArmorMaterial.LEATHER, 1, 0);
      DamageCalculation.Armor.addArmor(EntityEquipmentSlot.FEET, ArmorMaterial.GOLD, 1, 0);
      DamageCalculation.Armor.addArmor(EntityEquipmentSlot.FEET, ArmorMaterial.CHAIN, 1, 0);
      DamageCalculation.Armor.addArmor(EntityEquipmentSlot.FEET, ArmorMaterial.IRON, 2, 0);
      DamageCalculation.Armor.addArmor(EntityEquipmentSlot.FEET, ArmorMaterial.DIAMOND, 3, 3);
   }

   @SubscribeEvent
   public void calculateDamage(LivingDamageEvent event) {
      if (event.getEntity() instanceof Fighter) {
         Fighter girl = (Fighter)event.getEntity();
         ItemStack[] potentialArmors = new ItemStack[]{girl.inventory.getStackInSlot(2), girl.inventory.getStackInSlot(3), girl.inventory.getStackInSlot(4), girl.inventory.getStackInSlot(5)};
         List<ItemArmor> armorItems = new ArrayList();
         List<ItemStack> armorStacks = new ArrayList();
         ItemStack[] var6 = potentialArmors;
         int defencePoints = potentialArmors.length;

         int toughness;
         for(toughness = 0; toughness < defencePoints; ++toughness) {
            ItemStack potentialArmor = var6[toughness];
            if (potentialArmor.func_77973_b() instanceof ItemArmor) {
               armorItems.add((ItemArmor)potentialArmor.func_77973_b());
               armorStacks.add(potentialArmor);
            }
         }

         if (armorItems.size() == 0) {
            return;
         }

         DamageSource source = event.getSource();
         defencePoints = 0;
         toughness = 0;
         ItemArmor armor;
         if (!source.func_76363_c()) {
            for(Iterator var18 = armorItems.iterator(); var18.hasNext(); toughness += DamageCalculation.Armor.getToughness(armor.field_77881_a, armor.func_82812_d())) {
               armor = (ItemArmor)var18.next();
               defencePoints += DamageCalculation.Armor.getDefensePoints(armor.field_77881_a, armor.func_82812_d());
            }
         }

         float damage = event.getAmount();
         damage *= 1.0F - Math.min(20.0F, Math.max((float)defencePoints / 5.0F, (float)defencePoints - 4.0F * damage / ((float)toughness + 8.0F))) / 25.0F;
         float thornDamage = 0.0F;
         Iterator var12 = armorStacks.iterator();

         while(var12.hasNext()) {
            ItemStack stack = (ItemStack)var12.next();
            int protection = EnchantmentHelper.func_77506_a(Enchantments.field_180310_c, stack);
            damage -= (float)protection * 0.04F * damage;
            int thorns = EnchantmentHelper.func_77506_a(Enchantments.field_92091_k, stack);
            thornDamage += Reference.RANDOM.nextFloat() < 0.15F * (float)thorns ? Reference.RANDOM.nextFloat() * 4.0F + 1.0F : 0.0F;
            thornDamage = Math.min(4.0F, thornDamage);
            int projectileProtection;
            if (source.func_76347_k()) {
               projectileProtection = EnchantmentHelper.func_77506_a(Enchantments.field_77329_d, stack);
               damage -= (float)projectileProtection * 0.08F * damage;
            }

            if (source.func_94541_c()) {
               projectileProtection = EnchantmentHelper.func_77506_a(Enchantments.field_185297_d, stack);
               damage -= (float)projectileProtection * 0.08F * damage;
            }

            if (source.field_76373_n.equals("fall")) {
               projectileProtection = EnchantmentHelper.func_77506_a(Enchantments.field_180309_e, stack);
               damage -= (float)projectileProtection * 0.12F * damage;
            }

            if (source.func_76352_a()) {
               projectileProtection = EnchantmentHelper.func_77506_a(Enchantments.field_180308_g, stack);
               damage -= (float)projectileProtection * 0.08F * damage;
            }
         }

         if (thornDamage > 0.0F && source instanceof EntityDamageSource) {
            EntityDamageSource entityDamageSource = (EntityDamageSource)source;
            if (entityDamageSource.func_76346_g() != null) {
               entityDamageSource.func_76346_g().func_70097_a(DamageSource.func_92087_a(girl), thornDamage);
            }

            System.out.println(thornDamage);
         }

         event.setAmount(damage);
      }

   }

   static class Armor {
      public static HashMap<String, Integer[]> stats = new HashMap();

      public static int getDefensePoints(EntityEquipmentSlot slot, ArmorMaterial material) {
         try {
            return ((Integer[])stats.get(slot.toString() + material.toString()))[0];
         } catch (NullPointerException var3) {
            return 3;
         }
      }

      public static int getToughness(EntityEquipmentSlot slot, ArmorMaterial material) {
         try {
            return ((Integer[])stats.get(slot.toString() + material.toString()))[1];
         } catch (NullPointerException var3) {
            return 0;
         }
      }

      public static void addArmor(EntityEquipmentSlot slot, ArmorMaterial material, int defensePoints, int toughness) {
         stats.put(slot.toString() + material.toString(), new Integer[]{defensePoints, toughness});
      }
   }
}

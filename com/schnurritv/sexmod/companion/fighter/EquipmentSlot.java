package com.schnurritv.sexmod.companion.fighter;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class EquipmentSlot extends SlotItemHandler {
   EquipmentSlot.Slot slot;

   public EquipmentSlot(EquipmentSlot.Slot slot, IItemHandler inventoryIn, int index, int xPosition, int yPosition) {
      super(inventoryIn, index, xPosition, yPosition);
      this.slot = slot;
   }

   public static boolean isItemValidForSlot(ItemStack stack, int id) {
      return isIemValidForSlot(stack, EquipmentSlot.Slot.getSlotById(id));
   }

   public boolean func_75214_a(ItemStack stack) {
      return isIemValidForSlot(stack, this.slot);
   }

   static boolean isIemValidForSlot(ItemStack stack, EquipmentSlot.Slot slot) {
      Item item = stack.func_77973_b();
      switch(slot) {
      case WEAPON:
         return item instanceof ItemSword || item instanceof ItemTool;
      case BOW:
         return item instanceof ItemBow;
      case HELMET:
         return item instanceof ItemArmor && ((ItemArmor)item).field_77881_a == EntityEquipmentSlot.HEAD;
      case CHEST_PLATE:
         return item instanceof ItemArmor && ((ItemArmor)item).field_77881_a == EntityEquipmentSlot.CHEST;
      case PANTS:
         return item instanceof ItemArmor && ((ItemArmor)item).field_77881_a == EntityEquipmentSlot.LEGS;
      case SHOES:
         return item instanceof ItemArmor && ((ItemArmor)item).field_77881_a == EntityEquipmentSlot.FEET;
      default:
         return false;
      }
   }

   public static enum Slot {
      WEAPON(0),
      BOW(1),
      HELMET(2),
      CHEST_PLATE(3),
      PANTS(4),
      SHOES(5);

      public int id;

      public static EquipmentSlot.Slot getSlotById(int id) {
         switch(id) {
         case 0:
            return WEAPON;
         case 1:
            return BOW;
         case 2:
            return HELMET;
         case 3:
            return CHEST_PLATE;
         case 4:
            return PANTS;
         case 5:
            return SHOES;
         default:
            throw new NullPointerException("Girls don't have a slot nr. " + id);
         }
      }

      private Slot(int id) {
         this.id = id;
      }
   }
}

package com.schnurritv.sexmod.gui.menu;

import com.schnurritv.sexmod.companion.fighter.EquipmentSlot;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class GirlContainer extends Container {
   GirlEntity girl;
   public Slot[] equipmentSlots;
   public UUID containerId;
   public static List<GirlContainer> list = new ArrayList();

   public GirlContainer(GirlEntity girl, InventoryPlayer inventoryPlayer, UUID containerId) {
      this.containerId = containerId;
      list.add(this);
      if (girl.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH)) {
         IItemHandler inventory = (IItemHandler)girl.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
         this.girl = girl;
         this.equipmentSlots = new Slot[]{new EquipmentSlot(EquipmentSlot.Slot.WEAPON, inventory, EquipmentSlot.Slot.WEAPON.id, 31, 60), new EquipmentSlot(EquipmentSlot.Slot.BOW, inventory, EquipmentSlot.Slot.BOW.id, 50, 60), new EquipmentSlot(EquipmentSlot.Slot.HELMET, inventory, EquipmentSlot.Slot.HELMET.id, 72, 60), new EquipmentSlot(EquipmentSlot.Slot.CHEST_PLATE, inventory, EquipmentSlot.Slot.CHEST_PLATE.id, 91, 60), new EquipmentSlot(EquipmentSlot.Slot.PANTS, inventory, EquipmentSlot.Slot.PANTS.id, 110, 60), new EquipmentSlot(EquipmentSlot.Slot.SHOES, inventory, EquipmentSlot.Slot.SHOES.id, 129, 60)};
         List<Slot> playerSlots = new ArrayList();

         int x;
         int x;
         for(x = 0; x < 3; ++x) {
            for(x = 0; x < 9; ++x) {
               playerSlots.add(new Slot(inventoryPlayer, x + x * 9 + 9, 8 + x * 18, 84 + x * 18));
            }
         }

         for(x = 0; x < 9; ++x) {
            playerSlots.add(new Slot(inventoryPlayer, x, 8 + x * 18, 142));
         }

         Slot[] var10 = this.equipmentSlots;
         x = var10.length;

         for(int var8 = 0; var8 < x; ++var8) {
            Slot slot = var10[var8];
            this.func_75146_a(slot);
         }

         Iterator var11 = playerSlots.iterator();

         while(var11.hasNext()) {
            Slot slot = (Slot)var11.next();
            this.func_75146_a(slot);
         }
      }

   }

   public ItemStack func_82846_b(EntityPlayer player, int index) {
      ItemStack stack = ItemStack.field_190927_a;
      Slot slot = (Slot)this.field_75151_b.get(index);
      if (slot != null && slot.func_75216_d()) {
         ItemStack stackInSlot = slot.func_75211_c();
         stack = stackInSlot.func_77946_l();
         int containerSlots = this.field_75151_b.size() - player.field_71071_by.field_70462_a.size();
         if (index < containerSlots) {
            if (!this.func_75135_a(stackInSlot, containerSlots, this.field_75151_b.size(), true)) {
               return ItemStack.field_190927_a;
            }
         } else if (!this.func_75135_a(stackInSlot, 0, containerSlots, false)) {
            return ItemStack.field_190927_a;
         }

         if (stackInSlot.func_190916_E() == 0) {
            slot.func_75215_d(ItemStack.field_190927_a);
         } else {
            slot.func_75218_e();
         }

         slot.func_190901_a(player, stackInSlot);
      }

      return stack;
   }

   public void func_75141_a(int slotID, ItemStack stack) {
      super.func_75141_a(slotID, stack);
   }

   public boolean func_75145_c(EntityPlayer playerIn) {
      return true;
   }

   public void func_75134_a(EntityPlayer playerIn) {
      super.func_75134_a(playerIn);
   }
}

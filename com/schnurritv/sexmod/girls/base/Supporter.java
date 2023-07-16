package com.schnurritv.sexmod.girls.base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

public abstract class Supporter extends GirlEntity implements IInventory {
   public static final DataParameter<Boolean> HAS_CHEST;
   public ItemStackHandler inventory = new ItemStackHandler(27);

   protected Supporter(World worldIn) {
      super(worldIn);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(HAS_CHEST, false);
   }

   public int func_70302_i_() {
      return 27;
   }

   public boolean func_191420_l() {
      return false;
   }

   public ItemStack func_70301_a(int index) {
      return this.inventory.getStackInSlot(index);
   }

   public ItemStack func_70298_a(int index, int count) {
      return this.inventory.extractItem(index, count, false);
   }

   public ItemStack func_70304_b(int index) {
      return this.inventory.extractItem(index, this.inventory.getStackInSlot(index).func_190916_E(), false);
   }

   public void func_70299_a(int index, ItemStack stack) {
      this.inventory.setStackInSlot(index, stack);
   }

   public int func_70297_j_() {
      return 27;
   }

   public void func_70296_d() {
   }

   public boolean func_70300_a(EntityPlayer player) {
      return true;
   }

   public void func_174889_b(EntityPlayer player) {
   }

   public void func_174886_c(EntityPlayer player) {
   }

   public boolean func_94041_b(int index, ItemStack stack) {
      return true;
   }

   public int func_174887_a_(int id) {
      return id;
   }

   public void func_174885_b(int id, int value) {
   }

   public int func_174890_g() {
      return 27;
   }

   public void func_174888_l() {
   }

   static {
      HAS_CHEST = EntityDataManager.func_187226_a(GirlEntity.class, DataSerializers.field_187198_h).func_187156_b().func_187161_a(75);
   }
}

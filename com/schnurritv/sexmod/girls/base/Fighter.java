package com.schnurritv.sexmod.girls.base;

import com.schnurritv.sexmod.Packets.OpenGirlInventory;
import com.schnurritv.sexmod.Packets.SendCompanionHome;
import com.schnurritv.sexmod.Packets.SetNewHome;
import com.schnurritv.sexmod.companion.fighter.FighterCompanion;
import com.schnurritv.sexmod.util.Reference;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public abstract class Fighter extends GirlEntity {
   public int nextAttack = 1;
   public int slashSwordRot;
   public int stabSwordRot;
   public int holdBowRot;
   public Vec3d swordOffsetStab;
   public boolean downed;
   public ItemStackHandler inventory = new ItemStackHandler(6);
   public static final DataParameter<ItemStack> WEAPON;
   public static final DataParameter<ItemStack> BOW;
   public static final DataParameter<ItemStack> HELMET;
   public static final DataParameter<ItemStack> CHEST_PLATE;
   public static final DataParameter<ItemStack> PANTS;
   public static final DataParameter<ItemStack> SHOES;
   public static final DataParameter<Integer> ATTACK_MODE;

   protected Fighter(World worldIn) {
      super(worldIn);
      this.inventory.setStackInSlot(0, new ItemStack(Items.field_151040_l));
      this.inventory.setStackInSlot(1, new ItemStack(Items.field_151031_f));
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(ATTACK_MODE, 0);
      this.field_70180_af.func_187214_a(WEAPON, ItemStack.field_190927_a);
      this.field_70180_af.func_187214_a(BOW, ItemStack.field_190927_a);
      this.field_70180_af.func_187214_a(HELMET, ItemStack.field_190927_a);
      this.field_70180_af.func_187214_a(CHEST_PLATE, ItemStack.field_190927_a);
      this.field_70180_af.func_187214_a(PANTS, ItemStack.field_190927_a);
      this.field_70180_af.func_187214_a(SHOES, ItemStack.field_190927_a);
   }

   protected void func_184651_r() {
      super.func_184651_r();
      this.field_70714_bg.func_75776_a(1, new FighterCompanion(this));
   }

   public void func_70619_bc() {
      super.func_70619_bc();
      if (this.field_70173_aa % 80 == 0 && this.func_110143_aJ() != this.func_110138_aP()) {
         if (!this.hasMaster()) {
            this.func_70691_i(1.0F);
         } else {
            List<EntityMob> mobs = this.field_70170_p.func_72872_a(EntityMob.class, new AxisAlignedBB(new BlockPos(this.field_70165_t - 7.0D, this.field_70163_u - 1.0D, this.field_70161_v - 7.0D), new BlockPos(this.field_70165_t + 7.0D, this.field_70163_u + 1.0D, this.field_70161_v + 7.0D)));
            int healAmount = mobs.isEmpty() ? 4 : 1;
            this.func_70691_i((float)healAmount);
            ((WorldServer)this.field_70170_p).func_180505_a(EnumParticleTypes.HEART, false, this.field_70165_t, this.field_70163_u + 1.0D + Reference.RANDOM.nextDouble(), this.field_70161_v, healAmount, 1.0D, 1.0D, 1.0D, Reference.RANDOM.nextGaussian(), new int[0]);
         }
      }

      if (this.downed && !this.hasMaster()) {
         this.downed = false;
      }

      this.field_70180_af.func_187227_b(field_184621_as, Byte.valueOf("1"));
      this.field_70180_af.func_187227_b(WEAPON, this.inventory.getStackInSlot(0));
      this.field_70180_af.func_187227_b(BOW, this.inventory.getStackInSlot(1));
      this.field_70180_af.func_187227_b(HELMET, this.inventory.getStackInSlot(2));
      this.field_70180_af.func_187227_b(CHEST_PLATE, this.inventory.getStackInSlot(3));
      this.field_70180_af.func_187227_b(PANTS, this.inventory.getStackInSlot(4));
      this.field_70180_af.func_187227_b(SHOES, this.inventory.getStackInSlot(5));
   }

   @SideOnly(Side.CLIENT)
   public void doAction(String actionName, UUID player) {
      if ("action.names.followme".equals(actionName)) {
         this.changeDataParameterFromClient("master", player.toString());
      } else if ("action.names.stopfollowme".equals(actionName)) {
         this.stopCompanionShip();
      } else if ("action.names.equipment".equals(actionName)) {
         EntityPlayer playerEntity = Minecraft.func_71410_x().field_71439_g;
         PacketHandler.INSTANCE.sendToServer(new OpenGirlInventory(this.girlId(), playerEntity.getPersistentID()));
      } else if ("action.names.gohome".equals(actionName)) {
         PacketHandler.INSTANCE.sendToServer(new SendCompanionHome(this.girlId()));
      } else if ("action.names.setnewhome".equals(actionName)) {
         PacketHandler.INSTANCE.sendToServer(new SetNewHome(this.girlId(), new Vec3d(this.func_180425_c())));
      }

   }

   public void func_70014_b(NBTTagCompound compound) {
      compound.func_74782_a("inventory", this.inventory.serializeNBT());
      super.func_70014_b(compound);
   }

   public void func_70037_a(NBTTagCompound compound) {
      super.func_70037_a(compound);
      this.inventory.deserializeNBT(compound.func_74775_l("inventory"));
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? this.inventory : super.getCapability(capability, facing);
   }

   static {
      WEAPON = EntityDataManager.func_187226_a(GirlEntity.class, DataSerializers.field_187196_f).func_187156_b().func_187161_a(61);
      BOW = EntityDataManager.func_187226_a(GirlEntity.class, DataSerializers.field_187196_f).func_187156_b().func_187161_a(60);
      HELMET = EntityDataManager.func_187226_a(GirlEntity.class, DataSerializers.field_187196_f).func_187156_b().func_187161_a(59);
      CHEST_PLATE = EntityDataManager.func_187226_a(GirlEntity.class, DataSerializers.field_187196_f).func_187156_b().func_187161_a(58);
      PANTS = EntityDataManager.func_187226_a(GirlEntity.class, DataSerializers.field_187196_f).func_187156_b().func_187161_a(57);
      SHOES = EntityDataManager.func_187226_a(GirlEntity.class, DataSerializers.field_187196_f).func_187156_b().func_187161_a(56);
      ATTACK_MODE = EntityDataManager.func_187226_a(GirlEntity.class, DataSerializers.field_187192_b).func_187156_b().func_187161_a(55);
   }
}

package com.schnurritv.sexmod.world;

import com.schnurritv.sexmod.util.Reference;
import java.util.Random;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BiaStructure extends WorldGenStructure {
   public BiaStructure(String name) {
      super(name);
   }

   public boolean func_180709_b(World worldIn, Random rand, BlockPos position) {
      generateStructure(worldIn, position);
      if (Reference.RANDOM.nextInt(5) != 0) {
         return true;
      } else {
         EntityItemFrame frame = new EntityItemFrame(worldIn, position.func_177982_a(2, 2, 6), EnumFacing.EAST);
         ItemStack map = ItemMapSecret.setupNewMap(worldIn, (double)position.func_177958_n(), (double)position.func_177952_p(), (byte)0, false, false);
         worldIn.func_72838_d(frame);
         frame.func_82334_a(map);
         return true;
      }
   }
}

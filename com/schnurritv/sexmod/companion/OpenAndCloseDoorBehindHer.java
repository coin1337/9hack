package com.schnurritv.sexmod.companion;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;

public class OpenAndCloseDoorBehindHer extends EntityAIBase {
   protected EntityLiving entity;
   protected BlockPos doorPosition;
   protected BlockDoor doorBlock;
   boolean hasStoppedDoorInteraction;
   float entityPositionX;
   float entityPositionZ;
   int closeDoorTick;

   public OpenAndCloseDoorBehindHer(EntityLiving entityIn) {
      this.doorPosition = BlockPos.field_177992_a;
      this.closeDoorTick = 10;
      this.entity = entityIn;
      if (!(entityIn.func_70661_as() instanceof PathNavigateGround)) {
         throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
      }
   }

   public boolean func_75250_a() {
      boolean flag = true;

      for(int x = -3; x < 5; ++x) {
         for(int z = -3; z < 5; ++z) {
            IBlockState state = this.entity.field_70170_p.func_180495_p(this.entity.func_180425_c().func_177982_a(x, 0, z));
            if (state.func_177230_c() instanceof BlockDoor && state.func_185904_a() == Material.field_151575_d) {
               flag = false;
               break;
            }
         }

         if (!flag) {
            break;
         }
      }

      if (flag) {
         return false;
      } else {
         PathNavigateGround pathnavigateground = (PathNavigateGround)this.entity.func_70661_as();
         Path path = pathnavigateground.func_75505_d();
         if (path != null && !path.func_75879_b() && pathnavigateground.func_179686_g()) {
            for(int i = 0; i < Math.min(path.func_75873_e() + 2, path.func_75874_d()); ++i) {
               PathPoint pathpoint = path.func_75877_a(i);
               this.doorPosition = new BlockPos(pathpoint.field_75839_a, pathpoint.field_75837_b + 1, pathpoint.field_75838_c);
               if (this.entity.func_70092_e((double)this.doorPosition.func_177958_n(), this.entity.field_70163_u, (double)this.doorPosition.func_177952_p()) <= 2.25D) {
                  this.doorBlock = this.getBlockDoor(this.doorPosition);
                  if (this.doorBlock != null) {
                     return true;
                  }
               }
            }

            this.doorPosition = (new BlockPos(this.entity)).func_177984_a();
            this.doorBlock = this.getBlockDoor(this.doorPosition);
            return this.doorBlock != null;
         } else {
            return false;
         }
      }
   }

   public boolean func_75253_b() {
      return this.closeDoorTick >= 0;
   }

   public void func_75249_e() {
      this.hasStoppedDoorInteraction = false;
      this.entityPositionX = (float)((double)((float)this.doorPosition.func_177958_n() + 0.5F) - this.entity.field_70165_t);
      this.entityPositionZ = (float)((double)((float)this.doorPosition.func_177952_p() + 0.5F) - this.entity.field_70161_v);
      this.doorBlock.func_176512_a(this.entity.field_70170_p, this.doorPosition, true);
   }

   public void func_75246_d() {
      float f = (float)((double)((float)this.doorPosition.func_177958_n() + 0.5F) - this.entity.field_70165_t);
      float f1 = (float)((double)((float)this.doorPosition.func_177952_p() + 0.5F) - this.entity.field_70161_v);
      float f2 = this.entityPositionX * f + this.entityPositionZ * f1;
      if (f2 < 0.0F && --this.closeDoorTick <= 0) {
         this.doorBlock.func_176512_a(this.entity.field_70170_p, this.doorPosition, false);
         this.hasStoppedDoorInteraction = true;
      }

   }

   public void func_75251_c() {
      this.closeDoorTick = 10;
   }

   private BlockDoor getBlockDoor(BlockPos pos) {
      IBlockState iblockstate = this.entity.field_70170_p.func_180495_p(pos);
      Block block = iblockstate.func_177230_c();
      return block instanceof BlockDoor && iblockstate.func_185904_a() == Material.field_151575_d ? (BlockDoor)block : null;
   }
}

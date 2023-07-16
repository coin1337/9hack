package com.schnurritv.sexmod.companion;

import com.schnurritv.sexmod.girls.base.GirlEntity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class CompanionPearl extends EntityEnderPearl {
   public CompanionPearl(World worldIn) {
      super(worldIn);
   }

   public CompanionPearl(World worldIn, EntityLivingBase throwerIn) {
      super(worldIn, throwerIn);
   }

   protected void func_70184_a(RayTraceResult result) {
      EntityLivingBase entitylivingbase = this.func_85052_h();
      if (result.field_72313_a == Type.BLOCK) {
         BlockPos blockpos = result.func_178782_a();
         TileEntity tileentity = this.field_70170_p.func_175625_s(blockpos);
         if (tileentity instanceof TileEntityEndGateway) {
            TileEntityEndGateway tileentityendgateway = (TileEntityEndGateway)tileentity;
            if (entitylivingbase != null) {
               if (entitylivingbase instanceof EntityPlayerMP) {
                  CriteriaTriggers.field_192124_d.func_192193_a((EntityPlayerMP)entitylivingbase, this.field_70170_p.func_180495_p(blockpos));
               }

               tileentityendgateway.func_184306_a(entitylivingbase);
               this.func_70106_y();
               return;
            }

            tileentityendgateway.func_184306_a(this);
            return;
         }
      }

      for(int i = 0; i < 32; ++i) {
         this.field_70170_p.func_175688_a(EnumParticleTypes.PORTAL, this.field_70165_t, this.field_70163_u + this.field_70146_Z.nextDouble() * 2.0D, this.field_70161_v, this.field_70146_Z.nextGaussian(), 0.0D, this.field_70146_Z.nextGaussian(), new int[0]);
      }

      if (!this.field_70170_p.field_72995_K) {
         if (entitylivingbase != null) {
            GirlEntity girl = (GirlEntity)entitylivingbase;
            if (girl.home.func_72438_d(this.func_174791_d()) < 5.0D) {
               EnderTeleportEvent event = new EnderTeleportEvent(entitylivingbase, this.field_70165_t, this.field_70163_u, this.field_70161_v, 5.0F);
               if (!MinecraftForge.EVENT_BUS.post(event)) {
                  if (entitylivingbase.func_184218_aH()) {
                     entitylivingbase.func_184210_p();
                  }

                  entitylivingbase.func_70634_a(this.field_70165_t, this.field_70163_u, this.field_70161_v);
                  entitylivingbase.field_70143_R = 0.0F;
               }
            }
         }

         this.func_70106_y();
      }

   }

   public static class EventHandler {
      @SubscribeEvent
      public void arrive(EnderTeleportEvent event) {
         if (event.getEntityLiving() instanceof GirlEntity) {
            GirlEntity girl = (GirlEntity)event.getEntityLiving();
            girl.pearl = null;
            girl.setCurrentAction(GirlEntity.Action.NULL);
            girl.func_184212_Q().func_187227_b(GirlEntity.SHOULD_BE_AT_TARGET, false);
            girl.stopCompanionShip();
         }

      }
   }
}

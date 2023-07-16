package com.schnurritv.sexmod.events;

import com.schnurritv.sexmod.girls.allie.PlayerAllie;
import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirl;
import com.schnurritv.sexmod.girls.bee.PlayerBee;
import com.schnurritv.sexmod.girls.slime.PlayerSlime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PlayerGirlInteractions {
   public static boolean allowRape = false;

   @SubscribeEvent
   public void interactBed(PlayerInteractEvent event) {
      PlayerGirl girl = (PlayerGirl)PlayerGirl.playerGirlTable.get(event.getEntityPlayer().getPersistentID());
      BlockPos bedPos = event.getPos();
      World world = event.getWorld();
      EntityPlayer player = event.getEntityPlayer();
      if (girl != null) {
         if (girl.hasBedAnimation()) {
            if (world.func_180495_p(bedPos).func_177230_c() == Blocks.field_150324_C) {
               if (player.func_70093_af()) {
                  List<BlockPos> potentialSpots = new ArrayList();
                  if (world.func_180495_p(bedPos.func_177978_c()).func_177230_c() == Blocks.field_150350_a) {
                     potentialSpots.add(bedPos.func_177978_c());
                  }

                  if (world.func_180495_p(bedPos.func_177974_f()).func_177230_c() == Blocks.field_150350_a) {
                     potentialSpots.add(bedPos.func_177974_f());
                  }

                  if (world.func_180495_p(bedPos.func_177968_d()).func_177230_c() == Blocks.field_150350_a) {
                     potentialSpots.add(bedPos.func_177968_d());
                  }

                  if (world.func_180495_p(bedPos.func_177976_e()).func_177230_c() == Blocks.field_150350_a) {
                     potentialSpots.add(bedPos.func_177976_e());
                  }

                  BlockPos spot = null;
                  Iterator var8 = potentialSpots.iterator();

                  while(var8.hasNext()) {
                     BlockPos pos = (BlockPos)var8.next();
                     if (spot == null) {
                        spot = pos;
                     } else {
                        double potDist = pos.func_185332_f((int)player.field_70165_t, (int)player.field_70163_u, (int)player.field_70161_v);
                        double spotDist = spot.func_185332_f((int)player.field_70165_t, (int)player.field_70163_u, (int)player.field_70161_v);
                        if (potDist < spotDist) {
                           spot = pos;
                        }
                     }
                  }

                  if (spot == null) {
                     player.func_145747_a(new TextComponentString("Bed is obscured"));
                  } else {
                     player.func_70107_b((double)spot.func_177958_n() + 0.5D, (double)spot.func_177956_o(), (double)spot.func_177952_p() + 0.5D);
                     girl.setTargetPos(new Vec3d((double)spot.func_177958_n() + 0.5D, (double)((float)spot.func_177956_o() + 255.0F), (double)spot.func_177952_p() + 0.5D));
                     if (bedPos.func_177978_c().equals(spot)) {
                        player.field_70177_z = 0.0F;
                     }

                     if (bedPos.func_177974_f().equals(spot)) {
                        player.field_70177_z = 90.0F;
                     }

                     if (bedPos.func_177968_d().equals(spot)) {
                        player.field_70177_z = 180.0F;
                     }

                     if (bedPos.func_177976_e().equals(spot)) {
                        player.field_70177_z = -90.0F;
                     }

                     girl.setTargetYaw(player.field_70177_z);
                     if (world.field_72995_K && Minecraft.func_71410_x().field_71439_g.getPersistentID().equals(player.getPersistentID())) {
                        HandlePlayerMovement.setActive(false);
                        Minecraft.func_71410_x().field_71474_y.field_74320_O = 1;
                        player.field_70733_aJ = 0.0F;
                     }

                     girl.func_184212_Q().func_187227_b(GirlEntity.SHOULD_BE_AT_TARGET, true);
                     girl.startBedAnimation();
                     if (event.isCancelable()) {
                        event.setCanceled(true);
                     }

                  }
               }
            }
         }
      }
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void interactPlayerGirl(EntityInteract event) {
      if (event.getTarget() instanceof EntityPlayer) {
         if (event.getEntityPlayer().getPersistentID().equals(Minecraft.func_71410_x().field_71439_g.getPersistentID())) {
            EntityPlayer malePlayer = Minecraft.func_71410_x().field_71439_g;
            PlayerGirl maleGirl = (PlayerGirl)PlayerGirl.playerGirlTable.get(malePlayer.getPersistentID());
            EntityPlayer player = (EntityPlayer)event.getTarget();
            PlayerGirl girl = (PlayerGirl)PlayerGirl.playerGirlTable.get(player.getPersistentID());
            if (girl != null) {
               if (maleGirl != null) {
                  malePlayer.func_146105_b(new TextComponentString("no lesbo yet owo"), true);
               } else {
                  girl.openMenu(Minecraft.func_71410_x().field_71439_g);
               }
            }
         }
      }
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void interactPlayerAsGirl(EntityInteract event) {
      if (event.getTarget() instanceof EntityPlayer) {
         if (event.getEntityPlayer().getPersistentID().equals(Minecraft.func_71410_x().field_71439_g.getPersistentID())) {
            EntityPlayer girlPlayer = Minecraft.func_71410_x().field_71439_g;
            PlayerGirl girl = (PlayerGirl)PlayerGirl.playerGirlTable.get(girlPlayer.getPersistentID());
            if (girl != null) {
               EntityPlayer malePlayer = (EntityPlayer)event.getTarget();
               PlayerGirl maleGirl = (PlayerGirl)PlayerGirl.playerGirlTable.get(malePlayer.getPersistentID());
               if (maleGirl != null) {
                  malePlayer.func_146105_b(new TextComponentString("no lesbo yet owo"), true);
               } else {
                  girl.senderIsMale = false;
                  girl.openMenu(malePlayer);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public void activateSlimeGirlDoggy(RightClickBlock event) {
      EntityPlayer player = event.getEntityPlayer();
      PlayerGirl girl = (PlayerGirl)PlayerGirl.playerGirlTable.get(player.getPersistentID());
      if (girl != null) {
         if (girl instanceof PlayerSlime) {
            if (player.func_70093_af()) {
               if (player.func_184614_ca().equals(ItemStack.field_190927_a)) {
                  if (!(Boolean)girl.func_184212_Q().func_187225_a(GirlEntity.SHOULD_BE_AT_TARGET)) {
                     if (!(player.field_70125_A < 20.0F)) {
                        Vec3d hitVec = event.getHitVec();
                        if (hitVec != null) {
                           Vec3d girlServerPos = new Vec3d(hitVec.field_72450_a, Math.floor(hitVec.field_72448_b) + 255.0D, hitVec.field_72449_c);
                           if (!(hitVec.func_72438_d(player.func_174791_d()) > 3.0D)) {
                              player.func_70107_b(girlServerPos.field_72450_a, Math.floor(hitVec.field_72448_b), girlServerPos.field_72449_c);
                              girl.setTargetPos(girlServerPos);
                              girl.setTargetYaw(player.field_70177_z);
                              girl.func_184212_Q().func_187227_b(GirlEntity.SHOULD_BE_AT_TARGET, true);
                              girl.func_184212_Q().func_187227_b(GirlEntity.CURRENT_MODEL, 0);
                              girl.setCurrentAction(GirlEntity.Action.STARTDOGGY);
                              if (event.getWorld().field_72995_K && Minecraft.func_71410_x().field_71439_g.getPersistentID().equals(player.getPersistentID())) {
                                 Minecraft.func_71410_x().field_71474_y.field_74320_O = 1;
                                 HandlePlayerMovement.setActive(false);
                              }

                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public void noFallDamage(LivingHurtEvent event) {
      if (event.getEntityLiving() instanceof EntityPlayer) {
         if (event.getSource() == DamageSource.field_76379_h) {
            EntityPlayer player = (EntityPlayer)event.getEntityLiving();
            PlayerGirl girl = (PlayerGirl)PlayerGirl.playerGirlTable.get(player.getPersistentID());
            if (girl != null) {
               if (girl instanceof PlayerAllie || girl instanceof PlayerBee) {
                  event.setCanceled(true);
               }

            }
         }
      }
   }
}

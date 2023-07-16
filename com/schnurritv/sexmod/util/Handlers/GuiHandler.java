package com.schnurritv.sexmod.util.Handlers;

import com.schnurritv.sexmod.girls.base.GirlEntity;
import com.schnurritv.sexmod.gui.menu.GirlContainer;
import com.schnurritv.sexmod.gui.menu.GirlInventoryUI;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
   public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      if (ID == 0) {
         Iterator var7 = GirlEntity.girlEntities.iterator();

         while(var7.hasNext()) {
            GirlEntity girl = (GirlEntity)var7.next();
            if (!girl.field_70170_p.field_72995_K && girl.func_180425_c().func_177958_n() == x && girl.func_180425_c().func_177956_o() == y && girl.func_180425_c().func_177952_p() == z) {
               return new GirlContainer(girl, player.field_71071_by, UUID.randomUUID());
            }
         }
      }

      return null;
   }

   public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      if (ID == 0) {
         Iterator var7 = GirlEntity.girlEntities.iterator();

         while(var7.hasNext()) {
            GirlEntity girl = (GirlEntity)var7.next();
            if (girl.field_70170_p.field_72995_K && girl.func_180425_c().func_177958_n() == x && girl.func_180425_c().func_177956_o() == y && girl.func_180425_c().func_177952_p() == z) {
               return new GirlInventoryUI(girl, player.field_71071_by, UUID.randomUUID());
            }
         }
      }

      return null;
   }
}

package com.schnurritv.sexmod.world;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapData.MapInfo;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMapSecret extends ItemMap {
   public static final ItemMapSecret ITEM_MAP_SECRET = new ItemMapSecret();

   public static ItemStack setupNewMap(World worldIn, double worldX, double worldZ, byte scale, boolean trackingPosition, boolean unlimitedTracking) {
      ItemStack itemstack = new ItemStack(ITEM_MAP_SECRET, 1, worldIn.func_72841_b("map"));
      String s = "map_" + itemstack.func_77960_j();
      MapData mapdata = new MapData(s);
      worldIn.func_72823_a(s, mapdata);
      mapdata.field_76197_d = scale;
      mapdata.func_176054_a(worldX, worldZ, mapdata.field_76197_d);
      mapdata.field_76200_c = worldIn.field_73011_w.getDimension();
      mapdata.field_186210_e = trackingPosition;
      mapdata.field_191096_f = unlimitedTracking;
      mapdata.func_76185_a();
      ItemMapSecret map = (ItemMapSecret)itemstack.func_77973_b();
      List<EntityPlayerMP> playerList = worldIn.func_73046_m().func_184103_al().func_181057_v();
      if (playerList.size() == 0) {
         return itemstack;
      } else {
         EntityPlayer forcedViewer = (EntityPlayer)playerList.get(0);
         if (forcedViewer != null) {
            map.func_77872_a(worldIn, forcedViewer, mapdata);
         }

         return itemstack;
      }
   }

   public void func_77872_a(World worldIn, Entity viewer, MapData mapData) {
      short size = 128;
      MapInfo mapinfo = mapData.func_82568_a((EntityPlayer)viewer);
      InputStream stream = null;

      try {
         stream = Minecraft.func_71410_x().func_110442_L().func_110536_a(new ResourceLocation("sexmod", "textures/colors.txt")).func_110527_b();
      } catch (IOException var12) {
         var12.printStackTrace();
      }

      if (stream != null) {
         try {
            Scanner scanner = new Scanner(stream);
            int[] colors = new int[16385];
            int i = 0;

            int x;
            while(scanner.hasNextInt()) {
               x = scanner.nextInt();
               colors[i++] = x;
               if (i >= colors.length) {
                  break;
               }
            }

            for(x = 0; x < size; ++x) {
               for(int y = 0; y < size; ++y) {
                  mapData.field_76198_e[x + y * size] = (byte)colors[x + y * size];
                  mapinfo.func_176102_a(x, y);
               }
            }

            scanner.close();
            stream.close();
         } catch (Exception var13) {
            var13.printStackTrace();
         }

      }
   }

   public static void registerItem() {
      ITEM_MAP_SECRET.setRegistryName("sexmod", "item_map_secret");
      ITEM_MAP_SECRET.func_77655_b("item_map_secret");
      MinecraftForge.EVENT_BUS.register(ItemMapSecret.class);
   }

   @SubscribeEvent
   public static void register(Register<Item> event) {
      event.getRegistry().register(ITEM_MAP_SECRET);
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void registerModel(ModelRegistryEvent event) {
      ModelLoader.setCustomModelResourceLocation(ITEM_MAP_SECRET, 0, new ModelResourceLocation("sexmod:item_map_secret"));
   }
}

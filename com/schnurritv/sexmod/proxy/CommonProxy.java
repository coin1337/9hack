package com.schnurritv.sexmod.proxy;

import com.schnurritv.sexmod.Main;
import com.schnurritv.sexmod.gender_change.hornypotion.HornyPotion;
import com.schnurritv.sexmod.girls.allie.lamp.LampItem;
import com.schnurritv.sexmod.girls.cat.fishing.CatFishingRod;
import com.schnurritv.sexmod.util.Handlers.EntityInnit;
import com.schnurritv.sexmod.util.Handlers.EventHandler;
import com.schnurritv.sexmod.util.Handlers.GuiHandler;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import com.schnurritv.sexmod.util.Handlers.SoundsHandler;
import com.schnurritv.sexmod.world.ItemMapSecret;
import com.schnurritv.sexmod.world.WorldGenCustomStructures;
import java.io.IOException;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {
   public void preInitRegistries(FMLPreInitializationEvent event) {
      GameRegistry.registerWorldGenerator(new WorldGenCustomStructures(), 0);
      EntityInnit.registerEntities();
      HornyPotion.registerHornyPotion();
      ItemMapSecret.registerItem();
      LampItem.registerLamp();
      CatFishingRod.registerCatFishingRod();
   }

   public void initRegistries(FMLInitializationEvent event) throws IOException {
      Main.setConfigs();
      SoundsHandler.registerSounds();
      NetworkRegistry.INSTANCE.registerGuiHandler(Main.instance, new GuiHandler());
      EventHandler.registerEvents(false);
      PacketHandler.registerMessages();
   }

   public void postInit(FMLPostInitializationEvent event) throws IOException {
   }
}

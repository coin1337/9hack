package com.schnurritv.sexmod;

import com.google.gson.Gson;
import com.schnurritv.sexmod.girls.base.GirlModel;
import com.schnurritv.sexmod.girls.base.player_girl.PlayerGirl;
import com.schnurritv.sexmod.proxy.CommonProxy;
import com.schnurritv.sexmod.util.Configs;
import com.schnurritv.sexmod.world.WorldGenCustomStructures;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;

@Mod(
   modid = "sexmod",
   name = "SchnurriTV's Sex Mod",
   version = "1.5.2"
)
public class Main {
   @Instance
   public static Main instance;
   @SidedProxy(
      clientSide = "com.schnurritv.sexmod.proxy.ClientProxy",
      serverSide = "com.schnurritv.sexmod.proxy.CommonProxy"
   )
   public static CommonProxy proxy;
   public static final Logger LOGGER = LogManager.getLogger("sexmod");

   public Main() {
      GeckoLib.initialize();
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      proxy.preInitRegistries(event);
   }

   @EventHandler
   public void init(FMLInitializationEvent event) throws IOException {
      proxy.initRegistries(event);
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) throws IOException {
      proxy.postInit(event);
   }

   public static void setConfigs() throws IOException {
      Gson gson = new Gson();
      File dir = new File("config");
      dir.mkdir();
      File configFile = new File("config/sexmod.json");
      if (!configFile.exists()) {
         configFile.createNewFile();
         FileWriter fw = new FileWriter(configFile);
         fw.write(gson.toJson(new Configs(true, true, true)));
         fw.close();
      }

      Configs.INSTANCE = (Configs)gson.fromJson(new FileReader(configFile), Configs.class);
      WorldGenCustomStructures.shouldGenerateBuildings = Configs.INSTANCE.shouldGenBuildings;
      GirlModel.shouldUseOtherSkins = Configs.INSTANCE.shouldLoadOtherSkins;
      PlayerGirl.ALLOW_FLYING = Configs.INSTANCE.allowFlying;
   }
}

package com.schnurritv.sexmod.proxy;

import com.daripher.sexmod.client.util.FakeWorld;
import com.schnurritv.sexmod.Main;
import com.schnurritv.sexmod.gender_change.SexPromptManager;
import com.schnurritv.sexmod.girls.allie.AllieEntity;
import com.schnurritv.sexmod.girls.bee.BeeEntity;
import com.schnurritv.sexmod.girls.bia.BiaEntity;
import com.schnurritv.sexmod.girls.cat.CatEntity;
import com.schnurritv.sexmod.girls.ellie.EllieEntity;
import com.schnurritv.sexmod.girls.jenny.JennyEntity;
import com.schnurritv.sexmod.girls.slime.SlimeEntity;
import com.schnurritv.sexmod.util.Handlers.EventHandler;
import com.schnurritv.sexmod.util.Handlers.GuiHandler;
import com.schnurritv.sexmod.util.Handlers.PacketHandler;
import com.schnurritv.sexmod.util.Handlers.RenderHandler;
import com.schnurritv.sexmod.util.Handlers.SoundsHandler;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class ClientProxy extends CommonProxy {
   public void preInitRegistries(FMLPreInitializationEvent event) {
      super.preInitRegistries(event);
      RenderHandler.registerEntityRenders();
   }

   public void initRegistries(FMLInitializationEvent event) throws IOException {
      Main.setConfigs();
      SoundsHandler.registerSounds();
      NetworkRegistry.INSTANCE.registerGuiHandler(Main.instance, new GuiHandler());
      EventHandler.registerEvents(true);
      PacketHandler.registerMessages();
      Minecraft client = Minecraft.func_71410_x();
      RenderManager rendermanager = client.func_175598_ae();
      WorldClient world = new FakeWorld();
      rendermanager.func_188391_a(new JennyEntity(world), 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
      rendermanager.func_188391_a(new EllieEntity(world), 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
      rendermanager.func_188391_a(new SlimeEntity(world), 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
      rendermanager.func_188391_a(new BiaEntity(world), 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
      rendermanager.func_188391_a(new AllieEntity(world), 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
      rendermanager.func_188391_a(new BiaEntity(world), 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
      rendermanager.func_188391_a(new BeeEntity(world), 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
      rendermanager.func_188391_a(new CatEntity(world), 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
      SexPromptManager.INSTANCE = new SexPromptManager();
   }
}

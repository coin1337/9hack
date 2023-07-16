package com.daripher.sexmod.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.world.World;

public class FakePlayer extends EntityPlayerSP {
   public FakePlayer(World world) {
      super(Minecraft.func_71410_x(), new FakeWorld(), new FakeNetHandlerPlayClient(Minecraft.func_71410_x()), new StatisticsManager(), new RecipeBookClient());
   }
}

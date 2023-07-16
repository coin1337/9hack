package com.schnurritv.sexmod.util.Handlers;

import com.schnurritv.sexmod.Main;
import com.schnurritv.sexmod.girls.allie.AllieEntity;
import com.schnurritv.sexmod.girls.allie.PlayerAllie;
import com.schnurritv.sexmod.girls.bee.BeeEntity;
import com.schnurritv.sexmod.girls.bee.PlayerBee;
import com.schnurritv.sexmod.girls.bia.BiaEntity;
import com.schnurritv.sexmod.girls.bia.PlayerBia;
import com.schnurritv.sexmod.girls.cat.CatEntity;
import com.schnurritv.sexmod.girls.cat.fishing.CatFishHook;
import com.schnurritv.sexmod.girls.ellie.EllieEntity;
import com.schnurritv.sexmod.girls.ellie.PlayerEllie;
import com.schnurritv.sexmod.girls.jenny.JennyEntity;
import com.schnurritv.sexmod.girls.jenny.PlayerJenny;
import com.schnurritv.sexmod.girls.slime.PlayerSlime;
import com.schnurritv.sexmod.girls.slime.SlimeEntity;
import com.schnurritv.sexmod.girls.slime.friendlySlime.FriendlySlimeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EntityInnit {
   public static void registerEntities() {
      registerEntity("jenny", JennyEntity.class, 177013, 3286592, 12655237);
      registerEntity("ellie", EllieEntity.class, 228922, 1447446, 9961472);
      registerEntity("slime", SlimeEntity.class, 168597, 13167780, 8244330);
      registerEntity("bia", BiaEntity.class, 230053, 7488816, 7254603);
      registerEntity("bee", BeeEntity.class, 4663354, 16701032, 4400155);
      registerEntity("luna", CatEntity.class, 6816463, 7881787, 7940422);
      EntityRegistry.registerModEntity(new ResourceLocation("sexmod:allie"), AllieEntity.class, "allie", 5614613, Main.instance, 50, 1, true);
      registerPlayerGirl("player_jenny", PlayerJenny.class, 12388645);
      registerPlayerGirl("player_ellie", PlayerEllie.class, 46348348);
      registerPlayerGirl("player_slime", PlayerSlime.class, 54816432);
      registerPlayerGirl("player_bia", PlayerBia.class, 65456415);
      registerPlayerGirl("player_bee", PlayerBee.class, 48648638);
      registerPlayerGirl("player_allie", PlayerAllie.class, 64867483);
      EntityRegistry.registerModEntity(new ResourceLocation("sexmod:friendly_slime"), FriendlySlimeEntity.class, "friendly_slime", 5548484, Main.instance, 50, 1, true);
      EntityRegistry.registerModEntity(new ResourceLocation("sexmod:luna_hook"), CatFishHook.class, "luna_hook", 4768742, Main.instance, 50, 1, true);
      EntityRegistry.addSpawn(SlimeEntity.class, 10, 1, 1, EnumCreatureType.CREATURE, new Biome[]{Biomes.field_76780_h, Biomes.field_150599_m});
      EntityRegistry.addSpawn(BeeEntity.class, 5, 1, 1, EnumCreatureType.CREATURE, new Biome[]{Biomes.field_76767_f, Biomes.field_76785_t});
   }

   private static void registerPlayerGirl(String name, Class<? extends Entity> entity, int id) {
      EntityRegistry.registerModEntity(new ResourceLocation("sexmod:" + name), entity, name, id, Main.instance, 100, 1, false);
   }

   private static void registerEntity(String name, Class<? extends Entity> entity, int id, int color1, int color2) {
      EntityRegistry.registerModEntity(new ResourceLocation("sexmod:" + name), entity, name, id, Main.instance, 50, 1, true, color1, color2);
   }
}

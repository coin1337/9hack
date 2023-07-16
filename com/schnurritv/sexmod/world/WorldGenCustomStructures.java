package com.schnurritv.sexmod.world;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.vecmath.Vector2f;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGenCustomStructures implements IWorldGenerator {
   public static boolean shouldGenerateBuildings = true;
   static int minimumDistance = 50;
   public static HashMap<String, List<Vector2f>> buildings = new HashMap();
   boolean didIt = false;

   public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
      if (shouldGenerateBuildings) {
         switch(world.field_73011_w.getDimension()) {
         case 0:
            this.generateStructure(new WorldGenStructure("jenny"), world, random, chunkX, chunkZ, Arrays.asList(Biomes.field_76772_c, Biomes.field_76767_f), new Vec3i(9, 4, 9), 0, "jenny");
            this.generateStructure(new WorldGenStructure("ellie"), world, random, chunkX, chunkZ, Arrays.asList(Biomes.field_150578_U, Biomes.field_150584_S, Biomes.field_76768_g, Biomes.field_150585_R), new Vec3i(30, 27, 26), 0, "ellie");
            this.generateStructure(new BiaStructure("bia"), world, random, chunkX, chunkZ, Arrays.asList(Biomes.field_150583_P, Biomes.field_185448_Z), new Vec3i(11, 9, 15), 0, "bia");
         case 1:
         case 2:
         }
      }

   }

   private void generateStructure(WorldGenerator generator, World world, Random random, int chunkX, int chunkZ, List<Biome> biomes, Vec3i bounds, int yOffset, String name) {
      if (!this.didIt) {
         buildings.put("jenny", new ArrayList());
         buildings.put("ellie", new ArrayList());
         buildings.put("bia", new ArrayList());
         this.didIt = true;
      }

      int x = chunkX * 16 + random.nextInt(15);
      int z = chunkZ * 16 + random.nextInt(15);
      int y = calculateGenerationHeight(world, x, z);
      BlockPos pos = new BlockPos(x, y - yOffset, z);
      Biome currentBiome = world.field_73011_w.getBiomeForCoords(pos);
      if (world.func_175624_G() != WorldType.field_77138_c && biomes.contains(currentBiome)) {
         Iterator var15 = ((List)buildings.get(name)).iterator();

         while(var15.hasNext()) {
            Vector2f chunk = (Vector2f)var15.next();
            if (Math.abs(chunk.x - (float)chunkX) + Math.abs(chunk.y - (float)chunkZ) < (float)minimumDistance) {
               return;
            }
         }

         for(x = 0; x < bounds.func_177952_p(); ++x) {
            for(z = 0; z < bounds.func_177958_n(); ++z) {
               Block underground = world.func_180495_p(new BlockPos(pos.func_177958_n() + x, pos.func_177956_o() - 1, pos.func_177952_p() + z)).func_177230_c();
               Block floor = world.func_180495_p(new BlockPos(pos.func_177958_n() + x, pos.func_177956_o(), pos.func_177952_p() + z)).func_177230_c();
               Block surface = world.func_180495_p(new BlockPos(pos.func_177958_n() + x, pos.func_177956_o() + 1, pos.func_177952_p() + z)).func_177230_c();
               if (underground != Blocks.field_150348_b && underground != Blocks.field_150346_d) {
                  return;
               }

               if (floor != Blocks.field_150349_c && floor != Blocks.field_150346_d && floor != Blocks.field_150348_b) {
                  return;
               }

               if (name.equals("jenny") && surface != Blocks.field_150350_a && !(surface instanceof BlockBush) && surface != Blocks.field_150431_aC && surface != Blocks.field_150433_aE && surface != Blocks.field_150364_r && surface != Blocks.field_150363_s) {
                  return;
               }
            }
         }

         ((List)buildings.get(name)).add(new Vector2f((float)chunkX, (float)chunkZ));
         generator.func_180709_b(world, random, pos);
      }

   }

   private static int calculateGenerationHeight(World world, int x, int z) {
      Set<Block> topBlocks = Sets.newHashSet(new Block[]{Blocks.field_150349_c, Blocks.field_150354_m, Blocks.field_180395_cM});
      int y = world.func_72800_K();

      Block block;
      for(boolean foundGround = false; !foundGround && y-- >= 0; foundGround = topBlocks.contains(block)) {
         block = world.func_180495_p(new BlockPos(x, y, z)).func_177230_c();
      }

      return y;
   }
}

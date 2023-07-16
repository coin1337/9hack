package com.daripher.sexmod.client.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomePlains;
import net.minecraft.world.biome.Biome.BiomeProperties;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FakeWorldFakeWorldProvider extends WorldProvider {
   public DimensionType func_186058_p() {
      // $FF: Couldn't be decompiled
   }

   protected FakeWorldFakeWorldProvider() {
   }

   protected void func_76572_b() {
      this.field_191067_f = true;
   }

   public DimensionType func_186058_p() {
      // $FF: Couldn't be decompiled
   }

   public boolean func_76569_d() {
      return true;
   }

   public boolean func_76567_e() {
      return true;
   }

   public int func_76557_i() {
      return 63;
   }

   @SideOnly(Side.CLIENT)
   public boolean func_76568_b(int par1, int par2) {
      return false;
   }

   public void setDimension(int dim) {
   }

   public String getSaveFolder() {
      return null;
   }

   public BlockPos getRandomizedSpawnPoint() {
      return new BlockPos(0, 64, 0);
   }

   public boolean shouldMapSpin(String entity, double x, double y, double z) {
      return false;
   }

   public int getRespawnDimension(EntityPlayerMP player) {
      return 0;
   }

   public Biome getBiomeForCoords(BlockPos pos) {
      return new BiomePlains(false, (new BiomeProperties("Plains")).func_185398_c(0.125F).func_185400_d(0.05F).func_185400_d(0.8F).func_185400_d(0.4F));
   }

   public boolean isDaytime() {
      return true;
   }

   public void setAllowedSpawnTypes(boolean allowHostile, boolean allowPeaceful) {
   }

   public void calculateInitialWeather() {
   }

   public void updateWeather() {
   }

   public boolean canBlockFreeze(BlockPos pos, boolean byWater) {
      return false;
   }

   public boolean canSnowAt(BlockPos pos, boolean checkLight) {
      return false;
   }

   public long getSeed() {
      return 1L;
   }

   public long getWorldTime() {
      return 1L;
   }

   public void setWorldTime(long time) {
   }

   public boolean canMineBlock(EntityPlayer player, BlockPos pos) {
      return false;
   }

   public boolean isBlockHighHumidity(BlockPos pos) {
      return false;
   }

   public int getHeight() {
      return 256;
   }

   public int getActualHeight() {
      return 256;
   }

   public void resetRainAndThunder() {
   }

   public boolean canDoLightning(Chunk chunk) {
      return false;
   }

   public boolean canDoRainSnowIce(Chunk chunk) {
      return false;
   }

   public BlockPos getSpawnPoint() {
      return new BlockPos(0, 64, 0);
   }

   public boolean func_76566_a(int par1, int par2) {
      return true;
   }
}

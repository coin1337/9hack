package com.daripher.sexmod.client.util;

import java.io.File;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.GameType;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomePlains;
import net.minecraft.world.biome.Biome.BiomeProperties;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FakeWorld extends WorldClient {
   public FakeWorld() {
      super(new FakeNetHandlerPlayClient(Minecraft.func_71410_x()), new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.field_77138_c), 0, EnumDifficulty.HARD, new Profiler());
      this.field_73011_w.func_76558_a(this);
   }

   public Biome func_180494_b(BlockPos pos) {
      return new BiomePlains(false, (new BiomeProperties("Plains")).func_185398_c(0.125F).func_185400_d(0.05F).func_185400_d(0.8F).func_185395_b(0.4F));
   }

   public Biome getBiomeForCoordsBody(BlockPos pos) {
      return new BiomePlains(false, (new BiomeProperties("Plains")).func_185398_c(0.125F).func_185400_d(0.05F).func_185400_d(0.8F).func_185395_b(0.4F));
   }

   protected boolean func_175680_a(int i, int i1, boolean b) {
      return false;
   }

   public BlockPos func_175672_r(BlockPos pos) {
      return new BlockPos(pos.func_177958_n(), 63, pos.func_177952_p());
   }

   public boolean func_175623_d(BlockPos pos) {
      return pos.func_177956_o() > 63;
   }

   public IBlockState func_180495_p(BlockPos pos) {
      return pos.func_177956_o() > 63 ? Blocks.field_150350_a.func_176223_P() : Blocks.field_150349_c.func_176223_P();
   }

   public boolean func_175656_a(BlockPos pos, IBlockState state) {
      return true;
   }

   public boolean func_175698_g(BlockPos pos) {
      return true;
   }

   public void func_175646_b(BlockPos pos, TileEntity unusedTileEntity) {
   }

   public void func_184138_a(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
   }

   public boolean func_175655_b(BlockPos pos, boolean dropBlock) {
      return this.func_175623_d(pos);
   }

   public void func_175685_c(BlockPos pos, Block blockType, boolean p_175685_3_) {
      super.func_175685_c(pos, blockType, p_175685_3_);
   }

   public void func_175695_a(BlockPos pos, Block blockType, EnumFacing skipSide) {
   }

   public void func_175722_b(BlockPos pos, Block blockType, boolean p_175722_3_) {
   }

   public void markAndNotifyBlock(BlockPos pos, Chunk chunk, IBlockState iblockstate, IBlockState newState, int flags) {
   }

   public void func_72975_g(int par1, int par2, int par3, int par4) {
   }

   public void func_147458_c(int p_147458_1_, int p_147458_2_, int p_147458_3_, int p_147458_4_, int p_147458_5_, int p_147458_6_) {
   }

   public boolean func_175691_a(BlockPos pos, Block blockType) {
      return false;
   }

   public int func_175671_l(BlockPos pos) {
      return 14;
   }

   public int func_175721_c(BlockPos pos, boolean checkNeighbors) {
      return 14;
   }

   public int func_175699_k(BlockPos pos) {
      return 14;
   }

   public int func_175642_b(EnumSkyBlock type, BlockPos pos) {
      return 14;
   }

   public int func_175705_a(EnumSkyBlock type, BlockPos pos) {
      return 14;
   }

   public boolean func_175710_j(BlockPos pos) {
      return pos.func_177956_o() > 62;
   }

   public BlockPos func_175645_m(BlockPos pos) {
      return new BlockPos(pos.func_177958_n(), 63, pos.func_177952_p());
   }

   public int func_82734_g(int x, int z) {
      return 63;
   }

   protected void func_147456_g() {
   }

   public void func_175704_b(BlockPos rangeMin, BlockPos rangeMax) {
   }

   public void func_175653_a(EnumSkyBlock type, BlockPos pos, int lightValue) {
   }

   public float func_175724_o(BlockPos pos) {
      return 1.0F;
   }

   public float getSunBrightnessFactor(float p_72967_1_) {
      return 1.0F;
   }

   @SideOnly(Side.CLIENT)
   public float func_72971_b(float p_72971_1_) {
      return 1.0F;
   }

   @SideOnly(Side.CLIENT)
   public float getSunBrightnessBody(float p_72971_1_) {
      return 1.0F;
   }

   public boolean func_72935_r() {
      return true;
   }

   public void func_184148_a(EntityPlayer player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
   }

   public void func_184156_a(BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch, boolean distanceDelay) {
   }

   public void func_184134_a(double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch, boolean distanceDelay) {
   }

   public void func_175682_a(EnumParticleTypes particleType, boolean p_175682_2_, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... p_175682_15_) {
   }

   public void func_175688_a(EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... p_175688_14_) {
   }

   public void func_184149_a(BlockPos blockPositionIn, SoundEvent soundEventIn) {
   }

   public RayTraceResult func_72901_a(Vec3d start, Vec3d end, boolean stopOnLiquid) {
      return null;
   }

   public RayTraceResult func_72933_a(Vec3d start, Vec3d end) {
      return null;
   }

   public RayTraceResult func_147447_a(Vec3d vec31, Vec3d vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
      return null;
   }

   public boolean func_72942_c(Entity par1Entity) {
      return false;
   }

   public boolean func_72838_d(Entity par1Entity) {
      return false;
   }

   public void func_72923_a(Entity par1Entity) {
   }

   public void func_72847_b(Entity par1Entity) {
   }

   public void func_72900_e(Entity par1Entity) {
   }

   public void func_72973_f(Entity entityIn) {
   }

   public int func_72967_a(float par1) {
      return 6;
   }

   public void func_180497_b(BlockPos pos, Block blockIn, int delay, int priority) {
   }

   public void func_72939_s() {
   }

   public void func_72866_a(Entity entityIn, boolean forceUpdate) {
      if (forceUpdate) {
         ++entityIn.field_70173_aa;
      }

   }

   public boolean func_72855_b(AxisAlignedBB bb) {
      return true;
   }

   public boolean func_72917_a(AxisAlignedBB bb, Entity entityIn) {
      return true;
   }

   public boolean func_72829_c(AxisAlignedBB bb) {
      return false;
   }

   public boolean func_72953_d(AxisAlignedBB bb) {
      return false;
   }

   public boolean func_72918_a(AxisAlignedBB par1AxisAlignedBB, Material par2Material, Entity par3Entity) {
      return false;
   }

   public boolean func_72875_a(AxisAlignedBB par1AxisAlignedBB, Material par2Material) {
      return false;
   }

   public TileEntity func_175625_s(BlockPos pos) {
      return null;
   }

   public boolean func_175719_a(EntityPlayer player, BlockPos pos, EnumFacing side) {
      return true;
   }

   @SideOnly(Side.CLIENT)
   public String func_72981_t() {
      return "";
   }

   @SideOnly(Side.CLIENT)
   public String func_72827_u() {
      return "";
   }

   public void func_175690_a(BlockPos pos, TileEntity tileEntityIn) {
   }

   public void func_175713_t(BlockPos pos) {
   }

   public void func_147457_a(TileEntity p_147457_1_) {
   }

   public boolean func_175677_d(BlockPos pos, boolean _default) {
      return true;
   }

   public void func_72835_b() {
   }

   protected void func_72979_l() {
   }

   public void updateWeatherBody() {
   }

   public boolean func_175675_v(BlockPos pos) {
      return false;
   }

   public boolean func_175662_w(BlockPos pos) {
      return false;
   }

   public boolean func_175670_e(BlockPos pos, boolean noWaterAdj) {
      return false;
   }

   public boolean canBlockFreezeBody(BlockPos pos, boolean noWaterAdj) {
      return false;
   }

   public boolean func_175708_f(BlockPos pos, boolean checkLight) {
      return false;
   }

   public boolean canSnowAtBody(BlockPos pos, boolean checkLight) {
      return false;
   }

   public boolean func_72955_a(boolean par1) {
      return false;
   }

   public List func_72920_a(Chunk par1Chunk, boolean par2) {
      return null;
   }

   public Entity func_72857_a(Class par1Class, AxisAlignedBB par2AxisAlignedBB, Entity par3Entity) {
      return null;
   }

   public void func_175650_b(Collection<Entity> entityCollection) {
   }

   public void func_175681_c(Collection<Entity> entityCollection) {
   }

   public int func_72907_a(Class par1Class) {
      return 0;
   }

   public int func_175676_y(BlockPos pos) {
      return 0;
   }

   public int func_175627_a(BlockPos pos, EnumFacing direction) {
      return 0;
   }

   public boolean func_175709_b(BlockPos pos, EnumFacing side) {
      return false;
   }

   public int func_175651_c(BlockPos pos, EnumFacing facing) {
      return 0;
   }

   public boolean func_175640_z(BlockPos pos) {
      return false;
   }

   public int func_175687_A(BlockPos pos) {
      return 0;
   }

   public void func_72906_B() throws MinecraftException {
   }

   public long func_72905_C() {
      return 1L;
   }

   public long func_82737_E() {
      return 1L;
   }

   public long func_72820_D() {
      return 1L;
   }

   public void func_72877_b(long par1) {
   }

   public BlockPos func_175694_M() {
      return new BlockPos(0, 64, 0);
   }

   @SideOnly(Side.CLIENT)
   public void func_72897_h(Entity par1Entity) {
   }

   public boolean func_175678_i(BlockPos pos) {
      return pos.func_177956_o() > 62;
   }

   public boolean canMineBlockBody(EntityPlayer player, BlockPos pos) {
      return false;
   }

   public void func_72960_a(Entity par1Entity, byte par2) {
   }

   public float func_72819_i(float delta) {
      return 0.0F;
   }

   public void func_175641_c(BlockPos pos, Block blockIn, int eventID, int eventParam) {
   }

   public void func_72854_c() {
   }

   public boolean func_175727_C(BlockPos strikePosition) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public void func_147442_i(float p_147442_1_) {
   }

   public float func_72867_j(float par1) {
      return 0.0F;
   }

   @SideOnly(Side.CLIENT)
   public void func_72894_k(float par1) {
   }

   public boolean func_72911_I() {
      return false;
   }

   public boolean func_72896_J() {
      return false;
   }

   public boolean func_180502_D(BlockPos pos) {
      return false;
   }

   public void func_72823_a(String dataID, WorldSavedData worldSavedDataIn) {
   }

   public void func_175669_a(int p_175669_1_, BlockPos pos, int p_175669_3_) {
   }

   public void func_180498_a(@Nullable EntityPlayer player, int type, BlockPos pos, int data) {
   }

   public void func_175718_b(int type, BlockPos pos, int data) {
   }

   public int func_72800_K() {
      return 256;
   }

   public int func_72940_L() {
      return 256;
   }

   @SideOnly(Side.CLIENT)
   public void func_92088_a(double par1, double par3, double par5, double par7, double par9, double par11, NBTTagCompound par13nbtTagCompound) {
   }

   public boolean func_175700_a(TileEntity tile) {
      return true;
   }

   public void func_147448_a(Collection<TileEntity> tileEntityCollection) {
   }

   public boolean isSideSolid(BlockPos pos, EnumFacing side) {
      return pos.func_177956_o() <= 63;
   }

   public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
      return pos.func_177956_o() <= 63;
   }

   public int countEntities(EnumCreatureType type, boolean forSpawnCount) {
      return 0;
   }

   protected IChunkProvider func_72970_h() {
      return new FakeWorld.FakeChunkProvider();
   }

   public Chunk func_72964_e(int par1, int par2) {
      return null;
   }

   protected static class FakeWorldProvider extends WorldProvider {
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
         return new BiomePlains(false, (new BiomeProperties("Plains")).func_185398_c(0.125F).func_185400_d(0.05F).func_185400_d(0.8F).func_185395_b(0.4F));
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

      public DimensionType func_186058_p() {
         // $FF: Couldn't be decompiled
      }

      public BlockPos getSpawnPoint() {
         return new BlockPos(0, 64, 0);
      }

      public boolean func_76566_a(int par1, int par2) {
         return true;
      }
   }

   protected static class FakeSaveHandler implements ISaveHandler {
      @Nullable
      public WorldInfo func_75757_d() {
         return null;
      }

      public void func_75762_c() throws MinecraftException {
      }

      public IChunkLoader func_75763_a(WorldProvider provider) {
         return null;
      }

      public void func_75755_a(WorldInfo worldInformation, NBTTagCompound tagCompound) {
      }

      public void func_75761_a(WorldInfo worldInformation) {
      }

      public IPlayerFileData func_75756_e() {
         return null;
      }

      public void func_75759_a() {
      }

      public File func_75765_b() {
         return null;
      }

      public File func_75758_b(String mapName) {
         return null;
      }

      public TemplateManager func_186340_h() {
         return new TemplateManager("", Minecraft.func_71410_x().func_184126_aj());
      }
   }

   protected static class FakeChunkProvider implements IChunkProvider {
      @Nullable
      public Chunk func_186026_b(int x, int z) {
         return null;
      }

      public Chunk func_186025_d(int x, int z) {
         return null;
      }

      public boolean func_73156_b() {
         return false;
      }

      public String func_73148_d() {
         return null;
      }

      public boolean func_191062_e(int x, int z) {
         return true;
      }
   }
}

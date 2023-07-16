package software.bernie.example.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.example.GeckoLibMod;
import software.bernie.example.block.tile.FertilizerTileEntity;

public class FertilizerBlock extends BlockDirectional implements ITileEntityProvider {
   public FertilizerBlock() {
      super(Material.field_151576_e);
      this.func_149647_a(GeckoLibMod.getGeckolibItemGroup());
   }

   public boolean hasTileEntity(IBlockState state) {
      return true;
   }

   @Nullable
   public TileEntity func_149915_a(World worldIn, int meta) {
      return new FertilizerTileEntity();
   }

   public EnumBlockRenderType func_149645_b(IBlockState state) {
      return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
   }

   protected BlockStateContainer func_180661_e() {
      return new BlockStateContainer(this, new IProperty[]{field_176387_N});
   }

   public int func_176201_c(IBlockState state) {
      return ((EnumFacing)state.func_177229_b(field_176387_N)).func_176745_a();
   }

   public IBlockState func_176203_a(int meta) {
      return this.func_176223_P().func_177226_a(field_176387_N, EnumFacing.func_82600_a(meta));
   }

   public IBlockState func_180642_a(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.func_176223_P().func_177226_a(field_176387_N, EnumFacing.func_190914_a(pos, placer).func_176734_d());
   }

   public boolean func_149662_c(IBlockState state) {
      return false;
   }

   public boolean func_149686_d(IBlockState state) {
      return false;
   }

   public void func_190948_a(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {
      tooltip.add("Turn on rain to see the fertilizer model!");
      super.func_190948_a(stack, player, tooltip, advanced);
   }
}

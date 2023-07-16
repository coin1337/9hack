package software.bernie.geckolib3.renderers.geo;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public abstract class GeoBlockRenderer<T extends TileEntity & IAnimatable> extends TileEntitySpecialRenderer<T> implements IGeoRenderer<T> {
   private final AnimatedGeoModel<T> modelProvider;

   public GeoBlockRenderer(AnimatedGeoModel<T> modelProvider) {
      this.modelProvider = modelProvider;
   }

   public void func_192841_a(T te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
      this.render(te, x, y, z, partialTicks, destroyStage);
   }

   public void render(T tile, double x, double y, double z, float partialTicks, int destroyStage) {
      GeoModel model = this.modelProvider.getModel(this.modelProvider.getModelLocation(tile));
      this.modelProvider.setLivingAnimations(tile, this.getUniqueID(tile));
      int light = tile.func_145831_w().func_175626_b(tile.func_174877_v(), 0);
      int lx = light % 65536;
      int ly = light / 65536;
      GlStateManager.func_179138_g(OpenGlHelper.field_77476_b);
      OpenGlHelper.func_77475_a(3553, (float)lx, (float)ly);
      GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
      GlStateManager.func_179094_E();
      GlStateManager.func_179137_b(x, y, z);
      GlStateManager.func_179109_b(0.0F, 0.01F, 0.0F);
      GlStateManager.func_179137_b(0.5D, 0.0D, 0.5D);
      this.rotateBlock(this.getFacing(tile));
      Minecraft.func_71410_x().field_71446_o.func_110577_a(this.getTextureLocation(tile));
      Color renderColor = this.getRenderColor(tile, partialTicks);
      this.render(model, tile, partialTicks, (float)renderColor.getRed() / 255.0F, (float)renderColor.getGreen() / 255.0F, (float)renderColor.getBlue() / 255.0F, (float)renderColor.getAlpha() / 255.0F);
      GlStateManager.func_179121_F();
   }

   public AnimatedGeoModel<T> getGeoModelProvider() {
      return this.modelProvider;
   }

   protected void rotateBlock(EnumFacing facing) {
      switch(facing) {
      case SOUTH:
         GlStateManager.func_179114_b(180.0F, 0.0F, 1.0F, 0.0F);
         break;
      case WEST:
         GlStateManager.func_179114_b(90.0F, 0.0F, 1.0F, 0.0F);
      case NORTH:
      default:
         break;
      case EAST:
         GlStateManager.func_179114_b(270.0F, 0.0F, 1.0F, 0.0F);
         break;
      case UP:
         GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
         break;
      case DOWN:
         GlStateManager.func_179114_b(90.0F, -1.0F, 0.0F, 0.0F);
      }

   }

   private EnumFacing getFacing(T tile) {
      IBlockState blockState = tile.func_145831_w().func_180495_p(tile.func_174877_v());
      if (blockState.func_177227_a().contains(BlockHorizontal.field_185512_D)) {
         return (EnumFacing)blockState.func_177229_b(BlockHorizontal.field_185512_D);
      } else {
         return blockState.func_177227_a().contains(BlockDirectional.field_176387_N) ? (EnumFacing)blockState.func_177229_b(BlockDirectional.field_176387_N) : EnumFacing.NORTH;
      }
   }

   public ResourceLocation getTextureLocation(T instance) {
      return this.modelProvider.getTextureLocation(instance);
   }

   static {
      AnimationController.addModelFetcher((object) -> {
         if (object instanceof TileEntity) {
            TileEntity tile = (TileEntity)object;
            TileEntitySpecialRenderer<TileEntity> renderer = TileEntityRendererDispatcher.field_147556_a.func_147547_b(tile);
            if (renderer instanceof GeoBlockRenderer) {
               return ((GeoBlockRenderer)renderer).getGeoModelProvider();
            }
         }

         return null;
      });
   }
}

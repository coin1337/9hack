package software.bernie.geckolib3.renderers.geo;

import java.util.Collections;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public abstract class GeoItemRenderer<T extends Item & IAnimatable> extends TileEntityItemStackRenderer implements IGeoRenderer<T> {
   protected AnimatedGeoModel<T> modelProvider;
   protected ItemStack currentItemStack;

   public GeoItemRenderer(AnimatedGeoModel<T> modelProvider) {
      this.modelProvider = modelProvider;
   }

   public void setModel(AnimatedGeoModel<T> model) {
      this.modelProvider = model;
   }

   public AnimatedGeoModel<T> getGeoModelProvider() {
      return this.modelProvider;
   }

   public void func_192838_a(ItemStack itemStack, float partialTicks) {
      this.render(itemStack.func_77973_b(), itemStack);
   }

   public void render(T animatable, ItemStack itemStack) {
      this.currentItemStack = itemStack;
      GeoModel model = this.modelProvider.getModel(this.modelProvider.getModelLocation(animatable));
      AnimationEvent itemEvent = new AnimationEvent((IAnimatable)animatable, 0.0F, 0.0F, Minecraft.func_71410_x().func_184121_ak(), false, Collections.singletonList(itemStack));
      this.modelProvider.setLivingAnimations((IAnimatable)animatable, this.getUniqueID(animatable), itemEvent);
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b(0.0F, 0.01F, 0.0F);
      GlStateManager.func_179137_b(0.5D, 0.5D, 0.5D);
      Minecraft.func_71410_x().field_71446_o.func_110577_a(this.getTextureLocation(animatable));
      Color renderColor = this.getRenderColor(animatable, 0.0F);
      this.render(model, animatable, 0.0F, (float)renderColor.getRed() / 255.0F, (float)renderColor.getGreen() / 255.0F, (float)renderColor.getBlue() / 255.0F, (float)renderColor.getAlpha() / 255.0F);
      GlStateManager.func_179121_F();
   }

   public ResourceLocation getTextureLocation(T instance) {
      return this.modelProvider.getTextureLocation(instance);
   }

   public Integer getUniqueID(T animatable) {
      return Objects.hash(new Object[]{this.currentItemStack.func_77973_b(), this.currentItemStack.func_190916_E(), this.currentItemStack.func_77942_o() ? this.currentItemStack.func_77978_p().toString() : 1});
   }

   static {
      AnimationController.addModelFetcher((object) -> {
         if (object instanceof Item) {
            Item item = (Item)object;
            TileEntityItemStackRenderer renderer = item.getTileEntityItemStackRenderer();
            if (renderer instanceof GeoItemRenderer) {
               return ((GeoItemRenderer)renderer).getGeoModelProvider();
            }
         }

         return null;
      });
   }
}

package software.bernie.geckolib3.item;

import javax.annotation.Nullable;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public abstract class GeoArmorItem extends ItemArmor {
   public GeoArmorItem(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot slot) {
      super(materialIn, renderIndexIn, slot);
   }

   @Nullable
   @SideOnly(Side.CLIENT)
   public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
      Class<? extends ItemArmor> clazz = this.getClass();
      GeoArmorRenderer renderer = GeoArmorRenderer.getRenderer(clazz);
      renderer.applyEntityStats(_default).applySlot(armorSlot);
      renderer.setCurrentItem(entityLiving, itemStack, armorSlot);
      return renderer;
   }

   @Nullable
   @SideOnly(Side.CLIENT)
   public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
      Class<? extends ItemArmor> clazz = this.getClass();
      GeoArmorRenderer renderer = GeoArmorRenderer.getRenderer(clazz);
      return renderer.getTextureLocation((ItemArmor)stack.func_77973_b()).toString();
   }
}

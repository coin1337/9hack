package com.schnurritv.sexmod.girls.cat.fishing;

import com.schnurritv.sexmod.girls.cat.CatEntity;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CatFishingRod extends ItemFishingRod {
   public static final CatFishingRod CAT_FISHING_ROD = new CatFishingRod();

   public CatFishingRod() {
      this.func_77656_e(64);
      this.func_77625_d(1);
      this.func_185043_a(new ResourceLocation("cast"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float func_185085_a(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
            if (entityIn == null) {
               return 0.0F;
            } else if (!(entityIn instanceof CatEntity)) {
               return 0.0F;
            } else {
               return (Boolean)entityIn.func_184212_Q().func_187225_a(CatEntity.IS_FISHING) ? 1.0F : 0.0F;
            }
         }
      });
   }

   public static void registerCatFishingRod() {
      CAT_FISHING_ROD.setRegistryName("sexmod", "luna_rod");
      CAT_FISHING_ROD.func_77655_b("luna_rod");
      MinecraftForge.EVENT_BUS.register(CatFishingRod.class);
   }

   @SubscribeEvent
   public static void register(Register<Item> event) {
      event.getRegistry().register(CAT_FISHING_ROD);
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void registerModel(ModelRegistryEvent event) {
      ModelLoader.setCustomModelResourceLocation(CAT_FISHING_ROD, 0, new ModelResourceLocation("fishing_rod"));
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, CatEntity cat, EnumHand handIn) {
      ItemStack itemstack = cat.func_184586_b(handIn);
      if (cat.fishEntity != null) {
         int i = cat.fishEntity.handleHookRetraction();
         itemstack.func_77972_a(i, cat);
         cat.func_184609_a(handIn);
         worldIn.func_184148_a((EntityPlayer)null, cat.field_70165_t, cat.field_70163_u, cat.field_70161_v, SoundEvents.field_193780_J, SoundCategory.NEUTRAL, 1.0F, 0.4F / (field_77697_d.nextFloat() * 0.4F + 0.8F));
      } else {
         worldIn.func_184148_a((EntityPlayer)null, cat.field_70165_t, cat.field_70163_u, cat.field_70161_v, SoundEvents.field_187612_G, SoundCategory.NEUTRAL, 0.5F, 0.4F / (field_77697_d.nextFloat() * 0.4F + 0.8F));
         if (!worldIn.field_72995_K) {
            CatFishHook.nextAngler = cat;
            CatFishHook entityfishhook = new CatFishHook(worldIn, cat);
            int j = EnchantmentHelper.func_191528_c(itemstack);
            if (j > 0) {
               entityfishhook.setLureSpeed(j);
            }

            int k = EnchantmentHelper.func_191529_b(itemstack);
            if (k > 0) {
               entityfishhook.setLuck(k);
            }

            worldIn.func_72838_d(entityfishhook);
         }

         cat.func_184609_a(handIn);
      }

      return new ActionResult(EnumActionResult.SUCCESS, itemstack);
   }
}

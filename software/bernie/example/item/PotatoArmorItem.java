package software.bernie.example.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import software.bernie.example.GeckoLibMod;
import software.bernie.example.registry.ItemRegistry;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.GeoArmorItem;

public class PotatoArmorItem extends GeoArmorItem implements IAnimatable {
   private AnimationFactory factory = new AnimationFactory(this);

   public PotatoArmorItem(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot slot) {
      super(materialIn, renderIndexIn, slot);
      this.func_77637_a(GeckoLibMod.getGeckolibItemGroup());
   }

   private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
      EntityLivingBase livingEntity = (EntityLivingBase)event.getExtraDataOfType(EntityLivingBase.class).get(0);
      event.getController().setAnimation((new AnimationBuilder()).addAnimation("animation.potato_armor.new", true));
      if (livingEntity instanceof EntityArmorStand) {
         return PlayState.CONTINUE;
      } else if (livingEntity instanceof EntityPlayerSP) {
         EntityPlayerSP client = (EntityPlayerSP)livingEntity;
         List<Item> equipmentList = new ArrayList();
         client.func_184209_aF().forEach((x) -> {
            equipmentList.add(x.func_77973_b());
         });
         List<Item> armorList = equipmentList.subList(2, 6);
         boolean isWearingAll = armorList.containsAll(Arrays.asList(ItemRegistry.POTATO_BOOTS, ItemRegistry.POTATO_LEGGINGS, ItemRegistry.POTATO_CHEST, ItemRegistry.POTATO_HEAD));
         return isWearingAll ? PlayState.CONTINUE : PlayState.STOP;
      } else {
         return PlayState.STOP;
      }
   }

   public void registerControllers(AnimationData data) {
      data.addAnimationController(new AnimationController(this, "controller", 20.0F, this::predicate));
   }

   public AnimationFactory getFactory() {
      return this.factory;
   }
}

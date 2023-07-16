package software.bernie.example.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import software.bernie.example.GeckoLibMod;
import software.bernie.example.registry.SoundRegistry;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class JackInTheBoxItem extends Item implements IAnimatable {
   public AnimationFactory factory = new AnimationFactory(this);
   private String controllerName = "popupController";

   private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event) {
      return PlayState.CONTINUE;
   }

   public JackInTheBoxItem() {
      this.func_77637_a(GeckoLibMod.getGeckolibItemGroup());
   }

   public void registerControllers(AnimationData data) {
      AnimationController<JackInTheBoxItem> controller = new AnimationController(this, this.controllerName, 20.0F, this::predicate);
      controller.registerSoundListener(this::soundListener);
      data.addAnimationController(controller);
   }

   private <ENTITY extends IAnimatable> void soundListener(SoundKeyframeEvent<ENTITY> event) {
      EntityPlayerSP player = Minecraft.func_71410_x().field_71439_g;
      player.func_184185_a(SoundRegistry.JACK_MUSIC, 1.0F, 1.0F);
   }

   public AnimationFactory getFactory() {
      return this.factory;
   }

   public ActionResult<ItemStack> func_77659_a(World worldIn, EntityPlayer player, EnumHand hand) {
      if (!worldIn.field_72995_K) {
         return super.func_77659_a(worldIn, player, hand);
      } else {
         ItemStack stack = player.func_184586_b(hand);
         AnimationController<?> controller = GeckoLibUtil.getControllerForStack(this.factory, stack, this.controllerName);
         if (controller.getAnimationState() == AnimationState.Stopped) {
            player.func_146105_b(new TextComponentString("Opening the jack in the box!"), true);
            controller.markNeedsReload();
            controller.setAnimation((new AnimationBuilder()).addAnimation("Soaryn_chest_popup", false));
         }

         return super.func_77659_a(worldIn, player, hand);
      }
   }
}

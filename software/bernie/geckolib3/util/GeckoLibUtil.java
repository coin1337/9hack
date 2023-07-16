package software.bernie.geckolib3.util;

import java.util.Objects;
import net.minecraft.item.ItemStack;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class GeckoLibUtil {
   public static int getIDFromStack(ItemStack stack) {
      return Objects.hash(new Object[]{stack.func_77973_b(), stack.func_190916_E(), stack.func_77942_o() ? stack.func_77978_p().toString() : 1});
   }

   public static AnimationController getControllerForStack(AnimationFactory factory, ItemStack stack, String controllerName) {
      return (AnimationController)factory.getOrCreateAnimationData(getIDFromStack(stack)).getAnimationControllers().get(controllerName);
   }
}

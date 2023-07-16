package software.bernie.geckolib3.resource;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;

public class ResourceListener {
   public static void registerReloadListener() {
      if (Minecraft.func_71410_x().func_110442_L() == null) {
         throw new RuntimeException("GeckoLib was initialized too early! If you are on fabric, please read the wiki on when to initialize!");
      } else {
         IReloadableResourceManager reloadable = (IReloadableResourceManager)Minecraft.func_71410_x().func_110442_L();
         reloadable.func_110542_a(GeckoLibCache.getInstance());
      }
   }
}

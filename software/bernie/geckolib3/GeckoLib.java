package software.bernie.geckolib3;

import java.util.concurrent.FutureTask;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.resource.ResourceListener;

public class GeckoLib {
   public static final Logger LOGGER = LogManager.getLogger();
   public static final String ModID = "geckolib3";
   public static boolean hasInitialized;
   public static final String VERSION = "3.0.24";

   public static void initialize() {
      if (!hasInitialized) {
         FMLCommonHandler.callFuture(new FutureTask(() -> {
            if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
               doOnlyOnClient();
            }

         }, (Object)null));
      }

      hasInitialized = true;
   }

   @SideOnly(Side.CLIENT)
   private static void doOnlyOnClient() {
      ResourceListener.registerReloadListener();
   }
}

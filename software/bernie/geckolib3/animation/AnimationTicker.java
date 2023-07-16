package software.bernie.geckolib3.animation;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import software.bernie.geckolib3.core.manager.AnimationData;

public class AnimationTicker {
   private final AnimationData data;

   public AnimationTicker(AnimationData data) {
      this.data = data;
   }

   @SubscribeEvent
   public void tickEvent(ClientTickEvent event) {
      if (!Minecraft.func_71410_x().func_147113_T() || this.data.shouldPlayWhilePaused) {
         if (event.phase == Phase.END) {
            ++this.data.tick;
         }

      }
   }
}

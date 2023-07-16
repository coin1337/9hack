package com.schnurritv.sexmod.events;

import com.schnurritv.sexmod.gui.PornWarningWindow;
import javax.swing.JFrame;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class PornWarning extends JFrame {
   public boolean didIt = false;

   @SubscribeEvent
   public void PornWarning(ClientTickEvent event) {
      if (!this.didIt) {
         this.didIt = true;
         PornWarningWindow.launch();
      }

   }
}

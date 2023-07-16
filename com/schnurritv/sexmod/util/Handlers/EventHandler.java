package com.schnurritv.sexmod.util.Handlers;

import com.schnurritv.sexmod.companion.CompanionPearl;
import com.schnurritv.sexmod.companion.fighter.DamageCalculation;
import com.schnurritv.sexmod.companion.fighter.FighterCompanion;
import com.schnurritv.sexmod.events.DontRenderPlayersThatHaveSex;
import com.schnurritv.sexmod.events.HandlePlayerMovement;
import com.schnurritv.sexmod.events.NoDamageForGirlsWhileHavingTheSex;
import com.schnurritv.sexmod.events.PlayerGirlInteractions;
import com.schnurritv.sexmod.events.PornWarning;
import com.schnurritv.sexmod.events.RemoveEntityFromList;
import com.schnurritv.sexmod.events.ResetDisconnects;
import com.schnurritv.sexmod.events.ResetGirlList;
import com.schnurritv.sexmod.gender_change.RenderPlayerGirl;
import com.schnurritv.sexmod.gender_change.RenderPlayerGirlHand;
import com.schnurritv.sexmod.gender_change.SexPromptManager;
import com.schnurritv.sexmod.gender_change.hornypotion.HornyPotion;
import com.schnurritv.sexmod.girls.allie.PlayerAllieRenderer;
import com.schnurritv.sexmod.girls.allie.lamp.LampItem;
import com.schnurritv.sexmod.girls.cat.CatEntity;
import com.schnurritv.sexmod.girls.cat.fishing.CatFishingRod;
import com.schnurritv.sexmod.girls.slime.SlimeJumpDetector;
import com.schnurritv.sexmod.gui.PornWarningWindow;
import com.schnurritv.sexmod.gui.Sex.BlackScreenUI;
import com.schnurritv.sexmod.gui.Sex.SexUI;
import com.schnurritv.sexmod.world.ItemMapSecret;
import java.io.File;
import net.minecraftforge.common.MinecraftForge;

public class EventHandler {
   public static void registerEvents(boolean clientSide) {
      MinecraftForge.EVENT_BUS.register(new NoDamageForGirlsWhileHavingTheSex());
      MinecraftForge.EVENT_BUS.register(new RemoveEntityFromList());
      MinecraftForge.EVENT_BUS.register(new ResetDisconnects());
      MinecraftForge.EVENT_BUS.register(new SlimeJumpDetector());
      MinecraftForge.EVENT_BUS.register(new HornyPotion());
      MinecraftForge.EVENT_BUS.register(new DamageCalculation());
      MinecraftForge.EVENT_BUS.register(new CompanionPearl.EventHandler());
      MinecraftForge.EVENT_BUS.register(new FighterCompanion.EventHandler());
      MinecraftForge.EVENT_BUS.register(new LampItem());
      MinecraftForge.EVENT_BUS.register(new CatFishingRod());
      MinecraftForge.EVENT_BUS.register(new ItemMapSecret());
      MinecraftForge.EVENT_BUS.register(new PlayerGirlInteractions());
      MinecraftForge.EVENT_BUS.register(new CatEntity.EventHandler());
      if (clientSide) {
         if (needsPornWarning()) {
            MinecraftForge.EVENT_BUS.register(new PornWarning());
         } else {
            PornWarningWindow.wait = false;
         }

         MinecraftForge.EVENT_BUS.register(new SexUI());
         MinecraftForge.EVENT_BUS.register(new BlackScreenUI());
         MinecraftForge.EVENT_BUS.register(new HandlePlayerMovement());
         MinecraftForge.EVENT_BUS.register(new DontRenderPlayersThatHaveSex());
         MinecraftForge.EVENT_BUS.register(new ResetGirlList());
         MinecraftForge.EVENT_BUS.register(new RenderPlayerGirlHand());
         MinecraftForge.EVENT_BUS.register(new RenderPlayerGirl());
         MinecraftForge.EVENT_BUS.register(new SexPromptManager());
         MinecraftForge.EVENT_BUS.register(new PlayerAllieRenderer.EventHandler());
      }

   }

   static boolean needsPornWarning() {
      File save = new File("sexmod/dontAskAgain");
      save.getParentFile().mkdirs();
      return !save.exists();
   }
}

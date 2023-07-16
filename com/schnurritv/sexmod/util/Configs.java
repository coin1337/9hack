package com.schnurritv.sexmod.util;

public class Configs {
   public static Configs INSTANCE;
   public boolean shouldGenBuildings;
   public boolean shouldLoadOtherSkins;
   public boolean allowFlying;

   public Configs(boolean shouldGenBuildings, boolean shouldLoadOtherSkins, boolean allowFlying) {
      this.shouldGenBuildings = shouldGenBuildings;
      this.shouldLoadOtherSkins = shouldLoadOtherSkins;
      this.allowFlying = allowFlying;
   }
}

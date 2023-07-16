package com.schnurritv.sexmod.util.Handlers;

import com.schnurritv.sexmod.util.Reference;
import java.util.HashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SoundsHandler {
   public static final SoundEvent[] MISC_PLOB = new SoundEvent[1];
   public static final SoundEvent[] MISC_BELLJINGLE = new SoundEvent[1];
   public static final SoundEvent[] MISC_BEDRUSTLE = new SoundEvent[2];
   public static final SoundEvent[] MISC_SLAP = new SoundEvent[2];
   public static final SoundEvent[] MISC_TOUCH = new SoundEvent[2];
   public static final SoundEvent[] MISC_POUNDING = new SoundEvent[35];
   public static final SoundEvent[] MISC_SMALLINSERTS = new SoundEvent[5];
   public static final SoundEvent[] MISC_CUMINFLATION = new SoundEvent[1];
   public static final SoundEvent[] MISC_SCREAM = new SoundEvent[2];
   public static final SoundEvent[] MISC_FART = new SoundEvent[3];
   public static final SoundEvent[] MISC_JUMP = new SoundEvent[1];
   public static final SoundEvent[] GIRLS_JENNY_AFTERSESSIONMOAN = new SoundEvent[5];
   public static final SoundEvent[] GIRLS_JENNY_AHH = new SoundEvent[10];
   public static final SoundEvent[] GIRLS_JENNY_BJMOAN = new SoundEvent[13];
   public static final SoundEvent[] GIRLS_JENNY_GIGGLE = new SoundEvent[5];
   public static final SoundEvent[] GIRLS_JENNY_HAPPYOH = new SoundEvent[3];
   public static final SoundEvent[] GIRLS_JENNY_HEAVYBREATHING = new SoundEvent[8];
   public static final SoundEvent[] GIRLS_JENNY_HMPH = new SoundEvent[5];
   public static final SoundEvent[] GIRLS_JENNY_HUH = new SoundEvent[2];
   public static final SoundEvent[] GIRLS_JENNY_LIGHTBREATHING = new SoundEvent[12];
   public static final SoundEvent[] GIRLS_JENNY_LIPSOUND = new SoundEvent[10];
   public static final SoundEvent[] GIRLS_JENNY_MMM = new SoundEvent[9];
   public static final SoundEvent[] GIRLS_JENNY_MOAN = new SoundEvent[8];
   public static final SoundEvent[] GIRLS_JENNY_SADOH = new SoundEvent[2];
   public static final SoundEvent[] GIRLS_JENNY_SIGH = new SoundEvent[2];
   public static final SoundEvent[] GIRLS_ELLIE_AFTERSESSIONMOAN = new SoundEvent[5];
   public static final SoundEvent[] GIRLS_ELLIE_AHH = new SoundEvent[10];
   public static final SoundEvent[] GIRLS_ELLIE_BJMOAN = new SoundEvent[13];
   public static final SoundEvent[] GIRLS_ELLIE_GIGGLE = new SoundEvent[5];
   public static final SoundEvent[] GIRLS_ELLIE_HAPPYOH = new SoundEvent[3];
   public static final SoundEvent[] GIRLS_ELLIE_HEAVYBREATHING = new SoundEvent[8];
   public static final SoundEvent[] GIRLS_ELLIE_HMPH = new SoundEvent[4];
   public static final SoundEvent[] GIRLS_ELLIE_HUH = new SoundEvent[2];
   public static final SoundEvent[] GIRLS_ELLIE_LIGHTBREATHING = new SoundEvent[12];
   public static final SoundEvent[] GIRLS_ELLIE_LIPSOUND = new SoundEvent[10];
   public static final SoundEvent[] GIRLS_ELLIE_MMM = new SoundEvent[9];
   public static final SoundEvent[] GIRLS_ELLIE_MOAN = new SoundEvent[8];
   public static final SoundEvent[] GIRLS_ELLIE_SADOH = new SoundEvent[2];
   public static final SoundEvent[] GIRLS_ELLIE_SIGH = new SoundEvent[2];
   public static final SoundEvent[] GIRLS_BIA_AHH = new SoundEvent[8];
   public static final SoundEvent[] GIRLS_BIA_BJMOAN = new SoundEvent[5];
   public static final SoundEvent[] GIRLS_BIA_BREATH = new SoundEvent[4];
   public static final SoundEvent[] GIRLS_BIA_GIGGLE = new SoundEvent[3];
   public static final SoundEvent[] GIRLS_BIA_HEY = new SoundEvent[4];
   public static final SoundEvent[] GIRLS_BIA_HUH = new SoundEvent[3];
   public static final SoundEvent[] GIRLS_BIA_MMM = new SoundEvent[8];
   public static final SoundEvent[] GIRLS_LUNA_AHH = new SoundEvent[18];
   public static final SoundEvent[] GIRLS_LUNA_CUTENYA = new SoundEvent[12];
   public static final SoundEvent[] GIRLS_LUNA_HAPPYOH = new SoundEvent[8];
   public static final SoundEvent[] GIRLS_LUNA_HMPH = new SoundEvent[6];
   public static final SoundEvent[] GIRLS_LUNA_HORNINYA = new SoundEvent[10];
   public static final SoundEvent[] GIRLS_LUNA_HUH = new SoundEvent[5];
   public static final SoundEvent[] GIRLS_LUNA_LIGHTBREATHING = new SoundEvent[25];
   public static final SoundEvent[] GIRLS_LUNA_MMM = new SoundEvent[8];
   public static final SoundEvent[] GIRLS_LUNA_MOAN = new SoundEvent[10];
   public static final SoundEvent[] GIRLS_LUNA_SADOH = new SoundEvent[7];
   public static final SoundEvent[] GIRLS_LUNA_SIGH = new SoundEvent[8];
   public static final SoundEvent[] GIRLS_LUNA_SINGING = new SoundEvent[8];
   public static final SoundEvent[] GIRLS_LUNA_GIGGLE = new SoundEvent[15];
   public static final SoundEvent[] GIRLS_LUNA_OUU = new SoundEvent[13];
   public static final SoundEvent[] GIRLS_LUNA_OWO = new SoundEvent[8];
   private static final SoundEvent[][] soundCategorys;
   public static final SoundsHandler INSTANCE;
   static HashMap<SoundEvent, Integer> lastRandomSound;

   public static void registerSounds() {
      for(int soundCategoryIndex = 0; soundCategoryIndex < soundCategorys.length; ++soundCategoryIndex) {
         SoundEvent[] soundCategory = soundCategorys[soundCategoryIndex];

         for(int soundIndex = 0; soundIndex < soundCategory.length; ++soundIndex) {
            String path = INSTANCE.getClass().getDeclaredFields()[soundCategoryIndex].getName().toLowerCase().replace("_", ".");

            String categoryName;
            try {
               categoryName = path.split("\\.")[2];
            } catch (ArrayIndexOutOfBoundsException var6) {
               categoryName = path.split("\\.")[1];
            }

            soundCategory[soundIndex] = registerSound(path + "." + categoryName + soundIndex);
         }
      }

   }

   public static SoundEvent registerSound(String path) {
      ResourceLocation location = new ResourceLocation("sexmod", path);
      SoundEvent event = new SoundEvent(location);
      event.setRegistryName(path);
      ForgeRegistries.SOUND_EVENTS.register(event);
      return event;
   }

   public static SoundEvent random(SoundEvent[] soundArray) {
      lastRandomSound.putIfAbsent(soundArray[0], -69);
      int trys = 0;

      int random;
      do {
         random = Reference.RANDOM.nextInt(soundArray.length);
         ++trys;
      } while(trys < 10 && random == (Integer)lastRandomSound.get(soundArray[0]));

      lastRandomSound.replace(soundArray[0], random);
      return soundArray[random];
   }

   static {
      soundCategorys = new SoundEvent[][]{MISC_PLOB, MISC_BELLJINGLE, MISC_BEDRUSTLE, MISC_SLAP, MISC_TOUCH, MISC_POUNDING, MISC_SMALLINSERTS, MISC_CUMINFLATION, MISC_SCREAM, MISC_FART, MISC_JUMP, GIRLS_JENNY_AFTERSESSIONMOAN, GIRLS_JENNY_AHH, GIRLS_JENNY_BJMOAN, GIRLS_JENNY_GIGGLE, GIRLS_JENNY_HAPPYOH, GIRLS_JENNY_HEAVYBREATHING, GIRLS_JENNY_HMPH, GIRLS_JENNY_HUH, GIRLS_JENNY_LIGHTBREATHING, GIRLS_JENNY_LIPSOUND, GIRLS_JENNY_MMM, GIRLS_JENNY_MOAN, GIRLS_JENNY_SADOH, GIRLS_JENNY_SIGH, GIRLS_ELLIE_AFTERSESSIONMOAN, GIRLS_ELLIE_AHH, GIRLS_ELLIE_BJMOAN, GIRLS_ELLIE_GIGGLE, GIRLS_ELLIE_HAPPYOH, GIRLS_ELLIE_HEAVYBREATHING, GIRLS_ELLIE_HMPH, GIRLS_ELLIE_HUH, GIRLS_ELLIE_LIGHTBREATHING, GIRLS_ELLIE_LIPSOUND, GIRLS_ELLIE_MMM, GIRLS_ELLIE_MOAN, GIRLS_ELLIE_SADOH, GIRLS_ELLIE_SIGH, GIRLS_BIA_AHH, GIRLS_BIA_BJMOAN, GIRLS_BIA_BREATH, GIRLS_BIA_GIGGLE, GIRLS_BIA_HEY, GIRLS_BIA_HUH, GIRLS_BIA_MMM, GIRLS_LUNA_AHH, GIRLS_LUNA_CUTENYA, GIRLS_LUNA_HAPPYOH, GIRLS_LUNA_HMPH, GIRLS_LUNA_HORNINYA, GIRLS_LUNA_HUH, GIRLS_LUNA_LIGHTBREATHING, GIRLS_LUNA_MMM, GIRLS_LUNA_MOAN, GIRLS_LUNA_SADOH, GIRLS_LUNA_SIGH, GIRLS_LUNA_SINGING, GIRLS_LUNA_GIGGLE, GIRLS_LUNA_OUU, GIRLS_LUNA_OWO};
      INSTANCE = new SoundsHandler();
      lastRandomSound = new HashMap();
   }
}

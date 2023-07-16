package software.bernie.geckolib3.resource;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.LegacyV2Adapter;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.FMLFolderResourcePack;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.file.AnimationFile;
import software.bernie.geckolib3.file.AnimationFileLoader;
import software.bernie.geckolib3.file.GeoModelLoader;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.molang.MolangRegistrar;
import software.bernie.shadowed.eliotlash.molang.MolangParser;

public class GeckoLibCache implements IResourceManagerReloadListener {
   private static GeckoLibCache INSTANCE;
   private final AnimationFileLoader animationLoader = new AnimationFileLoader();
   private final GeoModelLoader modelLoader = new GeoModelLoader();
   public final MolangParser parser = new MolangParser();
   private HashMap<ResourceLocation, AnimationFile> animations = new HashMap();
   private HashMap<ResourceLocation, GeoModel> geoModels = new HashMap();

   public HashMap<ResourceLocation, AnimationFile> getAnimations() {
      if (!GeckoLib.hasInitialized) {
         throw new RuntimeException("GeckoLib was never initialized! Please read the documentation!");
      } else {
         return this.animations;
      }
   }

   public HashMap<ResourceLocation, GeoModel> getGeoModels() {
      if (!GeckoLib.hasInitialized) {
         throw new RuntimeException("GeckoLib was never initialized! Please read the documentation!");
      } else {
         return this.geoModels;
      }
   }

   protected GeckoLibCache() {
      MolangRegistrar.registerVars(this.parser);
   }

   public static GeckoLibCache getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new GeckoLibCache();
         return INSTANCE;
      } else {
         return INSTANCE;
      }
   }

   public void func_110549_a(IResourceManager resourceManager) {
      HashMap<ResourceLocation, AnimationFile> tempAnimations = new HashMap();
      HashMap<ResourceLocation, GeoModel> tempModels = new HashMap();
      List<IResourcePack> packs = this.getPacks();
      if (packs != null) {
         Iterator var5 = packs.iterator();

         while(var5.hasNext()) {
            IResourcePack pack = (IResourcePack)var5.next();
            Iterator var7 = this.getLocations(pack, "animations", (fileName) -> {
               return fileName.endsWith(".json");
            }).iterator();

            ResourceLocation location;
            while(var7.hasNext()) {
               location = (ResourceLocation)var7.next();

               try {
                  tempAnimations.put(location, this.animationLoader.loadAllAnimations(this.parser, location, resourceManager));
               } catch (Exception var10) {
                  var10.printStackTrace();
                  GeckoLib.LOGGER.error("Error loading animation file \"" + location + "\"!", var10);
               }
            }

            var7 = this.getLocations(pack, "geo", (fileName) -> {
               return fileName.endsWith(".json");
            }).iterator();

            while(var7.hasNext()) {
               location = (ResourceLocation)var7.next();

               try {
                  tempModels.put(location, this.modelLoader.loadModel(resourceManager, location));
               } catch (Exception var11) {
                  var11.printStackTrace();
                  GeckoLib.LOGGER.error("Error loading model file \"" + location + "\"!", var11);
               }
            }
         }

         this.animations = tempAnimations;
         this.geoModels = tempModels;
      }
   }

   private List<IResourcePack> getPacks() {
      try {
         Field field = FMLClientHandler.class.getDeclaredField("resourcePackList");
         field.setAccessible(true);
         return (List)field.get(FMLClientHandler.instance());
      } catch (Exception var2) {
         GeckoLib.LOGGER.error("Error accessing resource pack list!", var2);
         return null;
      }
   }

   private List<ResourceLocation> getLocations(IResourcePack pack, String folder, Predicate<String> predicate) {
      if (pack instanceof LegacyV2Adapter) {
         LegacyV2Adapter adapter = (LegacyV2Adapter)pack;
         Field packField = null;
         Field[] var6 = adapter.getClass().getDeclaredFields();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Field field = var6[var8];
            if (field.getType() == IResourcePack.class) {
               packField = field;
               break;
            }
         }

         if (packField != null) {
            packField.setAccessible(true);

            try {
               return this.getLocations((IResourcePack)packField.get(adapter), folder, predicate);
            } catch (Exception var10) {
            }
         }
      }

      List<ResourceLocation> locations = new ArrayList();
      if (pack instanceof FolderResourcePack) {
         this.handleFolderResourcePack((FolderResourcePack)pack, folder, predicate, locations);
      } else if (pack instanceof FileResourcePack) {
         this.handleZipResourcePack((FileResourcePack)pack, folder, predicate, locations);
      }

      return locations;
   }

   private void handleFolderResourcePack(FolderResourcePack folderPack, String folder, Predicate<String> predicate, List<ResourceLocation> locations) {
      Field fileField = null;
      Field[] var6 = AbstractResourcePack.class.getDeclaredFields();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Field field = var6[var8];
         if (field.getType() == File.class) {
            fileField = field;
            break;
         }
      }

      if (fileField != null) {
         fileField.setAccessible(true);

         try {
            File file = (File)fileField.get(folderPack);
            Set<String> domains = folderPack.func_110587_b();
            if (folderPack instanceof FMLFolderResourcePack) {
               domains.add(((FMLFolderResourcePack)folderPack).getFMLContainer().getModId());
            }

            Iterator var15 = domains.iterator();

            while(var15.hasNext()) {
               String domain = (String)var15.next();
               String prefix = "assets/" + domain + "/" + folder;
               File pathFile = new File(file, prefix);
               this.enumerateFiles(folderPack, pathFile, predicate, locations, domain, folder);
            }
         } catch (IllegalAccessException var12) {
            GeckoLib.LOGGER.error(var12);
         }
      }

   }

   private void enumerateFiles(FolderResourcePack folderPack, File parent, Predicate<String> predicate, List<ResourceLocation> locations, String domain, String prefix) {
      File[] files = parent.listFiles();
      if (files != null) {
         File[] var8 = files;
         int var9 = files.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            File file = var8[var10];
            if (file.isFile() && predicate.test(file.getName())) {
               locations.add(new ResourceLocation(domain, prefix + "/" + file.getName()));
            } else if (file.isDirectory()) {
               this.enumerateFiles(folderPack, file, predicate, locations, domain, prefix + "/" + file.getName());
            }
         }

      }
   }

   private void handleZipResourcePack(FileResourcePack filePack, String folder, Predicate<String> predicate, List<ResourceLocation> locations) {
      Field zipField = null;
      Field[] var6 = FileResourcePack.class.getDeclaredFields();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Field field = var6[var8];
         if (field.getType() == ZipFile.class) {
            zipField = field;
            break;
         }
      }

      if (zipField != null) {
         zipField.setAccessible(true);

         try {
            this.enumerateZipFile(filePack, folder, (ZipFile)zipField.get(filePack), predicate, locations);
         } catch (IllegalAccessException var10) {
            GeckoLib.LOGGER.error(var10);
         }
      }

   }

   private void enumerateZipFile(FileResourcePack filePack, String folder, ZipFile file, Predicate<String> predicate, List<ResourceLocation> locations) {
      Set<String> domains = filePack.func_110587_b();
      Enumeration it = file.entries();

      while(it.hasMoreElements()) {
         String name = ((ZipEntry)it.nextElement()).getName();
         Iterator var9 = domains.iterator();

         while(var9.hasNext()) {
            String domain = (String)var9.next();
            String assets = "assets/" + domain + "/";
            String path = assets + folder + "/";
            if (name.startsWith(path) && predicate.test(name)) {
               locations.add(new ResourceLocation(domain, name.substring(assets.length())));
            }
         }
      }

   }
}

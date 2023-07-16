package software.bernie.geckolib3.file;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.util.json.JsonAnimationUtils;
import software.bernie.shadowed.eliotlash.molang.MolangParser;

public class AnimationFileLoader {
   public AnimationFile loadAllAnimations(MolangParser parser, ResourceLocation location, IResourceManager manager) {
      AnimationFile animationFile = new AnimationFile();
      JsonObject jsonRepresentation = this.loadFile(location, manager);
      Set<Entry<String, JsonElement>> entrySet = JsonAnimationUtils.getAnimations(jsonRepresentation);
      Iterator var7 = entrySet.iterator();

      while(var7.hasNext()) {
         Entry<String, JsonElement> entry = (Entry)var7.next();
         String animationName = (String)entry.getKey();

         try {
            Animation animation = JsonAnimationUtils.deserializeJsonToAnimation(JsonAnimationUtils.getAnimation(jsonRepresentation, animationName), parser);
            animationFile.putAnimation(animationName, animation);
         } catch (JsonException var12) {
            GeckoLib.LOGGER.error("Could not load animation: {}", animationName, var12);
            throw new RuntimeException(var12);
         }
      }

      return animationFile;
   }

   private JsonObject loadFile(ResourceLocation location, IResourceManager manager) {
      String content = getResourceAsString(location, manager);
      Gson GSON = new Gson();
      return (JsonObject)JsonUtils.func_193839_a(GSON, new StringReader(content), JsonObject.class);
   }

   public static String getResourceAsString(ResourceLocation location, IResourceManager manager) {
      try {
         InputStream inputStream = manager.func_110536_a(location).func_110527_b();
         Throwable var17 = null;

         String var4;
         try {
            var4 = IOUtils.toString(inputStream, Charset.defaultCharset());
         } catch (Throwable var14) {
            var17 = var14;
            throw var14;
         } finally {
            if (inputStream != null) {
               if (var17 != null) {
                  try {
                     inputStream.close();
                  } catch (Throwable var13) {
                     var17.addSuppressed(var13);
                  }
               } else {
                  inputStream.close();
               }
            }

         }

         return var4;
      } catch (Exception var16) {
         String message = "Couldn't load " + location;
         GeckoLib.LOGGER.error(message, var16);
         throw new RuntimeException(new FileNotFoundException(location.toString()));
      }
   }
}

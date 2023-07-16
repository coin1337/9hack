package com.schnurritv.sexmod.util;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SkinHelper {
   @SideOnly(Side.CLIENT)
   public static BufferedImage getSkinByUUID(UUID id) throws IOException {
      try {
         URL SessionURL = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + id.toString().replace("-", ""));
         BufferedReader reader = new BufferedReader(new InputStreamReader(SessionURL.openStream()));
         String profileText = (String)reader.lines().collect(Collectors.joining());
         int charsUntilValue = profileText.indexOf("\"value\" : ");
         int charsUntilBase64 = charsUntilValue + 11;
         StringBuilder base64 = new StringBuilder();

         for(int i = 0; profileText.charAt(charsUntilBase64 + i) != '"'; ++i) {
            base64.append(profileText.charAt(charsUntilBase64 + i));
         }

         String skinText = new String(Base64.getDecoder().decode(base64.toString()));
         int charsUntilURL = skinText.indexOf("\"url\" : ");
         int charsUntilLink = charsUntilURL + 9;
         StringBuilder url = new StringBuilder();

         for(int i = 0; skinText.charAt(charsUntilLink + i) != '"'; ++i) {
            url.append(skinText.charAt(charsUntilLink + i));
         }

         URL skinUrl = new URL(url.toString());
         return ImageIO.read(skinUrl);
      } catch (Exception var12) {
         return ImageIO.read(Minecraft.func_71410_x().func_110442_L().func_110536_a(new ResourceLocation("sexmod", "textures/player/steve.png")).func_110527_b());
      }
   }
}

package com.schnurritv.sexmod.girls.allie.lamp;

import com.schnurritv.sexmod.util.SkinHelper;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class LampModel extends AnimatedGeoModel<LampItem> {
   ResourceLocation skin = null;

   public ResourceLocation getModelLocation(LampItem object) {
      return new ResourceLocation("sexmod", "geo/allie/lamp.geo.json");
   }

   public ResourceLocation getTextureLocation(LampItem object) {
      if (this.skin != null) {
         return this.skin;
      } else {
         try {
            Minecraft mc = Minecraft.func_71410_x();
            BufferedImage skinImage = SkinHelper.getSkinByUUID(mc.field_71439_g.getPersistentID());
            Graphics graphics = skinImage.getGraphics();
            graphics.setColor(new Color(185, 254, 255));
            graphics.fillRect(0, 0, 2, 2);
            graphics.setColor(new Color(255, 255, 255));
            graphics.fillRect(2, 0, 1, 2);
            graphics.setColor(new Color(0, 0, 0));
            graphics.fillRect(3, 0, 1, 2);
            this.skin = mc.field_71446_o.func_110578_a("alliesLamp", new DynamicTexture(skinImage));
         } catch (IOException var5) {
            var5.printStackTrace();
            this.skin = new ResourceLocation("sexmod", "textures/entity/allie/lamp.png");
         }

         return this.skin;
      }
   }

   public ResourceLocation getAnimationFileLocation(LampItem animatable) {
      return new ResourceLocation("sexmod", "animations/allie/lamp.animation.json");
   }
}

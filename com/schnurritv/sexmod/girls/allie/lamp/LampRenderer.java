package com.schnurritv.sexmod.girls.allie.lamp;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Base64;
import java.util.Iterator;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class LampRenderer extends GeoItemRenderer<LampItem> {
   Minecraft mc = Minecraft.func_71410_x();
   static ResourceLocation playerSkin = null;

   public LampRenderer() {
      super(new LampModel());
   }

   ResourceLocation getPlayerSkin() {
      if (playerSkin == null) {
         try {
            URL SessionURL = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + Minecraft.func_71410_x().field_71439_g.getPersistentID().toString().replace("-", ""));
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
            BufferedImage skinImage = ImageIO.read(skinUrl);
            BufferedImage lampTex = ImageIO.read(this.mc.func_110442_L().func_110536_a((new LampModel()).getTextureLocation(new LampItem())).func_110527_b());

            for(int x = 0; x < lampTex.getWidth(); ++x) {
               for(int y = 0; y < lampTex.getHeight(); ++y) {
                  int rgb = skinImage.getRGB(x, y);
                  System.out.println(rgb);
                  if (rgb != 0) {
                     lampTex.setRGB(x, y, rgb);
                  }
               }
            }

            playerSkin = Minecraft.func_71410_x().func_175598_ae().field_78724_e.func_110578_a("lamptex", new DynamicTexture(lampTex));
         } catch (Exception var17) {
            playerSkin = (new LampModel()).getTextureLocation(new LampItem());
         }
      }

      return playerSkin;
   }

   public void render(GeoModel model, LampItem animatable, float partialTicks, float red, float green, float blue, float alpha) {
      GlStateManager.func_179129_p();
      GlStateManager.func_179091_B();
      this.renderEarly(animatable, partialTicks, red, green, blue, alpha);
      this.renderLate(animatable, partialTicks, red, green, blue, alpha);
      BufferBuilder builder = Tessellator.func_178181_a().func_178180_c();
      builder.func_181668_a(7, DefaultVertexFormats.field_181712_l);
      Iterator var9 = model.topLevelBones.iterator();

      while(var9.hasNext()) {
         GeoBone group = (GeoBone)var9.next();
         this.renderRecursively(builder, animatable, group, red, green, blue, alpha);
      }

      Tessellator.func_178181_a().func_78381_a();
      this.renderAfter(animatable, partialTicks, red, green, blue, alpha);
      GlStateManager.func_179101_C();
      GlStateManager.func_179089_o();
   }

   public void renderRecursively(BufferBuilder builder, LampItem animatable, GeoBone bone, float red, float green, float blue, float alpha) {
      MATRIX_STACK.push();
      MATRIX_STACK.translate(bone);
      MATRIX_STACK.moveToPivot(bone);
      MATRIX_STACK.rotate(bone);
      MATRIX_STACK.scale(bone);
      MATRIX_STACK.moveBackFromPivot(bone);
      String name = bone.getName();
      Minecraft.func_71410_x().field_71446_o.func_110577_a(this.getPlayerSkin());
      if (!name.equals("leftArm") && !name.equals("rightArm")) {
         this.renderBoneNormally(builder, animatable, bone, red, green, blue, alpha);
      } else {
         ItemStack stack = this.mc.field_71439_g.func_184614_ca();
         if (stack.func_77942_o()) {
            NBTTagCompound nbt = stack.func_77978_p();
            if (nbt.func_186857_a("user").equals(this.mc.field_71439_g.getPersistentID()) && this.mc.field_71474_y.field_74320_O == 0) {
               this.renderBoneNormally(builder, animatable, bone, red, green, blue, alpha);
            }
         }
      }

      MATRIX_STACK.pop();
   }

   void renderBoneNormally(BufferBuilder builder, LampItem animatable, GeoBone bone, float red, float green, float blue, float alpha) {
      if (!bone.isHidden) {
         Iterator var8 = bone.childCubes.iterator();

         while(var8.hasNext()) {
            GeoCube cube = (GeoCube)var8.next();
            MATRIX_STACK.push();
            GlStateManager.func_179094_E();
            this.renderCube(builder, cube, red, green, blue, alpha);
            GlStateManager.func_179121_F();
            MATRIX_STACK.pop();
         }

         var8 = bone.childBones.iterator();

         while(var8.hasNext()) {
            GeoBone childBone = (GeoBone)var8.next();
            this.renderRecursively(builder, animatable, childBone, red, green, blue, alpha);
         }
      }

   }
}

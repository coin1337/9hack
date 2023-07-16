package software.bernie.geckolib3.renderers.geo;

import java.util.Iterator;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.geo.render.built.GeoQuad;
import software.bernie.geckolib3.geo.render.built.GeoVertex;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.util.MatrixStack;

public interface IGeoRenderer<T> {
   MatrixStack MATRIX_STACK = new MatrixStack();

   default void render(GeoModel model, T animatable, float partialTicks, float red, float green, float blue, float alpha) {
      GlStateManager.func_179129_p();
      GlStateManager.func_179091_B();
      this.renderEarly(animatable, partialTicks, red, green, blue, alpha);
      this.renderLate(animatable, partialTicks, red, green, blue, alpha);
      BufferBuilder builder = Tessellator.func_178181_a().func_178180_c();
      builder.func_181668_a(7, DefaultVertexFormats.field_181712_l);
      Iterator var9 = model.topLevelBones.iterator();

      while(var9.hasNext()) {
         GeoBone group = (GeoBone)var9.next();
         this.renderRecursively(builder, group, red, green, blue, alpha);
      }

      Tessellator.func_178181_a().func_78381_a();
      this.renderAfter(animatable, partialTicks, red, green, blue, alpha);
      GlStateManager.func_179101_C();
      GlStateManager.func_179089_o();
   }

   default void renderRecursively(BufferBuilder builder, GeoBone bone, float red, float green, float blue, float alpha) {
      MATRIX_STACK.push();
      MATRIX_STACK.translate(bone);
      MATRIX_STACK.moveToPivot(bone);
      MATRIX_STACK.rotate(bone);
      MATRIX_STACK.scale(bone);
      MATRIX_STACK.moveBackFromPivot(bone);
      Iterator var7;
      if (!bone.isHidden()) {
         var7 = bone.childCubes.iterator();

         while(var7.hasNext()) {
            GeoCube cube = (GeoCube)var7.next();
            MATRIX_STACK.push();
            GlStateManager.func_179094_E();
            this.renderCube(builder, cube, red, green, blue, alpha);
            GlStateManager.func_179121_F();
            MATRIX_STACK.pop();
         }
      }

      if (!bone.childBonesAreHiddenToo()) {
         var7 = bone.childBones.iterator();

         while(var7.hasNext()) {
            GeoBone childBone = (GeoBone)var7.next();
            this.renderRecursively(builder, childBone, red, green, blue, alpha);
         }
      }

      MATRIX_STACK.pop();
   }

   default void renderCube(BufferBuilder builder, GeoCube cube, float red, float green, float blue, float alpha) {
      MATRIX_STACK.moveToPivot(cube);
      MATRIX_STACK.rotate(cube);
      MATRIX_STACK.moveBackFromPivot(cube);
      GeoQuad[] var7 = cube.quads;
      int var8 = var7.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         GeoQuad quad = var7[var9];
         Vector3f normal = new Vector3f((float)quad.normal.func_177958_n(), (float)quad.normal.func_177956_o(), (float)quad.normal.func_177952_p());
         MATRIX_STACK.getNormalMatrix().transform(normal);
         if ((cube.size.y == 0.0F || cube.size.z == 0.0F) && normal.getX() < 0.0F) {
            normal.x *= -1.0F;
         }

         if ((cube.size.x == 0.0F || cube.size.z == 0.0F) && normal.getY() < 0.0F) {
            normal.y *= -1.0F;
         }

         if ((cube.size.x == 0.0F || cube.size.y == 0.0F) && normal.getZ() < 0.0F) {
            normal.z *= -1.0F;
         }

         GeoVertex[] var12 = quad.vertices;
         int var13 = var12.length;

         for(int var14 = 0; var14 < var13; ++var14) {
            GeoVertex vertex = var12[var14];
            Vector4f vector4f = new Vector4f(vertex.position.getX(), vertex.position.getY(), vertex.position.getZ(), 1.0F);
            MATRIX_STACK.getModelMatrix().transform(vector4f);
            builder.func_181662_b((double)vector4f.getX(), (double)vector4f.getY(), (double)vector4f.getZ()).func_187315_a((double)vertex.textureU, (double)vertex.textureV).func_181666_a(red, green, blue, alpha).func_181663_c(normal.getX(), normal.getY(), normal.getZ()).func_181675_d();
         }
      }

   }

   GeoModelProvider getGeoModelProvider();

   ResourceLocation getTextureLocation(T var1);

   default void renderEarly(T animatable, float ticks, float red, float green, float blue, float partialTicks) {
   }

   default void renderLate(T animatable, float ticks, float red, float green, float blue, float partialTicks) {
   }

   default void renderAfter(T animatable, float ticks, float red, float green, float blue, float partialTicks) {
   }

   default Color getRenderColor(T animatable, float partialTicks) {
      return Color.ofRGBA(255, 255, 255, 255);
   }

   default Integer getUniqueID(T animatable) {
      return animatable.hashCode();
   }
}

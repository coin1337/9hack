package com.schnurritv.sexmod.util;

import java.nio.FloatBuffer;
import javax.vecmath.Matrix4f;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.util.MatrixStack;

public class MatrixHelper {
   public static final float[] floats = new float[16];
   public static final FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
   private static final Matrix4f matrix = new Matrix4f();

   public static void multiplyMatrix(MatrixStack stack, GeoBone bone) {
      matrix.set(stack.getModelMatrix());
      matrix.transpose();
      matrixToFloat(floats, matrix);
      buffer.clear();
      buffer.put(floats);
      buffer.flip();
      GlStateManager.func_179110_a(buffer);
      GlStateManager.func_179109_b(bone.rotationPointX / 16.0F, bone.rotationPointY / 16.0F, bone.rotationPointZ / 16.0F);
   }

   public static void matrixToFloat(float[] floats, Matrix4f matrix4f) {
      floats[0] = matrix4f.m00;
      floats[1] = matrix4f.m01;
      floats[2] = matrix4f.m02;
      floats[3] = matrix4f.m03;
      floats[4] = matrix4f.m10;
      floats[5] = matrix4f.m11;
      floats[6] = matrix4f.m12;
      floats[7] = matrix4f.m13;
      floats[8] = matrix4f.m20;
      floats[9] = matrix4f.m21;
      floats[10] = matrix4f.m22;
      floats[11] = matrix4f.m23;
      floats[12] = matrix4f.m30;
      floats[13] = matrix4f.m31;
      floats[14] = matrix4f.m32;
      floats[15] = matrix4f.m33;
   }
}

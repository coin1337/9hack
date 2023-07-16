package software.bernie.geckolib3.util;

import java.util.Stack;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import org.lwjgl.util.vector.Quaternion;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;

public class MatrixStack {
   private Stack<Matrix4f> model = new Stack();
   private Stack<Matrix3f> normal = new Stack();
   private Matrix4f tempModelMatrix = new Matrix4f();
   private Matrix3f tempNormalMatrix = new Matrix3f();
   private float[] tempArray = new float[16];

   public MatrixStack() {
      Matrix4f model = new Matrix4f();
      Matrix3f normal = new Matrix3f();
      model.setIdentity();
      normal.setIdentity();
      this.model.add(model);
      this.normal.add(normal);
   }

   public Matrix4f getModelMatrix() {
      return (Matrix4f)this.model.peek();
   }

   public Matrix3f getNormalMatrix() {
      return (Matrix3f)this.normal.peek();
   }

   public void push() {
      this.model.add(new Matrix4f((Matrix4f)this.model.peek()));
      this.normal.add(new Matrix3f((Matrix3f)this.normal.peek()));
   }

   public void pop() {
      if (this.model.size() == 1) {
         throw new IllegalStateException("A one level stack can't be popped!");
      } else {
         this.model.pop();
         this.normal.pop();
      }
   }

   public void translate(float x, float y, float z) {
      this.translate(new Vector3f(x, y, z));
   }

   public void translate(Vector3f vec) {
      this.tempModelMatrix.setIdentity();
      this.tempModelMatrix.setTranslation(vec);
      ((Matrix4f)this.model.peek()).mul(this.tempModelMatrix);
   }

   public void moveToPivot(GeoCube cube) {
      Vector3f pivot = cube.pivot;
      this.translate(pivot.getX() / 16.0F, pivot.getY() / 16.0F, pivot.getZ() / 16.0F);
   }

   public void moveBackFromPivot(GeoCube cube) {
      Vector3f pivot = cube.pivot;
      this.translate(-pivot.getX() / 16.0F, -pivot.getY() / 16.0F, -pivot.getZ() / 16.0F);
   }

   public void moveToPivot(GeoBone bone) {
      this.translate(bone.rotationPointX / 16.0F, bone.rotationPointY / 16.0F, bone.rotationPointZ / 16.0F);
   }

   public void moveBackFromPivot(GeoBone bone) {
      this.translate(-bone.rotationPointX / 16.0F, -bone.rotationPointY / 16.0F, -bone.rotationPointZ / 16.0F);
   }

   public void translate(GeoBone bone) {
      this.translate(-bone.getPositionX() / 16.0F, bone.getPositionY() / 16.0F, bone.getPositionZ() / 16.0F);
   }

   public void scale(float x, float y, float z) {
      this.tempModelMatrix.setIdentity();
      this.tempModelMatrix.setM00(x);
      this.tempModelMatrix.setM11(y);
      this.tempModelMatrix.setM22(z);
      ((Matrix4f)this.model.peek()).mul(this.tempModelMatrix);
      if (x < 0.0F || y < 0.0F || z < 0.0F) {
         this.tempNormalMatrix.setIdentity();
         this.tempNormalMatrix.setM00(x < 0.0F ? -1.0F : 1.0F);
         this.tempNormalMatrix.setM11(y < 0.0F ? -1.0F : 1.0F);
         this.tempNormalMatrix.setM22(z < 0.0F ? -1.0F : 1.0F);
         ((Matrix3f)this.normal.peek()).mul(this.tempNormalMatrix);
      }

   }

   public void scale(GeoBone bone) {
      this.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
   }

   public void rotateX(float radian) {
      this.tempModelMatrix.setIdentity();
      this.tempModelMatrix.rotX(radian);
      this.tempNormalMatrix.setIdentity();
      this.tempNormalMatrix.rotX(radian);
      ((Matrix4f)this.model.peek()).mul(this.tempModelMatrix);
      ((Matrix3f)this.normal.peek()).mul(this.tempNormalMatrix);
   }

   public void rotateY(float radian) {
      this.tempModelMatrix.setIdentity();
      this.tempModelMatrix.rotY(radian);
      this.tempNormalMatrix.setIdentity();
      this.tempNormalMatrix.rotY(radian);
      ((Matrix4f)this.model.peek()).mul(this.tempModelMatrix);
      ((Matrix3f)this.normal.peek()).mul(this.tempNormalMatrix);
   }

   public void rotateZ(float radian) {
      this.tempModelMatrix.setIdentity();
      this.tempModelMatrix.rotZ(radian);
      this.tempNormalMatrix.setIdentity();
      this.tempNormalMatrix.rotZ(radian);
      ((Matrix4f)this.model.peek()).mul(this.tempModelMatrix);
      ((Matrix3f)this.normal.peek()).mul(this.tempNormalMatrix);
   }

   public void rotate(GeoBone bone) {
      if (bone.getRotationZ() != 0.0F) {
         this.rotateZ(bone.getRotationZ());
      }

      if (bone.getRotationY() != 0.0F) {
         this.rotateY(bone.getRotationY());
      }

      if (bone.getRotationX() != 0.0F) {
         this.rotateX(bone.getRotationX());
      }

   }

   public void rotate(GeoCube bone) {
      Vector3f rotation = bone.rotation;
      Matrix4f matrix4f = new Matrix4f();
      Matrix3f matrix3f = new Matrix3f();
      this.tempModelMatrix.setIdentity();
      matrix4f.rotZ(rotation.getZ());
      this.tempModelMatrix.mul(matrix4f);
      matrix4f.rotY(rotation.getY());
      this.tempModelMatrix.mul(matrix4f);
      matrix4f.rotX(rotation.getX());
      this.tempModelMatrix.mul(matrix4f);
      this.tempNormalMatrix.setIdentity();
      matrix3f.rotZ(rotation.getZ());
      this.tempNormalMatrix.mul(matrix3f);
      matrix3f.rotY(rotation.getY());
      this.tempNormalMatrix.mul(matrix3f);
      matrix3f.rotX(rotation.getX());
      this.tempNormalMatrix.mul(matrix3f);
      ((Matrix4f)this.model.peek()).mul(this.tempModelMatrix);
      ((Matrix3f)this.normal.peek()).mul(this.tempNormalMatrix);
   }

   private Quaternion fromAngles(float x, float y, float z) {
      float sx = (float)Math.sin((double)(0.5F * x));
      float cx = (float)Math.cos((double)(0.5F * x));
      float sy = (float)Math.sin((double)(0.5F * y));
      float cy = (float)Math.cos((double)(0.5F * y));
      float sz = (float)Math.sin((double)(0.5F * z));
      float cz = (float)Math.cos((double)(0.5F * z));
      float ox = sx * cy * cz + cx * sy * sz;
      float oy = cx * sy * cz - sx * cy * sz;
      float oz = sx * sy * cz + cx * cy * sz;
      float ow = cx * cy * cz - sx * sy * sz;
      return new Quaternion(ox, oy, oz, ow);
   }
}

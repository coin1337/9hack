package software.bernie.geckolib3.geo.render.built;

import java.util.ArrayList;
import java.util.List;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.snapshot.BoneSnapshot;

public class GeoBone implements IBone {
   public GeoBone parent;
   public List<GeoBone> childBones = new ArrayList();
   public List<GeoCube> childCubes = new ArrayList();
   public String name;
   private BoneSnapshot initialSnapshot;
   public Boolean mirror;
   public Double inflate;
   public Boolean dontRender;
   public boolean isHidden;
   public boolean areCubesHidden = false;
   public boolean hideChildBonesToo;
   public Boolean reset;
   private float scaleX = 1.0F;
   private float scaleY = 1.0F;
   private float scaleZ = 1.0F;
   private float positionX;
   private float positionY;
   private float positionZ;
   public float rotationPointX;
   public float rotationPointY;
   public float rotationPointZ;
   private float rotateX;
   private float rotateY;
   private float rotateZ;
   public Object extraData;

   public void setModelRendererName(String modelRendererName) {
      this.name = modelRendererName;
   }

   public void saveInitialSnapshot() {
      if (this.initialSnapshot == null) {
         this.initialSnapshot = new BoneSnapshot(this, true);
      }

   }

   public BoneSnapshot getInitialSnapshot() {
      return this.initialSnapshot;
   }

   public String getName() {
      return this.name;
   }

   public float getRotationX() {
      return this.rotateX;
   }

   public float getRotationY() {
      return this.rotateY;
   }

   public float getRotationZ() {
      return this.rotateZ;
   }

   public float getPositionX() {
      return this.positionX;
   }

   public float getPositionY() {
      return this.positionY;
   }

   public float getPositionZ() {
      return this.positionZ;
   }

   public float getScaleX() {
      return this.scaleX;
   }

   public float getScaleY() {
      return this.scaleY;
   }

   public float getScaleZ() {
      return this.scaleZ;
   }

   public void setRotationX(float value) {
      this.rotateX = value;
   }

   public void setRotationY(float value) {
      this.rotateY = value;
   }

   public void setRotationZ(float value) {
      this.rotateZ = value;
   }

   public void setPositionX(float value) {
      this.positionX = value;
   }

   public void setPositionY(float value) {
      this.positionY = value;
   }

   public void setPositionZ(float value) {
      this.positionZ = value;
   }

   public void setScaleX(float value) {
      this.scaleX = value;
   }

   public void setScaleY(float value) {
      this.scaleY = value;
   }

   public void setScaleZ(float value) {
      this.scaleZ = value;
   }

   public boolean isHidden() {
      return this.isHidden;
   }

   public void setHidden(boolean hidden) {
      this.setHidden(hidden, hidden);
   }

   public void setPivotX(float value) {
      this.rotationPointX = value;
   }

   public void setPivotY(float value) {
      this.rotationPointY = value;
   }

   public void setPivotZ(float value) {
      this.rotationPointZ = value;
   }

   public float getPivotX() {
      return this.rotationPointX;
   }

   public float getPivotY() {
      return this.rotationPointY;
   }

   public float getPivotZ() {
      return this.rotationPointZ;
   }

   public boolean cubesAreHidden() {
      return this.areCubesHidden;
   }

   public boolean childBonesAreHiddenToo() {
      return this.hideChildBonesToo;
   }

   public void setCubesHidden(boolean hidden) {
      this.areCubesHidden = hidden;
   }

   public void setHidden(boolean selfHidden, boolean skipChildRendering) {
      this.isHidden = selfHidden;
      this.hideChildBonesToo = skipChildRendering;
   }
}

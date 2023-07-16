package software.bernie.geckolib3.core.processor;

import software.bernie.geckolib3.core.snapshot.BoneSnapshot;

public interface IBone {
   float getRotationX();

   float getRotationY();

   float getRotationZ();

   float getPositionX();

   float getPositionY();

   float getPositionZ();

   float getScaleX();

   float getScaleY();

   float getScaleZ();

   void setRotationX(float var1);

   void setRotationY(float var1);

   void setRotationZ(float var1);

   void setPositionX(float var1);

   void setPositionY(float var1);

   void setPositionZ(float var1);

   void setScaleX(float var1);

   void setScaleY(float var1);

   void setScaleZ(float var1);

   void setPivotX(float var1);

   void setPivotY(float var1);

   void setPivotZ(float var1);

   float getPivotX();

   float getPivotY();

   float getPivotZ();

   boolean isHidden();

   boolean cubesAreHidden();

   boolean childBonesAreHiddenToo();

   void setHidden(boolean var1);

   void setCubesHidden(boolean var1);

   void setHidden(boolean var1, boolean var2);

   void setModelRendererName(String var1);

   void saveInitialSnapshot();

   BoneSnapshot getInitialSnapshot();

   default BoneSnapshot saveSnapshot() {
      return new BoneSnapshot(this);
   }

   String getName();
}

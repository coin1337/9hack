package software.bernie.geckolib3.core.keyframe;

import software.bernie.shadowed.eliotlash.mclib.math.IValue;

public class BoneAnimation {
   public String boneName;
   public VectorKeyFrameList<KeyFrame<IValue>> rotationKeyFrames;
   public VectorKeyFrameList<KeyFrame<IValue>> positionKeyFrames;
   public VectorKeyFrameList<KeyFrame<IValue>> scaleKeyFrames;
}

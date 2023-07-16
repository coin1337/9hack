package software.bernie.geckolib3.core.builder;

import java.util.ArrayList;
import java.util.List;
import software.bernie.geckolib3.core.keyframe.BoneAnimation;
import software.bernie.geckolib3.core.keyframe.EventKeyFrame;
import software.bernie.geckolib3.core.keyframe.ParticleEventKeyFrame;

public class Animation {
   public String animationName;
   public Double animationLength;
   public boolean loop = true;
   public List<BoneAnimation> boneAnimations;
   public List<EventKeyFrame<String>> soundKeyFrames = new ArrayList();
   public List<ParticleEventKeyFrame> particleKeyFrames = new ArrayList();
   public List<EventKeyFrame<String>> customInstructionKeyframes = new ArrayList();
}

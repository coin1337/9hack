package software.bernie.geckolib3.core.keyframe;

public class KeyFrameLocation<T extends KeyFrame> {
   public T currentFrame;
   public double currentTick;

   public KeyFrameLocation(T currentFrame, double currentTick) {
      this.currentFrame = currentFrame;
      this.currentTick = currentTick;
   }
}

package software.bernie.geckolib3.core.builder;

import java.util.Objects;

public class RawAnimation {
   public String animationName;
   public Boolean loop;

   public RawAnimation(String animationName, Boolean loop) {
      this.animationName = animationName;
      this.loop = loop;
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof RawAnimation)) {
         return false;
      } else {
         RawAnimation animation = (RawAnimation)obj;
         return animation.loop == this.loop && animation.animationName.equals(this.animationName);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.animationName, this.loop});
   }
}

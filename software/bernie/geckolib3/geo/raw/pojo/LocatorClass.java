package software.bernie.geckolib3.geo.raw.pojo;

import software.bernie.shadowed.fasterxml.jackson.annotation.JsonProperty;

public class LocatorClass {
   private Boolean ignoreInheritedScale;
   private double[] offset;
   private double[] rotation;

   @JsonProperty("ignore_inherited_scale")
   public Boolean getIgnoreInheritedScale() {
      return this.ignoreInheritedScale;
   }

   @JsonProperty("ignore_inherited_scale")
   public void setIgnoreInheritedScale(Boolean value) {
      this.ignoreInheritedScale = value;
   }

   @JsonProperty("offset")
   public double[] getOffset() {
      return this.offset;
   }

   @JsonProperty("offset")
   public void setOffset(double[] value) {
      this.offset = value;
   }

   @JsonProperty("rotation")
   public double[] getRotation() {
      return this.rotation;
   }

   @JsonProperty("rotation")
   public void setRotation(double[] value) {
      this.rotation = value;
   }
}

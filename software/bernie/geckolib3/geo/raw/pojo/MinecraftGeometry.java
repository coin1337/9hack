package software.bernie.geckolib3.geo.raw.pojo;

import software.bernie.shadowed.fasterxml.jackson.annotation.JsonProperty;

public class MinecraftGeometry {
   private Bone[] bones;
   private String cape;
   private ModelProperties modelProperties;

   @JsonProperty("bones")
   public Bone[] getBones() {
      return this.bones;
   }

   @JsonProperty("bones")
   public void setBones(Bone[] value) {
      this.bones = value;
   }

   @JsonProperty("cape")
   public String getCape() {
      return this.cape;
   }

   @JsonProperty("cape")
   public void setCape(String value) {
      this.cape = value;
   }

   @JsonProperty("description")
   public ModelProperties getProperties() {
      return this.modelProperties;
   }

   @JsonProperty("description")
   public void setProperties(ModelProperties value) {
      this.modelProperties = value;
   }
}

package software.bernie.geckolib3.geo.raw.pojo;

import software.bernie.shadowed.fasterxml.jackson.annotation.JsonProperty;

public class FaceUv {
   private String materialInstance;
   private double[] uv;
   private double[] uvSize;

   @JsonProperty("material_instance")
   public String getMaterialInstance() {
      return this.materialInstance;
   }

   @JsonProperty("material_instance")
   public void setMaterialInstance(String value) {
      this.materialInstance = value;
   }

   @JsonProperty("uv")
   public double[] getUv() {
      return this.uv;
   }

   @JsonProperty("uv")
   public void setUv(double[] value) {
      this.uv = value;
   }

   @JsonProperty("uv_size")
   public double[] getUvSize() {
      return this.uvSize;
   }

   @JsonProperty("uv_size")
   public void setUvSize(double[] value) {
      this.uvSize = value;
   }
}

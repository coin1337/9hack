package software.bernie.geckolib3.geo.raw.pojo;

import software.bernie.shadowed.fasterxml.jackson.annotation.JsonProperty;

public class PolyMesh {
   private Boolean normalizedUvs;
   private double[] normals;
   private PolysUnion polys;
   private double[] positions;
   private double[] uvs;

   @JsonProperty("normalized_uvs")
   public Boolean getNormalizedUvs() {
      return this.normalizedUvs;
   }

   @JsonProperty("normalized_uvs")
   public void setNormalizedUvs(Boolean value) {
      this.normalizedUvs = value;
   }

   @JsonProperty("normals")
   public double[] getNormals() {
      return this.normals;
   }

   @JsonProperty("normals")
   public void setNormals(double[] value) {
      this.normals = value;
   }

   @JsonProperty("polys")
   public PolysUnion getPolys() {
      return this.polys;
   }

   @JsonProperty("polys")
   public void setPolys(PolysUnion value) {
      this.polys = value;
   }

   @JsonProperty("positions")
   public double[] getPositions() {
      return this.positions;
   }

   @JsonProperty("positions")
   public void setPositions(double[] value) {
      this.positions = value;
   }

   @JsonProperty("uvs")
   public double[] getUvs() {
      return this.uvs;
   }

   @JsonProperty("uvs")
   public void setUvs(double[] value) {
      this.uvs = value;
   }
}

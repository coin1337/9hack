package software.bernie.geckolib3.geo.raw.pojo;

import java.util.Map;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonProperty;

public class Bone {
   private double[] bindPoseRotation;
   private Cube[] cubes;
   private Boolean debug;
   private Double inflate;
   private Map<String, LocatorValue> locators;
   private Boolean mirror;
   private String name;
   private Boolean neverRender;
   private String parent;
   private double[] pivot = new double[]{0.0D, 0.0D, 0.0D};
   private PolyMesh polyMesh;
   private Long renderGroupID;
   private Boolean reset;
   private double[] rotation = new double[]{0.0D, 0.0D, 0.0D};
   private TextureMesh[] textureMeshes;

   @JsonProperty("bind_pose_rotation")
   public double[] getBindPoseRotation() {
      return this.bindPoseRotation;
   }

   @JsonProperty("bind_pose_rotation")
   public void setBindPoseRotation(double[] value) {
      this.bindPoseRotation = value;
   }

   @JsonProperty("cubes")
   public Cube[] getCubes() {
      return this.cubes;
   }

   @JsonProperty("cubes")
   public void setCubes(Cube[] value) {
      this.cubes = value;
   }

   @JsonProperty("debug")
   public Boolean getDebug() {
      return this.debug;
   }

   @JsonProperty("debug")
   public void setDebug(Boolean value) {
      this.debug = value;
   }

   @JsonProperty("inflate")
   public Double getInflate() {
      return this.inflate;
   }

   @JsonProperty("inflate")
   public void setInflate(Double value) {
      this.inflate = value;
   }

   @JsonProperty("locators")
   public Map<String, LocatorValue> getLocators() {
      return this.locators;
   }

   @JsonProperty("locators")
   public void setLocators(Map<String, LocatorValue> value) {
      this.locators = value;
   }

   @JsonProperty("mirror")
   public Boolean getMirror() {
      return this.mirror;
   }

   @JsonProperty("mirror")
   public void setMirror(Boolean value) {
      this.mirror = value;
   }

   @JsonProperty("name")
   public String getName() {
      return this.name;
   }

   @JsonProperty("name")
   public void setName(String value) {
      this.name = value;
   }

   @JsonProperty("neverRender")
   public Boolean getNeverRender() {
      return this.neverRender;
   }

   @JsonProperty("neverRender")
   public void setNeverRender(Boolean value) {
      this.neverRender = value;
   }

   @JsonProperty("parent")
   public String getParent() {
      return this.parent;
   }

   @JsonProperty("parent")
   public void setParent(String value) {
      this.parent = value;
   }

   @JsonProperty("pivot")
   public double[] getPivot() {
      return this.pivot;
   }

   @JsonProperty("pivot")
   public void setPivot(double[] value) {
      this.pivot = value;
   }

   @JsonProperty("poly_mesh")
   public PolyMesh getPolyMesh() {
      return this.polyMesh;
   }

   @JsonProperty("poly_mesh")
   public void setPolyMesh(PolyMesh value) {
      this.polyMesh = value;
   }

   @JsonProperty("render_group_id")
   public Long getRenderGroupID() {
      return this.renderGroupID;
   }

   @JsonProperty("render_group_id")
   public void setRenderGroupID(Long value) {
      this.renderGroupID = value;
   }

   @JsonProperty("reset")
   public Boolean getReset() {
      return this.reset;
   }

   @JsonProperty("reset")
   public void setReset(Boolean value) {
      this.reset = value;
   }

   @JsonProperty("rotation")
   public double[] getRotation() {
      return this.rotation;
   }

   @JsonProperty("rotation")
   public void setRotation(double[] value) {
      this.rotation = value;
   }

   @JsonProperty("texture_meshes")
   public TextureMesh[] getTextureMeshes() {
      return this.textureMeshes;
   }

   @JsonProperty("texture_meshes")
   public void setTextureMeshes(TextureMesh[] value) {
      this.textureMeshes = value;
   }
}

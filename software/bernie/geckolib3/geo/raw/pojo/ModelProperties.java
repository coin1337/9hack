package software.bernie.geckolib3.geo.raw.pojo;

import software.bernie.shadowed.fasterxml.jackson.annotation.JsonProperty;

public class ModelProperties {
   private Boolean animationArmsDown;
   private Boolean animationArmsOutFront;
   private Boolean animationDontShowArmor;
   private Boolean animationInvertedCrouch;
   private Boolean animationNoHeadBob;
   private Boolean animationSingleArmAnimation;
   private Boolean animationSingleLegAnimation;
   private Boolean animationStationaryLegs;
   private Boolean animationStatueOfLibertyArms;
   private Boolean animationUpsideDown;
   private String identifier;
   private Boolean preserveModelPose;
   private Double textureHeight;
   private Double textureWidth;
   private Double visibleBoundsHeight;
   private double[] visibleBoundsOffset;
   private Double visibleBoundsWidth;

   @JsonProperty("animationArmsDown")
   public Boolean getAnimationArmsDown() {
      return this.animationArmsDown;
   }

   @JsonProperty("animationArmsDown")
   public void setAnimationArmsDown(Boolean value) {
      this.animationArmsDown = value;
   }

   @JsonProperty("animationArmsOutFront")
   public Boolean getAnimationArmsOutFront() {
      return this.animationArmsOutFront;
   }

   @JsonProperty("animationArmsOutFront")
   public void setAnimationArmsOutFront(Boolean value) {
      this.animationArmsOutFront = value;
   }

   @JsonProperty("animationDontShowArmor")
   public Boolean getAnimationDontShowArmor() {
      return this.animationDontShowArmor;
   }

   @JsonProperty("animationDontShowArmor")
   public void setAnimationDontShowArmor(Boolean value) {
      this.animationDontShowArmor = value;
   }

   @JsonProperty("animationInvertedCrouch")
   public Boolean getAnimationInvertedCrouch() {
      return this.animationInvertedCrouch;
   }

   @JsonProperty("animationInvertedCrouch")
   public void setAnimationInvertedCrouch(Boolean value) {
      this.animationInvertedCrouch = value;
   }

   @JsonProperty("animationNoHeadBob")
   public Boolean getAnimationNoHeadBob() {
      return this.animationNoHeadBob;
   }

   @JsonProperty("animationNoHeadBob")
   public void setAnimationNoHeadBob(Boolean value) {
      this.animationNoHeadBob = value;
   }

   @JsonProperty("animationSingleArmAnimation")
   public Boolean getAnimationSingleArmAnimation() {
      return this.animationSingleArmAnimation;
   }

   @JsonProperty("animationSingleArmAnimation")
   public void setAnimationSingleArmAnimation(Boolean value) {
      this.animationSingleArmAnimation = value;
   }

   @JsonProperty("animationSingleLegAnimation")
   public Boolean getAnimationSingleLegAnimation() {
      return this.animationSingleLegAnimation;
   }

   @JsonProperty("animationSingleLegAnimation")
   public void setAnimationSingleLegAnimation(Boolean value) {
      this.animationSingleLegAnimation = value;
   }

   @JsonProperty("animationStationaryLegs")
   public Boolean getAnimationStationaryLegs() {
      return this.animationStationaryLegs;
   }

   @JsonProperty("animationStationaryLegs")
   public void setAnimationStationaryLegs(Boolean value) {
      this.animationStationaryLegs = value;
   }

   @JsonProperty("animationStatueOfLibertyArms")
   public Boolean getAnimationStatueOfLibertyArms() {
      return this.animationStatueOfLibertyArms;
   }

   @JsonProperty("animationStatueOfLibertyArms")
   public void setAnimationStatueOfLibertyArms(Boolean value) {
      this.animationStatueOfLibertyArms = value;
   }

   @JsonProperty("animationUpsideDown")
   public Boolean getAnimationUpsideDown() {
      return this.animationUpsideDown;
   }

   @JsonProperty("animationUpsideDown")
   public void setAnimationUpsideDown(Boolean value) {
      this.animationUpsideDown = value;
   }

   @JsonProperty("identifier")
   public String getIdentifier() {
      return this.identifier;
   }

   @JsonProperty("identifier")
   public void setIdentifier(String value) {
      this.identifier = value;
   }

   @JsonProperty("preserve_model_pose")
   public Boolean getPreserveModelPose() {
      return this.preserveModelPose;
   }

   @JsonProperty("preserve_model_pose")
   public void setPreserveModelPose(Boolean value) {
      this.preserveModelPose = value;
   }

   @JsonProperty("texture_height")
   public Double getTextureHeight() {
      return this.textureHeight;
   }

   @JsonProperty("texture_height")
   public void setTextureHeight(Double value) {
      this.textureHeight = value;
   }

   @JsonProperty("texture_width")
   public Double getTextureWidth() {
      return this.textureWidth;
   }

   @JsonProperty("texture_width")
   public void setTextureWidth(Double value) {
      this.textureWidth = value;
   }

   @JsonProperty("visible_bounds_height")
   public Double getVisibleBoundsHeight() {
      return this.visibleBoundsHeight;
   }

   @JsonProperty("visible_bounds_height")
   public void setVisibleBoundsHeight(Double value) {
      this.visibleBoundsHeight = value;
   }

   @JsonProperty("visible_bounds_offset")
   public double[] getVisibleBoundsOffset() {
      return this.visibleBoundsOffset;
   }

   @JsonProperty("visible_bounds_offset")
   public void setVisibleBoundsOffset(double[] value) {
      this.visibleBoundsOffset = value;
   }

   @JsonProperty("visible_bounds_width")
   public Double getVisibleBoundsWidth() {
      return this.visibleBoundsWidth;
   }

   @JsonProperty("visible_bounds_width")
   public void setVisibleBoundsWidth(Double value) {
      this.visibleBoundsWidth = value;
   }
}

package software.bernie.geckolib3.geo.raw.pojo;

import software.bernie.shadowed.fasterxml.jackson.annotation.JsonProperty;

public class UvFaces {
   private FaceUv down;
   private FaceUv east;
   private FaceUv north;
   private FaceUv south;
   private FaceUv up;
   private FaceUv west;

   @JsonProperty("down")
   public FaceUv getDown() {
      return this.down;
   }

   @JsonProperty("down")
   public void setDown(FaceUv value) {
      this.down = value;
   }

   @JsonProperty("east")
   public FaceUv getEast() {
      return this.east;
   }

   @JsonProperty("east")
   public void setEast(FaceUv value) {
      this.east = value;
   }

   @JsonProperty("north")
   public FaceUv getNorth() {
      return this.north;
   }

   @JsonProperty("north")
   public void setNorth(FaceUv value) {
      this.north = value;
   }

   @JsonProperty("south")
   public FaceUv getSouth() {
      return this.south;
   }

   @JsonProperty("south")
   public void setSouth(FaceUv value) {
      this.south = value;
   }

   @JsonProperty("up")
   public FaceUv getUp() {
      return this.up;
   }

   @JsonProperty("up")
   public void setUp(FaceUv value) {
      this.up = value;
   }

   @JsonProperty("west")
   public FaceUv getWest() {
      return this.west;
   }

   @JsonProperty("west")
   public void setWest(FaceUv value) {
      this.west = value;
   }
}

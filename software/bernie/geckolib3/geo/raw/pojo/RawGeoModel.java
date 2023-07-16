package software.bernie.geckolib3.geo.raw.pojo;

import software.bernie.shadowed.fasterxml.jackson.annotation.JsonProperty;

public class RawGeoModel {
   private FormatVersion formatVersion;
   private MinecraftGeometry[] minecraftGeometry;

   @JsonProperty("format_version")
   public FormatVersion getFormatVersion() {
      return this.formatVersion;
   }

   @JsonProperty("format_version")
   public void setFormatVersion(FormatVersion value) {
      this.formatVersion = value;
   }

   @JsonProperty("minecraft:geometry")
   public MinecraftGeometry[] getMinecraftGeometry() {
      return this.minecraftGeometry;
   }

   @JsonProperty("minecraft:geometry")
   public void setMinecraftGeometry(MinecraftGeometry[] value) {
      this.minecraftGeometry = value;
   }
}

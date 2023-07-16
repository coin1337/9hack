package software.bernie.geckolib3.geo.raw.pojo;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonCreator;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonValue;

public enum PolysEnum {
   QUAD_LIST,
   TRI_LIST;

   @JsonValue
   public String toValue() {
      switch(this) {
      case QUAD_LIST:
         return "quad_list";
      case TRI_LIST:
         return "tri_list";
      default:
         return null;
      }
   }

   @JsonCreator
   public static PolysEnum forValue(String value) throws IOException {
      if (value.equals("quad_list")) {
         return QUAD_LIST;
      } else if (value.equals("tri_list")) {
         return TRI_LIST;
      } else {
         throw new IOException("Cannot deserialize PolysEnum");
      }
   }
}

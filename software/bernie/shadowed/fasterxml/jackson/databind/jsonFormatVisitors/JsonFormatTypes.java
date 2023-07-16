package software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors;

import java.util.HashMap;
import java.util.Map;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonCreator;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonValue;

public enum JsonFormatTypes {
   STRING,
   NUMBER,
   INTEGER,
   BOOLEAN,
   OBJECT,
   ARRAY,
   NULL,
   ANY;

   private static final Map<String, JsonFormatTypes> _byLCName = new HashMap();

   @JsonValue
   public String value() {
      return this.name().toLowerCase();
   }

   @JsonCreator
   public static JsonFormatTypes forValue(String s) {
      return (JsonFormatTypes)_byLCName.get(s);
   }

   static {
      JsonFormatTypes[] arr$ = values();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         JsonFormatTypes t = arr$[i$];
         _byLCName.put(t.name().toLowerCase(), t);
      }

   }
}

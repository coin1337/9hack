package software.bernie.shadowed.fasterxml.jackson.databind.ser.std;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;

/** @deprecated */
@Deprecated
public class StdKeySerializer extends StdSerializer<Object> {
   public StdKeySerializer() {
      super(Object.class);
   }

   public void serialize(Object value, JsonGenerator g, SerializerProvider provider) throws IOException {
      g.writeFieldName(value.toString());
   }
}

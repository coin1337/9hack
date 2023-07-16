package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.ser.key;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;

public class Jsr310NullKeySerializer extends JsonSerializer<Object> {
   public static final String NULL_KEY = "";

   public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      if (value != null) {
         throw JsonMappingException.from(gen, "Jsr310NullKeySerializer is only for serializing null values.");
      } else {
         gen.writeFieldName("");
      }
   }
}

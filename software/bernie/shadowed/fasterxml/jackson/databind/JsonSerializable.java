package software.bernie.shadowed.fasterxml.jackson.databind;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeSerializer;

public interface JsonSerializable {
   void serialize(JsonGenerator var1, SerializerProvider var2) throws IOException;

   void serializeWithType(JsonGenerator var1, SerializerProvider var2, TypeSerializer var3) throws IOException;

   public abstract static class Base implements JsonSerializable {
      public boolean isEmpty(SerializerProvider serializers) {
         return false;
      }
   }
}

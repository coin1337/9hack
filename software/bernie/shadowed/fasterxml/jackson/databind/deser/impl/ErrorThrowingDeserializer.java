package software.bernie.shadowed.fasterxml.jackson.databind.deser.impl;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;

public class ErrorThrowingDeserializer extends JsonDeserializer<Object> {
   private final Error _cause;

   public ErrorThrowingDeserializer(NoClassDefFoundError cause) {
      this._cause = cause;
   }

   public Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
      throw this._cause;
   }
}

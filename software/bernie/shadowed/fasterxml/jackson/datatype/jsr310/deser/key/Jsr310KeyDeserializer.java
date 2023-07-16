package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.deser.key;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.format.DateTimeParseException;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.KeyDeserializer;

abstract class Jsr310KeyDeserializer extends KeyDeserializer {
   public final Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
      return "".equals(key) ? null : this.deserialize(key, ctxt);
   }

   protected abstract Object deserialize(String var1, DeserializationContext var2) throws IOException;

   protected <T> T _rethrowDateTimeException(DeserializationContext ctxt, Class<?> type, DateTimeException e0, String value) throws IOException {
      JsonMappingException e;
      if (e0 instanceof DateTimeParseException) {
         e = ctxt.weirdStringException(value, type, e0.getMessage());
         e.initCause(e0);
      } else {
         e = JsonMappingException.from((DeserializationContext)ctxt, String.format("Failed to deserialize %s: (%s) %s", type.getName(), e0.getClass().getName(), e0.getMessage()), e0);
      }

      throw e;
   }
}

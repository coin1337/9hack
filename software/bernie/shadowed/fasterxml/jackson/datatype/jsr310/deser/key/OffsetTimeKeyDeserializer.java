package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.deser.key;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;

public class OffsetTimeKeyDeserializer extends Jsr310KeyDeserializer {
   public static final OffsetTimeKeyDeserializer INSTANCE = new OffsetTimeKeyDeserializer();

   private OffsetTimeKeyDeserializer() {
   }

   protected OffsetTime deserialize(String key, DeserializationContext ctxt) throws IOException {
      try {
         return OffsetTime.parse(key, DateTimeFormatter.ISO_OFFSET_TIME);
      } catch (DateTimeException var4) {
         return (OffsetTime)this._rethrowDateTimeException(ctxt, OffsetTime.class, var4, key);
      }
   }
}

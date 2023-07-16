package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.deser.key;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.Duration;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;

public class DurationKeyDeserializer extends Jsr310KeyDeserializer {
   public static final DurationKeyDeserializer INSTANCE = new DurationKeyDeserializer();

   private DurationKeyDeserializer() {
   }

   protected Duration deserialize(String key, DeserializationContext ctxt) throws IOException {
      try {
         return Duration.parse(key);
      } catch (DateTimeException var4) {
         return (Duration)this._rethrowDateTimeException(ctxt, Duration.class, var4, key);
      }
   }
}

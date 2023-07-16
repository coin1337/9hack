package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.deser.key;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZoneId;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;

public class ZoneIdKeyDeserializer extends Jsr310KeyDeserializer {
   public static final ZoneIdKeyDeserializer INSTANCE = new ZoneIdKeyDeserializer();

   private ZoneIdKeyDeserializer() {
   }

   protected Object deserialize(String key, DeserializationContext ctxt) throws IOException {
      try {
         return ZoneId.of(key);
      } catch (DateTimeException var4) {
         return this._rethrowDateTimeException(ctxt, ZoneId.class, var4, key);
      }
   }
}

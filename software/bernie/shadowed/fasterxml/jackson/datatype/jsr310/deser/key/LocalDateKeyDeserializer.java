package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.deser.key;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;

public class LocalDateKeyDeserializer extends Jsr310KeyDeserializer {
   public static final LocalDateKeyDeserializer INSTANCE = new LocalDateKeyDeserializer();

   private LocalDateKeyDeserializer() {
   }

   protected LocalDate deserialize(String key, DeserializationContext ctxt) throws IOException {
      try {
         return LocalDate.parse(key, DateTimeFormatter.ISO_LOCAL_DATE);
      } catch (DateTimeException var4) {
         return (LocalDate)this._rethrowDateTimeException(ctxt, LocalDate.class, var4, key);
      }
   }
}

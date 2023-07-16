package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.deser.key;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;

public class YearMothKeyDeserializer extends Jsr310KeyDeserializer {
   public static final YearMothKeyDeserializer INSTANCE = new YearMothKeyDeserializer();
   private static final DateTimeFormatter FORMATTER;

   private YearMothKeyDeserializer() {
   }

   protected YearMonth deserialize(String key, DeserializationContext ctxt) throws IOException {
      try {
         return YearMonth.parse(key, FORMATTER);
      } catch (DateTimeException var4) {
         return (YearMonth)this._rethrowDateTimeException(ctxt, YearMonth.class, var4, key);
      }
   }

   static {
      FORMATTER = (new DateTimeFormatterBuilder()).appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).toFormatter();
   }
}

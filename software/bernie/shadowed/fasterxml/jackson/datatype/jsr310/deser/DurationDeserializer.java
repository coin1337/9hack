package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.deser;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.Duration;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationFeature;
import software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.DecimalUtils;

public class DurationDeserializer extends JSR310DeserializerBase<Duration> {
   private static final long serialVersionUID = 1L;
   public static final DurationDeserializer INSTANCE = new DurationDeserializer();

   private DurationDeserializer() {
      super(Duration.class);
   }

   public Duration deserialize(JsonParser parser, DeserializationContext context) throws IOException {
      switch(parser.getCurrentTokenId()) {
      case 3:
         return (Duration)this._deserializeFromArray(parser, context);
      case 4:
      case 5:
      case 9:
      case 10:
      case 11:
      default:
         return (Duration)this._reportWrongToken(parser, context, new JsonToken[]{JsonToken.VALUE_STRING, JsonToken.VALUE_NUMBER_INT, JsonToken.VALUE_NUMBER_FLOAT});
      case 6:
         String string = parser.getText().trim();
         if (string.length() == 0) {
            return null;
         } else {
            try {
               return Duration.parse(string);
            } catch (DateTimeException var9) {
               return (Duration)this._rethrowDateTimeException(parser, context, var9, string);
            }
         }
      case 7:
         if (context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
            return Duration.ofSeconds(parser.getLongValue());
         }

         return Duration.ofMillis(parser.getLongValue());
      case 8:
         BigDecimal value = parser.getDecimalValue();
         long seconds = value.longValue();
         int nanoseconds = DecimalUtils.extractNanosecondDecimal(value, seconds);
         return Duration.ofSeconds(seconds, (long)nanoseconds);
      case 12:
         return (Duration)parser.getEmbeddedObject();
      }
   }
}

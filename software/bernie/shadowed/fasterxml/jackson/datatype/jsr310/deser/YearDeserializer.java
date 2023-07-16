package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.deser;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;

public class YearDeserializer extends JSR310DeserializerBase<Year> {
   private static final long serialVersionUID = 1L;
   public static final YearDeserializer INSTANCE = new YearDeserializer();
   private final DateTimeFormatter _formatter;

   private YearDeserializer() {
      this((DateTimeFormatter)null);
   }

   public YearDeserializer(DateTimeFormatter formatter) {
      super(Year.class);
      this._formatter = formatter;
   }

   public Year deserialize(JsonParser parser, DeserializationContext context) throws IOException {
      JsonToken t = parser.getCurrentToken();
      if (t == JsonToken.VALUE_STRING) {
         String string = parser.getValueAsString().trim();

         try {
            if (this._formatter == null) {
               return Year.parse(string);
            }

            return Year.parse(string, this._formatter);
         } catch (DateTimeException var6) {
            this._rethrowDateTimeException(parser, context, var6, string);
         }
      }

      if (t == JsonToken.VALUE_NUMBER_INT) {
         return Year.of(parser.getIntValue());
      } else if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
         return (Year)parser.getEmbeddedObject();
      } else {
         return parser.hasToken(JsonToken.START_ARRAY) ? (Year)this._deserializeFromArray(parser, context) : (Year)this._reportWrongToken(parser, context, new JsonToken[]{JsonToken.VALUE_STRING, JsonToken.VALUE_NUMBER_INT});
      }
   }
}

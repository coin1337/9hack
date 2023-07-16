package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.deser;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;

public class YearMonthDeserializer extends JSR310DateTimeDeserializerBase<YearMonth> {
   private static final long serialVersionUID = 1L;
   public static final YearMonthDeserializer INSTANCE = new YearMonthDeserializer();

   private YearMonthDeserializer() {
      this(DateTimeFormatter.ofPattern("uuuu-MM"));
   }

   public YearMonthDeserializer(DateTimeFormatter formatter) {
      super(YearMonth.class, formatter);
   }

   protected JsonDeserializer<YearMonth> withDateFormat(DateTimeFormatter dtf) {
      return new YearMonthDeserializer(dtf);
   }

   public YearMonth deserialize(JsonParser parser, DeserializationContext context) throws IOException {
      if (parser.hasToken(JsonToken.VALUE_STRING)) {
         String string = parser.getText().trim();
         if (string.length() == 0) {
            return null;
         }

         try {
            return YearMonth.parse(string, this._formatter);
         } catch (DateTimeException var6) {
            this._rethrowDateTimeException(parser, context, var6, string);
         }
      }

      if (parser.isExpectedStartArrayToken()) {
         JsonToken t = parser.nextToken();
         if (t == JsonToken.END_ARRAY) {
            return null;
         } else if ((t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT) && context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            YearMonth parsed = this.deserialize(parser, context);
            if (parser.nextToken() != JsonToken.END_ARRAY) {
               this.handleMissingEndArrayForSingle(parser, context);
            }

            return parsed;
         } else {
            if (t != JsonToken.VALUE_NUMBER_INT) {
               this._reportWrongToken(context, JsonToken.VALUE_NUMBER_INT, "years");
            }

            int year = parser.getIntValue();
            int month = parser.nextIntValue(-1);
            if (month == -1) {
               if (!parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                  this._reportWrongToken(context, JsonToken.VALUE_NUMBER_INT, "months");
               }

               month = parser.getIntValue();
            }

            if (parser.nextToken() != JsonToken.END_ARRAY) {
               throw context.wrongTokenException(parser, this.handledType(), JsonToken.END_ARRAY, "Expected array to end");
            } else {
               return YearMonth.of(year, month);
            }
         }
      } else {
         return parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT) ? (YearMonth)parser.getEmbeddedObject() : (YearMonth)this._reportWrongToken(parser, context, new JsonToken[]{JsonToken.VALUE_STRING, JsonToken.START_ARRAY});
      }
   }
}

package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.deser;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;

public class OffsetTimeDeserializer extends JSR310DateTimeDeserializerBase<OffsetTime> {
   private static final long serialVersionUID = 1L;
   public static final OffsetTimeDeserializer INSTANCE = new OffsetTimeDeserializer();

   private OffsetTimeDeserializer() {
      this(DateTimeFormatter.ISO_OFFSET_TIME);
   }

   protected OffsetTimeDeserializer(DateTimeFormatter dtf) {
      super(OffsetTime.class, dtf);
   }

   protected JsonDeserializer<OffsetTime> withDateFormat(DateTimeFormatter dtf) {
      return new OffsetTimeDeserializer(dtf);
   }

   public OffsetTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
      if (parser.hasToken(JsonToken.VALUE_STRING)) {
         String string = parser.getText().trim();
         if (string.length() == 0) {
            return null;
         }

         try {
            return OffsetTime.parse(string, this._formatter);
         } catch (DateTimeException var9) {
            this._rethrowDateTimeException(parser, context, var9, string);
         }
      }

      if (!parser.isExpectedStartArrayToken()) {
         if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return (OffsetTime)parser.getEmbeddedObject();
         } else {
            throw context.wrongTokenException(parser, this.handledType(), JsonToken.START_ARRAY, "Expected array or string.");
         }
      } else {
         JsonToken t = parser.nextToken();
         if (t != JsonToken.VALUE_NUMBER_INT) {
            if (t == JsonToken.END_ARRAY) {
               return null;
            }

            if ((t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT) && context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
               OffsetTime parsed = this.deserialize(parser, context);
               if (parser.nextToken() != JsonToken.END_ARRAY) {
                  this.handleMissingEndArrayForSingle(parser, context);
               }

               return parsed;
            }

            context.reportInputMismatch(this.handledType(), "Unexpected token (%s) within Array, expected VALUE_NUMBER_INT", t);
         }

         int hour = parser.getIntValue();
         int minute = parser.nextIntValue(-1);
         if (minute == -1) {
            t = parser.getCurrentToken();
            if (t == JsonToken.END_ARRAY) {
               return null;
            }

            if (t != JsonToken.VALUE_NUMBER_INT) {
               this._reportWrongToken(context, JsonToken.VALUE_NUMBER_INT, "minutes");
            }

            minute = parser.getIntValue();
         }

         int partialSecond = 0;
         int second = 0;
         if (parser.nextToken() == JsonToken.VALUE_NUMBER_INT) {
            second = parser.getIntValue();
            if (parser.nextToken() == JsonToken.VALUE_NUMBER_INT) {
               partialSecond = parser.getIntValue();
               if (partialSecond < 1000 && !context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
                  partialSecond *= 1000000;
               }

               parser.nextToken();
            }
         }

         if (parser.getCurrentToken() == JsonToken.VALUE_STRING) {
            OffsetTime result = OffsetTime.of(hour, minute, second, partialSecond, ZoneOffset.of(parser.getText()));
            if (parser.nextToken() != JsonToken.END_ARRAY) {
               this._reportWrongToken(context, JsonToken.END_ARRAY, "timezone");
            }

            return result;
         } else {
            throw context.wrongTokenException(parser, this.handledType(), JsonToken.VALUE_STRING, "Expected string for TimeZone after numeric values");
         }
      }
   }
}

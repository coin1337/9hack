package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.deser;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;

public class LocalDateTimeDeserializer extends JSR310DateTimeDeserializerBase<LocalDateTime> {
   private static final long serialVersionUID = 1L;
   private static final DateTimeFormatter DEFAULT_FORMATTER;
   public static final LocalDateTimeDeserializer INSTANCE;

   private LocalDateTimeDeserializer() {
      this(DEFAULT_FORMATTER);
   }

   public LocalDateTimeDeserializer(DateTimeFormatter formatter) {
      super(LocalDateTime.class, formatter);
   }

   protected JsonDeserializer<LocalDateTime> withDateFormat(DateTimeFormatter formatter) {
      return new LocalDateTimeDeserializer(formatter);
   }

   public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
      if (parser.hasTokenId(6)) {
         String string = parser.getText().trim();
         if (string.length() == 0) {
            return null;
         }

         try {
            if (this._formatter == DEFAULT_FORMATTER && string.length() > 10 && string.charAt(10) == 'T') {
               if (string.endsWith("Z")) {
                  return LocalDateTime.ofInstant(Instant.parse(string), ZoneOffset.UTC);
               }

               return LocalDateTime.parse(string, DEFAULT_FORMATTER);
            }

            return LocalDateTime.parse(string, this._formatter);
         } catch (DateTimeException var12) {
            this._rethrowDateTimeException(parser, context, var12, string);
         }
      }

      if (parser.isExpectedStartArrayToken()) {
         JsonToken t = parser.nextToken();
         if (t == JsonToken.END_ARRAY) {
            return null;
         }

         LocalDateTime result;
         if ((t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT) && context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            result = this.deserialize(parser, context);
            if (parser.nextToken() != JsonToken.END_ARRAY) {
               this.handleMissingEndArrayForSingle(parser, context);
            }

            return result;
         }

         if (t == JsonToken.VALUE_NUMBER_INT) {
            int year = parser.getIntValue();
            int month = parser.nextIntValue(-1);
            int day = parser.nextIntValue(-1);
            int hour = parser.nextIntValue(-1);
            int minute = parser.nextIntValue(-1);
            t = parser.nextToken();
            if (t == JsonToken.END_ARRAY) {
               result = LocalDateTime.of(year, month, day, hour, minute);
            } else {
               int second = parser.getIntValue();
               t = parser.nextToken();
               if (t == JsonToken.END_ARRAY) {
                  result = LocalDateTime.of(year, month, day, hour, minute, second);
               } else {
                  int partialSecond = parser.getIntValue();
                  if (partialSecond < 1000 && !context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
                     partialSecond *= 1000000;
                  }

                  if (parser.nextToken() != JsonToken.END_ARRAY) {
                     throw context.wrongTokenException(parser, this.handledType(), JsonToken.END_ARRAY, "Expected array to end");
                  }

                  result = LocalDateTime.of(year, month, day, hour, minute, second, partialSecond);
               }
            }

            return result;
         }

         context.reportInputMismatch(this.handledType(), "Unexpected token (%s) within Array, expected VALUE_NUMBER_INT", t);
      }

      if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
         return (LocalDateTime)parser.getEmbeddedObject();
      } else {
         throw context.wrongTokenException(parser, this.handledType(), JsonToken.VALUE_STRING, "Expected array or string.");
      }
   }

   static {
      DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
      INSTANCE = new LocalDateTimeDeserializer();
   }
}

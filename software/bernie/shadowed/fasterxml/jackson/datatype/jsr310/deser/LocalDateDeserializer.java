package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.deser;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;

public class LocalDateDeserializer extends JSR310DateTimeDeserializerBase<LocalDate> {
   private static final long serialVersionUID = 1L;
   private static final DateTimeFormatter DEFAULT_FORMATTER;
   public static final LocalDateDeserializer INSTANCE;

   private LocalDateDeserializer() {
      this(DEFAULT_FORMATTER);
   }

   public LocalDateDeserializer(DateTimeFormatter dtf) {
      super(LocalDate.class, dtf);
   }

   protected JsonDeserializer<LocalDate> withDateFormat(DateTimeFormatter dtf) {
      return new LocalDateDeserializer(dtf);
   }

   public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
      if (parser.hasToken(JsonToken.VALUE_STRING)) {
         String string = parser.getText().trim();
         if (string.length() == 0) {
            return null;
         }

         DateTimeFormatter format = this._formatter;

         try {
            if (format == DEFAULT_FORMATTER && string.length() > 10 && string.charAt(10) == 'T') {
               if (string.endsWith("Z")) {
                  return LocalDateTime.ofInstant(Instant.parse(string), ZoneOffset.UTC).toLocalDate();
               }

               return LocalDate.parse(string, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

            return LocalDate.parse(string, format);
         } catch (DateTimeException var7) {
            this._rethrowDateTimeException(parser, context, var7, string);
         }
      }

      if (parser.isExpectedStartArrayToken()) {
         JsonToken t = parser.nextToken();
         if (t == JsonToken.END_ARRAY) {
            return null;
         }

         if (context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS) && (t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT)) {
            LocalDate parsed = this.deserialize(parser, context);
            if (parser.nextToken() != JsonToken.END_ARRAY) {
               this.handleMissingEndArrayForSingle(parser, context);
            }

            return parsed;
         }

         if (t == JsonToken.VALUE_NUMBER_INT) {
            int year = parser.getIntValue();
            int month = parser.nextIntValue(-1);
            int day = parser.nextIntValue(-1);
            if (parser.nextToken() != JsonToken.END_ARRAY) {
               throw context.wrongTokenException(parser, this.handledType(), JsonToken.END_ARRAY, "Expected array to end");
            }

            return LocalDate.of(year, month, day);
         }

         context.reportInputMismatch(this.handledType(), "Unexpected token (%s) within Array, expected VALUE_NUMBER_INT", t);
      }

      if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
         return (LocalDate)parser.getEmbeddedObject();
      } else if (parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
         return LocalDate.ofEpochDay(parser.getLongValue());
      } else {
         throw context.wrongTokenException(parser, this.handledType(), JsonToken.VALUE_STRING, "Expected array or string.");
      }
   }

   static {
      DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
      INSTANCE = new LocalDateDeserializer();
   }
}

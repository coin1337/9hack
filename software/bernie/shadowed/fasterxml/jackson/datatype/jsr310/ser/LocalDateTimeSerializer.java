package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.ser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.type.WritableTypeId;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class LocalDateTimeSerializer extends JSR310FormattedSerializerBase<LocalDateTime> {
   private static final long serialVersionUID = 1L;
   public static final LocalDateTimeSerializer INSTANCE = new LocalDateTimeSerializer();

   protected LocalDateTimeSerializer() {
      this((DateTimeFormatter)null);
   }

   public LocalDateTimeSerializer(DateTimeFormatter f) {
      super(LocalDateTime.class, f);
   }

   private LocalDateTimeSerializer(LocalDateTimeSerializer base, Boolean useTimestamp, DateTimeFormatter f) {
      super(base, useTimestamp, f, (JsonFormat.Shape)null);
   }

   protected JSR310FormattedSerializerBase<LocalDateTime> withFormat(Boolean useTimestamp, DateTimeFormatter f, JsonFormat.Shape shape) {
      return new LocalDateTimeSerializer(this, useTimestamp, f);
   }

   protected DateTimeFormatter _defaultFormatter() {
      return DateTimeFormatter.ISO_LOCAL_DATE_TIME;
   }

   public void serialize(LocalDateTime value, JsonGenerator g, SerializerProvider provider) throws IOException {
      if (this.useTimestamp(provider)) {
         g.writeStartArray();
         this._serializeAsArrayContents(value, g, provider);
         g.writeEndArray();
      } else {
         DateTimeFormatter dtf = this._formatter;
         if (dtf == null) {
            dtf = this._defaultFormatter();
         }

         g.writeString(value.format(dtf));
      }

   }

   public void serializeWithType(LocalDateTime value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
      WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, this.serializationShape(provider)));
      if (typeIdDef.valueShape == JsonToken.START_ARRAY) {
         this._serializeAsArrayContents(value, g, provider);
      } else {
         DateTimeFormatter dtf = this._formatter;
         if (dtf == null) {
            dtf = this._defaultFormatter();
         }

         g.writeString(value.format(dtf));
      }

      typeSer.writeTypeSuffix(g, typeIdDef);
   }

   private final void _serializeAsArrayContents(LocalDateTime value, JsonGenerator g, SerializerProvider provider) throws IOException {
      g.writeNumber(value.getYear());
      g.writeNumber(value.getMonthValue());
      g.writeNumber(value.getDayOfMonth());
      g.writeNumber(value.getHour());
      g.writeNumber(value.getMinute());
      int secs = value.getSecond();
      int nanos = value.getNano();
      if (secs > 0 || nanos > 0) {
         g.writeNumber(secs);
         if (nanos > 0) {
            if (provider.isEnabled(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
               g.writeNumber(nanos);
            } else {
               g.writeNumber(value.get(ChronoField.MILLI_OF_SECOND));
            }
         }
      }

   }

   protected JsonToken serializationShape(SerializerProvider provider) {
      return this.useTimestamp(provider) ? JsonToken.START_ARRAY : JsonToken.VALUE_STRING;
   }
}

package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.ser;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.type.WritableTypeId;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class LocalTimeSerializer extends JSR310FormattedSerializerBase<LocalTime> {
   private static final long serialVersionUID = 1L;
   public static final LocalTimeSerializer INSTANCE = new LocalTimeSerializer();

   protected LocalTimeSerializer() {
      this((DateTimeFormatter)null);
   }

   public LocalTimeSerializer(DateTimeFormatter formatter) {
      super(LocalTime.class, formatter);
   }

   protected LocalTimeSerializer(LocalTimeSerializer base, Boolean useTimestamp, DateTimeFormatter formatter) {
      super(base, useTimestamp, formatter, (JsonFormat.Shape)null);
   }

   protected JSR310FormattedSerializerBase<LocalTime> withFormat(Boolean useTimestamp, DateTimeFormatter dtf, JsonFormat.Shape shape) {
      return new LocalTimeSerializer(this, useTimestamp, dtf);
   }

   protected DateTimeFormatter _defaultFormatter() {
      return DateTimeFormatter.ISO_LOCAL_TIME;
   }

   public void serialize(LocalTime value, JsonGenerator g, SerializerProvider provider) throws IOException {
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

   public void serializeWithType(LocalTime value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
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

   private final void _serializeAsArrayContents(LocalTime value, JsonGenerator g, SerializerProvider provider) throws IOException {
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

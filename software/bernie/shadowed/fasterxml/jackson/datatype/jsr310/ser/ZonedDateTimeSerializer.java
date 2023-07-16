package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.ser;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;

public class ZonedDateTimeSerializer extends InstantSerializerBase<ZonedDateTime> {
   private static final long serialVersionUID = 1L;
   public static final ZonedDateTimeSerializer INSTANCE = new ZonedDateTimeSerializer();
   protected final Boolean _writeZoneId;

   protected ZonedDateTimeSerializer() {
      this(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
   }

   public ZonedDateTimeSerializer(DateTimeFormatter formatter) {
      super(ZonedDateTime.class, (dt) -> {
         return dt.toInstant().toEpochMilli();
      }, ChronoZonedDateTime::toEpochSecond, ZonedDateTime::getNano, formatter);
      this._writeZoneId = null;
   }

   protected ZonedDateTimeSerializer(ZonedDateTimeSerializer base, Boolean useTimestamp, DateTimeFormatter formatter, Boolean writeZoneId) {
      super(base, useTimestamp, formatter);
      this._writeZoneId = writeZoneId;
   }

   protected JSR310FormattedSerializerBase<?> withFormat(Boolean useTimestamp, DateTimeFormatter formatter, JsonFormat.Shape shape) {
      return new ZonedDateTimeSerializer(this, useTimestamp, formatter, this._writeZoneId);
   }

   protected JSR310FormattedSerializerBase<?> withFeatures(Boolean writeZoneId) {
      return new ZonedDateTimeSerializer(this, this._useTimestamp, this._formatter, writeZoneId);
   }

   public void serialize(ZonedDateTime value, JsonGenerator g, SerializerProvider provider) throws IOException {
      if (!this.useTimestamp(provider) && this.shouldWriteWithZoneId(provider)) {
         g.writeString(DateTimeFormatter.ISO_ZONED_DATE_TIME.format(value));
      } else {
         super.serialize((Temporal)value, g, provider);
      }
   }

   public boolean shouldWriteWithZoneId(SerializerProvider ctxt) {
      return this._writeZoneId != null ? this._writeZoneId : ctxt.isEnabled(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);
   }

   protected JsonToken serializationShape(SerializerProvider provider) {
      return !this.useTimestamp(provider) && this.shouldWriteWithZoneId(provider) ? JsonToken.VALUE_STRING : super.serializationShape(provider);
   }
}

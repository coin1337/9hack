package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.ser;

import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;

/** @deprecated */
@Deprecated
public class ZonedDateTimeWithZoneIdSerializer extends InstantSerializerBase<ZonedDateTime> {
   private static final long serialVersionUID = 1L;
   public static final ZonedDateTimeWithZoneIdSerializer INSTANCE = new ZonedDateTimeWithZoneIdSerializer();

   protected ZonedDateTimeWithZoneIdSerializer() {
      super(ZonedDateTime.class, (dt) -> {
         return dt.toInstant().toEpochMilli();
      }, ChronoZonedDateTime::toEpochSecond, ZonedDateTime::getNano, (DateTimeFormatter)null);
   }

   protected ZonedDateTimeWithZoneIdSerializer(ZonedDateTimeWithZoneIdSerializer base, Boolean useTimestamp, DateTimeFormatter formatter) {
      super(base, useTimestamp, formatter);
   }

   protected JSR310FormattedSerializerBase<?> withFormat(Boolean useTimestamp, DateTimeFormatter formatter, JsonFormat.Shape shape) {
      return new ZonedDateTimeWithZoneIdSerializer(this, useTimestamp, formatter);
   }
}

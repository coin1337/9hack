package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.ser;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;

public class OffsetDateTimeSerializer extends InstantSerializerBase<OffsetDateTime> {
   private static final long serialVersionUID = 1L;
   public static final OffsetDateTimeSerializer INSTANCE = new OffsetDateTimeSerializer();

   protected OffsetDateTimeSerializer() {
      super(OffsetDateTime.class, (dt) -> {
         return dt.toInstant().toEpochMilli();
      }, OffsetDateTime::toEpochSecond, OffsetDateTime::getNano, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
   }

   protected OffsetDateTimeSerializer(OffsetDateTimeSerializer base, Boolean useTimestamp, DateTimeFormatter formatter) {
      super(base, useTimestamp, formatter);
   }

   protected JSR310FormattedSerializerBase<?> withFormat(Boolean useTimestamp, DateTimeFormatter formatter, JsonFormat.Shape shape) {
      return new OffsetDateTimeSerializer(this, useTimestamp, formatter);
   }
}

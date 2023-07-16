package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.ser;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;

public final class InstantSerializer extends InstantSerializerBase<Instant> {
   private static final long serialVersionUID = 1L;
   public static final InstantSerializer INSTANCE = new InstantSerializer();

   protected InstantSerializer() {
      super(Instant.class, Instant::toEpochMilli, Instant::getEpochSecond, Instant::getNano, (DateTimeFormatter)null);
   }

   protected InstantSerializer(InstantSerializer base, Boolean useTimestamp, DateTimeFormatter formatter) {
      super(base, useTimestamp, formatter);
   }

   protected JSR310FormattedSerializerBase<Instant> withFormat(Boolean useTimestamp, DateTimeFormatter formatter, JsonFormat.Shape shape) {
      return new InstantSerializer(this, useTimestamp, formatter);
   }
}

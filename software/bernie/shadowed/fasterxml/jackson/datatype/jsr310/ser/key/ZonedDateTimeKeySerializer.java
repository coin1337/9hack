package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.ser.key;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonProcessingException;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;

public class ZonedDateTimeKeySerializer extends JsonSerializer<ZonedDateTime> {
   public static final ZonedDateTimeKeySerializer INSTANCE = new ZonedDateTimeKeySerializer();

   private ZonedDateTimeKeySerializer() {
   }

   public void serialize(ZonedDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
      gen.writeFieldName(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(value));
   }
}

package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.ser;

import java.io.IOException;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;
import software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.DecimalUtils;

public class DurationSerializer extends JSR310FormattedSerializerBase<Duration> {
   private static final long serialVersionUID = 1L;
   public static final DurationSerializer INSTANCE = new DurationSerializer();

   private DurationSerializer() {
      super(Duration.class);
   }

   protected DurationSerializer(DurationSerializer base, Boolean useTimestamp, DateTimeFormatter dtf) {
      super(base, useTimestamp, dtf, (JsonFormat.Shape)null);
   }

   protected DurationSerializer withFormat(Boolean useTimestamp, DateTimeFormatter dtf, JsonFormat.Shape shape) {
      return new DurationSerializer(this, useTimestamp, dtf);
   }

   public void serialize(Duration duration, JsonGenerator generator, SerializerProvider provider) throws IOException {
      if (this.useTimestamp(provider)) {
         if (provider.isEnabled(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
            generator.writeNumber(DecimalUtils.toBigDecimal(duration.getSeconds(), duration.getNano()));
         } else {
            generator.writeNumber(duration.toMillis());
         }
      } else {
         generator.writeString(duration.toString());
      }

   }

   protected void _acceptTimestampVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      JsonIntegerFormatVisitor v2 = visitor.expectIntegerFormat(typeHint);
      if (v2 != null) {
         v2.numberType(JsonParser.NumberType.LONG);
         SerializerProvider provider = visitor.getProvider();
         if (provider == null || !provider.isEnabled(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
            v2.format(JsonValueFormat.UTC_MILLISEC);
         }
      }

   }

   protected JsonToken serializationShape(SerializerProvider provider) {
      if (this.useTimestamp(provider)) {
         return provider.isEnabled(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS) ? JsonToken.VALUE_NUMBER_FLOAT : JsonToken.VALUE_NUMBER_INT;
      } else {
         return JsonToken.VALUE_STRING;
      }
   }
}

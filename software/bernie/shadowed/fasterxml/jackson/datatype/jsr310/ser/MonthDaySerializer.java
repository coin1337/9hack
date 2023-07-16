package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.ser;

import java.io.IOException;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;

public class MonthDaySerializer extends JSR310FormattedSerializerBase<MonthDay> {
   private static final long serialVersionUID = 1L;
   public static final MonthDaySerializer INSTANCE = new MonthDaySerializer();

   private MonthDaySerializer() {
      this((DateTimeFormatter)null);
   }

   public MonthDaySerializer(DateTimeFormatter formatter) {
      super(MonthDay.class, formatter);
   }

   private MonthDaySerializer(MonthDaySerializer base, Boolean useTimestamp, DateTimeFormatter formatter) {
      super(base, useTimestamp, formatter, (JsonFormat.Shape)null);
   }

   protected MonthDaySerializer withFormat(Boolean useTimestamp, DateTimeFormatter formatter, JsonFormat.Shape shape) {
      return new MonthDaySerializer(this, useTimestamp, formatter);
   }

   public void serialize(MonthDay value, JsonGenerator generator, SerializerProvider provider) throws IOException {
      if (this._useTimestampExplicitOnly(provider)) {
         generator.writeStartArray();
         generator.writeNumber(value.getMonthValue());
         generator.writeNumber(value.getDayOfMonth());
         generator.writeEndArray();
      } else {
         String str = this._formatter == null ? value.toString() : value.format(this._formatter);
         generator.writeString(str);
      }

   }

   protected void _acceptTimestampVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      SerializerProvider provider = visitor.getProvider();
      boolean useTimestamp = provider != null && this._useTimestampExplicitOnly(provider);
      if (useTimestamp) {
         this._acceptTimestampVisitor(visitor, typeHint);
      } else {
         JsonStringFormatVisitor v2 = visitor.expectStringFormat(typeHint);
         if (v2 != null) {
            v2.format(JsonValueFormat.DATE_TIME);
         }
      }

   }

   protected JsonToken serializationShape(SerializerProvider provider) {
      return this._useTimestampExplicitOnly(provider) ? JsonToken.START_ARRAY : JsonToken.VALUE_STRING;
   }
}

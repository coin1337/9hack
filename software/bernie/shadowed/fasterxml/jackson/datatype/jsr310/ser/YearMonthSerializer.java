package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.ser;

import java.io.IOException;
import java.time.YearMonth;
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

public class YearMonthSerializer extends JSR310FormattedSerializerBase<YearMonth> {
   private static final long serialVersionUID = 1L;
   public static final YearMonthSerializer INSTANCE = new YearMonthSerializer();

   private YearMonthSerializer() {
      this((DateTimeFormatter)null);
   }

   public YearMonthSerializer(DateTimeFormatter formatter) {
      super(YearMonth.class, formatter);
   }

   private YearMonthSerializer(YearMonthSerializer base, Boolean useTimestamp, DateTimeFormatter formatter) {
      super(base, useTimestamp, formatter, (JsonFormat.Shape)null);
   }

   protected YearMonthSerializer withFormat(Boolean useTimestamp, DateTimeFormatter formatter, JsonFormat.Shape shape) {
      return new YearMonthSerializer(this, useTimestamp, formatter);
   }

   public void serialize(YearMonth yearMonth, JsonGenerator generator, SerializerProvider provider) throws IOException {
      if (this.useTimestamp(provider)) {
         generator.writeStartArray();
         generator.writeNumber(yearMonth.getYear());
         generator.writeNumber(yearMonth.getMonthValue());
         generator.writeEndArray();
      } else {
         String str = this._formatter == null ? yearMonth.toString() : yearMonth.format(this._formatter);
         generator.writeString(str);
      }

   }

   protected void _acceptTimestampVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      SerializerProvider provider = visitor.getProvider();
      boolean useTimestamp = provider != null && this.useTimestamp(provider);
      if (useTimestamp) {
         super._acceptTimestampVisitor(visitor, typeHint);
      } else {
         JsonStringFormatVisitor v2 = visitor.expectStringFormat(typeHint);
         if (v2 != null) {
            v2.format(JsonValueFormat.DATE_TIME);
         }
      }

   }

   protected JsonToken serializationShape(SerializerProvider provider) {
      return this.useTimestamp(provider) ? JsonToken.START_ARRAY : JsonToken.VALUE_STRING;
   }
}

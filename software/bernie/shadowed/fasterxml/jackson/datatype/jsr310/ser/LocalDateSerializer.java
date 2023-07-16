package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.ser;

import java.io.IOException;
import java.time.LocalDate;
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

public class LocalDateSerializer extends JSR310FormattedSerializerBase<LocalDate> {
   private static final long serialVersionUID = 1L;
   public static final LocalDateSerializer INSTANCE = new LocalDateSerializer();

   protected LocalDateSerializer() {
      super(LocalDate.class);
   }

   protected LocalDateSerializer(LocalDateSerializer base, Boolean useTimestamp, DateTimeFormatter dtf, JsonFormat.Shape shape) {
      super(base, useTimestamp, dtf, shape);
   }

   public LocalDateSerializer(DateTimeFormatter formatter) {
      super(LocalDate.class, formatter);
   }

   protected LocalDateSerializer withFormat(Boolean useTimestamp, DateTimeFormatter dtf, JsonFormat.Shape shape) {
      return new LocalDateSerializer(this, useTimestamp, dtf, shape);
   }

   public void serialize(LocalDate date, JsonGenerator generator, SerializerProvider provider) throws IOException {
      if (this.useTimestamp(provider)) {
         if (this._shape == JsonFormat.Shape.NUMBER_INT) {
            generator.writeNumber(date.toEpochDay());
         } else {
            generator.writeStartArray();
            generator.writeNumber(date.getYear());
            generator.writeNumber(date.getMonthValue());
            generator.writeNumber(date.getDayOfMonth());
            generator.writeEndArray();
         }
      } else {
         String str = this._formatter == null ? date.toString() : date.format(this._formatter);
         generator.writeString(str);
      }

   }

   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      SerializerProvider provider = visitor.getProvider();
      boolean useTimestamp = provider != null && this.useTimestamp(provider);
      if (useTimestamp) {
         this._acceptTimestampVisitor(visitor, typeHint);
      } else {
         JsonStringFormatVisitor v2 = visitor.expectStringFormat(typeHint);
         if (v2 != null) {
            v2.format(JsonValueFormat.DATE);
         }
      }

   }

   protected JsonToken serializationShape(SerializerProvider provider) {
      if (this.useTimestamp(provider)) {
         return this._shape == JsonFormat.Shape.NUMBER_INT ? JsonToken.VALUE_NUMBER_INT : JsonToken.START_ARRAY;
      } else {
         return JsonToken.VALUE_STRING;
      }
   }
}

package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.ser;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
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
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonNumberFormatVisitor;
import software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.DecimalUtils;

public abstract class InstantSerializerBase<T extends Temporal> extends JSR310FormattedSerializerBase<T> {
   private final DateTimeFormatter defaultFormat;
   private final ToLongFunction<T> getEpochMillis;
   private final ToLongFunction<T> getEpochSeconds;
   private final ToIntFunction<T> getNanoseconds;

   protected InstantSerializerBase(Class<T> supportedType, ToLongFunction<T> getEpochMillis, ToLongFunction<T> getEpochSeconds, ToIntFunction<T> getNanoseconds, DateTimeFormatter formatter) {
      super(supportedType, (DateTimeFormatter)null);
      this.defaultFormat = formatter;
      this.getEpochMillis = getEpochMillis;
      this.getEpochSeconds = getEpochSeconds;
      this.getNanoseconds = getNanoseconds;
   }

   protected InstantSerializerBase(InstantSerializerBase<T> base, Boolean useTimestamp, DateTimeFormatter dtf) {
      super(base, useTimestamp, dtf, (JsonFormat.Shape)null);
      this.defaultFormat = base.defaultFormat;
      this.getEpochMillis = base.getEpochMillis;
      this.getEpochSeconds = base.getEpochSeconds;
      this.getNanoseconds = base.getNanoseconds;
   }

   protected abstract JSR310FormattedSerializerBase<?> withFormat(Boolean var1, DateTimeFormatter var2, JsonFormat.Shape var3);

   public void serialize(T value, JsonGenerator generator, SerializerProvider provider) throws IOException {
      if (this.useTimestamp(provider)) {
         if (provider.isEnabled(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
            generator.writeNumber(DecimalUtils.toBigDecimal(this.getEpochSeconds.applyAsLong(value), this.getNanoseconds.applyAsInt(value)));
         } else {
            generator.writeNumber(this.getEpochMillis.applyAsLong(value));
         }
      } else {
         String str;
         if (this._formatter != null) {
            str = this._formatter.format(value);
         } else if (this.defaultFormat != null) {
            str = this.defaultFormat.format(value);
         } else {
            str = value.toString();
         }

         generator.writeString(str);
      }
   }

   protected void _acceptTimestampVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      SerializerProvider prov = visitor.getProvider();
      if (prov != null && prov.isEnabled(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
         JsonNumberFormatVisitor v2 = visitor.expectNumberFormat(typeHint);
         if (v2 != null) {
            v2.numberType(JsonParser.NumberType.BIG_DECIMAL);
         }
      } else {
         JsonIntegerFormatVisitor v2 = visitor.expectIntegerFormat(typeHint);
         if (v2 != null) {
            v2.numberType(JsonParser.NumberType.LONG);
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

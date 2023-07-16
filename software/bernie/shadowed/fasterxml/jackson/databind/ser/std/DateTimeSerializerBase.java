package software.bernie.shadowed.fasterxml.jackson.databind.ser.std;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonNode;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.ContextualSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.util.StdDateFormat;

public abstract class DateTimeSerializerBase<T> extends StdScalarSerializer<T> implements ContextualSerializer {
   protected final Boolean _useTimestamp;
   protected final DateFormat _customFormat;
   protected final AtomicReference<DateFormat> _reusedCustomFormat;

   protected DateTimeSerializerBase(Class<T> type, Boolean useTimestamp, DateFormat customFormat) {
      super(type);
      this._useTimestamp = useTimestamp;
      this._customFormat = customFormat;
      this._reusedCustomFormat = customFormat == null ? null : new AtomicReference();
   }

   public abstract DateTimeSerializerBase<T> withFormat(Boolean var1, DateFormat var2);

   public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property) throws JsonMappingException {
      if (property == null) {
         return this;
      } else {
         JsonFormat.Value format = this.findFormatOverrides(serializers, property, this.handledType());
         if (format == null) {
            return this;
         } else {
            JsonFormat.Shape shape = format.getShape();
            if (shape.isNumeric()) {
               return this.withFormat(Boolean.TRUE, (DateFormat)null);
            } else if (format.hasPattern()) {
               Locale loc = format.hasLocale() ? format.getLocale() : serializers.getLocale();
               SimpleDateFormat df = new SimpleDateFormat(format.getPattern(), loc);
               TimeZone tz = format.hasTimeZone() ? format.getTimeZone() : serializers.getTimeZone();
               df.setTimeZone(tz);
               return this.withFormat(Boolean.FALSE, df);
            } else {
               boolean hasLocale = format.hasLocale();
               boolean hasTZ = format.hasTimeZone();
               boolean asString = shape == JsonFormat.Shape.STRING;
               if (!hasLocale && !hasTZ && !asString) {
                  return this;
               } else {
                  DateFormat df0 = serializers.getConfig().getDateFormat();
                  if (df0 instanceof StdDateFormat) {
                     StdDateFormat std = (StdDateFormat)df0;
                     if (format.hasLocale()) {
                        std = std.withLocale(format.getLocale());
                     }

                     if (format.hasTimeZone()) {
                        std = std.withTimeZone(format.getTimeZone());
                     }

                     return this.withFormat(Boolean.FALSE, std);
                  } else {
                     if (!(df0 instanceof SimpleDateFormat)) {
                        serializers.reportBadDefinition(this.handledType(), String.format("Configured `DateFormat` (%s) not a `SimpleDateFormat`; cannot configure `Locale` or `TimeZone`", df0.getClass().getName()));
                     }

                     SimpleDateFormat df = (SimpleDateFormat)df0;
                     if (hasLocale) {
                        df = new SimpleDateFormat(df.toPattern(), format.getLocale());
                     } else {
                        df = (SimpleDateFormat)df.clone();
                     }

                     TimeZone newTz = format.getTimeZone();
                     boolean changeTZ = newTz != null && !newTz.equals(df.getTimeZone());
                     if (changeTZ) {
                        df.setTimeZone(newTz);
                     }

                     return this.withFormat(Boolean.FALSE, df);
                  }
               }
            }
         }
      }
   }

   public boolean isEmpty(SerializerProvider serializers, T value) {
      return false;
   }

   protected abstract long _timestamp(T var1);

   public JsonNode getSchema(SerializerProvider serializers, Type typeHint) {
      return this.createSchemaNode(this._asTimestamp(serializers) ? "number" : "string", true);
   }

   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      this._acceptJsonFormatVisitor(visitor, typeHint, this._asTimestamp(visitor.getProvider()));
   }

   public abstract void serialize(T var1, JsonGenerator var2, SerializerProvider var3) throws IOException;

   protected boolean _asTimestamp(SerializerProvider serializers) {
      if (this._useTimestamp != null) {
         return this._useTimestamp;
      } else if (this._customFormat == null) {
         if (serializers != null) {
            return serializers.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
         } else {
            throw new IllegalArgumentException("Null SerializerProvider passed for " + this.handledType().getName());
         }
      } else {
         return false;
      }
   }

   protected void _acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint, boolean asNumber) throws JsonMappingException {
      if (asNumber) {
         this.visitIntFormat(visitor, typeHint, JsonParser.NumberType.LONG, JsonValueFormat.UTC_MILLISEC);
      } else {
         this.visitStringFormat(visitor, typeHint, JsonValueFormat.DATE_TIME);
      }

   }

   protected void _serializeAsString(Date value, JsonGenerator g, SerializerProvider provider) throws IOException {
      if (this._customFormat == null) {
         provider.defaultSerializeDateValue(value, g);
      } else {
         DateFormat f = (DateFormat)this._reusedCustomFormat.getAndSet((Object)null);
         if (f == null) {
            f = (DateFormat)this._customFormat.clone();
         }

         g.writeString(f.format(value));
         this._reusedCustomFormat.compareAndSet((Object)null, f);
      }
   }
}

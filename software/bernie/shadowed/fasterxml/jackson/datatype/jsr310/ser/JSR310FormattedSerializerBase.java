package software.bernie.shadowed.fasterxml.jackson.datatype.jsr310.ser;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonNode;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.ContextualSerializer;

abstract class JSR310FormattedSerializerBase<T> extends JSR310SerializerBase<T> implements ContextualSerializer {
   private static final long serialVersionUID = 1L;
   protected final Boolean _useTimestamp;
   protected final DateTimeFormatter _formatter;
   protected final JsonFormat.Shape _shape;

   protected JSR310FormattedSerializerBase(Class<T> supportedType) {
      this(supportedType, (DateTimeFormatter)null);
   }

   protected JSR310FormattedSerializerBase(Class<T> supportedType, DateTimeFormatter formatter) {
      super(supportedType);
      this._useTimestamp = null;
      this._shape = null;
      this._formatter = formatter;
   }

   protected JSR310FormattedSerializerBase(JSR310FormattedSerializerBase<?> base, Boolean useTimestamp, DateTimeFormatter dtf, JsonFormat.Shape shape) {
      super(base.handledType());
      this._useTimestamp = useTimestamp;
      this._formatter = dtf;
      this._shape = shape;
   }

   protected abstract JSR310FormattedSerializerBase<?> withFormat(Boolean var1, DateTimeFormatter var2, JsonFormat.Shape var3);

   protected JSR310FormattedSerializerBase<?> withFeatures(Boolean writeZoneId) {
      return this;
   }

   public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
      JsonFormat.Value format = this.findFormatOverrides(prov, property, this.handledType());
      if (format == null) {
         return this;
      } else {
         Boolean useTimestamp = null;
         JsonFormat.Shape shape = format.getShape();
         if (shape != JsonFormat.Shape.ARRAY && !shape.isNumeric()) {
            useTimestamp = shape == JsonFormat.Shape.STRING ? Boolean.FALSE : null;
         } else {
            useTimestamp = Boolean.TRUE;
         }

         DateTimeFormatter dtf = this._formatter;
         if (format.hasPattern()) {
            String pattern = format.getPattern();
            Locale locale = format.hasLocale() ? format.getLocale() : prov.getLocale();
            if (locale == null) {
               dtf = DateTimeFormatter.ofPattern(pattern);
            } else {
               dtf = DateTimeFormatter.ofPattern(pattern, locale);
            }

            if (format.hasTimeZone()) {
               dtf = dtf.withZone(format.getTimeZone().toZoneId());
            }
         }

         JSR310FormattedSerializerBase<?> ser = this;
         if (shape != this._shape || useTimestamp != this._useTimestamp || dtf != this._formatter) {
            ser = this.withFormat(useTimestamp, dtf, shape);
         }

         Boolean writeZoneId = format.getFeature(JsonFormat.Feature.WRITE_DATES_WITH_ZONE_ID);
         if (writeZoneId != null) {
            ser = ser.withFeatures(writeZoneId);
         }

         return ser;
      }
   }

   public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
      return this.createSchemaNode(provider.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) ? "array" : "string", true);
   }

   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      SerializerProvider provider = visitor.getProvider();
      boolean useTimestamp = provider != null && this.useTimestamp(provider);
      if (useTimestamp) {
         this._acceptTimestampVisitor(visitor, typeHint);
      } else {
         JsonStringFormatVisitor v2 = visitor.expectStringFormat(typeHint);
         if (v2 != null) {
            v2.format(JsonValueFormat.DATE_TIME);
         }
      }

   }

   protected void _acceptTimestampVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
      if (v2 != null) {
         v2.itemsFormat(JsonFormatTypes.INTEGER);
      }

   }

   protected boolean useTimestamp(SerializerProvider provider) {
      if (this._useTimestamp != null) {
         return this._useTimestamp;
      } else {
         return this._formatter != null ? false : provider.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      }
   }

   protected boolean _useTimestampExplicitOnly(SerializerProvider provider) {
      return this._useTimestamp != null ? this._useTimestamp : false;
   }
}

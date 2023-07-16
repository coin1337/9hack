package software.bernie.shadowed.fasterxml.jackson.databind.ext;

import java.io.IOException;
import java.util.Calendar;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanDescription;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.ContextualSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.Serializers;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.std.CalendarSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.std.StdSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class CoreXMLSerializers extends Serializers.Base {
   public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
      Class<?> raw = type.getRawClass();
      if (!Duration.class.isAssignableFrom(raw) && !QName.class.isAssignableFrom(raw)) {
         return XMLGregorianCalendar.class.isAssignableFrom(raw) ? CoreXMLSerializers.XMLGregorianCalendarSerializer.instance : null;
      } else {
         return ToStringSerializer.instance;
      }
   }

   public static class XMLGregorianCalendarSerializer extends StdSerializer<XMLGregorianCalendar> implements ContextualSerializer {
      static final CoreXMLSerializers.XMLGregorianCalendarSerializer instance = new CoreXMLSerializers.XMLGregorianCalendarSerializer();
      final JsonSerializer<Object> _delegate;

      public XMLGregorianCalendarSerializer() {
         this(CalendarSerializer.instance);
      }

      protected XMLGregorianCalendarSerializer(JsonSerializer<?> del) {
         super(XMLGregorianCalendar.class);
         this._delegate = del;
      }

      public JsonSerializer<?> getDelegatee() {
         return this._delegate;
      }

      public boolean isEmpty(SerializerProvider provider, XMLGregorianCalendar value) {
         return this._delegate.isEmpty(provider, this._convert(value));
      }

      public void serialize(XMLGregorianCalendar value, JsonGenerator gen, SerializerProvider provider) throws IOException {
         this._delegate.serialize(this._convert(value), gen, provider);
      }

      public void serializeWithType(XMLGregorianCalendar value, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
         this._delegate.serializeWithType(this._convert(value), gen, provider, typeSer);
      }

      public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
         this._delegate.acceptJsonFormatVisitor(visitor, (JavaType)null);
      }

      public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
         JsonSerializer<?> ser = prov.handlePrimaryContextualization(this._delegate, property);
         return ser != this._delegate ? new CoreXMLSerializers.XMLGregorianCalendarSerializer(ser) : this;
      }

      protected Calendar _convert(XMLGregorianCalendar input) {
         return input == null ? null : input.toGregorianCalendar();
      }
   }
}

package software.bernie.shadowed.fasterxml.jackson.databind.ser;

import java.util.Map;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.MapperFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMember;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.std.MapSerializer;

public class AnyGetterWriter {
   protected final BeanProperty _property;
   protected final AnnotatedMember _accessor;
   protected JsonSerializer<Object> _serializer;
   protected MapSerializer _mapSerializer;

   public AnyGetterWriter(BeanProperty property, AnnotatedMember accessor, JsonSerializer<?> serializer) {
      this._accessor = accessor;
      this._property = property;
      this._serializer = serializer;
      if (serializer instanceof MapSerializer) {
         this._mapSerializer = (MapSerializer)serializer;
      }

   }

   public void fixAccess(SerializationConfig config) {
      this._accessor.fixAccess(config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
   }

   public void getAndSerialize(Object bean, JsonGenerator gen, SerializerProvider provider) throws Exception {
      Object value = this._accessor.getValue(bean);
      if (value != null) {
         if (!(value instanceof Map)) {
            provider.reportBadDefinition(this._property.getType(), String.format("Value returned by 'any-getter' %s() not java.util.Map but %s", this._accessor.getName(), value.getClass().getName()));
         }

         if (this._mapSerializer != null) {
            this._mapSerializer.serializeFields((Map)value, gen, provider);
         } else {
            this._serializer.serialize(value, gen, provider);
         }
      }
   }

   public void getAndFilter(Object bean, JsonGenerator gen, SerializerProvider provider, PropertyFilter filter) throws Exception {
      Object value = this._accessor.getValue(bean);
      if (value != null) {
         if (!(value instanceof Map)) {
            provider.reportBadDefinition(this._property.getType(), String.format("Value returned by 'any-getter' (%s()) not java.util.Map but %s", this._accessor.getName(), value.getClass().getName()));
         }

         if (this._mapSerializer != null) {
            this._mapSerializer.serializeFilteredAnyProperties(provider, gen, bean, (Map)value, filter, (Object)null);
         } else {
            this._serializer.serialize(value, gen, provider);
         }
      }
   }

   public void resolve(SerializerProvider provider) throws JsonMappingException {
      if (this._serializer instanceof ContextualSerializer) {
         JsonSerializer<?> ser = provider.handlePrimaryContextualization(this._serializer, this._property);
         this._serializer = ser;
         if (ser instanceof MapSerializer) {
            this._mapSerializer = (MapSerializer)ser;
         }
      }

   }
}

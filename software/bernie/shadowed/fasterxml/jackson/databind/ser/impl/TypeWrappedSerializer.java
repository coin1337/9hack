package software.bernie.shadowed.fasterxml.jackson.databind.ser.impl;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.ContextualSerializer;

public final class TypeWrappedSerializer extends JsonSerializer<Object> implements ContextualSerializer {
   protected final TypeSerializer _typeSerializer;
   protected final JsonSerializer<Object> _serializer;

   public TypeWrappedSerializer(TypeSerializer typeSer, JsonSerializer<?> ser) {
      this._typeSerializer = typeSer;
      this._serializer = ser;
   }

   public void serialize(Object value, JsonGenerator g, SerializerProvider provider) throws IOException {
      this._serializer.serializeWithType(value, g, provider, this._typeSerializer);
   }

   public void serializeWithType(Object value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
      this._serializer.serializeWithType(value, g, provider, typeSer);
   }

   public Class<Object> handledType() {
      return Object.class;
   }

   public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
      JsonSerializer<?> ser = this._serializer;
      if (ser instanceof ContextualSerializer) {
         ser = provider.handleSecondaryContextualization(ser, property);
      }

      return ser == this._serializer ? this : new TypeWrappedSerializer(this._typeSerializer, ser);
   }

   public JsonSerializer<Object> valueSerializer() {
      return this._serializer;
   }

   public TypeSerializer typeSerializer() {
      return this._typeSerializer;
   }
}

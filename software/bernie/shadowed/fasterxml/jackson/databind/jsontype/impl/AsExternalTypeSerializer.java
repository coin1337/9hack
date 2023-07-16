package software.bernie.shadowed.fasterxml.jackson.databind.jsontype.impl;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonTypeInfo;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeIdResolver;

public class AsExternalTypeSerializer extends TypeSerializerBase {
   protected final String _typePropertyName;

   public AsExternalTypeSerializer(TypeIdResolver idRes, BeanProperty property, String propName) {
      super(idRes, property);
      this._typePropertyName = propName;
   }

   public AsExternalTypeSerializer forProperty(BeanProperty prop) {
      return this._property == prop ? this : new AsExternalTypeSerializer(this._idResolver, prop, this._typePropertyName);
   }

   public String getPropertyName() {
      return this._typePropertyName;
   }

   public JsonTypeInfo.As getTypeInclusion() {
      return JsonTypeInfo.As.EXTERNAL_PROPERTY;
   }

   protected final void _writeScalarPrefix(Object value, JsonGenerator g) throws IOException {
   }

   protected final void _writeObjectPrefix(Object value, JsonGenerator g) throws IOException {
      g.writeStartObject();
   }

   protected final void _writeArrayPrefix(Object value, JsonGenerator g) throws IOException {
      g.writeStartArray();
   }

   protected final void _writeScalarSuffix(Object value, JsonGenerator g, String typeId) throws IOException {
      if (typeId != null) {
         g.writeStringField(this._typePropertyName, typeId);
      }

   }

   protected final void _writeObjectSuffix(Object value, JsonGenerator g, String typeId) throws IOException {
      g.writeEndObject();
      if (typeId != null) {
         g.writeStringField(this._typePropertyName, typeId);
      }

   }

   protected final void _writeArraySuffix(Object value, JsonGenerator g, String typeId) throws IOException {
      g.writeEndArray();
      if (typeId != null) {
         g.writeStringField(this._typePropertyName, typeId);
      }

   }
}

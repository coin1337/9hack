package software.bernie.shadowed.fasterxml.jackson.databind.ser.impl;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map.Entry;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.SerializableString;
import software.bernie.shadowed.fasterxml.jackson.core.io.SerializedString;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonNode;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import software.bernie.shadowed.fasterxml.jackson.databind.node.ObjectNode;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import software.bernie.shadowed.fasterxml.jackson.databind.util.NameTransformer;

public class UnwrappingBeanPropertyWriter extends BeanPropertyWriter implements Serializable {
   private static final long serialVersionUID = 1L;
   protected final NameTransformer _nameTransformer;

   public UnwrappingBeanPropertyWriter(BeanPropertyWriter base, NameTransformer unwrapper) {
      super(base);
      this._nameTransformer = unwrapper;
   }

   protected UnwrappingBeanPropertyWriter(UnwrappingBeanPropertyWriter base, NameTransformer transformer, SerializedString name) {
      super(base, (SerializedString)name);
      this._nameTransformer = transformer;
   }

   public UnwrappingBeanPropertyWriter rename(NameTransformer transformer) {
      String oldName = this._name.getValue();
      String newName = transformer.transform(oldName);
      transformer = NameTransformer.chainedTransformer(transformer, this._nameTransformer);
      return this._new(transformer, new SerializedString(newName));
   }

   protected UnwrappingBeanPropertyWriter _new(NameTransformer transformer, SerializedString newName) {
      return new UnwrappingBeanPropertyWriter(this, transformer, newName);
   }

   public boolean isUnwrapping() {
      return true;
   }

   public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
      Object value = this.get(bean);
      if (value != null) {
         JsonSerializer<Object> ser = this._serializer;
         if (ser == null) {
            Class<?> cls = value.getClass();
            PropertySerializerMap map = this._dynamicSerializers;
            ser = map.serializerFor(cls);
            if (ser == null) {
               ser = this._findAndAddDynamic(map, cls, prov);
            }
         }

         if (this._suppressableValue != null) {
            if (MARKER_FOR_EMPTY == this._suppressableValue) {
               if (ser.isEmpty(prov, value)) {
                  return;
               }
            } else if (this._suppressableValue.equals(value)) {
               return;
            }
         }

         if (value != bean || !this._handleSelfReference(bean, gen, prov, ser)) {
            if (!ser.isUnwrappingSerializer()) {
               gen.writeFieldName((SerializableString)this._name);
            }

            if (this._typeSerializer == null) {
               ser.serialize(value, gen, prov);
            } else {
               ser.serializeWithType(value, gen, prov, this._typeSerializer);
            }

         }
      }
   }

   public void assignSerializer(JsonSerializer<Object> ser) {
      if (ser != null) {
         NameTransformer t = this._nameTransformer;
         if (ser.isUnwrappingSerializer()) {
            t = NameTransformer.chainedTransformer(t, ((UnwrappingBeanSerializer)ser)._nameTransformer);
         }

         ser = ser.unwrappingSerializer(t);
      }

      super.assignSerializer(ser);
   }

   public void depositSchemaProperty(final JsonObjectFormatVisitor visitor, SerializerProvider provider) throws JsonMappingException {
      JsonSerializer<Object> ser = provider.findValueSerializer((JavaType)this.getType(), this).unwrappingSerializer(this._nameTransformer);
      if (ser.isUnwrappingSerializer()) {
         ser.acceptJsonFormatVisitor(new JsonFormatVisitorWrapper.Base(provider) {
            public JsonObjectFormatVisitor expectObjectFormat(JavaType type) throws JsonMappingException {
               return visitor;
            }
         }, this.getType());
      } else {
         super.depositSchemaProperty(visitor, provider);
      }

   }

   protected void _depositSchemaProperty(ObjectNode propertiesNode, JsonNode schemaNode) {
      JsonNode props = schemaNode.get("properties");
      Entry entry;
      String name;
      if (props != null) {
         for(Iterator it = props.fields(); it.hasNext(); propertiesNode.set(name, (JsonNode)entry.getValue())) {
            entry = (Entry)it.next();
            name = (String)entry.getKey();
            if (this._nameTransformer != null) {
               name = this._nameTransformer.transform(name);
            }
         }
      }

   }

   protected JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, Class<?> type, SerializerProvider provider) throws JsonMappingException {
      JsonSerializer serializer;
      if (this._nonTrivialBaseType != null) {
         JavaType subtype = provider.constructSpecializedType(this._nonTrivialBaseType, type);
         serializer = provider.findValueSerializer((JavaType)subtype, this);
      } else {
         serializer = provider.findValueSerializer((Class)type, this);
      }

      NameTransformer t = this._nameTransformer;
      if (serializer.isUnwrappingSerializer()) {
         t = NameTransformer.chainedTransformer(t, ((UnwrappingBeanSerializer)serializer)._nameTransformer);
      }

      serializer = serializer.unwrappingSerializer(t);
      this._dynamicSerializers = this._dynamicSerializers.newWith(type, serializer);
      return serializer;
   }
}

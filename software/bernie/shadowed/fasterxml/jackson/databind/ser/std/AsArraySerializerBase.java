package software.bernie.shadowed.fasterxml.jackson.databind.ser.std;

import java.io.IOException;
import java.lang.reflect.Type;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.type.WritableTypeId;
import software.bernie.shadowed.fasterxml.jackson.databind.AnnotationIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonNode;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMember;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonschema.JsonSchema;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonschema.SchemaAware;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.node.ObjectNode;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.ContainerSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.ContextualSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;

public abstract class AsArraySerializerBase<T> extends ContainerSerializer<T> implements ContextualSerializer {
   protected final JavaType _elementType;
   protected final BeanProperty _property;
   protected final boolean _staticTyping;
   protected final Boolean _unwrapSingle;
   protected final TypeSerializer _valueTypeSerializer;
   protected final JsonSerializer<Object> _elementSerializer;
   protected PropertySerializerMap _dynamicSerializers;

   protected AsArraySerializerBase(Class<?> cls, JavaType et, boolean staticTyping, TypeSerializer vts, JsonSerializer<Object> elementSerializer) {
      super(cls, false);
      this._elementType = et;
      this._staticTyping = staticTyping || et != null && et.isFinal();
      this._valueTypeSerializer = vts;
      this._property = null;
      this._elementSerializer = elementSerializer;
      this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
      this._unwrapSingle = null;
   }

   /** @deprecated */
   @Deprecated
   protected AsArraySerializerBase(Class<?> cls, JavaType et, boolean staticTyping, TypeSerializer vts, BeanProperty property, JsonSerializer<Object> elementSerializer) {
      super(cls, false);
      this._elementType = et;
      this._staticTyping = staticTyping || et != null && et.isFinal();
      this._valueTypeSerializer = vts;
      this._property = property;
      this._elementSerializer = elementSerializer;
      this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
      this._unwrapSingle = null;
   }

   protected AsArraySerializerBase(AsArraySerializerBase<?> src, BeanProperty property, TypeSerializer vts, JsonSerializer<?> elementSerializer, Boolean unwrapSingle) {
      super((ContainerSerializer)src);
      this._elementType = src._elementType;
      this._staticTyping = src._staticTyping;
      this._valueTypeSerializer = vts;
      this._property = property;
      this._elementSerializer = elementSerializer;
      this._dynamicSerializers = src._dynamicSerializers;
      this._unwrapSingle = unwrapSingle;
   }

   /** @deprecated */
   @Deprecated
   protected AsArraySerializerBase(AsArraySerializerBase<?> src, BeanProperty property, TypeSerializer vts, JsonSerializer<?> elementSerializer) {
      this(src, property, vts, elementSerializer, src._unwrapSingle);
   }

   /** @deprecated */
   @Deprecated
   public final AsArraySerializerBase<T> withResolved(BeanProperty property, TypeSerializer vts, JsonSerializer<?> elementSerializer) {
      return this.withResolved(property, vts, elementSerializer, this._unwrapSingle);
   }

   public abstract AsArraySerializerBase<T> withResolved(BeanProperty var1, TypeSerializer var2, JsonSerializer<?> var3, Boolean var4);

   public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property) throws JsonMappingException {
      TypeSerializer typeSer = this._valueTypeSerializer;
      if (typeSer != null) {
         typeSer = typeSer.forProperty(property);
      }

      JsonSerializer<?> ser = null;
      Boolean unwrapSingle = null;
      if (property != null) {
         AnnotationIntrospector intr = serializers.getAnnotationIntrospector();
         AnnotatedMember m = property.getMember();
         if (m != null) {
            Object serDef = intr.findContentSerializer(m);
            if (serDef != null) {
               ser = serializers.serializerInstance(m, serDef);
            }
         }
      }

      JsonFormat.Value format = this.findFormatOverrides(serializers, property, this.handledType());
      if (format != null) {
         unwrapSingle = format.getFeature(JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
      }

      if (ser == null) {
         ser = this._elementSerializer;
      }

      ser = this.findContextualConvertingSerializer(serializers, property, ser);
      if (ser == null && this._elementType != null && this._staticTyping && !this._elementType.isJavaLangObject()) {
         ser = serializers.findValueSerializer(this._elementType, property);
      }

      return ser == this._elementSerializer && property == this._property && this._valueTypeSerializer == typeSer && this._unwrapSingle == unwrapSingle ? this : this.withResolved(property, typeSer, ser, unwrapSingle);
   }

   public JavaType getContentType() {
      return this._elementType;
   }

   public JsonSerializer<?> getContentSerializer() {
      return this._elementSerializer;
   }

   public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      if (provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED) && this.hasSingleElement(value)) {
         this.serializeContents(value, gen, provider);
      } else {
         gen.writeStartArray();
         gen.setCurrentValue(value);
         this.serializeContents(value, gen, provider);
         gen.writeEndArray();
      }
   }

   public void serializeWithType(T value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
      g.setCurrentValue(value);
      WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, JsonToken.START_ARRAY));
      this.serializeContents(value, g, provider);
      typeSer.writeTypeSuffix(g, typeIdDef);
   }

   protected abstract void serializeContents(T var1, JsonGenerator var2, SerializerProvider var3) throws IOException;

   public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
      ObjectNode o = this.createSchemaNode("array", true);
      if (this._elementSerializer != null) {
         JsonNode schemaNode = null;
         if (this._elementSerializer instanceof SchemaAware) {
            schemaNode = ((SchemaAware)this._elementSerializer).getSchema(provider, (Type)null);
         }

         if (schemaNode == null) {
            schemaNode = JsonSchema.getDefaultSchemaNode();
         }

         o.set("items", schemaNode);
      }

      return o;
   }

   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      JsonSerializer<?> valueSer = this._elementSerializer;
      if (valueSer == null && this._elementType != null) {
         valueSer = visitor.getProvider().findValueSerializer(this._elementType, this._property);
      }

      this.visitArrayFormat(visitor, typeHint, valueSer, this._elementType);
   }

   protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, Class<?> type, SerializerProvider provider) throws JsonMappingException {
      PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, this._property);
      if (map != result.map) {
         this._dynamicSerializers = result.map;
      }

      return result.serializer;
   }

   protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, JavaType type, SerializerProvider provider) throws JsonMappingException {
      PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, this._property);
      if (map != result.map) {
         this._dynamicSerializers = result.map;
      }

      return result.serializer;
   }
}

package software.bernie.shadowed.fasterxml.jackson.databind.ser.std;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonInclude;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.databind.AnnotationIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonNode;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMember;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonNumberFormatVisitor;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonschema.SchemaAware;
import software.bernie.shadowed.fasterxml.jackson.databind.node.JsonNodeFactory;
import software.bernie.shadowed.fasterxml.jackson.databind.node.ObjectNode;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.FilterProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.PropertyFilter;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;
import software.bernie.shadowed.fasterxml.jackson.databind.util.Converter;

public abstract class StdSerializer<T> extends JsonSerializer<T> implements JsonFormatVisitable, SchemaAware, Serializable {
   private static final long serialVersionUID = 1L;
   private static final Object KEY_CONTENT_CONVERTER_LOCK = new Object();
   protected final Class<T> _handledType;

   protected StdSerializer(Class<T> t) {
      this._handledType = t;
   }

   protected StdSerializer(JavaType type) {
      this._handledType = type.getRawClass();
   }

   protected StdSerializer(Class<?> t, boolean dummy) {
      this._handledType = t;
   }

   protected StdSerializer(StdSerializer<?> src) {
      this._handledType = src._handledType;
   }

   public Class<T> handledType() {
      return this._handledType;
   }

   public abstract void serialize(T var1, JsonGenerator var2, SerializerProvider var3) throws IOException;

   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      visitor.expectAnyFormat(typeHint);
   }

   public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
      return this.createSchemaNode("string");
   }

   public JsonNode getSchema(SerializerProvider provider, Type typeHint, boolean isOptional) throws JsonMappingException {
      ObjectNode schema = (ObjectNode)this.getSchema(provider, typeHint);
      if (!isOptional) {
         schema.put("required", !isOptional);
      }

      return schema;
   }

   protected ObjectNode createSchemaNode(String type) {
      ObjectNode schema = JsonNodeFactory.instance.objectNode();
      schema.put("type", type);
      return schema;
   }

   protected ObjectNode createSchemaNode(String type, boolean isOptional) {
      ObjectNode schema = this.createSchemaNode(type);
      if (!isOptional) {
         schema.put("required", !isOptional);
      }

      return schema;
   }

   protected void visitStringFormat(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      visitor.expectStringFormat(typeHint);
   }

   protected void visitStringFormat(JsonFormatVisitorWrapper visitor, JavaType typeHint, JsonValueFormat format) throws JsonMappingException {
      JsonStringFormatVisitor v2 = visitor.expectStringFormat(typeHint);
      if (v2 != null) {
         v2.format(format);
      }

   }

   protected void visitIntFormat(JsonFormatVisitorWrapper visitor, JavaType typeHint, JsonParser.NumberType numberType) throws JsonMappingException {
      JsonIntegerFormatVisitor v2 = visitor.expectIntegerFormat(typeHint);
      if (_neitherNull(v2, numberType)) {
         v2.numberType(numberType);
      }

   }

   protected void visitIntFormat(JsonFormatVisitorWrapper visitor, JavaType typeHint, JsonParser.NumberType numberType, JsonValueFormat format) throws JsonMappingException {
      JsonIntegerFormatVisitor v2 = visitor.expectIntegerFormat(typeHint);
      if (v2 != null) {
         if (numberType != null) {
            v2.numberType(numberType);
         }

         if (format != null) {
            v2.format(format);
         }
      }

   }

   protected void visitFloatFormat(JsonFormatVisitorWrapper visitor, JavaType typeHint, JsonParser.NumberType numberType) throws JsonMappingException {
      JsonNumberFormatVisitor v2 = visitor.expectNumberFormat(typeHint);
      if (v2 != null) {
         v2.numberType(numberType);
      }

   }

   protected void visitArrayFormat(JsonFormatVisitorWrapper visitor, JavaType typeHint, JsonSerializer<?> itemSerializer, JavaType itemType) throws JsonMappingException {
      JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
      if (_neitherNull(v2, itemSerializer)) {
         v2.itemsFormat(itemSerializer, itemType);
      }

   }

   protected void visitArrayFormat(JsonFormatVisitorWrapper visitor, JavaType typeHint, JsonFormatTypes itemType) throws JsonMappingException {
      JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
      if (v2 != null) {
         v2.itemsFormat(itemType);
      }

   }

   public void wrapAndThrow(SerializerProvider provider, Throwable t, Object bean, String fieldName) throws IOException {
      while(t instanceof InvocationTargetException && t.getCause() != null) {
         t = t.getCause();
      }

      ClassUtil.throwIfError(t);
      boolean wrap = provider == null || provider.isEnabled(SerializationFeature.WRAP_EXCEPTIONS);
      if (t instanceof IOException) {
         if (!wrap || !(t instanceof JsonMappingException)) {
            throw (IOException)t;
         }
      } else if (!wrap) {
         ClassUtil.throwIfRTE(t);
      }

      throw JsonMappingException.wrapWithPath(t, bean, fieldName);
   }

   public void wrapAndThrow(SerializerProvider provider, Throwable t, Object bean, int index) throws IOException {
      while(t instanceof InvocationTargetException && t.getCause() != null) {
         t = t.getCause();
      }

      ClassUtil.throwIfError(t);
      boolean wrap = provider == null || provider.isEnabled(SerializationFeature.WRAP_EXCEPTIONS);
      if (t instanceof IOException) {
         if (!wrap || !(t instanceof JsonMappingException)) {
            throw (IOException)t;
         }
      } else if (!wrap) {
         ClassUtil.throwIfRTE(t);
      }

      throw JsonMappingException.wrapWithPath(t, bean, index);
   }

   protected JsonSerializer<?> findContextualConvertingSerializer(SerializerProvider provider, BeanProperty property, JsonSerializer<?> existingSerializer) throws JsonMappingException {
      Map<Object, Object> conversions = (Map)provider.getAttribute(KEY_CONTENT_CONVERTER_LOCK);
      if (conversions != null) {
         Object lock = ((Map)conversions).get(property);
         if (lock != null) {
            return existingSerializer;
         }
      } else {
         conversions = new IdentityHashMap();
         provider.setAttribute(KEY_CONTENT_CONVERTER_LOCK, conversions);
      }

      ((Map)conversions).put(property, Boolean.TRUE);

      JsonSerializer var6;
      try {
         JsonSerializer<?> ser = this.findConvertingContentSerializer(provider, property, existingSerializer);
         if (ser == null) {
            return existingSerializer;
         }

         var6 = provider.handleSecondaryContextualization(ser, property);
      } finally {
         ((Map)conversions).remove(property);
      }

      return var6;
   }

   /** @deprecated */
   @Deprecated
   protected JsonSerializer<?> findConvertingContentSerializer(SerializerProvider provider, BeanProperty prop, JsonSerializer<?> existingSerializer) throws JsonMappingException {
      AnnotationIntrospector intr = provider.getAnnotationIntrospector();
      if (_neitherNull(intr, prop)) {
         AnnotatedMember m = prop.getMember();
         if (m != null) {
            Object convDef = intr.findSerializationContentConverter(m);
            if (convDef != null) {
               Converter<Object, Object> conv = provider.converterInstance(prop.getMember(), convDef);
               JavaType delegateType = conv.getOutputType(provider.getTypeFactory());
               if (existingSerializer == null && !delegateType.isJavaLangObject()) {
                  existingSerializer = provider.findValueSerializer(delegateType);
               }

               return new StdDelegatingSerializer(conv, delegateType, existingSerializer);
            }
         }
      }

      return existingSerializer;
   }

   protected PropertyFilter findPropertyFilter(SerializerProvider provider, Object filterId, Object valueToFilter) throws JsonMappingException {
      FilterProvider filters = provider.getFilterProvider();
      if (filters == null) {
         provider.reportBadDefinition(this.handledType(), "Cannot resolve PropertyFilter with id '" + filterId + "'; no FilterProvider configured");
      }

      return filters.findPropertyFilter(filterId, valueToFilter);
   }

   protected JsonFormat.Value findFormatOverrides(SerializerProvider provider, BeanProperty prop, Class<?> typeForDefaults) {
      return prop != null ? prop.findPropertyFormat(provider.getConfig(), typeForDefaults) : provider.getDefaultPropertyFormat(typeForDefaults);
   }

   protected Boolean findFormatFeature(SerializerProvider provider, BeanProperty prop, Class<?> typeForDefaults, JsonFormat.Feature feat) {
      JsonFormat.Value format = this.findFormatOverrides(provider, prop, typeForDefaults);
      return format != null ? format.getFeature(feat) : null;
   }

   protected JsonInclude.Value findIncludeOverrides(SerializerProvider provider, BeanProperty prop, Class<?> typeForDefaults) {
      return prop != null ? prop.findPropertyInclusion(provider.getConfig(), typeForDefaults) : provider.getDefaultPropertyInclusion(typeForDefaults);
   }

   protected JsonSerializer<?> findAnnotatedContentSerializer(SerializerProvider serializers, BeanProperty property) throws JsonMappingException {
      if (property != null) {
         AnnotatedMember m = property.getMember();
         AnnotationIntrospector intr = serializers.getAnnotationIntrospector();
         if (m != null) {
            Object serDef = intr.findContentSerializer(m);
            if (serDef != null) {
               return serializers.serializerInstance(m, serDef);
            }
         }
      }

      return null;
   }

   protected boolean isDefaultSerializer(JsonSerializer<?> serializer) {
      return ClassUtil.isJacksonStdImpl((Object)serializer);
   }

   protected static final boolean _neitherNull(Object a, Object b) {
      return a != null && b != null;
   }

   protected static final boolean _nonEmpty(Collection<?> c) {
      return c != null && !c.isEmpty();
   }
}

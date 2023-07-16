package software.bernie.shadowed.fasterxml.jackson.databind.ser.impl;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonInclude;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.type.WritableTypeId;
import software.bernie.shadowed.fasterxml.jackson.databind.AnnotationIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMember;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.ContainerSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.ContextualSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ArrayBuilders;
import software.bernie.shadowed.fasterxml.jackson.databind.util.BeanUtil;

@JacksonStdImpl
public class MapEntrySerializer extends ContainerSerializer<Entry<?, ?>> implements ContextualSerializer {
   public static final Object MARKER_FOR_EMPTY;
   protected final BeanProperty _property;
   protected final boolean _valueTypeIsStatic;
   protected final JavaType _entryType;
   protected final JavaType _keyType;
   protected final JavaType _valueType;
   protected JsonSerializer<Object> _keySerializer;
   protected JsonSerializer<Object> _valueSerializer;
   protected final TypeSerializer _valueTypeSerializer;
   protected PropertySerializerMap _dynamicValueSerializers;
   protected final Object _suppressableValue;
   protected final boolean _suppressNulls;

   public MapEntrySerializer(JavaType type, JavaType keyType, JavaType valueType, boolean staticTyping, TypeSerializer vts, BeanProperty property) {
      super(type);
      this._entryType = type;
      this._keyType = keyType;
      this._valueType = valueType;
      this._valueTypeIsStatic = staticTyping;
      this._valueTypeSerializer = vts;
      this._property = property;
      this._dynamicValueSerializers = PropertySerializerMap.emptyForProperties();
      this._suppressableValue = null;
      this._suppressNulls = false;
   }

   /** @deprecated */
   @Deprecated
   protected MapEntrySerializer(MapEntrySerializer src, BeanProperty property, TypeSerializer vts, JsonSerializer<?> keySer, JsonSerializer<?> valueSer) {
      this(src, property, vts, keySer, valueSer, src._suppressableValue, src._suppressNulls);
   }

   protected MapEntrySerializer(MapEntrySerializer src, BeanProperty property, TypeSerializer vts, JsonSerializer<?> keySer, JsonSerializer<?> valueSer, Object suppressableValue, boolean suppressNulls) {
      super(Map.class, false);
      this._entryType = src._entryType;
      this._keyType = src._keyType;
      this._valueType = src._valueType;
      this._valueTypeIsStatic = src._valueTypeIsStatic;
      this._valueTypeSerializer = src._valueTypeSerializer;
      this._keySerializer = keySer;
      this._valueSerializer = valueSer;
      this._dynamicValueSerializers = src._dynamicValueSerializers;
      this._property = src._property;
      this._suppressableValue = suppressableValue;
      this._suppressNulls = suppressNulls;
   }

   public ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
      return new MapEntrySerializer(this, this._property, vts, this._keySerializer, this._valueSerializer, this._suppressableValue, this._suppressNulls);
   }

   public MapEntrySerializer withResolved(BeanProperty property, JsonSerializer<?> keySerializer, JsonSerializer<?> valueSerializer, Object suppressableValue, boolean suppressNulls) {
      return new MapEntrySerializer(this, property, this._valueTypeSerializer, keySerializer, valueSerializer, suppressableValue, suppressNulls);
   }

   public MapEntrySerializer withContentInclusion(Object suppressableValue, boolean suppressNulls) {
      return this._suppressableValue == suppressableValue && this._suppressNulls == suppressNulls ? this : new MapEntrySerializer(this, this._property, this._valueTypeSerializer, this._keySerializer, this._valueSerializer, suppressableValue, suppressNulls);
   }

   public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
      JsonSerializer<?> ser = null;
      JsonSerializer<?> keySer = null;
      AnnotationIntrospector intr = provider.getAnnotationIntrospector();
      AnnotatedMember propertyAcc = property == null ? null : property.getMember();
      Object valueToSuppress;
      if (propertyAcc != null && intr != null) {
         valueToSuppress = intr.findKeySerializer(propertyAcc);
         if (valueToSuppress != null) {
            keySer = provider.serializerInstance(propertyAcc, valueToSuppress);
         }

         valueToSuppress = intr.findContentSerializer(propertyAcc);
         if (valueToSuppress != null) {
            ser = provider.serializerInstance(propertyAcc, valueToSuppress);
         }
      }

      if (ser == null) {
         ser = this._valueSerializer;
      }

      ser = this.findContextualConvertingSerializer(provider, property, ser);
      if (ser == null && this._valueTypeIsStatic && !this._valueType.isJavaLangObject()) {
         ser = provider.findValueSerializer(this._valueType, property);
      }

      if (keySer == null) {
         keySer = this._keySerializer;
      }

      if (keySer == null) {
         keySer = provider.findKeySerializer(this._keyType, property);
      } else {
         keySer = provider.handleSecondaryContextualization(keySer, property);
      }

      valueToSuppress = this._suppressableValue;
      boolean suppressNulls = this._suppressNulls;
      if (property != null) {
         JsonInclude.Value inclV = property.findPropertyInclusion(provider.getConfig(), (Class)null);
         if (inclV != null) {
            JsonInclude.Include incl = inclV.getContentInclusion();
            if (incl != JsonInclude.Include.USE_DEFAULTS) {
               switch(incl) {
               case NON_DEFAULT:
                  valueToSuppress = BeanUtil.getDefaultValue(this._valueType);
                  suppressNulls = true;
                  if (valueToSuppress != null && valueToSuppress.getClass().isArray()) {
                     valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
                  }
                  break;
               case NON_ABSENT:
                  suppressNulls = true;
                  valueToSuppress = this._valueType.isReferenceType() ? MARKER_FOR_EMPTY : null;
                  break;
               case NON_EMPTY:
                  suppressNulls = true;
                  valueToSuppress = MARKER_FOR_EMPTY;
                  break;
               case CUSTOM:
                  valueToSuppress = provider.includeFilterInstance((BeanPropertyDefinition)null, inclV.getContentFilter());
                  if (valueToSuppress == null) {
                     suppressNulls = true;
                  } else {
                     suppressNulls = provider.includeFilterSuppressNulls(valueToSuppress);
                  }
                  break;
               case NON_NULL:
                  valueToSuppress = null;
                  suppressNulls = true;
                  break;
               case ALWAYS:
               default:
                  valueToSuppress = null;
                  suppressNulls = false;
               }
            }
         }
      }

      MapEntrySerializer mser = this.withResolved(property, keySer, ser, valueToSuppress, suppressNulls);
      return mser;
   }

   public JavaType getContentType() {
      return this._valueType;
   }

   public JsonSerializer<?> getContentSerializer() {
      return this._valueSerializer;
   }

   public boolean hasSingleElement(Entry<?, ?> value) {
      return true;
   }

   public boolean isEmpty(SerializerProvider prov, Entry<?, ?> entry) {
      Object value = entry.getValue();
      if (value == null) {
         return this._suppressNulls;
      } else if (this._suppressableValue == null) {
         return false;
      } else {
         JsonSerializer<Object> valueSer = this._valueSerializer;
         if (valueSer == null) {
            Class<?> cc = value.getClass();
            valueSer = this._dynamicValueSerializers.serializerFor(cc.getClass());
            if (valueSer == null) {
               try {
                  valueSer = this._findAndAddDynamic(this._dynamicValueSerializers, cc, prov);
               } catch (JsonMappingException var7) {
                  return false;
               }
            }
         }

         return this._suppressableValue == MARKER_FOR_EMPTY ? valueSer.isEmpty(prov, value) : this._suppressableValue.equals(value);
      }
   }

   public void serialize(Entry<?, ?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      gen.writeStartObject(value);
      this.serializeDynamic(value, gen, provider);
      gen.writeEndObject();
   }

   public void serializeWithType(Entry<?, ?> value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
      g.setCurrentValue(value);
      WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, JsonToken.START_OBJECT));
      this.serializeDynamic(value, g, provider);
      typeSer.writeTypeSuffix(g, typeIdDef);
   }

   protected void serializeDynamic(Entry<?, ?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      TypeSerializer vts = this._valueTypeSerializer;
      Object keyElem = value.getKey();
      JsonSerializer keySerializer;
      if (keyElem == null) {
         keySerializer = provider.findNullKeySerializer(this._keyType, this._property);
      } else {
         keySerializer = this._keySerializer;
      }

      Object valueElem = value.getValue();
      JsonSerializer valueSer;
      if (valueElem == null) {
         if (this._suppressNulls) {
            return;
         }

         valueSer = provider.getDefaultNullValueSerializer();
      } else {
         valueSer = this._valueSerializer;
         if (valueSer == null) {
            Class<?> cc = valueElem.getClass();
            valueSer = this._dynamicValueSerializers.serializerFor(cc);
            if (valueSer == null) {
               if (this._valueType.hasGenericTypes()) {
                  valueSer = this._findAndAddDynamic(this._dynamicValueSerializers, provider.constructSpecializedType(this._valueType, cc), provider);
               } else {
                  valueSer = this._findAndAddDynamic(this._dynamicValueSerializers, cc, provider);
               }
            }
         }

         if (this._suppressableValue != null) {
            if (this._suppressableValue == MARKER_FOR_EMPTY && valueSer.isEmpty(provider, valueElem)) {
               return;
            }

            if (this._suppressableValue.equals(valueElem)) {
               return;
            }
         }
      }

      keySerializer.serialize(keyElem, gen, provider);

      try {
         if (vts == null) {
            valueSer.serialize(valueElem, gen, provider);
         } else {
            valueSer.serializeWithType(valueElem, gen, provider, vts);
         }
      } catch (Exception var11) {
         String keyDesc = "" + keyElem;
         this.wrapAndThrow(provider, var11, value, keyDesc);
      }

   }

   protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, Class<?> type, SerializerProvider provider) throws JsonMappingException {
      PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, this._property);
      if (map != result.map) {
         this._dynamicValueSerializers = result.map;
      }

      return result.serializer;
   }

   protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, JavaType type, SerializerProvider provider) throws JsonMappingException {
      PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, this._property);
      if (map != result.map) {
         this._dynamicValueSerializers = result.map;
      }

      return result.serializer;
   }

   static {
      MARKER_FOR_EMPTY = JsonInclude.Include.NON_EMPTY;
   }
}

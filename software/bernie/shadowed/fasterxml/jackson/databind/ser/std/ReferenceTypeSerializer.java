package software.bernie.shadowed.fasterxml.jackson.databind.ser.std;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonInclude;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.databind.AnnotationIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.MapperFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.RuntimeJsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.JsonSerialize;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.Annotated;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.ContextualSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import software.bernie.shadowed.fasterxml.jackson.databind.type.ReferenceType;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ArrayBuilders;
import software.bernie.shadowed.fasterxml.jackson.databind.util.BeanUtil;
import software.bernie.shadowed.fasterxml.jackson.databind.util.NameTransformer;

public abstract class ReferenceTypeSerializer<T> extends StdSerializer<T> implements ContextualSerializer {
   private static final long serialVersionUID = 1L;
   public static final Object MARKER_FOR_EMPTY;
   protected final JavaType _referredType;
   protected final BeanProperty _property;
   protected final TypeSerializer _valueTypeSerializer;
   protected final JsonSerializer<Object> _valueSerializer;
   protected final NameTransformer _unwrapper;
   protected transient PropertySerializerMap _dynamicSerializers;
   protected final Object _suppressableValue;
   protected final boolean _suppressNulls;

   public ReferenceTypeSerializer(ReferenceType fullType, boolean staticTyping, TypeSerializer vts, JsonSerializer<Object> ser) {
      super((JavaType)fullType);
      this._referredType = fullType.getReferencedType();
      this._property = null;
      this._valueTypeSerializer = vts;
      this._valueSerializer = ser;
      this._unwrapper = null;
      this._suppressableValue = null;
      this._suppressNulls = false;
      this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
   }

   protected ReferenceTypeSerializer(ReferenceTypeSerializer<?> base, BeanProperty property, TypeSerializer vts, JsonSerializer<?> valueSer, NameTransformer unwrapper, Object suppressableValue, boolean suppressNulls) {
      super((StdSerializer)base);
      this._referredType = base._referredType;
      this._dynamicSerializers = base._dynamicSerializers;
      this._property = property;
      this._valueTypeSerializer = vts;
      this._valueSerializer = valueSer;
      this._unwrapper = unwrapper;
      this._suppressableValue = suppressableValue;
      this._suppressNulls = suppressNulls;
   }

   public JsonSerializer<T> unwrappingSerializer(NameTransformer transformer) {
      JsonSerializer<Object> valueSer = this._valueSerializer;
      if (valueSer != null) {
         valueSer = valueSer.unwrappingSerializer(transformer);
      }

      NameTransformer unwrapper = this._unwrapper == null ? transformer : NameTransformer.chainedTransformer(transformer, this._unwrapper);
      return this._valueSerializer == valueSer && this._unwrapper == unwrapper ? this : this.withResolved(this._property, this._valueTypeSerializer, valueSer, unwrapper);
   }

   protected abstract ReferenceTypeSerializer<T> withResolved(BeanProperty var1, TypeSerializer var2, JsonSerializer<?> var3, NameTransformer var4);

   public abstract ReferenceTypeSerializer<T> withContentInclusion(Object var1, boolean var2);

   protected abstract boolean _isValuePresent(T var1);

   protected abstract Object _getReferenced(T var1);

   protected abstract Object _getReferencedIfPresent(T var1);

   public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
      TypeSerializer typeSer = this._valueTypeSerializer;
      if (typeSer != null) {
         typeSer = typeSer.forProperty(property);
      }

      JsonSerializer<?> ser = this.findAnnotatedContentSerializer(provider, property);
      if (ser == null) {
         ser = this._valueSerializer;
         if (ser == null) {
            if (this._useStatic(provider, property, this._referredType)) {
               ser = this._findSerializer(provider, this._referredType, property);
            }
         } else {
            ser = provider.handlePrimaryContextualization(ser, property);
         }
      }

      ReferenceTypeSerializer refSer;
      if (this._property == property && this._valueTypeSerializer == typeSer && this._valueSerializer == ser) {
         refSer = this;
      } else {
         refSer = this.withResolved(property, typeSer, ser, this._unwrapper);
      }

      if (property != null) {
         JsonInclude.Value inclV = property.findPropertyInclusion(provider.getConfig(), this.handledType());
         if (inclV != null) {
            JsonInclude.Include incl = inclV.getContentInclusion();
            if (incl != JsonInclude.Include.USE_DEFAULTS) {
               Object valueToSuppress;
               boolean suppressNulls;
               switch(incl) {
               case NON_DEFAULT:
                  valueToSuppress = BeanUtil.getDefaultValue(this._referredType);
                  suppressNulls = true;
                  if (valueToSuppress != null && valueToSuppress.getClass().isArray()) {
                     valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
                  }
                  break;
               case NON_ABSENT:
                  suppressNulls = true;
                  valueToSuppress = this._referredType.isReferenceType() ? MARKER_FOR_EMPTY : null;
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

               if (this._suppressableValue != valueToSuppress || this._suppressNulls != suppressNulls) {
                  refSer = refSer.withContentInclusion(valueToSuppress, suppressNulls);
               }
            }
         }
      }

      return refSer;
   }

   protected boolean _useStatic(SerializerProvider provider, BeanProperty property, JavaType referredType) {
      if (referredType.isJavaLangObject()) {
         return false;
      } else if (referredType.isFinal()) {
         return true;
      } else if (referredType.useStaticType()) {
         return true;
      } else {
         AnnotationIntrospector intr = provider.getAnnotationIntrospector();
         if (intr != null && property != null) {
            Annotated ann = property.getMember();
            if (ann != null) {
               JsonSerialize.Typing t = intr.findSerializationTyping(property.getMember());
               if (t == JsonSerialize.Typing.STATIC) {
                  return true;
               }

               if (t == JsonSerialize.Typing.DYNAMIC) {
                  return false;
               }
            }
         }

         return provider.isEnabled(MapperFeature.USE_STATIC_TYPING);
      }
   }

   public boolean isEmpty(SerializerProvider provider, T value) {
      if (!this._isValuePresent(value)) {
         return true;
      } else {
         Object contents = this._getReferenced(value);
         if (contents == null) {
            return this._suppressNulls;
         } else if (this._suppressableValue == null) {
            return false;
         } else {
            JsonSerializer<Object> ser = this._valueSerializer;
            if (ser == null) {
               try {
                  ser = this._findCachedSerializer(provider, contents.getClass());
               } catch (JsonMappingException var6) {
                  throw new RuntimeJsonMappingException(var6);
               }
            }

            return this._suppressableValue == MARKER_FOR_EMPTY ? ser.isEmpty(provider, contents) : this._suppressableValue.equals(contents);
         }
      }
   }

   public boolean isUnwrappingSerializer() {
      return this._unwrapper != null;
   }

   public JavaType getReferredType() {
      return this._referredType;
   }

   public void serialize(T ref, JsonGenerator g, SerializerProvider provider) throws IOException {
      Object value = this._getReferencedIfPresent(ref);
      if (value == null) {
         if (this._unwrapper == null) {
            provider.defaultSerializeNull(g);
         }

      } else {
         JsonSerializer<Object> ser = this._valueSerializer;
         if (ser == null) {
            ser = this._findCachedSerializer(provider, value.getClass());
         }

         if (this._valueTypeSerializer != null) {
            ser.serializeWithType(value, g, provider, this._valueTypeSerializer);
         } else {
            ser.serialize(value, g, provider);
         }

      }
   }

   public void serializeWithType(T ref, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
      Object value = this._getReferencedIfPresent(ref);
      if (value == null) {
         if (this._unwrapper == null) {
            provider.defaultSerializeNull(g);
         }

      } else {
         JsonSerializer<Object> ser = this._valueSerializer;
         if (ser == null) {
            ser = this._findCachedSerializer(provider, value.getClass());
         }

         ser.serializeWithType(value, g, provider, typeSer);
      }
   }

   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      JsonSerializer<?> ser = this._valueSerializer;
      if (ser == null) {
         ser = this._findSerializer(visitor.getProvider(), this._referredType, this._property);
         if (this._unwrapper != null) {
            ser = ser.unwrappingSerializer(this._unwrapper);
         }
      }

      ser.acceptJsonFormatVisitor(visitor, this._referredType);
   }

   private final JsonSerializer<Object> _findCachedSerializer(SerializerProvider provider, Class<?> type) throws JsonMappingException {
      JsonSerializer<Object> ser = this._dynamicSerializers.serializerFor(type);
      if (ser == null) {
         ser = this._findSerializer(provider, type, this._property);
         if (this._unwrapper != null) {
            ser = ser.unwrappingSerializer(this._unwrapper);
         }

         this._dynamicSerializers = this._dynamicSerializers.newWith(type, ser);
      }

      return ser;
   }

   private final JsonSerializer<Object> _findSerializer(SerializerProvider provider, Class<?> type, BeanProperty prop) throws JsonMappingException {
      return provider.findValueSerializer(type, prop);
   }

   private final JsonSerializer<Object> _findSerializer(SerializerProvider provider, JavaType type, BeanProperty prop) throws JsonMappingException {
      return provider.findValueSerializer(type, prop);
   }

   static {
      MARKER_FOR_EMPTY = JsonInclude.Include.NON_EMPTY;
   }
}

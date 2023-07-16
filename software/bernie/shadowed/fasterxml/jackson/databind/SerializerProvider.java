package software.bernie.shadowed.fasterxml.jackson.databind;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonInclude;
import software.bernie.shadowed.fasterxml.jackson.annotation.ObjectIdGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.ContextAttributes;
import software.bernie.shadowed.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import software.bernie.shadowed.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.Annotated;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.ContextualSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.FilterProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.ResolvableSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.SerializerCache;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.SerializerFactory;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.impl.FailingSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.impl.ReadOnlyClassToSerializerMap;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.impl.TypeWrappedSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.impl.UnknownSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.impl.WritableObjectId;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.std.NullSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.type.TypeFactory;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;

public abstract class SerializerProvider extends DatabindContext {
   protected static final boolean CACHE_UNKNOWN_MAPPINGS = false;
   public static final JsonSerializer<Object> DEFAULT_NULL_KEY_SERIALIZER = new FailingSerializer("Null key for a Map not allowed in JSON (use a converting NullKeySerializer?)");
   protected static final JsonSerializer<Object> DEFAULT_UNKNOWN_SERIALIZER = new UnknownSerializer();
   protected final SerializationConfig _config;
   protected final Class<?> _serializationView;
   protected final SerializerFactory _serializerFactory;
   protected final SerializerCache _serializerCache;
   protected transient ContextAttributes _attributes;
   protected JsonSerializer<Object> _unknownTypeSerializer;
   protected JsonSerializer<Object> _keySerializer;
   protected JsonSerializer<Object> _nullValueSerializer;
   protected JsonSerializer<Object> _nullKeySerializer;
   protected final ReadOnlyClassToSerializerMap _knownSerializers;
   protected DateFormat _dateFormat;
   protected final boolean _stdNullValueSerializer;

   public SerializerProvider() {
      this._unknownTypeSerializer = DEFAULT_UNKNOWN_SERIALIZER;
      this._nullValueSerializer = NullSerializer.instance;
      this._nullKeySerializer = DEFAULT_NULL_KEY_SERIALIZER;
      this._config = null;
      this._serializerFactory = null;
      this._serializerCache = new SerializerCache();
      this._knownSerializers = null;
      this._serializationView = null;
      this._attributes = null;
      this._stdNullValueSerializer = true;
   }

   protected SerializerProvider(SerializerProvider src, SerializationConfig config, SerializerFactory f) {
      this._unknownTypeSerializer = DEFAULT_UNKNOWN_SERIALIZER;
      this._nullValueSerializer = NullSerializer.instance;
      this._nullKeySerializer = DEFAULT_NULL_KEY_SERIALIZER;
      this._serializerFactory = f;
      this._config = config;
      this._serializerCache = src._serializerCache;
      this._unknownTypeSerializer = src._unknownTypeSerializer;
      this._keySerializer = src._keySerializer;
      this._nullValueSerializer = src._nullValueSerializer;
      this._nullKeySerializer = src._nullKeySerializer;
      this._stdNullValueSerializer = this._nullValueSerializer == DEFAULT_NULL_KEY_SERIALIZER;
      this._serializationView = config.getActiveView();
      this._attributes = config.getAttributes();
      this._knownSerializers = this._serializerCache.getReadOnlyLookupMap();
   }

   protected SerializerProvider(SerializerProvider src) {
      this._unknownTypeSerializer = DEFAULT_UNKNOWN_SERIALIZER;
      this._nullValueSerializer = NullSerializer.instance;
      this._nullKeySerializer = DEFAULT_NULL_KEY_SERIALIZER;
      this._config = null;
      this._serializationView = null;
      this._serializerFactory = null;
      this._knownSerializers = null;
      this._serializerCache = new SerializerCache();
      this._unknownTypeSerializer = src._unknownTypeSerializer;
      this._keySerializer = src._keySerializer;
      this._nullValueSerializer = src._nullValueSerializer;
      this._nullKeySerializer = src._nullKeySerializer;
      this._stdNullValueSerializer = src._stdNullValueSerializer;
   }

   public void setDefaultKeySerializer(JsonSerializer<Object> ks) {
      if (ks == null) {
         throw new IllegalArgumentException("Cannot pass null JsonSerializer");
      } else {
         this._keySerializer = ks;
      }
   }

   public void setNullValueSerializer(JsonSerializer<Object> nvs) {
      if (nvs == null) {
         throw new IllegalArgumentException("Cannot pass null JsonSerializer");
      } else {
         this._nullValueSerializer = nvs;
      }
   }

   public void setNullKeySerializer(JsonSerializer<Object> nks) {
      if (nks == null) {
         throw new IllegalArgumentException("Cannot pass null JsonSerializer");
      } else {
         this._nullKeySerializer = nks;
      }
   }

   public final SerializationConfig getConfig() {
      return this._config;
   }

   public final AnnotationIntrospector getAnnotationIntrospector() {
      return this._config.getAnnotationIntrospector();
   }

   public final TypeFactory getTypeFactory() {
      return this._config.getTypeFactory();
   }

   public final Class<?> getActiveView() {
      return this._serializationView;
   }

   /** @deprecated */
   @Deprecated
   public final Class<?> getSerializationView() {
      return this._serializationView;
   }

   public final boolean canOverrideAccessModifiers() {
      return this._config.canOverrideAccessModifiers();
   }

   public final boolean isEnabled(MapperFeature feature) {
      return this._config.isEnabled(feature);
   }

   public final JsonFormat.Value getDefaultPropertyFormat(Class<?> baseType) {
      return this._config.getDefaultPropertyFormat(baseType);
   }

   public final JsonInclude.Value getDefaultPropertyInclusion(Class<?> baseType) {
      return this._config.getDefaultPropertyInclusion();
   }

   public Locale getLocale() {
      return this._config.getLocale();
   }

   public TimeZone getTimeZone() {
      return this._config.getTimeZone();
   }

   public Object getAttribute(Object key) {
      return this._attributes.getAttribute(key);
   }

   public SerializerProvider setAttribute(Object key, Object value) {
      this._attributes = this._attributes.withPerCallAttribute(key, value);
      return this;
   }

   public final boolean isEnabled(SerializationFeature feature) {
      return this._config.isEnabled(feature);
   }

   public final boolean hasSerializationFeatures(int featureMask) {
      return this._config.hasSerializationFeatures(featureMask);
   }

   public final FilterProvider getFilterProvider() {
      return this._config.getFilterProvider();
   }

   public JsonGenerator getGenerator() {
      return null;
   }

   public abstract WritableObjectId findObjectId(Object var1, ObjectIdGenerator<?> var2);

   public JsonSerializer<Object> findValueSerializer(Class<?> valueType, BeanProperty property) throws JsonMappingException {
      JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
      if (ser == null) {
         ser = this._serializerCache.untypedValueSerializer(valueType);
         if (ser == null) {
            ser = this._serializerCache.untypedValueSerializer(this._config.constructType(valueType));
            if (ser == null) {
               ser = this._createAndCacheUntypedSerializer(valueType);
               if (ser == null) {
                  ser = this.getUnknownTypeSerializer(valueType);
                  return ser;
               }
            }
         }
      }

      return this.handleSecondaryContextualization(ser, property);
   }

   public JsonSerializer<Object> findValueSerializer(JavaType valueType, BeanProperty property) throws JsonMappingException {
      if (valueType == null) {
         this.reportMappingProblem("Null passed for `valueType` of `findValueSerializer()`");
      }

      JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
      if (ser == null) {
         ser = this._serializerCache.untypedValueSerializer(valueType);
         if (ser == null) {
            ser = this._createAndCacheUntypedSerializer(valueType);
            if (ser == null) {
               ser = this.getUnknownTypeSerializer(valueType.getRawClass());
               return ser;
            }
         }
      }

      return this.handleSecondaryContextualization(ser, property);
   }

   public JsonSerializer<Object> findValueSerializer(Class<?> valueType) throws JsonMappingException {
      JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
      if (ser == null) {
         ser = this._serializerCache.untypedValueSerializer(valueType);
         if (ser == null) {
            ser = this._serializerCache.untypedValueSerializer(this._config.constructType(valueType));
            if (ser == null) {
               ser = this._createAndCacheUntypedSerializer(valueType);
               if (ser == null) {
                  ser = this.getUnknownTypeSerializer(valueType);
               }
            }
         }
      }

      return ser;
   }

   public JsonSerializer<Object> findValueSerializer(JavaType valueType) throws JsonMappingException {
      JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
      if (ser == null) {
         ser = this._serializerCache.untypedValueSerializer(valueType);
         if (ser == null) {
            ser = this._createAndCacheUntypedSerializer(valueType);
            if (ser == null) {
               ser = this.getUnknownTypeSerializer(valueType.getRawClass());
            }
         }
      }

      return ser;
   }

   public JsonSerializer<Object> findPrimaryPropertySerializer(JavaType valueType, BeanProperty property) throws JsonMappingException {
      JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
      if (ser == null) {
         ser = this._serializerCache.untypedValueSerializer(valueType);
         if (ser == null) {
            ser = this._createAndCacheUntypedSerializer(valueType);
            if (ser == null) {
               ser = this.getUnknownTypeSerializer(valueType.getRawClass());
               return ser;
            }
         }
      }

      return this.handlePrimaryContextualization(ser, property);
   }

   public JsonSerializer<Object> findPrimaryPropertySerializer(Class<?> valueType, BeanProperty property) throws JsonMappingException {
      JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
      if (ser == null) {
         ser = this._serializerCache.untypedValueSerializer(valueType);
         if (ser == null) {
            ser = this._serializerCache.untypedValueSerializer(this._config.constructType(valueType));
            if (ser == null) {
               ser = this._createAndCacheUntypedSerializer(valueType);
               if (ser == null) {
                  ser = this.getUnknownTypeSerializer(valueType);
                  return ser;
               }
            }
         }
      }

      return this.handlePrimaryContextualization(ser, property);
   }

   public JsonSerializer<Object> findTypedValueSerializer(Class<?> valueType, boolean cache, BeanProperty property) throws JsonMappingException {
      JsonSerializer<Object> ser = this._knownSerializers.typedValueSerializer(valueType);
      if (ser != null) {
         return ser;
      } else {
         ser = this._serializerCache.typedValueSerializer(valueType);
         if (ser != null) {
            return ser;
         } else {
            JsonSerializer<Object> ser = this.findValueSerializer(valueType, property);
            TypeSerializer typeSer = this._serializerFactory.createTypeSerializer(this._config, this._config.constructType(valueType));
            if (typeSer != null) {
               typeSer = typeSer.forProperty(property);
               ser = new TypeWrappedSerializer(typeSer, (JsonSerializer)ser);
            }

            if (cache) {
               this._serializerCache.addTypedSerializer((Class)valueType, (JsonSerializer)ser);
            }

            return (JsonSerializer)ser;
         }
      }
   }

   public JsonSerializer<Object> findTypedValueSerializer(JavaType valueType, boolean cache, BeanProperty property) throws JsonMappingException {
      JsonSerializer<Object> ser = this._knownSerializers.typedValueSerializer(valueType);
      if (ser != null) {
         return ser;
      } else {
         ser = this._serializerCache.typedValueSerializer(valueType);
         if (ser != null) {
            return ser;
         } else {
            JsonSerializer<Object> ser = this.findValueSerializer(valueType, property);
            TypeSerializer typeSer = this._serializerFactory.createTypeSerializer(this._config, valueType);
            if (typeSer != null) {
               typeSer = typeSer.forProperty(property);
               ser = new TypeWrappedSerializer(typeSer, (JsonSerializer)ser);
            }

            if (cache) {
               this._serializerCache.addTypedSerializer((JavaType)valueType, (JsonSerializer)ser);
            }

            return (JsonSerializer)ser;
         }
      }
   }

   public TypeSerializer findTypeSerializer(JavaType javaType) throws JsonMappingException {
      return this._serializerFactory.createTypeSerializer(this._config, javaType);
   }

   public JsonSerializer<Object> findKeySerializer(JavaType keyType, BeanProperty property) throws JsonMappingException {
      JsonSerializer<Object> ser = this._serializerFactory.createKeySerializer(this._config, keyType, this._keySerializer);
      return this._handleContextualResolvable(ser, property);
   }

   public JsonSerializer<Object> findKeySerializer(Class<?> rawKeyType, BeanProperty property) throws JsonMappingException {
      return this.findKeySerializer(this._config.constructType(rawKeyType), property);
   }

   public JsonSerializer<Object> getDefaultNullKeySerializer() {
      return this._nullKeySerializer;
   }

   public JsonSerializer<Object> getDefaultNullValueSerializer() {
      return this._nullValueSerializer;
   }

   public JsonSerializer<Object> findNullKeySerializer(JavaType serializationType, BeanProperty property) throws JsonMappingException {
      return this._nullKeySerializer;
   }

   public JsonSerializer<Object> findNullValueSerializer(BeanProperty property) throws JsonMappingException {
      return this._nullValueSerializer;
   }

   public JsonSerializer<Object> getUnknownTypeSerializer(Class<?> unknownType) {
      return (JsonSerializer)(unknownType == Object.class ? this._unknownTypeSerializer : new UnknownSerializer(unknownType));
   }

   public boolean isUnknownTypeSerializer(JsonSerializer<?> ser) {
      if (ser != this._unknownTypeSerializer && ser != null) {
         return this.isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS) && ser.getClass() == UnknownSerializer.class;
      } else {
         return true;
      }
   }

   public abstract JsonSerializer<Object> serializerInstance(Annotated var1, Object var2) throws JsonMappingException;

   public abstract Object includeFilterInstance(BeanPropertyDefinition var1, Class<?> var2) throws JsonMappingException;

   public abstract boolean includeFilterSuppressNulls(Object var1) throws JsonMappingException;

   public JsonSerializer<?> handlePrimaryContextualization(JsonSerializer<?> ser, BeanProperty property) throws JsonMappingException {
      if (ser != null && ser instanceof ContextualSerializer) {
         ser = ((ContextualSerializer)ser).createContextual(this, property);
      }

      return ser;
   }

   public JsonSerializer<?> handleSecondaryContextualization(JsonSerializer<?> ser, BeanProperty property) throws JsonMappingException {
      if (ser != null && ser instanceof ContextualSerializer) {
         ser = ((ContextualSerializer)ser).createContextual(this, property);
      }

      return ser;
   }

   public final void defaultSerializeValue(Object value, JsonGenerator gen) throws IOException {
      if (value == null) {
         if (this._stdNullValueSerializer) {
            gen.writeNull();
         } else {
            this._nullValueSerializer.serialize((Object)null, gen, this);
         }
      } else {
         Class<?> cls = value.getClass();
         this.findTypedValueSerializer((Class)cls, true, (BeanProperty)null).serialize(value, gen, this);
      }

   }

   public final void defaultSerializeField(String fieldName, Object value, JsonGenerator gen) throws IOException {
      gen.writeFieldName(fieldName);
      if (value == null) {
         if (this._stdNullValueSerializer) {
            gen.writeNull();
         } else {
            this._nullValueSerializer.serialize((Object)null, gen, this);
         }
      } else {
         Class<?> cls = value.getClass();
         this.findTypedValueSerializer((Class)cls, true, (BeanProperty)null).serialize(value, gen, this);
      }

   }

   public final void defaultSerializeDateValue(long timestamp, JsonGenerator gen) throws IOException {
      if (this.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)) {
         gen.writeNumber(timestamp);
      } else {
         gen.writeString(this._dateFormat().format(new Date(timestamp)));
      }

   }

   public final void defaultSerializeDateValue(Date date, JsonGenerator gen) throws IOException {
      if (this.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)) {
         gen.writeNumber(date.getTime());
      } else {
         gen.writeString(this._dateFormat().format(date));
      }

   }

   public void defaultSerializeDateKey(long timestamp, JsonGenerator gen) throws IOException {
      if (this.isEnabled(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)) {
         gen.writeFieldName(String.valueOf(timestamp));
      } else {
         gen.writeFieldName(this._dateFormat().format(new Date(timestamp)));
      }

   }

   public void defaultSerializeDateKey(Date date, JsonGenerator gen) throws IOException {
      if (this.isEnabled(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)) {
         gen.writeFieldName(String.valueOf(date.getTime()));
      } else {
         gen.writeFieldName(this._dateFormat().format(date));
      }

   }

   public final void defaultSerializeNull(JsonGenerator gen) throws IOException {
      if (this._stdNullValueSerializer) {
         gen.writeNull();
      } else {
         this._nullValueSerializer.serialize((Object)null, gen, this);
      }

   }

   public void reportMappingProblem(String message, Object... args) throws JsonMappingException {
      throw this.mappingException(message, args);
   }

   public <T> T reportBadTypeDefinition(BeanDescription bean, String msg, Object... msgArgs) throws JsonMappingException {
      String beanDesc = "N/A";
      if (bean != null) {
         beanDesc = ClassUtil.nameOf(bean.getBeanClass());
      }

      msg = String.format("Invalid type definition for type %s: %s", beanDesc, this._format(msg, msgArgs));
      throw InvalidDefinitionException.from((JsonGenerator)this.getGenerator(), msg, bean, (BeanPropertyDefinition)null);
   }

   public <T> T reportBadPropertyDefinition(BeanDescription bean, BeanPropertyDefinition prop, String message, Object... msgArgs) throws JsonMappingException {
      message = this._format(message, msgArgs);
      String propName = "N/A";
      if (prop != null) {
         propName = this._quotedString(prop.getName());
      }

      String beanDesc = "N/A";
      if (bean != null) {
         beanDesc = ClassUtil.nameOf(bean.getBeanClass());
      }

      message = String.format("Invalid definition for property %s (of type %s): %s", propName, beanDesc, message);
      throw InvalidDefinitionException.from(this.getGenerator(), message, bean, prop);
   }

   public <T> T reportBadDefinition(JavaType type, String msg) throws JsonMappingException {
      throw InvalidDefinitionException.from(this.getGenerator(), msg, type);
   }

   public <T> T reportBadDefinition(JavaType type, String msg, Throwable cause) throws JsonMappingException {
      InvalidDefinitionException e = InvalidDefinitionException.from(this.getGenerator(), msg, type);
      e.initCause(cause);
      throw e;
   }

   public <T> T reportBadDefinition(Class<?> raw, String msg, Throwable cause) throws JsonMappingException {
      InvalidDefinitionException e = InvalidDefinitionException.from(this.getGenerator(), msg, this.constructType(raw));
      e.initCause(cause);
      throw e;
   }

   public void reportMappingProblem(Throwable t, String message, Object... msgArgs) throws JsonMappingException {
      message = this._format(message, msgArgs);
      throw JsonMappingException.from(this.getGenerator(), message, t);
   }

   public JsonMappingException invalidTypeIdException(JavaType baseType, String typeId, String extraDesc) {
      String msg = String.format("Could not resolve type id '%s' as a subtype of %s", typeId, baseType);
      return InvalidTypeIdException.from((JsonParser)null, this._colonConcat(msg, extraDesc), baseType, typeId);
   }

   /** @deprecated */
   @Deprecated
   public JsonMappingException mappingException(String message, Object... msgArgs) {
      return JsonMappingException.from(this.getGenerator(), this._format(message, msgArgs));
   }

   /** @deprecated */
   @Deprecated
   protected JsonMappingException mappingException(Throwable t, String message, Object... msgArgs) {
      return JsonMappingException.from(this.getGenerator(), this._format(message, msgArgs), t);
   }

   protected void _reportIncompatibleRootType(Object value, JavaType rootType) throws IOException {
      if (rootType.isPrimitive()) {
         Class<?> wrapperType = ClassUtil.wrapperType(rootType.getRawClass());
         if (wrapperType.isAssignableFrom(value.getClass())) {
            return;
         }
      }

      this.reportBadDefinition(rootType, String.format("Incompatible types: declared root type (%s) vs %s", rootType, ClassUtil.classNameOf(value)));
   }

   protected JsonSerializer<Object> _findExplicitUntypedSerializer(Class<?> runtimeType) throws JsonMappingException {
      JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(runtimeType);
      if (ser == null) {
         ser = this._serializerCache.untypedValueSerializer(runtimeType);
         if (ser == null) {
            ser = this._createAndCacheUntypedSerializer(runtimeType);
         }
      }

      return this.isUnknownTypeSerializer(ser) ? null : ser;
   }

   protected JsonSerializer<Object> _createAndCacheUntypedSerializer(Class<?> rawType) throws JsonMappingException {
      JavaType fullType = this._config.constructType(rawType);

      JsonSerializer ser;
      try {
         ser = this._createUntypedSerializer(fullType);
      } catch (IllegalArgumentException var5) {
         ser = null;
         this.reportMappingProblem(var5, var5.getMessage());
      }

      if (ser != null) {
         this._serializerCache.addAndResolveNonTypedSerializer(rawType, fullType, ser, this);
      }

      return ser;
   }

   protected JsonSerializer<Object> _createAndCacheUntypedSerializer(JavaType type) throws JsonMappingException {
      JsonSerializer ser;
      try {
         ser = this._createUntypedSerializer(type);
      } catch (IllegalArgumentException var4) {
         ser = null;
         this.reportMappingProblem(var4, var4.getMessage());
      }

      if (ser != null) {
         this._serializerCache.addAndResolveNonTypedSerializer(type, ser, this);
      }

      return ser;
   }

   protected JsonSerializer<Object> _createUntypedSerializer(JavaType type) throws JsonMappingException {
      synchronized(this._serializerCache) {
         return this._serializerFactory.createSerializer(this, type);
      }
   }

   protected JsonSerializer<Object> _handleContextualResolvable(JsonSerializer<?> ser, BeanProperty property) throws JsonMappingException {
      if (ser instanceof ResolvableSerializer) {
         ((ResolvableSerializer)ser).resolve(this);
      }

      return this.handleSecondaryContextualization(ser, property);
   }

   protected JsonSerializer<Object> _handleResolvable(JsonSerializer<?> ser) throws JsonMappingException {
      if (ser instanceof ResolvableSerializer) {
         ((ResolvableSerializer)ser).resolve(this);
      }

      return ser;
   }

   protected final DateFormat _dateFormat() {
      if (this._dateFormat != null) {
         return this._dateFormat;
      } else {
         DateFormat df = this._config.getDateFormat();
         this._dateFormat = df = (DateFormat)df.clone();
         return df;
      }
   }
}

package software.bernie.shadowed.fasterxml.jackson.databind.deser.std;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.MapperFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ContextualDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.SettableBeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ValueInstantiator;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;
import software.bernie.shadowed.fasterxml.jackson.databind.util.CompactStringObjectMap;
import software.bernie.shadowed.fasterxml.jackson.databind.util.EnumResolver;

@JacksonStdImpl
public class EnumDeserializer extends StdScalarDeserializer<Object> implements ContextualDeserializer {
   private static final long serialVersionUID = 1L;
   protected Object[] _enumsByIndex;
   private final Enum<?> _enumDefaultValue;
   protected final CompactStringObjectMap _lookupByName;
   protected CompactStringObjectMap _lookupByToString;
   protected final Boolean _caseInsensitive;

   public EnumDeserializer(EnumResolver byNameResolver, Boolean caseInsensitive) {
      super(byNameResolver.getEnumClass());
      this._lookupByName = byNameResolver.constructLookup();
      this._enumsByIndex = byNameResolver.getRawEnums();
      this._enumDefaultValue = byNameResolver.getDefaultValue();
      this._caseInsensitive = caseInsensitive;
   }

   protected EnumDeserializer(EnumDeserializer base, Boolean caseInsensitive) {
      super((StdScalarDeserializer)base);
      this._lookupByName = base._lookupByName;
      this._enumsByIndex = base._enumsByIndex;
      this._enumDefaultValue = base._enumDefaultValue;
      this._caseInsensitive = caseInsensitive;
   }

   /** @deprecated */
   @Deprecated
   public EnumDeserializer(EnumResolver byNameResolver) {
      this((EnumResolver)byNameResolver, (Boolean)null);
   }

   /** @deprecated */
   @Deprecated
   public static JsonDeserializer<?> deserializerForCreator(DeserializationConfig config, Class<?> enumClass, AnnotatedMethod factory) {
      return deserializerForCreator(config, enumClass, factory, (ValueInstantiator)null, (SettableBeanProperty[])null);
   }

   public static JsonDeserializer<?> deserializerForCreator(DeserializationConfig config, Class<?> enumClass, AnnotatedMethod factory, ValueInstantiator valueInstantiator, SettableBeanProperty[] creatorProps) {
      if (config.canOverrideAccessModifiers()) {
         ClassUtil.checkAndFixAccess(factory.getMember(), config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
      }

      return new FactoryBasedEnumDeserializer(enumClass, factory, factory.getParameterType(0), valueInstantiator, creatorProps);
   }

   public static JsonDeserializer<?> deserializerForNoArgsCreator(DeserializationConfig config, Class<?> enumClass, AnnotatedMethod factory) {
      if (config.canOverrideAccessModifiers()) {
         ClassUtil.checkAndFixAccess(factory.getMember(), config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
      }

      return new FactoryBasedEnumDeserializer(enumClass, factory);
   }

   public EnumDeserializer withResolved(Boolean caseInsensitive) {
      return this._caseInsensitive == caseInsensitive ? this : new EnumDeserializer(this, caseInsensitive);
   }

   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
      Boolean caseInsensitive = this.findFormatFeature(ctxt, property, this.handledType(), JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
      if (caseInsensitive == null) {
         caseInsensitive = this._caseInsensitive;
      }

      return this.withResolved(caseInsensitive);
   }

   public boolean isCachable() {
      return true;
   }

   public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonToken curr = p.getCurrentToken();
      if (curr != JsonToken.VALUE_STRING && curr != JsonToken.FIELD_NAME) {
         if (curr == JsonToken.VALUE_NUMBER_INT) {
            int index = p.getIntValue();
            if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS)) {
               return ctxt.handleWeirdNumberValue(this._enumClass(), index, "not allowed to deserialize Enum value out of number: disable DeserializationConfig.DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS to allow");
            } else if (index >= 0 && index < this._enumsByIndex.length) {
               return this._enumsByIndex[index];
            } else if (this._enumDefaultValue != null && ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)) {
               return this._enumDefaultValue;
            } else {
               return !ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL) ? ctxt.handleWeirdNumberValue(this._enumClass(), index, "index value outside legal index range [0..%s]", this._enumsByIndex.length - 1) : null;
            }
         } else {
            return this._deserializeOther(p, ctxt);
         }
      } else {
         CompactStringObjectMap lookup = ctxt.isEnabled(DeserializationFeature.READ_ENUMS_USING_TO_STRING) ? this._getToStringLookup(ctxt) : this._lookupByName;
         String name = p.getText();
         Object result = lookup.find(name);
         return result == null ? this._deserializeAltString(p, ctxt, lookup, name) : result;
      }
   }

   private final Object _deserializeAltString(JsonParser p, DeserializationContext ctxt, CompactStringObjectMap lookup, String name) throws IOException {
      name = name.trim();
      if (name.length() == 0) {
         if (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
            return this.getEmptyValue(ctxt);
         }
      } else if (Boolean.TRUE.equals(this._caseInsensitive)) {
         Object match = lookup.findCaseInsensitive(name);
         if (match != null) {
            return match;
         }
      } else if (!ctxt.isEnabled(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS)) {
         char c = name.charAt(0);
         if (c >= '0' && c <= '9') {
            try {
               int index = Integer.parseInt(name);
               if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
                  return ctxt.handleWeirdStringValue(this._enumClass(), name, "value looks like quoted Enum index, but `MapperFeature.ALLOW_COERCION_OF_SCALARS` prevents use");
               }

               if (index >= 0 && index < this._enumsByIndex.length) {
                  return this._enumsByIndex[index];
               }
            } catch (NumberFormatException var7) {
            }
         }
      }

      if (this._enumDefaultValue != null && ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)) {
         return this._enumDefaultValue;
      } else {
         return !ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL) ? ctxt.handleWeirdStringValue(this._enumClass(), name, "value not one of declared Enum instance names: %s", lookup.keys()) : null;
      }
   }

   protected Object _deserializeOther(JsonParser p, DeserializationContext ctxt) throws IOException {
      return p.hasToken(JsonToken.START_ARRAY) ? this._deserializeFromArray(p, ctxt) : ctxt.handleUnexpectedToken(this._enumClass(), p);
   }

   protected Class<?> _enumClass() {
      return this.handledType();
   }

   protected CompactStringObjectMap _getToStringLookup(DeserializationContext ctxt) {
      CompactStringObjectMap lookup = this._lookupByToString;
      if (lookup == null) {
         synchronized(this) {
            lookup = EnumResolver.constructUnsafeUsingToString(this._enumClass(), ctxt.getAnnotationIntrospector()).constructLookup();
         }

         this._lookupByToString = lookup;
      }

      return lookup;
   }
}

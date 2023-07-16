package software.bernie.shadowed.fasterxml.jackson.databind.deser.std;

import java.io.IOException;
import java.util.EnumMap;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.KeyDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.MapperFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ContextualDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.NullValueProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.SettableBeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ValueInstantiator;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;

public class EnumMapDeserializer extends ContainerDeserializerBase<EnumMap<?, ?>> implements ContextualDeserializer, ResolvableDeserializer {
   private static final long serialVersionUID = 1L;
   protected final Class<?> _enumClass;
   protected KeyDeserializer _keyDeserializer;
   protected JsonDeserializer<Object> _valueDeserializer;
   protected final TypeDeserializer _valueTypeDeserializer;
   protected final ValueInstantiator _valueInstantiator;
   protected JsonDeserializer<Object> _delegateDeserializer;
   protected PropertyBasedCreator _propertyBasedCreator;

   public EnumMapDeserializer(JavaType mapType, ValueInstantiator valueInst, KeyDeserializer keyDeser, JsonDeserializer<?> valueDeser, TypeDeserializer vtd, NullValueProvider nuller) {
      super((JavaType)mapType, nuller, (Boolean)null);
      this._enumClass = mapType.getKeyType().getRawClass();
      this._keyDeserializer = keyDeser;
      this._valueDeserializer = valueDeser;
      this._valueTypeDeserializer = vtd;
      this._valueInstantiator = valueInst;
   }

   protected EnumMapDeserializer(EnumMapDeserializer base, KeyDeserializer keyDeser, JsonDeserializer<?> valueDeser, TypeDeserializer vtd, NullValueProvider nuller) {
      super((ContainerDeserializerBase)base, nuller, base._unwrapSingle);
      this._enumClass = base._enumClass;
      this._keyDeserializer = keyDeser;
      this._valueDeserializer = valueDeser;
      this._valueTypeDeserializer = vtd;
      this._valueInstantiator = base._valueInstantiator;
      this._delegateDeserializer = base._delegateDeserializer;
      this._propertyBasedCreator = base._propertyBasedCreator;
   }

   /** @deprecated */
   @Deprecated
   public EnumMapDeserializer(JavaType mapType, KeyDeserializer keyDeser, JsonDeserializer<?> valueDeser, TypeDeserializer vtd) {
      this(mapType, (ValueInstantiator)null, keyDeser, valueDeser, vtd, (NullValueProvider)null);
   }

   public EnumMapDeserializer withResolved(KeyDeserializer keyDeserializer, JsonDeserializer<?> valueDeserializer, TypeDeserializer valueTypeDeser, NullValueProvider nuller) {
      return keyDeserializer == this._keyDeserializer && nuller == this._nullProvider && valueDeserializer == this._valueDeserializer && valueTypeDeser == this._valueTypeDeserializer ? this : new EnumMapDeserializer(this, keyDeserializer, valueDeserializer, valueTypeDeser, nuller);
   }

   public void resolve(DeserializationContext ctxt) throws JsonMappingException {
      if (this._valueInstantiator != null) {
         JavaType delegateType;
         if (this._valueInstantiator.canCreateUsingDelegate()) {
            delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
            if (delegateType == null) {
               ctxt.reportBadDefinition(this._containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'", this._containerType, this._valueInstantiator.getClass().getName()));
            }

            this._delegateDeserializer = this.findDeserializer(ctxt, delegateType, (BeanProperty)null);
         } else if (this._valueInstantiator.canCreateUsingArrayDelegate()) {
            delegateType = this._valueInstantiator.getArrayDelegateType(ctxt.getConfig());
            if (delegateType == null) {
               ctxt.reportBadDefinition(this._containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingArrayDelegate()', but null for 'getArrayDelegateType()'", this._containerType, this._valueInstantiator.getClass().getName()));
            }

            this._delegateDeserializer = this.findDeserializer(ctxt, delegateType, (BeanProperty)null);
         } else if (this._valueInstantiator.canCreateFromObjectWith()) {
            SettableBeanProperty[] creatorProps = this._valueInstantiator.getFromObjectArguments(ctxt.getConfig());
            this._propertyBasedCreator = PropertyBasedCreator.construct(ctxt, this._valueInstantiator, creatorProps, ctxt.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
         }
      }

   }

   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
      KeyDeserializer keyDeser = this._keyDeserializer;
      if (keyDeser == null) {
         keyDeser = ctxt.findKeyDeserializer(this._containerType.getKeyType(), property);
      }

      JsonDeserializer<?> valueDeser = this._valueDeserializer;
      JavaType vt = this._containerType.getContentType();
      if (valueDeser == null) {
         valueDeser = ctxt.findContextualValueDeserializer(vt, property);
      } else {
         valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, vt);
      }

      TypeDeserializer vtd = this._valueTypeDeserializer;
      if (vtd != null) {
         vtd = vtd.forProperty(property);
      }

      return this.withResolved(keyDeser, valueDeser, vtd, this.findContentNullProvider(ctxt, property, valueDeser));
   }

   public boolean isCachable() {
      return this._valueDeserializer == null && this._keyDeserializer == null && this._valueTypeDeserializer == null;
   }

   public JsonDeserializer<Object> getContentDeserializer() {
      return this._valueDeserializer;
   }

   public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
      return this.constructMap(ctxt);
   }

   public EnumMap<?, ?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (this._propertyBasedCreator != null) {
         return this._deserializeUsingProperties(p, ctxt);
      } else if (this._delegateDeserializer != null) {
         return (EnumMap)this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
      } else {
         JsonToken t = p.getCurrentToken();
         if (t != JsonToken.START_OBJECT && t != JsonToken.FIELD_NAME && t != JsonToken.END_OBJECT) {
            return t == JsonToken.VALUE_STRING ? (EnumMap)this._valueInstantiator.createFromString(ctxt, p.getText()) : (EnumMap)this._deserializeFromEmpty(p, ctxt);
         } else {
            EnumMap result = this.constructMap(ctxt);
            return this.deserialize(p, ctxt, result);
         }
      }
   }

   public EnumMap<?, ?> deserialize(JsonParser p, DeserializationContext ctxt, EnumMap result) throws IOException {
      p.setCurrentValue(result);
      JsonDeserializer<Object> valueDes = this._valueDeserializer;
      TypeDeserializer typeDeser = this._valueTypeDeserializer;

      while(true) {
         String keyName;
         while((keyName = p.nextFieldName()) != null) {
            Enum<?> key = (Enum)this._keyDeserializer.deserializeKey(keyName, ctxt);
            if (key == null) {
               if (!ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
                  return (EnumMap)ctxt.handleWeirdStringValue(this._enumClass, keyName, "value not one of declared Enum instance names for %s", this._containerType.getKeyType());
               }

               p.nextToken();
               p.skipChildren();
            } else {
               JsonToken t = p.nextToken();

               Object value;
               try {
                  if (t == JsonToken.VALUE_NULL) {
                     if (this._skipNullValues) {
                        continue;
                     }

                     value = this._nullProvider.getNullValue(ctxt);
                  } else if (typeDeser == null) {
                     value = valueDes.deserialize(p, ctxt);
                  } else {
                     value = valueDes.deserializeWithType(p, ctxt, typeDeser);
                  }
               } catch (Exception var11) {
                  return (EnumMap)this.wrapAndThrow(var11, result, keyName);
               }

               result.put(key, value);
            }
         }

         return result;
      }
   }

   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
      return typeDeserializer.deserializeTypedFromObject(p, ctxt);
   }

   protected EnumMap<?, ?> constructMap(DeserializationContext ctxt) throws JsonMappingException {
      if (this._valueInstantiator == null) {
         return new EnumMap(this._enumClass);
      } else {
         try {
            return !this._valueInstantiator.canCreateUsingDefault() ? (EnumMap)ctxt.handleMissingInstantiator(this.handledType(), this.getValueInstantiator(), (JsonParser)null, "no default constructor found") : (EnumMap)this._valueInstantiator.createUsingDefault(ctxt);
         } catch (IOException var3) {
            return (EnumMap)ClassUtil.throwAsMappingException(ctxt, var3);
         }
      }
   }

   public EnumMap<?, ?> _deserializeUsingProperties(JsonParser p, DeserializationContext ctxt) throws IOException {
      PropertyBasedCreator creator = this._propertyBasedCreator;
      PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, (ObjectIdReader)null);
      String keyName;
      if (p.isExpectedStartObjectToken()) {
         keyName = p.nextFieldName();
      } else if (p.hasToken(JsonToken.FIELD_NAME)) {
         keyName = p.getCurrentName();
      } else {
         keyName = null;
      }

      for(; keyName != null; keyName = p.nextFieldName()) {
         JsonToken t = p.nextToken();
         SettableBeanProperty prop = creator.findCreatorProperty(keyName);
         if (prop != null) {
            if (buffer.assignParameter(prop, prop.deserialize(p, ctxt))) {
               EnumMap result;
               try {
                  result = (EnumMap)creator.build(ctxt, buffer);
               } catch (Exception var11) {
                  return (EnumMap)this.wrapAndThrow(var11, this._containerType.getRawClass(), keyName);
               }

               return this.deserialize(p, ctxt, result);
            }
         } else {
            Enum<?> key = (Enum)this._keyDeserializer.deserializeKey(keyName, ctxt);
            if (key == null) {
               if (!ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
                  return (EnumMap)ctxt.handleWeirdStringValue(this._enumClass, keyName, "value not one of declared Enum instance names for %s", this._containerType.getKeyType());
               }

               p.nextToken();
               p.skipChildren();
            } else {
               Object value;
               try {
                  if (t == JsonToken.VALUE_NULL) {
                     if (this._skipNullValues) {
                        continue;
                     }

                     value = this._nullProvider.getNullValue(ctxt);
                  } else if (this._valueTypeDeserializer == null) {
                     value = this._valueDeserializer.deserialize(p, ctxt);
                  } else {
                     value = this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
                  }
               } catch (Exception var13) {
                  this.wrapAndThrow(var13, this._containerType.getRawClass(), keyName);
                  return null;
               }

               buffer.bufferMapProperty(key, value);
            }
         }
      }

      try {
         return (EnumMap)creator.build(ctxt, buffer);
      } catch (Exception var12) {
         this.wrapAndThrow(var12, this._containerType.getRawClass(), keyName);
         return null;
      }
   }
}

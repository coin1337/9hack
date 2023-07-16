package software.bernie.shadowed.fasterxml.jackson.databind.deser.std;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonIgnoreProperties;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.AnnotationIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.KeyDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.MapperFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ContextualDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ContextualKeyDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.NullValueProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.SettableBeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ValueInstantiator;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMember;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ArrayBuilders;

@JacksonStdImpl
public class MapDeserializer extends ContainerDeserializerBase<Map<Object, Object>> implements ContextualDeserializer, ResolvableDeserializer {
   private static final long serialVersionUID = 1L;
   protected final KeyDeserializer _keyDeserializer;
   protected boolean _standardStringKey;
   protected final JsonDeserializer<Object> _valueDeserializer;
   protected final TypeDeserializer _valueTypeDeserializer;
   protected final ValueInstantiator _valueInstantiator;
   protected JsonDeserializer<Object> _delegateDeserializer;
   protected PropertyBasedCreator _propertyBasedCreator;
   protected final boolean _hasDefaultCreator;
   protected Set<String> _ignorableProperties;

   public MapDeserializer(JavaType mapType, ValueInstantiator valueInstantiator, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser) {
      super((JavaType)mapType, (NullValueProvider)null, (Boolean)null);
      this._keyDeserializer = keyDeser;
      this._valueDeserializer = valueDeser;
      this._valueTypeDeserializer = valueTypeDeser;
      this._valueInstantiator = valueInstantiator;
      this._hasDefaultCreator = valueInstantiator.canCreateUsingDefault();
      this._delegateDeserializer = null;
      this._propertyBasedCreator = null;
      this._standardStringKey = this._isStdKeyDeser(mapType, keyDeser);
   }

   protected MapDeserializer(MapDeserializer src) {
      super((ContainerDeserializerBase)src);
      this._keyDeserializer = src._keyDeserializer;
      this._valueDeserializer = src._valueDeserializer;
      this._valueTypeDeserializer = src._valueTypeDeserializer;
      this._valueInstantiator = src._valueInstantiator;
      this._propertyBasedCreator = src._propertyBasedCreator;
      this._delegateDeserializer = src._delegateDeserializer;
      this._hasDefaultCreator = src._hasDefaultCreator;
      this._ignorableProperties = src._ignorableProperties;
      this._standardStringKey = src._standardStringKey;
   }

   protected MapDeserializer(MapDeserializer src, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, NullValueProvider nuller, Set<String> ignorable) {
      super((ContainerDeserializerBase)src, nuller, src._unwrapSingle);
      this._keyDeserializer = keyDeser;
      this._valueDeserializer = valueDeser;
      this._valueTypeDeserializer = valueTypeDeser;
      this._valueInstantiator = src._valueInstantiator;
      this._propertyBasedCreator = src._propertyBasedCreator;
      this._delegateDeserializer = src._delegateDeserializer;
      this._hasDefaultCreator = src._hasDefaultCreator;
      this._ignorableProperties = ignorable;
      this._standardStringKey = this._isStdKeyDeser(this._containerType, keyDeser);
   }

   protected MapDeserializer withResolved(KeyDeserializer keyDeser, TypeDeserializer valueTypeDeser, JsonDeserializer<?> valueDeser, NullValueProvider nuller, Set<String> ignorable) {
      return this._keyDeserializer == keyDeser && this._valueDeserializer == valueDeser && this._valueTypeDeserializer == valueTypeDeser && this._nullProvider == nuller && this._ignorableProperties == ignorable ? this : new MapDeserializer(this, keyDeser, valueDeser, valueTypeDeser, nuller, ignorable);
   }

   protected final boolean _isStdKeyDeser(JavaType mapType, KeyDeserializer keyDeser) {
      if (keyDeser == null) {
         return true;
      } else {
         JavaType keyType = mapType.getKeyType();
         if (keyType == null) {
            return true;
         } else {
            Class<?> rawKeyType = keyType.getRawClass();
            return (rawKeyType == String.class || rawKeyType == Object.class) && this.isDefaultKeyDeserializer(keyDeser);
         }
      }
   }

   public void setIgnorableProperties(String[] ignorable) {
      this._ignorableProperties = ignorable != null && ignorable.length != 0 ? ArrayBuilders.arrayToSet(ignorable) : null;
   }

   public void setIgnorableProperties(Set<String> ignorable) {
      this._ignorableProperties = ignorable != null && ignorable.size() != 0 ? ignorable : null;
   }

   public void resolve(DeserializationContext ctxt) throws JsonMappingException {
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
      }

      if (this._valueInstantiator.canCreateFromObjectWith()) {
         SettableBeanProperty[] creatorProps = this._valueInstantiator.getFromObjectArguments(ctxt.getConfig());
         this._propertyBasedCreator = PropertyBasedCreator.construct(ctxt, this._valueInstantiator, creatorProps, ctxt.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
      }

      this._standardStringKey = this._isStdKeyDeser(this._containerType, this._keyDeserializer);
   }

   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
      KeyDeserializer keyDeser = this._keyDeserializer;
      if (keyDeser == null) {
         keyDeser = ctxt.findKeyDeserializer(this._containerType.getKeyType(), property);
      } else if (keyDeser instanceof ContextualKeyDeserializer) {
         keyDeser = ((ContextualKeyDeserializer)keyDeser).createContextual(ctxt, property);
      }

      JsonDeserializer<?> valueDeser = this._valueDeserializer;
      if (property != null) {
         valueDeser = this.findConvertingContentDeserializer(ctxt, property, valueDeser);
      }

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

      Set<String> ignored = this._ignorableProperties;
      AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
      if (_neitherNull(intr, property)) {
         AnnotatedMember member = property.getMember();
         if (member != null) {
            JsonIgnoreProperties.Value ignorals = intr.findPropertyIgnorals(member);
            if (ignorals != null) {
               Set<String> ignoresToAdd = ignorals.findIgnoredForDeserialization();
               if (!ignoresToAdd.isEmpty()) {
                  ignored = ignored == null ? new HashSet() : new HashSet((Collection)ignored);
                  Iterator i$ = ignoresToAdd.iterator();

                  while(i$.hasNext()) {
                     String str = (String)i$.next();
                     ((Set)ignored).add(str);
                  }
               }
            }
         }
      }

      return this.withResolved(keyDeser, vtd, valueDeser, this.findContentNullProvider(ctxt, property, valueDeser), (Set)ignored);
   }

   public JsonDeserializer<Object> getContentDeserializer() {
      return this._valueDeserializer;
   }

   public ValueInstantiator getValueInstantiator() {
      return this._valueInstantiator;
   }

   public boolean isCachable() {
      return this._valueDeserializer == null && this._keyDeserializer == null && this._valueTypeDeserializer == null && this._ignorableProperties == null;
   }

   public Map<Object, Object> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (this._propertyBasedCreator != null) {
         return this._deserializeUsingCreator(p, ctxt);
      } else if (this._delegateDeserializer != null) {
         return (Map)this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
      } else if (!this._hasDefaultCreator) {
         return (Map)ctxt.handleMissingInstantiator(this.getMapClass(), this.getValueInstantiator(), p, "no default constructor found");
      } else {
         JsonToken t = p.getCurrentToken();
         if (t != JsonToken.START_OBJECT && t != JsonToken.FIELD_NAME && t != JsonToken.END_OBJECT) {
            return t == JsonToken.VALUE_STRING ? (Map)this._valueInstantiator.createFromString(ctxt, p.getText()) : (Map)this._deserializeFromEmpty(p, ctxt);
         } else {
            Map<Object, Object> result = (Map)this._valueInstantiator.createUsingDefault(ctxt);
            if (this._standardStringKey) {
               this._readAndBindStringKeyMap(p, ctxt, result);
               return result;
            } else {
               this._readAndBind(p, ctxt, result);
               return result;
            }
         }
      }
   }

   public Map<Object, Object> deserialize(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result) throws IOException {
      p.setCurrentValue(result);
      JsonToken t = p.getCurrentToken();
      if (t != JsonToken.START_OBJECT && t != JsonToken.FIELD_NAME) {
         return (Map)ctxt.handleUnexpectedToken(this.getMapClass(), p);
      } else if (this._standardStringKey) {
         this._readAndUpdateStringKeyMap(p, ctxt, result);
         return result;
      } else {
         this._readAndUpdate(p, ctxt, result);
         return result;
      }
   }

   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
      return typeDeserializer.deserializeTypedFromObject(p, ctxt);
   }

   public final Class<?> getMapClass() {
      return this._containerType.getRawClass();
   }

   public JavaType getValueType() {
      return this._containerType;
   }

   protected final void _readAndBind(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result) throws IOException {
      KeyDeserializer keyDes = this._keyDeserializer;
      JsonDeserializer<Object> valueDes = this._valueDeserializer;
      TypeDeserializer typeDeser = this._valueTypeDeserializer;
      MapDeserializer.MapReferringAccumulator referringAccumulator = null;
      boolean useObjectId = valueDes.getObjectIdReader() != null;
      if (useObjectId) {
         referringAccumulator = new MapDeserializer.MapReferringAccumulator(this._containerType.getContentType().getRawClass(), result);
      }

      String keyStr;
      if (p.isExpectedStartObjectToken()) {
         keyStr = p.nextFieldName();
      } else {
         JsonToken t = p.getCurrentToken();
         if (t == JsonToken.END_OBJECT) {
            return;
         }

         if (t != JsonToken.FIELD_NAME) {
            ctxt.reportWrongTokenException((JsonDeserializer)this, JsonToken.FIELD_NAME, (String)null);
         }

         keyStr = p.getCurrentName();
      }

      for(; keyStr != null; keyStr = p.nextFieldName()) {
         Object key = keyDes.deserializeKey(keyStr, ctxt);
         JsonToken t = p.nextToken();
         if (this._ignorableProperties != null && this._ignorableProperties.contains(keyStr)) {
            p.skipChildren();
         } else {
            try {
               Object value;
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

               if (useObjectId) {
                  referringAccumulator.put(key, value);
               } else {
                  result.put(key, value);
               }
            } catch (UnresolvedForwardReference var13) {
               this.handleUnresolvedReference(ctxt, referringAccumulator, key, var13);
            } catch (Exception var14) {
               this.wrapAndThrow(var14, result, keyStr);
            }
         }
      }

   }

   protected final void _readAndBindStringKeyMap(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result) throws IOException {
      JsonDeserializer<Object> valueDes = this._valueDeserializer;
      TypeDeserializer typeDeser = this._valueTypeDeserializer;
      MapDeserializer.MapReferringAccumulator referringAccumulator = null;
      boolean useObjectId = valueDes.getObjectIdReader() != null;
      if (useObjectId) {
         referringAccumulator = new MapDeserializer.MapReferringAccumulator(this._containerType.getContentType().getRawClass(), result);
      }

      String key;
      JsonToken t;
      if (p.isExpectedStartObjectToken()) {
         key = p.nextFieldName();
      } else {
         t = p.getCurrentToken();
         if (t == JsonToken.END_OBJECT) {
            return;
         }

         if (t != JsonToken.FIELD_NAME) {
            ctxt.reportWrongTokenException((JsonDeserializer)this, JsonToken.FIELD_NAME, (String)null);
         }

         key = p.getCurrentName();
      }

      for(; key != null; key = p.nextFieldName()) {
         t = p.nextToken();
         if (this._ignorableProperties != null && this._ignorableProperties.contains(key)) {
            p.skipChildren();
         } else {
            try {
               Object value;
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

               if (useObjectId) {
                  referringAccumulator.put(key, value);
               } else {
                  result.put(key, value);
               }
            } catch (UnresolvedForwardReference var11) {
               this.handleUnresolvedReference(ctxt, referringAccumulator, key, var11);
            } catch (Exception var12) {
               this.wrapAndThrow(var12, result, key);
            }
         }
      }

   }

   public Map<Object, Object> _deserializeUsingCreator(JsonParser p, DeserializationContext ctxt) throws IOException {
      PropertyBasedCreator creator = this._propertyBasedCreator;
      PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, (ObjectIdReader)null);
      JsonDeserializer<Object> valueDes = this._valueDeserializer;
      TypeDeserializer typeDeser = this._valueTypeDeserializer;
      String key;
      if (p.isExpectedStartObjectToken()) {
         key = p.nextFieldName();
      } else if (p.hasToken(JsonToken.FIELD_NAME)) {
         key = p.getCurrentName();
      } else {
         key = null;
      }

      for(; key != null; key = p.nextFieldName()) {
         JsonToken t = p.nextToken();
         if (this._ignorableProperties != null && this._ignorableProperties.contains(key)) {
            p.skipChildren();
         } else {
            SettableBeanProperty prop = creator.findCreatorProperty(key);
            if (prop != null) {
               if (buffer.assignParameter(prop, prop.deserialize(p, ctxt))) {
                  p.nextToken();

                  Map result;
                  try {
                     result = (Map)creator.build(ctxt, buffer);
                  } catch (Exception var13) {
                     return (Map)this.wrapAndThrow(var13, this._containerType.getRawClass(), key);
                  }

                  this._readAndBind(p, ctxt, result);
                  return result;
               }
            } else {
               Object actualKey = this._keyDeserializer.deserializeKey(key, ctxt);

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
               } catch (Exception var15) {
                  this.wrapAndThrow(var15, this._containerType.getRawClass(), key);
                  return null;
               }

               buffer.bufferMapProperty(actualKey, value);
            }
         }
      }

      try {
         return (Map)creator.build(ctxt, buffer);
      } catch (Exception var14) {
         this.wrapAndThrow(var14, this._containerType.getRawClass(), key);
         return null;
      }
   }

   protected final void _readAndUpdate(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result) throws IOException {
      KeyDeserializer keyDes = this._keyDeserializer;
      JsonDeserializer<Object> valueDes = this._valueDeserializer;
      TypeDeserializer typeDeser = this._valueTypeDeserializer;
      String keyStr;
      if (p.isExpectedStartObjectToken()) {
         keyStr = p.nextFieldName();
      } else {
         JsonToken t = p.getCurrentToken();
         if (t == JsonToken.END_OBJECT) {
            return;
         }

         if (t != JsonToken.FIELD_NAME) {
            ctxt.reportWrongTokenException((JsonDeserializer)this, JsonToken.FIELD_NAME, (String)null);
         }

         keyStr = p.getCurrentName();
      }

      for(; keyStr != null; keyStr = p.nextFieldName()) {
         Object key = keyDes.deserializeKey(keyStr, ctxt);
         JsonToken t = p.nextToken();
         if (this._ignorableProperties != null && this._ignorableProperties.contains(keyStr)) {
            p.skipChildren();
         } else {
            try {
               if (t == JsonToken.VALUE_NULL) {
                  if (!this._skipNullValues) {
                     result.put(key, this._nullProvider.getNullValue(ctxt));
                  }
               } else {
                  Object value = result.get(key);
                  if (value != null) {
                     valueDes.deserialize(p, ctxt, value);
                  } else {
                     if (typeDeser == null) {
                        value = valueDes.deserialize(p, ctxt);
                     } else {
                        value = valueDes.deserializeWithType(p, ctxt, typeDeser);
                     }

                     result.put(key, value);
                  }
               }
            } catch (Exception var11) {
               this.wrapAndThrow(var11, result, keyStr);
            }
         }
      }

   }

   protected final void _readAndUpdateStringKeyMap(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result) throws IOException {
      JsonDeserializer<Object> valueDes = this._valueDeserializer;
      TypeDeserializer typeDeser = this._valueTypeDeserializer;
      String key;
      JsonToken t;
      if (p.isExpectedStartObjectToken()) {
         key = p.nextFieldName();
      } else {
         t = p.getCurrentToken();
         if (t == JsonToken.END_OBJECT) {
            return;
         }

         if (t != JsonToken.FIELD_NAME) {
            ctxt.reportWrongTokenException((JsonDeserializer)this, JsonToken.FIELD_NAME, (String)null);
         }

         key = p.getCurrentName();
      }

      for(; key != null; key = p.nextFieldName()) {
         t = p.nextToken();
         if (this._ignorableProperties != null && this._ignorableProperties.contains(key)) {
            p.skipChildren();
         } else {
            try {
               if (t == JsonToken.VALUE_NULL) {
                  if (!this._skipNullValues) {
                     result.put(key, this._nullProvider.getNullValue(ctxt));
                  }
               } else {
                  Object old = result.get(key);
                  Object value;
                  if (old != null) {
                     value = valueDes.deserialize(p, ctxt, old);
                  } else if (typeDeser == null) {
                     value = valueDes.deserialize(p, ctxt);
                  } else {
                     value = valueDes.deserializeWithType(p, ctxt, typeDeser);
                  }

                  if (value != old) {
                     result.put(key, value);
                  }
               }
            } catch (Exception var10) {
               this.wrapAndThrow(var10, result, key);
            }
         }
      }

   }

   private void handleUnresolvedReference(DeserializationContext ctxt, MapDeserializer.MapReferringAccumulator accumulator, Object key, UnresolvedForwardReference reference) throws JsonMappingException {
      if (accumulator == null) {
         ctxt.reportInputMismatch((JsonDeserializer)this, "Unresolved forward reference but no identity info: " + reference);
      }

      ReadableObjectId.Referring referring = accumulator.handleUnresolvedReference(reference, key);
      reference.getRoid().appendReferring(referring);
   }

   static class MapReferring extends ReadableObjectId.Referring {
      private final MapDeserializer.MapReferringAccumulator _parent;
      public final Map<Object, Object> next = new LinkedHashMap();
      public final Object key;

      MapReferring(MapDeserializer.MapReferringAccumulator parent, UnresolvedForwardReference ref, Class<?> valueType, Object key) {
         super(ref, valueType);
         this._parent = parent;
         this.key = key;
      }

      public void handleResolvedForwardReference(Object id, Object value) throws IOException {
         this._parent.resolveForwardReference(id, value);
      }
   }

   private static final class MapReferringAccumulator {
      private final Class<?> _valueType;
      private Map<Object, Object> _result;
      private List<MapDeserializer.MapReferring> _accumulator = new ArrayList();

      public MapReferringAccumulator(Class<?> valueType, Map<Object, Object> result) {
         this._valueType = valueType;
         this._result = result;
      }

      public void put(Object key, Object value) {
         if (this._accumulator.isEmpty()) {
            this._result.put(key, value);
         } else {
            MapDeserializer.MapReferring ref = (MapDeserializer.MapReferring)this._accumulator.get(this._accumulator.size() - 1);
            ref.next.put(key, value);
         }

      }

      public ReadableObjectId.Referring handleUnresolvedReference(UnresolvedForwardReference reference, Object key) {
         MapDeserializer.MapReferring id = new MapDeserializer.MapReferring(this, reference, this._valueType, key);
         this._accumulator.add(id);
         return id;
      }

      public void resolveForwardReference(Object id, Object value) throws IOException {
         Iterator<MapDeserializer.MapReferring> iterator = this._accumulator.iterator();

         MapDeserializer.MapReferring ref;
         for(Map previous = this._result; iterator.hasNext(); previous = ref.next) {
            ref = (MapDeserializer.MapReferring)iterator.next();
            if (ref.hasId(id)) {
               iterator.remove();
               previous.put(ref.key, value);
               previous.putAll(ref.next);
               return;
            }
         }

         throw new IllegalArgumentException("Trying to resolve a forward reference with id [" + id + "] that wasn't previously seen as unresolved.");
      }
   }
}

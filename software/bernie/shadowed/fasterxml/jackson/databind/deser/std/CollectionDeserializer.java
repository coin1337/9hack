package software.bernie.shadowed.fasterxml.jackson.databind.deser.std;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ContextualDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.NullValueProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ValueInstantiator;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;

@JacksonStdImpl
public class CollectionDeserializer extends ContainerDeserializerBase<Collection<Object>> implements ContextualDeserializer {
   private static final long serialVersionUID = -1L;
   protected final JsonDeserializer<Object> _valueDeserializer;
   protected final TypeDeserializer _valueTypeDeserializer;
   protected final ValueInstantiator _valueInstantiator;
   protected final JsonDeserializer<Object> _delegateDeserializer;

   public CollectionDeserializer(JavaType collectionType, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, ValueInstantiator valueInstantiator) {
      this(collectionType, valueDeser, valueTypeDeser, valueInstantiator, (JsonDeserializer)null, (NullValueProvider)null, (Boolean)null);
   }

   protected CollectionDeserializer(JavaType collectionType, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, ValueInstantiator valueInstantiator, JsonDeserializer<Object> delegateDeser, NullValueProvider nuller, Boolean unwrapSingle) {
      super(collectionType, nuller, unwrapSingle);
      this._valueDeserializer = valueDeser;
      this._valueTypeDeserializer = valueTypeDeser;
      this._valueInstantiator = valueInstantiator;
      this._delegateDeserializer = delegateDeser;
   }

   protected CollectionDeserializer(CollectionDeserializer src) {
      super((ContainerDeserializerBase)src);
      this._valueDeserializer = src._valueDeserializer;
      this._valueTypeDeserializer = src._valueTypeDeserializer;
      this._valueInstantiator = src._valueInstantiator;
      this._delegateDeserializer = src._delegateDeserializer;
   }

   protected CollectionDeserializer withResolved(JsonDeserializer<?> dd, JsonDeserializer<?> vd, TypeDeserializer vtd, NullValueProvider nuller, Boolean unwrapSingle) {
      return new CollectionDeserializer(this._containerType, vd, vtd, this._valueInstantiator, dd, nuller, unwrapSingle);
   }

   public boolean isCachable() {
      return this._valueDeserializer == null && this._valueTypeDeserializer == null && this._delegateDeserializer == null;
   }

   public CollectionDeserializer createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
      JsonDeserializer<Object> delegateDeser = null;
      if (this._valueInstantiator != null) {
         JavaType delegateType;
         if (this._valueInstantiator.canCreateUsingDelegate()) {
            delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
            if (delegateType == null) {
               ctxt.reportBadDefinition(this._containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'", this._containerType, this._valueInstantiator.getClass().getName()));
            }

            delegateDeser = this.findDeserializer(ctxt, delegateType, property);
         } else if (this._valueInstantiator.canCreateUsingArrayDelegate()) {
            delegateType = this._valueInstantiator.getArrayDelegateType(ctxt.getConfig());
            if (delegateType == null) {
               ctxt.reportBadDefinition(this._containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingArrayDelegate()', but null for 'getArrayDelegateType()'", this._containerType, this._valueInstantiator.getClass().getName()));
            }

            delegateDeser = this.findDeserializer(ctxt, delegateType, property);
         }
      }

      Boolean unwrapSingle = this.findFormatFeature(ctxt, property, Collection.class, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
      JsonDeserializer<?> valueDeser = this._valueDeserializer;
      valueDeser = this.findConvertingContentDeserializer(ctxt, property, valueDeser);
      JavaType vt = this._containerType.getContentType();
      if (valueDeser == null) {
         valueDeser = ctxt.findContextualValueDeserializer(vt, property);
      } else {
         valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, vt);
      }

      TypeDeserializer valueTypeDeser = this._valueTypeDeserializer;
      if (valueTypeDeser != null) {
         valueTypeDeser = valueTypeDeser.forProperty(property);
      }

      NullValueProvider nuller = this.findContentNullProvider(ctxt, property, valueDeser);
      return unwrapSingle == this._unwrapSingle && nuller == this._nullProvider && delegateDeser == this._delegateDeserializer && valueDeser == this._valueDeserializer && valueTypeDeser == this._valueTypeDeserializer ? this : this.withResolved(delegateDeser, valueDeser, valueTypeDeser, nuller, unwrapSingle);
   }

   public JsonDeserializer<Object> getContentDeserializer() {
      return this._valueDeserializer;
   }

   public ValueInstantiator getValueInstantiator() {
      return this._valueInstantiator;
   }

   public Collection<Object> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (this._delegateDeserializer != null) {
         return (Collection)this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
      } else {
         if (p.hasToken(JsonToken.VALUE_STRING)) {
            String str = p.getText();
            if (str.length() == 0) {
               return (Collection)this._valueInstantiator.createFromString(ctxt, str);
            }
         }

         return this.deserialize(p, ctxt, this.createDefaultInstance(ctxt));
      }
   }

   protected Collection<Object> createDefaultInstance(DeserializationContext ctxt) throws IOException {
      return (Collection)this._valueInstantiator.createUsingDefault(ctxt);
   }

   public Collection<Object> deserialize(JsonParser p, DeserializationContext ctxt, Collection<Object> result) throws IOException {
      if (!p.isExpectedStartArrayToken()) {
         return this.handleNonArray(p, ctxt, result);
      } else {
         p.setCurrentValue(result);
         JsonDeserializer<Object> valueDes = this._valueDeserializer;
         TypeDeserializer typeDeser = this._valueTypeDeserializer;
         CollectionDeserializer.CollectionReferringAccumulator referringAccumulator = valueDes.getObjectIdReader() == null ? null : new CollectionDeserializer.CollectionReferringAccumulator(this._containerType.getContentType().getRawClass(), result);

         JsonToken t;
         while((t = p.nextToken()) != JsonToken.END_ARRAY) {
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

               if (referringAccumulator != null) {
                  referringAccumulator.add(value);
               } else {
                  result.add(value);
               }
            } catch (UnresolvedForwardReference var10) {
               if (referringAccumulator == null) {
                  throw JsonMappingException.from((JsonParser)p, "Unresolved forward reference but no identity info", var10);
               }

               ReadableObjectId.Referring ref = referringAccumulator.handleUnresolvedReference(var10);
               var10.getRoid().appendReferring(ref);
            } catch (Exception var11) {
               boolean wrap = ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS);
               if (!wrap) {
                  ClassUtil.throwIfRTE(var11);
               }

               throw JsonMappingException.wrapWithPath(var11, result, result.size());
            }
         }

         return result;
      }
   }

   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
      return typeDeserializer.deserializeTypedFromArray(p, ctxt);
   }

   protected final Collection<Object> handleNonArray(JsonParser p, DeserializationContext ctxt, Collection<Object> result) throws IOException {
      boolean canWrap = this._unwrapSingle == Boolean.TRUE || this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
      if (!canWrap) {
         return (Collection)ctxt.handleUnexpectedToken(this._containerType.getRawClass(), p);
      } else {
         JsonDeserializer<Object> valueDes = this._valueDeserializer;
         TypeDeserializer typeDeser = this._valueTypeDeserializer;
         JsonToken t = p.getCurrentToken();

         Object value;
         try {
            if (t == JsonToken.VALUE_NULL) {
               if (this._skipNullValues) {
                  return result;
               }

               value = this._nullProvider.getNullValue(ctxt);
            } else if (typeDeser == null) {
               value = valueDes.deserialize(p, ctxt);
            } else {
               value = valueDes.deserializeWithType(p, ctxt, typeDeser);
            }
         } catch (Exception var10) {
            throw JsonMappingException.wrapWithPath(var10, Object.class, result.size());
         }

         result.add(value);
         return result;
      }
   }

   private static final class CollectionReferring extends ReadableObjectId.Referring {
      private final CollectionDeserializer.CollectionReferringAccumulator _parent;
      public final List<Object> next = new ArrayList();

      CollectionReferring(CollectionDeserializer.CollectionReferringAccumulator parent, UnresolvedForwardReference reference, Class<?> contentType) {
         super(reference, contentType);
         this._parent = parent;
      }

      public void handleResolvedForwardReference(Object id, Object value) throws IOException {
         this._parent.resolveForwardReference(id, value);
      }
   }

   public static final class CollectionReferringAccumulator {
      private final Class<?> _elementType;
      private final Collection<Object> _result;
      private List<CollectionDeserializer.CollectionReferring> _accumulator = new ArrayList();

      public CollectionReferringAccumulator(Class<?> elementType, Collection<Object> result) {
         this._elementType = elementType;
         this._result = result;
      }

      public void add(Object value) {
         if (this._accumulator.isEmpty()) {
            this._result.add(value);
         } else {
            CollectionDeserializer.CollectionReferring ref = (CollectionDeserializer.CollectionReferring)this._accumulator.get(this._accumulator.size() - 1);
            ref.next.add(value);
         }

      }

      public ReadableObjectId.Referring handleUnresolvedReference(UnresolvedForwardReference reference) {
         CollectionDeserializer.CollectionReferring id = new CollectionDeserializer.CollectionReferring(this, reference, this._elementType);
         this._accumulator.add(id);
         return id;
      }

      public void resolveForwardReference(Object id, Object value) throws IOException {
         Iterator<CollectionDeserializer.CollectionReferring> iterator = this._accumulator.iterator();

         CollectionDeserializer.CollectionReferring ref;
         for(Object previous = this._result; iterator.hasNext(); previous = ref.next) {
            ref = (CollectionDeserializer.CollectionReferring)iterator.next();
            if (ref.hasId(id)) {
               iterator.remove();
               ((Collection)previous).add(value);
               ((Collection)previous).addAll(ref.next);
               return;
            }
         }

         throw new IllegalArgumentException("Trying to resolve a forward reference with id [" + id + "] that wasn't previously seen as unresolved.");
      }
   }
}

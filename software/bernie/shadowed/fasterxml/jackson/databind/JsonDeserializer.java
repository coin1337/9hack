package software.bernie.shadowed.fasterxml.jackson.databind;

import java.io.IOException;
import java.util.Collection;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonProcessingException;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.NullValueProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.SettableBeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.util.AccessPattern;
import software.bernie.shadowed.fasterxml.jackson.databind.util.NameTransformer;

public abstract class JsonDeserializer<T> implements NullValueProvider {
   public abstract T deserialize(JsonParser var1, DeserializationContext var2) throws IOException, JsonProcessingException;

   public T deserialize(JsonParser p, DeserializationContext ctxt, T intoValue) throws IOException {
      if (ctxt.isEnabled(MapperFeature.IGNORE_MERGE_FOR_UNMERGEABLE)) {
         return this.deserialize(p, ctxt);
      } else {
         throw new UnsupportedOperationException("Cannot update object of type " + intoValue.getClass().getName() + " (by deserializer of type " + this.getClass().getName() + ")");
      }
   }

   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
      return typeDeserializer.deserializeTypedFromAny(p, ctxt);
   }

   public JsonDeserializer<T> unwrappingDeserializer(NameTransformer unwrapper) {
      return this;
   }

   public JsonDeserializer<?> replaceDelegatee(JsonDeserializer<?> delegatee) {
      throw new UnsupportedOperationException();
   }

   public Class<?> handledType() {
      return null;
   }

   public boolean isCachable() {
      return false;
   }

   public JsonDeserializer<?> getDelegatee() {
      return null;
   }

   public Collection<Object> getKnownPropertyNames() {
      return null;
   }

   public T getNullValue(DeserializationContext ctxt) throws JsonMappingException {
      return this.getNullValue();
   }

   public AccessPattern getNullAccessPattern() {
      return AccessPattern.CONSTANT;
   }

   public AccessPattern getEmptyAccessPattern() {
      return AccessPattern.DYNAMIC;
   }

   public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
      return this.getNullValue(ctxt);
   }

   public ObjectIdReader getObjectIdReader() {
      return null;
   }

   public SettableBeanProperty findBackReference(String refName) {
      throw new IllegalArgumentException("Cannot handle managed/back reference '" + refName + "': type: value deserializer of type " + this.getClass().getName() + " does not support them");
   }

   public Boolean supportsUpdate(DeserializationConfig config) {
      return null;
   }

   /** @deprecated */
   @Deprecated
   public T getNullValue() {
      return null;
   }

   /** @deprecated */
   @Deprecated
   public Object getEmptyValue() {
      return this.getNullValue();
   }

   public abstract static class None extends JsonDeserializer<Object> {
      private None() {
      }
   }
}

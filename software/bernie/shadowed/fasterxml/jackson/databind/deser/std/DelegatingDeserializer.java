package software.bernie.shadowed.fasterxml.jackson.databind.deser.std;

import java.io.IOException;
import java.util.Collection;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ContextualDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.SettableBeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.util.AccessPattern;

public abstract class DelegatingDeserializer extends StdDeserializer<Object> implements ContextualDeserializer, ResolvableDeserializer {
   private static final long serialVersionUID = 1L;
   protected final JsonDeserializer<?> _delegatee;

   public DelegatingDeserializer(JsonDeserializer<?> d) {
      super(d.getClass());
      this._delegatee = d;
   }

   protected abstract JsonDeserializer<?> newDelegatingInstance(JsonDeserializer<?> var1);

   public void resolve(DeserializationContext ctxt) throws JsonMappingException {
      if (this._delegatee instanceof ResolvableDeserializer) {
         ((ResolvableDeserializer)this._delegatee).resolve(ctxt);
      }

   }

   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
      JavaType vt = ctxt.constructType(this._delegatee.handledType());
      JsonDeserializer<?> del = ctxt.handleSecondaryContextualization(this._delegatee, property, vt);
      return (JsonDeserializer)(del == this._delegatee ? this : this.newDelegatingInstance(del));
   }

   public JsonDeserializer<?> replaceDelegatee(JsonDeserializer<?> delegatee) {
      return (JsonDeserializer)(delegatee == this._delegatee ? this : this.newDelegatingInstance(delegatee));
   }

   public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      return this._delegatee.deserialize(p, ctxt);
   }

   public Object deserialize(JsonParser p, DeserializationContext ctxt, Object intoValue) throws IOException {
      return this._delegatee.deserialize(p, ctxt, intoValue);
   }

   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
      return this._delegatee.deserializeWithType(p, ctxt, typeDeserializer);
   }

   public boolean isCachable() {
      return this._delegatee.isCachable();
   }

   public Boolean supportsUpdate(DeserializationConfig config) {
      return this._delegatee.supportsUpdate(config);
   }

   public JsonDeserializer<?> getDelegatee() {
      return this._delegatee;
   }

   public SettableBeanProperty findBackReference(String logicalName) {
      return this._delegatee.findBackReference(logicalName);
   }

   public AccessPattern getNullAccessPattern() {
      return this._delegatee.getNullAccessPattern();
   }

   public Object getNullValue(DeserializationContext ctxt) throws JsonMappingException {
      return this._delegatee.getNullValue(ctxt);
   }

   public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
      return this._delegatee.getEmptyValue(ctxt);
   }

   public Collection<Object> getKnownPropertyNames() {
      return this._delegatee.getKnownPropertyNames();
   }

   public ObjectIdReader getObjectIdReader() {
      return this._delegatee.getObjectIdReader();
   }
}

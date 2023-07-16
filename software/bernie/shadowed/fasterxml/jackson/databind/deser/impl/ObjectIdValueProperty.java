package software.bernie.shadowed.fasterxml.jackson.databind.deser.impl;

import java.io.IOException;
import java.lang.annotation.Annotation;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyMetadata;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyName;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.NullValueProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.SettableBeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMember;

public final class ObjectIdValueProperty extends SettableBeanProperty {
   private static final long serialVersionUID = 1L;
   protected final ObjectIdReader _objectIdReader;

   public ObjectIdValueProperty(ObjectIdReader objectIdReader, PropertyMetadata metadata) {
      super(objectIdReader.propertyName, objectIdReader.getIdType(), metadata, objectIdReader.getDeserializer());
      this._objectIdReader = objectIdReader;
   }

   protected ObjectIdValueProperty(ObjectIdValueProperty src, JsonDeserializer<?> deser, NullValueProvider nva) {
      super(src, deser, nva);
      this._objectIdReader = src._objectIdReader;
   }

   protected ObjectIdValueProperty(ObjectIdValueProperty src, PropertyName newName) {
      super(src, newName);
      this._objectIdReader = src._objectIdReader;
   }

   public SettableBeanProperty withName(PropertyName newName) {
      return new ObjectIdValueProperty(this, newName);
   }

   public SettableBeanProperty withValueDeserializer(JsonDeserializer<?> deser) {
      return this._valueDeserializer == deser ? this : new ObjectIdValueProperty(this, deser, this._nullProvider);
   }

   public SettableBeanProperty withNullProvider(NullValueProvider nva) {
      return new ObjectIdValueProperty(this, this._valueDeserializer, nva);
   }

   public <A extends Annotation> A getAnnotation(Class<A> acls) {
      return null;
   }

   public AnnotatedMember getMember() {
      return null;
   }

   public void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
      this.deserializeSetAndReturn(p, ctxt, instance);
   }

   public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
      if (p.hasToken(JsonToken.VALUE_NULL)) {
         return null;
      } else {
         Object id = this._valueDeserializer.deserialize(p, ctxt);
         ReadableObjectId roid = ctxt.findObjectId(id, this._objectIdReader.generator, this._objectIdReader.resolver);
         roid.bindItem(instance);
         SettableBeanProperty idProp = this._objectIdReader.idProperty;
         return idProp != null ? idProp.setAndReturn(instance, id) : instance;
      }
   }

   public void set(Object instance, Object value) throws IOException {
      this.setAndReturn(instance, value);
   }

   public Object setAndReturn(Object instance, Object value) throws IOException {
      SettableBeanProperty idProp = this._objectIdReader.idProperty;
      if (idProp == null) {
         throw new UnsupportedOperationException("Should not call set() on ObjectIdProperty that has no SettableBeanProperty");
      } else {
         return idProp.setAndReturn(instance, value);
      }
   }
}

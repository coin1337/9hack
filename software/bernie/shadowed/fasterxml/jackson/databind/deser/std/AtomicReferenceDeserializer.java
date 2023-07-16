package software.bernie.shadowed.fasterxml.jackson.databind.deser.std;

import java.util.concurrent.atomic.AtomicReference;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ValueInstantiator;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;

public class AtomicReferenceDeserializer extends ReferenceTypeDeserializer<AtomicReference<Object>> {
   private static final long serialVersionUID = 1L;

   public AtomicReferenceDeserializer(JavaType fullType, ValueInstantiator inst, TypeDeserializer typeDeser, JsonDeserializer<?> deser) {
      super(fullType, inst, typeDeser, deser);
   }

   public AtomicReferenceDeserializer withResolved(TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser) {
      return new AtomicReferenceDeserializer(this._fullType, this._valueInstantiator, typeDeser, valueDeser);
   }

   public AtomicReference<Object> getNullValue(DeserializationContext ctxt) {
      return new AtomicReference();
   }

   public Object getEmptyValue(DeserializationContext ctxt) {
      return new AtomicReference();
   }

   public AtomicReference<Object> referenceValue(Object contents) {
      return new AtomicReference(contents);
   }

   public Object getReferenced(AtomicReference<Object> reference) {
      return reference.get();
   }

   public AtomicReference<Object> updateReference(AtomicReference<Object> reference, Object contents) {
      reference.set(contents);
      return reference;
   }

   public Boolean supportsUpdate(DeserializationConfig config) {
      return Boolean.TRUE;
   }
}

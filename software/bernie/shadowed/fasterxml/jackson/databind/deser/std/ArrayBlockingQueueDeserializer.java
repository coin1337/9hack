package software.bernie.shadowed.fasterxml.jackson.databind.deser.std;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.NullValueProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ValueInstantiator;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;

public class ArrayBlockingQueueDeserializer extends CollectionDeserializer {
   private static final long serialVersionUID = 1L;

   public ArrayBlockingQueueDeserializer(JavaType containerType, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, ValueInstantiator valueInstantiator) {
      super(containerType, valueDeser, valueTypeDeser, valueInstantiator);
   }

   protected ArrayBlockingQueueDeserializer(JavaType containerType, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, ValueInstantiator valueInstantiator, JsonDeserializer<Object> delegateDeser, NullValueProvider nuller, Boolean unwrapSingle) {
      super(containerType, valueDeser, valueTypeDeser, valueInstantiator, delegateDeser, nuller, unwrapSingle);
   }

   protected ArrayBlockingQueueDeserializer(ArrayBlockingQueueDeserializer src) {
      super(src);
   }

   protected ArrayBlockingQueueDeserializer withResolved(JsonDeserializer<?> dd, JsonDeserializer<?> vd, TypeDeserializer vtd, NullValueProvider nuller, Boolean unwrapSingle) {
      return new ArrayBlockingQueueDeserializer(this._containerType, vd, vtd, this._valueInstantiator, dd, nuller, unwrapSingle);
   }

   protected Collection<Object> createDefaultInstance(DeserializationContext ctxt) throws IOException {
      return null;
   }

   public Collection<Object> deserialize(JsonParser p, DeserializationContext ctxt, Collection<Object> result0) throws IOException {
      if (result0 != null) {
         return super.deserialize(p, ctxt, result0);
      } else if (!p.isExpectedStartArrayToken()) {
         return this.handleNonArray(p, ctxt, new ArrayBlockingQueue(1));
      } else {
         result0 = super.deserialize(p, ctxt, (Collection)(new ArrayList()));
         return new ArrayBlockingQueue(result0.size(), false, result0);
      }
   }

   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
      return typeDeserializer.deserializeTypedFromArray(p, ctxt);
   }
}

package software.bernie.shadowed.fasterxml.jackson.databind.deser.std;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.util.AccessPattern;

public abstract class StdScalarDeserializer<T> extends StdDeserializer<T> {
   private static final long serialVersionUID = 1L;

   protected StdScalarDeserializer(Class<?> vc) {
      super(vc);
   }

   protected StdScalarDeserializer(JavaType valueType) {
      super(valueType);
   }

   protected StdScalarDeserializer(StdScalarDeserializer<?> src) {
      super((StdDeserializer)src);
   }

   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
      return typeDeserializer.deserializeTypedFromScalar(p, ctxt);
   }

   public T deserialize(JsonParser p, DeserializationContext ctxt, T intoValue) throws IOException {
      ctxt.reportBadMerge(this);
      return this.deserialize(p, ctxt);
   }

   public Boolean supportsUpdate(DeserializationConfig config) {
      return Boolean.FALSE;
   }

   public AccessPattern getNullAccessPattern() {
      return AccessPattern.ALWAYS_NULL;
   }

   public AccessPattern getEmptyAccessPattern() {
      return AccessPattern.CONSTANT;
   }
}

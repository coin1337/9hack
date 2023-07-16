package software.bernie.shadowed.fasterxml.jackson.databind.deser.impl;

import java.io.Serializable;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.NullValueProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.util.AccessPattern;

public class NullsAsEmptyProvider implements NullValueProvider, Serializable {
   private static final long serialVersionUID = 1L;
   protected final JsonDeserializer<?> _deserializer;

   public NullsAsEmptyProvider(JsonDeserializer<?> deser) {
      this._deserializer = deser;
   }

   public AccessPattern getNullAccessPattern() {
      return AccessPattern.DYNAMIC;
   }

   public Object getNullValue(DeserializationContext ctxt) throws JsonMappingException {
      return this._deserializer.getEmptyValue(ctxt);
   }
}

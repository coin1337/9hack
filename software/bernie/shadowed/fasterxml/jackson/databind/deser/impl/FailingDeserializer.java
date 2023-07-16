package software.bernie.shadowed.fasterxml.jackson.databind.deser.impl;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class FailingDeserializer extends StdDeserializer<Object> {
   private static final long serialVersionUID = 1L;
   protected final String _message;

   public FailingDeserializer(String m) {
      super(Object.class);
      this._message = m;
   }

   public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      ctxt.reportInputMismatch((JsonDeserializer)this, this._message);
      return null;
   }
}

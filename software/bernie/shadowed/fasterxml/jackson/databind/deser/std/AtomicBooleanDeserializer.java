package software.bernie.shadowed.fasterxml.jackson.databind.deser.std;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;

public class AtomicBooleanDeserializer extends StdScalarDeserializer<AtomicBoolean> {
   private static final long serialVersionUID = 1L;

   public AtomicBooleanDeserializer() {
      super(AtomicBoolean.class);
   }

   public AtomicBoolean deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
      return new AtomicBoolean(this._parseBooleanPrimitive(jp, ctxt));
   }
}

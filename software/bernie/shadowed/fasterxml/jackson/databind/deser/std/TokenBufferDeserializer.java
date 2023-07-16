package software.bernie.shadowed.fasterxml.jackson.databind.deser.std;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import software.bernie.shadowed.fasterxml.jackson.databind.util.TokenBuffer;

@JacksonStdImpl
public class TokenBufferDeserializer extends StdScalarDeserializer<TokenBuffer> {
   private static final long serialVersionUID = 1L;

   public TokenBufferDeserializer() {
      super(TokenBuffer.class);
   }

   public TokenBuffer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      return this.createBufferInstance(p).deserialize(p, ctxt);
   }

   protected TokenBuffer createBufferInstance(JsonParser p) {
      return new TokenBuffer(p);
   }
}

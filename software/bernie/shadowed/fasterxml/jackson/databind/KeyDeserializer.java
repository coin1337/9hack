package software.bernie.shadowed.fasterxml.jackson.databind;

import java.io.IOException;

public abstract class KeyDeserializer {
   public abstract Object deserializeKey(String var1, DeserializationContext var2) throws IOException;

   public abstract static class None extends KeyDeserializer {
   }
}

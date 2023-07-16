package software.bernie.shadowed.fasterxml.jackson.databind.node;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;

public final class NullNode extends ValueNode {
   public static final NullNode instance = new NullNode();

   private NullNode() {
   }

   public static NullNode getInstance() {
      return instance;
   }

   public JsonNodeType getNodeType() {
      return JsonNodeType.NULL;
   }

   public JsonToken asToken() {
      return JsonToken.VALUE_NULL;
   }

   public String asText(String defaultValue) {
      return defaultValue;
   }

   public String asText() {
      return "null";
   }

   public final void serialize(JsonGenerator g, SerializerProvider provider) throws IOException {
      provider.defaultSerializeNull(g);
   }

   public boolean equals(Object o) {
      return o == this;
   }

   public int hashCode() {
      return JsonNodeType.NULL.ordinal();
   }
}

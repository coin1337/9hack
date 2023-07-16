package software.bernie.shadowed.fasterxml.jackson.databind.node;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonProcessingException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonNode;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeSerializer;

public final class MissingNode extends ValueNode {
   private static final MissingNode instance = new MissingNode();

   private MissingNode() {
   }

   public <T extends JsonNode> T deepCopy() {
      return this;
   }

   public static MissingNode getInstance() {
      return instance;
   }

   public JsonNodeType getNodeType() {
      return JsonNodeType.MISSING;
   }

   public JsonToken asToken() {
      return JsonToken.NOT_AVAILABLE;
   }

   public String asText() {
      return "";
   }

   public String asText(String defaultValue) {
      return defaultValue;
   }

   public final void serialize(JsonGenerator jg, SerializerProvider provider) throws IOException, JsonProcessingException {
      jg.writeNull();
   }

   public void serializeWithType(JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonProcessingException {
      g.writeNull();
   }

   public boolean equals(Object o) {
      return o == this;
   }

   public String toString() {
      return "";
   }

   public int hashCode() {
      return JsonNodeType.MISSING.ordinal();
   }
}

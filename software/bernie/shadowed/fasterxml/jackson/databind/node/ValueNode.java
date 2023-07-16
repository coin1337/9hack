package software.bernie.shadowed.fasterxml.jackson.databind.node;

import java.io.IOException;
import java.util.List;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonPointer;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.type.WritableTypeId;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonNode;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeSerializer;

public abstract class ValueNode extends BaseJsonNode {
   protected ValueNode() {
   }

   protected JsonNode _at(JsonPointer ptr) {
      return MissingNode.getInstance();
   }

   public <T extends JsonNode> T deepCopy() {
      return this;
   }

   public abstract JsonToken asToken();

   public void serializeWithType(JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
      WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(this, this.asToken()));
      this.serialize(g, provider);
      typeSer.writeTypeSuffix(g, typeIdDef);
   }

   public String toString() {
      return this.asText();
   }

   public final JsonNode get(int index) {
      return null;
   }

   public final JsonNode path(int index) {
      return MissingNode.getInstance();
   }

   public final boolean has(int index) {
      return false;
   }

   public final boolean hasNonNull(int index) {
      return false;
   }

   public final JsonNode get(String fieldName) {
      return null;
   }

   public final JsonNode path(String fieldName) {
      return MissingNode.getInstance();
   }

   public final boolean has(String fieldName) {
      return false;
   }

   public final boolean hasNonNull(String fieldName) {
      return false;
   }

   public final JsonNode findValue(String fieldName) {
      return null;
   }

   public final ObjectNode findParent(String fieldName) {
      return null;
   }

   public final List<JsonNode> findValues(String fieldName, List<JsonNode> foundSoFar) {
      return foundSoFar;
   }

   public final List<String> findValuesAsText(String fieldName, List<String> foundSoFar) {
      return foundSoFar;
   }

   public final List<JsonNode> findParents(String fieldName, List<JsonNode> foundSoFar) {
      return foundSoFar;
   }
}

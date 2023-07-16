package software.bernie.shadowed.fasterxml.jackson.databind.jsonschema;

import software.bernie.shadowed.fasterxml.jackson.annotation.JsonCreator;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonValue;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonNode;
import software.bernie.shadowed.fasterxml.jackson.databind.node.JsonNodeFactory;
import software.bernie.shadowed.fasterxml.jackson.databind.node.ObjectNode;

/** @deprecated */
@Deprecated
public class JsonSchema {
   private final ObjectNode schema;

   @JsonCreator
   public JsonSchema(ObjectNode schema) {
      this.schema = schema;
   }

   @JsonValue
   public ObjectNode getSchemaNode() {
      return this.schema;
   }

   public String toString() {
      return this.schema.toString();
   }

   public int hashCode() {
      return this.schema.hashCode();
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o == null) {
         return false;
      } else if (!(o instanceof JsonSchema)) {
         return false;
      } else {
         JsonSchema other = (JsonSchema)o;
         if (this.schema == null) {
            return other.schema == null;
         } else {
            return this.schema.equals(other.schema);
         }
      }
   }

   public static JsonNode getDefaultSchemaNode() {
      ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
      objectNode.put("type", "any");
      return objectNode;
   }
}

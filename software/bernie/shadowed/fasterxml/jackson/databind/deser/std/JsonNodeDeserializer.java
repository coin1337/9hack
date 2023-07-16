package software.bernie.shadowed.fasterxml.jackson.databind.deser.std;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonNode;
import software.bernie.shadowed.fasterxml.jackson.databind.node.ArrayNode;
import software.bernie.shadowed.fasterxml.jackson.databind.node.NullNode;
import software.bernie.shadowed.fasterxml.jackson.databind.node.ObjectNode;

public class JsonNodeDeserializer extends BaseNodeDeserializer<JsonNode> {
   private static final JsonNodeDeserializer instance = new JsonNodeDeserializer();

   protected JsonNodeDeserializer() {
      super(JsonNode.class, (Boolean)null);
   }

   public static JsonDeserializer<? extends JsonNode> getDeserializer(Class<?> nodeClass) {
      if (nodeClass == ObjectNode.class) {
         return JsonNodeDeserializer.ObjectDeserializer.getInstance();
      } else {
         return (JsonDeserializer)(nodeClass == ArrayNode.class ? JsonNodeDeserializer.ArrayDeserializer.getInstance() : instance);
      }
   }

   public JsonNode getNullValue(DeserializationContext ctxt) {
      return NullNode.getInstance();
   }

   public JsonNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      switch(p.getCurrentTokenId()) {
      case 1:
         return this.deserializeObject(p, ctxt, ctxt.getNodeFactory());
      case 3:
         return this.deserializeArray(p, ctxt, ctxt.getNodeFactory());
      default:
         return this.deserializeAny(p, ctxt, ctxt.getNodeFactory());
      }
   }

   static final class ArrayDeserializer extends BaseNodeDeserializer<ArrayNode> {
      private static final long serialVersionUID = 1L;
      protected static final JsonNodeDeserializer.ArrayDeserializer _instance = new JsonNodeDeserializer.ArrayDeserializer();

      protected ArrayDeserializer() {
         super(ArrayNode.class, true);
      }

      public static JsonNodeDeserializer.ArrayDeserializer getInstance() {
         return _instance;
      }

      public ArrayNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         return p.isExpectedStartArrayToken() ? this.deserializeArray(p, ctxt, ctxt.getNodeFactory()) : (ArrayNode)ctxt.handleUnexpectedToken(ArrayNode.class, p);
      }

      public ArrayNode deserialize(JsonParser p, DeserializationContext ctxt, ArrayNode node) throws IOException {
         return p.isExpectedStartArrayToken() ? (ArrayNode)this.updateArray(p, ctxt, node) : (ArrayNode)ctxt.handleUnexpectedToken(ArrayNode.class, p);
      }
   }

   static final class ObjectDeserializer extends BaseNodeDeserializer<ObjectNode> {
      private static final long serialVersionUID = 1L;
      protected static final JsonNodeDeserializer.ObjectDeserializer _instance = new JsonNodeDeserializer.ObjectDeserializer();

      protected ObjectDeserializer() {
         super(ObjectNode.class, true);
      }

      public static JsonNodeDeserializer.ObjectDeserializer getInstance() {
         return _instance;
      }

      public ObjectNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         if (p.isExpectedStartObjectToken()) {
            return this.deserializeObject(p, ctxt, ctxt.getNodeFactory());
         } else if (p.hasToken(JsonToken.FIELD_NAME)) {
            return this.deserializeObjectAtName(p, ctxt, ctxt.getNodeFactory());
         } else {
            return p.hasToken(JsonToken.END_OBJECT) ? ctxt.getNodeFactory().objectNode() : (ObjectNode)ctxt.handleUnexpectedToken(ObjectNode.class, p);
         }
      }

      public ObjectNode deserialize(JsonParser p, DeserializationContext ctxt, ObjectNode node) throws IOException {
         return !p.isExpectedStartObjectToken() && !p.hasToken(JsonToken.FIELD_NAME) ? (ObjectNode)ctxt.handleUnexpectedToken(ObjectNode.class, p) : (ObjectNode)this.updateObject(p, ctxt, node);
      }
   }
}

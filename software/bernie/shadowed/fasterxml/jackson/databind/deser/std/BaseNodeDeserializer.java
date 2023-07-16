package software.bernie.shadowed.fasterxml.jackson.databind.deser.std;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonProcessingException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonNode;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.node.ArrayNode;
import software.bernie.shadowed.fasterxml.jackson.databind.node.JsonNodeFactory;
import software.bernie.shadowed.fasterxml.jackson.databind.node.ObjectNode;
import software.bernie.shadowed.fasterxml.jackson.databind.util.RawValue;

abstract class BaseNodeDeserializer<T extends JsonNode> extends StdDeserializer<T> {
   protected final Boolean _supportsUpdates;

   public BaseNodeDeserializer(Class<T> vc, Boolean supportsUpdates) {
      super(vc);
      this._supportsUpdates = supportsUpdates;
   }

   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
      return typeDeserializer.deserializeTypedFromAny(p, ctxt);
   }

   public boolean isCachable() {
      return true;
   }

   public Boolean supportsUpdate(DeserializationConfig config) {
      return this._supportsUpdates;
   }

   protected void _handleDuplicateField(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory, String fieldName, ObjectNode objectNode, JsonNode oldValue, JsonNode newValue) throws JsonProcessingException {
      if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)) {
         ctxt.reportInputMismatch(JsonNode.class, "Duplicate field '%s' for ObjectNode: not allowed when FAIL_ON_READING_DUP_TREE_KEY enabled", fieldName);
      }

   }

   protected final ObjectNode deserializeObject(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
      ObjectNode node = nodeFactory.objectNode();

      for(String key = p.nextFieldName(); key != null; key = p.nextFieldName()) {
         JsonToken t = p.nextToken();
         if (t == null) {
            t = JsonToken.NOT_AVAILABLE;
         }

         Object value;
         switch(t.id()) {
         case 1:
            value = this.deserializeObject(p, ctxt, nodeFactory);
            break;
         case 2:
         case 4:
         case 5:
         case 8:
         default:
            value = this.deserializeAny(p, ctxt, nodeFactory);
            break;
         case 3:
            value = this.deserializeArray(p, ctxt, nodeFactory);
            break;
         case 6:
            value = nodeFactory.textNode(p.getText());
            break;
         case 7:
            value = this._fromInt(p, ctxt, nodeFactory);
            break;
         case 9:
            value = nodeFactory.booleanNode(true);
            break;
         case 10:
            value = nodeFactory.booleanNode(false);
            break;
         case 11:
            value = nodeFactory.nullNode();
            break;
         case 12:
            value = this._fromEmbedded(p, ctxt, nodeFactory);
         }

         JsonNode old = node.replace(key, (JsonNode)value);
         if (old != null) {
            this._handleDuplicateField(p, ctxt, nodeFactory, key, node, old, (JsonNode)value);
         }
      }

      return node;
   }

   protected final ObjectNode deserializeObjectAtName(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
      ObjectNode node = nodeFactory.objectNode();

      for(String key = p.getCurrentName(); key != null; key = p.nextFieldName()) {
         JsonToken t = p.nextToken();
         if (t == null) {
            t = JsonToken.NOT_AVAILABLE;
         }

         Object value;
         switch(t.id()) {
         case 1:
            value = this.deserializeObject(p, ctxt, nodeFactory);
            break;
         case 2:
         case 4:
         case 5:
         case 8:
         default:
            value = this.deserializeAny(p, ctxt, nodeFactory);
            break;
         case 3:
            value = this.deserializeArray(p, ctxt, nodeFactory);
            break;
         case 6:
            value = nodeFactory.textNode(p.getText());
            break;
         case 7:
            value = this._fromInt(p, ctxt, nodeFactory);
            break;
         case 9:
            value = nodeFactory.booleanNode(true);
            break;
         case 10:
            value = nodeFactory.booleanNode(false);
            break;
         case 11:
            value = nodeFactory.nullNode();
            break;
         case 12:
            value = this._fromEmbedded(p, ctxt, nodeFactory);
         }

         JsonNode old = node.replace(key, (JsonNode)value);
         if (old != null) {
            this._handleDuplicateField(p, ctxt, nodeFactory, key, node, old, (JsonNode)value);
         }
      }

      return node;
   }

   protected final JsonNode updateObject(JsonParser p, DeserializationContext ctxt, ObjectNode node) throws IOException {
      String key;
      if (p.isExpectedStartObjectToken()) {
         key = p.nextFieldName();
      } else {
         if (!p.hasToken(JsonToken.FIELD_NAME)) {
            return (JsonNode)this.deserialize(p, ctxt);
         }

         key = p.getCurrentName();
      }

      for(; key != null; key = p.nextFieldName()) {
         JsonToken t = p.nextToken();
         JsonNode old = node.get(key);
         if (old != null) {
            JsonNode newValue;
            if (old instanceof ObjectNode) {
               newValue = this.updateObject(p, ctxt, (ObjectNode)old);
               if (newValue != old) {
                  node.set(key, newValue);
               }
               continue;
            }

            if (old instanceof ArrayNode) {
               newValue = this.updateArray(p, ctxt, (ArrayNode)old);
               if (newValue != old) {
                  node.set(key, newValue);
               }
               continue;
            }
         }

         if (t == null) {
            t = JsonToken.NOT_AVAILABLE;
         }

         JsonNodeFactory nodeFactory = ctxt.getNodeFactory();
         Object value;
         switch(t.id()) {
         case 1:
            value = this.deserializeObject(p, ctxt, nodeFactory);
            break;
         case 2:
         case 4:
         case 5:
         case 8:
         default:
            value = this.deserializeAny(p, ctxt, nodeFactory);
            break;
         case 3:
            value = this.deserializeArray(p, ctxt, nodeFactory);
            break;
         case 6:
            value = nodeFactory.textNode(p.getText());
            break;
         case 7:
            value = this._fromInt(p, ctxt, nodeFactory);
            break;
         case 9:
            value = nodeFactory.booleanNode(true);
            break;
         case 10:
            value = nodeFactory.booleanNode(false);
            break;
         case 11:
            value = nodeFactory.nullNode();
            break;
         case 12:
            value = this._fromEmbedded(p, ctxt, nodeFactory);
         }

         if (old != null) {
            this._handleDuplicateField(p, ctxt, nodeFactory, key, node, old, (JsonNode)value);
         }

         node.set(key, (JsonNode)value);
      }

      return node;
   }

   protected final ArrayNode deserializeArray(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
      ArrayNode node = nodeFactory.arrayNode();

      while(true) {
         JsonToken t = p.nextToken();
         switch(t.id()) {
         case 1:
            node.add((JsonNode)this.deserializeObject(p, ctxt, nodeFactory));
            break;
         case 2:
         case 5:
         case 8:
         default:
            node.add(this.deserializeAny(p, ctxt, nodeFactory));
            break;
         case 3:
            node.add((JsonNode)this.deserializeArray(p, ctxt, nodeFactory));
            break;
         case 4:
            return node;
         case 6:
            node.add((JsonNode)nodeFactory.textNode(p.getText()));
            break;
         case 7:
            node.add(this._fromInt(p, ctxt, nodeFactory));
            break;
         case 9:
            node.add((JsonNode)nodeFactory.booleanNode(true));
            break;
         case 10:
            node.add((JsonNode)nodeFactory.booleanNode(false));
            break;
         case 11:
            node.add((JsonNode)nodeFactory.nullNode());
            break;
         case 12:
            node.add(this._fromEmbedded(p, ctxt, nodeFactory));
         }
      }
   }

   protected final JsonNode updateArray(JsonParser p, DeserializationContext ctxt, ArrayNode node) throws IOException {
      JsonNodeFactory nodeFactory = ctxt.getNodeFactory();

      while(true) {
         JsonToken t = p.nextToken();
         switch(t.id()) {
         case 1:
            node.add((JsonNode)this.deserializeObject(p, ctxt, nodeFactory));
            break;
         case 2:
         case 5:
         case 8:
         default:
            node.add(this.deserializeAny(p, ctxt, nodeFactory));
            break;
         case 3:
            node.add((JsonNode)this.deserializeArray(p, ctxt, nodeFactory));
            break;
         case 4:
            return node;
         case 6:
            node.add((JsonNode)nodeFactory.textNode(p.getText()));
            break;
         case 7:
            node.add(this._fromInt(p, ctxt, nodeFactory));
            break;
         case 9:
            node.add((JsonNode)nodeFactory.booleanNode(true));
            break;
         case 10:
            node.add((JsonNode)nodeFactory.booleanNode(false));
            break;
         case 11:
            node.add((JsonNode)nodeFactory.nullNode());
            break;
         case 12:
            node.add(this._fromEmbedded(p, ctxt, nodeFactory));
         }
      }
   }

   protected final JsonNode deserializeAny(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
      switch(p.getCurrentTokenId()) {
      case 2:
         return nodeFactory.objectNode();
      case 3:
      case 4:
      default:
         return (JsonNode)ctxt.handleUnexpectedToken(this.handledType(), p);
      case 5:
         return this.deserializeObjectAtName(p, ctxt, nodeFactory);
      case 6:
         return nodeFactory.textNode(p.getText());
      case 7:
         return this._fromInt(p, ctxt, nodeFactory);
      case 8:
         return this._fromFloat(p, ctxt, nodeFactory);
      case 9:
         return nodeFactory.booleanNode(true);
      case 10:
         return nodeFactory.booleanNode(false);
      case 11:
         return nodeFactory.nullNode();
      case 12:
         return this._fromEmbedded(p, ctxt, nodeFactory);
      }
   }

   protected final JsonNode _fromInt(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
      int feats = ctxt.getDeserializationFeatures();
      JsonParser.NumberType nt;
      if ((feats & F_MASK_INT_COERCIONS) != 0) {
         if (DeserializationFeature.USE_BIG_INTEGER_FOR_INTS.enabledIn(feats)) {
            nt = JsonParser.NumberType.BIG_INTEGER;
         } else if (DeserializationFeature.USE_LONG_FOR_INTS.enabledIn(feats)) {
            nt = JsonParser.NumberType.LONG;
         } else {
            nt = p.getNumberType();
         }
      } else {
         nt = p.getNumberType();
      }

      if (nt == JsonParser.NumberType.INT) {
         return nodeFactory.numberNode(p.getIntValue());
      } else {
         return (JsonNode)(nt == JsonParser.NumberType.LONG ? nodeFactory.numberNode(p.getLongValue()) : nodeFactory.numberNode(p.getBigIntegerValue()));
      }
   }

   protected final JsonNode _fromFloat(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
      JsonParser.NumberType nt = p.getNumberType();
      if (nt == JsonParser.NumberType.BIG_DECIMAL) {
         return nodeFactory.numberNode(p.getDecimalValue());
      } else if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
         return (JsonNode)(p.isNaN() ? nodeFactory.numberNode(p.getDoubleValue()) : nodeFactory.numberNode(p.getDecimalValue()));
      } else {
         return nt == JsonParser.NumberType.FLOAT ? nodeFactory.numberNode(p.getFloatValue()) : nodeFactory.numberNode(p.getDoubleValue());
      }
   }

   protected final JsonNode _fromEmbedded(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
      Object ob = p.getEmbeddedObject();
      if (ob == null) {
         return nodeFactory.nullNode();
      } else {
         Class<?> type = ob.getClass();
         if (type == byte[].class) {
            return nodeFactory.binaryNode((byte[])((byte[])ob));
         } else if (ob instanceof RawValue) {
            return nodeFactory.rawValueNode((RawValue)ob);
         } else {
            return (JsonNode)(ob instanceof JsonNode ? (JsonNode)ob : nodeFactory.pojoNode(ob));
         }
      }
   }
}

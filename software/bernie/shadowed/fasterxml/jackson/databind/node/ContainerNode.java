package software.bernie.shadowed.fasterxml.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonNode;
import software.bernie.shadowed.fasterxml.jackson.databind.util.RawValue;

public abstract class ContainerNode<T extends ContainerNode<T>> extends BaseJsonNode implements JsonNodeCreator {
   protected final JsonNodeFactory _nodeFactory;

   protected ContainerNode(JsonNodeFactory nc) {
      this._nodeFactory = nc;
   }

   public abstract JsonToken asToken();

   public String asText() {
      return "";
   }

   public abstract int size();

   public abstract JsonNode get(int var1);

   public abstract JsonNode get(String var1);

   public final ArrayNode arrayNode() {
      return this._nodeFactory.arrayNode();
   }

   public final ArrayNode arrayNode(int capacity) {
      return this._nodeFactory.arrayNode(capacity);
   }

   public final ObjectNode objectNode() {
      return this._nodeFactory.objectNode();
   }

   public final NullNode nullNode() {
      return this._nodeFactory.nullNode();
   }

   public final BooleanNode booleanNode(boolean v) {
      return this._nodeFactory.booleanNode(v);
   }

   public final NumericNode numberNode(byte v) {
      return this._nodeFactory.numberNode(v);
   }

   public final NumericNode numberNode(short v) {
      return this._nodeFactory.numberNode(v);
   }

   public final NumericNode numberNode(int v) {
      return this._nodeFactory.numberNode(v);
   }

   public final NumericNode numberNode(long v) {
      return this._nodeFactory.numberNode(v);
   }

   public final NumericNode numberNode(float v) {
      return this._nodeFactory.numberNode(v);
   }

   public final NumericNode numberNode(double v) {
      return this._nodeFactory.numberNode(v);
   }

   public final ValueNode numberNode(BigInteger v) {
      return this._nodeFactory.numberNode(v);
   }

   public final ValueNode numberNode(BigDecimal v) {
      return this._nodeFactory.numberNode(v);
   }

   public final ValueNode numberNode(Byte v) {
      return this._nodeFactory.numberNode(v);
   }

   public final ValueNode numberNode(Short v) {
      return this._nodeFactory.numberNode(v);
   }

   public final ValueNode numberNode(Integer v) {
      return this._nodeFactory.numberNode(v);
   }

   public final ValueNode numberNode(Long v) {
      return this._nodeFactory.numberNode(v);
   }

   public final ValueNode numberNode(Float v) {
      return this._nodeFactory.numberNode(v);
   }

   public final ValueNode numberNode(Double v) {
      return this._nodeFactory.numberNode(v);
   }

   public final TextNode textNode(String text) {
      return this._nodeFactory.textNode(text);
   }

   public final BinaryNode binaryNode(byte[] data) {
      return this._nodeFactory.binaryNode(data);
   }

   public final BinaryNode binaryNode(byte[] data, int offset, int length) {
      return this._nodeFactory.binaryNode(data, offset, length);
   }

   public final ValueNode pojoNode(Object pojo) {
      return this._nodeFactory.pojoNode(pojo);
   }

   public final ValueNode rawValueNode(RawValue value) {
      return this._nodeFactory.rawValueNode(value);
   }

   public abstract T removeAll();
}

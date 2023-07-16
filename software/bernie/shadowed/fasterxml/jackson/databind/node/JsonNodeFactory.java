package software.bernie.shadowed.fasterxml.jackson.databind.node;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import software.bernie.shadowed.fasterxml.jackson.databind.util.RawValue;

public class JsonNodeFactory implements Serializable, JsonNodeCreator {
   private static final long serialVersionUID = 1L;
   private final boolean _cfgBigDecimalExact;
   private static final JsonNodeFactory decimalsNormalized = new JsonNodeFactory(false);
   private static final JsonNodeFactory decimalsAsIs = new JsonNodeFactory(true);
   public static final JsonNodeFactory instance;

   public JsonNodeFactory(boolean bigDecimalExact) {
      this._cfgBigDecimalExact = bigDecimalExact;
   }

   protected JsonNodeFactory() {
      this(false);
   }

   public static JsonNodeFactory withExactBigDecimals(boolean bigDecimalExact) {
      return bigDecimalExact ? decimalsAsIs : decimalsNormalized;
   }

   public BooleanNode booleanNode(boolean v) {
      return v ? BooleanNode.getTrue() : BooleanNode.getFalse();
   }

   public NullNode nullNode() {
      return NullNode.getInstance();
   }

   public NumericNode numberNode(byte v) {
      return IntNode.valueOf(v);
   }

   public ValueNode numberNode(Byte value) {
      return (ValueNode)(value == null ? this.nullNode() : IntNode.valueOf(value.intValue()));
   }

   public NumericNode numberNode(short v) {
      return ShortNode.valueOf(v);
   }

   public ValueNode numberNode(Short value) {
      return (ValueNode)(value == null ? this.nullNode() : ShortNode.valueOf(value));
   }

   public NumericNode numberNode(int v) {
      return IntNode.valueOf(v);
   }

   public ValueNode numberNode(Integer value) {
      return (ValueNode)(value == null ? this.nullNode() : IntNode.valueOf(value));
   }

   public NumericNode numberNode(long v) {
      return LongNode.valueOf(v);
   }

   public ValueNode numberNode(Long v) {
      return (ValueNode)(v == null ? this.nullNode() : LongNode.valueOf(v));
   }

   public ValueNode numberNode(BigInteger v) {
      return (ValueNode)(v == null ? this.nullNode() : BigIntegerNode.valueOf(v));
   }

   public NumericNode numberNode(float v) {
      return FloatNode.valueOf(v);
   }

   public ValueNode numberNode(Float value) {
      return (ValueNode)(value == null ? this.nullNode() : FloatNode.valueOf(value));
   }

   public NumericNode numberNode(double v) {
      return DoubleNode.valueOf(v);
   }

   public ValueNode numberNode(Double value) {
      return (ValueNode)(value == null ? this.nullNode() : DoubleNode.valueOf(value));
   }

   public ValueNode numberNode(BigDecimal v) {
      if (v == null) {
         return this.nullNode();
      } else if (this._cfgBigDecimalExact) {
         return DecimalNode.valueOf(v);
      } else {
         return v.compareTo(BigDecimal.ZERO) == 0 ? DecimalNode.ZERO : DecimalNode.valueOf(v.stripTrailingZeros());
      }
   }

   public TextNode textNode(String text) {
      return TextNode.valueOf(text);
   }

   public BinaryNode binaryNode(byte[] data) {
      return BinaryNode.valueOf(data);
   }

   public BinaryNode binaryNode(byte[] data, int offset, int length) {
      return BinaryNode.valueOf(data, offset, length);
   }

   public ArrayNode arrayNode() {
      return new ArrayNode(this);
   }

   public ArrayNode arrayNode(int capacity) {
      return new ArrayNode(this, capacity);
   }

   public ObjectNode objectNode() {
      return new ObjectNode(this);
   }

   public ValueNode pojoNode(Object pojo) {
      return new POJONode(pojo);
   }

   public ValueNode rawValueNode(RawValue value) {
      return new POJONode(value);
   }

   protected boolean _inIntRange(long l) {
      int i = (int)l;
      long l2 = (long)i;
      return l2 == l;
   }

   static {
      instance = decimalsNormalized;
   }
}

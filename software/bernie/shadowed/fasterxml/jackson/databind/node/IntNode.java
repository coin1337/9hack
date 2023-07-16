package software.bernie.shadowed.fasterxml.jackson.databind.node;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonProcessingException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.io.NumberOutput;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;

public class IntNode extends NumericNode {
   static final int MIN_CANONICAL = -1;
   static final int MAX_CANONICAL = 10;
   private static final IntNode[] CANONICALS;
   protected final int _value;

   public IntNode(int v) {
      this._value = v;
   }

   public static IntNode valueOf(int i) {
      return i <= 10 && i >= -1 ? CANONICALS[i - -1] : new IntNode(i);
   }

   public JsonToken asToken() {
      return JsonToken.VALUE_NUMBER_INT;
   }

   public JsonParser.NumberType numberType() {
      return JsonParser.NumberType.INT;
   }

   public boolean isIntegralNumber() {
      return true;
   }

   public boolean isInt() {
      return true;
   }

   public boolean canConvertToInt() {
      return true;
   }

   public boolean canConvertToLong() {
      return true;
   }

   public Number numberValue() {
      return this._value;
   }

   public short shortValue() {
      return (short)this._value;
   }

   public int intValue() {
      return this._value;
   }

   public long longValue() {
      return (long)this._value;
   }

   public float floatValue() {
      return (float)this._value;
   }

   public double doubleValue() {
      return (double)this._value;
   }

   public BigDecimal decimalValue() {
      return BigDecimal.valueOf((long)this._value);
   }

   public BigInteger bigIntegerValue() {
      return BigInteger.valueOf((long)this._value);
   }

   public String asText() {
      return NumberOutput.toString(this._value);
   }

   public boolean asBoolean(boolean defaultValue) {
      return this._value != 0;
   }

   public final void serialize(JsonGenerator jg, SerializerProvider provider) throws IOException, JsonProcessingException {
      jg.writeNumber(this._value);
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o == null) {
         return false;
      } else if (o instanceof IntNode) {
         return ((IntNode)o)._value == this._value;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this._value;
   }

   static {
      int count = 12;
      CANONICALS = new IntNode[count];

      for(int i = 0; i < count; ++i) {
         CANONICALS[i] = new IntNode(-1 + i);
      }

   }
}

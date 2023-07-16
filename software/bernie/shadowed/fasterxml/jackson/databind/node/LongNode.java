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

public class LongNode extends NumericNode {
   protected final long _value;

   public LongNode(long v) {
      this._value = v;
   }

   public static LongNode valueOf(long l) {
      return new LongNode(l);
   }

   public JsonToken asToken() {
      return JsonToken.VALUE_NUMBER_INT;
   }

   public JsonParser.NumberType numberType() {
      return JsonParser.NumberType.LONG;
   }

   public boolean isIntegralNumber() {
      return true;
   }

   public boolean isLong() {
      return true;
   }

   public boolean canConvertToInt() {
      return this._value >= -2147483648L && this._value <= 2147483647L;
   }

   public boolean canConvertToLong() {
      return true;
   }

   public Number numberValue() {
      return this._value;
   }

   public short shortValue() {
      return (short)((int)this._value);
   }

   public int intValue() {
      return (int)this._value;
   }

   public long longValue() {
      return this._value;
   }

   public float floatValue() {
      return (float)this._value;
   }

   public double doubleValue() {
      return (double)this._value;
   }

   public BigDecimal decimalValue() {
      return BigDecimal.valueOf(this._value);
   }

   public BigInteger bigIntegerValue() {
      return BigInteger.valueOf(this._value);
   }

   public String asText() {
      return NumberOutput.toString(this._value);
   }

   public boolean asBoolean(boolean defaultValue) {
      return this._value != 0L;
   }

   public final void serialize(JsonGenerator jg, SerializerProvider provider) throws IOException, JsonProcessingException {
      jg.writeNumber(this._value);
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o == null) {
         return false;
      } else if (o instanceof LongNode) {
         return ((LongNode)o)._value == this._value;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return (int)this._value ^ (int)(this._value >> 32);
   }
}

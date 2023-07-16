package software.bernie.shadowed.fasterxml.jackson.databind.node;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.io.NumberOutput;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;

public class FloatNode extends NumericNode {
   protected final float _value;

   public FloatNode(float v) {
      this._value = v;
   }

   public static FloatNode valueOf(float v) {
      return new FloatNode(v);
   }

   public JsonToken asToken() {
      return JsonToken.VALUE_NUMBER_FLOAT;
   }

   public JsonParser.NumberType numberType() {
      return JsonParser.NumberType.FLOAT;
   }

   public boolean isFloatingPointNumber() {
      return true;
   }

   public boolean isFloat() {
      return true;
   }

   public boolean canConvertToInt() {
      return this._value >= -2.14748365E9F && this._value <= 2.14748365E9F;
   }

   public boolean canConvertToLong() {
      return this._value >= -9.223372E18F && this._value <= 9.223372E18F;
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
      return (long)this._value;
   }

   public float floatValue() {
      return this._value;
   }

   public double doubleValue() {
      return (double)this._value;
   }

   public BigDecimal decimalValue() {
      return BigDecimal.valueOf((double)this._value);
   }

   public BigInteger bigIntegerValue() {
      return this.decimalValue().toBigInteger();
   }

   public String asText() {
      return NumberOutput.toString(this._value);
   }

   public boolean isNaN() {
      return Float.isNaN(this._value) || Float.isInfinite(this._value);
   }

   public final void serialize(JsonGenerator g, SerializerProvider provider) throws IOException {
      g.writeNumber(this._value);
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o == null) {
         return false;
      } else if (o instanceof FloatNode) {
         float otherValue = ((FloatNode)o)._value;
         return Float.compare(this._value, otherValue) == 0;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Float.floatToIntBits(this._value);
   }
}

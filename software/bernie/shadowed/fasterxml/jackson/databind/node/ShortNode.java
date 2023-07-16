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

public class ShortNode extends NumericNode {
   protected final short _value;

   public ShortNode(short v) {
      this._value = v;
   }

   public static ShortNode valueOf(short l) {
      return new ShortNode(l);
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

   public boolean isShort() {
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
      return this._value;
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
      } else if (o instanceof ShortNode) {
         return ((ShortNode)o)._value == this._value;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this._value;
   }
}

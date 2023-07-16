package software.bernie.shadowed.fasterxml.jackson.databind.node;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonProcessingException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;

public class DecimalNode extends NumericNode {
   public static final DecimalNode ZERO;
   private static final BigDecimal MIN_INTEGER;
   private static final BigDecimal MAX_INTEGER;
   private static final BigDecimal MIN_LONG;
   private static final BigDecimal MAX_LONG;
   protected final BigDecimal _value;

   public DecimalNode(BigDecimal v) {
      this._value = v;
   }

   public static DecimalNode valueOf(BigDecimal d) {
      return new DecimalNode(d);
   }

   public JsonToken asToken() {
      return JsonToken.VALUE_NUMBER_FLOAT;
   }

   public JsonParser.NumberType numberType() {
      return JsonParser.NumberType.BIG_DECIMAL;
   }

   public boolean isFloatingPointNumber() {
      return true;
   }

   public boolean isBigDecimal() {
      return true;
   }

   public boolean canConvertToInt() {
      return this._value.compareTo(MIN_INTEGER) >= 0 && this._value.compareTo(MAX_INTEGER) <= 0;
   }

   public boolean canConvertToLong() {
      return this._value.compareTo(MIN_LONG) >= 0 && this._value.compareTo(MAX_LONG) <= 0;
   }

   public Number numberValue() {
      return this._value;
   }

   public short shortValue() {
      return this._value.shortValue();
   }

   public int intValue() {
      return this._value.intValue();
   }

   public long longValue() {
      return this._value.longValue();
   }

   public BigInteger bigIntegerValue() {
      return this._value.toBigInteger();
   }

   public float floatValue() {
      return this._value.floatValue();
   }

   public double doubleValue() {
      return this._value.doubleValue();
   }

   public BigDecimal decimalValue() {
      return this._value;
   }

   public String asText() {
      return this._value.toString();
   }

   public final void serialize(JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
      jgen.writeNumber(this._value);
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o == null) {
         return false;
      } else if (o instanceof DecimalNode) {
         return ((DecimalNode)o)._value.compareTo(this._value) == 0;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Double.valueOf(this.doubleValue()).hashCode();
   }

   static {
      ZERO = new DecimalNode(BigDecimal.ZERO);
      MIN_INTEGER = BigDecimal.valueOf(-2147483648L);
      MAX_INTEGER = BigDecimal.valueOf(2147483647L);
      MIN_LONG = BigDecimal.valueOf(Long.MIN_VALUE);
      MAX_LONG = BigDecimal.valueOf(Long.MAX_VALUE);
   }
}

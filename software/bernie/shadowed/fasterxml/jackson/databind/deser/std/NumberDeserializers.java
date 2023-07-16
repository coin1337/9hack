package software.bernie.shadowed.fasterxml.jackson.databind.deser.std;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.io.NumberInput;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.util.AccessPattern;

public class NumberDeserializers {
   private static final HashSet<String> _classNames = new HashSet();

   public static JsonDeserializer<?> find(Class<?> rawType, String clsName) {
      if (rawType.isPrimitive()) {
         if (rawType == Integer.TYPE) {
            return NumberDeserializers.IntegerDeserializer.primitiveInstance;
         }

         if (rawType == Boolean.TYPE) {
            return NumberDeserializers.BooleanDeserializer.primitiveInstance;
         }

         if (rawType == Long.TYPE) {
            return NumberDeserializers.LongDeserializer.primitiveInstance;
         }

         if (rawType == Double.TYPE) {
            return NumberDeserializers.DoubleDeserializer.primitiveInstance;
         }

         if (rawType == Character.TYPE) {
            return NumberDeserializers.CharacterDeserializer.primitiveInstance;
         }

         if (rawType == Byte.TYPE) {
            return NumberDeserializers.ByteDeserializer.primitiveInstance;
         }

         if (rawType == Short.TYPE) {
            return NumberDeserializers.ShortDeserializer.primitiveInstance;
         }

         if (rawType == Float.TYPE) {
            return NumberDeserializers.FloatDeserializer.primitiveInstance;
         }
      } else {
         if (!_classNames.contains(clsName)) {
            return null;
         }

         if (rawType == Integer.class) {
            return NumberDeserializers.IntegerDeserializer.wrapperInstance;
         }

         if (rawType == Boolean.class) {
            return NumberDeserializers.BooleanDeserializer.wrapperInstance;
         }

         if (rawType == Long.class) {
            return NumberDeserializers.LongDeserializer.wrapperInstance;
         }

         if (rawType == Double.class) {
            return NumberDeserializers.DoubleDeserializer.wrapperInstance;
         }

         if (rawType == Character.class) {
            return NumberDeserializers.CharacterDeserializer.wrapperInstance;
         }

         if (rawType == Byte.class) {
            return NumberDeserializers.ByteDeserializer.wrapperInstance;
         }

         if (rawType == Short.class) {
            return NumberDeserializers.ShortDeserializer.wrapperInstance;
         }

         if (rawType == Float.class) {
            return NumberDeserializers.FloatDeserializer.wrapperInstance;
         }

         if (rawType == Number.class) {
            return NumberDeserializers.NumberDeserializer.instance;
         }

         if (rawType == BigDecimal.class) {
            return NumberDeserializers.BigDecimalDeserializer.instance;
         }

         if (rawType == BigInteger.class) {
            return NumberDeserializers.BigIntegerDeserializer.instance;
         }
      }

      throw new IllegalArgumentException("Internal error: can't find deserializer for " + rawType.getName());
   }

   static {
      Class<?>[] numberTypes = new Class[]{Boolean.class, Byte.class, Short.class, Character.class, Integer.class, Long.class, Float.class, Double.class, Number.class, BigDecimal.class, BigInteger.class};
      Class[] arr$ = numberTypes;
      int len$ = numberTypes.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Class<?> cls = arr$[i$];
         _classNames.add(cls.getName());
      }

   }

   @JacksonStdImpl
   public static class BigDecimalDeserializer extends StdScalarDeserializer<BigDecimal> {
      public static final NumberDeserializers.BigDecimalDeserializer instance = new NumberDeserializers.BigDecimalDeserializer();

      public BigDecimalDeserializer() {
         super(BigDecimal.class);
      }

      public Object getEmptyValue(DeserializationContext ctxt) {
         return BigDecimal.ZERO;
      }

      public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         switch(p.getCurrentTokenId()) {
         case 3:
            return (BigDecimal)this._deserializeFromArray(p, ctxt);
         case 4:
         case 5:
         default:
            return (BigDecimal)ctxt.handleUnexpectedToken(this._valueClass, p);
         case 6:
            String text = p.getText().trim();
            if (this._isEmptyOrTextualNull(text)) {
               this._verifyNullForScalarCoercion(ctxt, text);
               return (BigDecimal)this.getNullValue(ctxt);
            } else {
               this._verifyStringForScalarCoercion(ctxt, text);

               try {
                  return new BigDecimal(text);
               } catch (IllegalArgumentException var5) {
                  return (BigDecimal)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid representation");
               }
            }
         case 7:
         case 8:
            return p.getDecimalValue();
         }
      }
   }

   @JacksonStdImpl
   public static class BigIntegerDeserializer extends StdScalarDeserializer<BigInteger> {
      public static final NumberDeserializers.BigIntegerDeserializer instance = new NumberDeserializers.BigIntegerDeserializer();

      public BigIntegerDeserializer() {
         super(BigInteger.class);
      }

      public Object getEmptyValue(DeserializationContext ctxt) {
         return BigInteger.ZERO;
      }

      public BigInteger deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         switch(p.getCurrentTokenId()) {
         case 3:
            return (BigInteger)this._deserializeFromArray(p, ctxt);
         case 6:
            String text = p.getText().trim();
            if (this._isEmptyOrTextualNull(text)) {
               this._verifyNullForScalarCoercion(ctxt, text);
               return (BigInteger)this.getNullValue(ctxt);
            } else {
               this._verifyStringForScalarCoercion(ctxt, text);

               try {
                  return new BigInteger(text);
               } catch (IllegalArgumentException var5) {
                  return (BigInteger)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid representation");
               }
            }
         case 7:
            switch(p.getNumberType()) {
            case INT:
            case LONG:
            case BIG_INTEGER:
               return p.getBigIntegerValue();
            }
         case 4:
         case 5:
         default:
            return (BigInteger)ctxt.handleUnexpectedToken(this._valueClass, p);
         case 8:
            if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
               this._failDoubleToIntCoercion(p, ctxt, "java.math.BigInteger");
            }

            return p.getDecimalValue().toBigInteger();
         }
      }
   }

   @JacksonStdImpl
   public static class NumberDeserializer extends StdScalarDeserializer<Object> {
      public static final NumberDeserializers.NumberDeserializer instance = new NumberDeserializers.NumberDeserializer();

      public NumberDeserializer() {
         super(Number.class);
      }

      public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         switch(p.getCurrentTokenId()) {
         case 3:
            return this._deserializeFromArray(p, ctxt);
         case 4:
         case 5:
         default:
            return ctxt.handleUnexpectedToken(this._valueClass, p);
         case 6:
            String text = p.getText().trim();
            if (text.length() == 0) {
               return this.getNullValue(ctxt);
            } else if (this._hasTextualNull(text)) {
               return this.getNullValue(ctxt);
            } else if (this._isPosInf(text)) {
               return Double.POSITIVE_INFINITY;
            } else if (this._isNegInf(text)) {
               return Double.NEGATIVE_INFINITY;
            } else if (this._isNaN(text)) {
               return Double.NaN;
            } else {
               this._verifyStringForScalarCoercion(ctxt, text);

               try {
                  if (!this._isIntNumber(text)) {
                     if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                        return new BigDecimal(text);
                     }

                     return Double.valueOf(text);
                  } else if (ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
                     return new BigInteger(text);
                  } else {
                     long value = Long.parseLong(text);
                     if (!ctxt.isEnabled(DeserializationFeature.USE_LONG_FOR_INTS) && value <= 2147483647L && value >= -2147483648L) {
                        return (int)value;
                     }

                     return value;
                  }
               } catch (IllegalArgumentException var6) {
                  return ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid number");
               }
            }
         case 7:
            if (ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS)) {
               return this._coerceIntegral(p, ctxt);
            }

            return p.getNumberValue();
         case 8:
            return ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS) && !p.isNaN() ? p.getDecimalValue() : p.getNumberValue();
         }
      }

      public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
         switch(p.getCurrentTokenId()) {
         case 6:
         case 7:
         case 8:
            return this.deserialize(p, ctxt);
         default:
            return typeDeserializer.deserializeTypedFromScalar(p, ctxt);
         }
      }
   }

   @JacksonStdImpl
   public static class DoubleDeserializer extends NumberDeserializers.PrimitiveOrWrapperDeserializer<Double> {
      private static final long serialVersionUID = 1L;
      static final NumberDeserializers.DoubleDeserializer primitiveInstance;
      static final NumberDeserializers.DoubleDeserializer wrapperInstance;

      public DoubleDeserializer(Class<Double> cls, Double nvl) {
         super(cls, nvl, 0.0D);
      }

      public Double deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         return this._parseDouble(p, ctxt);
      }

      public Double deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
         return this._parseDouble(p, ctxt);
      }

      protected final Double _parseDouble(JsonParser p, DeserializationContext ctxt) throws IOException {
         JsonToken t = p.getCurrentToken();
         if (t != JsonToken.VALUE_NUMBER_INT && t != JsonToken.VALUE_NUMBER_FLOAT) {
            if (t == JsonToken.VALUE_STRING) {
               String text = p.getText().trim();
               if (text.length() == 0) {
                  return (Double)this._coerceEmptyString(ctxt, this._primitive);
               } else if (this._hasTextualNull(text)) {
                  return (Double)this._coerceTextualNull(ctxt, this._primitive);
               } else {
                  switch(text.charAt(0)) {
                  case '-':
                     if (this._isNegInf(text)) {
                        return Double.NEGATIVE_INFINITY;
                     }
                     break;
                  case 'I':
                     if (this._isPosInf(text)) {
                        return Double.POSITIVE_INFINITY;
                     }
                     break;
                  case 'N':
                     if (this._isNaN(text)) {
                        return Double.NaN;
                     }
                  }

                  this._verifyStringForScalarCoercion(ctxt, text);

                  try {
                     return parseDouble(text);
                  } catch (IllegalArgumentException var6) {
                     return (Double)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Double value");
                  }
               }
            } else if (t == JsonToken.VALUE_NULL) {
               return (Double)this._coerceNullToken(ctxt, this._primitive);
            } else {
               return t == JsonToken.START_ARRAY ? (Double)this._deserializeFromArray(p, ctxt) : (Double)ctxt.handleUnexpectedToken(this._valueClass, p);
            }
         } else {
            return p.getDoubleValue();
         }
      }

      static {
         primitiveInstance = new NumberDeserializers.DoubleDeserializer(Double.TYPE, 0.0D);
         wrapperInstance = new NumberDeserializers.DoubleDeserializer(Double.class, (Double)null);
      }
   }

   @JacksonStdImpl
   public static class FloatDeserializer extends NumberDeserializers.PrimitiveOrWrapperDeserializer<Float> {
      private static final long serialVersionUID = 1L;
      static final NumberDeserializers.FloatDeserializer primitiveInstance;
      static final NumberDeserializers.FloatDeserializer wrapperInstance;

      public FloatDeserializer(Class<Float> cls, Float nvl) {
         super(cls, nvl, 0.0F);
      }

      public Float deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         return this._parseFloat(p, ctxt);
      }

      protected final Float _parseFloat(JsonParser p, DeserializationContext ctxt) throws IOException {
         JsonToken t = p.getCurrentToken();
         if (t != JsonToken.VALUE_NUMBER_FLOAT && t != JsonToken.VALUE_NUMBER_INT) {
            if (t == JsonToken.VALUE_STRING) {
               String text = p.getText().trim();
               if (text.length() == 0) {
                  return (Float)this._coerceEmptyString(ctxt, this._primitive);
               } else if (this._hasTextualNull(text)) {
                  return (Float)this._coerceTextualNull(ctxt, this._primitive);
               } else {
                  switch(text.charAt(0)) {
                  case '-':
                     if (this._isNegInf(text)) {
                        return Float.NEGATIVE_INFINITY;
                     }
                     break;
                  case 'I':
                     if (this._isPosInf(text)) {
                        return Float.POSITIVE_INFINITY;
                     }
                     break;
                  case 'N':
                     if (this._isNaN(text)) {
                        return Float.NaN;
                     }
                  }

                  this._verifyStringForScalarCoercion(ctxt, text);

                  try {
                     return Float.parseFloat(text);
                  } catch (IllegalArgumentException var6) {
                     return (Float)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Float value");
                  }
               }
            } else if (t == JsonToken.VALUE_NULL) {
               return (Float)this._coerceNullToken(ctxt, this._primitive);
            } else {
               return t == JsonToken.START_ARRAY ? (Float)this._deserializeFromArray(p, ctxt) : (Float)ctxt.handleUnexpectedToken(this._valueClass, p);
            }
         } else {
            return p.getFloatValue();
         }
      }

      static {
         primitiveInstance = new NumberDeserializers.FloatDeserializer(Float.TYPE, 0.0F);
         wrapperInstance = new NumberDeserializers.FloatDeserializer(Float.class, (Float)null);
      }
   }

   @JacksonStdImpl
   public static final class LongDeserializer extends NumberDeserializers.PrimitiveOrWrapperDeserializer<Long> {
      private static final long serialVersionUID = 1L;
      static final NumberDeserializers.LongDeserializer primitiveInstance;
      static final NumberDeserializers.LongDeserializer wrapperInstance;

      public LongDeserializer(Class<Long> cls, Long nvl) {
         super(cls, nvl, 0L);
      }

      public boolean isCachable() {
         return true;
      }

      public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         return p.hasToken(JsonToken.VALUE_NUMBER_INT) ? p.getLongValue() : this._parseLong(p, ctxt);
      }

      protected final Long _parseLong(JsonParser p, DeserializationContext ctxt) throws IOException {
         switch(p.getCurrentTokenId()) {
         case 3:
            return (Long)this._deserializeFromArray(p, ctxt);
         case 4:
         case 5:
         case 9:
         case 10:
         default:
            return (Long)ctxt.handleUnexpectedToken(this._valueClass, p);
         case 6:
            String text = p.getText().trim();
            if (text.length() == 0) {
               return (Long)this._coerceEmptyString(ctxt, this._primitive);
            } else if (this._hasTextualNull(text)) {
               return (Long)this._coerceTextualNull(ctxt, this._primitive);
            } else {
               this._verifyStringForScalarCoercion(ctxt, text);

               try {
                  return NumberInput.parseLong(text);
               } catch (IllegalArgumentException var5) {
                  return (Long)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Long value");
               }
            }
         case 7:
            return p.getLongValue();
         case 8:
            if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
               this._failDoubleToIntCoercion(p, ctxt, "Long");
            }

            return p.getValueAsLong();
         case 11:
            return (Long)this._coerceNullToken(ctxt, this._primitive);
         }
      }

      static {
         primitiveInstance = new NumberDeserializers.LongDeserializer(Long.TYPE, 0L);
         wrapperInstance = new NumberDeserializers.LongDeserializer(Long.class, (Long)null);
      }
   }

   @JacksonStdImpl
   public static final class IntegerDeserializer extends NumberDeserializers.PrimitiveOrWrapperDeserializer<Integer> {
      private static final long serialVersionUID = 1L;
      static final NumberDeserializers.IntegerDeserializer primitiveInstance;
      static final NumberDeserializers.IntegerDeserializer wrapperInstance;

      public IntegerDeserializer(Class<Integer> cls, Integer nvl) {
         super(cls, nvl, 0);
      }

      public boolean isCachable() {
         return true;
      }

      public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         return p.hasToken(JsonToken.VALUE_NUMBER_INT) ? p.getIntValue() : this._parseInteger(p, ctxt);
      }

      public Integer deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
         return p.hasToken(JsonToken.VALUE_NUMBER_INT) ? p.getIntValue() : this._parseInteger(p, ctxt);
      }

      protected final Integer _parseInteger(JsonParser p, DeserializationContext ctxt) throws IOException {
         switch(p.getCurrentTokenId()) {
         case 3:
            return (Integer)this._deserializeFromArray(p, ctxt);
         case 4:
         case 5:
         case 9:
         case 10:
         default:
            return (Integer)ctxt.handleUnexpectedToken(this._valueClass, p);
         case 6:
            String text = p.getText().trim();
            int len = text.length();
            if (len == 0) {
               return (Integer)this._coerceEmptyString(ctxt, this._primitive);
            } else if (this._hasTextualNull(text)) {
               return (Integer)this._coerceTextualNull(ctxt, this._primitive);
            } else {
               this._verifyStringForScalarCoercion(ctxt, text);

               try {
                  if (len > 9) {
                     long l = Long.parseLong(text);
                     if (this._intOverflow(l)) {
                        return (Integer)ctxt.handleWeirdStringValue(this._valueClass, text, String.format("Overflow: numeric value (%s) out of range of Integer (%d - %d)", text, Integer.MIN_VALUE, Integer.MAX_VALUE));
                     }

                     return (int)l;
                  }

                  return NumberInput.parseInt(text);
               } catch (IllegalArgumentException var7) {
                  return (Integer)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Integer value");
               }
            }
         case 7:
            return p.getIntValue();
         case 8:
            if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
               this._failDoubleToIntCoercion(p, ctxt, "Integer");
            }

            return p.getValueAsInt();
         case 11:
            return (Integer)this._coerceNullToken(ctxt, this._primitive);
         }
      }

      static {
         primitiveInstance = new NumberDeserializers.IntegerDeserializer(Integer.TYPE, 0);
         wrapperInstance = new NumberDeserializers.IntegerDeserializer(Integer.class, (Integer)null);
      }
   }

   @JacksonStdImpl
   public static class CharacterDeserializer extends NumberDeserializers.PrimitiveOrWrapperDeserializer<Character> {
      private static final long serialVersionUID = 1L;
      static final NumberDeserializers.CharacterDeserializer primitiveInstance;
      static final NumberDeserializers.CharacterDeserializer wrapperInstance;

      public CharacterDeserializer(Class<Character> cls, Character nvl) {
         super(cls, nvl, '\u0000');
      }

      public Character deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         switch(p.getCurrentTokenId()) {
         case 3:
            return (Character)this._deserializeFromArray(p, ctxt);
         case 4:
         case 5:
         case 8:
         case 9:
         case 10:
         default:
            break;
         case 6:
            String text = p.getText();
            if (text.length() == 1) {
               return text.charAt(0);
            }

            if (text.length() == 0) {
               return (Character)this._coerceEmptyString(ctxt, this._primitive);
            }
            break;
         case 7:
            this._verifyNumberForScalarCoercion(ctxt, p);
            int value = p.getIntValue();
            if (value >= 0 && value <= 65535) {
               return (char)value;
            }
            break;
         case 11:
            return (Character)this._coerceNullToken(ctxt, this._primitive);
         }

         return (Character)ctxt.handleUnexpectedToken(this._valueClass, p);
      }

      static {
         primitiveInstance = new NumberDeserializers.CharacterDeserializer(Character.TYPE, '\u0000');
         wrapperInstance = new NumberDeserializers.CharacterDeserializer(Character.class, (Character)null);
      }
   }

   @JacksonStdImpl
   public static class ShortDeserializer extends NumberDeserializers.PrimitiveOrWrapperDeserializer<Short> {
      private static final long serialVersionUID = 1L;
      static final NumberDeserializers.ShortDeserializer primitiveInstance;
      static final NumberDeserializers.ShortDeserializer wrapperInstance;

      public ShortDeserializer(Class<Short> cls, Short nvl) {
         super(cls, nvl, Short.valueOf((short)0));
      }

      public Short deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         return this._parseShort(p, ctxt);
      }

      protected Short _parseShort(JsonParser p, DeserializationContext ctxt) throws IOException {
         JsonToken t = p.getCurrentToken();
         if (t == JsonToken.VALUE_NUMBER_INT) {
            return p.getShortValue();
         } else if (t == JsonToken.VALUE_STRING) {
            String text = p.getText().trim();
            int len = text.length();
            if (len == 0) {
               return (Short)this._coerceEmptyString(ctxt, this._primitive);
            } else if (this._hasTextualNull(text)) {
               return (Short)this._coerceTextualNull(ctxt, this._primitive);
            } else {
               this._verifyStringForScalarCoercion(ctxt, text);

               int value;
               try {
                  value = NumberInput.parseInt(text);
               } catch (IllegalArgumentException var8) {
                  return (Short)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Short value");
               }

               return this._shortOverflow(value) ? (Short)ctxt.handleWeirdStringValue(this._valueClass, text, "overflow, value cannot be represented as 16-bit value") : (short)value;
            }
         } else if (t == JsonToken.VALUE_NUMBER_FLOAT) {
            if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
               this._failDoubleToIntCoercion(p, ctxt, "Short");
            }

            return p.getShortValue();
         } else if (t == JsonToken.VALUE_NULL) {
            return (Short)this._coerceNullToken(ctxt, this._primitive);
         } else {
            return t == JsonToken.START_ARRAY ? (Short)this._deserializeFromArray(p, ctxt) : (Short)ctxt.handleUnexpectedToken(this._valueClass, p);
         }
      }

      static {
         primitiveInstance = new NumberDeserializers.ShortDeserializer(Short.TYPE, Short.valueOf((short)0));
         wrapperInstance = new NumberDeserializers.ShortDeserializer(Short.class, (Short)null);
      }
   }

   @JacksonStdImpl
   public static class ByteDeserializer extends NumberDeserializers.PrimitiveOrWrapperDeserializer<Byte> {
      private static final long serialVersionUID = 1L;
      static final NumberDeserializers.ByteDeserializer primitiveInstance;
      static final NumberDeserializers.ByteDeserializer wrapperInstance;

      public ByteDeserializer(Class<Byte> cls, Byte nvl) {
         super(cls, nvl, (byte)0);
      }

      public Byte deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         return p.hasToken(JsonToken.VALUE_NUMBER_INT) ? p.getByteValue() : this._parseByte(p, ctxt);
      }

      protected Byte _parseByte(JsonParser p, DeserializationContext ctxt) throws IOException {
         JsonToken t = p.getCurrentToken();
         if (t == JsonToken.VALUE_STRING) {
            String text = p.getText().trim();
            if (this._hasTextualNull(text)) {
               return (Byte)this._coerceTextualNull(ctxt, this._primitive);
            } else {
               int len = text.length();
               if (len == 0) {
                  return (Byte)this._coerceEmptyString(ctxt, this._primitive);
               } else {
                  this._verifyStringForScalarCoercion(ctxt, text);

                  int value;
                  try {
                     value = NumberInput.parseInt(text);
                  } catch (IllegalArgumentException var8) {
                     return (Byte)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Byte value");
                  }

                  return this._byteOverflow(value) ? (Byte)ctxt.handleWeirdStringValue(this._valueClass, text, "overflow, value cannot be represented as 8-bit value") : (byte)value;
               }
            }
         } else if (t == JsonToken.VALUE_NUMBER_FLOAT) {
            if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
               this._failDoubleToIntCoercion(p, ctxt, "Byte");
            }

            return p.getByteValue();
         } else if (t == JsonToken.VALUE_NULL) {
            return (Byte)this._coerceNullToken(ctxt, this._primitive);
         } else if (t == JsonToken.START_ARRAY) {
            return (Byte)this._deserializeFromArray(p, ctxt);
         } else {
            return t == JsonToken.VALUE_NUMBER_INT ? p.getByteValue() : (Byte)ctxt.handleUnexpectedToken(this._valueClass, p);
         }
      }

      static {
         primitiveInstance = new NumberDeserializers.ByteDeserializer(Byte.TYPE, (byte)0);
         wrapperInstance = new NumberDeserializers.ByteDeserializer(Byte.class, (Byte)null);
      }
   }

   @JacksonStdImpl
   public static final class BooleanDeserializer extends NumberDeserializers.PrimitiveOrWrapperDeserializer<Boolean> {
      private static final long serialVersionUID = 1L;
      static final NumberDeserializers.BooleanDeserializer primitiveInstance;
      static final NumberDeserializers.BooleanDeserializer wrapperInstance;

      public BooleanDeserializer(Class<Boolean> cls, Boolean nvl) {
         super(cls, nvl, Boolean.FALSE);
      }

      public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         JsonToken t = p.getCurrentToken();
         if (t == JsonToken.VALUE_TRUE) {
            return Boolean.TRUE;
         } else {
            return t == JsonToken.VALUE_FALSE ? Boolean.FALSE : this._parseBoolean(p, ctxt);
         }
      }

      public Boolean deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
         JsonToken t = p.getCurrentToken();
         if (t == JsonToken.VALUE_TRUE) {
            return Boolean.TRUE;
         } else {
            return t == JsonToken.VALUE_FALSE ? Boolean.FALSE : this._parseBoolean(p, ctxt);
         }
      }

      protected final Boolean _parseBoolean(JsonParser p, DeserializationContext ctxt) throws IOException {
         JsonToken t = p.getCurrentToken();
         if (t == JsonToken.VALUE_NULL) {
            return (Boolean)this._coerceNullToken(ctxt, this._primitive);
         } else if (t == JsonToken.START_ARRAY) {
            return (Boolean)this._deserializeFromArray(p, ctxt);
         } else if (t == JsonToken.VALUE_NUMBER_INT) {
            return this._parseBooleanFromInt(p, ctxt);
         } else if (t == JsonToken.VALUE_STRING) {
            String text = p.getText().trim();
            if (!"true".equals(text) && !"True".equals(text)) {
               if (!"false".equals(text) && !"False".equals(text)) {
                  if (text.length() == 0) {
                     return (Boolean)this._coerceEmptyString(ctxt, this._primitive);
                  } else {
                     return this._hasTextualNull(text) ? (Boolean)this._coerceTextualNull(ctxt, this._primitive) : (Boolean)ctxt.handleWeirdStringValue(this._valueClass, text, "only \"true\" or \"false\" recognized");
                  }
               } else {
                  this._verifyStringForScalarCoercion(ctxt, text);
                  return Boolean.FALSE;
               }
            } else {
               this._verifyStringForScalarCoercion(ctxt, text);
               return Boolean.TRUE;
            }
         } else if (t == JsonToken.VALUE_TRUE) {
            return Boolean.TRUE;
         } else {
            return t == JsonToken.VALUE_FALSE ? Boolean.FALSE : (Boolean)ctxt.handleUnexpectedToken(this._valueClass, p);
         }
      }

      static {
         primitiveInstance = new NumberDeserializers.BooleanDeserializer(Boolean.TYPE, Boolean.FALSE);
         wrapperInstance = new NumberDeserializers.BooleanDeserializer(Boolean.class, (Boolean)null);
      }
   }

   protected abstract static class PrimitiveOrWrapperDeserializer<T> extends StdScalarDeserializer<T> {
      private static final long serialVersionUID = 1L;
      protected final T _nullValue;
      protected final T _emptyValue;
      protected final boolean _primitive;

      protected PrimitiveOrWrapperDeserializer(Class<T> vc, T nvl, T empty) {
         super(vc);
         this._nullValue = nvl;
         this._emptyValue = empty;
         this._primitive = vc.isPrimitive();
      }

      public AccessPattern getNullAccessPattern() {
         if (this._primitive) {
            return AccessPattern.DYNAMIC;
         } else {
            return this._nullValue == null ? AccessPattern.ALWAYS_NULL : AccessPattern.CONSTANT;
         }
      }

      public final T getNullValue(DeserializationContext ctxt) throws JsonMappingException {
         if (this._primitive && ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
            ctxt.reportInputMismatch((JsonDeserializer)this, "Cannot map `null` into type %s (set DeserializationConfig.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES to 'false' to allow)", this.handledType().toString());
         }

         return this._nullValue;
      }

      public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
         return this._emptyValue;
      }
   }
}

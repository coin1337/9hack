package software.bernie.shadowed.fasterxml.jackson.databind.deser.std;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;
import software.bernie.shadowed.fasterxml.jackson.annotation.Nulls;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParseException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.io.NumberInput;
import software.bernie.shadowed.fasterxml.jackson.databind.AnnotationIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.KeyDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.MapperFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyMetadata;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.NullValueProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.SettableBeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ValueInstantiator;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.NullsAsEmptyProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.impl.NullsFailProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMember;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.util.AccessPattern;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;
import software.bernie.shadowed.fasterxml.jackson.databind.util.Converter;

public abstract class StdDeserializer<T> extends JsonDeserializer<T> implements Serializable {
   private static final long serialVersionUID = 1L;
   protected static final int F_MASK_INT_COERCIONS;
   protected static final int F_MASK_ACCEPT_ARRAYS;
   protected final Class<?> _valueClass;

   protected StdDeserializer(Class<?> vc) {
      this._valueClass = vc;
   }

   protected StdDeserializer(JavaType valueType) {
      this._valueClass = valueType.getRawClass();
   }

   protected StdDeserializer(StdDeserializer<?> src) {
      this._valueClass = src._valueClass;
   }

   public Class<?> handledType() {
      return this._valueClass;
   }

   /** @deprecated */
   @Deprecated
   public final Class<?> getValueClass() {
      return this._valueClass;
   }

   public JavaType getValueType() {
      return null;
   }

   protected boolean isDefaultDeserializer(JsonDeserializer<?> deserializer) {
      return ClassUtil.isJacksonStdImpl((Object)deserializer);
   }

   protected boolean isDefaultKeyDeserializer(KeyDeserializer keyDeser) {
      return ClassUtil.isJacksonStdImpl((Object)keyDeser);
   }

   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
      return typeDeserializer.deserializeTypedFromAny(p, ctxt);
   }

   protected final boolean _parseBooleanPrimitive(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonToken t = p.getCurrentToken();
      if (t == JsonToken.VALUE_TRUE) {
         return true;
      } else if (t == JsonToken.VALUE_FALSE) {
         return false;
      } else if (t == JsonToken.VALUE_NULL) {
         this._verifyNullForPrimitive(ctxt);
         return false;
      } else if (t == JsonToken.VALUE_NUMBER_INT) {
         return this._parseBooleanFromInt(p, ctxt);
      } else if (t == JsonToken.VALUE_STRING) {
         String text = p.getText().trim();
         if (!"true".equals(text) && !"True".equals(text)) {
            if (!"false".equals(text) && !"False".equals(text)) {
               if (this._isEmptyOrTextualNull(text)) {
                  this._verifyNullForPrimitiveCoercion(ctxt, text);
                  return false;
               } else {
                  Boolean b = (Boolean)ctxt.handleWeirdStringValue(this._valueClass, text, "only \"true\" or \"false\" recognized");
                  return Boolean.TRUE.equals(b);
               }
            } else {
               return false;
            }
         } else {
            return true;
         }
      } else if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
         p.nextToken();
         boolean parsed = this._parseBooleanPrimitive(p, ctxt);
         this._verifyEndArrayForSingle(p, ctxt);
         return parsed;
      } else {
         return (Boolean)ctxt.handleUnexpectedToken(this._valueClass, p);
      }
   }

   protected boolean _parseBooleanFromInt(JsonParser p, DeserializationContext ctxt) throws IOException {
      this._verifyNumberForScalarCoercion(ctxt, p);
      return !"0".equals(p.getText());
   }

   protected final byte _parseBytePrimitive(JsonParser p, DeserializationContext ctxt) throws IOException {
      int value = this._parseIntPrimitive(p, ctxt);
      if (this._byteOverflow(value)) {
         Number v = (Number)ctxt.handleWeirdStringValue(this._valueClass, String.valueOf(value), "overflow, value cannot be represented as 8-bit value");
         return this._nonNullNumber(v).byteValue();
      } else {
         return (byte)value;
      }
   }

   protected final short _parseShortPrimitive(JsonParser p, DeserializationContext ctxt) throws IOException {
      int value = this._parseIntPrimitive(p, ctxt);
      if (this._shortOverflow(value)) {
         Number v = (Number)ctxt.handleWeirdStringValue(this._valueClass, String.valueOf(value), "overflow, value cannot be represented as 16-bit value");
         return this._nonNullNumber(v).shortValue();
      } else {
         return (short)value;
      }
   }

   protected final int _parseIntPrimitive(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
         return p.getIntValue();
      } else {
         switch(p.getCurrentTokenId()) {
         case 3:
            if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
               p.nextToken();
               int parsed = this._parseIntPrimitive(p, ctxt);
               this._verifyEndArrayForSingle(p, ctxt);
               return parsed;
            }
         case 4:
         case 5:
         case 7:
         case 9:
         case 10:
         default:
            return ((Number)ctxt.handleUnexpectedToken(this._valueClass, p)).intValue();
         case 6:
            String text = p.getText().trim();
            if (this._isEmptyOrTextualNull(text)) {
               this._verifyNullForPrimitiveCoercion(ctxt, text);
               return 0;
            }

            return this._parseIntPrimitive(ctxt, text);
         case 8:
            if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
               this._failDoubleToIntCoercion(p, ctxt, "int");
            }

            return p.getValueAsInt();
         case 11:
            this._verifyNullForPrimitive(ctxt);
            return 0;
         }
      }
   }

   protected final int _parseIntPrimitive(DeserializationContext ctxt, String text) throws IOException {
      try {
         if (text.length() > 9) {
            long l = Long.parseLong(text);
            if (this._intOverflow(l)) {
               Number v = (Number)ctxt.handleWeirdStringValue(this._valueClass, text, "Overflow: numeric value (%s) out of range of int (%d -%d)", text, Integer.MIN_VALUE, Integer.MAX_VALUE);
               return this._nonNullNumber(v).intValue();
            } else {
               return (int)l;
            }
         } else {
            return NumberInput.parseInt(text);
         }
      } catch (IllegalArgumentException var6) {
         Number v = (Number)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid int value");
         return this._nonNullNumber(v).intValue();
      }
   }

   protected final long _parseLongPrimitive(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
         return p.getLongValue();
      } else {
         switch(p.getCurrentTokenId()) {
         case 3:
            if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
               p.nextToken();
               long parsed = this._parseLongPrimitive(p, ctxt);
               this._verifyEndArrayForSingle(p, ctxt);
               return parsed;
            }
         case 4:
         case 5:
         case 7:
         case 9:
         case 10:
         default:
            return ((Number)ctxt.handleUnexpectedToken(this._valueClass, p)).longValue();
         case 6:
            String text = p.getText().trim();
            if (this._isEmptyOrTextualNull(text)) {
               this._verifyNullForPrimitiveCoercion(ctxt, text);
               return 0L;
            }

            return this._parseLongPrimitive(ctxt, text);
         case 8:
            if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
               this._failDoubleToIntCoercion(p, ctxt, "long");
            }

            return p.getValueAsLong();
         case 11:
            this._verifyNullForPrimitive(ctxt);
            return 0L;
         }
      }
   }

   protected final long _parseLongPrimitive(DeserializationContext ctxt, String text) throws IOException {
      try {
         return NumberInput.parseLong(text);
      } catch (IllegalArgumentException var4) {
         Number v = (Number)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid long value");
         return this._nonNullNumber(v).longValue();
      }
   }

   protected final float _parseFloatPrimitive(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
         return p.getFloatValue();
      } else {
         switch(p.getCurrentTokenId()) {
         case 3:
            if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
               p.nextToken();
               float parsed = this._parseFloatPrimitive(p, ctxt);
               this._verifyEndArrayForSingle(p, ctxt);
               return parsed;
            }
         case 4:
         case 5:
         case 8:
         case 9:
         case 10:
         default:
            return ((Number)ctxt.handleUnexpectedToken(this._valueClass, p)).floatValue();
         case 6:
            String text = p.getText().trim();
            if (this._isEmptyOrTextualNull(text)) {
               this._verifyNullForPrimitiveCoercion(ctxt, text);
               return 0.0F;
            }

            return this._parseFloatPrimitive(ctxt, text);
         case 7:
            return p.getFloatValue();
         case 11:
            this._verifyNullForPrimitive(ctxt);
            return 0.0F;
         }
      }
   }

   protected final float _parseFloatPrimitive(DeserializationContext ctxt, String text) throws IOException {
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

      try {
         return Float.parseFloat(text);
      } catch (IllegalArgumentException var4) {
         Number v = (Number)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid float value");
         return this._nonNullNumber(v).floatValue();
      }
   }

   protected final double _parseDoublePrimitive(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
         return p.getDoubleValue();
      } else {
         switch(p.getCurrentTokenId()) {
         case 3:
            if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
               p.nextToken();
               double parsed = this._parseDoublePrimitive(p, ctxt);
               this._verifyEndArrayForSingle(p, ctxt);
               return parsed;
            }
         case 4:
         case 5:
         case 8:
         case 9:
         case 10:
         default:
            return ((Number)ctxt.handleUnexpectedToken(this._valueClass, p)).doubleValue();
         case 6:
            String text = p.getText().trim();
            if (this._isEmptyOrTextualNull(text)) {
               this._verifyNullForPrimitiveCoercion(ctxt, text);
               return 0.0D;
            }

            return this._parseDoublePrimitive(ctxt, text);
         case 7:
            return p.getDoubleValue();
         case 11:
            this._verifyNullForPrimitive(ctxt);
            return 0.0D;
         }
      }
   }

   protected final double _parseDoublePrimitive(DeserializationContext ctxt, String text) throws IOException {
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

      try {
         return parseDouble(text);
      } catch (IllegalArgumentException var4) {
         Number v = (Number)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid double value (as String to convert)");
         return this._nonNullNumber(v).doubleValue();
      }
   }

   protected Date _parseDate(JsonParser p, DeserializationContext ctxt) throws IOException {
      switch(p.getCurrentTokenId()) {
      case 3:
         return this._parseDateFromArray(p, ctxt);
      case 4:
      case 5:
      case 8:
      case 9:
      case 10:
      default:
         return (Date)ctxt.handleUnexpectedToken(this._valueClass, p);
      case 6:
         return this._parseDate(p.getText().trim(), ctxt);
      case 7:
         long ts;
         try {
            ts = p.getLongValue();
         } catch (JsonParseException var7) {
            Number v = (Number)ctxt.handleWeirdNumberValue(this._valueClass, p.getNumberValue(), "not a valid 64-bit long for creating `java.util.Date`");
            ts = v.longValue();
         }

         return new Date(ts);
      case 11:
         return (Date)this.getNullValue(ctxt);
      }
   }

   protected Date _parseDateFromArray(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonToken t;
      if (ctxt.hasSomeOfFeatures(F_MASK_ACCEPT_ARRAYS)) {
         t = p.nextToken();
         if (t == JsonToken.END_ARRAY && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)) {
            return (Date)this.getNullValue(ctxt);
         }

         if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            Date parsed = this._parseDate(p, ctxt);
            this._verifyEndArrayForSingle(p, ctxt);
            return parsed;
         }
      } else {
         t = p.getCurrentToken();
      }

      return (Date)ctxt.handleUnexpectedToken(this._valueClass, t, p, (String)null);
   }

   protected Date _parseDate(String value, DeserializationContext ctxt) throws IOException {
      try {
         return this._isEmptyOrTextualNull(value) ? (Date)this.getNullValue(ctxt) : ctxt.parseDate(value);
      } catch (IllegalArgumentException var4) {
         return (Date)ctxt.handleWeirdStringValue(this._valueClass, value, "not a valid representation (error: %s)", var4.getMessage());
      }
   }

   protected static final double parseDouble(String numStr) throws NumberFormatException {
      return "2.2250738585072012e-308".equals(numStr) ? 2.2250738585072014E-308D : Double.parseDouble(numStr);
   }

   protected final String _parseString(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonToken t = p.getCurrentToken();
      if (t == JsonToken.VALUE_STRING) {
         return p.getText();
      } else {
         String value = p.getValueAsString();
         return value != null ? value : (String)ctxt.handleUnexpectedToken(String.class, p);
      }
   }

   protected T _deserializeFromEmpty(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonToken t = p.getCurrentToken();
      if (t == JsonToken.START_ARRAY) {
         if (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)) {
            t = p.nextToken();
            if (t == JsonToken.END_ARRAY) {
               return null;
            }

            return ctxt.handleUnexpectedToken(this.handledType(), p);
         }
      } else if (t == JsonToken.VALUE_STRING && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
         String str = p.getText().trim();
         if (str.isEmpty()) {
            return null;
         }
      }

      return ctxt.handleUnexpectedToken(this.handledType(), p);
   }

   protected boolean _hasTextualNull(String value) {
      return "null".equals(value);
   }

   protected boolean _isEmptyOrTextualNull(String value) {
      return value.isEmpty() || "null".equals(value);
   }

   protected final boolean _isNegInf(String text) {
      return "-Infinity".equals(text) || "-INF".equals(text);
   }

   protected final boolean _isPosInf(String text) {
      return "Infinity".equals(text) || "INF".equals(text);
   }

   protected final boolean _isNaN(String text) {
      return "NaN".equals(text);
   }

   protected T _deserializeFromArray(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonToken t;
      Object parsed;
      if (ctxt.hasSomeOfFeatures(F_MASK_ACCEPT_ARRAYS)) {
         t = p.nextToken();
         if (t == JsonToken.END_ARRAY && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)) {
            return this.getNullValue(ctxt);
         }

         if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            parsed = this.deserialize(p, ctxt);
            if (p.nextToken() != JsonToken.END_ARRAY) {
               this.handleMissingEndArrayForSingle(p, ctxt);
            }

            return parsed;
         }
      } else {
         t = p.getCurrentToken();
      }

      parsed = ctxt.handleUnexpectedToken(this._valueClass, t, p, (String)null);
      return parsed;
   }

   protected T _deserializeWrappedValue(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (p.hasToken(JsonToken.START_ARRAY)) {
         String msg = String.format("Cannot deserialize instance of %s out of %s token: nested Arrays not allowed with %s", ClassUtil.nameOf(this._valueClass), JsonToken.START_ARRAY, "DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS");
         T result = ctxt.handleUnexpectedToken(this._valueClass, p.getCurrentToken(), p, msg);
         return result;
      } else {
         return this.deserialize(p, ctxt);
      }
   }

   protected void _failDoubleToIntCoercion(JsonParser p, DeserializationContext ctxt, String type) throws IOException {
      ctxt.reportInputMismatch(this.handledType(), "Cannot coerce a floating-point value ('%s') into %s (enable `DeserializationFeature.ACCEPT_FLOAT_AS_INT` to allow)", p.getValueAsString(), type);
   }

   protected Object _coerceIntegral(JsonParser p, DeserializationContext ctxt) throws IOException {
      int feats = ctxt.getDeserializationFeatures();
      if (DeserializationFeature.USE_BIG_INTEGER_FOR_INTS.enabledIn(feats)) {
         return p.getBigIntegerValue();
      } else {
         return DeserializationFeature.USE_LONG_FOR_INTS.enabledIn(feats) ? p.getLongValue() : p.getBigIntegerValue();
      }
   }

   protected Object _coerceNullToken(DeserializationContext ctxt, boolean isPrimitive) throws JsonMappingException {
      if (isPrimitive) {
         this._verifyNullForPrimitive(ctxt);
      }

      return this.getNullValue(ctxt);
   }

   protected Object _coerceTextualNull(DeserializationContext ctxt, boolean isPrimitive) throws JsonMappingException {
      Object feat;
      boolean enable;
      if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
         feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
         enable = true;
      } else {
         if (!isPrimitive || !ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
            return this.getNullValue(ctxt);
         }

         feat = DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
         enable = false;
      }

      this._reportFailedNullCoerce(ctxt, enable, (Enum)feat, "String \"null\"");
      return null;
   }

   protected Object _coerceEmptyString(DeserializationContext ctxt, boolean isPrimitive) throws JsonMappingException {
      Object feat;
      boolean enable;
      if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
         feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
         enable = true;
      } else {
         if (!isPrimitive || !ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
            return this.getNullValue(ctxt);
         }

         feat = DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
         enable = false;
      }

      this._reportFailedNullCoerce(ctxt, enable, (Enum)feat, "empty String (\"\")");
      return null;
   }

   protected final void _verifyNullForPrimitive(DeserializationContext ctxt) throws JsonMappingException {
      if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
         ctxt.reportInputMismatch((JsonDeserializer)this, "Cannot coerce `null` %s (disable `DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES` to allow)", this._coercedTypeDesc());
      }

   }

   protected final void _verifyNullForPrimitiveCoercion(DeserializationContext ctxt, String str) throws JsonMappingException {
      Object feat;
      boolean enable;
      if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
         feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
         enable = true;
      } else {
         if (!ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
            return;
         }

         feat = DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
         enable = false;
      }

      String strDesc = str.isEmpty() ? "empty String (\"\")" : String.format("String \"%s\"", str);
      this._reportFailedNullCoerce(ctxt, enable, (Enum)feat, strDesc);
   }

   protected final void _verifyNullForScalarCoercion(DeserializationContext ctxt, String str) throws JsonMappingException {
      if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
         String strDesc = str.isEmpty() ? "empty String (\"\")" : String.format("String \"%s\"", str);
         this._reportFailedNullCoerce(ctxt, true, MapperFeature.ALLOW_COERCION_OF_SCALARS, strDesc);
      }

   }

   protected void _verifyStringForScalarCoercion(DeserializationContext ctxt, String str) throws JsonMappingException {
      MapperFeature feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
      if (!ctxt.isEnabled(feat)) {
         ctxt.reportInputMismatch((JsonDeserializer)this, "Cannot coerce String \"%s\" %s (enable `%s.%s` to allow)", str, this._coercedTypeDesc(), feat.getClass().getSimpleName(), feat.name());
      }

   }

   protected void _verifyNumberForScalarCoercion(DeserializationContext ctxt, JsonParser p) throws IOException {
      MapperFeature feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
      if (!ctxt.isEnabled(feat)) {
         String valueDesc = p.getText();
         ctxt.reportInputMismatch((JsonDeserializer)this, "Cannot coerce Number (%s) %s (enable `%s.%s` to allow)", valueDesc, this._coercedTypeDesc(), feat.getClass().getSimpleName(), feat.name());
      }

   }

   protected void _reportFailedNullCoerce(DeserializationContext ctxt, boolean state, Enum<?> feature, String inputDesc) throws JsonMappingException {
      String enableDesc = state ? "enable" : "disable";
      ctxt.reportInputMismatch((JsonDeserializer)this, "Cannot coerce %s to Null value %s (%s `%s.%s` to allow)", inputDesc, this._coercedTypeDesc(), enableDesc, feature.getClass().getSimpleName(), feature.name());
   }

   protected String _coercedTypeDesc() {
      JavaType t = this.getValueType();
      boolean structured;
      String typeDesc;
      if (t != null && !t.isPrimitive()) {
         structured = t.isContainerType() || t.isReferenceType();
         typeDesc = "'" + t.toString() + "'";
      } else {
         Class<?> cls = this.handledType();
         structured = cls.isArray() || Collection.class.isAssignableFrom(cls) || Map.class.isAssignableFrom(cls);
         typeDesc = ClassUtil.nameOf(cls);
      }

      return structured ? "as content of type " + typeDesc : "for type " + typeDesc;
   }

   protected JsonDeserializer<Object> findDeserializer(DeserializationContext ctxt, JavaType type, BeanProperty property) throws JsonMappingException {
      return ctxt.findContextualValueDeserializer(type, property);
   }

   protected final boolean _isIntNumber(String text) {
      int len = text.length();
      if (len <= 0) {
         return false;
      } else {
         char c = text.charAt(0);

         for(int i = c != '-' && c != '+' ? 0 : 1; i < len; ++i) {
            int ch = text.charAt(i);
            if (ch > '9' || ch < '0') {
               return false;
            }
         }

         return true;
      }
   }

   protected JsonDeserializer<?> findConvertingContentDeserializer(DeserializationContext ctxt, BeanProperty prop, JsonDeserializer<?> existingDeserializer) throws JsonMappingException {
      AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
      if (_neitherNull(intr, prop)) {
         AnnotatedMember member = prop.getMember();
         if (member != null) {
            Object convDef = intr.findDeserializationContentConverter(member);
            if (convDef != null) {
               Converter<Object, Object> conv = ctxt.converterInstance(prop.getMember(), convDef);
               JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
               if (existingDeserializer == null) {
                  existingDeserializer = ctxt.findContextualValueDeserializer(delegateType, prop);
               }

               return new StdDelegatingDeserializer(conv, delegateType, existingDeserializer);
            }
         }
      }

      return existingDeserializer;
   }

   protected JsonFormat.Value findFormatOverrides(DeserializationContext ctxt, BeanProperty prop, Class<?> typeForDefaults) {
      return prop != null ? prop.findPropertyFormat(ctxt.getConfig(), typeForDefaults) : ctxt.getDefaultPropertyFormat(typeForDefaults);
   }

   protected Boolean findFormatFeature(DeserializationContext ctxt, BeanProperty prop, Class<?> typeForDefaults, JsonFormat.Feature feat) {
      JsonFormat.Value format = this.findFormatOverrides(ctxt, prop, typeForDefaults);
      return format != null ? format.getFeature(feat) : null;
   }

   protected final NullValueProvider findValueNullProvider(DeserializationContext ctxt, SettableBeanProperty prop, PropertyMetadata propMetadata) throws JsonMappingException {
      return prop != null ? this._findNullProvider(ctxt, prop, propMetadata.getValueNulls(), prop.getValueDeserializer()) : null;
   }

   protected NullValueProvider findContentNullProvider(DeserializationContext ctxt, BeanProperty prop, JsonDeserializer<?> valueDeser) throws JsonMappingException {
      Nulls nulls = this.findContentNullStyle(ctxt, prop);
      if (nulls == Nulls.SKIP) {
         return NullsConstantProvider.skipper();
      } else {
         NullValueProvider prov = this._findNullProvider(ctxt, prop, nulls, valueDeser);
         return (NullValueProvider)(prov != null ? prov : valueDeser);
      }
   }

   protected Nulls findContentNullStyle(DeserializationContext ctxt, BeanProperty prop) throws JsonMappingException {
      return prop != null ? prop.getMetadata().getContentNulls() : null;
   }

   protected final NullValueProvider _findNullProvider(DeserializationContext ctxt, BeanProperty prop, Nulls nulls, JsonDeserializer<?> valueDeser) throws JsonMappingException {
      if (nulls == Nulls.FAIL) {
         return prop == null ? NullsFailProvider.constructForRootValue(ctxt.constructType(valueDeser.handledType())) : NullsFailProvider.constructForProperty(prop);
      } else if (nulls == Nulls.AS_EMPTY) {
         if (valueDeser == null) {
            return null;
         } else {
            if (valueDeser instanceof BeanDeserializerBase) {
               ValueInstantiator vi = ((BeanDeserializerBase)valueDeser).getValueInstantiator();
               if (!vi.canCreateUsingDefault()) {
                  JavaType type = prop.getType();
                  ctxt.reportBadDefinition(type, String.format("Cannot create empty instance of %s, no default Creator", type));
               }
            }

            AccessPattern access = valueDeser.getEmptyAccessPattern();
            if (access == AccessPattern.ALWAYS_NULL) {
               return NullsConstantProvider.nuller();
            } else {
               return (NullValueProvider)(access == AccessPattern.CONSTANT ? NullsConstantProvider.forValue(valueDeser.getEmptyValue(ctxt)) : new NullsAsEmptyProvider(valueDeser));
            }
         }
      } else {
         return nulls == Nulls.SKIP ? NullsConstantProvider.skipper() : null;
      }
   }

   protected void handleUnknownProperty(JsonParser p, DeserializationContext ctxt, Object instanceOrClass, String propName) throws IOException {
      if (instanceOrClass == null) {
         instanceOrClass = this.handledType();
      }

      if (!ctxt.handleUnknownProperty(p, this, instanceOrClass, propName)) {
         p.skipChildren();
      }
   }

   protected void handleMissingEndArrayForSingle(JsonParser p, DeserializationContext ctxt) throws IOException {
      ctxt.reportWrongTokenException((JsonDeserializer)this, JsonToken.END_ARRAY, "Attempted to unwrap '%s' value from an array (with `DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS`) but it contains more than one value", this.handledType().getName());
   }

   protected void _verifyEndArrayForSingle(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonToken t = p.nextToken();
      if (t != JsonToken.END_ARRAY) {
         this.handleMissingEndArrayForSingle(p, ctxt);
      }

   }

   protected static final boolean _neitherNull(Object a, Object b) {
      return a != null && b != null;
   }

   protected final boolean _byteOverflow(int value) {
      return value < -128 || value > 255;
   }

   protected final boolean _shortOverflow(int value) {
      return value < -32768 || value > 32767;
   }

   protected final boolean _intOverflow(long value) {
      return value < -2147483648L || value > 2147483647L;
   }

   protected Number _nonNullNumber(Number n) {
      if (n == null) {
         n = 0;
      }

      return (Number)n;
   }

   static {
      F_MASK_INT_COERCIONS = DeserializationFeature.USE_BIG_INTEGER_FOR_INTS.getMask() | DeserializationFeature.USE_LONG_FOR_INTS.getMask();
      F_MASK_ACCEPT_ARRAYS = DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS.getMask() | DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT.getMask();
   }
}

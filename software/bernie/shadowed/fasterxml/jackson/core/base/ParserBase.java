package software.bernie.shadowed.fasterxml.jackson.core.base;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import software.bernie.shadowed.fasterxml.jackson.core.Base64Variant;
import software.bernie.shadowed.fasterxml.jackson.core.JsonLocation;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParseException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.Version;
import software.bernie.shadowed.fasterxml.jackson.core.io.IOContext;
import software.bernie.shadowed.fasterxml.jackson.core.io.NumberInput;
import software.bernie.shadowed.fasterxml.jackson.core.json.DupDetector;
import software.bernie.shadowed.fasterxml.jackson.core.json.JsonReadContext;
import software.bernie.shadowed.fasterxml.jackson.core.json.PackageVersion;
import software.bernie.shadowed.fasterxml.jackson.core.util.ByteArrayBuilder;
import software.bernie.shadowed.fasterxml.jackson.core.util.TextBuffer;

public abstract class ParserBase extends ParserMinimalBase {
   protected final IOContext _ioContext;
   protected boolean _closed;
   protected int _inputPtr;
   protected int _inputEnd;
   protected long _currInputProcessed;
   protected int _currInputRow = 1;
   protected int _currInputRowStart;
   protected long _tokenInputTotal;
   protected int _tokenInputRow = 1;
   protected int _tokenInputCol;
   protected JsonReadContext _parsingContext;
   protected JsonToken _nextToken;
   protected final TextBuffer _textBuffer;
   protected char[] _nameCopyBuffer;
   protected boolean _nameCopied;
   protected ByteArrayBuilder _byteArrayBuilder;
   protected byte[] _binaryValue;
   protected int _numTypesValid = 0;
   protected int _numberInt;
   protected long _numberLong;
   protected double _numberDouble;
   protected BigInteger _numberBigInt;
   protected BigDecimal _numberBigDecimal;
   protected boolean _numberNegative;
   protected int _intLength;
   protected int _fractLength;
   protected int _expLength;

   protected ParserBase(IOContext ctxt, int features) {
      super(features);
      this._ioContext = ctxt;
      this._textBuffer = ctxt.constructTextBuffer();
      DupDetector dups = JsonParser.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(features) ? DupDetector.rootDetector((JsonParser)this) : null;
      this._parsingContext = JsonReadContext.createRootContext(dups);
   }

   public Version version() {
      return PackageVersion.VERSION;
   }

   public Object getCurrentValue() {
      return this._parsingContext.getCurrentValue();
   }

   public void setCurrentValue(Object v) {
      this._parsingContext.setCurrentValue(v);
   }

   public JsonParser enable(JsonParser.Feature f) {
      this._features |= f.getMask();
      if (f == JsonParser.Feature.STRICT_DUPLICATE_DETECTION && this._parsingContext.getDupDetector() == null) {
         this._parsingContext = this._parsingContext.withDupDetector(DupDetector.rootDetector((JsonParser)this));
      }

      return this;
   }

   public JsonParser disable(JsonParser.Feature f) {
      this._features &= ~f.getMask();
      if (f == JsonParser.Feature.STRICT_DUPLICATE_DETECTION) {
         this._parsingContext = this._parsingContext.withDupDetector((DupDetector)null);
      }

      return this;
   }

   /** @deprecated */
   @Deprecated
   public JsonParser setFeatureMask(int newMask) {
      int changes = this._features ^ newMask;
      if (changes != 0) {
         this._features = newMask;
         this._checkStdFeatureChanges(newMask, changes);
      }

      return this;
   }

   public JsonParser overrideStdFeatures(int values, int mask) {
      int oldState = this._features;
      int newState = oldState & ~mask | values & mask;
      int changed = oldState ^ newState;
      if (changed != 0) {
         this._features = newState;
         this._checkStdFeatureChanges(newState, changed);
      }

      return this;
   }

   protected void _checkStdFeatureChanges(int newFeatureFlags, int changedFeatures) {
      int f = JsonParser.Feature.STRICT_DUPLICATE_DETECTION.getMask();
      if ((changedFeatures & f) != 0 && (newFeatureFlags & f) != 0) {
         if (this._parsingContext.getDupDetector() == null) {
            this._parsingContext = this._parsingContext.withDupDetector(DupDetector.rootDetector((JsonParser)this));
         } else {
            this._parsingContext = this._parsingContext.withDupDetector((DupDetector)null);
         }
      }

   }

   public String getCurrentName() throws IOException {
      if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
         JsonReadContext parent = this._parsingContext.getParent();
         if (parent != null) {
            return parent.getCurrentName();
         }
      }

      return this._parsingContext.getCurrentName();
   }

   public void overrideCurrentName(String name) {
      JsonReadContext ctxt = this._parsingContext;
      if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
         ctxt = ctxt.getParent();
      }

      try {
         ctxt.setCurrentName(name);
      } catch (IOException var4) {
         throw new IllegalStateException(var4);
      }
   }

   public void close() throws IOException {
      if (!this._closed) {
         this._closed = true;

         try {
            this._closeInput();
         } finally {
            this._releaseBuffers();
         }
      }

   }

   public boolean isClosed() {
      return this._closed;
   }

   public JsonReadContext getParsingContext() {
      return this._parsingContext;
   }

   public JsonLocation getTokenLocation() {
      return new JsonLocation(this._getSourceReference(), -1L, this.getTokenCharacterOffset(), this.getTokenLineNr(), this.getTokenColumnNr());
   }

   public JsonLocation getCurrentLocation() {
      int col = this._inputPtr - this._currInputRowStart + 1;
      return new JsonLocation(this._getSourceReference(), -1L, this._currInputProcessed + (long)this._inputPtr, this._currInputRow, col);
   }

   public boolean hasTextCharacters() {
      if (this._currToken == JsonToken.VALUE_STRING) {
         return true;
      } else {
         return this._currToken == JsonToken.FIELD_NAME ? this._nameCopied : false;
      }
   }

   public byte[] getBinaryValue(Base64Variant variant) throws IOException {
      if (this._binaryValue == null) {
         if (this._currToken != JsonToken.VALUE_STRING) {
            this._reportError("Current token (" + this._currToken + ") not VALUE_STRING, can not access as binary");
         }

         ByteArrayBuilder builder = this._getByteArrayBuilder();
         this._decodeBase64(this.getText(), builder, variant);
         this._binaryValue = builder.toByteArray();
      }

      return this._binaryValue;
   }

   public long getTokenCharacterOffset() {
      return this._tokenInputTotal;
   }

   public int getTokenLineNr() {
      return this._tokenInputRow;
   }

   public int getTokenColumnNr() {
      int col = this._tokenInputCol;
      return col < 0 ? col : col + 1;
   }

   protected abstract void _closeInput() throws IOException;

   protected void _releaseBuffers() throws IOException {
      this._textBuffer.releaseBuffers();
      char[] buf = this._nameCopyBuffer;
      if (buf != null) {
         this._nameCopyBuffer = null;
         this._ioContext.releaseNameCopyBuffer(buf);
      }

   }

   protected void _handleEOF() throws JsonParseException {
      if (!this._parsingContext.inRoot()) {
         String marker = this._parsingContext.inArray() ? "Array" : "Object";
         this._reportInvalidEOF(String.format(": expected close marker for %s (start marker at %s)", marker, this._parsingContext.getStartLocation(this._getSourceReference())), (JsonToken)null);
      }

   }

   protected final int _eofAsNextChar() throws JsonParseException {
      this._handleEOF();
      return -1;
   }

   public ByteArrayBuilder _getByteArrayBuilder() {
      if (this._byteArrayBuilder == null) {
         this._byteArrayBuilder = new ByteArrayBuilder();
      } else {
         this._byteArrayBuilder.reset();
      }

      return this._byteArrayBuilder;
   }

   protected final JsonToken reset(boolean negative, int intLen, int fractLen, int expLen) {
      return fractLen < 1 && expLen < 1 ? this.resetInt(negative, intLen) : this.resetFloat(negative, intLen, fractLen, expLen);
   }

   protected final JsonToken resetInt(boolean negative, int intLen) {
      this._numberNegative = negative;
      this._intLength = intLen;
      this._fractLength = 0;
      this._expLength = 0;
      this._numTypesValid = 0;
      return JsonToken.VALUE_NUMBER_INT;
   }

   protected final JsonToken resetFloat(boolean negative, int intLen, int fractLen, int expLen) {
      this._numberNegative = negative;
      this._intLength = intLen;
      this._fractLength = fractLen;
      this._expLength = expLen;
      this._numTypesValid = 0;
      return JsonToken.VALUE_NUMBER_FLOAT;
   }

   protected final JsonToken resetAsNaN(String valueStr, double value) {
      this._textBuffer.resetWithString(valueStr);
      this._numberDouble = value;
      this._numTypesValid = 8;
      return JsonToken.VALUE_NUMBER_FLOAT;
   }

   public boolean isNaN() {
      if (this._currToken == JsonToken.VALUE_NUMBER_FLOAT && (this._numTypesValid & 8) != 0) {
         double d = this._numberDouble;
         return Double.isNaN(d) || Double.isInfinite(d);
      } else {
         return false;
      }
   }

   public Number getNumberValue() throws IOException {
      if (this._numTypesValid == 0) {
         this._parseNumericValue(0);
      }

      if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
         if ((this._numTypesValid & 1) != 0) {
            return this._numberInt;
         } else if ((this._numTypesValid & 2) != 0) {
            return this._numberLong;
         } else {
            return (Number)((this._numTypesValid & 4) != 0 ? this._numberBigInt : this._numberBigDecimal);
         }
      } else if ((this._numTypesValid & 16) != 0) {
         return this._numberBigDecimal;
      } else {
         if ((this._numTypesValid & 8) == 0) {
            this._throwInternal();
         }

         return this._numberDouble;
      }
   }

   public JsonParser.NumberType getNumberType() throws IOException {
      if (this._numTypesValid == 0) {
         this._parseNumericValue(0);
      }

      if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
         if ((this._numTypesValid & 1) != 0) {
            return JsonParser.NumberType.INT;
         } else {
            return (this._numTypesValid & 2) != 0 ? JsonParser.NumberType.LONG : JsonParser.NumberType.BIG_INTEGER;
         }
      } else {
         return (this._numTypesValid & 16) != 0 ? JsonParser.NumberType.BIG_DECIMAL : JsonParser.NumberType.DOUBLE;
      }
   }

   public int getIntValue() throws IOException {
      if ((this._numTypesValid & 1) == 0) {
         if (this._numTypesValid == 0) {
            return this._parseIntValue();
         }

         if ((this._numTypesValid & 1) == 0) {
            this.convertNumberToInt();
         }
      }

      return this._numberInt;
   }

   public long getLongValue() throws IOException {
      if ((this._numTypesValid & 2) == 0) {
         if (this._numTypesValid == 0) {
            this._parseNumericValue(2);
         }

         if ((this._numTypesValid & 2) == 0) {
            this.convertNumberToLong();
         }
      }

      return this._numberLong;
   }

   public BigInteger getBigIntegerValue() throws IOException {
      if ((this._numTypesValid & 4) == 0) {
         if (this._numTypesValid == 0) {
            this._parseNumericValue(4);
         }

         if ((this._numTypesValid & 4) == 0) {
            this.convertNumberToBigInteger();
         }
      }

      return this._numberBigInt;
   }

   public float getFloatValue() throws IOException {
      double value = this.getDoubleValue();
      return (float)value;
   }

   public double getDoubleValue() throws IOException {
      if ((this._numTypesValid & 8) == 0) {
         if (this._numTypesValid == 0) {
            this._parseNumericValue(8);
         }

         if ((this._numTypesValid & 8) == 0) {
            this.convertNumberToDouble();
         }
      }

      return this._numberDouble;
   }

   public BigDecimal getDecimalValue() throws IOException {
      if ((this._numTypesValid & 16) == 0) {
         if (this._numTypesValid == 0) {
            this._parseNumericValue(16);
         }

         if ((this._numTypesValid & 16) == 0) {
            this.convertNumberToBigDecimal();
         }
      }

      return this._numberBigDecimal;
   }

   protected void _parseNumericValue(int expType) throws IOException {
      if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
         int len = this._intLength;
         if (len <= 9) {
            int i = this._textBuffer.contentsAsInt(this._numberNegative);
            this._numberInt = i;
            this._numTypesValid = 1;
         } else if (len <= 18) {
            long l = this._textBuffer.contentsAsLong(this._numberNegative);
            if (len == 10) {
               if (this._numberNegative) {
                  if (l >= -2147483648L) {
                     this._numberInt = (int)l;
                     this._numTypesValid = 1;
                     return;
                  }
               } else if (l <= 2147483647L) {
                  this._numberInt = (int)l;
                  this._numTypesValid = 1;
                  return;
               }
            }

            this._numberLong = l;
            this._numTypesValid = 2;
         } else {
            this._parseSlowInt(expType);
         }
      } else if (this._currToken == JsonToken.VALUE_NUMBER_FLOAT) {
         this._parseSlowFloat(expType);
      } else {
         this._reportError("Current token (%s) not numeric, can not use numeric value accessors", this._currToken);
      }
   }

   protected int _parseIntValue() throws IOException {
      if (this._currToken == JsonToken.VALUE_NUMBER_INT && this._intLength <= 9) {
         int i = this._textBuffer.contentsAsInt(this._numberNegative);
         this._numberInt = i;
         this._numTypesValid = 1;
         return i;
      } else {
         this._parseNumericValue(1);
         if ((this._numTypesValid & 1) == 0) {
            this.convertNumberToInt();
         }

         return this._numberInt;
      }
   }

   private void _parseSlowFloat(int expType) throws IOException {
      try {
         if (expType == 16) {
            this._numberBigDecimal = this._textBuffer.contentsAsDecimal();
            this._numTypesValid = 16;
         } else {
            this._numberDouble = this._textBuffer.contentsAsDouble();
            this._numTypesValid = 8;
         }
      } catch (NumberFormatException var3) {
         this._wrapError("Malformed numeric value '" + this._textBuffer.contentsAsString() + "'", var3);
      }

   }

   private void _parseSlowInt(int expType) throws IOException {
      String numStr = this._textBuffer.contentsAsString();

      try {
         int len = this._intLength;
         char[] buf = this._textBuffer.getTextBuffer();
         int offset = this._textBuffer.getTextOffset();
         if (this._numberNegative) {
            ++offset;
         }

         if (NumberInput.inLongRange(buf, offset, len, this._numberNegative)) {
            this._numberLong = Long.parseLong(numStr);
            this._numTypesValid = 2;
         } else {
            this._numberBigInt = new BigInteger(numStr);
            this._numTypesValid = 4;
         }
      } catch (NumberFormatException var6) {
         this._wrapError("Malformed numeric value '" + numStr + "'", var6);
      }

   }

   protected void convertNumberToInt() throws IOException {
      if ((this._numTypesValid & 2) != 0) {
         int result = (int)this._numberLong;
         if ((long)result != this._numberLong) {
            this._reportError("Numeric value (" + this.getText() + ") out of range of int");
         }

         this._numberInt = result;
      } else if ((this._numTypesValid & 4) != 0) {
         if (BI_MIN_INT.compareTo(this._numberBigInt) > 0 || BI_MAX_INT.compareTo(this._numberBigInt) < 0) {
            this.reportOverflowInt();
         }

         this._numberInt = this._numberBigInt.intValue();
      } else if ((this._numTypesValid & 8) != 0) {
         if (this._numberDouble < -2.147483648E9D || this._numberDouble > 2.147483647E9D) {
            this.reportOverflowInt();
         }

         this._numberInt = (int)this._numberDouble;
      } else if ((this._numTypesValid & 16) != 0) {
         if (BD_MIN_INT.compareTo(this._numberBigDecimal) > 0 || BD_MAX_INT.compareTo(this._numberBigDecimal) < 0) {
            this.reportOverflowInt();
         }

         this._numberInt = this._numberBigDecimal.intValue();
      } else {
         this._throwInternal();
      }

      this._numTypesValid |= 1;
   }

   protected void convertNumberToLong() throws IOException {
      if ((this._numTypesValid & 1) != 0) {
         this._numberLong = (long)this._numberInt;
      } else if ((this._numTypesValid & 4) != 0) {
         if (BI_MIN_LONG.compareTo(this._numberBigInt) > 0 || BI_MAX_LONG.compareTo(this._numberBigInt) < 0) {
            this.reportOverflowLong();
         }

         this._numberLong = this._numberBigInt.longValue();
      } else if ((this._numTypesValid & 8) != 0) {
         if (this._numberDouble < -9.223372036854776E18D || this._numberDouble > 9.223372036854776E18D) {
            this.reportOverflowLong();
         }

         this._numberLong = (long)this._numberDouble;
      } else if ((this._numTypesValid & 16) != 0) {
         if (BD_MIN_LONG.compareTo(this._numberBigDecimal) > 0 || BD_MAX_LONG.compareTo(this._numberBigDecimal) < 0) {
            this.reportOverflowLong();
         }

         this._numberLong = this._numberBigDecimal.longValue();
      } else {
         this._throwInternal();
      }

      this._numTypesValid |= 2;
   }

   protected void convertNumberToBigInteger() throws IOException {
      if ((this._numTypesValid & 16) != 0) {
         this._numberBigInt = this._numberBigDecimal.toBigInteger();
      } else if ((this._numTypesValid & 2) != 0) {
         this._numberBigInt = BigInteger.valueOf(this._numberLong);
      } else if ((this._numTypesValid & 1) != 0) {
         this._numberBigInt = BigInteger.valueOf((long)this._numberInt);
      } else if ((this._numTypesValid & 8) != 0) {
         this._numberBigInt = BigDecimal.valueOf(this._numberDouble).toBigInteger();
      } else {
         this._throwInternal();
      }

      this._numTypesValid |= 4;
   }

   protected void convertNumberToDouble() throws IOException {
      if ((this._numTypesValid & 16) != 0) {
         this._numberDouble = this._numberBigDecimal.doubleValue();
      } else if ((this._numTypesValid & 4) != 0) {
         this._numberDouble = this._numberBigInt.doubleValue();
      } else if ((this._numTypesValid & 2) != 0) {
         this._numberDouble = (double)this._numberLong;
      } else if ((this._numTypesValid & 1) != 0) {
         this._numberDouble = (double)this._numberInt;
      } else {
         this._throwInternal();
      }

      this._numTypesValid |= 8;
   }

   protected void convertNumberToBigDecimal() throws IOException {
      if ((this._numTypesValid & 8) != 0) {
         this._numberBigDecimal = NumberInput.parseBigDecimal(this.getText());
      } else if ((this._numTypesValid & 4) != 0) {
         this._numberBigDecimal = new BigDecimal(this._numberBigInt);
      } else if ((this._numTypesValid & 2) != 0) {
         this._numberBigDecimal = BigDecimal.valueOf(this._numberLong);
      } else if ((this._numTypesValid & 1) != 0) {
         this._numberBigDecimal = BigDecimal.valueOf((long)this._numberInt);
      } else {
         this._throwInternal();
      }

      this._numTypesValid |= 16;
   }

   protected void _reportMismatchedEndMarker(int actCh, char expCh) throws JsonParseException {
      JsonReadContext ctxt = this.getParsingContext();
      this._reportError(String.format("Unexpected close marker '%s': expected '%c' (for %s starting at %s)", (char)actCh, expCh, ctxt.typeDesc(), ctxt.getStartLocation(this._getSourceReference())));
   }

   protected char _decodeEscaped() throws IOException {
      throw new UnsupportedOperationException();
   }

   protected final int _decodeBase64Escape(Base64Variant b64variant, int ch, int index) throws IOException {
      if (ch != 92) {
         throw this.reportInvalidBase64Char(b64variant, ch, index);
      } else {
         int unescaped = this._decodeEscaped();
         if (unescaped <= ' ' && index == 0) {
            return -1;
         } else {
            int bits = b64variant.decodeBase64Char((int)unescaped);
            if (bits < 0) {
               throw this.reportInvalidBase64Char(b64variant, unescaped, index);
            } else {
               return bits;
            }
         }
      }
   }

   protected final int _decodeBase64Escape(Base64Variant b64variant, char ch, int index) throws IOException {
      if (ch != '\\') {
         throw this.reportInvalidBase64Char(b64variant, ch, index);
      } else {
         char unescaped = this._decodeEscaped();
         if (unescaped <= ' ' && index == 0) {
            return -1;
         } else {
            int bits = b64variant.decodeBase64Char(unescaped);
            if (bits < 0) {
               throw this.reportInvalidBase64Char(b64variant, unescaped, index);
            } else {
               return bits;
            }
         }
      }
   }

   protected IllegalArgumentException reportInvalidBase64Char(Base64Variant b64variant, int ch, int bindex) throws IllegalArgumentException {
      return this.reportInvalidBase64Char(b64variant, ch, bindex, (String)null);
   }

   protected IllegalArgumentException reportInvalidBase64Char(Base64Variant b64variant, int ch, int bindex, String msg) throws IllegalArgumentException {
      String base;
      if (ch <= 32) {
         base = String.format("Illegal white space character (code 0x%s) as character #%d of 4-char base64 unit: can only used between units", Integer.toHexString(ch), bindex + 1);
      } else if (b64variant.usesPaddingChar(ch)) {
         base = "Unexpected padding character ('" + b64variant.getPaddingChar() + "') as character #" + (bindex + 1) + " of 4-char base64 unit: padding only legal as 3rd or 4th character";
      } else if (Character.isDefined(ch) && !Character.isISOControl(ch)) {
         base = "Illegal character '" + (char)ch + "' (code 0x" + Integer.toHexString(ch) + ") in base64 content";
      } else {
         base = "Illegal character (code 0x" + Integer.toHexString(ch) + ") in base64 content";
      }

      if (msg != null) {
         base = base + ": " + msg;
      }

      return new IllegalArgumentException(base);
   }

   protected Object _getSourceReference() {
      return JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION.enabledIn(this._features) ? this._ioContext.getSourceReference() : null;
   }

   protected static int[] growArrayBy(int[] arr, int more) {
      return arr == null ? new int[more] : Arrays.copyOf(arr, arr.length + more);
   }

   /** @deprecated */
   @Deprecated
   protected void loadMoreGuaranteed() throws IOException {
      if (!this.loadMore()) {
         this._reportInvalidEOF();
      }

   }

   /** @deprecated */
   @Deprecated
   protected boolean loadMore() throws IOException {
      return false;
   }

   protected void _finishString() throws IOException {
   }
}

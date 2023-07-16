package software.bernie.shadowed.fasterxml.jackson.core.json.async;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import software.bernie.shadowed.fasterxml.jackson.core.Base64Variant;
import software.bernie.shadowed.fasterxml.jackson.core.JsonLocation;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParseException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.ObjectCodec;
import software.bernie.shadowed.fasterxml.jackson.core.base.ParserBase;
import software.bernie.shadowed.fasterxml.jackson.core.io.IOContext;
import software.bernie.shadowed.fasterxml.jackson.core.json.JsonReadContext;
import software.bernie.shadowed.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
import software.bernie.shadowed.fasterxml.jackson.core.util.ByteArrayBuilder;

public abstract class NonBlockingJsonParserBase extends ParserBase {
   protected static final int MAJOR_INITIAL = 0;
   protected static final int MAJOR_ROOT = 1;
   protected static final int MAJOR_OBJECT_FIELD_FIRST = 2;
   protected static final int MAJOR_OBJECT_FIELD_NEXT = 3;
   protected static final int MAJOR_OBJECT_VALUE = 4;
   protected static final int MAJOR_ARRAY_ELEMENT_FIRST = 5;
   protected static final int MAJOR_ARRAY_ELEMENT_NEXT = 6;
   protected static final int MAJOR_CLOSED = 7;
   protected static final int MINOR_ROOT_BOM = 1;
   protected static final int MINOR_ROOT_NEED_SEPARATOR = 2;
   protected static final int MINOR_ROOT_GOT_SEPARATOR = 3;
   protected static final int MINOR_FIELD_LEADING_WS = 4;
   protected static final int MINOR_FIELD_LEADING_COMMA = 5;
   protected static final int MINOR_FIELD_NAME = 7;
   protected static final int MINOR_FIELD_NAME_ESCAPE = 8;
   protected static final int MINOR_FIELD_APOS_NAME = 9;
   protected static final int MINOR_FIELD_UNQUOTED_NAME = 10;
   protected static final int MINOR_VALUE_LEADING_WS = 12;
   protected static final int MINOR_VALUE_EXPECTING_COMMA = 13;
   protected static final int MINOR_VALUE_EXPECTING_COLON = 14;
   protected static final int MINOR_VALUE_WS_AFTER_COMMA = 15;
   protected static final int MINOR_VALUE_TOKEN_NULL = 16;
   protected static final int MINOR_VALUE_TOKEN_TRUE = 17;
   protected static final int MINOR_VALUE_TOKEN_FALSE = 18;
   protected static final int MINOR_VALUE_TOKEN_NON_STD = 19;
   protected static final int MINOR_NUMBER_MINUS = 23;
   protected static final int MINOR_NUMBER_ZERO = 24;
   protected static final int MINOR_NUMBER_MINUSZERO = 25;
   protected static final int MINOR_NUMBER_INTEGER_DIGITS = 26;
   protected static final int MINOR_NUMBER_FRACTION_DIGITS = 30;
   protected static final int MINOR_NUMBER_EXPONENT_MARKER = 31;
   protected static final int MINOR_NUMBER_EXPONENT_DIGITS = 32;
   protected static final int MINOR_VALUE_STRING = 40;
   protected static final int MINOR_VALUE_STRING_ESCAPE = 41;
   protected static final int MINOR_VALUE_STRING_UTF8_2 = 42;
   protected static final int MINOR_VALUE_STRING_UTF8_3 = 43;
   protected static final int MINOR_VALUE_STRING_UTF8_4 = 44;
   protected static final int MINOR_VALUE_APOS_STRING = 45;
   protected static final int MINOR_VALUE_TOKEN_ERROR = 50;
   protected static final int MINOR_COMMENT_LEADING_SLASH = 51;
   protected static final int MINOR_COMMENT_CLOSING_ASTERISK = 52;
   protected static final int MINOR_COMMENT_C = 53;
   protected static final int MINOR_COMMENT_CPP = 54;
   protected static final int MINOR_COMMENT_YAML = 55;
   protected final ByteQuadsCanonicalizer _symbols;
   protected int[] _quadBuffer = new int[8];
   protected int _quadLength;
   protected int _quad1;
   protected int _pending32;
   protected int _pendingBytes;
   protected int _quoted32;
   protected int _quotedDigits;
   protected int _majorState;
   protected int _majorStateAfterValue;
   protected int _minorState;
   protected int _minorStateAfterSplit;
   protected boolean _endOfInput = false;
   protected static final int NON_STD_TOKEN_NAN = 0;
   protected static final int NON_STD_TOKEN_INFINITY = 1;
   protected static final int NON_STD_TOKEN_PLUS_INFINITY = 2;
   protected static final int NON_STD_TOKEN_MINUS_INFINITY = 3;
   protected static final String[] NON_STD_TOKENS = new String[]{"NaN", "Infinity", "+Infinity", "-Infinity"};
   protected static final double[] NON_STD_TOKEN_VALUES = new double[]{Double.NaN, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
   protected int _nonStdTokenType;
   protected int _currBufferStart = 0;
   protected int _currInputRowAlt = 1;

   public NonBlockingJsonParserBase(IOContext ctxt, int parserFeatures, ByteQuadsCanonicalizer sym) {
      super(ctxt, parserFeatures);
      this._symbols = sym;
      this._currToken = null;
      this._majorState = 0;
      this._majorStateAfterValue = 1;
   }

   public ObjectCodec getCodec() {
      return null;
   }

   public void setCodec(ObjectCodec c) {
      throw new UnsupportedOperationException("Can not use ObjectMapper with non-blocking parser");
   }

   public boolean canParseAsync() {
      return true;
   }

   protected ByteQuadsCanonicalizer symbolTableForTests() {
      return this._symbols;
   }

   public abstract int releaseBuffered(OutputStream var1) throws IOException;

   protected void _releaseBuffers() throws IOException {
      super._releaseBuffers();
      this._symbols.release();
   }

   public Object getInputSource() {
      return null;
   }

   protected void _closeInput() throws IOException {
      this._currBufferStart = 0;
      this._inputEnd = 0;
   }

   public boolean hasTextCharacters() {
      if (this._currToken == JsonToken.VALUE_STRING) {
         return this._textBuffer.hasTextAsCharacters();
      } else {
         return this._currToken == JsonToken.FIELD_NAME ? this._nameCopied : false;
      }
   }

   public JsonLocation getCurrentLocation() {
      int col = this._inputPtr - this._currInputRowStart + 1;
      int row = Math.max(this._currInputRow, this._currInputRowAlt);
      return new JsonLocation(this._getSourceReference(), this._currInputProcessed + (long)(this._inputPtr - this._currBufferStart), -1L, row, col);
   }

   public JsonLocation getTokenLocation() {
      return new JsonLocation(this._getSourceReference(), this._tokenInputTotal, -1L, this._tokenInputRow, this._tokenInputCol);
   }

   public String getText() throws IOException {
      return this._currToken == JsonToken.VALUE_STRING ? this._textBuffer.contentsAsString() : this._getText2(this._currToken);
   }

   protected final String _getText2(JsonToken t) {
      if (t == null) {
         return null;
      } else {
         switch(t.id()) {
         case -1:
            return null;
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         default:
            return t.asString();
         case 5:
            return this._parsingContext.getCurrentName();
         case 6:
         case 7:
         case 8:
            return this._textBuffer.contentsAsString();
         }
      }
   }

   public int getText(Writer writer) throws IOException {
      JsonToken t = this._currToken;
      if (t == JsonToken.VALUE_STRING) {
         return this._textBuffer.contentsToWriter(writer);
      } else if (t == JsonToken.FIELD_NAME) {
         String n = this._parsingContext.getCurrentName();
         writer.write(n);
         return n.length();
      } else if (t != null) {
         if (t.isNumeric()) {
            return this._textBuffer.contentsToWriter(writer);
         } else {
            if (t == JsonToken.NOT_AVAILABLE) {
               this._reportError("Current token not available: can not call this method");
            }

            char[] ch = t.asCharArray();
            writer.write(ch);
            return ch.length;
         }
      } else {
         return 0;
      }
   }

   public String getValueAsString() throws IOException {
      if (this._currToken == JsonToken.VALUE_STRING) {
         return this._textBuffer.contentsAsString();
      } else {
         return this._currToken == JsonToken.FIELD_NAME ? this.getCurrentName() : super.getValueAsString((String)null);
      }
   }

   public String getValueAsString(String defValue) throws IOException {
      if (this._currToken == JsonToken.VALUE_STRING) {
         return this._textBuffer.contentsAsString();
      } else {
         return this._currToken == JsonToken.FIELD_NAME ? this.getCurrentName() : super.getValueAsString(defValue);
      }
   }

   public char[] getTextCharacters() throws IOException {
      if (this._currToken != null) {
         switch(this._currToken.id()) {
         case 5:
            if (!this._nameCopied) {
               String name = this._parsingContext.getCurrentName();
               int nameLen = name.length();
               if (this._nameCopyBuffer == null) {
                  this._nameCopyBuffer = this._ioContext.allocNameCopyBuffer(nameLen);
               } else if (this._nameCopyBuffer.length < nameLen) {
                  this._nameCopyBuffer = new char[nameLen];
               }

               name.getChars(0, nameLen, this._nameCopyBuffer, 0);
               this._nameCopied = true;
            }

            return this._nameCopyBuffer;
         case 6:
         case 7:
         case 8:
            return this._textBuffer.getTextBuffer();
         default:
            return this._currToken.asCharArray();
         }
      } else {
         return null;
      }
   }

   public int getTextLength() throws IOException {
      if (this._currToken != null) {
         switch(this._currToken.id()) {
         case 5:
            return this._parsingContext.getCurrentName().length();
         case 6:
         case 7:
         case 8:
            return this._textBuffer.size();
         default:
            return this._currToken.asCharArray().length;
         }
      } else {
         return 0;
      }
   }

   public int getTextOffset() throws IOException {
      if (this._currToken != null) {
         switch(this._currToken.id()) {
         case 5:
            return 0;
         case 6:
         case 7:
         case 8:
            return this._textBuffer.getTextOffset();
         }
      }

      return 0;
   }

   public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
      if (this._currToken != JsonToken.VALUE_STRING) {
         this._reportError("Current token (%s) not VALUE_STRING or VALUE_EMBEDDED_OBJECT, can not access as binary", this._currToken);
      }

      if (this._binaryValue == null) {
         ByteArrayBuilder builder = this._getByteArrayBuilder();
         this._decodeBase64(this.getText(), builder, b64variant);
         this._binaryValue = builder.toByteArray();
      }

      return this._binaryValue;
   }

   public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
      byte[] b = this.getBinaryValue(b64variant);
      out.write(b);
      return b.length;
   }

   public Object getEmbeddedObject() throws IOException {
      return this._currToken == JsonToken.VALUE_EMBEDDED_OBJECT ? this._binaryValue : null;
   }

   protected final JsonToken _startArrayScope() throws IOException {
      this._parsingContext = this._parsingContext.createChildArrayContext(-1, -1);
      this._majorState = 5;
      this._majorStateAfterValue = 6;
      return this._currToken = JsonToken.START_ARRAY;
   }

   protected final JsonToken _startObjectScope() throws IOException {
      this._parsingContext = this._parsingContext.createChildObjectContext(-1, -1);
      this._majorState = 2;
      this._majorStateAfterValue = 3;
      return this._currToken = JsonToken.START_OBJECT;
   }

   protected final JsonToken _closeArrayScope() throws IOException {
      if (!this._parsingContext.inArray()) {
         this._reportMismatchedEndMarker(93, '}');
      }

      JsonReadContext ctxt = this._parsingContext.getParent();
      this._parsingContext = ctxt;
      byte st;
      if (ctxt.inObject()) {
         st = 3;
      } else if (ctxt.inArray()) {
         st = 6;
      } else {
         st = 1;
      }

      this._majorState = st;
      this._majorStateAfterValue = st;
      return this._currToken = JsonToken.END_ARRAY;
   }

   protected final JsonToken _closeObjectScope() throws IOException {
      if (!this._parsingContext.inObject()) {
         this._reportMismatchedEndMarker(125, ']');
      }

      JsonReadContext ctxt = this._parsingContext.getParent();
      this._parsingContext = ctxt;
      byte st;
      if (ctxt.inObject()) {
         st = 3;
      } else if (ctxt.inArray()) {
         st = 6;
      } else {
         st = 1;
      }

      this._majorState = st;
      this._majorStateAfterValue = st;
      return this._currToken = JsonToken.END_OBJECT;
   }

   protected final String _findName(int q1, int lastQuadBytes) throws JsonParseException {
      q1 = _padLastQuad(q1, lastQuadBytes);
      String name = this._symbols.findName(q1);
      if (name != null) {
         return name;
      } else {
         this._quadBuffer[0] = q1;
         return this._addName(this._quadBuffer, 1, lastQuadBytes);
      }
   }

   protected final String _findName(int q1, int q2, int lastQuadBytes) throws JsonParseException {
      q2 = _padLastQuad(q2, lastQuadBytes);
      String name = this._symbols.findName(q1, q2);
      if (name != null) {
         return name;
      } else {
         this._quadBuffer[0] = q1;
         this._quadBuffer[1] = q2;
         return this._addName(this._quadBuffer, 2, lastQuadBytes);
      }
   }

   protected final String _findName(int q1, int q2, int q3, int lastQuadBytes) throws JsonParseException {
      q3 = _padLastQuad(q3, lastQuadBytes);
      String name = this._symbols.findName(q1, q2, q3);
      if (name != null) {
         return name;
      } else {
         int[] quads = this._quadBuffer;
         quads[0] = q1;
         quads[1] = q2;
         quads[2] = _padLastQuad(q3, lastQuadBytes);
         return this._addName(quads, 3, lastQuadBytes);
      }
   }

   protected final String _addName(int[] quads, int qlen, int lastQuadBytes) throws JsonParseException {
      int byteLen = (qlen << 2) - 4 + lastQuadBytes;
      int lastQuad;
      if (lastQuadBytes < 4) {
         lastQuad = quads[qlen - 1];
         quads[qlen - 1] = lastQuad << (4 - lastQuadBytes << 3);
      } else {
         lastQuad = 0;
      }

      char[] cbuf = this._textBuffer.emptyAndGetCurrentSegment();
      int cix = 0;

      int ch;
      for(int ix = 0; ix < byteLen; cbuf[cix++] = (char)ch) {
         ch = quads[ix >> 2];
         int byteIx = ix & 3;
         ch = ch >> (3 - byteIx << 3) & 255;
         ++ix;
         if (ch > 127) {
            byte needed;
            if ((ch & 224) == 192) {
               ch &= 31;
               needed = 1;
            } else if ((ch & 240) == 224) {
               ch &= 15;
               needed = 2;
            } else if ((ch & 248) == 240) {
               ch &= 7;
               needed = 3;
            } else {
               this._reportInvalidInitial(ch);
               ch = 1;
               needed = 1;
            }

            if (ix + needed > byteLen) {
               this._reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
            }

            int ch2 = quads[ix >> 2];
            byteIx = ix & 3;
            ch2 >>= 3 - byteIx << 3;
            ++ix;
            if ((ch2 & 192) != 128) {
               this._reportInvalidOther(ch2);
            }

            ch = ch << 6 | ch2 & 63;
            if (needed > 1) {
               ch2 = quads[ix >> 2];
               byteIx = ix & 3;
               ch2 >>= 3 - byteIx << 3;
               ++ix;
               if ((ch2 & 192) != 128) {
                  this._reportInvalidOther(ch2);
               }

               ch = ch << 6 | ch2 & 63;
               if (needed > 2) {
                  ch2 = quads[ix >> 2];
                  byteIx = ix & 3;
                  ch2 >>= 3 - byteIx << 3;
                  ++ix;
                  if ((ch2 & 192) != 128) {
                     this._reportInvalidOther(ch2 & 255);
                  }

                  ch = ch << 6 | ch2 & 63;
               }
            }

            if (needed > 2) {
               ch -= 65536;
               if (cix >= cbuf.length) {
                  cbuf = this._textBuffer.expandCurrentSegment();
               }

               cbuf[cix++] = (char)('\ud800' + (ch >> 10));
               ch = '\udc00' | ch & 1023;
            }
         }

         if (cix >= cbuf.length) {
            cbuf = this._textBuffer.expandCurrentSegment();
         }
      }

      String baseName = new String(cbuf, 0, cix);
      if (lastQuadBytes < 4) {
         quads[qlen - 1] = lastQuad;
      }

      return this._symbols.addName(baseName, quads, qlen);
   }

   protected static final int _padLastQuad(int q, int bytes) {
      return bytes == 4 ? q : q | -1 << (bytes << 3);
   }

   protected final JsonToken _eofAsNextToken() throws IOException {
      this._majorState = 7;
      if (!this._parsingContext.inRoot()) {
         this._handleEOF();
      }

      this.close();
      return this._currToken = null;
   }

   protected final JsonToken _fieldComplete(String name) throws IOException {
      this._majorState = 4;
      this._parsingContext.setCurrentName(name);
      return this._currToken = JsonToken.FIELD_NAME;
   }

   protected final JsonToken _valueComplete(JsonToken t) throws IOException {
      this._majorState = this._majorStateAfterValue;
      this._currToken = t;
      return t;
   }

   protected final JsonToken _valueCompleteInt(int value, String asText) throws IOException {
      this._textBuffer.resetWithString(asText);
      this._intLength = asText.length();
      this._numTypesValid = 1;
      this._numberInt = value;
      this._majorState = this._majorStateAfterValue;
      JsonToken t = JsonToken.VALUE_NUMBER_INT;
      this._currToken = t;
      return t;
   }

   protected final JsonToken _valueNonStdNumberComplete(int type) throws IOException {
      String tokenStr = NON_STD_TOKENS[type];
      this._textBuffer.resetWithString(tokenStr);
      if (!this.isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
         this._reportError("Non-standard token '%s': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow", tokenStr);
      }

      this._intLength = 0;
      this._numTypesValid = 8;
      this._numberDouble = NON_STD_TOKEN_VALUES[type];
      this._majorState = this._majorStateAfterValue;
      return this._currToken = JsonToken.VALUE_NUMBER_FLOAT;
   }

   protected final String _nonStdToken(int type) {
      return NON_STD_TOKENS[type];
   }

   protected final void _updateTokenLocation() {
      this._tokenInputRow = Math.max(this._currInputRow, this._currInputRowAlt);
      int ptr = this._inputPtr;
      this._tokenInputCol = ptr - this._currInputRowStart;
      this._tokenInputTotal = this._currInputProcessed + (long)(ptr - this._currBufferStart);
   }

   protected void _reportInvalidChar(int c) throws JsonParseException {
      if (c < 32) {
         this._throwInvalidSpace(c);
      }

      this._reportInvalidInitial(c);
   }

   protected void _reportInvalidInitial(int mask) throws JsonParseException {
      this._reportError("Invalid UTF-8 start byte 0x" + Integer.toHexString(mask));
   }

   protected void _reportInvalidOther(int mask, int ptr) throws JsonParseException {
      this._inputPtr = ptr;
      this._reportInvalidOther(mask);
   }

   protected void _reportInvalidOther(int mask) throws JsonParseException {
      this._reportError("Invalid UTF-8 middle byte 0x" + Integer.toHexString(mask));
   }
}

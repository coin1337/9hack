package software.bernie.shadowed.fasterxml.jackson.core.json.async;

import java.io.IOException;
import java.io.OutputStream;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.async.ByteArrayFeeder;
import software.bernie.shadowed.fasterxml.jackson.core.io.CharTypes;
import software.bernie.shadowed.fasterxml.jackson.core.io.IOContext;
import software.bernie.shadowed.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
import software.bernie.shadowed.fasterxml.jackson.core.util.VersionUtil;

public class NonBlockingJsonParser extends NonBlockingJsonParserBase implements ByteArrayFeeder {
   private static final int[] _icUTF8 = CharTypes.getInputCodeUtf8();
   protected static final int[] _icLatin1 = CharTypes.getInputCodeLatin1();
   protected byte[] _inputBuffer;
   protected int _origBufferLen;

   public NonBlockingJsonParser(IOContext ctxt, int parserFeatures, ByteQuadsCanonicalizer sym) {
      super(ctxt, parserFeatures, sym);
      this._inputBuffer = NO_BYTES;
   }

   public ByteArrayFeeder getNonBlockingInputFeeder() {
      return this;
   }

   public final boolean needMoreInput() {
      return this._inputPtr >= this._inputEnd && !this._endOfInput;
   }

   public void feedInput(byte[] buf, int start, int end) throws IOException {
      if (this._inputPtr < this._inputEnd) {
         this._reportError("Still have %d undecoded bytes, should not call 'feedInput'", this._inputEnd - this._inputPtr);
      }

      if (end < start) {
         this._reportError("Input end (%d) may not be before start (%d)", end, start);
      }

      if (this._endOfInput) {
         this._reportError("Already closed, can not feed more input");
      }

      this._currInputProcessed += (long)this._origBufferLen;
      this._currInputRowStart = start - (this._inputEnd - this._currInputRowStart);
      this._inputBuffer = buf;
      this._inputPtr = start;
      this._inputEnd = end;
      this._origBufferLen = end - start;
   }

   public void endOfInput() {
      this._endOfInput = true;
   }

   public int releaseBuffered(OutputStream out) throws IOException {
      int avail = this._inputEnd - this._inputPtr;
      if (avail > 0) {
         out.write(this._inputBuffer, this._inputPtr, avail);
      }

      return avail;
   }

   protected char _decodeEscaped() throws IOException {
      VersionUtil.throwInternal();
      return ' ';
   }

   public JsonToken nextToken() throws IOException {
      if (this._inputPtr >= this._inputEnd) {
         if (this._closed) {
            return null;
         } else if (this._endOfInput) {
            return this._currToken == JsonToken.NOT_AVAILABLE ? this._finishTokenWithEOF() : this._eofAsNextToken();
         } else {
            return JsonToken.NOT_AVAILABLE;
         }
      } else if (this._currToken == JsonToken.NOT_AVAILABLE) {
         return this._finishToken();
      } else {
         this._numTypesValid = 0;
         this._tokenInputTotal = this._currInputProcessed + (long)this._inputPtr;
         this._binaryValue = null;
         int ch = this._inputBuffer[this._inputPtr++] & 255;
         switch(this._majorState) {
         case 0:
            return this._startDocument(ch);
         case 1:
            return this._startValue(ch);
         case 2:
            return this._startFieldName(ch);
         case 3:
            return this._startFieldNameAfterComma(ch);
         case 4:
            return this._startValueExpectColon(ch);
         case 5:
            return this._startValue(ch);
         case 6:
            return this._startValueExpectComma(ch);
         default:
            VersionUtil.throwInternal();
            return null;
         }
      }
   }

   protected final JsonToken _finishToken() throws IOException {
      switch(this._minorState) {
      case 1:
         return this._finishBOM(this._pending32);
      case 2:
      case 3:
      case 6:
      case 11:
      case 20:
      case 21:
      case 22:
      case 27:
      case 28:
      case 29:
      case 33:
      case 34:
      case 35:
      case 36:
      case 37:
      case 38:
      case 39:
      case 46:
      case 47:
      case 48:
      case 49:
      default:
         VersionUtil.throwInternal();
         return null;
      case 4:
         return this._startFieldName(this._inputBuffer[this._inputPtr++] & 255);
      case 5:
         return this._startFieldNameAfterComma(this._inputBuffer[this._inputPtr++] & 255);
      case 7:
         return this._parseEscapedName(this._quadLength, this._pending32, this._pendingBytes);
      case 8:
         return this._finishFieldWithEscape();
      case 9:
         return this._finishAposName(this._quadLength, this._pending32, this._pendingBytes);
      case 10:
         return this._finishUnquotedName(this._quadLength, this._pending32, this._pendingBytes);
      case 12:
         return this._startValue(this._inputBuffer[this._inputPtr++] & 255);
      case 13:
         return this._startValueExpectComma(this._inputBuffer[this._inputPtr++] & 255);
      case 14:
         return this._startValueExpectColon(this._inputBuffer[this._inputPtr++] & 255);
      case 15:
         return this._startValueAfterComma(this._inputBuffer[this._inputPtr++] & 255);
      case 16:
         return this._finishKeywordToken("null", this._pending32, JsonToken.VALUE_NULL);
      case 17:
         return this._finishKeywordToken("true", this._pending32, JsonToken.VALUE_TRUE);
      case 18:
         return this._finishKeywordToken("false", this._pending32, JsonToken.VALUE_FALSE);
      case 19:
         return this._finishNonStdToken(this._nonStdTokenType, this._pending32);
      case 23:
         return this._finishNumberMinus(this._inputBuffer[this._inputPtr++] & 255);
      case 24:
         return this._finishNumberLeadingZeroes();
      case 25:
         return this._finishNumberLeadingNegZeroes();
      case 26:
         return this._finishNumberIntegralPart(this._textBuffer.getBufferWithoutReset(), this._textBuffer.getCurrentSegmentSize());
      case 30:
         return this._finishFloatFraction();
      case 31:
         return this._finishFloatExponent(true, this._inputBuffer[this._inputPtr++] & 255);
      case 32:
         return this._finishFloatExponent(false, this._inputBuffer[this._inputPtr++] & 255);
      case 40:
         return this._finishRegularString();
      case 41:
         int c = this._decodeSplitEscaped(this._quoted32, this._quotedDigits);
         if (c < 0) {
            return JsonToken.NOT_AVAILABLE;
         } else {
            this._textBuffer.append((char)c);
            if (this._minorStateAfterSplit == 45) {
               return this._finishAposString();
            }

            return this._finishRegularString();
         }
      case 42:
         this._textBuffer.append((char)this._decodeUTF8_2(this._pending32, this._inputBuffer[this._inputPtr++]));
         if (this._minorStateAfterSplit == 45) {
            return this._finishAposString();
         }

         return this._finishRegularString();
      case 43:
         if (!this._decodeSplitUTF8_3(this._pending32, this._pendingBytes, this._inputBuffer[this._inputPtr++])) {
            return JsonToken.NOT_AVAILABLE;
         } else {
            if (this._minorStateAfterSplit == 45) {
               return this._finishAposString();
            }

            return this._finishRegularString();
         }
      case 44:
         if (!this._decodeSplitUTF8_4(this._pending32, this._pendingBytes, this._inputBuffer[this._inputPtr++])) {
            return JsonToken.NOT_AVAILABLE;
         } else {
            if (this._minorStateAfterSplit == 45) {
               return this._finishAposString();
            }

            return this._finishRegularString();
         }
      case 45:
         return this._finishAposString();
      case 50:
         return this._finishErrorToken();
      case 51:
         return this._startSlashComment(this._pending32);
      case 52:
         return this._finishCComment(this._pending32, true);
      case 53:
         return this._finishCComment(this._pending32, false);
      case 54:
         return this._finishCppComment(this._pending32);
      case 55:
         return this._finishHashComment(this._pending32);
      }
   }

   protected final JsonToken _finishTokenWithEOF() throws IOException {
      JsonToken t = this._currToken;
      switch(this._minorState) {
      case 3:
         return this._eofAsNextToken();
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
      case 13:
      case 14:
      case 15:
      case 20:
      case 21:
      case 22:
      case 23:
      case 27:
      case 28:
      case 29:
      case 33:
      case 34:
      case 35:
      case 36:
      case 37:
      case 38:
      case 39:
      case 40:
      case 41:
      case 42:
      case 43:
      case 44:
      case 45:
      case 46:
      case 47:
      case 48:
      case 49:
      case 51:
      default:
         this._reportInvalidEOF(": was expecting rest of token (internal state: " + this._minorState + ")", this._currToken);
         return t;
      case 12:
         return this._eofAsNextToken();
      case 16:
         return this._finishKeywordTokenWithEOF("null", this._pending32, JsonToken.VALUE_NULL);
      case 17:
         return this._finishKeywordTokenWithEOF("true", this._pending32, JsonToken.VALUE_TRUE);
      case 18:
         return this._finishKeywordTokenWithEOF("false", this._pending32, JsonToken.VALUE_FALSE);
      case 19:
         return this._finishNonStdTokenWithEOF(this._nonStdTokenType, this._pending32);
      case 24:
      case 25:
         return this._valueCompleteInt(0, "0");
      case 26:
         int len = this._textBuffer.getCurrentSegmentSize();
         if (this._numberNegative) {
            --len;
         }

         this._intLength = len;
         return this._valueComplete(JsonToken.VALUE_NUMBER_INT);
      case 30:
         this._expLength = 0;
      case 32:
         return this._valueComplete(JsonToken.VALUE_NUMBER_FLOAT);
      case 31:
         this._reportInvalidEOF(": was expecting fraction after exponent marker", JsonToken.VALUE_NUMBER_FLOAT);
      case 52:
      case 53:
         this._reportInvalidEOF(": was expecting closing '*/' for comment", JsonToken.NOT_AVAILABLE);
      case 54:
      case 55:
         return this._eofAsNextToken();
      case 50:
         return this._finishErrorTokenWithEOF();
      }
   }

   private final JsonToken _startDocument(int ch) throws IOException {
      ch &= 255;
      if (ch == 239 && this._minorState != 1) {
         return this._finishBOM(1);
      } else {
         while(ch <= 32) {
            if (ch != 32) {
               if (ch == 10) {
                  ++this._currInputRow;
                  this._currInputRowStart = this._inputPtr;
               } else if (ch == 13) {
                  ++this._currInputRowAlt;
                  this._currInputRowStart = this._inputPtr;
               } else if (ch != 9) {
                  this._throwInvalidSpace(ch);
               }
            }

            if (this._inputPtr >= this._inputEnd) {
               this._minorState = 3;
               if (this._closed) {
                  return null;
               }

               if (this._endOfInput) {
                  return this._eofAsNextToken();
               }

               return JsonToken.NOT_AVAILABLE;
            }

            ch = this._inputBuffer[this._inputPtr++] & 255;
         }

         return this._startValue(ch);
      }
   }

   private final JsonToken _finishBOM(int bytesHandled) throws IOException {
      for(; this._inputPtr < this._inputEnd; ++bytesHandled) {
         int ch = this._inputBuffer[this._inputPtr++] & 255;
         switch(bytesHandled) {
         case 1:
            if (ch != 187) {
               this._reportError("Unexpected byte 0x%02x following 0xEF; should get 0xBB as second byte UTF-8 BOM", ch);
            }
            break;
         case 2:
            if (ch != 191) {
               this._reportError("Unexpected byte 0x%02x following 0xEF 0xBB; should get 0xBF as third byte of UTF-8 BOM", ch);
            }
            break;
         case 3:
            this._currInputProcessed -= 3L;
            return this._startDocument(ch);
         }
      }

      this._pending32 = bytesHandled;
      this._minorState = 1;
      return this._currToken = JsonToken.NOT_AVAILABLE;
   }

   private final JsonToken _startFieldName(int ch) throws IOException {
      if (ch <= 32) {
         ch = this._skipWS(ch);
         if (ch <= 0) {
            this._minorState = 4;
            return this._currToken;
         }
      }

      this._updateTokenLocation();
      if (ch != 34) {
         return ch == 125 ? this._closeObjectScope() : this._handleOddName(ch);
      } else {
         if (this._inputPtr + 13 <= this._inputEnd) {
            String n = this._fastParseName();
            if (n != null) {
               return this._fieldComplete(n);
            }
         }

         return this._parseEscapedName(0, 0, 0);
      }
   }

   private final JsonToken _startFieldNameAfterComma(int ch) throws IOException {
      if (ch <= 32) {
         ch = this._skipWS(ch);
         if (ch <= 0) {
            this._minorState = 5;
            return this._currToken;
         }
      }

      if (ch != 44) {
         if (ch == 125) {
            return this._closeObjectScope();
         }

         if (ch == 35) {
            return this._finishHashComment(5);
         }

         if (ch == 47) {
            return this._startSlashComment(5);
         }

         this._reportUnexpectedChar(ch, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
      }

      int ptr = this._inputPtr;
      if (ptr >= this._inputEnd) {
         this._minorState = 4;
         return this._currToken = JsonToken.NOT_AVAILABLE;
      } else {
         ch = this._inputBuffer[ptr];
         this._inputPtr = ptr + 1;
         if (ch <= 32) {
            ch = this._skipWS(ch);
            if (ch <= 0) {
               this._minorState = 4;
               return this._currToken;
            }
         }

         this._updateTokenLocation();
         if (ch != 34) {
            return ch == 125 && JsonParser.Feature.ALLOW_TRAILING_COMMA.enabledIn(this._features) ? this._closeObjectScope() : this._handleOddName(ch);
         } else {
            if (this._inputPtr + 13 <= this._inputEnd) {
               String n = this._fastParseName();
               if (n != null) {
                  return this._fieldComplete(n);
               }
            }

            return this._parseEscapedName(0, 0, 0);
         }
      }
   }

   private final JsonToken _startValue(int ch) throws IOException {
      if (ch <= 32) {
         ch = this._skipWS(ch);
         if (ch <= 0) {
            this._minorState = 12;
            return this._currToken;
         }
      }

      this._updateTokenLocation();
      if (ch == 34) {
         return this._startString();
      } else {
         switch(ch) {
         case 35:
            return this._finishHashComment(12);
         case 45:
            return this._startNegativeNumber();
         case 47:
            return this._startSlashComment(12);
         case 48:
            return this._startNumberLeadingZero();
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
            return this._startPositiveNumber(ch);
         case 91:
            return this._startArrayScope();
         case 93:
            return this._closeArrayScope();
         case 102:
            return this._startFalseToken();
         case 110:
            return this._startNullToken();
         case 116:
            return this._startTrueToken();
         case 123:
            return this._startObjectScope();
         case 125:
            return this._closeObjectScope();
         default:
            return this._startUnexpectedValue(false, ch);
         }
      }
   }

   private final JsonToken _startValueExpectComma(int ch) throws IOException {
      if (ch <= 32) {
         ch = this._skipWS(ch);
         if (ch <= 0) {
            this._minorState = 13;
            return this._currToken;
         }
      }

      if (ch != 44) {
         if (ch == 93) {
            return this._closeArrayScope();
         }

         if (ch == 125) {
            return this._closeObjectScope();
         }

         if (ch == 47) {
            return this._startSlashComment(13);
         }

         if (ch == 35) {
            return this._finishHashComment(13);
         }

         this._reportUnexpectedChar(ch, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
      }

      int ptr = this._inputPtr;
      if (ptr >= this._inputEnd) {
         this._minorState = 15;
         return this._currToken = JsonToken.NOT_AVAILABLE;
      } else {
         ch = this._inputBuffer[ptr];
         this._inputPtr = ptr + 1;
         if (ch <= 32) {
            ch = this._skipWS(ch);
            if (ch <= 0) {
               this._minorState = 15;
               return this._currToken;
            }
         }

         this._updateTokenLocation();
         if (ch == 34) {
            return this._startString();
         } else {
            switch(ch) {
            case 35:
               return this._finishHashComment(15);
            case 45:
               return this._startNegativeNumber();
            case 47:
               return this._startSlashComment(15);
            case 48:
               return this._startNumberLeadingZero();
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
               return this._startPositiveNumber(ch);
            case 91:
               return this._startArrayScope();
            case 93:
               if (this.isEnabled(JsonParser.Feature.ALLOW_TRAILING_COMMA)) {
                  return this._closeArrayScope();
               }
               break;
            case 102:
               return this._startFalseToken();
            case 110:
               return this._startNullToken();
            case 116:
               return this._startTrueToken();
            case 123:
               return this._startObjectScope();
            case 125:
               if (this.isEnabled(JsonParser.Feature.ALLOW_TRAILING_COMMA)) {
                  return this._closeObjectScope();
               }
            }

            return this._startUnexpectedValue(true, ch);
         }
      }
   }

   private final JsonToken _startValueExpectColon(int ch) throws IOException {
      if (ch <= 32) {
         ch = this._skipWS(ch);
         if (ch <= 0) {
            this._minorState = 14;
            return this._currToken;
         }
      }

      if (ch != 58) {
         if (ch == 47) {
            return this._startSlashComment(14);
         }

         if (ch == 35) {
            return this._finishHashComment(14);
         }

         this._reportUnexpectedChar(ch, "was expecting a colon to separate field name and value");
      }

      int ptr = this._inputPtr;
      if (ptr >= this._inputEnd) {
         this._minorState = 12;
         return this._currToken = JsonToken.NOT_AVAILABLE;
      } else {
         ch = this._inputBuffer[ptr];
         this._inputPtr = ptr + 1;
         if (ch <= 32) {
            ch = this._skipWS(ch);
            if (ch <= 0) {
               this._minorState = 12;
               return this._currToken;
            }
         }

         this._updateTokenLocation();
         if (ch == 34) {
            return this._startString();
         } else {
            switch(ch) {
            case 35:
               return this._finishHashComment(12);
            case 45:
               return this._startNegativeNumber();
            case 47:
               return this._startSlashComment(12);
            case 48:
               return this._startNumberLeadingZero();
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
               return this._startPositiveNumber(ch);
            case 91:
               return this._startArrayScope();
            case 102:
               return this._startFalseToken();
            case 110:
               return this._startNullToken();
            case 116:
               return this._startTrueToken();
            case 123:
               return this._startObjectScope();
            default:
               return this._startUnexpectedValue(false, ch);
            }
         }
      }
   }

   private final JsonToken _startValueAfterComma(int ch) throws IOException {
      if (ch <= 32) {
         ch = this._skipWS(ch);
         if (ch <= 0) {
            this._minorState = 15;
            return this._currToken;
         }
      }

      this._updateTokenLocation();
      if (ch == 34) {
         return this._startString();
      } else {
         switch(ch) {
         case 35:
            return this._finishHashComment(15);
         case 45:
            return this._startNegativeNumber();
         case 47:
            return this._startSlashComment(15);
         case 48:
            return this._startNumberLeadingZero();
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
            return this._startPositiveNumber(ch);
         case 91:
            return this._startArrayScope();
         case 93:
            if (this.isEnabled(JsonParser.Feature.ALLOW_TRAILING_COMMA)) {
               return this._closeArrayScope();
            }
            break;
         case 102:
            return this._startFalseToken();
         case 110:
            return this._startNullToken();
         case 116:
            return this._startTrueToken();
         case 123:
            return this._startObjectScope();
         case 125:
            if (this.isEnabled(JsonParser.Feature.ALLOW_TRAILING_COMMA)) {
               return this._closeObjectScope();
            }
         }

         return this._startUnexpectedValue(true, ch);
      }
   }

   protected JsonToken _startUnexpectedValue(boolean leadingComma, int ch) throws IOException {
      switch(ch) {
      case 39:
         if (this.isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES)) {
            return this._startAposString();
         }
         break;
      case 43:
         return this._finishNonStdToken(2, 1);
      case 73:
         return this._finishNonStdToken(1, 1);
      case 78:
         return this._finishNonStdToken(0, 1);
      case 93:
         if (!this._parsingContext.inArray()) {
            break;
         }
      case 44:
         if (this.isEnabled(JsonParser.Feature.ALLOW_MISSING_VALUES)) {
            --this._inputPtr;
            return this._valueComplete(JsonToken.VALUE_NULL);
         }
      case 125:
      }

      this._reportUnexpectedChar(ch, "expected a valid value (number, String, array, object, 'true', 'false' or 'null')");
      return null;
   }

   private final int _skipWS(int ch) throws IOException {
      do {
         if (ch != 32) {
            if (ch == 10) {
               ++this._currInputRow;
               this._currInputRowStart = this._inputPtr;
            } else if (ch == 13) {
               ++this._currInputRowAlt;
               this._currInputRowStart = this._inputPtr;
            } else if (ch != 9) {
               this._throwInvalidSpace(ch);
            }
         }

         if (this._inputPtr >= this._inputEnd) {
            this._currToken = JsonToken.NOT_AVAILABLE;
            return 0;
         }

         ch = this._inputBuffer[this._inputPtr++] & 255;
      } while(ch <= 32);

      return ch;
   }

   private final JsonToken _startSlashComment(int fromMinorState) throws IOException {
      if (!JsonParser.Feature.ALLOW_COMMENTS.enabledIn(this._features)) {
         this._reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
      }

      if (this._inputPtr >= this._inputEnd) {
         this._pending32 = fromMinorState;
         this._minorState = 51;
         return this._currToken = JsonToken.NOT_AVAILABLE;
      } else {
         int ch = this._inputBuffer[this._inputPtr++];
         if (ch == 42) {
            return this._finishCComment(fromMinorState, false);
         } else if (ch == 47) {
            return this._finishCppComment(fromMinorState);
         } else {
            this._reportUnexpectedChar(ch & 255, "was expecting either '*' or '/' for a comment");
            return null;
         }
      }
   }

   private final JsonToken _finishHashComment(int fromMinorState) throws IOException {
      if (!JsonParser.Feature.ALLOW_YAML_COMMENTS.enabledIn(this._features)) {
         this._reportUnexpectedChar(35, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_YAML_COMMENTS' not enabled for parser)");
      }

      while(true) {
         int ch;
         do {
            if (this._inputPtr >= this._inputEnd) {
               this._minorState = 55;
               this._pending32 = fromMinorState;
               return this._currToken = JsonToken.NOT_AVAILABLE;
            }

            ch = this._inputBuffer[this._inputPtr++] & 255;
         } while(ch >= 32);

         if (ch == 10) {
            ++this._currInputRow;
            this._currInputRowStart = this._inputPtr;
            break;
         }

         if (ch == 13) {
            ++this._currInputRowAlt;
            this._currInputRowStart = this._inputPtr;
            break;
         }

         if (ch != 9) {
            this._throwInvalidSpace(ch);
         }
      }

      return this._startAfterComment(fromMinorState);
   }

   private final JsonToken _finishCppComment(int fromMinorState) throws IOException {
      while(this._inputPtr < this._inputEnd) {
         int ch = this._inputBuffer[this._inputPtr++] & 255;
         if (ch < 32) {
            if (ch == 10) {
               ++this._currInputRow;
               this._currInputRowStart = this._inputPtr;
            } else {
               if (ch != 13) {
                  if (ch != 9) {
                     this._throwInvalidSpace(ch);
                  }
                  continue;
               }

               ++this._currInputRowAlt;
               this._currInputRowStart = this._inputPtr;
            }

            return this._startAfterComment(fromMinorState);
         }
      }

      this._minorState = 54;
      this._pending32 = fromMinorState;
      return this._currToken = JsonToken.NOT_AVAILABLE;
   }

   private final JsonToken _finishCComment(int fromMinorState, boolean gotStar) throws IOException {
      while(this._inputPtr < this._inputEnd) {
         int ch = this._inputBuffer[this._inputPtr++] & 255;
         if (ch < 32) {
            if (ch == 10) {
               ++this._currInputRow;
               this._currInputRowStart = this._inputPtr;
            } else if (ch == 13) {
               ++this._currInputRowAlt;
               this._currInputRowStart = this._inputPtr;
            } else if (ch != 9) {
               this._throwInvalidSpace(ch);
            }
         } else {
            if (ch == 42) {
               gotStar = true;
               continue;
            }

            if (ch == 47 && gotStar) {
               return this._startAfterComment(fromMinorState);
            }
         }

         gotStar = false;
      }

      this._minorState = gotStar ? 52 : 53;
      this._pending32 = fromMinorState;
      return this._currToken = JsonToken.NOT_AVAILABLE;
   }

   private final JsonToken _startAfterComment(int fromMinorState) throws IOException {
      if (this._inputPtr >= this._inputEnd) {
         this._minorState = fromMinorState;
         return this._currToken = JsonToken.NOT_AVAILABLE;
      } else {
         int ch = this._inputBuffer[this._inputPtr++] & 255;
         switch(fromMinorState) {
         case 4:
            return this._startFieldName(ch);
         case 5:
            return this._startFieldNameAfterComma(ch);
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         default:
            VersionUtil.throwInternal();
            return null;
         case 12:
            return this._startValue(ch);
         case 13:
            return this._startValueExpectComma(ch);
         case 14:
            return this._startValueExpectColon(ch);
         case 15:
            return this._startValueAfterComma(ch);
         }
      }
   }

   protected JsonToken _startFalseToken() throws IOException {
      int ptr = this._inputPtr;
      if (ptr + 4 < this._inputEnd) {
         byte[] buf = this._inputBuffer;
         if (buf[ptr++] == 97 && buf[ptr++] == 108 && buf[ptr++] == 115 && buf[ptr++] == 101) {
            int ch = buf[ptr] & 255;
            if (ch < 48 || ch == 93 || ch == 125) {
               this._inputPtr = ptr;
               return this._valueComplete(JsonToken.VALUE_FALSE);
            }
         }
      }

      this._minorState = 18;
      return this._finishKeywordToken("false", 1, JsonToken.VALUE_FALSE);
   }

   protected JsonToken _startTrueToken() throws IOException {
      int ptr = this._inputPtr;
      if (ptr + 3 < this._inputEnd) {
         byte[] buf = this._inputBuffer;
         if (buf[ptr++] == 114 && buf[ptr++] == 117 && buf[ptr++] == 101) {
            int ch = buf[ptr] & 255;
            if (ch < 48 || ch == 93 || ch == 125) {
               this._inputPtr = ptr;
               return this._valueComplete(JsonToken.VALUE_TRUE);
            }
         }
      }

      this._minorState = 17;
      return this._finishKeywordToken("true", 1, JsonToken.VALUE_TRUE);
   }

   protected JsonToken _startNullToken() throws IOException {
      int ptr = this._inputPtr;
      if (ptr + 3 < this._inputEnd) {
         byte[] buf = this._inputBuffer;
         if (buf[ptr++] == 117 && buf[ptr++] == 108 && buf[ptr++] == 108) {
            int ch = buf[ptr] & 255;
            if (ch < 48 || ch == 93 || ch == 125) {
               this._inputPtr = ptr;
               return this._valueComplete(JsonToken.VALUE_NULL);
            }
         }
      }

      this._minorState = 16;
      return this._finishKeywordToken("null", 1, JsonToken.VALUE_NULL);
   }

   protected JsonToken _finishKeywordToken(String expToken, int matched, JsonToken result) throws IOException {
      int end = expToken.length();

      while(this._inputPtr < this._inputEnd) {
         int ch = this._inputBuffer[this._inputPtr];
         if (matched == end) {
            if (ch < 48 || ch == 93 || ch == 125) {
               return this._valueComplete(result);
            }
         } else if (ch == expToken.charAt(matched)) {
            ++matched;
            ++this._inputPtr;
            continue;
         }

         this._minorState = 50;
         this._textBuffer.resetWithCopy((String)expToken, 0, matched);
         return this._finishErrorToken();
      }

      this._pending32 = matched;
      return this._currToken = JsonToken.NOT_AVAILABLE;
   }

   protected JsonToken _finishKeywordTokenWithEOF(String expToken, int matched, JsonToken result) throws IOException {
      if (matched == expToken.length()) {
         return this._currToken = result;
      } else {
         this._textBuffer.resetWithCopy((String)expToken, 0, matched);
         return this._finishErrorTokenWithEOF();
      }
   }

   protected JsonToken _finishNonStdToken(int type, int matched) throws IOException {
      String expToken = this._nonStdToken(type);
      int end = expToken.length();

      while(this._inputPtr < this._inputEnd) {
         int ch = this._inputBuffer[this._inputPtr];
         if (matched == end) {
            if (ch < 48 || ch == 93 || ch == 125) {
               return this._valueNonStdNumberComplete(type);
            }
         } else if (ch == expToken.charAt(matched)) {
            ++matched;
            ++this._inputPtr;
            continue;
         }

         this._minorState = 50;
         this._textBuffer.resetWithCopy((String)expToken, 0, matched);
         return this._finishErrorToken();
      }

      this._nonStdTokenType = type;
      this._pending32 = matched;
      this._minorState = 19;
      return this._currToken = JsonToken.NOT_AVAILABLE;
   }

   protected JsonToken _finishNonStdTokenWithEOF(int type, int matched) throws IOException {
      String expToken = this._nonStdToken(type);
      if (matched == expToken.length()) {
         return this._valueNonStdNumberComplete(type);
      } else {
         this._textBuffer.resetWithCopy((String)expToken, 0, matched);
         return this._finishErrorTokenWithEOF();
      }
   }

   protected JsonToken _finishErrorToken() throws IOException {
      while(true) {
         if (this._inputPtr < this._inputEnd) {
            int i = this._inputBuffer[this._inputPtr++];
            char ch = (char)i;
            if (Character.isJavaIdentifierPart(ch)) {
               this._textBuffer.append(ch);
               if (this._textBuffer.size() < 256) {
                  continue;
               }
            }

            return this._reportErrorToken(this._textBuffer.contentsAsString());
         }

         return this._currToken = JsonToken.NOT_AVAILABLE;
      }
   }

   protected JsonToken _finishErrorTokenWithEOF() throws IOException {
      return this._reportErrorToken(this._textBuffer.contentsAsString());
   }

   protected JsonToken _reportErrorToken(String actualToken) throws IOException {
      this._reportError("Unrecognized token '%s': was expecting %s", this._textBuffer.contentsAsString(), "'null', 'true' or 'false'");
      return JsonToken.NOT_AVAILABLE;
   }

   protected JsonToken _startPositiveNumber(int ch) throws IOException {
      this._numberNegative = false;
      char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
      outBuf[0] = (char)ch;
      if (this._inputPtr >= this._inputEnd) {
         this._minorState = 26;
         this._textBuffer.setCurrentLength(1);
         return this._currToken = JsonToken.NOT_AVAILABLE;
      } else {
         int outPtr = 1;
         ch = this._inputBuffer[this._inputPtr] & 255;

         while(true) {
            if (ch < 48) {
               if (ch == 46) {
                  this._intLength = outPtr;
                  ++this._inputPtr;
                  return this._startFloat(outBuf, outPtr, ch);
               }
               break;
            }

            if (ch > 57) {
               if (ch != 101 && ch != 69) {
                  break;
               }

               this._intLength = outPtr;
               ++this._inputPtr;
               return this._startFloat(outBuf, outPtr, ch);
            }

            if (outPtr >= outBuf.length) {
               outBuf = this._textBuffer.expandCurrentSegment();
            }

            outBuf[outPtr++] = (char)ch;
            if (++this._inputPtr >= this._inputEnd) {
               this._minorState = 26;
               this._textBuffer.setCurrentLength(outPtr);
               return this._currToken = JsonToken.NOT_AVAILABLE;
            }

            ch = this._inputBuffer[this._inputPtr] & 255;
         }

         this._intLength = outPtr;
         this._textBuffer.setCurrentLength(outPtr);
         return this._valueComplete(JsonToken.VALUE_NUMBER_INT);
      }
   }

   protected JsonToken _startNegativeNumber() throws IOException {
      this._numberNegative = true;
      if (this._inputPtr >= this._inputEnd) {
         this._minorState = 23;
         return this._currToken = JsonToken.NOT_AVAILABLE;
      } else {
         int ch = this._inputBuffer[this._inputPtr++] & 255;
         if (ch <= 48) {
            if (ch == 48) {
               return this._finishNumberLeadingNegZeroes();
            }

            this.reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
         } else if (ch > 57) {
            if (ch == 73) {
               return this._finishNonStdToken(3, 2);
            }

            this.reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
         }

         char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
         outBuf[0] = '-';
         outBuf[1] = (char)ch;
         if (this._inputPtr >= this._inputEnd) {
            this._minorState = 26;
            this._textBuffer.setCurrentLength(2);
            this._intLength = 1;
            return this._currToken = JsonToken.NOT_AVAILABLE;
         } else {
            ch = this._inputBuffer[this._inputPtr];
            int outPtr = 2;

            while(true) {
               if (ch < 48) {
                  if (ch == 46) {
                     this._intLength = outPtr - 1;
                     ++this._inputPtr;
                     return this._startFloat(outBuf, outPtr, ch);
                  }
               } else {
                  if (ch <= 57) {
                     if (outPtr >= outBuf.length) {
                        outBuf = this._textBuffer.expandCurrentSegment();
                     }

                     outBuf[outPtr++] = (char)ch;
                     if (++this._inputPtr >= this._inputEnd) {
                        this._minorState = 26;
                        this._textBuffer.setCurrentLength(outPtr);
                        return this._currToken = JsonToken.NOT_AVAILABLE;
                     }

                     ch = this._inputBuffer[this._inputPtr] & 255;
                     continue;
                  }

                  if (ch == 101 || ch == 69) {
                     this._intLength = outPtr - 1;
                     ++this._inputPtr;
                     return this._startFloat(outBuf, outPtr, ch);
                  }
               }

               this._intLength = outPtr - 1;
               this._textBuffer.setCurrentLength(outPtr);
               return this._valueComplete(JsonToken.VALUE_NUMBER_INT);
            }
         }
      }
   }

   protected JsonToken _startNumberLeadingZero() throws IOException {
      int ptr = this._inputPtr;
      if (ptr >= this._inputEnd) {
         this._minorState = 24;
         return this._currToken = JsonToken.NOT_AVAILABLE;
      } else {
         int ch = this._inputBuffer[ptr++] & 255;
         char[] outBuf;
         if (ch < 48) {
            if (ch == 46) {
               this._inputPtr = ptr;
               this._intLength = 1;
               outBuf = this._textBuffer.emptyAndGetCurrentSegment();
               outBuf[0] = '0';
               return this._startFloat(outBuf, 1, ch);
            }
         } else {
            if (ch <= 57) {
               return this._finishNumberLeadingZeroes();
            }

            if (ch == 101 || ch == 69) {
               this._inputPtr = ptr;
               this._intLength = 1;
               outBuf = this._textBuffer.emptyAndGetCurrentSegment();
               outBuf[0] = '0';
               return this._startFloat(outBuf, 1, ch);
            }

            if (ch != 93 && ch != 125) {
               this.reportUnexpectedNumberChar(ch, "expected digit (0-9), decimal point (.) or exponent indicator (e/E) to follow '0'");
            }
         }

         return this._valueCompleteInt(0, "0");
      }
   }

   protected JsonToken _finishNumberMinus(int ch) throws IOException {
      if (ch <= 48) {
         if (ch == 48) {
            return this._finishNumberLeadingNegZeroes();
         }

         this.reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
      } else if (ch > 57) {
         if (ch == 73) {
            return this._finishNonStdToken(3, 2);
         }

         this.reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
      }

      char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
      outBuf[0] = '-';
      outBuf[1] = (char)ch;
      this._intLength = 1;
      return this._finishNumberIntegralPart(outBuf, 2);
   }

   protected JsonToken _finishNumberLeadingZeroes() throws IOException {
      while(this._inputPtr < this._inputEnd) {
         int ch = this._inputBuffer[this._inputPtr++] & 255;
         char[] outBuf;
         if (ch < 48) {
            if (ch == 46) {
               outBuf = this._textBuffer.emptyAndGetCurrentSegment();
               outBuf[0] = '0';
               this._intLength = 1;
               return this._startFloat(outBuf, 1, ch);
            }
         } else {
            if (ch <= 57) {
               if (!this.isEnabled(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS)) {
                  this.reportInvalidNumber("Leading zeroes not allowed");
               }

               if (ch == 48) {
                  continue;
               }

               outBuf = this._textBuffer.emptyAndGetCurrentSegment();
               outBuf[0] = (char)ch;
               this._intLength = 1;
               return this._finishNumberIntegralPart(outBuf, 1);
            }

            if (ch == 101 || ch == 69) {
               outBuf = this._textBuffer.emptyAndGetCurrentSegment();
               outBuf[0] = '0';
               this._intLength = 1;
               return this._startFloat(outBuf, 1, ch);
            }

            if (ch != 93 && ch != 125) {
               this.reportUnexpectedNumberChar(ch, "expected digit (0-9), decimal point (.) or exponent indicator (e/E) to follow '0'");
            }
         }

         --this._inputPtr;
         return this._valueCompleteInt(0, "0");
      }

      this._minorState = 24;
      return this._currToken = JsonToken.NOT_AVAILABLE;
   }

   protected JsonToken _finishNumberLeadingNegZeroes() throws IOException {
      while(this._inputPtr < this._inputEnd) {
         int ch = this._inputBuffer[this._inputPtr++] & 255;
         char[] outBuf;
         if (ch < 48) {
            if (ch == 46) {
               outBuf = this._textBuffer.emptyAndGetCurrentSegment();
               outBuf[0] = '-';
               outBuf[1] = '0';
               this._intLength = 1;
               return this._startFloat(outBuf, 2, ch);
            }
         } else {
            if (ch <= 57) {
               if (!this.isEnabled(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS)) {
                  this.reportInvalidNumber("Leading zeroes not allowed");
               }

               if (ch == 48) {
                  continue;
               }

               outBuf = this._textBuffer.emptyAndGetCurrentSegment();
               outBuf[0] = '-';
               outBuf[1] = (char)ch;
               this._intLength = 1;
               return this._finishNumberIntegralPart(outBuf, 2);
            }

            if (ch == 101 || ch == 69) {
               outBuf = this._textBuffer.emptyAndGetCurrentSegment();
               outBuf[0] = '-';
               outBuf[1] = '0';
               this._intLength = 1;
               return this._startFloat(outBuf, 2, ch);
            }

            if (ch != 93 && ch != 125) {
               this.reportUnexpectedNumberChar(ch, "expected digit (0-9), decimal point (.) or exponent indicator (e/E) to follow '0'");
            }
         }

         --this._inputPtr;
         return this._valueCompleteInt(0, "0");
      }

      this._minorState = 25;
      return this._currToken = JsonToken.NOT_AVAILABLE;
   }

   protected JsonToken _finishNumberIntegralPart(char[] outBuf, int outPtr) throws IOException {
      int negMod = this._numberNegative ? -1 : 0;

      while(this._inputPtr < this._inputEnd) {
         int ch = this._inputBuffer[this._inputPtr] & 255;
         if (ch < 48) {
            if (ch == 46) {
               this._intLength = outPtr + negMod;
               ++this._inputPtr;
               return this._startFloat(outBuf, outPtr, ch);
            }
         } else {
            if (ch <= 57) {
               ++this._inputPtr;
               if (outPtr >= outBuf.length) {
                  outBuf = this._textBuffer.expandCurrentSegment();
               }

               outBuf[outPtr++] = (char)ch;
               continue;
            }

            if (ch == 101 || ch == 69) {
               this._intLength = outPtr + negMod;
               ++this._inputPtr;
               return this._startFloat(outBuf, outPtr, ch);
            }
         }

         this._intLength = outPtr + negMod;
         this._textBuffer.setCurrentLength(outPtr);
         return this._valueComplete(JsonToken.VALUE_NUMBER_INT);
      }

      this._minorState = 26;
      this._textBuffer.setCurrentLength(outPtr);
      return this._currToken = JsonToken.NOT_AVAILABLE;
   }

   protected JsonToken _startFloat(char[] outBuf, int outPtr, int ch) throws IOException {
      int fractLen = 0;
      byte ch;
      if (ch == 46) {
         if (outPtr >= outBuf.length) {
            outBuf = this._textBuffer.expandCurrentSegment();
         }

         outBuf[outPtr++] = '.';

         while(true) {
            if (this._inputPtr >= this._inputEnd) {
               this._textBuffer.setCurrentLength(outPtr);
               this._minorState = 30;
               this._fractLength = fractLen;
               return this._currToken = JsonToken.NOT_AVAILABLE;
            }

            ch = this._inputBuffer[this._inputPtr++];
            if (ch < 48 || ch > 57) {
               ch = ch & 255;
               if (fractLen == 0) {
                  this.reportUnexpectedNumberChar(ch, "Decimal point not followed by a digit");
               }
               break;
            }

            if (outPtr >= outBuf.length) {
               outBuf = this._textBuffer.expandCurrentSegment();
            }

            outBuf[outPtr++] = (char)ch;
            ++fractLen;
         }
      }

      this._fractLength = fractLen;
      int expLen = 0;
      if (ch == 101 || ch == 69) {
         if (outPtr >= outBuf.length) {
            outBuf = this._textBuffer.expandCurrentSegment();
         }

         outBuf[outPtr++] = (char)ch;
         if (this._inputPtr >= this._inputEnd) {
            this._textBuffer.setCurrentLength(outPtr);
            this._minorState = 31;
            this._expLength = 0;
            return this._currToken = JsonToken.NOT_AVAILABLE;
         }

         ch = this._inputBuffer[this._inputPtr++];
         if (ch == 45 || ch == 43) {
            if (outPtr >= outBuf.length) {
               outBuf = this._textBuffer.expandCurrentSegment();
            }

            outBuf[outPtr++] = (char)ch;
            if (this._inputPtr >= this._inputEnd) {
               this._textBuffer.setCurrentLength(outPtr);
               this._minorState = 32;
               this._expLength = 0;
               return this._currToken = JsonToken.NOT_AVAILABLE;
            }

            ch = this._inputBuffer[this._inputPtr++];
         }

         while(ch >= 48 && ch <= 57) {
            ++expLen;
            if (outPtr >= outBuf.length) {
               outBuf = this._textBuffer.expandCurrentSegment();
            }

            outBuf[outPtr++] = (char)ch;
            if (this._inputPtr >= this._inputEnd) {
               this._textBuffer.setCurrentLength(outPtr);
               this._minorState = 32;
               this._expLength = expLen;
               return this._currToken = JsonToken.NOT_AVAILABLE;
            }

            ch = this._inputBuffer[this._inputPtr++];
         }

         ch = ch & 255;
         if (expLen == 0) {
            this.reportUnexpectedNumberChar(ch, "Exponent indicator not followed by a digit");
         }
      }

      --this._inputPtr;
      this._textBuffer.setCurrentLength(outPtr);
      this._expLength = expLen;
      return this._valueComplete(JsonToken.VALUE_NUMBER_FLOAT);
   }

   protected JsonToken _finishFloatFraction() throws IOException {
      int fractLen = this._fractLength;
      char[] outBuf = this._textBuffer.getBufferWithoutReset();
      int outPtr = this._textBuffer.getCurrentSegmentSize();

      byte ch;
      while((ch = this._inputBuffer[this._inputPtr++]) >= 48 && ch <= 57) {
         ++fractLen;
         if (outPtr >= outBuf.length) {
            outBuf = this._textBuffer.expandCurrentSegment();
         }

         outBuf[outPtr++] = (char)ch;
         if (this._inputPtr >= this._inputEnd) {
            this._textBuffer.setCurrentLength(outPtr);
            this._fractLength = fractLen;
            return JsonToken.NOT_AVAILABLE;
         }
      }

      if (fractLen == 0) {
         this.reportUnexpectedNumberChar(ch, "Decimal point not followed by a digit");
      }

      this._fractLength = fractLen;
      this._textBuffer.setCurrentLength(outPtr);
      if (ch != 101 && ch != 69) {
         --this._inputPtr;
         this._textBuffer.setCurrentLength(outPtr);
         this._expLength = 0;
         return this._valueComplete(JsonToken.VALUE_NUMBER_FLOAT);
      } else {
         this._textBuffer.append((char)ch);
         this._expLength = 0;
         if (this._inputPtr >= this._inputEnd) {
            this._minorState = 31;
            return JsonToken.NOT_AVAILABLE;
         } else {
            this._minorState = 32;
            return this._finishFloatExponent(true, this._inputBuffer[this._inputPtr++] & 255);
         }
      }
   }

   protected JsonToken _finishFloatExponent(boolean checkSign, int ch) throws IOException {
      if (checkSign) {
         this._minorState = 32;
         if (ch == 45 || ch == 43) {
            this._textBuffer.append((char)ch);
            if (this._inputPtr >= this._inputEnd) {
               this._minorState = 32;
               this._expLength = 0;
               return JsonToken.NOT_AVAILABLE;
            }

            ch = this._inputBuffer[this._inputPtr++];
         }
      }

      char[] outBuf = this._textBuffer.getBufferWithoutReset();
      int outPtr = this._textBuffer.getCurrentSegmentSize();

      int expLen;
      for(expLen = this._expLength; ch >= 48 && ch <= 57; ch = this._inputBuffer[this._inputPtr++]) {
         ++expLen;
         if (outPtr >= outBuf.length) {
            outBuf = this._textBuffer.expandCurrentSegment();
         }

         outBuf[outPtr++] = (char)ch;
         if (this._inputPtr >= this._inputEnd) {
            this._textBuffer.setCurrentLength(outPtr);
            this._expLength = expLen;
            return JsonToken.NOT_AVAILABLE;
         }
      }

      ch &= 255;
      if (expLen == 0) {
         this.reportUnexpectedNumberChar(ch, "Exponent indicator not followed by a digit");
      }

      --this._inputPtr;
      this._textBuffer.setCurrentLength(outPtr);
      this._expLength = expLen;
      return this._valueComplete(JsonToken.VALUE_NUMBER_FLOAT);
   }

   private final String _fastParseName() throws IOException {
      byte[] input = this._inputBuffer;
      int[] codes = _icLatin1;
      int ptr = this._inputPtr;
      int q0 = input[ptr++] & 255;
      if (codes[q0] == 0) {
         int i = input[ptr++] & 255;
         if (codes[i] == 0) {
            int q = q0 << 8 | i;
            i = input[ptr++] & 255;
            if (codes[i] == 0) {
               q = q << 8 | i;
               i = input[ptr++] & 255;
               if (codes[i] == 0) {
                  q = q << 8 | i;
                  i = input[ptr++] & 255;
                  if (codes[i] == 0) {
                     this._quad1 = q;
                     return this._parseMediumName(ptr, i);
                  } else if (i == 34) {
                     this._inputPtr = ptr;
                     return this._findName(q, 4);
                  } else {
                     return null;
                  }
               } else if (i == 34) {
                  this._inputPtr = ptr;
                  return this._findName(q, 3);
               } else {
                  return null;
               }
            } else if (i == 34) {
               this._inputPtr = ptr;
               return this._findName(q, 2);
            } else {
               return null;
            }
         } else if (i == 34) {
            this._inputPtr = ptr;
            return this._findName(q0, 1);
         } else {
            return null;
         }
      } else if (q0 == 34) {
         this._inputPtr = ptr;
         return "";
      } else {
         return null;
      }
   }

   private final String _parseMediumName(int ptr, int q2) throws IOException {
      byte[] input = this._inputBuffer;
      int[] codes = _icLatin1;
      int i = input[ptr++] & 255;
      if (codes[i] == 0) {
         q2 = q2 << 8 | i;
         i = input[ptr++] & 255;
         if (codes[i] == 0) {
            q2 = q2 << 8 | i;
            i = input[ptr++] & 255;
            if (codes[i] == 0) {
               q2 = q2 << 8 | i;
               i = input[ptr++] & 255;
               if (codes[i] == 0) {
                  return this._parseMediumName2(ptr, i, q2);
               } else if (i == 34) {
                  this._inputPtr = ptr;
                  return this._findName(this._quad1, q2, 4);
               } else {
                  return null;
               }
            } else if (i == 34) {
               this._inputPtr = ptr;
               return this._findName(this._quad1, q2, 3);
            } else {
               return null;
            }
         } else if (i == 34) {
            this._inputPtr = ptr;
            return this._findName(this._quad1, q2, 2);
         } else {
            return null;
         }
      } else if (i == 34) {
         this._inputPtr = ptr;
         return this._findName(this._quad1, q2, 1);
      } else {
         return null;
      }
   }

   private final String _parseMediumName2(int ptr, int q3, int q2) throws IOException {
      byte[] input = this._inputBuffer;
      int[] codes = _icLatin1;
      int i = input[ptr++] & 255;
      if (codes[i] != 0) {
         if (i == 34) {
            this._inputPtr = ptr;
            return this._findName(this._quad1, q2, q3, 1);
         } else {
            return null;
         }
      } else {
         q3 = q3 << 8 | i;
         i = input[ptr++] & 255;
         if (codes[i] != 0) {
            if (i == 34) {
               this._inputPtr = ptr;
               return this._findName(this._quad1, q2, q3, 2);
            } else {
               return null;
            }
         } else {
            q3 = q3 << 8 | i;
            i = input[ptr++] & 255;
            if (codes[i] != 0) {
               if (i == 34) {
                  this._inputPtr = ptr;
                  return this._findName(this._quad1, q2, q3, 3);
               } else {
                  return null;
               }
            } else {
               q3 = q3 << 8 | i;
               i = input[ptr++] & 255;
               if (i == 34) {
                  this._inputPtr = ptr;
                  return this._findName(this._quad1, q2, q3, 4);
               } else {
                  return null;
               }
            }
         }
      }
   }

   private final JsonToken _parseEscapedName(int qlen, int currQuad, int currQuadBytes) throws IOException {
      int[] quads = this._quadBuffer;
      int[] codes = _icLatin1;

      while(this._inputPtr < this._inputEnd) {
         int ch = this._inputBuffer[this._inputPtr++] & 255;
         if (codes[ch] == 0) {
            if (currQuadBytes < 4) {
               ++currQuadBytes;
               currQuad = currQuad << 8 | ch;
            } else {
               if (qlen >= quads.length) {
                  this._quadBuffer = quads = growArrayBy(quads, quads.length);
               }

               quads[qlen++] = currQuad;
               currQuad = ch;
               currQuadBytes = 1;
            }
         } else {
            if (ch == 34) {
               if (currQuadBytes > 0) {
                  if (qlen >= quads.length) {
                     this._quadBuffer = quads = growArrayBy(quads, quads.length);
                  }

                  quads[qlen++] = _padLastQuad(currQuad, currQuadBytes);
               } else if (qlen == 0) {
                  return this._fieldComplete("");
               }

               String name = this._symbols.findName(quads, qlen);
               if (name == null) {
                  name = this._addName(quads, qlen, currQuadBytes);
               }

               return this._fieldComplete(name);
            }

            if (ch != 92) {
               this._throwUnquotedSpace(ch, "name");
            } else {
               ch = this._decodeCharEscape();
               if (ch < 0) {
                  this._minorState = 8;
                  this._minorStateAfterSplit = 7;
                  this._quadLength = qlen;
                  this._pending32 = currQuad;
                  this._pendingBytes = currQuadBytes;
                  return this._currToken = JsonToken.NOT_AVAILABLE;
               }
            }

            if (qlen >= quads.length) {
               this._quadBuffer = quads = growArrayBy(quads, quads.length);
            }

            if (ch > 127) {
               if (currQuadBytes >= 4) {
                  quads[qlen++] = currQuad;
                  currQuad = 0;
                  currQuadBytes = 0;
               }

               if (ch < 2048) {
                  currQuad = currQuad << 8 | 192 | ch >> 6;
                  ++currQuadBytes;
               } else {
                  currQuad = currQuad << 8 | 224 | ch >> 12;
                  ++currQuadBytes;
                  if (currQuadBytes >= 4) {
                     quads[qlen++] = currQuad;
                     currQuad = 0;
                     currQuadBytes = 0;
                  }

                  currQuad = currQuad << 8 | 128 | ch >> 6 & 63;
                  ++currQuadBytes;
               }

               ch = 128 | ch & 63;
            }

            if (currQuadBytes < 4) {
               ++currQuadBytes;
               currQuad = currQuad << 8 | ch;
            } else {
               quads[qlen++] = currQuad;
               currQuad = ch;
               currQuadBytes = 1;
            }
         }
      }

      this._quadLength = qlen;
      this._pending32 = currQuad;
      this._pendingBytes = currQuadBytes;
      this._minorState = 7;
      return this._currToken = JsonToken.NOT_AVAILABLE;
   }

   private JsonToken _handleOddName(int ch) throws IOException {
      switch(ch) {
      case 35:
         if (JsonParser.Feature.ALLOW_YAML_COMMENTS.enabledIn(this._features)) {
            return this._finishHashComment(4);
         }
         break;
      case 39:
         if (this.isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES)) {
            return this._finishAposName(0, 0, 0);
         }
         break;
      case 47:
         return this._startSlashComment(4);
      case 93:
         return this._closeArrayScope();
      }

      if (!this.isEnabled(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)) {
         char c = (char)ch;
         this._reportUnexpectedChar(c, "was expecting double-quote to start field name");
      }

      int[] codes = CharTypes.getInputCodeUtf8JsNames();
      if (codes[ch] != 0) {
         this._reportUnexpectedChar(ch, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
      }

      return this._finishUnquotedName(0, ch, 1);
   }

   private JsonToken _finishUnquotedName(int qlen, int currQuad, int currQuadBytes) throws IOException {
      int[] quads = this._quadBuffer;
      int[] codes = CharTypes.getInputCodeUtf8JsNames();

      while(this._inputPtr < this._inputEnd) {
         int ch = this._inputBuffer[this._inputPtr] & 255;
         if (codes[ch] != 0) {
            if (currQuadBytes > 0) {
               if (qlen >= quads.length) {
                  this._quadBuffer = quads = growArrayBy(quads, quads.length);
               }

               quads[qlen++] = currQuad;
            }

            String name = this._symbols.findName(quads, qlen);
            if (name == null) {
               name = this._addName(quads, qlen, currQuadBytes);
            }

            return this._fieldComplete(name);
         }

         ++this._inputPtr;
         if (currQuadBytes < 4) {
            ++currQuadBytes;
            currQuad = currQuad << 8 | ch;
         } else {
            if (qlen >= quads.length) {
               this._quadBuffer = quads = growArrayBy(quads, quads.length);
            }

            quads[qlen++] = currQuad;
            currQuad = ch;
            currQuadBytes = 1;
         }
      }

      this._quadLength = qlen;
      this._pending32 = currQuad;
      this._pendingBytes = currQuadBytes;
      this._minorState = 10;
      return this._currToken = JsonToken.NOT_AVAILABLE;
   }

   private JsonToken _finishAposName(int qlen, int currQuad, int currQuadBytes) throws IOException {
      int[] quads = this._quadBuffer;
      int[] codes = _icLatin1;

      while(this._inputPtr < this._inputEnd) {
         int ch = this._inputBuffer[this._inputPtr++] & 255;
         if (ch == 39) {
            if (currQuadBytes > 0) {
               if (qlen >= quads.length) {
                  this._quadBuffer = quads = growArrayBy(quads, quads.length);
               }

               quads[qlen++] = _padLastQuad(currQuad, currQuadBytes);
            } else if (qlen == 0) {
               return this._fieldComplete("");
            }

            String name = this._symbols.findName(quads, qlen);
            if (name == null) {
               name = this._addName(quads, qlen, currQuadBytes);
            }

            return this._fieldComplete(name);
         }

         if (ch != 34 && codes[ch] != 0) {
            if (ch != 92) {
               this._throwUnquotedSpace(ch, "name");
            } else {
               ch = this._decodeCharEscape();
               if (ch < 0) {
                  this._minorState = 8;
                  this._minorStateAfterSplit = 9;
                  this._quadLength = qlen;
                  this._pending32 = currQuad;
                  this._pendingBytes = currQuadBytes;
                  return this._currToken = JsonToken.NOT_AVAILABLE;
               }
            }

            if (ch > 127) {
               if (currQuadBytes >= 4) {
                  if (qlen >= quads.length) {
                     this._quadBuffer = quads = growArrayBy(quads, quads.length);
                  }

                  quads[qlen++] = currQuad;
                  currQuad = 0;
                  currQuadBytes = 0;
               }

               if (ch < 2048) {
                  currQuad = currQuad << 8 | 192 | ch >> 6;
                  ++currQuadBytes;
               } else {
                  currQuad = currQuad << 8 | 224 | ch >> 12;
                  ++currQuadBytes;
                  if (currQuadBytes >= 4) {
                     if (qlen >= quads.length) {
                        this._quadBuffer = quads = growArrayBy(quads, quads.length);
                     }

                     quads[qlen++] = currQuad;
                     currQuad = 0;
                     currQuadBytes = 0;
                  }

                  currQuad = currQuad << 8 | 128 | ch >> 6 & 63;
                  ++currQuadBytes;
               }

               ch = 128 | ch & 63;
            }
         }

         if (currQuadBytes < 4) {
            ++currQuadBytes;
            currQuad = currQuad << 8 | ch;
         } else {
            if (qlen >= quads.length) {
               this._quadBuffer = quads = growArrayBy(quads, quads.length);
            }

            quads[qlen++] = currQuad;
            currQuad = ch;
            currQuadBytes = 1;
         }
      }

      this._quadLength = qlen;
      this._pending32 = currQuad;
      this._pendingBytes = currQuadBytes;
      this._minorState = 9;
      return this._currToken = JsonToken.NOT_AVAILABLE;
   }

   protected final JsonToken _finishFieldWithEscape() throws IOException {
      int ch = this._decodeSplitEscaped(this._quoted32, this._quotedDigits);
      if (ch < 0) {
         this._minorState = 8;
         return JsonToken.NOT_AVAILABLE;
      } else {
         if (this._quadLength >= this._quadBuffer.length) {
            this._quadBuffer = growArrayBy(this._quadBuffer, 32);
         }

         int currQuad = this._pending32;
         int currQuadBytes = this._pendingBytes;
         if (ch > 127) {
            if (currQuadBytes >= 4) {
               this._quadBuffer[this._quadLength++] = currQuad;
               currQuad = 0;
               currQuadBytes = 0;
            }

            if (ch < 2048) {
               currQuad = currQuad << 8 | 192 | ch >> 6;
               ++currQuadBytes;
            } else {
               currQuad = currQuad << 8 | 224 | ch >> 12;
               ++currQuadBytes;
               if (currQuadBytes >= 4) {
                  this._quadBuffer[this._quadLength++] = currQuad;
                  currQuad = 0;
                  currQuadBytes = 0;
               }

               currQuad = currQuad << 8 | 128 | ch >> 6 & 63;
               ++currQuadBytes;
            }

            ch = 128 | ch & 63;
         }

         if (currQuadBytes < 4) {
            ++currQuadBytes;
            currQuad = currQuad << 8 | ch;
         } else {
            this._quadBuffer[this._quadLength++] = currQuad;
            currQuad = ch;
            currQuadBytes = 1;
         }

         return this._minorStateAfterSplit == 9 ? this._finishAposName(this._quadLength, currQuad, currQuadBytes) : this._parseEscapedName(this._quadLength, currQuad, currQuadBytes);
      }
   }

   private int _decodeSplitEscaped(int value, int bytesRead) throws IOException {
      if (this._inputPtr >= this._inputEnd) {
         this._quoted32 = value;
         this._quotedDigits = bytesRead;
         return -1;
      } else {
         int c = this._inputBuffer[this._inputPtr++];
         if (bytesRead == -1) {
            switch(c) {
            case 34:
            case 47:
            case 92:
               return c;
            case 98:
               return 8;
            case 102:
               return 12;
            case 110:
               return 10;
            case 114:
               return 13;
            case 116:
               return 9;
            case 117:
               if (this._inputPtr >= this._inputEnd) {
                  this._quotedDigits = 0;
                  this._quoted32 = 0;
                  return -1;
               }

               c = this._inputBuffer[this._inputPtr++];
               bytesRead = 0;
               break;
            default:
               char ch = (char)c;
               return this._handleUnrecognizedCharacterEscape(ch);
            }
         }

         int c = c & 255;

         while(true) {
            int digit = CharTypes.charToHex(c);
            if (digit < 0) {
               this._reportUnexpectedChar(c, "expected a hex-digit for character escape sequence");
            }

            value = value << 4 | digit;
            ++bytesRead;
            if (bytesRead == 4) {
               return value;
            }

            if (this._inputPtr >= this._inputEnd) {
               this._quotedDigits = bytesRead;
               this._quoted32 = value;
               return -1;
            }

            c = this._inputBuffer[this._inputPtr++] & 255;
         }
      }
   }

   protected JsonToken _startString() throws IOException {
      int ptr = this._inputPtr;
      int outPtr = 0;
      char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
      int[] codes = _icUTF8;
      int max = Math.min(this._inputEnd, ptr + outBuf.length);

      int c;
      for(byte[] inputBuffer = this._inputBuffer; ptr < max; outBuf[outPtr++] = (char)c) {
         c = inputBuffer[ptr] & 255;
         if (codes[c] != 0) {
            if (c == 34) {
               this._inputPtr = ptr + 1;
               this._textBuffer.setCurrentLength(outPtr);
               return this._valueComplete(JsonToken.VALUE_STRING);
            }
            break;
         }

         ++ptr;
      }

      this._textBuffer.setCurrentLength(outPtr);
      this._inputPtr = ptr;
      return this._finishRegularString();
   }

   private final JsonToken _finishRegularString() throws IOException {
      int[] codes = _icUTF8;
      byte[] inputBuffer = this._inputBuffer;
      char[] outBuf = this._textBuffer.getBufferWithoutReset();
      int outPtr = this._textBuffer.getCurrentSegmentSize();
      int ptr = this._inputPtr;
      int safeEnd = this._inputEnd - 5;

      while(true) {
         while(ptr < this._inputEnd) {
            if (outPtr >= outBuf.length) {
               outBuf = this._textBuffer.finishCurrentSegment();
               outPtr = 0;
            }

            int c;
            for(int max = Math.min(this._inputEnd, ptr + (outBuf.length - outPtr)); ptr < max; outBuf[outPtr++] = (char)c) {
               c = inputBuffer[ptr++] & 255;
               if (codes[c] != 0) {
                  if (c == 34) {
                     this._inputPtr = ptr;
                     this._textBuffer.setCurrentLength(outPtr);
                     return this._valueComplete(JsonToken.VALUE_STRING);
                  }

                  if (ptr >= safeEnd) {
                     this._inputPtr = ptr;
                     this._textBuffer.setCurrentLength(outPtr);
                     if (!this._decodeSplitMultiByte(c, codes[c], ptr < this._inputEnd)) {
                        this._minorStateAfterSplit = 40;
                        return this._currToken = JsonToken.NOT_AVAILABLE;
                     }

                     outBuf = this._textBuffer.getBufferWithoutReset();
                     outPtr = this._textBuffer.getCurrentSegmentSize();
                     ptr = this._inputPtr;
                  } else {
                     switch(codes[c]) {
                     case 1:
                        this._inputPtr = ptr;
                        c = this._decodeFastCharEscape();
                        ptr = this._inputPtr;
                        break;
                     case 2:
                        c = this._decodeUTF8_2(c, this._inputBuffer[ptr++]);
                        break;
                     case 3:
                        c = this._decodeUTF8_3(c, this._inputBuffer[ptr++], this._inputBuffer[ptr++]);
                        break;
                     case 4:
                        c = this._decodeUTF8_4(c, this._inputBuffer[ptr++], this._inputBuffer[ptr++], this._inputBuffer[ptr++]);
                        outBuf[outPtr++] = (char)('\ud800' | c >> 10);
                        if (outPtr >= outBuf.length) {
                           outBuf = this._textBuffer.finishCurrentSegment();
                           outPtr = 0;
                        }

                        c = '\udc00' | c & 1023;
                        break;
                     default:
                        if (c < 32) {
                           this._throwUnquotedSpace(c, "string value");
                        } else {
                           this._reportInvalidChar(c);
                        }
                     }

                     if (outPtr >= outBuf.length) {
                        outBuf = this._textBuffer.finishCurrentSegment();
                        outPtr = 0;
                     }

                     outBuf[outPtr++] = (char)c;
                  }
                  break;
               }
            }
         }

         this._inputPtr = ptr;
         this._minorState = 40;
         this._textBuffer.setCurrentLength(outPtr);
         return this._currToken = JsonToken.NOT_AVAILABLE;
      }
   }

   protected JsonToken _startAposString() throws IOException {
      int ptr = this._inputPtr;
      int outPtr = 0;
      char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
      int[] codes = _icUTF8;
      int max = Math.min(this._inputEnd, ptr + outBuf.length);

      int c;
      for(byte[] inputBuffer = this._inputBuffer; ptr < max; outBuf[outPtr++] = (char)c) {
         c = inputBuffer[ptr] & 255;
         if (c == 39) {
            this._inputPtr = ptr + 1;
            this._textBuffer.setCurrentLength(outPtr);
            return this._valueComplete(JsonToken.VALUE_STRING);
         }

         if (codes[c] != 0) {
            break;
         }

         ++ptr;
      }

      this._textBuffer.setCurrentLength(outPtr);
      this._inputPtr = ptr;
      return this._finishAposString();
   }

   private final JsonToken _finishAposString() throws IOException {
      int[] codes = _icUTF8;
      byte[] inputBuffer = this._inputBuffer;
      char[] outBuf = this._textBuffer.getBufferWithoutReset();
      int outPtr = this._textBuffer.getCurrentSegmentSize();
      int ptr = this._inputPtr;
      int safeEnd = this._inputEnd - 5;

      while(true) {
         while(ptr < this._inputEnd) {
            if (outPtr >= outBuf.length) {
               outBuf = this._textBuffer.finishCurrentSegment();
               outPtr = 0;
            }

            int c;
            for(int max = Math.min(this._inputEnd, ptr + (outBuf.length - outPtr)); ptr < max; outBuf[outPtr++] = (char)c) {
               c = inputBuffer[ptr++] & 255;
               if (codes[c] != 0 && c != 34) {
                  if (ptr >= safeEnd) {
                     this._inputPtr = ptr;
                     this._textBuffer.setCurrentLength(outPtr);
                     if (!this._decodeSplitMultiByte(c, codes[c], ptr < this._inputEnd)) {
                        this._minorStateAfterSplit = 45;
                        return this._currToken = JsonToken.NOT_AVAILABLE;
                     }

                     outBuf = this._textBuffer.getBufferWithoutReset();
                     outPtr = this._textBuffer.getCurrentSegmentSize();
                     ptr = this._inputPtr;
                  } else {
                     switch(codes[c]) {
                     case 1:
                        this._inputPtr = ptr;
                        c = this._decodeFastCharEscape();
                        ptr = this._inputPtr;
                        break;
                     case 2:
                        c = this._decodeUTF8_2(c, this._inputBuffer[ptr++]);
                        break;
                     case 3:
                        c = this._decodeUTF8_3(c, this._inputBuffer[ptr++], this._inputBuffer[ptr++]);
                        break;
                     case 4:
                        c = this._decodeUTF8_4(c, this._inputBuffer[ptr++], this._inputBuffer[ptr++], this._inputBuffer[ptr++]);
                        outBuf[outPtr++] = (char)('\ud800' | c >> 10);
                        if (outPtr >= outBuf.length) {
                           outBuf = this._textBuffer.finishCurrentSegment();
                           outPtr = 0;
                        }

                        c = '\udc00' | c & 1023;
                        break;
                     default:
                        if (c < 32) {
                           this._throwUnquotedSpace(c, "string value");
                        } else {
                           this._reportInvalidChar(c);
                        }
                     }

                     if (outPtr >= outBuf.length) {
                        outBuf = this._textBuffer.finishCurrentSegment();
                        outPtr = 0;
                     }

                     outBuf[outPtr++] = (char)c;
                  }
                  break;
               }

               if (c == 39) {
                  this._inputPtr = ptr;
                  this._textBuffer.setCurrentLength(outPtr);
                  return this._valueComplete(JsonToken.VALUE_STRING);
               }
            }
         }

         this._inputPtr = ptr;
         this._minorState = 45;
         this._textBuffer.setCurrentLength(outPtr);
         return this._currToken = JsonToken.NOT_AVAILABLE;
      }
   }

   private final boolean _decodeSplitMultiByte(int c, int type, boolean gotNext) throws IOException {
      switch(type) {
      case 1:
         c = this._decodeSplitEscaped(0, -1);
         if (c < 0) {
            this._minorState = 41;
            return false;
         }

         this._textBuffer.append((char)c);
         return true;
      case 2:
         if (gotNext) {
            c = this._decodeUTF8_2(c, this._inputBuffer[this._inputPtr++]);
            this._textBuffer.append((char)c);
            return true;
         }

         this._minorState = 42;
         this._pending32 = c;
         return false;
      case 3:
         c &= 15;
         if (gotNext) {
            return this._decodeSplitUTF8_3(c, 1, this._inputBuffer[this._inputPtr++]);
         }

         this._minorState = 43;
         this._pending32 = c;
         this._pendingBytes = 1;
         return false;
      case 4:
         c &= 7;
         if (gotNext) {
            return this._decodeSplitUTF8_4(c, 1, this._inputBuffer[this._inputPtr++]);
         }

         this._pending32 = c;
         this._pendingBytes = 1;
         this._minorState = 44;
         return false;
      default:
         if (c < 32) {
            this._throwUnquotedSpace(c, "string value");
         } else {
            this._reportInvalidChar(c);
         }

         this._textBuffer.append((char)c);
         return true;
      }
   }

   private final boolean _decodeSplitUTF8_3(int prev, int prevCount, int next) throws IOException {
      if (prevCount == 1) {
         if ((next & 192) != 128) {
            this._reportInvalidOther(next & 255, this._inputPtr);
         }

         prev = prev << 6 | next & 63;
         if (this._inputPtr >= this._inputEnd) {
            this._minorState = 43;
            this._pending32 = prev;
            this._pendingBytes = 2;
            return false;
         }

         next = this._inputBuffer[this._inputPtr++];
      }

      if ((next & 192) != 128) {
         this._reportInvalidOther(next & 255, this._inputPtr);
      }

      this._textBuffer.append((char)(prev << 6 | next & 63));
      return true;
   }

   private final boolean _decodeSplitUTF8_4(int prev, int prevCount, int next) throws IOException {
      if (prevCount == 1) {
         if ((next & 192) != 128) {
            this._reportInvalidOther(next & 255, this._inputPtr);
         }

         prev = prev << 6 | next & 63;
         if (this._inputPtr >= this._inputEnd) {
            this._minorState = 44;
            this._pending32 = prev;
            this._pendingBytes = 2;
            return false;
         }

         prevCount = 2;
         next = this._inputBuffer[this._inputPtr++];
      }

      if (prevCount == 2) {
         if ((next & 192) != 128) {
            this._reportInvalidOther(next & 255, this._inputPtr);
         }

         prev = prev << 6 | next & 63;
         if (this._inputPtr >= this._inputEnd) {
            this._minorState = 44;
            this._pending32 = prev;
            this._pendingBytes = 3;
            return false;
         }

         next = this._inputBuffer[this._inputPtr++];
      }

      if ((next & 192) != 128) {
         this._reportInvalidOther(next & 255, this._inputPtr);
      }

      int c = (prev << 6 | next & 63) - 65536;
      this._textBuffer.append((char)('\ud800' | c >> 10));
      c = '\udc00' | c & 1023;
      this._textBuffer.append((char)c);
      return true;
   }

   private final int _decodeCharEscape() throws IOException {
      int left = this._inputEnd - this._inputPtr;
      return left < 5 ? this._decodeSplitEscaped(0, -1) : this._decodeFastCharEscape();
   }

   private final int _decodeFastCharEscape() throws IOException {
      int c = this._inputBuffer[this._inputPtr++];
      switch(c) {
      case 34:
      case 47:
      case 92:
         return (char)c;
      case 98:
         return 8;
      case 102:
         return 12;
      case 110:
         return 10;
      case 114:
         return 13;
      case 116:
         return 9;
      case 117:
         int ch = this._inputBuffer[this._inputPtr++];
         int digit = CharTypes.charToHex(ch);
         int result = digit;
         if (digit >= 0) {
            ch = this._inputBuffer[this._inputPtr++];
            digit = CharTypes.charToHex(ch);
            if (digit >= 0) {
               result = result << 4 | digit;
               ch = this._inputBuffer[this._inputPtr++];
               digit = CharTypes.charToHex(ch);
               if (digit >= 0) {
                  result = result << 4 | digit;
                  ch = this._inputBuffer[this._inputPtr++];
                  digit = CharTypes.charToHex(ch);
                  if (digit >= 0) {
                     return result << 4 | digit;
                  }
               }
            }
         }

         this._reportUnexpectedChar(ch & 255, "expected a hex-digit for character escape sequence");
         return -1;
      default:
         char ch = (char)c;
         return this._handleUnrecognizedCharacterEscape(ch);
      }
   }

   private final int _decodeUTF8_2(int c, int d) throws IOException {
      if ((d & 192) != 128) {
         this._reportInvalidOther(d & 255, this._inputPtr);
      }

      return (c & 31) << 6 | d & 63;
   }

   private final int _decodeUTF8_3(int c, int d, int e) throws IOException {
      c &= 15;
      if ((d & 192) != 128) {
         this._reportInvalidOther(d & 255, this._inputPtr);
      }

      c = c << 6 | d & 63;
      if ((e & 192) != 128) {
         this._reportInvalidOther(e & 255, this._inputPtr);
      }

      return c << 6 | e & 63;
   }

   private final int _decodeUTF8_4(int c, int d, int e, int f) throws IOException {
      if ((d & 192) != 128) {
         this._reportInvalidOther(d & 255, this._inputPtr);
      }

      c = (c & 7) << 6 | d & 63;
      if ((e & 192) != 128) {
         this._reportInvalidOther(e & 255, this._inputPtr);
      }

      c = c << 6 | e & 63;
      if ((f & 192) != 128) {
         this._reportInvalidOther(f & 255, this._inputPtr);
      }

      return (c << 6 | f & 63) - 65536;
   }
}

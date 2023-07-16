package software.bernie.shadowed.fasterxml.jackson.core.json;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Arrays;
import software.bernie.shadowed.fasterxml.jackson.core.Base64Variant;
import software.bernie.shadowed.fasterxml.jackson.core.JsonLocation;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParseException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.ObjectCodec;
import software.bernie.shadowed.fasterxml.jackson.core.base.ParserBase;
import software.bernie.shadowed.fasterxml.jackson.core.io.CharTypes;
import software.bernie.shadowed.fasterxml.jackson.core.io.IOContext;
import software.bernie.shadowed.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
import software.bernie.shadowed.fasterxml.jackson.core.util.ByteArrayBuilder;

public class UTF8DataInputJsonParser extends ParserBase {
   static final byte BYTE_LF = 10;
   private static final int[] _icUTF8 = CharTypes.getInputCodeUtf8();
   protected static final int[] _icLatin1 = CharTypes.getInputCodeLatin1();
   protected ObjectCodec _objectCodec;
   protected final ByteQuadsCanonicalizer _symbols;
   protected int[] _quadBuffer = new int[16];
   protected boolean _tokenIncomplete;
   private int _quad1;
   protected DataInput _inputData;
   protected int _nextByte = -1;

   public UTF8DataInputJsonParser(IOContext ctxt, int features, DataInput inputData, ObjectCodec codec, ByteQuadsCanonicalizer sym, int firstByte) {
      super(ctxt, features);
      this._objectCodec = codec;
      this._symbols = sym;
      this._inputData = inputData;
      this._nextByte = firstByte;
   }

   public ObjectCodec getCodec() {
      return this._objectCodec;
   }

   public void setCodec(ObjectCodec c) {
      this._objectCodec = c;
   }

   public int releaseBuffered(OutputStream out) throws IOException {
      return 0;
   }

   public Object getInputSource() {
      return this._inputData;
   }

   protected void _closeInput() throws IOException {
   }

   protected void _releaseBuffers() throws IOException {
      super._releaseBuffers();
      this._symbols.release();
   }

   public String getText() throws IOException {
      if (this._currToken == JsonToken.VALUE_STRING) {
         if (this._tokenIncomplete) {
            this._tokenIncomplete = false;
            return this._finishAndReturnString();
         } else {
            return this._textBuffer.contentsAsString();
         }
      } else {
         return this._getText2(this._currToken);
      }
   }

   public int getText(Writer writer) throws IOException {
      JsonToken t = this._currToken;
      if (t == JsonToken.VALUE_STRING) {
         if (this._tokenIncomplete) {
            this._tokenIncomplete = false;
            this._finishString();
         }

         return this._textBuffer.contentsToWriter(writer);
      } else if (t == JsonToken.FIELD_NAME) {
         String n = this._parsingContext.getCurrentName();
         writer.write(n);
         return n.length();
      } else if (t != null) {
         if (t.isNumeric()) {
            return this._textBuffer.contentsToWriter(writer);
         } else {
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
         if (this._tokenIncomplete) {
            this._tokenIncomplete = false;
            return this._finishAndReturnString();
         } else {
            return this._textBuffer.contentsAsString();
         }
      } else {
         return this._currToken == JsonToken.FIELD_NAME ? this.getCurrentName() : super.getValueAsString((String)null);
      }
   }

   public String getValueAsString(String defValue) throws IOException {
      if (this._currToken == JsonToken.VALUE_STRING) {
         if (this._tokenIncomplete) {
            this._tokenIncomplete = false;
            return this._finishAndReturnString();
         } else {
            return this._textBuffer.contentsAsString();
         }
      } else {
         return this._currToken == JsonToken.FIELD_NAME ? this.getCurrentName() : super.getValueAsString(defValue);
      }
   }

   public int getValueAsInt() throws IOException {
      JsonToken t = this._currToken;
      if (t != JsonToken.VALUE_NUMBER_INT && t != JsonToken.VALUE_NUMBER_FLOAT) {
         return super.getValueAsInt(0);
      } else {
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
   }

   public int getValueAsInt(int defValue) throws IOException {
      JsonToken t = this._currToken;
      if (t != JsonToken.VALUE_NUMBER_INT && t != JsonToken.VALUE_NUMBER_FLOAT) {
         return super.getValueAsInt(defValue);
      } else {
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
   }

   protected final String _getText2(JsonToken t) {
      if (t == null) {
         return null;
      } else {
         switch(t.id()) {
         case 5:
            return this._parsingContext.getCurrentName();
         case 6:
         case 7:
         case 8:
            return this._textBuffer.contentsAsString();
         default:
            return t.asString();
         }
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
            if (this._tokenIncomplete) {
               this._tokenIncomplete = false;
               this._finishString();
            }
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
      if (this._currToken == JsonToken.VALUE_STRING) {
         if (this._tokenIncomplete) {
            this._tokenIncomplete = false;
            this._finishString();
         }

         return this._textBuffer.size();
      } else if (this._currToken == JsonToken.FIELD_NAME) {
         return this._parsingContext.getCurrentName().length();
      } else if (this._currToken != null) {
         return this._currToken.isNumeric() ? this._textBuffer.size() : this._currToken.asCharArray().length;
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
            if (this._tokenIncomplete) {
               this._tokenIncomplete = false;
               this._finishString();
            }
         case 7:
         case 8:
            return this._textBuffer.getTextOffset();
         }
      }

      return 0;
   }

   public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
      if (this._currToken != JsonToken.VALUE_STRING && (this._currToken != JsonToken.VALUE_EMBEDDED_OBJECT || this._binaryValue == null)) {
         this._reportError("Current token (" + this._currToken + ") not VALUE_STRING or VALUE_EMBEDDED_OBJECT, can not access as binary");
      }

      if (this._tokenIncomplete) {
         try {
            this._binaryValue = this._decodeBase64(b64variant);
         } catch (IllegalArgumentException var3) {
            throw this._constructError("Failed to decode VALUE_STRING as base64 (" + b64variant + "): " + var3.getMessage());
         }

         this._tokenIncomplete = false;
      } else if (this._binaryValue == null) {
         ByteArrayBuilder builder = this._getByteArrayBuilder();
         this._decodeBase64(this.getText(), builder, b64variant);
         this._binaryValue = builder.toByteArray();
      }

      return this._binaryValue;
   }

   public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
      byte[] buf;
      if (this._tokenIncomplete && this._currToken == JsonToken.VALUE_STRING) {
         buf = this._ioContext.allocBase64Buffer();

         int var4;
         try {
            var4 = this._readBinary(b64variant, out, buf);
         } finally {
            this._ioContext.releaseBase64Buffer(buf);
         }

         return var4;
      } else {
         buf = this.getBinaryValue(b64variant);
         out.write(buf);
         return buf.length;
      }
   }

   protected int _readBinary(Base64Variant b64variant, OutputStream out, byte[] buffer) throws IOException {
      int outputPtr = 0;
      int outputEnd = buffer.length - 3;
      int outputCount = 0;

      while(true) {
         int ch;
         do {
            ch = this._inputData.readUnsignedByte();
         } while(ch <= 32);

         int bits = b64variant.decodeBase64Char(ch);
         if (bits < 0) {
            if (ch == 34) {
               break;
            }

            bits = this._decodeBase64Escape(b64variant, ch, 0);
            if (bits < 0) {
               continue;
            }
         }

         if (outputPtr > outputEnd) {
            outputCount += outputPtr;
            out.write(buffer, 0, outputPtr);
            outputPtr = 0;
         }

         int decodedData = bits;
         ch = this._inputData.readUnsignedByte();
         bits = b64variant.decodeBase64Char(ch);
         if (bits < 0) {
            bits = this._decodeBase64Escape(b64variant, ch, 1);
         }

         decodedData = decodedData << 6 | bits;
         ch = this._inputData.readUnsignedByte();
         bits = b64variant.decodeBase64Char(ch);
         if (bits < 0) {
            if (bits != -2) {
               if (ch == 34 && !b64variant.usesPadding()) {
                  decodedData >>= 4;
                  buffer[outputPtr++] = (byte)decodedData;
                  break;
               }

               bits = this._decodeBase64Escape(b64variant, ch, 2);
            }

            if (bits == -2) {
               ch = this._inputData.readUnsignedByte();
               if (!b64variant.usesPaddingChar(ch)) {
                  throw this.reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
               }

               decodedData >>= 4;
               buffer[outputPtr++] = (byte)decodedData;
               continue;
            }
         }

         decodedData = decodedData << 6 | bits;
         ch = this._inputData.readUnsignedByte();
         bits = b64variant.decodeBase64Char(ch);
         if (bits < 0) {
            if (bits != -2) {
               if (ch == 34 && !b64variant.usesPadding()) {
                  decodedData >>= 2;
                  buffer[outputPtr++] = (byte)(decodedData >> 8);
                  buffer[outputPtr++] = (byte)decodedData;
                  break;
               }

               bits = this._decodeBase64Escape(b64variant, ch, 3);
            }

            if (bits == -2) {
               decodedData >>= 2;
               buffer[outputPtr++] = (byte)(decodedData >> 8);
               buffer[outputPtr++] = (byte)decodedData;
               continue;
            }
         }

         decodedData = decodedData << 6 | bits;
         buffer[outputPtr++] = (byte)(decodedData >> 16);
         buffer[outputPtr++] = (byte)(decodedData >> 8);
         buffer[outputPtr++] = (byte)decodedData;
      }

      this._tokenIncomplete = false;
      if (outputPtr > 0) {
         outputCount += outputPtr;
         out.write(buffer, 0, outputPtr);
      }

      return outputCount;
   }

   public JsonToken nextToken() throws IOException {
      if (this._currToken == JsonToken.FIELD_NAME) {
         return this._nextAfterName();
      } else {
         this._numTypesValid = 0;
         if (this._tokenIncomplete) {
            this._skipString();
         }

         int i = this._skipWSOrEnd();
         if (i < 0) {
            this.close();
            return this._currToken = null;
         } else {
            this._binaryValue = null;
            this._tokenInputRow = this._currInputRow;
            if (i != 93 && i != 125) {
               if (this._parsingContext.expectComma()) {
                  if (i != 44) {
                     this._reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
                  }

                  i = this._skipWS();
                  if (JsonParser.Feature.ALLOW_TRAILING_COMMA.enabledIn(this._features) && (i == 93 || i == 125)) {
                     this._closeScope(i);
                     return this._currToken;
                  }
               }

               if (!this._parsingContext.inObject()) {
                  return this._nextTokenNotInObject(i);
               } else {
                  String n = this._parseName(i);
                  this._parsingContext.setCurrentName(n);
                  this._currToken = JsonToken.FIELD_NAME;
                  i = this._skipColon();
                  if (i == 34) {
                     this._tokenIncomplete = true;
                     this._nextToken = JsonToken.VALUE_STRING;
                     return this._currToken;
                  } else {
                     JsonToken t;
                     switch(i) {
                     case 45:
                        t = this._parseNegNumber();
                        break;
                     case 48:
                     case 49:
                     case 50:
                     case 51:
                     case 52:
                     case 53:
                     case 54:
                     case 55:
                     case 56:
                     case 57:
                        t = this._parsePosNumber(i);
                        break;
                     case 91:
                        t = JsonToken.START_ARRAY;
                        break;
                     case 102:
                        this._matchToken("false", 1);
                        t = JsonToken.VALUE_FALSE;
                        break;
                     case 110:
                        this._matchToken("null", 1);
                        t = JsonToken.VALUE_NULL;
                        break;
                     case 116:
                        this._matchToken("true", 1);
                        t = JsonToken.VALUE_TRUE;
                        break;
                     case 123:
                        t = JsonToken.START_OBJECT;
                        break;
                     default:
                        t = this._handleUnexpectedValue(i);
                     }

                     this._nextToken = t;
                     return this._currToken;
                  }
               }
            } else {
               this._closeScope(i);
               return this._currToken;
            }
         }
      }
   }

   private final JsonToken _nextTokenNotInObject(int i) throws IOException {
      if (i == 34) {
         this._tokenIncomplete = true;
         return this._currToken = JsonToken.VALUE_STRING;
      } else {
         switch(i) {
         case 45:
            return this._currToken = this._parseNegNumber();
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
            return this._currToken = this._parsePosNumber(i);
         case 91:
            this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
            return this._currToken = JsonToken.START_ARRAY;
         case 102:
            this._matchToken("false", 1);
            return this._currToken = JsonToken.VALUE_FALSE;
         case 110:
            this._matchToken("null", 1);
            return this._currToken = JsonToken.VALUE_NULL;
         case 116:
            this._matchToken("true", 1);
            return this._currToken = JsonToken.VALUE_TRUE;
         case 123:
            this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
            return this._currToken = JsonToken.START_OBJECT;
         default:
            return this._currToken = this._handleUnexpectedValue(i);
         }
      }
   }

   private final JsonToken _nextAfterName() {
      this._nameCopied = false;
      JsonToken t = this._nextToken;
      this._nextToken = null;
      if (t == JsonToken.START_ARRAY) {
         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
      } else if (t == JsonToken.START_OBJECT) {
         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
      }

      return this._currToken = t;
   }

   public void finishToken() throws IOException {
      if (this._tokenIncomplete) {
         this._tokenIncomplete = false;
         this._finishString();
      }

   }

   public String nextFieldName() throws IOException {
      this._numTypesValid = 0;
      if (this._currToken == JsonToken.FIELD_NAME) {
         this._nextAfterName();
         return null;
      } else {
         if (this._tokenIncomplete) {
            this._skipString();
         }

         int i = this._skipWS();
         this._binaryValue = null;
         this._tokenInputRow = this._currInputRow;
         if (i != 93 && i != 125) {
            if (this._parsingContext.expectComma()) {
               if (i != 44) {
                  this._reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
               }

               i = this._skipWS();
               if (JsonParser.Feature.ALLOW_TRAILING_COMMA.enabledIn(this._features) && (i == 93 || i == 125)) {
                  this._closeScope(i);
                  return null;
               }
            }

            if (!this._parsingContext.inObject()) {
               this._nextTokenNotInObject(i);
               return null;
            } else {
               String nameStr = this._parseName(i);
               this._parsingContext.setCurrentName(nameStr);
               this._currToken = JsonToken.FIELD_NAME;
               i = this._skipColon();
               if (i == 34) {
                  this._tokenIncomplete = true;
                  this._nextToken = JsonToken.VALUE_STRING;
                  return nameStr;
               } else {
                  JsonToken t;
                  switch(i) {
                  case 45:
                     t = this._parseNegNumber();
                     break;
                  case 48:
                  case 49:
                  case 50:
                  case 51:
                  case 52:
                  case 53:
                  case 54:
                  case 55:
                  case 56:
                  case 57:
                     t = this._parsePosNumber(i);
                     break;
                  case 91:
                     t = JsonToken.START_ARRAY;
                     break;
                  case 102:
                     this._matchToken("false", 1);
                     t = JsonToken.VALUE_FALSE;
                     break;
                  case 110:
                     this._matchToken("null", 1);
                     t = JsonToken.VALUE_NULL;
                     break;
                  case 116:
                     this._matchToken("true", 1);
                     t = JsonToken.VALUE_TRUE;
                     break;
                  case 123:
                     t = JsonToken.START_OBJECT;
                     break;
                  default:
                     t = this._handleUnexpectedValue(i);
                  }

                  this._nextToken = t;
                  return nameStr;
               }
            }
         } else {
            this._closeScope(i);
            return null;
         }
      }
   }

   public String nextTextValue() throws IOException {
      if (this._currToken == JsonToken.FIELD_NAME) {
         this._nameCopied = false;
         JsonToken t = this._nextToken;
         this._nextToken = null;
         this._currToken = t;
         if (t == JsonToken.VALUE_STRING) {
            if (this._tokenIncomplete) {
               this._tokenIncomplete = false;
               return this._finishAndReturnString();
            } else {
               return this._textBuffer.contentsAsString();
            }
         } else {
            if (t == JsonToken.START_ARRAY) {
               this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
            } else if (t == JsonToken.START_OBJECT) {
               this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
            }

            return null;
         }
      } else {
         return this.nextToken() == JsonToken.VALUE_STRING ? this.getText() : null;
      }
   }

   public int nextIntValue(int defaultValue) throws IOException {
      if (this._currToken == JsonToken.FIELD_NAME) {
         this._nameCopied = false;
         JsonToken t = this._nextToken;
         this._nextToken = null;
         this._currToken = t;
         if (t == JsonToken.VALUE_NUMBER_INT) {
            return this.getIntValue();
         } else {
            if (t == JsonToken.START_ARRAY) {
               this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
            } else if (t == JsonToken.START_OBJECT) {
               this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
            }

            return defaultValue;
         }
      } else {
         return this.nextToken() == JsonToken.VALUE_NUMBER_INT ? this.getIntValue() : defaultValue;
      }
   }

   public long nextLongValue(long defaultValue) throws IOException {
      if (this._currToken == JsonToken.FIELD_NAME) {
         this._nameCopied = false;
         JsonToken t = this._nextToken;
         this._nextToken = null;
         this._currToken = t;
         if (t == JsonToken.VALUE_NUMBER_INT) {
            return this.getLongValue();
         } else {
            if (t == JsonToken.START_ARRAY) {
               this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
            } else if (t == JsonToken.START_OBJECT) {
               this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
            }

            return defaultValue;
         }
      } else {
         return this.nextToken() == JsonToken.VALUE_NUMBER_INT ? this.getLongValue() : defaultValue;
      }
   }

   public Boolean nextBooleanValue() throws IOException {
      JsonToken t;
      if (this._currToken == JsonToken.FIELD_NAME) {
         this._nameCopied = false;
         t = this._nextToken;
         this._nextToken = null;
         this._currToken = t;
         if (t == JsonToken.VALUE_TRUE) {
            return Boolean.TRUE;
         } else if (t == JsonToken.VALUE_FALSE) {
            return Boolean.FALSE;
         } else {
            if (t == JsonToken.START_ARRAY) {
               this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
            } else if (t == JsonToken.START_OBJECT) {
               this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
            }

            return null;
         }
      } else {
         t = this.nextToken();
         if (t == JsonToken.VALUE_TRUE) {
            return Boolean.TRUE;
         } else {
            return t == JsonToken.VALUE_FALSE ? Boolean.FALSE : null;
         }
      }
   }

   protected JsonToken _parsePosNumber(int c) throws IOException {
      char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
      int outPtr;
      if (c == 48) {
         c = this._handleLeadingZeroes();
         if (c <= 57 && c >= 48) {
            outPtr = 0;
         } else {
            outBuf[0] = '0';
            outPtr = 1;
         }
      } else {
         outBuf[0] = (char)c;
         c = this._inputData.readUnsignedByte();
         outPtr = 1;
      }

      int intLen;
      for(intLen = outPtr; c <= 57 && c >= 48; c = this._inputData.readUnsignedByte()) {
         ++intLen;
         outBuf[outPtr++] = (char)c;
      }

      if (c != 46 && c != 101 && c != 69) {
         this._textBuffer.setCurrentLength(outPtr);
         if (this._parsingContext.inRoot()) {
            this._verifyRootSpace();
         } else {
            this._nextByte = c;
         }

         return this.resetInt(false, intLen);
      } else {
         return this._parseFloat(outBuf, outPtr, c, false, intLen);
      }
   }

   protected JsonToken _parseNegNumber() throws IOException {
      char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
      int outPtr = 0;
      int outPtr = outPtr + 1;
      outBuf[outPtr] = '-';
      int c = this._inputData.readUnsignedByte();
      outBuf[outPtr++] = (char)c;
      if (c <= 48) {
         if (c != 48) {
            return this._handleInvalidNumberStart(c, true);
         }

         c = this._handleLeadingZeroes();
      } else {
         if (c > 57) {
            return this._handleInvalidNumberStart(c, true);
         }

         c = this._inputData.readUnsignedByte();
      }

      int intLen;
      for(intLen = 1; c <= 57 && c >= 48; c = this._inputData.readUnsignedByte()) {
         ++intLen;
         outBuf[outPtr++] = (char)c;
      }

      if (c != 46 && c != 101 && c != 69) {
         this._textBuffer.setCurrentLength(outPtr);
         this._nextByte = c;
         if (this._parsingContext.inRoot()) {
            this._verifyRootSpace();
         }

         return this.resetInt(true, intLen);
      } else {
         return this._parseFloat(outBuf, outPtr, c, true, intLen);
      }
   }

   private final int _handleLeadingZeroes() throws IOException {
      int ch = this._inputData.readUnsignedByte();
      if (ch >= 48 && ch <= 57) {
         if (!this.isEnabled(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS)) {
            this.reportInvalidNumber("Leading zeroes not allowed");
         }

         while(ch == 48) {
            ch = this._inputData.readUnsignedByte();
         }

         return ch;
      } else {
         return ch;
      }
   }

   private final JsonToken _parseFloat(char[] outBuf, int outPtr, int c, boolean negative, int integerPartLength) throws IOException {
      int fractLen = 0;
      if (c == 46) {
         outBuf[outPtr++] = (char)c;

         while(true) {
            c = this._inputData.readUnsignedByte();
            if (c < 48 || c > 57) {
               if (fractLen == 0) {
                  this.reportUnexpectedNumberChar(c, "Decimal point not followed by a digit");
               }
               break;
            }

            ++fractLen;
            if (outPtr >= outBuf.length) {
               outBuf = this._textBuffer.finishCurrentSegment();
               outPtr = 0;
            }

            outBuf[outPtr++] = (char)c;
         }
      }

      int expLen = 0;
      if (c == 101 || c == 69) {
         if (outPtr >= outBuf.length) {
            outBuf = this._textBuffer.finishCurrentSegment();
            outPtr = 0;
         }

         outBuf[outPtr++] = (char)c;
         c = this._inputData.readUnsignedByte();
         if (c == 45 || c == 43) {
            if (outPtr >= outBuf.length) {
               outBuf = this._textBuffer.finishCurrentSegment();
               outPtr = 0;
            }

            outBuf[outPtr++] = (char)c;
            c = this._inputData.readUnsignedByte();
         }

         while(c <= 57 && c >= 48) {
            ++expLen;
            if (outPtr >= outBuf.length) {
               outBuf = this._textBuffer.finishCurrentSegment();
               outPtr = 0;
            }

            outBuf[outPtr++] = (char)c;
            c = this._inputData.readUnsignedByte();
         }

         if (expLen == 0) {
            this.reportUnexpectedNumberChar(c, "Exponent indicator not followed by a digit");
         }
      }

      this._nextByte = c;
      if (this._parsingContext.inRoot()) {
         this._verifyRootSpace();
      }

      this._textBuffer.setCurrentLength(outPtr);
      return this.resetFloat(negative, integerPartLength, fractLen, expLen);
   }

   private final void _verifyRootSpace() throws IOException {
      int ch = this._nextByte;
      if (ch > 32) {
         this._reportMissingRootWS(ch);
      } else {
         this._nextByte = -1;
         if (ch == 13 || ch == 10) {
            ++this._currInputRow;
         }

      }
   }

   protected final String _parseName(int i) throws IOException {
      if (i != 34) {
         return this._handleOddName(i);
      } else {
         int[] codes = _icLatin1;
         int q = this._inputData.readUnsignedByte();
         if (codes[q] == 0) {
            i = this._inputData.readUnsignedByte();
            if (codes[i] == 0) {
               q = q << 8 | i;
               i = this._inputData.readUnsignedByte();
               if (codes[i] == 0) {
                  q = q << 8 | i;
                  i = this._inputData.readUnsignedByte();
                  if (codes[i] == 0) {
                     q = q << 8 | i;
                     i = this._inputData.readUnsignedByte();
                     if (codes[i] == 0) {
                        this._quad1 = q;
                        return this._parseMediumName(i);
                     } else {
                        return i == 34 ? this.findName(q, 4) : this.parseName(q, i, 4);
                     }
                  } else {
                     return i == 34 ? this.findName(q, 3) : this.parseName(q, i, 3);
                  }
               } else {
                  return i == 34 ? this.findName(q, 2) : this.parseName(q, i, 2);
               }
            } else {
               return i == 34 ? this.findName(q, 1) : this.parseName(q, i, 1);
            }
         } else {
            return q == 34 ? "" : this.parseName(0, q, 0);
         }
      }
   }

   private final String _parseMediumName(int q2) throws IOException {
      int[] codes = _icLatin1;
      int i = this._inputData.readUnsignedByte();
      if (codes[i] != 0) {
         return i == 34 ? this.findName(this._quad1, q2, 1) : this.parseName(this._quad1, q2, i, 1);
      } else {
         q2 = q2 << 8 | i;
         i = this._inputData.readUnsignedByte();
         if (codes[i] != 0) {
            return i == 34 ? this.findName(this._quad1, q2, 2) : this.parseName(this._quad1, q2, i, 2);
         } else {
            q2 = q2 << 8 | i;
            i = this._inputData.readUnsignedByte();
            if (codes[i] != 0) {
               return i == 34 ? this.findName(this._quad1, q2, 3) : this.parseName(this._quad1, q2, i, 3);
            } else {
               q2 = q2 << 8 | i;
               i = this._inputData.readUnsignedByte();
               if (codes[i] != 0) {
                  return i == 34 ? this.findName(this._quad1, q2, 4) : this.parseName(this._quad1, q2, i, 4);
               } else {
                  return this._parseMediumName2(i, q2);
               }
            }
         }
      }
   }

   private final String _parseMediumName2(int q3, int q2) throws IOException {
      int[] codes = _icLatin1;
      int i = this._inputData.readUnsignedByte();
      if (codes[i] != 0) {
         return i == 34 ? this.findName(this._quad1, q2, q3, 1) : this.parseName(this._quad1, q2, q3, i, 1);
      } else {
         q3 = q3 << 8 | i;
         i = this._inputData.readUnsignedByte();
         if (codes[i] != 0) {
            return i == 34 ? this.findName(this._quad1, q2, q3, 2) : this.parseName(this._quad1, q2, q3, i, 2);
         } else {
            q3 = q3 << 8 | i;
            i = this._inputData.readUnsignedByte();
            if (codes[i] != 0) {
               return i == 34 ? this.findName(this._quad1, q2, q3, 3) : this.parseName(this._quad1, q2, q3, i, 3);
            } else {
               q3 = q3 << 8 | i;
               i = this._inputData.readUnsignedByte();
               if (codes[i] != 0) {
                  return i == 34 ? this.findName(this._quad1, q2, q3, 4) : this.parseName(this._quad1, q2, q3, i, 4);
               } else {
                  return this._parseLongName(i, q2, q3);
               }
            }
         }
      }
   }

   private final String _parseLongName(int q, int q2, int q3) throws IOException {
      this._quadBuffer[0] = this._quad1;
      this._quadBuffer[1] = q2;
      this._quadBuffer[2] = q3;
      int[] codes = _icLatin1;
      int qlen = 3;

      while(true) {
         int i = this._inputData.readUnsignedByte();
         if (codes[i] != 0) {
            return i == 34 ? this.findName(this._quadBuffer, qlen, q, 1) : this.parseEscapedName(this._quadBuffer, qlen, q, i, 1);
         }

         q = q << 8 | i;
         i = this._inputData.readUnsignedByte();
         if (codes[i] != 0) {
            if (i == 34) {
               return this.findName(this._quadBuffer, qlen, q, 2);
            }

            return this.parseEscapedName(this._quadBuffer, qlen, q, i, 2);
         }

         q = q << 8 | i;
         i = this._inputData.readUnsignedByte();
         if (codes[i] != 0) {
            if (i == 34) {
               return this.findName(this._quadBuffer, qlen, q, 3);
            }

            return this.parseEscapedName(this._quadBuffer, qlen, q, i, 3);
         }

         q = q << 8 | i;
         i = this._inputData.readUnsignedByte();
         if (codes[i] != 0) {
            if (i == 34) {
               return this.findName(this._quadBuffer, qlen, q, 4);
            }

            return this.parseEscapedName(this._quadBuffer, qlen, q, i, 4);
         }

         if (qlen >= this._quadBuffer.length) {
            this._quadBuffer = _growArrayBy(this._quadBuffer, qlen);
         }

         this._quadBuffer[qlen++] = q;
         q = i;
      }
   }

   private final String parseName(int q1, int ch, int lastQuadBytes) throws IOException {
      return this.parseEscapedName(this._quadBuffer, 0, q1, ch, lastQuadBytes);
   }

   private final String parseName(int q1, int q2, int ch, int lastQuadBytes) throws IOException {
      this._quadBuffer[0] = q1;
      return this.parseEscapedName(this._quadBuffer, 1, q2, ch, lastQuadBytes);
   }

   private final String parseName(int q1, int q2, int q3, int ch, int lastQuadBytes) throws IOException {
      this._quadBuffer[0] = q1;
      this._quadBuffer[1] = q2;
      return this.parseEscapedName(this._quadBuffer, 2, q3, ch, lastQuadBytes);
   }

   protected final String parseEscapedName(int[] quads, int qlen, int currQuad, int ch, int currQuadBytes) throws IOException {
      int[] codes = _icLatin1;

      while(true) {
         if (codes[ch] != 0) {
            if (ch == 34) {
               if (currQuadBytes > 0) {
                  if (qlen >= quads.length) {
                     this._quadBuffer = quads = _growArrayBy(quads, quads.length);
                  }

                  quads[qlen++] = pad(currQuad, currQuadBytes);
               }

               String name = this._symbols.findName(quads, qlen);
               if (name == null) {
                  name = this.addName(quads, qlen, currQuadBytes);
               }

               return name;
            }

            if (ch != 92) {
               this._throwUnquotedSpace(ch, "name");
            } else {
               ch = this._decodeEscaped();
            }

            if (ch > 127) {
               if (currQuadBytes >= 4) {
                  if (qlen >= quads.length) {
                     this._quadBuffer = quads = _growArrayBy(quads, quads.length);
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
                        this._quadBuffer = quads = _growArrayBy(quads, quads.length);
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
               this._quadBuffer = quads = _growArrayBy(quads, quads.length);
            }

            quads[qlen++] = currQuad;
            currQuad = ch;
            currQuadBytes = 1;
         }

         ch = this._inputData.readUnsignedByte();
      }
   }

   protected String _handleOddName(int ch) throws IOException {
      if (ch == 39 && this.isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES)) {
         return this._parseAposName();
      } else {
         if (!this.isEnabled(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)) {
            char c = (char)this._decodeCharForError(ch);
            this._reportUnexpectedChar(c, "was expecting double-quote to start field name");
         }

         int[] codes = CharTypes.getInputCodeUtf8JsNames();
         if (codes[ch] != 0) {
            this._reportUnexpectedChar(ch, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
         }

         int[] quads = this._quadBuffer;
         int qlen = 0;
         int currQuad = 0;
         int currQuadBytes = 0;

         do {
            if (currQuadBytes < 4) {
               ++currQuadBytes;
               currQuad = currQuad << 8 | ch;
            } else {
               if (qlen >= quads.length) {
                  this._quadBuffer = quads = _growArrayBy(quads, quads.length);
               }

               quads[qlen++] = currQuad;
               currQuad = ch;
               currQuadBytes = 1;
            }

            ch = this._inputData.readUnsignedByte();
         } while(codes[ch] == 0);

         this._nextByte = ch;
         if (currQuadBytes > 0) {
            if (qlen >= quads.length) {
               this._quadBuffer = quads = _growArrayBy(quads, quads.length);
            }

            quads[qlen++] = currQuad;
         }

         String name = this._symbols.findName(quads, qlen);
         if (name == null) {
            name = this.addName(quads, qlen, currQuadBytes);
         }

         return name;
      }
   }

   protected String _parseAposName() throws IOException {
      int ch = this._inputData.readUnsignedByte();
      if (ch == 39) {
         return "";
      } else {
         int[] quads = this._quadBuffer;
         int qlen = 0;
         int currQuad = 0;
         int currQuadBytes = 0;

         for(int[] codes = _icLatin1; ch != 39; ch = this._inputData.readUnsignedByte()) {
            if (ch != 34 && codes[ch] != 0) {
               if (ch != 92) {
                  this._throwUnquotedSpace(ch, "name");
               } else {
                  ch = this._decodeEscaped();
               }

               if (ch > 127) {
                  if (currQuadBytes >= 4) {
                     if (qlen >= quads.length) {
                        this._quadBuffer = quads = _growArrayBy(quads, quads.length);
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
                           this._quadBuffer = quads = _growArrayBy(quads, quads.length);
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
                  this._quadBuffer = quads = _growArrayBy(quads, quads.length);
               }

               quads[qlen++] = currQuad;
               currQuad = ch;
               currQuadBytes = 1;
            }
         }

         if (currQuadBytes > 0) {
            if (qlen >= quads.length) {
               this._quadBuffer = quads = _growArrayBy(quads, quads.length);
            }

            quads[qlen++] = pad(currQuad, currQuadBytes);
         }

         String name = this._symbols.findName(quads, qlen);
         if (name == null) {
            name = this.addName(quads, qlen, currQuadBytes);
         }

         return name;
      }
   }

   private final String findName(int q1, int lastQuadBytes) throws JsonParseException {
      q1 = pad(q1, lastQuadBytes);
      String name = this._symbols.findName(q1);
      if (name != null) {
         return name;
      } else {
         this._quadBuffer[0] = q1;
         return this.addName(this._quadBuffer, 1, lastQuadBytes);
      }
   }

   private final String findName(int q1, int q2, int lastQuadBytes) throws JsonParseException {
      q2 = pad(q2, lastQuadBytes);
      String name = this._symbols.findName(q1, q2);
      if (name != null) {
         return name;
      } else {
         this._quadBuffer[0] = q1;
         this._quadBuffer[1] = q2;
         return this.addName(this._quadBuffer, 2, lastQuadBytes);
      }
   }

   private final String findName(int q1, int q2, int q3, int lastQuadBytes) throws JsonParseException {
      q3 = pad(q3, lastQuadBytes);
      String name = this._symbols.findName(q1, q2, q3);
      if (name != null) {
         return name;
      } else {
         int[] quads = this._quadBuffer;
         quads[0] = q1;
         quads[1] = q2;
         quads[2] = pad(q3, lastQuadBytes);
         return this.addName(quads, 3, lastQuadBytes);
      }
   }

   private final String findName(int[] quads, int qlen, int lastQuad, int lastQuadBytes) throws JsonParseException {
      if (qlen >= quads.length) {
         this._quadBuffer = quads = _growArrayBy(quads, quads.length);
      }

      quads[qlen++] = pad(lastQuad, lastQuadBytes);
      String name = this._symbols.findName(quads, qlen);
      return name == null ? this.addName(quads, qlen, lastQuadBytes) : name;
   }

   private final String addName(int[] quads, int qlen, int lastQuadBytes) throws JsonParseException {
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

   protected void _finishString() throws IOException {
      int outPtr = 0;
      char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
      int[] codes = _icUTF8;
      int outEnd = outBuf.length;

      do {
         int c = this._inputData.readUnsignedByte();
         if (codes[c] != 0) {
            if (c == 34) {
               this._textBuffer.setCurrentLength(outPtr);
               return;
            } else {
               this._finishString2(outBuf, outPtr, c);
               return;
            }
         }

         outBuf[outPtr++] = (char)c;
      } while(outPtr < outEnd);

      this._finishString2(outBuf, outPtr, this._inputData.readUnsignedByte());
   }

   private String _finishAndReturnString() throws IOException {
      int outPtr = 0;
      char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
      int[] codes = _icUTF8;
      int outEnd = outBuf.length;

      do {
         int c = this._inputData.readUnsignedByte();
         if (codes[c] != 0) {
            if (c == 34) {
               return this._textBuffer.setCurrentAndReturn(outPtr);
            } else {
               this._finishString2(outBuf, outPtr, c);
               return this._textBuffer.contentsAsString();
            }
         }

         outBuf[outPtr++] = (char)c;
      } while(outPtr < outEnd);

      this._finishString2(outBuf, outPtr, this._inputData.readUnsignedByte());
      return this._textBuffer.contentsAsString();
   }

   private final void _finishString2(char[] outBuf, int outPtr, int c) throws IOException {
      int[] codes = _icUTF8;
      int outEnd = outBuf.length;

      while(true) {
         while(codes[c] != 0) {
            if (c == 34) {
               this._textBuffer.setCurrentLength(outPtr);
               return;
            }

            switch(codes[c]) {
            case 1:
               c = this._decodeEscaped();
               break;
            case 2:
               c = this._decodeUtf8_2(c);
               break;
            case 3:
               c = this._decodeUtf8_3(c);
               break;
            case 4:
               c = this._decodeUtf8_4(c);
               outBuf[outPtr++] = (char)('\ud800' | c >> 10);
               if (outPtr >= outBuf.length) {
                  outBuf = this._textBuffer.finishCurrentSegment();
                  outPtr = 0;
                  outEnd = outBuf.length;
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
               outEnd = outBuf.length;
            }

            outBuf[outPtr++] = (char)c;
            c = this._inputData.readUnsignedByte();
         }

         if (outPtr >= outEnd) {
            outBuf = this._textBuffer.finishCurrentSegment();
            outPtr = 0;
            outEnd = outBuf.length;
         }

         outBuf[outPtr++] = (char)c;
         c = this._inputData.readUnsignedByte();
      }
   }

   protected void _skipString() throws IOException {
      this._tokenIncomplete = false;
      int[] codes = _icUTF8;

      while(true) {
         int c;
         do {
            c = this._inputData.readUnsignedByte();
         } while(codes[c] == 0);

         if (c == 34) {
            return;
         }

         switch(codes[c]) {
         case 1:
            this._decodeEscaped();
            break;
         case 2:
            this._skipUtf8_2();
            break;
         case 3:
            this._skipUtf8_3();
            break;
         case 4:
            this._skipUtf8_4();
            break;
         default:
            if (c < 32) {
               this._throwUnquotedSpace(c, "string value");
            } else {
               this._reportInvalidChar(c);
            }
         }
      }
   }

   protected JsonToken _handleUnexpectedValue(int c) throws IOException {
      switch(c) {
      case 43:
         return this._handleInvalidNumberStart(this._inputData.readUnsignedByte(), false);
      case 73:
         this._matchToken("Infinity", 1);
         if (this.isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
            return this.resetAsNaN("Infinity", Double.POSITIVE_INFINITY);
         }

         this._reportError("Non-standard token 'Infinity': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
         break;
      case 78:
         this._matchToken("NaN", 1);
         if (this.isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
            return this.resetAsNaN("NaN", Double.NaN);
         }

         this._reportError("Non-standard token 'NaN': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
         break;
      case 93:
         if (!this._parsingContext.inArray()) {
            break;
         }
      case 44:
         if (this.isEnabled(JsonParser.Feature.ALLOW_MISSING_VALUES)) {
            this._nextByte = c;
            return JsonToken.VALUE_NULL;
         }
      case 125:
         this._reportUnexpectedChar(c, "expected a value");
      case 39:
         if (this.isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES)) {
            return this._handleApos();
         }
      }

      if (Character.isJavaIdentifierStart(c)) {
         this._reportInvalidToken(c, "" + (char)c, "('true', 'false' or 'null')");
      }

      this._reportUnexpectedChar(c, "expected a valid value (number, String, array, object, 'true', 'false' or 'null')");
      return null;
   }

   protected JsonToken _handleApos() throws IOException {
      int c = false;
      int outPtr = 0;
      char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
      int[] codes = _icUTF8;

      while(true) {
         while(true) {
            int outEnd = outBuf.length;
            if (outPtr >= outBuf.length) {
               outBuf = this._textBuffer.finishCurrentSegment();
               outPtr = 0;
               outEnd = outBuf.length;
            }

            while(true) {
               int c = this._inputData.readUnsignedByte();
               if (c == 39) {
                  this._textBuffer.setCurrentLength(outPtr);
                  return JsonToken.VALUE_STRING;
               }

               if (codes[c] != 0) {
                  switch(codes[c]) {
                  case 1:
                     c = this._decodeEscaped();
                     break;
                  case 2:
                     c = this._decodeUtf8_2(c);
                     break;
                  case 3:
                     c = this._decodeUtf8_3(c);
                     break;
                  case 4:
                     c = this._decodeUtf8_4(c);
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
                     }

                     this._reportInvalidChar(c);
                  }

                  if (outPtr >= outBuf.length) {
                     outBuf = this._textBuffer.finishCurrentSegment();
                     outPtr = 0;
                  }

                  outBuf[outPtr++] = (char)c;
                  break;
               }

               outBuf[outPtr++] = (char)c;
               if (outPtr >= outEnd) {
                  break;
               }
            }
         }
      }
   }

   protected JsonToken _handleInvalidNumberStart(int ch, boolean neg) throws IOException {
      while(ch == 73) {
         ch = this._inputData.readUnsignedByte();
         String match;
         if (ch == 78) {
            match = neg ? "-INF" : "+INF";
         } else {
            if (ch != 110) {
               break;
            }

            match = neg ? "-Infinity" : "+Infinity";
         }

         this._matchToken(match, 3);
         if (this.isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
            return this.resetAsNaN(match, neg ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
         }

         this._reportError("Non-standard token '" + match + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
      }

      this.reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
      return null;
   }

   protected final void _matchToken(String matchStr, int i) throws IOException {
      int len = matchStr.length();

      int ch;
      do {
         ch = this._inputData.readUnsignedByte();
         if (ch != matchStr.charAt(i)) {
            this._reportInvalidToken(ch, matchStr.substring(0, i));
         }

         ++i;
      } while(i < len);

      ch = this._inputData.readUnsignedByte();
      if (ch >= 48 && ch != 93 && ch != 125) {
         this._checkMatchEnd(matchStr, i, ch);
      }

      this._nextByte = ch;
   }

   private final void _checkMatchEnd(String matchStr, int i, int ch) throws IOException {
      char c = (char)this._decodeCharForError(ch);
      if (Character.isJavaIdentifierPart(c)) {
         this._reportInvalidToken(c, matchStr.substring(0, i));
      }

   }

   private final int _skipWS() throws IOException {
      int i = this._nextByte;
      if (i < 0) {
         i = this._inputData.readUnsignedByte();
      } else {
         this._nextByte = -1;
      }

      for(; i <= 32; i = this._inputData.readUnsignedByte()) {
         if (i == 13 || i == 10) {
            ++this._currInputRow;
         }
      }

      if (i != 47 && i != 35) {
         return i;
      } else {
         return this._skipWSComment(i);
      }
   }

   private final int _skipWSOrEnd() throws IOException {
      int i = this._nextByte;
      if (i < 0) {
         try {
            i = this._inputData.readUnsignedByte();
         } catch (EOFException var4) {
            return this._eofAsNextChar();
         }
      } else {
         this._nextByte = -1;
      }

      while(i <= 32) {
         if (i == 13 || i == 10) {
            ++this._currInputRow;
         }

         try {
            i = this._inputData.readUnsignedByte();
         } catch (EOFException var3) {
            return this._eofAsNextChar();
         }
      }

      if (i != 47 && i != 35) {
         return i;
      } else {
         return this._skipWSComment(i);
      }
   }

   private final int _skipWSComment(int i) throws IOException {
      while(true) {
         if (i > 32) {
            if (i == 47) {
               this._skipComment();
            } else {
               if (i != 35) {
                  return i;
               }

               if (!this._skipYAMLComment()) {
                  return i;
               }
            }
         } else if (i == 13 || i == 10) {
            ++this._currInputRow;
         }

         i = this._inputData.readUnsignedByte();
      }
   }

   private final int _skipColon() throws IOException {
      int i = this._nextByte;
      if (i < 0) {
         i = this._inputData.readUnsignedByte();
      } else {
         this._nextByte = -1;
      }

      if (i == 58) {
         i = this._inputData.readUnsignedByte();
         if (i > 32) {
            return i != 47 && i != 35 ? i : this._skipColon2(i, true);
         } else {
            if (i == 32 || i == 9) {
               i = this._inputData.readUnsignedByte();
               if (i > 32) {
                  if (i != 47 && i != 35) {
                     return i;
                  }

                  return this._skipColon2(i, true);
               }
            }

            return this._skipColon2(i, true);
         }
      } else {
         if (i == 32 || i == 9) {
            i = this._inputData.readUnsignedByte();
         }

         if (i == 58) {
            i = this._inputData.readUnsignedByte();
            if (i > 32) {
               return i != 47 && i != 35 ? i : this._skipColon2(i, true);
            } else {
               if (i == 32 || i == 9) {
                  i = this._inputData.readUnsignedByte();
                  if (i > 32) {
                     if (i != 47 && i != 35) {
                        return i;
                     }

                     return this._skipColon2(i, true);
                  }
               }

               return this._skipColon2(i, true);
            }
         } else {
            return this._skipColon2(i, false);
         }
      }
   }

   private final int _skipColon2(int i, boolean gotColon) throws IOException {
      while(true) {
         if (i > 32) {
            if (i == 47) {
               this._skipComment();
            } else if (i != 35 || !this._skipYAMLComment()) {
               if (gotColon) {
                  return i;
               }

               if (i != 58) {
                  this._reportUnexpectedChar(i, "was expecting a colon to separate field name and value");
               }

               gotColon = true;
            }
         } else if (i == 13 || i == 10) {
            ++this._currInputRow;
         }

         i = this._inputData.readUnsignedByte();
      }
   }

   private final void _skipComment() throws IOException {
      if (!this.isEnabled(JsonParser.Feature.ALLOW_COMMENTS)) {
         this._reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
      }

      int c = this._inputData.readUnsignedByte();
      if (c == 47) {
         this._skipLine();
      } else if (c == 42) {
         this._skipCComment();
      } else {
         this._reportUnexpectedChar(c, "was expecting either '*' or '/' for a comment");
      }

   }

   private final void _skipCComment() throws IOException {
      int[] codes = CharTypes.getInputCodeComment();
      int i = this._inputData.readUnsignedByte();

      label24:
      do {
         while(true) {
            int code = codes[i];
            if (code != 0) {
               switch(code) {
               case 2:
                  this._skipUtf8_2();
                  break;
               case 3:
                  this._skipUtf8_3();
                  break;
               case 4:
                  this._skipUtf8_4();
                  break;
               case 10:
               case 13:
                  ++this._currInputRow;
                  break;
               case 42:
                  i = this._inputData.readUnsignedByte();
                  continue label24;
               default:
                  this._reportInvalidChar(i);
               }
            }

            i = this._inputData.readUnsignedByte();
         }
      } while(i != 47);

   }

   private final boolean _skipYAMLComment() throws IOException {
      if (!this.isEnabled(JsonParser.Feature.ALLOW_YAML_COMMENTS)) {
         return false;
      } else {
         this._skipLine();
         return true;
      }
   }

   private final void _skipLine() throws IOException {
      int[] codes = CharTypes.getInputCodeComment();

      while(true) {
         int i;
         int code;
         do {
            i = this._inputData.readUnsignedByte();
            code = codes[i];
         } while(code == 0);

         switch(code) {
         case 2:
            this._skipUtf8_2();
            break;
         case 3:
            this._skipUtf8_3();
            break;
         case 4:
            this._skipUtf8_4();
            break;
         case 10:
         case 13:
            ++this._currInputRow;
            return;
         case 42:
            break;
         default:
            if (code < 0) {
               this._reportInvalidChar(i);
            }
         }
      }
   }

   protected char _decodeEscaped() throws IOException {
      int c = this._inputData.readUnsignedByte();
      switch(c) {
      case 34:
      case 47:
      case 92:
         return (char)c;
      case 98:
         return '\b';
      case 102:
         return '\f';
      case 110:
         return '\n';
      case 114:
         return '\r';
      case 116:
         return '\t';
      case 117:
         int value = 0;

         for(int i = 0; i < 4; ++i) {
            int ch = this._inputData.readUnsignedByte();
            int digit = CharTypes.charToHex(ch);
            if (digit < 0) {
               this._reportUnexpectedChar(ch, "expected a hex-digit for character escape sequence");
            }

            value = value << 4 | digit;
         }

         return (char)value;
      default:
         return this._handleUnrecognizedCharacterEscape((char)this._decodeCharForError(c));
      }
   }

   protected int _decodeCharForError(int firstByte) throws IOException {
      int c = firstByte & 255;
      if (c > 127) {
         byte needed;
         if ((c & 224) == 192) {
            c &= 31;
            needed = 1;
         } else if ((c & 240) == 224) {
            c &= 15;
            needed = 2;
         } else if ((c & 248) == 240) {
            c &= 7;
            needed = 3;
         } else {
            this._reportInvalidInitial(c & 255);
            needed = 1;
         }

         int d = this._inputData.readUnsignedByte();
         if ((d & 192) != 128) {
            this._reportInvalidOther(d & 255);
         }

         c = c << 6 | d & 63;
         if (needed > 1) {
            d = this._inputData.readUnsignedByte();
            if ((d & 192) != 128) {
               this._reportInvalidOther(d & 255);
            }

            c = c << 6 | d & 63;
            if (needed > 2) {
               d = this._inputData.readUnsignedByte();
               if ((d & 192) != 128) {
                  this._reportInvalidOther(d & 255);
               }

               c = c << 6 | d & 63;
            }
         }
      }

      return c;
   }

   private final int _decodeUtf8_2(int c) throws IOException {
      int d = this._inputData.readUnsignedByte();
      if ((d & 192) != 128) {
         this._reportInvalidOther(d & 255);
      }

      return (c & 31) << 6 | d & 63;
   }

   private final int _decodeUtf8_3(int c1) throws IOException {
      c1 &= 15;
      int d = this._inputData.readUnsignedByte();
      if ((d & 192) != 128) {
         this._reportInvalidOther(d & 255);
      }

      int c = c1 << 6 | d & 63;
      d = this._inputData.readUnsignedByte();
      if ((d & 192) != 128) {
         this._reportInvalidOther(d & 255);
      }

      c = c << 6 | d & 63;
      return c;
   }

   private final int _decodeUtf8_4(int c) throws IOException {
      int d = this._inputData.readUnsignedByte();
      if ((d & 192) != 128) {
         this._reportInvalidOther(d & 255);
      }

      c = (c & 7) << 6 | d & 63;
      d = this._inputData.readUnsignedByte();
      if ((d & 192) != 128) {
         this._reportInvalidOther(d & 255);
      }

      c = c << 6 | d & 63;
      d = this._inputData.readUnsignedByte();
      if ((d & 192) != 128) {
         this._reportInvalidOther(d & 255);
      }

      return (c << 6 | d & 63) - 65536;
   }

   private final void _skipUtf8_2() throws IOException {
      int c = this._inputData.readUnsignedByte();
      if ((c & 192) != 128) {
         this._reportInvalidOther(c & 255);
      }

   }

   private final void _skipUtf8_3() throws IOException {
      int c = this._inputData.readUnsignedByte();
      if ((c & 192) != 128) {
         this._reportInvalidOther(c & 255);
      }

      c = this._inputData.readUnsignedByte();
      if ((c & 192) != 128) {
         this._reportInvalidOther(c & 255);
      }

   }

   private final void _skipUtf8_4() throws IOException {
      int d = this._inputData.readUnsignedByte();
      if ((d & 192) != 128) {
         this._reportInvalidOther(d & 255);
      }

      d = this._inputData.readUnsignedByte();
      if ((d & 192) != 128) {
         this._reportInvalidOther(d & 255);
      }

      d = this._inputData.readUnsignedByte();
      if ((d & 192) != 128) {
         this._reportInvalidOther(d & 255);
      }

   }

   protected void _reportInvalidToken(int ch, String matchedPart) throws IOException {
      this._reportInvalidToken(ch, matchedPart, "'null', 'true', 'false' or NaN");
   }

   protected void _reportInvalidToken(int ch, String matchedPart, String msg) throws IOException {
      StringBuilder sb = new StringBuilder(matchedPart);

      while(true) {
         char c = (char)this._decodeCharForError(ch);
         if (!Character.isJavaIdentifierPart(c)) {
            this._reportError("Unrecognized token '" + sb.toString() + "': was expecting " + msg);
            return;
         }

         sb.append(c);
         ch = this._inputData.readUnsignedByte();
      }
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

   private void _reportInvalidOther(int mask) throws JsonParseException {
      this._reportError("Invalid UTF-8 middle byte 0x" + Integer.toHexString(mask));
   }

   private static int[] _growArrayBy(int[] arr, int more) {
      return arr == null ? new int[more] : Arrays.copyOf(arr, arr.length + more);
   }

   protected final byte[] _decodeBase64(Base64Variant b64variant) throws IOException {
      ByteArrayBuilder builder = this._getByteArrayBuilder();

      while(true) {
         while(true) {
            int ch;
            int bits;
            do {
               do {
                  ch = this._inputData.readUnsignedByte();
               } while(ch <= 32);

               bits = b64variant.decodeBase64Char(ch);
               if (bits >= 0) {
                  break;
               }

               if (ch == 34) {
                  return builder.toByteArray();
               }

               bits = this._decodeBase64Escape(b64variant, ch, 0);
            } while(bits < 0);

            int decodedData = bits;
            ch = this._inputData.readUnsignedByte();
            bits = b64variant.decodeBase64Char(ch);
            if (bits < 0) {
               bits = this._decodeBase64Escape(b64variant, ch, 1);
            }

            decodedData = decodedData << 6 | bits;
            ch = this._inputData.readUnsignedByte();
            bits = b64variant.decodeBase64Char(ch);
            if (bits < 0) {
               if (bits != -2) {
                  if (ch == 34 && !b64variant.usesPadding()) {
                     decodedData >>= 4;
                     builder.append(decodedData);
                     return builder.toByteArray();
                  }

                  bits = this._decodeBase64Escape(b64variant, ch, 2);
               }

               if (bits == -2) {
                  ch = this._inputData.readUnsignedByte();
                  if (!b64variant.usesPaddingChar(ch)) {
                     throw this.reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
                  }

                  decodedData >>= 4;
                  builder.append(decodedData);
                  continue;
               }
            }

            decodedData = decodedData << 6 | bits;
            ch = this._inputData.readUnsignedByte();
            bits = b64variant.decodeBase64Char(ch);
            if (bits < 0) {
               if (bits != -2) {
                  if (ch == 34 && !b64variant.usesPadding()) {
                     decodedData >>= 2;
                     builder.appendTwoBytes(decodedData);
                     return builder.toByteArray();
                  }

                  bits = this._decodeBase64Escape(b64variant, ch, 3);
               }

               if (bits == -2) {
                  decodedData >>= 2;
                  builder.appendTwoBytes(decodedData);
                  continue;
               }
            }

            decodedData = decodedData << 6 | bits;
            builder.appendThreeBytes(decodedData);
         }
      }
   }

   public JsonLocation getTokenLocation() {
      return new JsonLocation(this._getSourceReference(), -1L, -1L, this._tokenInputRow, -1);
   }

   public JsonLocation getCurrentLocation() {
      return new JsonLocation(this._getSourceReference(), -1L, -1L, this._currInputRow, -1);
   }

   private void _closeScope(int i) throws JsonParseException {
      if (i == 93) {
         if (!this._parsingContext.inArray()) {
            this._reportMismatchedEndMarker(i, '}');
         }

         this._parsingContext = this._parsingContext.clearAndGetParent();
         this._currToken = JsonToken.END_ARRAY;
      }

      if (i == 125) {
         if (!this._parsingContext.inObject()) {
            this._reportMismatchedEndMarker(i, ']');
         }

         this._parsingContext = this._parsingContext.clearAndGetParent();
         this._currToken = JsonToken.END_OBJECT;
      }

   }

   private static final int pad(int q, int bytes) {
      return bytes == 4 ? q : q | -1 << (bytes << 3);
   }
}

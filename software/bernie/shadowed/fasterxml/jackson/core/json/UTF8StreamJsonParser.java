package software.bernie.shadowed.fasterxml.jackson.core.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import software.bernie.shadowed.fasterxml.jackson.core.Base64Variant;
import software.bernie.shadowed.fasterxml.jackson.core.JsonLocation;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParseException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.ObjectCodec;
import software.bernie.shadowed.fasterxml.jackson.core.SerializableString;
import software.bernie.shadowed.fasterxml.jackson.core.base.ParserBase;
import software.bernie.shadowed.fasterxml.jackson.core.io.CharTypes;
import software.bernie.shadowed.fasterxml.jackson.core.io.IOContext;
import software.bernie.shadowed.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
import software.bernie.shadowed.fasterxml.jackson.core.util.ByteArrayBuilder;

public class UTF8StreamJsonParser extends ParserBase {
   static final byte BYTE_LF = 10;
   private static final int[] _icUTF8 = CharTypes.getInputCodeUtf8();
   protected static final int[] _icLatin1 = CharTypes.getInputCodeLatin1();
   protected static final int FEAT_MASK_TRAILING_COMMA;
   protected ObjectCodec _objectCodec;
   protected final ByteQuadsCanonicalizer _symbols;
   protected int[] _quadBuffer = new int[16];
   protected boolean _tokenIncomplete;
   private int _quad1;
   protected int _nameStartOffset;
   protected int _nameStartRow;
   protected int _nameStartCol;
   protected InputStream _inputStream;
   protected byte[] _inputBuffer;
   protected boolean _bufferRecyclable;

   public UTF8StreamJsonParser(IOContext ctxt, int features, InputStream in, ObjectCodec codec, ByteQuadsCanonicalizer sym, byte[] inputBuffer, int start, int end, boolean bufferRecyclable) {
      super(ctxt, features);
      this._inputStream = in;
      this._objectCodec = codec;
      this._symbols = sym;
      this._inputBuffer = inputBuffer;
      this._inputPtr = start;
      this._inputEnd = end;
      this._currInputRowStart = start;
      this._currInputProcessed = (long)(-start);
      this._bufferRecyclable = bufferRecyclable;
   }

   public ObjectCodec getCodec() {
      return this._objectCodec;
   }

   public void setCodec(ObjectCodec c) {
      this._objectCodec = c;
   }

   public int releaseBuffered(OutputStream out) throws IOException {
      int count = this._inputEnd - this._inputPtr;
      if (count < 1) {
         return 0;
      } else {
         int origPtr = this._inputPtr;
         out.write(this._inputBuffer, origPtr, count);
         return count;
      }
   }

   public Object getInputSource() {
      return this._inputStream;
   }

   protected final boolean _loadMore() throws IOException {
      int bufSize = this._inputEnd;
      this._currInputProcessed += (long)this._inputEnd;
      this._currInputRowStart -= this._inputEnd;
      this._nameStartOffset -= bufSize;
      if (this._inputStream != null) {
         int space = this._inputBuffer.length;
         if (space == 0) {
            return false;
         }

         int count = this._inputStream.read(this._inputBuffer, 0, space);
         if (count > 0) {
            this._inputPtr = 0;
            this._inputEnd = count;
            return true;
         }

         this._closeInput();
         if (count == 0) {
            throw new IOException("InputStream.read() returned 0 characters when trying to read " + this._inputBuffer.length + " bytes");
         }
      }

      return false;
   }

   protected void _closeInput() throws IOException {
      if (this._inputStream != null) {
         if (this._ioContext.isResourceManaged() || this.isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE)) {
            this._inputStream.close();
         }

         this._inputStream = null;
      }

   }

   protected void _releaseBuffers() throws IOException {
      super._releaseBuffers();
      this._symbols.release();
      if (this._bufferRecyclable) {
         byte[] buf = this._inputBuffer;
         if (buf != null) {
            this._inputBuffer = NO_BYTES;
            this._ioContext.releaseReadIOBuffer(buf);
         }
      }

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
      if (this._currToken != null) {
         switch(this._currToken.id()) {
         case 5:
            return this._parsingContext.getCurrentName().length();
         case 6:
            if (this._tokenIncomplete) {
               this._tokenIncomplete = false;
               this._finishString();
            }
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
            if (this._inputPtr >= this._inputEnd) {
               this._loadMoreGuaranteed();
            }

            ch = this._inputBuffer[this._inputPtr++] & 255;
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
         if (this._inputPtr >= this._inputEnd) {
            this._loadMoreGuaranteed();
         }

         ch = this._inputBuffer[this._inputPtr++] & 255;
         bits = b64variant.decodeBase64Char(ch);
         if (bits < 0) {
            bits = this._decodeBase64Escape(b64variant, ch, 1);
         }

         decodedData = decodedData << 6 | bits;
         if (this._inputPtr >= this._inputEnd) {
            this._loadMoreGuaranteed();
         }

         ch = this._inputBuffer[this._inputPtr++] & 255;
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
               if (this._inputPtr >= this._inputEnd) {
                  this._loadMoreGuaranteed();
               }

               ch = this._inputBuffer[this._inputPtr++] & 255;
               if (!b64variant.usesPaddingChar(ch)) {
                  throw this.reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
               }

               decodedData >>= 4;
               buffer[outputPtr++] = (byte)decodedData;
               continue;
            }
         }

         decodedData = decodedData << 6 | bits;
         if (this._inputPtr >= this._inputEnd) {
            this._loadMoreGuaranteed();
         }

         ch = this._inputBuffer[this._inputPtr++] & 255;
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
            if (i == 93) {
               this._closeArrayScope();
               return this._currToken = JsonToken.END_ARRAY;
            } else if (i == 125) {
               this._closeObjectScope();
               return this._currToken = JsonToken.END_OBJECT;
            } else {
               if (this._parsingContext.expectComma()) {
                  if (i != 44) {
                     this._reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
                  }

                  i = this._skipWS();
                  if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (i == 93 || i == 125)) {
                     return this._closeScope(i);
                  }
               }

               if (!this._parsingContext.inObject()) {
                  this._updateLocation();
                  return this._nextTokenNotInObject(i);
               } else {
                  this._updateNameLocation();
                  String n = this._parseName(i);
                  this._parsingContext.setCurrentName(n);
                  this._currToken = JsonToken.FIELD_NAME;
                  i = this._skipColon();
                  this._updateLocation();
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
                        this._matchFalse();
                        t = JsonToken.VALUE_FALSE;
                        break;
                     case 110:
                        this._matchNull();
                        t = JsonToken.VALUE_NULL;
                        break;
                     case 116:
                        this._matchTrue();
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
            this._matchFalse();
            return this._currToken = JsonToken.VALUE_FALSE;
         case 110:
            this._matchNull();
            return this._currToken = JsonToken.VALUE_NULL;
         case 116:
            this._matchTrue();
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

   public boolean nextFieldName(SerializableString str) throws IOException {
      this._numTypesValid = 0;
      if (this._currToken == JsonToken.FIELD_NAME) {
         this._nextAfterName();
         return false;
      } else {
         if (this._tokenIncomplete) {
            this._skipString();
         }

         int i = this._skipWSOrEnd();
         if (i < 0) {
            this.close();
            this._currToken = null;
            return false;
         } else {
            this._binaryValue = null;
            if (i == 93) {
               this._closeArrayScope();
               this._currToken = JsonToken.END_ARRAY;
               return false;
            } else if (i == 125) {
               this._closeObjectScope();
               this._currToken = JsonToken.END_OBJECT;
               return false;
            } else {
               if (this._parsingContext.expectComma()) {
                  if (i != 44) {
                     this._reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
                  }

                  i = this._skipWS();
                  if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (i == 93 || i == 125)) {
                     this._closeScope(i);
                     return false;
                  }
               }

               if (!this._parsingContext.inObject()) {
                  this._updateLocation();
                  this._nextTokenNotInObject(i);
                  return false;
               } else {
                  this._updateNameLocation();
                  if (i == 34) {
                     byte[] nameBytes = str.asQuotedUTF8();
                     int len = nameBytes.length;
                     if (this._inputPtr + len + 4 < this._inputEnd) {
                        int end = this._inputPtr + len;
                        if (this._inputBuffer[end] == 34) {
                           int offset = 0;
                           int ptr = this._inputPtr;

                           while(true) {
                              if (ptr == end) {
                                 this._parsingContext.setCurrentName(str.getValue());
                                 i = this._skipColonFast(ptr + 1);
                                 this._isNextTokenNameYes(i);
                                 return true;
                              }

                              if (nameBytes[offset] != this._inputBuffer[ptr]) {
                                 break;
                              }

                              ++offset;
                              ++ptr;
                           }
                        }
                     }
                  }

                  return this._isNextTokenNameMaybe(i, str);
               }
            }
         }
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

         int i = this._skipWSOrEnd();
         if (i < 0) {
            this.close();
            this._currToken = null;
            return null;
         } else {
            this._binaryValue = null;
            if (i == 93) {
               this._closeArrayScope();
               this._currToken = JsonToken.END_ARRAY;
               return null;
            } else if (i == 125) {
               this._closeObjectScope();
               this._currToken = JsonToken.END_OBJECT;
               return null;
            } else {
               if (this._parsingContext.expectComma()) {
                  if (i != 44) {
                     this._reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
                  }

                  i = this._skipWS();
                  if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (i == 93 || i == 125)) {
                     this._closeScope(i);
                     return null;
                  }
               }

               if (!this._parsingContext.inObject()) {
                  this._updateLocation();
                  this._nextTokenNotInObject(i);
                  return null;
               } else {
                  this._updateNameLocation();
                  String nameStr = this._parseName(i);
                  this._parsingContext.setCurrentName(nameStr);
                  this._currToken = JsonToken.FIELD_NAME;
                  i = this._skipColon();
                  this._updateLocation();
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
                        this._matchFalse();
                        t = JsonToken.VALUE_FALSE;
                        break;
                     case 110:
                        this._matchNull();
                        t = JsonToken.VALUE_NULL;
                        break;
                     case 116:
                        this._matchTrue();
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
            }
         }
      }
   }

   private final int _skipColonFast(int ptr) throws IOException {
      int i = this._inputBuffer[ptr++];
      if (i == 58) {
         i = this._inputBuffer[ptr++];
         if (i > 32) {
            if (i != 47 && i != 35) {
               this._inputPtr = ptr;
               return i;
            }
         } else if (i == 32 || i == 9) {
            i = this._inputBuffer[ptr++];
            if (i > 32 && i != 47 && i != 35) {
               this._inputPtr = ptr;
               return i;
            }
         }

         this._inputPtr = ptr - 1;
         return this._skipColon2(true);
      } else {
         if (i == 32 || i == 9) {
            i = this._inputBuffer[ptr++];
         }

         if (i != 58) {
            this._inputPtr = ptr - 1;
            return this._skipColon2(false);
         } else {
            i = this._inputBuffer[ptr++];
            if (i > 32) {
               if (i != 47 && i != 35) {
                  this._inputPtr = ptr;
                  return i;
               }
            } else if (i == 32 || i == 9) {
               i = this._inputBuffer[ptr++];
               if (i > 32 && i != 47 && i != 35) {
                  this._inputPtr = ptr;
                  return i;
               }
            }

            this._inputPtr = ptr - 1;
            return this._skipColon2(true);
         }
      }
   }

   private final void _isNextTokenNameYes(int i) throws IOException {
      this._currToken = JsonToken.FIELD_NAME;
      this._updateLocation();
      switch(i) {
      case 34:
         this._tokenIncomplete = true;
         this._nextToken = JsonToken.VALUE_STRING;
         return;
      case 45:
         this._nextToken = this._parseNegNumber();
         return;
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
         this._nextToken = this._parsePosNumber(i);
         return;
      case 91:
         this._nextToken = JsonToken.START_ARRAY;
         return;
      case 102:
         this._matchFalse();
         this._nextToken = JsonToken.VALUE_FALSE;
         return;
      case 110:
         this._matchNull();
         this._nextToken = JsonToken.VALUE_NULL;
         return;
      case 116:
         this._matchTrue();
         this._nextToken = JsonToken.VALUE_TRUE;
         return;
      case 123:
         this._nextToken = JsonToken.START_OBJECT;
         return;
      default:
         this._nextToken = this._handleUnexpectedValue(i);
      }
   }

   private final boolean _isNextTokenNameMaybe(int i, SerializableString str) throws IOException {
      String n = this._parseName(i);
      this._parsingContext.setCurrentName(n);
      boolean match = n.equals(str.getValue());
      this._currToken = JsonToken.FIELD_NAME;
      i = this._skipColon();
      this._updateLocation();
      if (i == 34) {
         this._tokenIncomplete = true;
         this._nextToken = JsonToken.VALUE_STRING;
         return match;
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
            this._matchFalse();
            t = JsonToken.VALUE_FALSE;
            break;
         case 110:
            this._matchNull();
            t = JsonToken.VALUE_NULL;
            break;
         case 116:
            this._matchTrue();
            t = JsonToken.VALUE_TRUE;
            break;
         case 123:
            t = JsonToken.START_OBJECT;
            break;
         default:
            t = this._handleUnexpectedValue(i);
         }

         this._nextToken = t;
         return match;
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
      if (c == 48) {
         c = this._verifyNoLeadingZeroes();
      }

      outBuf[0] = (char)c;
      int intLen = 1;
      int outPtr = 1;

      for(int end = Math.min(this._inputEnd, this._inputPtr + outBuf.length - 1); this._inputPtr < end; outBuf[outPtr++] = (char)c) {
         c = this._inputBuffer[this._inputPtr++] & 255;
         if (c < 48 || c > 57) {
            if (c != 46 && c != 101 && c != 69) {
               --this._inputPtr;
               this._textBuffer.setCurrentLength(outPtr);
               if (this._parsingContext.inRoot()) {
                  this._verifyRootSpace(c);
               }

               return this.resetInt(false, intLen);
            } else {
               return this._parseFloat(outBuf, outPtr, c, false, intLen);
            }
         }

         ++intLen;
      }

      return this._parseNumber2(outBuf, outPtr, false, intLen);
   }

   protected JsonToken _parseNegNumber() throws IOException {
      char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
      int outPtr = 0;
      int outPtr = outPtr + 1;
      outBuf[outPtr] = '-';
      if (this._inputPtr >= this._inputEnd) {
         this._loadMoreGuaranteed();
      }

      int c = this._inputBuffer[this._inputPtr++] & 255;
      if (c <= 48) {
         if (c != 48) {
            return this._handleInvalidNumberStart(c, true);
         }

         c = this._verifyNoLeadingZeroes();
      } else if (c > 57) {
         return this._handleInvalidNumberStart(c, true);
      }

      outBuf[outPtr++] = (char)c;
      int intLen = 1;

      for(int end = Math.min(this._inputEnd, this._inputPtr + outBuf.length - outPtr); this._inputPtr < end; outBuf[outPtr++] = (char)c) {
         c = this._inputBuffer[this._inputPtr++] & 255;
         if (c < 48 || c > 57) {
            if (c != 46 && c != 101 && c != 69) {
               --this._inputPtr;
               this._textBuffer.setCurrentLength(outPtr);
               if (this._parsingContext.inRoot()) {
                  this._verifyRootSpace(c);
               }

               return this.resetInt(true, intLen);
            } else {
               return this._parseFloat(outBuf, outPtr, c, true, intLen);
            }
         }

         ++intLen;
      }

      return this._parseNumber2(outBuf, outPtr, true, intLen);
   }

   private final JsonToken _parseNumber2(char[] outBuf, int outPtr, boolean negative, int intPartLength) throws IOException {
      while(this._inputPtr < this._inputEnd || this._loadMore()) {
         int c = this._inputBuffer[this._inputPtr++] & 255;
         if (c > 57 || c < 48) {
            if (c != 46 && c != 101 && c != 69) {
               --this._inputPtr;
               this._textBuffer.setCurrentLength(outPtr);
               if (this._parsingContext.inRoot()) {
                  this._verifyRootSpace(this._inputBuffer[this._inputPtr++] & 255);
               }

               return this.resetInt(negative, intPartLength);
            }

            return this._parseFloat(outBuf, outPtr, c, negative, intPartLength);
         }

         if (outPtr >= outBuf.length) {
            outBuf = this._textBuffer.finishCurrentSegment();
            outPtr = 0;
         }

         outBuf[outPtr++] = (char)c;
         ++intPartLength;
      }

      this._textBuffer.setCurrentLength(outPtr);
      return this.resetInt(negative, intPartLength);
   }

   private final int _verifyNoLeadingZeroes() throws IOException {
      if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
         return 48;
      } else {
         int ch = this._inputBuffer[this._inputPtr] & 255;
         if (ch >= 48 && ch <= 57) {
            if (!this.isEnabled(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS)) {
               this.reportInvalidNumber("Leading zeroes not allowed");
            }

            ++this._inputPtr;
            if (ch == 48) {
               while(this._inputPtr < this._inputEnd || this._loadMore()) {
                  ch = this._inputBuffer[this._inputPtr] & 255;
                  if (ch < 48 || ch > 57) {
                     return 48;
                  }

                  ++this._inputPtr;
                  if (ch != 48) {
                     break;
                  }
               }
            }

            return ch;
         } else {
            return 48;
         }
      }
   }

   private final JsonToken _parseFloat(char[] outBuf, int outPtr, int c, boolean negative, int integerPartLength) throws IOException {
      int fractLen = 0;
      boolean eof = false;
      if (c == 46) {
         if (outPtr >= outBuf.length) {
            outBuf = this._textBuffer.finishCurrentSegment();
            outPtr = 0;
         }

         outBuf[outPtr++] = (char)c;

         while(true) {
            if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
               eof = true;
               break;
            }

            c = this._inputBuffer[this._inputPtr++] & 255;
            if (c < 48 || c > 57) {
               break;
            }

            ++fractLen;
            if (outPtr >= outBuf.length) {
               outBuf = this._textBuffer.finishCurrentSegment();
               outPtr = 0;
            }

            outBuf[outPtr++] = (char)c;
         }

         if (fractLen == 0) {
            this.reportUnexpectedNumberChar(c, "Decimal point not followed by a digit");
         }
      }

      int expLen = 0;
      if (c == 101 || c == 69) {
         if (outPtr >= outBuf.length) {
            outBuf = this._textBuffer.finishCurrentSegment();
            outPtr = 0;
         }

         outBuf[outPtr++] = (char)c;
         if (this._inputPtr >= this._inputEnd) {
            this._loadMoreGuaranteed();
         }

         c = this._inputBuffer[this._inputPtr++] & 255;
         if (c == 45 || c == 43) {
            if (outPtr >= outBuf.length) {
               outBuf = this._textBuffer.finishCurrentSegment();
               outPtr = 0;
            }

            outBuf[outPtr++] = (char)c;
            if (this._inputPtr >= this._inputEnd) {
               this._loadMoreGuaranteed();
            }

            c = this._inputBuffer[this._inputPtr++] & 255;
         }

         while(c >= 48 && c <= 57) {
            ++expLen;
            if (outPtr >= outBuf.length) {
               outBuf = this._textBuffer.finishCurrentSegment();
               outPtr = 0;
            }

            outBuf[outPtr++] = (char)c;
            if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
               eof = true;
               break;
            }

            c = this._inputBuffer[this._inputPtr++] & 255;
         }

         if (expLen == 0) {
            this.reportUnexpectedNumberChar(c, "Exponent indicator not followed by a digit");
         }
      }

      if (!eof) {
         --this._inputPtr;
         if (this._parsingContext.inRoot()) {
            this._verifyRootSpace(c);
         }
      }

      this._textBuffer.setCurrentLength(outPtr);
      return this.resetFloat(negative, integerPartLength, fractLen, expLen);
   }

   private final void _verifyRootSpace(int ch) throws IOException {
      ++this._inputPtr;
      switch(ch) {
      case 9:
      case 32:
         return;
      case 10:
         ++this._currInputRow;
         this._currInputRowStart = this._inputPtr;
         return;
      case 13:
         this._skipCR();
         return;
      default:
         this._reportMissingRootWS(ch);
      }
   }

   protected final String _parseName(int i) throws IOException {
      if (i != 34) {
         return this._handleOddName(i);
      } else if (this._inputPtr + 13 > this._inputEnd) {
         return this.slowParseName();
      } else {
         byte[] input = this._inputBuffer;
         int[] codes = _icLatin1;
         int q = input[this._inputPtr++] & 255;
         if (codes[q] == 0) {
            i = input[this._inputPtr++] & 255;
            if (codes[i] == 0) {
               q = q << 8 | i;
               i = input[this._inputPtr++] & 255;
               if (codes[i] == 0) {
                  q = q << 8 | i;
                  i = input[this._inputPtr++] & 255;
                  if (codes[i] == 0) {
                     q = q << 8 | i;
                     i = input[this._inputPtr++] & 255;
                     if (codes[i] == 0) {
                        this._quad1 = q;
                        return this.parseMediumName(i);
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

   protected final String parseMediumName(int q2) throws IOException {
      byte[] input = this._inputBuffer;
      int[] codes = _icLatin1;
      int i = input[this._inputPtr++] & 255;
      if (codes[i] != 0) {
         return i == 34 ? this.findName(this._quad1, q2, 1) : this.parseName(this._quad1, q2, i, 1);
      } else {
         q2 = q2 << 8 | i;
         i = input[this._inputPtr++] & 255;
         if (codes[i] != 0) {
            return i == 34 ? this.findName(this._quad1, q2, 2) : this.parseName(this._quad1, q2, i, 2);
         } else {
            q2 = q2 << 8 | i;
            i = input[this._inputPtr++] & 255;
            if (codes[i] != 0) {
               return i == 34 ? this.findName(this._quad1, q2, 3) : this.parseName(this._quad1, q2, i, 3);
            } else {
               q2 = q2 << 8 | i;
               i = input[this._inputPtr++] & 255;
               if (codes[i] != 0) {
                  return i == 34 ? this.findName(this._quad1, q2, 4) : this.parseName(this._quad1, q2, i, 4);
               } else {
                  return this.parseMediumName2(i, q2);
               }
            }
         }
      }
   }

   protected final String parseMediumName2(int q3, int q2) throws IOException {
      byte[] input = this._inputBuffer;
      int[] codes = _icLatin1;
      int i = input[this._inputPtr++] & 255;
      if (codes[i] != 0) {
         return i == 34 ? this.findName(this._quad1, q2, q3, 1) : this.parseName(this._quad1, q2, q3, i, 1);
      } else {
         q3 = q3 << 8 | i;
         i = input[this._inputPtr++] & 255;
         if (codes[i] != 0) {
            return i == 34 ? this.findName(this._quad1, q2, q3, 2) : this.parseName(this._quad1, q2, q3, i, 2);
         } else {
            q3 = q3 << 8 | i;
            i = input[this._inputPtr++] & 255;
            if (codes[i] != 0) {
               return i == 34 ? this.findName(this._quad1, q2, q3, 3) : this.parseName(this._quad1, q2, q3, i, 3);
            } else {
               q3 = q3 << 8 | i;
               i = input[this._inputPtr++] & 255;
               if (codes[i] != 0) {
                  return i == 34 ? this.findName(this._quad1, q2, q3, 4) : this.parseName(this._quad1, q2, q3, i, 4);
               } else {
                  return this.parseLongName(i, q2, q3);
               }
            }
         }
      }
   }

   protected final String parseLongName(int q, int q2, int q3) throws IOException {
      this._quadBuffer[0] = this._quad1;
      this._quadBuffer[1] = q2;
      this._quadBuffer[2] = q3;
      byte[] input = this._inputBuffer;
      int[] codes = _icLatin1;

      int qlen;
      int i;
      for(qlen = 3; this._inputPtr + 4 <= this._inputEnd; q = i) {
         i = input[this._inputPtr++] & 255;
         if (codes[i] != 0) {
            if (i == 34) {
               return this.findName(this._quadBuffer, qlen, q, 1);
            }

            return this.parseEscapedName(this._quadBuffer, qlen, q, i, 1);
         }

         q = q << 8 | i;
         i = input[this._inputPtr++] & 255;
         if (codes[i] != 0) {
            if (i == 34) {
               return this.findName(this._quadBuffer, qlen, q, 2);
            }

            return this.parseEscapedName(this._quadBuffer, qlen, q, i, 2);
         }

         q = q << 8 | i;
         i = input[this._inputPtr++] & 255;
         if (codes[i] != 0) {
            if (i == 34) {
               return this.findName(this._quadBuffer, qlen, q, 3);
            }

            return this.parseEscapedName(this._quadBuffer, qlen, q, i, 3);
         }

         q = q << 8 | i;
         i = input[this._inputPtr++] & 255;
         if (codes[i] != 0) {
            if (i == 34) {
               return this.findName(this._quadBuffer, qlen, q, 4);
            }

            return this.parseEscapedName(this._quadBuffer, qlen, q, i, 4);
         }

         if (qlen >= this._quadBuffer.length) {
            this._quadBuffer = growArrayBy(this._quadBuffer, qlen);
         }

         this._quadBuffer[qlen++] = q;
      }

      return this.parseEscapedName(this._quadBuffer, qlen, 0, q, 0);
   }

   protected String slowParseName() throws IOException {
      if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
         this._reportInvalidEOF(": was expecting closing '\"' for name", JsonToken.FIELD_NAME);
      }

      int i = this._inputBuffer[this._inputPtr++] & 255;
      return i == 34 ? "" : this.parseEscapedName(this._quadBuffer, 0, 0, i, 0);
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
                     this._quadBuffer = quads = growArrayBy(quads, quads.length);
                  }

                  quads[qlen++] = _padLastQuad(currQuad, currQuadBytes);
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

         if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
            this._reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
         }

         ch = this._inputBuffer[this._inputPtr++] & 255;
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

         while(true) {
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

            if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
               this._reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
            }

            ch = this._inputBuffer[this._inputPtr] & 255;
            if (codes[ch] != 0) {
               if (currQuadBytes > 0) {
                  if (qlen >= quads.length) {
                     this._quadBuffer = quads = growArrayBy(quads, quads.length);
                  }

                  quads[qlen++] = currQuad;
               }

               String name = this._symbols.findName(quads, qlen);
               if (name == null) {
                  name = this.addName(quads, qlen, currQuadBytes);
               }

               return name;
            }

            ++this._inputPtr;
         }
      }
   }

   protected String _parseAposName() throws IOException {
      if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
         this._reportInvalidEOF(": was expecting closing ''' for field name", JsonToken.FIELD_NAME);
      }

      int ch = this._inputBuffer[this._inputPtr++] & 255;
      if (ch == 39) {
         return "";
      } else {
         int[] quads = this._quadBuffer;
         int qlen = 0;
         int currQuad = 0;
         int currQuadBytes = 0;

         for(int[] codes = _icLatin1; ch != 39; ch = this._inputBuffer[this._inputPtr++] & 255) {
            if (codes[ch] != 0 && ch != 34) {
               if (ch != 92) {
                  this._throwUnquotedSpace(ch, "name");
               } else {
                  ch = this._decodeEscaped();
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

            if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
               this._reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
            }
         }

         if (currQuadBytes > 0) {
            if (qlen >= quads.length) {
               this._quadBuffer = quads = growArrayBy(quads, quads.length);
            }

            quads[qlen++] = _padLastQuad(currQuad, currQuadBytes);
         }

         String name = this._symbols.findName(quads, qlen);
         if (name == null) {
            name = this.addName(quads, qlen, currQuadBytes);
         }

         return name;
      }
   }

   private final String findName(int q1, int lastQuadBytes) throws JsonParseException {
      q1 = _padLastQuad(q1, lastQuadBytes);
      String name = this._symbols.findName(q1);
      if (name != null) {
         return name;
      } else {
         this._quadBuffer[0] = q1;
         return this.addName(this._quadBuffer, 1, lastQuadBytes);
      }
   }

   private final String findName(int q1, int q2, int lastQuadBytes) throws JsonParseException {
      q2 = _padLastQuad(q2, lastQuadBytes);
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
      q3 = _padLastQuad(q3, lastQuadBytes);
      String name = this._symbols.findName(q1, q2, q3);
      if (name != null) {
         return name;
      } else {
         int[] quads = this._quadBuffer;
         quads[0] = q1;
         quads[1] = q2;
         quads[2] = _padLastQuad(q3, lastQuadBytes);
         return this.addName(quads, 3, lastQuadBytes);
      }
   }

   private final String findName(int[] quads, int qlen, int lastQuad, int lastQuadBytes) throws JsonParseException {
      if (qlen >= quads.length) {
         this._quadBuffer = quads = growArrayBy(quads, quads.length);
      }

      quads[qlen++] = _padLastQuad(lastQuad, lastQuadBytes);
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

   private static final int _padLastQuad(int q, int bytes) {
      return bytes == 4 ? q : q | -1 << (bytes << 3);
   }

   protected void _loadMoreGuaranteed() throws IOException {
      if (!this._loadMore()) {
         this._reportInvalidEOF();
      }

   }

   protected void _finishString() throws IOException {
      int ptr = this._inputPtr;
      if (ptr >= this._inputEnd) {
         this._loadMoreGuaranteed();
         ptr = this._inputPtr;
      }

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
               return;
            }
            break;
         }

         ++ptr;
      }

      this._inputPtr = ptr;
      this._finishString2(outBuf, outPtr);
   }

   protected String _finishAndReturnString() throws IOException {
      int ptr = this._inputPtr;
      if (ptr >= this._inputEnd) {
         this._loadMoreGuaranteed();
         ptr = this._inputPtr;
      }

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
               return this._textBuffer.setCurrentAndReturn(outPtr);
            }
            break;
         }

         ++ptr;
      }

      this._inputPtr = ptr;
      this._finishString2(outBuf, outPtr);
      return this._textBuffer.contentsAsString();
   }

   private final void _finishString2(char[] outBuf, int outPtr) throws IOException {
      int[] codes = _icUTF8;
      byte[] inputBuffer = this._inputBuffer;

      while(true) {
         label54:
         while(true) {
            int ptr = this._inputPtr;
            if (ptr >= this._inputEnd) {
               this._loadMoreGuaranteed();
               ptr = this._inputPtr;
            }

            if (outPtr >= outBuf.length) {
               outBuf = this._textBuffer.finishCurrentSegment();
               outPtr = 0;
            }

            int c;
            for(int max = Math.min(this._inputEnd, ptr + (outBuf.length - outPtr)); ptr < max; outBuf[outPtr++] = (char)c) {
               c = inputBuffer[ptr++] & 255;
               if (codes[c] != 0) {
                  this._inputPtr = ptr;
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
                     if (this._inputEnd - this._inputPtr >= 2) {
                        c = this._decodeUtf8_3fast(c);
                     } else {
                        c = this._decodeUtf8_3(c);
                     }
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
                     } else {
                        this._reportInvalidChar(c);
                     }
                  }

                  if (outPtr >= outBuf.length) {
                     outBuf = this._textBuffer.finishCurrentSegment();
                     outPtr = 0;
                  }

                  outBuf[outPtr++] = (char)c;
                  continue label54;
               }
            }

            this._inputPtr = ptr;
         }
      }
   }

   protected void _skipString() throws IOException {
      this._tokenIncomplete = false;
      int[] codes = _icUTF8;
      byte[] inputBuffer = this._inputBuffer;

      while(true) {
         label34:
         while(true) {
            int ptr = this._inputPtr;
            int max = this._inputEnd;
            if (ptr >= max) {
               this._loadMoreGuaranteed();
               ptr = this._inputPtr;
               max = this._inputEnd;
            }

            while(ptr < max) {
               int c = inputBuffer[ptr++] & 255;
               if (codes[c] != 0) {
                  this._inputPtr = ptr;
                  if (c == 34) {
                     return;
                  }

                  switch(codes[c]) {
                  case 1:
                     this._decodeEscaped();
                     continue label34;
                  case 2:
                     this._skipUtf8_2();
                     continue label34;
                  case 3:
                     this._skipUtf8_3();
                     continue label34;
                  case 4:
                     this._skipUtf8_4(c);
                     continue label34;
                  default:
                     if (c < 32) {
                        this._throwUnquotedSpace(c, "string value");
                     } else {
                        this._reportInvalidChar(c);
                     }
                     continue label34;
                  }
               }
            }

            this._inputPtr = ptr;
         }
      }
   }

   protected JsonToken _handleUnexpectedValue(int c) throws IOException {
      switch(c) {
      case 43:
         if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
            this._reportInvalidEOFInValue(JsonToken.VALUE_NUMBER_INT);
         }

         return this._handleInvalidNumberStart(this._inputBuffer[this._inputPtr++] & 255, false);
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
            --this._inputPtr;
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
         this._reportInvalidToken("" + (char)c, "('true', 'false' or 'null')");
      }

      this._reportUnexpectedChar(c, "expected a valid value (number, String, array, object, 'true', 'false' or 'null')");
      return null;
   }

   protected JsonToken _handleApos() throws IOException {
      int c = false;
      int outPtr = 0;
      char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
      int[] codes = _icUTF8;
      byte[] inputBuffer = this._inputBuffer;

      while(true) {
         while(true) {
            if (this._inputPtr >= this._inputEnd) {
               this._loadMoreGuaranteed();
            }

            if (outPtr >= outBuf.length) {
               outBuf = this._textBuffer.finishCurrentSegment();
               outPtr = 0;
            }

            int max = this._inputEnd;
            int max2 = this._inputPtr + (outBuf.length - outPtr);
            if (max2 < max) {
               max = max2;
            }

            while(this._inputPtr < max) {
               int c = inputBuffer[this._inputPtr++] & 255;
               if (c == 39 || codes[c] != 0) {
                  if (c == 39) {
                     this._textBuffer.setCurrentLength(outPtr);
                     return JsonToken.VALUE_STRING;
                  }

                  switch(codes[c]) {
                  case 1:
                     c = this._decodeEscaped();
                     break;
                  case 2:
                     c = this._decodeUtf8_2(c);
                     break;
                  case 3:
                     if (this._inputEnd - this._inputPtr >= 2) {
                        c = this._decodeUtf8_3fast(c);
                     } else {
                        c = this._decodeUtf8_3(c);
                     }
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
            }
         }
      }
   }

   protected JsonToken _handleInvalidNumberStart(int ch, boolean neg) throws IOException {
      while(ch == 73) {
         if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
            this._reportInvalidEOFInValue(JsonToken.VALUE_NUMBER_FLOAT);
         }

         ch = this._inputBuffer[this._inputPtr++];
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

         this._reportError("Non-standard token '%s': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow", match);
      }

      this.reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
      return null;
   }

   protected final void _matchTrue() throws IOException {
      int ptr = this._inputPtr;
      if (ptr + 3 < this._inputEnd) {
         byte[] buf = this._inputBuffer;
         if (buf[ptr++] == 114 && buf[ptr++] == 117 && buf[ptr++] == 101) {
            int ch = buf[ptr] & 255;
            if (ch < 48 || ch == 93 || ch == 125) {
               this._inputPtr = ptr;
               return;
            }
         }
      }

      this._matchToken2("true", 1);
   }

   protected final void _matchFalse() throws IOException {
      int ptr = this._inputPtr;
      if (ptr + 4 < this._inputEnd) {
         byte[] buf = this._inputBuffer;
         if (buf[ptr++] == 97 && buf[ptr++] == 108 && buf[ptr++] == 115 && buf[ptr++] == 101) {
            int ch = buf[ptr] & 255;
            if (ch < 48 || ch == 93 || ch == 125) {
               this._inputPtr = ptr;
               return;
            }
         }
      }

      this._matchToken2("false", 1);
   }

   protected final void _matchNull() throws IOException {
      int ptr = this._inputPtr;
      if (ptr + 3 < this._inputEnd) {
         byte[] buf = this._inputBuffer;
         if (buf[ptr++] == 117 && buf[ptr++] == 108 && buf[ptr++] == 108) {
            int ch = buf[ptr] & 255;
            if (ch < 48 || ch == 93 || ch == 125) {
               this._inputPtr = ptr;
               return;
            }
         }
      }

      this._matchToken2("null", 1);
   }

   protected final void _matchToken(String matchStr, int i) throws IOException {
      int len = matchStr.length();
      if (this._inputPtr + len >= this._inputEnd) {
         this._matchToken2(matchStr, i);
      } else {
         do {
            if (this._inputBuffer[this._inputPtr] != matchStr.charAt(i)) {
               this._reportInvalidToken(matchStr.substring(0, i));
            }

            ++this._inputPtr;
            ++i;
         } while(i < len);

         int ch = this._inputBuffer[this._inputPtr] & 255;
         if (ch >= 48 && ch != 93 && ch != 125) {
            this._checkMatchEnd(matchStr, i, ch);
         }

      }
   }

   private final void _matchToken2(String matchStr, int i) throws IOException {
      int len = matchStr.length();

      do {
         if (this._inputPtr >= this._inputEnd && !this._loadMore() || this._inputBuffer[this._inputPtr] != matchStr.charAt(i)) {
            this._reportInvalidToken(matchStr.substring(0, i));
         }

         ++this._inputPtr;
         ++i;
      } while(i < len);

      if (this._inputPtr < this._inputEnd || this._loadMore()) {
         int ch = this._inputBuffer[this._inputPtr] & 255;
         if (ch >= 48 && ch != 93 && ch != 125) {
            this._checkMatchEnd(matchStr, i, ch);
         }

      }
   }

   private final void _checkMatchEnd(String matchStr, int i, int ch) throws IOException {
      char c = (char)this._decodeCharForError(ch);
      if (Character.isJavaIdentifierPart(c)) {
         this._reportInvalidToken(matchStr.substring(0, i));
      }

   }

   private final int _skipWS() throws IOException {
      while(this._inputPtr < this._inputEnd) {
         int i = this._inputBuffer[this._inputPtr++] & 255;
         if (i > 32) {
            if (i != 47 && i != 35) {
               return i;
            }

            --this._inputPtr;
            return this._skipWS2();
         }

         if (i != 32) {
            if (i == 10) {
               ++this._currInputRow;
               this._currInputRowStart = this._inputPtr;
            } else if (i == 13) {
               this._skipCR();
            } else if (i != 9) {
               this._throwInvalidSpace(i);
            }
         }
      }

      return this._skipWS2();
   }

   private final int _skipWS2() throws IOException {
      while(this._inputPtr < this._inputEnd || this._loadMore()) {
         int i = this._inputBuffer[this._inputPtr++] & 255;
         if (i > 32) {
            if (i == 47) {
               this._skipComment();
            } else if (i != 35 || !this._skipYAMLComment()) {
               return i;
            }
         } else if (i != 32) {
            if (i == 10) {
               ++this._currInputRow;
               this._currInputRowStart = this._inputPtr;
            } else if (i == 13) {
               this._skipCR();
            } else if (i != 9) {
               this._throwInvalidSpace(i);
            }
         }
      }

      throw this._constructError("Unexpected end-of-input within/between " + this._parsingContext.typeDesc() + " entries");
   }

   private final int _skipWSOrEnd() throws IOException {
      if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
         return this._eofAsNextChar();
      } else {
         int i = this._inputBuffer[this._inputPtr++] & 255;
         if (i > 32) {
            if (i != 47 && i != 35) {
               return i;
            } else {
               --this._inputPtr;
               return this._skipWSOrEnd2();
            }
         } else {
            if (i != 32) {
               if (i == 10) {
                  ++this._currInputRow;
                  this._currInputRowStart = this._inputPtr;
               } else if (i == 13) {
                  this._skipCR();
               } else if (i != 9) {
                  this._throwInvalidSpace(i);
               }
            }

            while(this._inputPtr < this._inputEnd) {
               i = this._inputBuffer[this._inputPtr++] & 255;
               if (i > 32) {
                  if (i != 47 && i != 35) {
                     return i;
                  }

                  --this._inputPtr;
                  return this._skipWSOrEnd2();
               }

               if (i != 32) {
                  if (i == 10) {
                     ++this._currInputRow;
                     this._currInputRowStart = this._inputPtr;
                  } else if (i == 13) {
                     this._skipCR();
                  } else if (i != 9) {
                     this._throwInvalidSpace(i);
                  }
               }
            }

            return this._skipWSOrEnd2();
         }
      }
   }

   private final int _skipWSOrEnd2() throws IOException {
      while(this._inputPtr < this._inputEnd || this._loadMore()) {
         int i = this._inputBuffer[this._inputPtr++] & 255;
         if (i > 32) {
            if (i == 47) {
               this._skipComment();
            } else if (i != 35 || !this._skipYAMLComment()) {
               return i;
            }
         } else if (i != 32) {
            if (i == 10) {
               ++this._currInputRow;
               this._currInputRowStart = this._inputPtr;
            } else if (i == 13) {
               this._skipCR();
            } else if (i != 9) {
               this._throwInvalidSpace(i);
            }
         }
      }

      return this._eofAsNextChar();
   }

   private final int _skipColon() throws IOException {
      if (this._inputPtr + 4 >= this._inputEnd) {
         return this._skipColon2(false);
      } else {
         int i = this._inputBuffer[this._inputPtr];
         if (i == 58) {
            i = this._inputBuffer[++this._inputPtr];
            if (i > 32) {
               if (i != 47 && i != 35) {
                  ++this._inputPtr;
                  return i;
               } else {
                  return this._skipColon2(true);
               }
            } else {
               if (i == 32 || i == 9) {
                  i = this._inputBuffer[++this._inputPtr];
                  if (i > 32) {
                     if (i != 47 && i != 35) {
                        ++this._inputPtr;
                        return i;
                     }

                     return this._skipColon2(true);
                  }
               }

               return this._skipColon2(true);
            }
         } else {
            if (i == 32 || i == 9) {
               i = this._inputBuffer[++this._inputPtr];
            }

            if (i == 58) {
               i = this._inputBuffer[++this._inputPtr];
               if (i > 32) {
                  if (i != 47 && i != 35) {
                     ++this._inputPtr;
                     return i;
                  } else {
                     return this._skipColon2(true);
                  }
               } else {
                  if (i == 32 || i == 9) {
                     i = this._inputBuffer[++this._inputPtr];
                     if (i > 32) {
                        if (i != 47 && i != 35) {
                           ++this._inputPtr;
                           return i;
                        }

                        return this._skipColon2(true);
                     }
                  }

                  return this._skipColon2(true);
               }
            } else {
               return this._skipColon2(false);
            }
         }
      }
   }

   private final int _skipColon2(boolean gotColon) throws IOException {
      while(this._inputPtr < this._inputEnd || this._loadMore()) {
         int i = this._inputBuffer[this._inputPtr++] & 255;
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
         } else if (i != 32) {
            if (i == 10) {
               ++this._currInputRow;
               this._currInputRowStart = this._inputPtr;
            } else if (i == 13) {
               this._skipCR();
            } else if (i != 9) {
               this._throwInvalidSpace(i);
            }
         }
      }

      this._reportInvalidEOF(" within/between " + this._parsingContext.typeDesc() + " entries", (JsonToken)null);
      return -1;
   }

   private final void _skipComment() throws IOException {
      if (!this.isEnabled(JsonParser.Feature.ALLOW_COMMENTS)) {
         this._reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
      }

      if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
         this._reportInvalidEOF(" in a comment", (JsonToken)null);
      }

      int c = this._inputBuffer[this._inputPtr++] & 255;
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

      label33:
      while(this._inputPtr < this._inputEnd || this._loadMore()) {
         int i = this._inputBuffer[this._inputPtr++] & 255;
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
               this._skipUtf8_4(i);
               break;
            case 10:
               ++this._currInputRow;
               this._currInputRowStart = this._inputPtr;
               break;
            case 13:
               this._skipCR();
               break;
            case 42:
               if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
                  break label33;
               }

               if (this._inputBuffer[this._inputPtr] == 47) {
                  ++this._inputPtr;
                  return;
               }
               break;
            default:
               this._reportInvalidChar(i);
            }
         }
      }

      this._reportInvalidEOF(" in a comment", (JsonToken)null);
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

      while(this._inputPtr < this._inputEnd || this._loadMore()) {
         int i = this._inputBuffer[this._inputPtr++] & 255;
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
               this._skipUtf8_4(i);
               break;
            case 10:
               ++this._currInputRow;
               this._currInputRowStart = this._inputPtr;
               return;
            case 13:
               this._skipCR();
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

   }

   protected char _decodeEscaped() throws IOException {
      if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
         this._reportInvalidEOF(" in character escape sequence", JsonToken.VALUE_STRING);
      }

      int c = this._inputBuffer[this._inputPtr++];
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
            if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
               this._reportInvalidEOF(" in character escape sequence", JsonToken.VALUE_STRING);
            }

            int ch = this._inputBuffer[this._inputPtr++];
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

         int d = this.nextByte();
         if ((d & 192) != 128) {
            this._reportInvalidOther(d & 255);
         }

         c = c << 6 | d & 63;
         if (needed > 1) {
            d = this.nextByte();
            if ((d & 192) != 128) {
               this._reportInvalidOther(d & 255);
            }

            c = c << 6 | d & 63;
            if (needed > 2) {
               d = this.nextByte();
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
      if (this._inputPtr >= this._inputEnd) {
         this._loadMoreGuaranteed();
      }

      int d = this._inputBuffer[this._inputPtr++];
      if ((d & 192) != 128) {
         this._reportInvalidOther(d & 255, this._inputPtr);
      }

      return (c & 31) << 6 | d & 63;
   }

   private final int _decodeUtf8_3(int c1) throws IOException {
      if (this._inputPtr >= this._inputEnd) {
         this._loadMoreGuaranteed();
      }

      c1 &= 15;
      int d = this._inputBuffer[this._inputPtr++];
      if ((d & 192) != 128) {
         this._reportInvalidOther(d & 255, this._inputPtr);
      }

      int c = c1 << 6 | d & 63;
      if (this._inputPtr >= this._inputEnd) {
         this._loadMoreGuaranteed();
      }

      d = this._inputBuffer[this._inputPtr++];
      if ((d & 192) != 128) {
         this._reportInvalidOther(d & 255, this._inputPtr);
      }

      c = c << 6 | d & 63;
      return c;
   }

   private final int _decodeUtf8_3fast(int c1) throws IOException {
      c1 &= 15;
      int d = this._inputBuffer[this._inputPtr++];
      if ((d & 192) != 128) {
         this._reportInvalidOther(d & 255, this._inputPtr);
      }

      int c = c1 << 6 | d & 63;
      d = this._inputBuffer[this._inputPtr++];
      if ((d & 192) != 128) {
         this._reportInvalidOther(d & 255, this._inputPtr);
      }

      c = c << 6 | d & 63;
      return c;
   }

   private final int _decodeUtf8_4(int c) throws IOException {
      if (this._inputPtr >= this._inputEnd) {
         this._loadMoreGuaranteed();
      }

      int d = this._inputBuffer[this._inputPtr++];
      if ((d & 192) != 128) {
         this._reportInvalidOther(d & 255, this._inputPtr);
      }

      c = (c & 7) << 6 | d & 63;
      if (this._inputPtr >= this._inputEnd) {
         this._loadMoreGuaranteed();
      }

      d = this._inputBuffer[this._inputPtr++];
      if ((d & 192) != 128) {
         this._reportInvalidOther(d & 255, this._inputPtr);
      }

      c = c << 6 | d & 63;
      if (this._inputPtr >= this._inputEnd) {
         this._loadMoreGuaranteed();
      }

      d = this._inputBuffer[this._inputPtr++];
      if ((d & 192) != 128) {
         this._reportInvalidOther(d & 255, this._inputPtr);
      }

      return (c << 6 | d & 63) - 65536;
   }

   private final void _skipUtf8_2() throws IOException {
      if (this._inputPtr >= this._inputEnd) {
         this._loadMoreGuaranteed();
      }

      int c = this._inputBuffer[this._inputPtr++];
      if ((c & 192) != 128) {
         this._reportInvalidOther(c & 255, this._inputPtr);
      }

   }

   private final void _skipUtf8_3() throws IOException {
      if (this._inputPtr >= this._inputEnd) {
         this._loadMoreGuaranteed();
      }

      int c = this._inputBuffer[this._inputPtr++];
      if ((c & 192) != 128) {
         this._reportInvalidOther(c & 255, this._inputPtr);
      }

      if (this._inputPtr >= this._inputEnd) {
         this._loadMoreGuaranteed();
      }

      c = this._inputBuffer[this._inputPtr++];
      if ((c & 192) != 128) {
         this._reportInvalidOther(c & 255, this._inputPtr);
      }

   }

   private final void _skipUtf8_4(int c) throws IOException {
      if (this._inputPtr >= this._inputEnd) {
         this._loadMoreGuaranteed();
      }

      int d = this._inputBuffer[this._inputPtr++];
      if ((d & 192) != 128) {
         this._reportInvalidOther(d & 255, this._inputPtr);
      }

      if (this._inputPtr >= this._inputEnd) {
         this._loadMoreGuaranteed();
      }

      d = this._inputBuffer[this._inputPtr++];
      if ((d & 192) != 128) {
         this._reportInvalidOther(d & 255, this._inputPtr);
      }

      if (this._inputPtr >= this._inputEnd) {
         this._loadMoreGuaranteed();
      }

      d = this._inputBuffer[this._inputPtr++];
      if ((d & 192) != 128) {
         this._reportInvalidOther(d & 255, this._inputPtr);
      }

   }

   protected final void _skipCR() throws IOException {
      if ((this._inputPtr < this._inputEnd || this._loadMore()) && this._inputBuffer[this._inputPtr] == 10) {
         ++this._inputPtr;
      }

      ++this._currInputRow;
      this._currInputRowStart = this._inputPtr;
   }

   private int nextByte() throws IOException {
      if (this._inputPtr >= this._inputEnd) {
         this._loadMoreGuaranteed();
      }

      return this._inputBuffer[this._inputPtr++] & 255;
   }

   protected void _reportInvalidToken(String matchedPart, int ptr) throws IOException {
      this._inputPtr = ptr;
      this._reportInvalidToken(matchedPart, "'null', 'true', 'false' or NaN");
   }

   protected void _reportInvalidToken(String matchedPart) throws IOException {
      this._reportInvalidToken(matchedPart, "'null', 'true', 'false' or NaN");
   }

   protected void _reportInvalidToken(String matchedPart, String msg) throws IOException {
      StringBuilder sb = new StringBuilder(matchedPart);

      while(this._inputPtr < this._inputEnd || this._loadMore()) {
         int i = this._inputBuffer[this._inputPtr++];
         char c = (char)this._decodeCharForError(i);
         if (!Character.isJavaIdentifierPart(c)) {
            break;
         }

         sb.append(c);
         if (sb.length() >= 256) {
            sb.append("...");
            break;
         }
      }

      this._reportError("Unrecognized token '%s': was expecting %s", sb, msg);
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

   protected void _reportInvalidOther(int mask) throws JsonParseException {
      this._reportError("Invalid UTF-8 middle byte 0x" + Integer.toHexString(mask));
   }

   protected void _reportInvalidOther(int mask, int ptr) throws JsonParseException {
      this._inputPtr = ptr;
      this._reportInvalidOther(mask);
   }

   protected final byte[] _decodeBase64(Base64Variant b64variant) throws IOException {
      ByteArrayBuilder builder = this._getByteArrayBuilder();

      while(true) {
         while(true) {
            int ch;
            int bits;
            do {
               do {
                  if (this._inputPtr >= this._inputEnd) {
                     this._loadMoreGuaranteed();
                  }

                  ch = this._inputBuffer[this._inputPtr++] & 255;
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
            if (this._inputPtr >= this._inputEnd) {
               this._loadMoreGuaranteed();
            }

            ch = this._inputBuffer[this._inputPtr++] & 255;
            bits = b64variant.decodeBase64Char(ch);
            if (bits < 0) {
               bits = this._decodeBase64Escape(b64variant, ch, 1);
            }

            decodedData = decodedData << 6 | bits;
            if (this._inputPtr >= this._inputEnd) {
               this._loadMoreGuaranteed();
            }

            ch = this._inputBuffer[this._inputPtr++] & 255;
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
                  if (this._inputPtr >= this._inputEnd) {
                     this._loadMoreGuaranteed();
                  }

                  ch = this._inputBuffer[this._inputPtr++] & 255;
                  if (!b64variant.usesPaddingChar(ch)) {
                     throw this.reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
                  }

                  decodedData >>= 4;
                  builder.append(decodedData);
                  continue;
               }
            }

            decodedData = decodedData << 6 | bits;
            if (this._inputPtr >= this._inputEnd) {
               this._loadMoreGuaranteed();
            }

            ch = this._inputBuffer[this._inputPtr++] & 255;
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
      if (this._currToken == JsonToken.FIELD_NAME) {
         long total = this._currInputProcessed + (long)(this._nameStartOffset - 1);
         return new JsonLocation(this._getSourceReference(), total, -1L, this._nameStartRow, this._nameStartCol);
      } else {
         return new JsonLocation(this._getSourceReference(), this._tokenInputTotal - 1L, -1L, this._tokenInputRow, this._tokenInputCol);
      }
   }

   public JsonLocation getCurrentLocation() {
      int col = this._inputPtr - this._currInputRowStart + 1;
      return new JsonLocation(this._getSourceReference(), this._currInputProcessed + (long)this._inputPtr, -1L, this._currInputRow, col);
   }

   private final void _updateLocation() {
      this._tokenInputRow = this._currInputRow;
      int ptr = this._inputPtr;
      this._tokenInputTotal = this._currInputProcessed + (long)ptr;
      this._tokenInputCol = ptr - this._currInputRowStart;
   }

   private final void _updateNameLocation() {
      this._nameStartRow = this._currInputRow;
      int ptr = this._inputPtr;
      this._nameStartOffset = ptr;
      this._nameStartCol = ptr - this._currInputRowStart;
   }

   private final JsonToken _closeScope(int i) throws JsonParseException {
      if (i == 125) {
         this._closeObjectScope();
         return this._currToken = JsonToken.END_OBJECT;
      } else {
         this._closeArrayScope();
         return this._currToken = JsonToken.END_ARRAY;
      }
   }

   private final void _closeArrayScope() throws JsonParseException {
      this._updateLocation();
      if (!this._parsingContext.inArray()) {
         this._reportMismatchedEndMarker(93, '}');
      }

      this._parsingContext = this._parsingContext.clearAndGetParent();
   }

   private final void _closeObjectScope() throws JsonParseException {
      this._updateLocation();
      if (!this._parsingContext.inObject()) {
         this._reportMismatchedEndMarker(125, ']');
      }

      this._parsingContext = this._parsingContext.clearAndGetParent();
   }

   static {
      FEAT_MASK_TRAILING_COMMA = JsonParser.Feature.ALLOW_TRAILING_COMMA.getMask();
   }
}

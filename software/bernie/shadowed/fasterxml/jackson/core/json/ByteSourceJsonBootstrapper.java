package software.bernie.shadowed.fasterxml.jackson.core.json;

import java.io.ByteArrayInputStream;
import java.io.CharConversionException;
import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import software.bernie.shadowed.fasterxml.jackson.core.JsonEncoding;
import software.bernie.shadowed.fasterxml.jackson.core.JsonFactory;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.ObjectCodec;
import software.bernie.shadowed.fasterxml.jackson.core.format.InputAccessor;
import software.bernie.shadowed.fasterxml.jackson.core.format.MatchStrength;
import software.bernie.shadowed.fasterxml.jackson.core.io.IOContext;
import software.bernie.shadowed.fasterxml.jackson.core.io.MergedStream;
import software.bernie.shadowed.fasterxml.jackson.core.io.UTF32Reader;
import software.bernie.shadowed.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
import software.bernie.shadowed.fasterxml.jackson.core.sym.CharsToNameCanonicalizer;

public final class ByteSourceJsonBootstrapper {
   public static final byte UTF8_BOM_1 = -17;
   public static final byte UTF8_BOM_2 = -69;
   public static final byte UTF8_BOM_3 = -65;
   private final IOContext _context;
   private final InputStream _in;
   private final byte[] _inputBuffer;
   private int _inputPtr;
   private int _inputEnd;
   private final boolean _bufferRecyclable;
   private boolean _bigEndian = true;
   private int _bytesPerChar;

   public ByteSourceJsonBootstrapper(IOContext ctxt, InputStream in) {
      this._context = ctxt;
      this._in = in;
      this._inputBuffer = ctxt.allocReadIOBuffer();
      this._inputEnd = this._inputPtr = 0;
      this._bufferRecyclable = true;
   }

   public ByteSourceJsonBootstrapper(IOContext ctxt, byte[] inputBuffer, int inputStart, int inputLen) {
      this._context = ctxt;
      this._in = null;
      this._inputBuffer = inputBuffer;
      this._inputPtr = inputStart;
      this._inputEnd = inputStart + inputLen;
      this._bufferRecyclable = false;
   }

   public JsonEncoding detectEncoding() throws IOException {
      boolean foundEncoding = false;
      int quad;
      if (this.ensureLoaded(4)) {
         quad = this._inputBuffer[this._inputPtr] << 24 | (this._inputBuffer[this._inputPtr + 1] & 255) << 16 | (this._inputBuffer[this._inputPtr + 2] & 255) << 8 | this._inputBuffer[this._inputPtr + 3] & 255;
         if (this.handleBOM(quad)) {
            foundEncoding = true;
         } else if (this.checkUTF32(quad)) {
            foundEncoding = true;
         } else if (this.checkUTF16(quad >>> 16)) {
            foundEncoding = true;
         }
      } else if (this.ensureLoaded(2)) {
         quad = (this._inputBuffer[this._inputPtr] & 255) << 8 | this._inputBuffer[this._inputPtr + 1] & 255;
         if (this.checkUTF16(quad)) {
            foundEncoding = true;
         }
      }

      JsonEncoding enc;
      if (!foundEncoding) {
         enc = JsonEncoding.UTF8;
      } else {
         switch(this._bytesPerChar) {
         case 1:
            enc = JsonEncoding.UTF8;
            break;
         case 2:
            enc = this._bigEndian ? JsonEncoding.UTF16_BE : JsonEncoding.UTF16_LE;
            break;
         case 3:
         default:
            throw new RuntimeException("Internal error");
         case 4:
            enc = this._bigEndian ? JsonEncoding.UTF32_BE : JsonEncoding.UTF32_LE;
         }
      }

      this._context.setEncoding(enc);
      return enc;
   }

   public static int skipUTF8BOM(DataInput input) throws IOException {
      int b = input.readUnsignedByte();
      if (b != 239) {
         return b;
      } else {
         b = input.readUnsignedByte();
         if (b != 187) {
            throw new IOException("Unexpected byte 0x" + Integer.toHexString(b) + " following 0xEF; should get 0xBB as part of UTF-8 BOM");
         } else {
            b = input.readUnsignedByte();
            if (b != 191) {
               throw new IOException("Unexpected byte 0x" + Integer.toHexString(b) + " following 0xEF 0xBB; should get 0xBF as part of UTF-8 BOM");
            } else {
               return input.readUnsignedByte();
            }
         }
      }
   }

   public Reader constructReader() throws IOException {
      JsonEncoding enc = this._context.getEncoding();
      switch(enc.bits()) {
      case 8:
      case 16:
         InputStream in = this._in;
         if (in == null) {
            in = new ByteArrayInputStream(this._inputBuffer, this._inputPtr, this._inputEnd);
         } else if (this._inputPtr < this._inputEnd) {
            in = new MergedStream(this._context, (InputStream)in, this._inputBuffer, this._inputPtr, this._inputEnd);
         }

         return new InputStreamReader((InputStream)in, enc.getJavaName());
      case 32:
         return new UTF32Reader(this._context, this._in, this._inputBuffer, this._inputPtr, this._inputEnd, this._context.getEncoding().isBigEndian());
      default:
         throw new RuntimeException("Internal error");
      }
   }

   public JsonParser constructParser(int parserFeatures, ObjectCodec codec, ByteQuadsCanonicalizer rootByteSymbols, CharsToNameCanonicalizer rootCharSymbols, int factoryFeatures) throws IOException {
      JsonEncoding enc = this.detectEncoding();
      if (enc == JsonEncoding.UTF8 && JsonFactory.Feature.CANONICALIZE_FIELD_NAMES.enabledIn(factoryFeatures)) {
         ByteQuadsCanonicalizer can = rootByteSymbols.makeChild(factoryFeatures);
         return new UTF8StreamJsonParser(this._context, parserFeatures, this._in, codec, can, this._inputBuffer, this._inputPtr, this._inputEnd, this._bufferRecyclable);
      } else {
         return new ReaderBasedJsonParser(this._context, parserFeatures, this.constructReader(), codec, rootCharSymbols.makeChild(factoryFeatures));
      }
   }

   public static MatchStrength hasJSONFormat(InputAccessor acc) throws IOException {
      if (!acc.hasMoreBytes()) {
         return MatchStrength.INCONCLUSIVE;
      } else {
         byte b = acc.nextByte();
         if (b == -17) {
            if (!acc.hasMoreBytes()) {
               return MatchStrength.INCONCLUSIVE;
            }

            if (acc.nextByte() != -69) {
               return MatchStrength.NO_MATCH;
            }

            if (!acc.hasMoreBytes()) {
               return MatchStrength.INCONCLUSIVE;
            }

            if (acc.nextByte() != -65) {
               return MatchStrength.NO_MATCH;
            }

            if (!acc.hasMoreBytes()) {
               return MatchStrength.INCONCLUSIVE;
            }

            b = acc.nextByte();
         }

         int ch = skipSpace(acc, b);
         if (ch < 0) {
            return MatchStrength.INCONCLUSIVE;
         } else if (ch == 123) {
            ch = skipSpace(acc);
            if (ch < 0) {
               return MatchStrength.INCONCLUSIVE;
            } else {
               return ch != 34 && ch != 125 ? MatchStrength.NO_MATCH : MatchStrength.SOLID_MATCH;
            }
         } else if (ch == 91) {
            ch = skipSpace(acc);
            if (ch < 0) {
               return MatchStrength.INCONCLUSIVE;
            } else {
               return ch != 93 && ch != 91 ? MatchStrength.SOLID_MATCH : MatchStrength.SOLID_MATCH;
            }
         } else {
            MatchStrength strength = MatchStrength.WEAK_MATCH;
            if (ch == 34) {
               return strength;
            } else if (ch <= 57 && ch >= 48) {
               return strength;
            } else if (ch == 45) {
               ch = skipSpace(acc);
               if (ch < 0) {
                  return MatchStrength.INCONCLUSIVE;
               } else {
                  return ch <= 57 && ch >= 48 ? strength : MatchStrength.NO_MATCH;
               }
            } else if (ch == 110) {
               return tryMatch(acc, "ull", strength);
            } else if (ch == 116) {
               return tryMatch(acc, "rue", strength);
            } else {
               return ch == 102 ? tryMatch(acc, "alse", strength) : MatchStrength.NO_MATCH;
            }
         }
      }
   }

   private static MatchStrength tryMatch(InputAccessor acc, String matchStr, MatchStrength fullMatchStrength) throws IOException {
      int i = 0;

      for(int len = matchStr.length(); i < len; ++i) {
         if (!acc.hasMoreBytes()) {
            return MatchStrength.INCONCLUSIVE;
         }

         if (acc.nextByte() != matchStr.charAt(i)) {
            return MatchStrength.NO_MATCH;
         }
      }

      return fullMatchStrength;
   }

   private static int skipSpace(InputAccessor acc) throws IOException {
      return !acc.hasMoreBytes() ? -1 : skipSpace(acc, acc.nextByte());
   }

   private static int skipSpace(InputAccessor acc, byte b) throws IOException {
      while(true) {
         int ch = b & 255;
         if (ch != 32 && ch != 13 && ch != 10 && ch != 9) {
            return ch;
         }

         if (!acc.hasMoreBytes()) {
            return -1;
         }

         b = acc.nextByte();
      }
   }

   private boolean handleBOM(int quad) throws IOException {
      switch(quad) {
      case -16842752:
         this.reportWeirdUCS4("3412");
         break;
      case -131072:
         this._inputPtr += 4;
         this._bytesPerChar = 4;
         this._bigEndian = false;
         return true;
      case 65279:
         this._bigEndian = true;
         this._inputPtr += 4;
         this._bytesPerChar = 4;
         return true;
      case 65534:
         this.reportWeirdUCS4("2143");
      }

      int msw = quad >>> 16;
      if (msw == 65279) {
         this._inputPtr += 2;
         this._bytesPerChar = 2;
         this._bigEndian = true;
         return true;
      } else if (msw == 65534) {
         this._inputPtr += 2;
         this._bytesPerChar = 2;
         this._bigEndian = false;
         return true;
      } else if (quad >>> 8 == 15711167) {
         this._inputPtr += 3;
         this._bytesPerChar = 1;
         this._bigEndian = true;
         return true;
      } else {
         return false;
      }
   }

   private boolean checkUTF32(int quad) throws IOException {
      if (quad >> 8 == 0) {
         this._bigEndian = true;
      } else if ((quad & 16777215) == 0) {
         this._bigEndian = false;
      } else if ((quad & -16711681) == 0) {
         this.reportWeirdUCS4("3412");
      } else {
         if ((quad & -65281) != 0) {
            return false;
         }

         this.reportWeirdUCS4("2143");
      }

      this._bytesPerChar = 4;
      return true;
   }

   private boolean checkUTF16(int i16) {
      if ((i16 & '\uff00') == 0) {
         this._bigEndian = true;
      } else {
         if ((i16 & 255) != 0) {
            return false;
         }

         this._bigEndian = false;
      }

      this._bytesPerChar = 2;
      return true;
   }

   private void reportWeirdUCS4(String type) throws IOException {
      throw new CharConversionException("Unsupported UCS-4 endianness (" + type + ") detected");
   }

   protected boolean ensureLoaded(int minimum) throws IOException {
      int count;
      for(int gotten = this._inputEnd - this._inputPtr; gotten < minimum; gotten += count) {
         if (this._in == null) {
            count = -1;
         } else {
            count = this._in.read(this._inputBuffer, this._inputEnd, this._inputBuffer.length - this._inputEnd);
         }

         if (count < 1) {
            return false;
         }

         this._inputEnd += count;
      }

      return true;
   }
}

package software.bernie.shadowed.fasterxml.jackson.core.io;

import java.lang.ref.SoftReference;
import software.bernie.shadowed.fasterxml.jackson.core.util.BufferRecycler;
import software.bernie.shadowed.fasterxml.jackson.core.util.ByteArrayBuilder;
import software.bernie.shadowed.fasterxml.jackson.core.util.TextBuffer;

public final class JsonStringEncoder {
   private static final char[] HC = CharTypes.copyHexChars();
   private static final byte[] HB = CharTypes.copyHexBytes();
   private static final int SURR1_FIRST = 55296;
   private static final int SURR1_LAST = 56319;
   private static final int SURR2_FIRST = 56320;
   private static final int SURR2_LAST = 57343;
   protected static final ThreadLocal<SoftReference<JsonStringEncoder>> _threadEncoder = new ThreadLocal();
   protected TextBuffer _text;
   protected ByteArrayBuilder _bytes;
   protected final char[] _qbuf = new char[6];

   public JsonStringEncoder() {
      this._qbuf[0] = '\\';
      this._qbuf[2] = '0';
      this._qbuf[3] = '0';
   }

   public static JsonStringEncoder getInstance() {
      SoftReference<JsonStringEncoder> ref = (SoftReference)_threadEncoder.get();
      JsonStringEncoder enc = ref == null ? null : (JsonStringEncoder)ref.get();
      if (enc == null) {
         enc = new JsonStringEncoder();
         _threadEncoder.set(new SoftReference(enc));
      }

      return enc;
   }

   public char[] quoteAsString(String input) {
      TextBuffer textBuffer = this._text;
      if (textBuffer == null) {
         this._text = textBuffer = new TextBuffer((BufferRecycler)null);
      }

      char[] outputBuffer = textBuffer.emptyAndGetCurrentSegment();
      int[] escCodes = CharTypes.get7BitOutputEscapes();
      int escCodeCount = escCodes.length;
      int inPtr = 0;
      int inputLen = input.length();
      int outPtr = 0;

      label45:
      while(inPtr < inputLen) {
         while(true) {
            char c = input.charAt(inPtr);
            if (c < escCodeCount && escCodes[c] != 0) {
               c = input.charAt(inPtr++);
               int escCode = escCodes[c];
               int length = escCode < 0 ? this._appendNumeric(c, this._qbuf) : this._appendNamed(escCode, this._qbuf);
               if (outPtr + length > outputBuffer.length) {
                  int first = outputBuffer.length - outPtr;
                  if (first > 0) {
                     System.arraycopy(this._qbuf, 0, outputBuffer, outPtr, first);
                  }

                  outputBuffer = textBuffer.finishCurrentSegment();
                  int second = length - first;
                  System.arraycopy(this._qbuf, first, outputBuffer, 0, second);
                  outPtr = second;
               } else {
                  System.arraycopy(this._qbuf, 0, outputBuffer, outPtr, length);
                  outPtr += length;
               }
               break;
            }

            if (outPtr >= outputBuffer.length) {
               outputBuffer = textBuffer.finishCurrentSegment();
               outPtr = 0;
            }

            outputBuffer[outPtr++] = c;
            ++inPtr;
            if (inPtr >= inputLen) {
               break label45;
            }
         }
      }

      textBuffer.setCurrentLength(outPtr);
      return textBuffer.contentsAsArray();
   }

   public void quoteAsString(CharSequence input, StringBuilder output) {
      int[] escCodes = CharTypes.get7BitOutputEscapes();
      int escCodeCount = escCodes.length;
      int inPtr = 0;
      int inputLen = input.length();

      while(inPtr < inputLen) {
         while(true) {
            char c = input.charAt(inPtr);
            if (c < escCodeCount && escCodes[c] != 0) {
               c = input.charAt(inPtr++);
               int escCode = escCodes[c];
               int length = escCode < 0 ? this._appendNumeric(c, this._qbuf) : this._appendNamed(escCode, this._qbuf);
               output.append(this._qbuf, 0, length);
            } else {
               output.append(c);
               ++inPtr;
               if (inPtr >= inputLen) {
                  return;
               }
            }
         }
      }

   }

   public byte[] quoteAsUTF8(String text) {
      ByteArrayBuilder bb = this._bytes;
      if (bb == null) {
         this._bytes = bb = new ByteArrayBuilder((BufferRecycler)null);
      }

      int inputPtr = 0;
      int inputEnd = text.length();
      int outputPtr = 0;
      byte[] outputBuffer = bb.resetAndGetFirstSegment();

      label83:
      while(inputPtr < inputEnd) {
         int[] escCodes = CharTypes.get7BitOutputEscapes();

         do {
            int ch = text.charAt(inputPtr);
            if (ch > 127 || escCodes[ch] != 0) {
               if (outputPtr >= outputBuffer.length) {
                  outputBuffer = bb.finishCurrentSegment();
                  outputPtr = 0;
               }

               ch = text.charAt(inputPtr++);
               if (ch <= 127) {
                  int escape = escCodes[ch];
                  outputPtr = this._appendByte(ch, escape, bb, outputPtr);
                  outputBuffer = bb.getCurrentSegment();
               } else {
                  int ch;
                  if (ch <= 2047) {
                     outputBuffer[outputPtr++] = (byte)(192 | ch >> 6);
                     ch = 128 | ch & 63;
                  } else if (ch >= '\ud800' && ch <= '\udfff') {
                     if (ch > '\udbff') {
                        _illegal(ch);
                     }

                     if (inputPtr >= inputEnd) {
                        _illegal(ch);
                     }

                     ch = _convert(ch, text.charAt(inputPtr++));
                     if (ch > 1114111) {
                        _illegal(ch);
                     }

                     outputBuffer[outputPtr++] = (byte)(240 | ch >> 18);
                     if (outputPtr >= outputBuffer.length) {
                        outputBuffer = bb.finishCurrentSegment();
                        outputPtr = 0;
                     }

                     outputBuffer[outputPtr++] = (byte)(128 | ch >> 12 & 63);
                     if (outputPtr >= outputBuffer.length) {
                        outputBuffer = bb.finishCurrentSegment();
                        outputPtr = 0;
                     }

                     outputBuffer[outputPtr++] = (byte)(128 | ch >> 6 & 63);
                     ch = 128 | ch & 63;
                  } else {
                     outputBuffer[outputPtr++] = (byte)(224 | ch >> 12);
                     if (outputPtr >= outputBuffer.length) {
                        outputBuffer = bb.finishCurrentSegment();
                        outputPtr = 0;
                     }

                     outputBuffer[outputPtr++] = (byte)(128 | ch >> 6 & 63);
                     ch = 128 | ch & 63;
                  }

                  if (outputPtr >= outputBuffer.length) {
                     outputBuffer = bb.finishCurrentSegment();
                     outputPtr = 0;
                  }

                  outputBuffer[outputPtr++] = (byte)ch;
               }
               continue label83;
            }

            if (outputPtr >= outputBuffer.length) {
               outputBuffer = bb.finishCurrentSegment();
               outputPtr = 0;
            }

            outputBuffer[outputPtr++] = (byte)ch;
            ++inputPtr;
         } while(inputPtr < inputEnd);

         return this._bytes.completeAndCoalesce(outputPtr);
      }

      return this._bytes.completeAndCoalesce(outputPtr);
   }

   public byte[] encodeAsUTF8(String text) {
      ByteArrayBuilder byteBuilder = this._bytes;
      if (byteBuilder == null) {
         this._bytes = byteBuilder = new ByteArrayBuilder((BufferRecycler)null);
      }

      int inputPtr = 0;
      int inputEnd = text.length();
      int outputPtr = 0;
      byte[] outputBuffer = byteBuilder.resetAndGetFirstSegment();

      int c;
      for(int outputEnd = outputBuffer.length; inputPtr < inputEnd; outputBuffer[outputPtr++] = (byte)(128 | c & 63)) {
         for(c = text.charAt(inputPtr++); c <= 127; c = text.charAt(inputPtr++)) {
            if (outputPtr >= outputEnd) {
               outputBuffer = byteBuilder.finishCurrentSegment();
               outputEnd = outputBuffer.length;
               outputPtr = 0;
            }

            outputBuffer[outputPtr++] = (byte)c;
            if (inputPtr >= inputEnd) {
               return this._bytes.completeAndCoalesce(outputPtr);
            }
         }

         if (outputPtr >= outputEnd) {
            outputBuffer = byteBuilder.finishCurrentSegment();
            outputEnd = outputBuffer.length;
            outputPtr = 0;
         }

         if (c < 2048) {
            outputBuffer[outputPtr++] = (byte)(192 | c >> 6);
         } else if (c >= 55296 && c <= 57343) {
            if (c > 56319) {
               _illegal(c);
            }

            if (inputPtr >= inputEnd) {
               _illegal(c);
            }

            c = _convert(c, text.charAt(inputPtr++));
            if (c > 1114111) {
               _illegal(c);
            }

            outputBuffer[outputPtr++] = (byte)(240 | c >> 18);
            if (outputPtr >= outputEnd) {
               outputBuffer = byteBuilder.finishCurrentSegment();
               outputEnd = outputBuffer.length;
               outputPtr = 0;
            }

            outputBuffer[outputPtr++] = (byte)(128 | c >> 12 & 63);
            if (outputPtr >= outputEnd) {
               outputBuffer = byteBuilder.finishCurrentSegment();
               outputEnd = outputBuffer.length;
               outputPtr = 0;
            }

            outputBuffer[outputPtr++] = (byte)(128 | c >> 6 & 63);
         } else {
            outputBuffer[outputPtr++] = (byte)(224 | c >> 12);
            if (outputPtr >= outputEnd) {
               outputBuffer = byteBuilder.finishCurrentSegment();
               outputEnd = outputBuffer.length;
               outputPtr = 0;
            }

            outputBuffer[outputPtr++] = (byte)(128 | c >> 6 & 63);
         }

         if (outputPtr >= outputEnd) {
            outputBuffer = byteBuilder.finishCurrentSegment();
            outputEnd = outputBuffer.length;
            outputPtr = 0;
         }
      }

      return this._bytes.completeAndCoalesce(outputPtr);
   }

   private int _appendNumeric(int value, char[] qbuf) {
      qbuf[1] = 'u';
      qbuf[4] = HC[value >> 4];
      qbuf[5] = HC[value & 15];
      return 6;
   }

   private int _appendNamed(int esc, char[] qbuf) {
      qbuf[1] = (char)esc;
      return 2;
   }

   private int _appendByte(int ch, int esc, ByteArrayBuilder bb, int ptr) {
      bb.setCurrentSegmentLength(ptr);
      bb.append(92);
      if (esc < 0) {
         bb.append(117);
         if (ch > 255) {
            int hi = ch >> 8;
            bb.append(HB[hi >> 4]);
            bb.append(HB[hi & 15]);
            ch &= 255;
         } else {
            bb.append(48);
            bb.append(48);
         }

         bb.append(HB[ch >> 4]);
         bb.append(HB[ch & 15]);
      } else {
         bb.append((byte)esc);
      }

      return bb.getCurrentSegmentLength();
   }

   private static int _convert(int p1, int p2) {
      if (p2 >= 56320 && p2 <= 57343) {
         return 65536 + (p1 - '\ud800' << 10) + (p2 - '\udc00');
      } else {
         throw new IllegalArgumentException("Broken surrogate pair: first char 0x" + Integer.toHexString(p1) + ", second 0x" + Integer.toHexString(p2) + "; illegal combination");
      }
   }

   private static void _illegal(int c) {
      throw new IllegalArgumentException(UTF8Writer.illegalSurrogateDesc(c));
   }
}

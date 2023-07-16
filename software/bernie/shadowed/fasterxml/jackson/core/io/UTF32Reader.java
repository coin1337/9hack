package software.bernie.shadowed.fasterxml.jackson.core.io;

import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class UTF32Reader extends Reader {
   protected static final int LAST_VALID_UNICODE_CHAR = 1114111;
   protected static final char NC = '\u0000';
   protected final IOContext _context;
   protected InputStream _in;
   protected byte[] _buffer;
   protected int _ptr;
   protected int _length;
   protected final boolean _bigEndian;
   protected char _surrogate = 0;
   protected int _charCount;
   protected int _byteCount;
   protected final boolean _managedBuffers;
   protected char[] _tmpBuf;

   public UTF32Reader(IOContext ctxt, InputStream in, byte[] buf, int ptr, int len, boolean isBigEndian) {
      this._context = ctxt;
      this._in = in;
      this._buffer = buf;
      this._ptr = ptr;
      this._length = len;
      this._bigEndian = isBigEndian;
      this._managedBuffers = in != null;
   }

   public void close() throws IOException {
      InputStream in = this._in;
      if (in != null) {
         this._in = null;
         this.freeBuffers();
         in.close();
      }

   }

   public int read() throws IOException {
      if (this._tmpBuf == null) {
         this._tmpBuf = new char[1];
      }

      return this.read(this._tmpBuf, 0, 1) < 1 ? -1 : this._tmpBuf[0];
   }

   public int read(char[] cbuf, int start, int len) throws IOException {
      if (this._buffer == null) {
         return -1;
      } else if (len < 1) {
         return len;
      } else {
         if (start < 0 || start + len > cbuf.length) {
            this.reportBounds(cbuf, start, len);
         }

         int outPtr = start;
         int outEnd = len + start;
         int _lastValidInputStart;
         if (this._surrogate != 0) {
            outPtr = start + 1;
            cbuf[start] = this._surrogate;
            this._surrogate = 0;
         } else {
            _lastValidInputStart = this._length - this._ptr;
            if (_lastValidInputStart < 4 && !this.loadMore(_lastValidInputStart)) {
               if (_lastValidInputStart == 0) {
                  return -1;
               }

               this.reportUnexpectedEOF(this._length - this._ptr, 4);
            }
         }

         _lastValidInputStart = this._length - 3;

         int ptr;
         while(outPtr < outEnd) {
            ptr = this._ptr;
            int hi;
            int lo;
            if (this._bigEndian) {
               hi = this._buffer[ptr] << 8 | this._buffer[ptr + 1] & 255;
               lo = (this._buffer[ptr + 2] & 255) << 8 | this._buffer[ptr + 3] & 255;
            } else {
               lo = this._buffer[ptr] & 255 | (this._buffer[ptr + 1] & 255) << 8;
               hi = this._buffer[ptr + 2] & 255 | this._buffer[ptr + 3] << 8;
            }

            this._ptr += 4;
            if (hi != 0) {
               hi &= 65535;
               int ch = hi - 1 << 16 | lo;
               if (hi > 16) {
                  this.reportInvalid(ch, outPtr - start, String.format(" (above 0x%08x)", 1114111));
               }

               cbuf[outPtr++] = (char)('\ud800' + (ch >> 10));
               lo = '\udc00' | ch & 1023;
               if (outPtr >= outEnd) {
                  this._surrogate = (char)ch;
                  break;
               }
            }

            cbuf[outPtr++] = (char)lo;
            if (this._ptr > _lastValidInputStart) {
               break;
            }
         }

         ptr = outPtr - start;
         this._charCount += ptr;
         return ptr;
      }
   }

   private void reportUnexpectedEOF(int gotBytes, int needed) throws IOException {
      int bytePos = this._byteCount + gotBytes;
      int charPos = this._charCount;
      throw new CharConversionException("Unexpected EOF in the middle of a 4-byte UTF-32 char: got " + gotBytes + ", needed " + needed + ", at char #" + charPos + ", byte #" + bytePos + ")");
   }

   private void reportInvalid(int value, int offset, String msg) throws IOException {
      int bytePos = this._byteCount + this._ptr - 1;
      int charPos = this._charCount + offset;
      throw new CharConversionException("Invalid UTF-32 character 0x" + Integer.toHexString(value) + msg + " at char #" + charPos + ", byte #" + bytePos + ")");
   }

   private boolean loadMore(int available) throws IOException {
      this._byteCount += this._length - available;
      int count;
      if (available > 0) {
         if (this._ptr > 0) {
            System.arraycopy(this._buffer, this._ptr, this._buffer, 0, available);
            this._ptr = 0;
         }

         this._length = available;
      } else {
         this._ptr = 0;
         count = this._in == null ? -1 : this._in.read(this._buffer);
         if (count < 1) {
            this._length = 0;
            if (count < 0) {
               if (this._managedBuffers) {
                  this.freeBuffers();
               }

               return false;
            }

            this.reportStrangeStream();
         }

         this._length = count;
      }

      for(; this._length < 4; this._length += count) {
         count = this._in == null ? -1 : this._in.read(this._buffer, this._length, this._buffer.length - this._length);
         if (count < 1) {
            if (count < 0) {
               if (this._managedBuffers) {
                  this.freeBuffers();
               }

               this.reportUnexpectedEOF(this._length, 4);
            }

            this.reportStrangeStream();
         }
      }

      return true;
   }

   private void freeBuffers() {
      byte[] buf = this._buffer;
      if (buf != null) {
         this._buffer = null;
         this._context.releaseReadIOBuffer(buf);
      }

   }

   private void reportBounds(char[] cbuf, int start, int len) throws IOException {
      throw new ArrayIndexOutOfBoundsException("read(buf," + start + "," + len + "), cbuf[" + cbuf.length + "]");
   }

   private void reportStrangeStream() throws IOException {
      throw new IOException("Strange I/O stream, returned 0 bytes on read");
   }
}

package software.bernie.shadowed.fasterxml.jackson.core.format;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import software.bernie.shadowed.fasterxml.jackson.core.JsonFactory;

public interface InputAccessor {
   boolean hasMoreBytes() throws IOException;

   byte nextByte() throws IOException;

   void reset();

   public static class Std implements InputAccessor {
      protected final InputStream _in;
      protected final byte[] _buffer;
      protected final int _bufferedStart;
      protected int _bufferedEnd;
      protected int _ptr;

      public Std(InputStream in, byte[] buffer) {
         this._in = in;
         this._buffer = buffer;
         this._bufferedStart = 0;
         this._ptr = 0;
         this._bufferedEnd = 0;
      }

      public Std(byte[] inputDocument) {
         this._in = null;
         this._buffer = inputDocument;
         this._bufferedStart = 0;
         this._bufferedEnd = inputDocument.length;
      }

      public Std(byte[] inputDocument, int start, int len) {
         this._in = null;
         this._buffer = inputDocument;
         this._ptr = start;
         this._bufferedStart = start;
         this._bufferedEnd = start + len;
      }

      public boolean hasMoreBytes() throws IOException {
         if (this._ptr < this._bufferedEnd) {
            return true;
         } else if (this._in == null) {
            return false;
         } else {
            int amount = this._buffer.length - this._ptr;
            if (amount < 1) {
               return false;
            } else {
               int count = this._in.read(this._buffer, this._ptr, amount);
               if (count <= 0) {
                  return false;
               } else {
                  this._bufferedEnd += count;
                  return true;
               }
            }
         }
      }

      public byte nextByte() throws IOException {
         if (this._ptr >= this._bufferedEnd && !this.hasMoreBytes()) {
            throw new EOFException("Failed auto-detect: could not read more than " + this._ptr + " bytes (max buffer size: " + this._buffer.length + ")");
         } else {
            return this._buffer[this._ptr++];
         }
      }

      public void reset() {
         this._ptr = this._bufferedStart;
      }

      public DataFormatMatcher createMatcher(JsonFactory match, MatchStrength matchStrength) {
         return new DataFormatMatcher(this._in, this._buffer, this._bufferedStart, this._bufferedEnd - this._bufferedStart, match, matchStrength);
      }
   }
}
